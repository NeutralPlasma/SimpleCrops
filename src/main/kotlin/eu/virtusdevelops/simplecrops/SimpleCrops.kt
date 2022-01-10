package eu.virtusdevelops.simplecrops

import eu.virtusdevelops.simplecrops.commands.*
import eu.virtusdevelops.simplecrops.handlers.ItemHandler
import eu.virtusdevelops.simplecrops.handlers.ParticleHandler
import eu.virtusdevelops.simplecrops.handlers.StructureHandler
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.handlers.hoehandler.HoeHandler
import eu.virtusdevelops.simplecrops.listeners.*
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.region.SelectionTool
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropStorage
import eu.virtusdevelops.simplecrops.storage.database.StorageHandler
import eu.virtusdevelops.simplecrops.util.nbtutil.NBTUtil
import eu.virtusdevelops.virtuscore.VirtusCore
import eu.virtusdevelops.virtuscore.command.CommandManager
import eu.virtusdevelops.virtuscore.command.MainCommand
import eu.virtusdevelops.virtuscore.gui.Handler
import eu.virtusdevelops.virtuscore.managers.FileManager
import eu.virtusdevelops.virtuscore.utils.FileLocation
import eu.virtusdevelops.virtuscore.utils.HexUtil
import eu.virtusdevelops.virtuscore.utils.TextUtils
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin

class SimpleCrops : JavaPlugin() {
    private lateinit var storage: StorageHandler
    private lateinit var nbt: NBTUtil
    private lateinit var cropStorage: CropStorage
    private lateinit var fileManager: FileManager
    private lateinit var cropDrops: CropDrops
    private lateinit var commandManager: CommandManager
    private lateinit var itemHandler: ItemHandler
    private lateinit var hoeHandler: HoeHandler
    private lateinit var handler: Handler
    private lateinit var locale: LocaleHandler
    private lateinit var playerSneakListener: PlayerSneakListener
    private lateinit var particleHandler: ParticleHandler
    private lateinit var structureHandler: StructureHandler
    private lateinit var selectionTool: SelectionTool
    private var doesHolograms: Boolean = false

    override fun onEnable() {
        // storage and configuration load.
        setupConfig()
        nbt = NBTUtil(this)
        commandManager = CommandManager(this)
        storage = StorageHandler(this)
        cropStorage = CropStorage(this, storage)
        handler = Handler(this)

        VirtusCore.console().sendMessage(HexUtil.colorify(
                        "\n &d____   &e___ \n" +
                        "&d/ ___) &e/ __)   &dSimpleCrops\n" +
                        "&d\\___ \\&e( (__  &dCoded by &cVirtusDevelops <3\n" +
                        "&d(____/ &e\\___)  &eVersion: ${this.description.version}"
        ))



        fileManager = FileManager(this, linkedSetOf(
                FileLocation.of("crops.yml", true, false),
                FileLocation.of("hoes.yml", true, false),
                FileLocation.of("items.yml", true, false),
                FileLocation.of("structure_datas.yml", true, false),
                FileLocation.of("language.yml", true, false)
        ))

        fileManager.loadFiles()
        locale = LocaleHandler(fileManager)
        itemHandler = ItemHandler(fileManager)

        structureHandler = StructureHandler(this)


        /*
            Load particles api
         */


        particleHandler = ParticleHandler(this)




        /*
            Everything after file managing.
         */

        cropDrops = CropDrops(this, fileManager, nbt, cropStorage, itemHandler, structureHandler)
        hoeHandler = HoeHandler(fileManager, itemHandler, nbt)


        val pm:PluginManager = VirtusCore.plugins();
        // Event Listeners
        pm.registerEvents(CropPlaceListener(cropStorage, nbt, fileManager, locale), this)
        pm.registerEvents(CropBreakListener(cropStorage, cropDrops, locale, this, particleHandler), this)
        pm.registerEvents(CropGrowEvent(cropStorage, cropDrops, particleHandler), this)
        pm.registerEvents(CropPistonListener(cropStorage, cropDrops), this)
        pm.registerEvents(CropFromToListener(cropStorage, cropDrops), this)
        pm.registerEvents(CropInteractListener(cropStorage, cropDrops, nbt, hoeHandler, this, particleHandler), this)
        pm.registerEvents(CropDispenseListener(cropStorage, cropDrops), this)

        // selection tool
        selectionTool = SelectionTool(selectionTool(), this)

        if(pm.isPluginEnabled("SimpleHolograms")){
            playerSneakListener = PlayerSneakListener(cropStorage, this)
            pm.registerEvents(playerSneakListener, this)
            doesHolograms = true
        }

        // Commands
        setupCommands()
        INSTANCE = this
        CropDropsAPI = cropDrops
        CropStorageAPI = cropStorage


        super.onEnable()
    }

    fun reload(){
        reloadConfig()
        fileManager.clear()
        fileManager.loadFiles()
        structureHandler.reload()

        cropDrops.cacheConfiguration()
        hoeHandler.cacheConfigurations()
    }

    override fun onDisable() {
        cropStorage.syncData()
        storage.closeConnection()
        super.onDisable()
    }

    private fun setupConfig(){
        this.saveDefaultConfig()
    }

    private fun setupCommands(){

        val command : MainCommand = commandManager.addMainCommand("simplecrops")

        command.addSubCommands(
            GiveCommand(cropDrops, locale),
            ReloadCommand(this, locale),
            GiveHoeCommand(hoeHandler, locale),
            EditCommand(cropDrops, locale, this),
            StructureToolCommand(selectionTool, locale),
            SaveStructureCommand(selectionTool, this, structureHandler, fileManager)
        )
        if(doesHolograms){
            command.addSubCommands(
                CheckCommand(this, locale, playerSneakListener)
            )
        }

    }

    private fun selectionTool(): ItemStack{
        val selection = ItemStack(Material.STICK)
        val meta = selection.itemMeta
        meta?.setDisplayName(TextUtils.colorFormat("&8[&cSelection tool&8]"))
        meta?.lore = TextUtils.colorFormatList(listOf("&7R-Click to set pos1", "&7L-Click to set pos2", "&7Shift-Click to select center of area"))
        selection.itemMeta = meta
        return selection
    }

    fun getGuiHandler(): Handler{
        return this.handler
    }

    companion object{
        @JvmStatic
        lateinit var INSTANCE: SimpleCrops
        lateinit var CropDropsAPI: CropDrops
        lateinit var CropStorageAPI: CropStorage

    }



}