package eu.virtusdevelops.simplecrops.gui

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropConfiguration
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.locale.Locales
import eu.virtusdevelops.virtuscore.gui.Icon
import eu.virtusdevelops.virtuscore.gui.InventoryCreator
import eu.virtusdevelops.virtuscore.utils.AbstractChatUtil
import eu.virtusdevelops.virtuscore.utils.HexUtil
import eu.virtusdevelops.virtuscore.utils.TextUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class CommandDropsGui(private val id: String, private val cropConfiguration: CropConfiguration, private val player: Player,
                      private val plugin: SimpleCrops, private val cropDrops: CropDrops, private val locale: LocaleHandler) {

    private val gui : InventoryCreator = InventoryCreator(45, HexUtil.colorify(
        TextUtils.formatString(locale.getLocale(Locales.COMMANDS_GUI_TITLE),
            "{id}:${id}")))
    private var currentPage: Int = 1
    private val positions = mutableListOf(10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34)

    init {
        gui.addCloseActions { _, _ ->
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
            if(cropConfiguration.commandDrops.size > i){
                val data = cropConfiguration.commandDrops[i]

                val item = ItemStack(Material.BOOK)
                val meta = item.itemMeta
                if(meta != null){
                    meta.setDisplayName(data)
                    meta.lore = mutableListOf(HexUtil.colorify(locale.getLocale(Locales.GLOBAL_GUI_REMOVE)))
                    item.itemMeta = meta
                }

                val icon = Icon(item)
                icon.addRightClickAction {
                    cropConfiguration.commandDrops.remove(data)
                    update()
                }

                gui.setIcon(positions[x], icon)
            }
        }

        /*
            Next page button
         */
        if(cropConfiguration.commandDrops.size > (currentPage*positions.size)){
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
            itemMeta.setDisplayName(HexUtil.colorify(locale.getLocale(Locales.COMMANDS_GUI_ADD_NEW_TITLE)))
            val lore = TextUtils.colorFormatList(locale.getList(Locales.COMMANDS_GUI_ADD_NEW_LORE))
            itemMeta.lore = lore
            newItem.itemMeta = itemMeta
        }
        val icon = Icon(newItem)
        icon.addClickAction() {

            player.closeInventory()
            for(string in TextUtils.colorFormatList(locale.getList(Locales.COMMANDS_GUI_ADD_INFO))){
                player.sendMessage(string)
            }
            val chat = AbstractChatUtil(player, {
                cropConfiguration.commandDrops.add(it.message)
            }, plugin)
            chat.setOnClose {
                update()
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