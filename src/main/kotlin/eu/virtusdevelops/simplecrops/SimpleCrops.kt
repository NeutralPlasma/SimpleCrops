package eu.virtusdevelops.simplecrops

import eu.virtusdevelops.simplecrops.commands.*
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.handlers.ItemHandler
import eu.virtusdevelops.simplecrops.handlers.hoehandler.HoeHandler
import eu.virtusdevelops.simplecrops.listeners.*
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
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
import eu.virtusdevelops.virtuscore.utils.ItemUtils
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

class SimpleCrops : JavaPlugin() {
    private lateinit var storage: StorageHandler
    private lateinit var nbt: NBTUtil
    private lateinit var cropStorage: CropStorage
    private lateinit var fileManager: FileManager
    private lateinit var cropDrops: CropDrops
    private lateinit var commandManager: CommandManager
    private lateinit var itemHandler: ItemHandler
    private var itemUtil = ItemUtils()
    private lateinit var hoeHandler: HoeHandler
    private lateinit var handler: Handler
    private lateinit var locale: LocaleHandler
    private lateinit var playerSneakListener: PlayerSneakListener
    private var doesHolograms: Boolean = false

    override fun onEnable() {
        // logger stuff
        logger.level = Level.WARNING
        // storage and configuration load.
        setupConfig()
        nbt = NBTUtil(this)
        commandManager = CommandManager(this)
        storage = StorageHandler(this)
        cropStorage = CropStorage(this, storage)
        handler = Handler(this)

        VirtusCore.console().sendMessage(HexUtil.colorify(
                        " &d____   &e___ " +
                        "&d/ ___) &e/ __)   &dSimpleCrops" +
                        "&d\\___ \\&e( (__  &dCoded by &cVirtusDevelops <3" +
                        "&d(____/ &e\\___)  &eVersion: ${this.description.version}"
        ))



        fileManager = FileManager(this, linkedSetOf(
                FileLocation.of("crops.yml", true, false),
                FileLocation.of("hoes.yml", true, false),
                FileLocation.of("items.yml", true, false),
                FileLocation.of("language.yml", true, false)
        ))

        fileManager.loadFiles()
        locale = LocaleHandler(fileManager)
        itemHandler = ItemHandler(fileManager)

        /*
            Everything after file managing.
         */

        cropDrops = CropDrops(this, fileManager, nbt, itemUtil, cropStorage, itemHandler)
        hoeHandler = HoeHandler(fileManager, itemHandler, nbt)

        logger.info("Loading functions....")

        val pm:PluginManager = VirtusCore.plugins();
        // Event Listeners
        pm.registerEvents(CropPlaceListener(cropStorage, nbt, fileManager, locale), this)
        pm.registerEvents(CropBreakListener(cropStorage, cropDrops, locale), this)
        pm.registerEvents(CropGrowEvent(cropStorage, cropDrops), this)
        pm.registerEvents(CropPistonListener(cropStorage, cropDrops), this)
        pm.registerEvents(CropFromToListener(cropStorage, cropDrops), this)
        pm.registerEvents(CropInteractListener(cropStorage, cropDrops, nbt, hoeHandler, this), this)
        pm.registerEvents(CropDispenseListener(cropStorage, cropDrops), this)

        if(pm.isPluginEnabled("HolographicDisplays")){
            playerSneakListener = PlayerSneakListener(cropStorage, this)
            pm.registerEvents(playerSneakListener, this)
            doesHolograms = true
        }

        // Commands
        setupCommands()
        logger.info("Plugin enabled!")

        super.onEnable()
    }

    fun reload(){
        reloadConfig()
        fileManager.clear()
        fileManager.loadFiles()

        cropDrops.cacheConfiguration()
        hoeHandler.cacheConfigurations()
    }

    override fun onDisable() {
        logger.info("Disabling plugin.....")
        cropStorage.syncData()
        storage.closeConnection()
        logger.info("Plugin disabled.")
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
            EditCommand(cropDrops, locale, this)
        )
        if(doesHolograms){
            command.addSubCommands(
                CheckCommand(this, locale, playerSneakListener)
            )
        }

    }
}