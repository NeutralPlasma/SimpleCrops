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

class StructuresDropsGui(private val id: String, private val cropConfiguration: CropConfiguration, private val player: Player,
                         private val plugin: SimpleCrops, private val cropDrops: CropDrops, private val locale: LocaleHandler) {


    private val pag = Paginator(player,
        listOf(10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34),
        TextUtils.colorFormat("&cCrop structures: ${cropConfiguration.name}"),
        54)


    init {
        refresh()
        pag.addCloseAction { _, _ ->
            cropDrops.updateCropData(id)
            CropConfigurationMenu(id, cropConfiguration, player, plugin, cropDrops, locale)

        }

        val item = ItemUtils.create(Material.BOOK, "&8[&bAdd new&8]", listOf(
            "&7Click to add new"
        ))


        val icon = Icon(item)

        icon.addClickAction {
            plugin.getGuiHandler().removeFromList(player.uniqueId)
            player.sendMessage("Please enter structure <STRUCTURE>:<CHANCE>")
            val chat = AbstractChatUtil(player, {
                val name = it.message.split(":")[0]
                var chance = it.message.split(":")[1].toDoubleOrNull()
                if(chance == null) chance = 10.0
                val min = if(cropConfiguration.structures.size > 0 ) cropConfiguration.structures.last().max else 0.0
                cropConfiguration.structures.add(StructureDropData(
                    name,
                    chance,
                    min,
                    min + chance
                ))
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
        for(drop in cropConfiguration.structures){
            val item = ItemUtils.create(Material.STRUCTURE_BLOCK, "&7${drop.structureName}", listOf(
                "&7L-Click to remove",
                "&7R-Click to edit chance",
                "&7Current: &e${drop.dropChance}"
            ))
            val icon = Icon(item)
            icon.addRightClickAction {
                cropConfiguration.structures.remove(drop)
                refresh()
                pag.page()
            }
            icon.addLeftClickAction {
                player.sendMessage(TextUtils.colorFormat("&7Please enter chance in next format &c<CHANCE>"))
                plugin.getGuiHandler().removeFromList(player.uniqueId)
                val chat = AbstractChatUtil(player, {
                    val dataText = it.message.split(":")
                    if(dataText.size > 1){
                        cropConfiguration.structures.remove(drop)
                        drop.dropChance = dataText[1].toDouble()
                        cropConfiguration.structures.add(drop)
                    }else{
                        player.sendMessage(TextUtils.colorFormat("&cInvalid input."))
                    }
                }, plugin)
                chat.setOnClose {
                    refresh()
                    pag.page()
                }
            }
            icons.add(icon)
        }
        pag.setIcons(icons)
    }

}