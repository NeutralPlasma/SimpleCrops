package eu.virtusdevelops.simplecrops.gui

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropConfiguration
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.locale.Locales
import eu.virtusdevelops.virtuscore.gui.Icon
import eu.virtusdevelops.virtuscore.gui.InventoryCreator
import eu.virtusdevelops.virtuscore.utils.HexUtil
import eu.virtusdevelops.virtuscore.utils.ItemUtils
import eu.virtusdevelops.virtuscore.utils.TextUtils
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class EditCropGui(private val id: String, private val cropConfiguration: CropConfiguration, private val player: Player,
                    private val plugin: SimpleCrops, private val cropDrops: CropDrops, private val locale: LocaleHandler) {

    private val gui : InventoryCreator = InventoryCreator(45,
        HexUtil.colorify(TextUtils.formatString(
            locale.getLocale(Locales.MAIN_GUI_TITLE),
            "{id}:${id}", "{name}:${cropConfiguration.name}")))
    private var time : Long = 0L

    init {
        gui.addCloseActions { _, _ ->
            cropDrops.updateCropData(id)
        }
        load()
    }

    private fun load(){

        val item = ItemStack(Material.NAME_TAG)
        val meta = item.itemMeta
        if(meta != null) {
            meta.setDisplayName(HexUtil.colorify("&8[&eEdit Name&8]"))
            val lore = mutableListOf(HexUtil.colorify("&7Click to edit"))
            meta.lore = lore
            item.itemMeta = meta
        }
        val iconName = Icon(item)

        iconName.addClickAction {
            val builder = AnvilGUI.Builder()
            builder.plugin(plugin)
            builder.text("Name")
            builder.onComplete { player, text ->
                if(text.isNotEmpty()){
                    cropConfiguration.name = text
                    player.sendMessage(cropDrops.cropConfigurations[id]!!.name)
                    cropDrops.updateCropName(id)
                    update()
                    return@onComplete AnvilGUI.Response.text("Done")
                }else{
                    update()
                    return@onComplete AnvilGUI.Response.text("You should at least enter a valid name right?")
                }
            }
            builder.open(player)
        } // Crop Name
        gui.setIcon(10, iconName)

        val item2 = cropDrops.createSeed(id, 0, 0)
        if(item2 != null) {
            val meta2 = item2.itemMeta
            if (meta2 != null) {
                meta2.setDisplayName(HexUtil.colorify("&8[&eCrop Type&8]"))
                val lore = listOf(HexUtil.colorify("&7Drag and drop to change"))
                meta2.lore = lore
                item2.itemMeta = meta2
            }
            val iconType = Icon(item2)
            iconType.addDragItemIntoAction {_, itemStack ->
                if(itemStack.type != Material.AIR){
                    cropDrops.updateCropType(id, itemStack)
                    itemStack.amount = 0
                    update()
                }
            }
            gui.setIcon(12, iconType)
        } // Crop Type

        /*
            Crop bonemeal
         */
        val item3 = ItemStack(Material.BONE_MEAL)
        val meta3 = item3.itemMeta
        if(meta3 != null){
            meta3.setDisplayName(HexUtil.colorify("&8[&eBoneMeal&8]"))


            val lore = listOf(HexUtil.colorify("&7Left click to toggle: ${if (!cropConfiguration.useBoneMeal) "&cDisabled" else "&aEnabled"}"),
                                HexUtil.colorify("&7R-Click to change amount: &e${cropConfiguration.boneMeal}"))
            meta3.lore = lore
            item3.itemMeta = meta3
        }
        val iconBoneMeal = Icon(item3)
        iconBoneMeal.addLeftClickAction {
            cropConfiguration.useBoneMeal = !cropConfiguration.useBoneMeal
            cropDrops.updateBonemealCrop(id)
            update()
        }

        iconBoneMeal.addRightClickAction {
            AnvilGUI.Builder()
                .plugin(plugin)
                .text("Number")
                .onComplete{ _, text ->
                    cropConfiguration.boneMeal = text.toInt()
                    cropDrops.updateBonemealCrop(id)
                    return@onComplete AnvilGUI.Response.close()
                }
                .onClose { update() }
                .open(player)
        }


        gui.setIcon(14, iconBoneMeal)

        /*
            ItemDrops icon
         */
        val item4 = ItemStack(Material.CHEST)
        val meta4 = item4.itemMeta
        if(meta4 != null){
            meta4.setDisplayName(HexUtil.colorify("&8[&eItem Drops&8]"))
            val lore = mutableListOf(HexUtil.colorify(locale.getLocale(Locales.MAIN_GUI_EDIT)))
            meta4.lore = lore
            item4.itemMeta = meta4
        }
        val iconItemDrops = Icon(item4)
        iconItemDrops.addClickAction {
            ItemDropsGui(id, cropConfiguration, player, plugin, cropDrops, locale)
        }
        gui.setIcon(16, iconItemDrops)


        /*
            Gain icon
         */

        val item5 = ItemStack(Material.ANVIL)
        val meta5 = item5.itemMeta
        if(meta5 != null){
            meta5.setDisplayName(HexUtil.colorify("&8[&eGain&8]"))
            val lore = mutableListOf(HexUtil.colorify("&7R-Click to set minimum"),
                    HexUtil.colorify("&7L-Click to set maximum"))
            meta5.lore = lore
            item5.itemMeta = meta5
        }
        val icongain = Icon(item5)
        icongain.addRightClickAction {
            AnvilGUI.Builder()
                    .plugin(plugin)
                    .text("Number")
                    .onComplete{ _, text ->
                        cropConfiguration.minGain = text.toInt()
                        return@onComplete AnvilGUI.Response.close()
                    }
                    .onClose { update() }
                    .open(player)
        }
        icongain.addLeftClickAction {
            AnvilGUI.Builder()
                    .plugin(plugin)
                    .text("Number")
                    .onComplete{ _, text ->
                        cropConfiguration.maxGain = text.toInt()
                        return@onComplete AnvilGUI.Response.close()
                    }
                    .onClose { update() }
                    .open(player)
        }

        gui.setIcon(28, icongain)

        /*
            Strength icon
         */

        val item6 = ItemStack(Material.ANVIL)
        val meta6 = item6.itemMeta
        if(meta6 != null){
            meta6.setDisplayName(HexUtil.colorify("&8[&eStrength&8]"))
            val lore = mutableListOf(HexUtil.colorify("&7R-Click to set minimum"),
                    HexUtil.colorify("&7L-Click to set maximum"))
            meta6.lore = lore
            item6.itemMeta = meta6
        }
        val iconstrength = Icon(item6)
        iconstrength.addRightClickAction {
            AnvilGUI.Builder()
                    .plugin(plugin)
                    .text("Number")
                    .onComplete{ _, text ->
                        cropConfiguration.minStrength = text.toInt()
                        return@onComplete AnvilGUI.Response.close()
                    }
                    .onClose { update() }
                    .open(player)
        }
        iconstrength.addLeftClickAction {
            AnvilGUI.Builder()
                    .plugin(plugin)
                    .text("Number")
                    .onComplete{ _, text ->
                        cropConfiguration.maxStrength = text.toInt()
                        return@onComplete AnvilGUI.Response.close()
                    }
                    .onClose { update() }
                    .open(player)
        }

        gui.setIcon(30, iconstrength)


        /*
            BlockDrops icon
         */
        val item7 = ItemStack(Material.STONE)
        val meta7 = item7.itemMeta
        if(meta7 != null){
            meta7.setDisplayName(HexUtil.colorify("&8[&eBlock Drops&8]"))
            val lore = mutableListOf(HexUtil.colorify(locale.getLocale(Locales.MAIN_GUI_EDIT)))
            meta7.lore = lore
            item7.itemMeta = meta7
        }
        val blockDrops = Icon(item7)
        blockDrops.addClickAction {
            BlockDropsGui(id, cropConfiguration, player, plugin, cropDrops, locale)
        }
        gui.setIcon(32, blockDrops)

        /*
            CommandDrops icon
         */
        val item8 = ItemStack(Material.COMMAND_BLOCK)
        val meta8 = item8.itemMeta
        if(meta8 != null){
            meta8.setDisplayName(HexUtil.colorify("&8[&eCommand Drops&8]"))
            val lore = mutableListOf(HexUtil.colorify(locale.getLocale(Locales.MAIN_GUI_EDIT)))
            meta8.lore = lore
            item8.itemMeta = meta8
        }
        val commandDrops = Icon(item8)
        commandDrops.addClickAction {
            CommandDropsGui(id, cropConfiguration, player, plugin, cropDrops, locale)
        }
        gui.setIcon(34, commandDrops)


        /*
            Structure settings
         */
        var item9 = ItemStack(Material.STRUCTURE_BLOCK)
        item9 = ItemUtils.rename(item9, TextUtils.colorFormat("&8[&eStructure settings&8]"))
        item9 = ItemUtils.setLore(item9, TextUtils.colorFormatList(listOf("&7Current structure: &c${cropConfiguration.structureName}", "&7Click to change.")))
        val structureIcon = Icon(item9)
        structureIcon.addClickAction {
            AnvilGUI.Builder()
                .plugin(plugin)
                .text("<STRUCTURE NAME>")
                .onComplete{ _, text ->
                    cropConfiguration.structureName = text
                    return@onComplete AnvilGUI.Response.close()
                }
                .onClose { update() }
                .open(player)
        }
        gui.setIcon(22, structureIcon)



        gui.setBackground(ItemStack(Material.LIME_STAINED_GLASS_PANE))
//        player.sendMessage("Took ${time/1e6}ms to construct gui.")
        player.openInventory(gui.inventory)
    }

    private fun update(){
        gui.title = HexUtil.colorify(TextUtils.formatString(
            locale.getLocale(Locales.MAIN_GUI_TITLE),
            "{id}:${id}", "{name}:${cropConfiguration.name}"))
        load()
        player.openInventory(gui.inventory)
    }
}