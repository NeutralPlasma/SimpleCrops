package eu.virtusdevelops.simplecrops.gui

import eu.virtusdevelops.simplecrops.SimpleCrops
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
import java.awt.TextField

class ItemDropsGui(private val id: String, private val cropConfiguration: CropConfiguration, private val player: Player,
                  private val plugin: SimpleCrops, private val cropDrops: CropDrops, private val locale: LocaleHandler) {

    private val gui : InventoryCreator = InventoryCreator(45, HexUtil.colorify(
        TextUtil.formatString(locale.getLocale(Locales.ITEMS_GUI_TITLE),
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
            if(cropConfiguration.itemDrops.size > i){
                val data = cropConfiguration.itemDrops[i]
                val item = data.item.clone()
                val meta = item.itemMeta
                if(meta != null){
                    meta.lore = mutableListOf(HexUtil.colorify(locale.getLocale(Locales.GLOBAL_GUI_REMOVE)), HexUtil.colorify(locale.getLocale(Locales.GLOBAL_GUI_EDIT)))
                    item.itemMeta = meta
                }
                val icon = Icon(item)
                icon.addRightClickAction {
                    cropConfiguration.itemDrops.remove(data)
                    update()
                }
                icon.addLeftClickAction {
                    AnvilGUI.Builder()
                            .plugin(plugin)
                            .text("<MIN>:<MAX>")
                            .onClose{update()}
                            .onComplete {_, text ->
                                val dataText = text.split(":")
                                if(dataText.size > 1){
                                    cropConfiguration.itemDrops.remove(data)
                                    data.min = dataText[0].toInt()
                                    data.max = dataText[1].toInt()
                                    cropConfiguration.itemDrops.add(data)
                                }
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
        if(cropConfiguration.itemDrops.size > (currentPage*positions.size)){
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
            itemMeta.setDisplayName(HexUtil.colorify(locale.getLocale(Locales.ITEMS_GUI_ADD_NEW_TITLE)))
            val lore = TextUtil.colorFormatList(locale.getList(Locales.ITEMS_GUI_ADD_NEW_LORE))
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
                        .text("MIN:MAX")
                        .onClose { update() }
                        .onComplete { _, text ->
                            val data = text.split(":")
                            if(data.size > 1){
                                cropConfiguration.itemDrops.add(DropData(item, data[0].toInt(), data[1].toInt()))
                            }
                            return@onComplete AnvilGUI.Response.close()
                        }
                        .open(player)
            }else{
                AnvilGUI.Builder()
                        .plugin(plugin)
                        .text("MATERIAL:MIN:MAX")
                        .onClose { update() }
                        .onComplete { _, text ->
                            val data = text.split(":")
                            if(data.size > 2){
                                val material = Material.getMaterial(data[0])
                                if(material != null){
                                    cropConfiguration.itemDrops.add(DropData(ItemStack(material), data[1].toInt(), data[2].toInt()))
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