package eu.virtusdevelops.simplecrops.gui

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.handlers.crophandler.BlockDropData
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropConfiguration
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.locale.Locales
import eu.virtusdevelops.virtuscore.gui.Icon
import eu.virtusdevelops.virtuscore.gui.InventoryCreator
import eu.virtusdevelops.virtuscore.gui.Paginator
import eu.virtusdevelops.virtuscore.utils.AbstractChatUtil
import eu.virtusdevelops.virtuscore.utils.HexUtil
import eu.virtusdevelops.virtuscore.utils.ItemUtils
import eu.virtusdevelops.virtuscore.utils.TextUtils
//import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class BlockDropsGui(private val id: String, private val cropConfiguration: CropConfiguration, private val player: Player,
                    private val plugin: SimpleCrops, private val cropDrops: CropDrops, private val locale: LocaleHandler) {


    private val pag = Paginator(player,
        listOf(10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34),
        TextUtils.colorFormat("&cBlock drops: ${cropConfiguration.name}"),
        54)



    init {
        refresh()
        pag.addCloseAction { _, _ ->
            cropDrops.updateCropData(id)
            // add way to open the main menu
        }


        var item = ItemStack(Material.BOOK)
        item = ItemUtils.rename(item, TextUtils.colorFormat("&8[&6New drop&8]"))
        item = ItemUtils.setLore(item, TextUtils.colorFormatList(listOf("&7Drag & drop", "&7Or click")))


        val icon = Icon(item)
        icon.addDragItemIntoAction { player, itemStack ->
            if(itemStack.type != Material.AIR){
                val item2 = itemStack.clone()
                itemStack.amount = 0


                player.sendMessage("Please enter chance in next format <CHANCE>")
                val chat = AbstractChatUtil(player, {
                    //cropConfiguration.commandDrops.add(it.message)
                    cropConfiguration.blockDrops.add(BlockDropData(item2.type, it.message.toDouble()))
                }, plugin)
                chat.setOnClose {
                    refresh()
                    pag.page()
                }


//                AnvilGUI.Builder()
//                    .plugin(plugin)
//                    .text("<CHANCE>")
//                    .onClose {
//                        refresh()
//                        pag.page()
//                    }
//                    .onComplete { _, text ->
//                        cropConfiguration.blockDrops.add(BlockDropData(item.type, text.toDouble()))
//                        return@onComplete AnvilGUI.Response.close()
//                    }
//                    .open(player)
            }else{

                player.sendMessage("Please enter chance in next format <MATERIAL><CHANCE>")
                val chat = AbstractChatUtil(player, {
                    //cropConfiguration.commandDrops.add(it.message)
                    val data = it.message.split(":")
                    if(data.size > 1){
                        val material = Material.getMaterial(data[0])
                        if(material != null){
                            cropConfiguration.blockDrops.add(BlockDropData(material, data[1].toDouble()))
                        }else{
                            player.sendMessage(HexUtil.colorify(locale.getLocale(Locales.GLOBAL_GUI_INVALID_MATERIAL)))
                        }

                    }

                }, plugin)
                chat.setOnClose {
                    refresh()
                    pag.page()
                }


//                AnvilGUI.Builder()
//                    .plugin(plugin)
//                    .text("<MATERIAL:CHANCE>")
//                    .onClose {
//                        refresh()
//                        pag.page()
//                    }
//                    .onComplete { _, text ->
//                        val data = text.split(":")
//                        if(data.size > 1){
//                            val material = Material.getMaterial(data[0])
//                            if(material != null){
//                                cropConfiguration.blockDrops.add(BlockDropData(material, data[1].toDouble()))
//                            }else{
//                                player.sendMessage(HexUtil.colorify(locale.getLocale(Locales.GLOBAL_GUI_INVALID_MATERIAL)))
//                            }
//
//                        }
//
//                        return@onComplete AnvilGUI.Response.close()
//                    }
//                    .open(player)
            }
        }
        pag.addIcon(51, icon)
        pag.page(0)


    }







    private fun refresh(){
        val icons = mutableListOf<Icon>()


        for(data in cropConfiguration.blockDrops) {

            val material = data.material
            val item = ItemStack(material)
            val meta = item.itemMeta
            if (meta != null) {
                meta.setDisplayName("${material.name}:${data.chance}")
                meta.lore = mutableListOf(
                    HexUtil.colorify(locale.getLocale(Locales.GLOBAL_GUI_REMOVE)),
                    HexUtil.colorify(locale.getLocale(Locales.GLOBAL_GUI_EDIT))
                )
                item.itemMeta = meta
            }
            val icon = Icon(item)
            icon.addRightClickAction {
                cropConfiguration.blockDrops.remove(data)
                refresh()
                pag.page()
            }
            icon.addLeftClickAction {

                player.sendMessage("Please enter chance in next format <CHANCE>")

                val chat = AbstractChatUtil(player, {
                    //cropConfiguration.commandDrops.add(it.message)
                    cropConfiguration.blockDrops.remove(data)
                    data.chance = it.message.toDouble()
                    cropConfiguration.blockDrops.add(data)

                }, plugin)
                chat.setOnClose {
                    refresh()
                    pag.page()
                }



//                AnvilGUI.Builder()
//                    .plugin(plugin)
//                    .text("<CHANCE>")
//                    .onClose {
//                        refresh()
//                        pag.page()
//                    }
//                    .onComplete { _, text ->
//
//                        cropConfiguration.blockDrops.remove(data)
//                        data.chance = text.toDouble()
//                        cropConfiguration.blockDrops.add(data)
//
//                        return@onComplete AnvilGUI.Response.close()
//                    }
//                    .open(player)
            }
            icons.add(icon)
        }
        pag.setIcons(icons)
    }
}