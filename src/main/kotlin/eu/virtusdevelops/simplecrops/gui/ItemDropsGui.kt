package eu.virtusdevelops.simplecrops.gui

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.handlers.crophandler.*
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.locale.Locales
import eu.virtusdevelops.virtuscore.gui.Icon
import eu.virtusdevelops.virtuscore.gui.InventoryCreator
import eu.virtusdevelops.virtuscore.gui.Paginator
import eu.virtusdevelops.virtuscore.utils.AbstractChatUtil
import eu.virtusdevelops.virtuscore.utils.HexUtil
import eu.virtusdevelops.virtuscore.utils.ItemUtils
import eu.virtusdevelops.virtuscore.utils.TextUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ItemDropsGui(private val id: String, private val cropConfiguration: CropConfiguration, private val player: Player,
                  private val plugin: SimpleCrops, private val cropDrops: CropDrops, private val locale: LocaleHandler) {


    private val pag = Paginator(player,
        listOf(10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34),
        TextUtils.colorFormat("&cCrop drops: ${cropConfiguration.name}"),
        54)


    init {
        refresh()
        pag.addCloseAction { _, _ ->
            cropDrops.updateCropData(id)
            CropConfigurationMenu(id, cropConfiguration, player, plugin, cropDrops, locale)

        }

        var item = ItemStack(Material.BOOK)
        item = ItemUtils.rename(item, TextUtils.colorFormat("&8[&6New drop&8]"))
        item = ItemUtils.setLore(item, TextUtils.colorFormatList(listOf("&7Drag & drop", "&7Or click")))

        val icon = Icon(item)
        icon.addDragItemIntoAction { player, itemStack ->
            if(itemStack.type != Material.AIR){
                val item2 = itemStack.clone()
                itemStack.amount = 0

                player.sendMessage(TextUtils.colorFormat("Please enter <MIN>:<MAX>"))
                plugin.getGuiHandler().removeFromList(player.uniqueId)
                val chat = AbstractChatUtil(player, {

                    val data = it.message.split(":")
                    if(data.size > 1){
                        cropConfiguration.itemDrops.add(DropData(item2, data[0].toInt(), data[1].toInt()))
                    }
                }, plugin)

                chat.setOnClose {
                    refresh()
                    pag.page()
                }




            }else{

                player.sendMessage(TextUtils.colorFormat("Please enter &c<MATERIAL>:<MIN>:<MAX>"))
                plugin.getGuiHandler().removeFromList(player.uniqueId)
                val chat = AbstractChatUtil(player, {

                    val data = it.message.split(":")
                    if(data.size > 2){
                        val material = Material.getMaterial(data[0].toUpperCase())
                        if(material != null){
                            cropConfiguration.itemDrops.add(DropData(ItemStack(material), data[1].toInt(), data[2].toInt()))
                        }else{
                            player.sendMessage(HexUtil.colorify(locale.getLocale(Locales.GLOBAL_GUI_INVALID_MATERIAL)))
                        }
                    }
                }, plugin)
                chat.setOnClose {
                    refresh()
                    pag.page()
                }
            }
        }


        pag.addIcon(51, icon)
        pag.page(0)
    }


    fun refresh(){
        val icons = mutableListOf<Icon>()

        for(drop in cropConfiguration.itemDrops){
            var item = drop.item.clone()
            item = ItemUtils.setLore(item, listOf(HexUtil.colorify(locale.getLocale(Locales.GLOBAL_GUI_REMOVE)), HexUtil.colorify(locale.getLocale(Locales.GLOBAL_GUI_EDIT))))
            item = ItemUtils.setName(item, TextUtils.colorFormat("&e${item.type} -> &c${drop.min}:${drop.max}"))

            val icon = Icon(item)
            icon.addRightClickAction {
                cropConfiguration.itemDrops.remove(drop)
                refresh()
                pag.page()
            }
            icon.addLeftClickAction {

                player.sendMessage(TextUtils.colorFormat("&7Please enter chance in next format &c<CHANCE>"))
                plugin.getGuiHandler().removeFromList(player.uniqueId)
                val chat = AbstractChatUtil(player, {
                    val dataText = it.message.split(":")
                    if(dataText.size > 1){
                        cropConfiguration.itemDrops.remove(drop)
                        drop.min = dataText[0].toInt()
                        drop.max = dataText[1].toInt()
                        cropConfiguration.itemDrops.add(drop)
                    }else{
                        player.sendMessage(TextUtils.colorFormat("&cInvalid input."))
                    }
                }, plugin)
                chat.setOnClose {
                    refresh()
                    pag.page()
                }

            }
            icon.addRightClickAction {
                cropConfiguration.itemDrops.remove(drop)
                pag.page()
            }

            icons.add(icon)

        }

        pag.setIcons(icons)
    }

}