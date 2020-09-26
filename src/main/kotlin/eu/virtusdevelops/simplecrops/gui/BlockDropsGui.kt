package eu.virtusdevelops.simplecrops.gui

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.handlers.crophandler.BlockDropData
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropConfiguration
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.handlers.crophandler.DropData
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.locale.Locales
import eu.virtusdevelops.virtuscore.gui.Icon
import eu.virtusdevelops.virtuscore.gui.InventoryCreator
import eu.virtusdevelops.virtuscore.utils.HexUtil
import eu.virtusdevelops.virtuscore.utils.TextUtil
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class BlockDropsGui(private val id: String, private val cropConfiguration: CropConfiguration, private val player: Player,
                    private val plugin: SimpleCrops, private val cropDrops: CropDrops, private val locale: LocaleHandler) {

    private val gui : InventoryCreator = InventoryCreator(45, HexUtil.colorify(
        TextUtil.formatString(locale.getLocale(Locales.BLOCKS_GUI_TITLE),
            "{id}:${id}")))
    private var currentPage: Int = 1
    private val positions = mutableListOf(10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34)

    init {
        gui.addCloseActions { player, inventory ->
            cropDrops.updateCropData(id)
        }
        load()
    }

    private fun load(){
        gui.clean()

        /*
            All the items yes.
         */

        for(x in 0 until 21){
            val i = x + (currentPage-1) * 27
            if(cropConfiguration.blockDrops.size > i){
                val data = cropConfiguration.blockDrops[i]
                val material = data.material
                val item = ItemStack(material)
                val meta = item.itemMeta
                if(meta != null){
                    meta.setDisplayName("${material.name}:${data.chance}")
                    meta.lore = mutableListOf(HexUtil.colorify(locale.getLocale(Locales.GLOBAL_GUI_REMOVE)), HexUtil.colorify(locale.getLocale(Locales.GLOBAL_GUI_EDIT)))
                    item.itemMeta = meta
                }
                val icon = Icon(item)
                icon.addRightClickAction {
                    cropConfiguration.blockDrops.remove(data)
                    update()
                }
                icon.addLeftClickAction {
                    AnvilGUI.Builder()
                            .plugin(plugin)
                            .text("CHANCE")
                            .onClose{update()}
                            .onComplete {_, text ->

                                cropConfiguration.blockDrops.remove(data)
                                data.chance = text.toDouble()
                                cropConfiguration.blockDrops.add(data)

                                return@onComplete AnvilGUI.Response.close()
                            }
                            .open(player)
                }
                gui.setIcon(positions[x], icon)
            }
        }

        /*
            Next page button
         */
        if(cropConfiguration.blockDrops.size > (currentPage*positions.size)){
            val item = ItemStack(Material.PAPER)
            val itemMeta = item.itemMeta
            if(itemMeta != null){
                itemMeta.setDisplayName(HexUtil.colorify(locale.getLocale(Locales.GLOBAL_GUI_NEXT_PAGE_TITLE)))
                val lore = mutableListOf(HexUtil.colorify(locale.getLocale(Locales.GLOBAL_GUI_NEXT_PAGE)))
                itemMeta.lore = lore
                item.itemMeta = itemMeta
            }
            val icon = Icon(item)
            icon.addClickAction {
                currentPage++;
                update()
            }
            gui.setIcon(43, icon)
        }

        /*
            Prev page
         */
        if(currentPage > 1){
            val item = ItemStack(Material.PAPER)
            val itemMeta = item.itemMeta
            if(itemMeta != null){
                itemMeta.setDisplayName(HexUtil.colorify(locale.getLocale(Locales.GLOBAL_GUI_PREV_PAGE_TITLE)))
                val lore = mutableListOf(HexUtil.colorify(locale.getLocale(Locales.GLOBAL_GUI_PREV_PAGE)))
                itemMeta.lore = lore
                item.itemMeta = itemMeta
            }
            val icon = Icon(item)
            icon.addClickAction {
                currentPage--;
                update()
            }
            gui.setIcon(37, icon)
        }
        /*
            Add new item
         */
        val newItem = ItemStack(Material.BOOK)
        val itemMeta = newItem.itemMeta
        if(itemMeta != null){
            itemMeta.setDisplayName(HexUtil.colorify(locale.getLocale(Locales.BLOCKS_GUI_ADD_NEW_TITLE)))
            val lore = TextUtil.colorFormatList(locale.getList(Locales.BLOCKS_GUI_ADD_NEW_LORE))
            itemMeta.lore = lore
            newItem.itemMeta = itemMeta
        }
        val icon = Icon(newItem)
        icon.addDragItemIntoAction { player, itemStack ->
            if(itemStack.type != Material.AIR){
                val item = itemStack.clone()
                itemStack.amount = 0
                AnvilGUI.Builder()
                        .plugin(plugin)
                        .text("CHANCE")
                        .onClose { update() }
                        .onComplete { _, text ->
                            cropConfiguration.blockDrops.add(BlockDropData(item.type, text.toDouble()))
                            return@onComplete AnvilGUI.Response.close()
                        }
                        .open(player)
            }else{
                AnvilGUI.Builder()
                        .plugin(plugin)
                        .text("MATERIAL:CHANCE")
                        .onClose { update() }
                        .onComplete { _, text ->
                            val data = text.split(":")
                            if(data.size > 1){
                                val material = Material.getMaterial(data[0])
                                if(material != null){
                                    cropConfiguration.blockDrops.add(BlockDropData(material, data[1].toDouble()))
                                }else{
                                    player.sendMessage(HexUtil.colorify(locale.getLocale(Locales.GLOBAL_GUI_INVALID_MATERIAL)))
                                }

                            }

                            return@onComplete AnvilGUI.Response.close()
                        }
                        .open(player)
            }
        }

        gui.setIcon(41, icon)


        gui.setBackground(ItemStack(Material.GRAY_STAINED_GLASS_PANE))
        player.openInventory(gui.inventory)

    }

    private fun update(){
        load()
    }

}