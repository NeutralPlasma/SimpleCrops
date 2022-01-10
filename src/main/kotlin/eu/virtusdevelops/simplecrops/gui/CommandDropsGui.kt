package eu.virtusdevelops.simplecrops.gui

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropConfiguration
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.locale.Locales
import eu.virtusdevelops.virtuscore.gui.Icon
import eu.virtusdevelops.virtuscore.gui.InventoryCreator
import eu.virtusdevelops.virtuscore.gui.Paginator
import eu.virtusdevelops.virtuscore.utils.AbstractChatUtil
import eu.virtusdevelops.virtuscore.utils.HexUtil
import eu.virtusdevelops.virtuscore.utils.TextUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class CommandDropsGui(private val id: String, private val cropConfiguration: CropConfiguration, private val player: Player,
                      private val plugin: SimpleCrops, private val cropDrops: CropDrops, private val locale: LocaleHandler) {

    private val pag = Paginator(player,
        listOf(10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34),
        TextUtils.colorFormat("&cCommand drops: ${cropConfiguration.name}"),
        54)


    init {
        refresh()
        pag.addCloseAction { _, _ ->
            cropDrops.updateCropData(id)
            EditCropGui(id, cropConfiguration, player, plugin, cropDrops, locale)
            // add way to open the main menu
        }

        val newItem = ItemStack(Material.BOOK)
        val itemMeta = newItem.itemMeta
        if(itemMeta != null){
            itemMeta.setDisplayName(HexUtil.colorify(locale.getLocale(Locales.COMMANDS_GUI_ADD_NEW_TITLE)))
            val lore = TextUtils.colorFormatList(locale.getList(Locales.COMMANDS_GUI_ADD_NEW_LORE))
            itemMeta.lore = lore
            newItem.itemMeta = itemMeta
        }
        val icon = Icon(newItem)
        icon.addClickAction {
            plugin.getGuiHandler().removeFromList(player.uniqueId)
            player.closeInventory()
            for(string in TextUtils.colorFormatList(locale.getList(Locales.COMMANDS_GUI_ADD_INFO))){
                player.sendMessage(string)
            }
            val chat = AbstractChatUtil(player, {
                cropConfiguration.commandDrops.add(it.message)
            }, plugin)
            chat.setOnClose {
                refresh()
                pag.page()
            }
        }
        pag.addIcon(51, icon)
        pag.page(0)


    }



    private fun refresh(){
        val icons = mutableListOf<Icon>()
        for(data in cropConfiguration.commandDrops){
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
                refresh()
                pag.page()
            }
            icons.add(icon)
        }
        pag.setIcons(icons)
    }

}