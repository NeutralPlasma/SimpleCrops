package eu.virtusdevelops.simplecrops.gui

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.handlers.crophandler.BlockDropData
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropConfiguration
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropType
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.locale.Locales
import eu.virtusdevelops.virtuscore.gui.Icon
import eu.virtusdevelops.virtuscore.gui.InventoryCreator
import eu.virtusdevelops.virtuscore.utils.AbstractChatUtil
import eu.virtusdevelops.virtuscore.utils.HexUtil
import eu.virtusdevelops.virtuscore.utils.ItemUtils
import eu.virtusdevelops.virtuscore.utils.TextUtils
//import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.w3c.dom.Text

class EditCropGui(private val id: String, private val cropConfiguration: CropConfiguration, private val player: Player,
                    private val plugin: SimpleCrops, private val cropDrops: CropDrops, private val locale: LocaleHandler) {

    private val gui : InventoryCreator = InventoryCreator(54,
        TextUtils.colorFormat("&7Editing: &e${id} : &r${cropConfiguration.name}"))


    init {
        gui.addCloseActions { _, _ ->
            cropDrops.updateCropData(id)
        }
        load()
    }

    private fun load(){

        var item = ItemStack(Material.NAME_TAG)
        item = ItemUtils.rename(item, TextUtils.colorFormat("&8[&eEdit Name&8]"))
        item = ItemUtils.setLore(item, TextUtils.colorFormatList(listOf("&7Click to edit")))

        val iconName = Icon(item)
        iconName.addClickAction {

            player.sendMessage("Please enter name:")
            val chat = AbstractChatUtil(player, {
                //cropConfiguration.commandDrops.add(it.message)
                cropConfiguration.name = it.message
                player.sendMessage(cropDrops.cropConfigurations[id]!!.name)
                cropDrops.updateCropName(id)

                cropConfiguration.blockDrops.add(BlockDropData(item.type, it.message.toDouble()))
            }, plugin)
            chat.setOnClose {
                update()
            }



//            val builder = AnvilGUI.Builder()
//            builder.plugin(plugin)
//            builder.text("<NAME>")
//            builder.onComplete { player, text ->
//                if(text.isNotEmpty()){
//                    cropConfiguration.name = text
//                    player.sendMessage(cropDrops.cropConfigurations[id]!!.name)
//                    cropDrops.updateCropName(id)
//                    update()
//                    return@onComplete AnvilGUI.Response.text("Done")
//                }else{
//                    update()
//                    return@onComplete AnvilGUI.Response.text("You should at least enter a valid name right?")
//                }
//            }
//            builder.open(player)
        } // Crop Name
        gui.setIcon(10, iconName)


        // Crop type icon
        var item2 = cropDrops.createSeed(id, 0, 0)
        item2 = ItemUtils.setName(item2, TextUtils.colorFormat("&8[&eCrop Type&8]"))
        item2 = ItemUtils.setLore(item2, TextUtils.colorFormatList(listOf("&7Drag and drop to change")))
        val iconType = Icon(item2)
        iconType.addDragItemIntoAction {_, itemStack ->
            if(itemStack.type != Material.AIR){
                cropDrops.updateCropType(id, itemStack)
                itemStack.amount = 0
                update()
            }
        }
        gui.setIcon(12, iconType)


        /*
            Crop bonemeal
         */
        var item3 = ItemStack(Material.BONE_MEAL)
        item3 = ItemUtils.setName(item3, TextUtils.colorFormat("&8[&eBoneMeal&8]"))
        item3 = ItemUtils.setLore(item3, TextUtils.colorFormatList(listOf("&7Left click to toggle: ${if (!cropConfiguration.useBoneMeal) "&cDisabled" else "&aEnabled"}",
        "&7R-Click to change amount: &e${cropConfiguration.boneMeal}")))
        val iconBoneMeal = Icon(item3)
        iconBoneMeal.addLeftClickAction {
            cropConfiguration.useBoneMeal = !cropConfiguration.useBoneMeal
            cropDrops.updateBonemealCrop(id)
            update()
        }
        iconBoneMeal.addRightClickAction {

            player.sendMessage("Please enter number")
            val chat = AbstractChatUtil(player, {
                //cropConfiguration.commandDrops.add(it.message)
                cropConfiguration.boneMeal = it.message.toInt()
                cropDrops.updateBonemealCrop(id)
            }, plugin)
            chat.setOnClose {
                update()
            }


//            AnvilGUI.Builder()
//                .plugin(plugin)
//                .text("<NUMBER>")
//                .onComplete{ _, text ->
//                    cropConfiguration.boneMeal = text.toInt()
//                    cropDrops.updateBonemealCrop(id)
//                    return@onComplete AnvilGUI.Response.close()
//                }
//                .onClose { update() }
//                .open(player)
        }


        gui.setIcon(14, iconBoneMeal)

        /*
            ItemDrops icon
         */
        var item4 = ItemStack(Material.CHEST)
        item4 = ItemUtils.setName(item4, TextUtils.colorFormat("&8[&eItem Drops&8]"))
        item4 = ItemUtils.setLore(item4, TextUtils.colorFormatList(listOf("&7Click to edit")))
        val iconItemDrops = Icon(item4)
        iconItemDrops.addClickAction {
            ItemDropsGui(id, cropConfiguration, player, plugin, cropDrops, locale)
        }
        gui.setIcon(16, iconItemDrops)


        /*
            Gain icon
         */

        var item5 = ItemStack(Material.ANVIL)
        item5 = ItemUtils.setName(item5, TextUtils.colorFormat("&8[&eGain&8]"))
        item5 = ItemUtils.setLore(item5, TextUtils.colorFormatList(listOf("&7R-Click to set minimum",
            "&7L-Click to set maximum",
            "&a${cropConfiguration.minGain}&7:&a${cropConfiguration.maxGain}")))

        val icongain = Icon(item5)
        icongain.addRightClickAction {

            player.sendMessage("Please enter <NUMBER>")
            val chat = AbstractChatUtil(player, {
                //cropConfiguration.commandDrops.add(it.message)
                cropConfiguration.minGain = it.message.toInt()
            }, plugin)
            chat.setOnClose {
                update()
            }


//            AnvilGUI.Builder()
//                    .plugin(plugin)
//                    .text("<NUMBER>")
//                    .onComplete{ _, text ->
//                        cropConfiguration.minGain = text.toInt()
//                        return@onComplete AnvilGUI.Response.close()
//                    }
//                    .onClose { update() }
//                    .open(player)
        }
        icongain.addLeftClickAction {
//            AnvilGUI.Builder()
//                    .plugin(plugin)
//                    .text("<NUMBER>")
//                    .onComplete{ _, text ->
//                        cropConfiguration.maxGain = text.toInt()
//                        return@onComplete AnvilGUI.Response.close()
//                    }
//                    .onClose { update() }
//                    .open(player)


            player.sendMessage("Please enter <NUMBER>")
            val chat = AbstractChatUtil(player, {
                //cropConfiguration.commandDrops.add(it.message)
                cropConfiguration.maxGain = it.message.toInt()
            }, plugin)
            chat.setOnClose {
                update()
            }
        }

        gui.setIcon(37, icongain)

        /*
            Strength icon
         */

        var item6 = ItemStack(Material.ANVIL)
        item6 = ItemUtils.setName(item6, TextUtils.colorFormat("&8[&eStrength&8]"))
        item6 = ItemUtils.setLore(item6, TextUtils.colorFormatList(listOf("&7R-Click to set minimum",
            "&7L-Click to set maximum",
            "&a${cropConfiguration.minStrength}&7:&a${cropConfiguration.maxStrength}")))
        val iconstrength = Icon(item6)
        iconstrength.addRightClickAction {
//            AnvilGUI.Builder()
//                    .plugin(plugin)
//                    .text("<NUMBER>")
//                    .onComplete{ _, text ->
//                        cropConfiguration.minStrength = text.toInt()
//                        return@onComplete AnvilGUI.Response.close()
//                    }
//                    .onClose { update() }
//                    .open(player)


            player.sendMessage("Please enter <NUMBER>")
            val chat = AbstractChatUtil(player, {
                //cropConfiguration.commandDrops.add(it.message)
                cropConfiguration.minStrength = it.message.toInt()
            }, plugin)
            chat.setOnClose {
                update()
            }

        }
        iconstrength.addLeftClickAction {
//            AnvilGUI.Builder()
//                    .plugin(plugin)
//                    .text("<NUMBER>")
//                    .onComplete{ _, text ->
//                        cropConfiguration.maxStrength = text.toInt()
//                        return@onComplete AnvilGUI.Response.close()
//                    }
//                    .onClose { update() }
//                    .open(player)

            player.sendMessage("Please enter <NUMBER>")
            val chat = AbstractChatUtil(player, {
                //cropConfiguration.commandDrops.add(it.message)
                cropConfiguration.maxStrength = it.message.toInt()
            }, plugin)
            chat.setOnClose {
                update()
            }
        }

        gui.setIcon(39, iconstrength)


        /*
            BlockDrops icon
         */
        var item7 = ItemStack(Material.STONE)
        item7 = ItemUtils.setName(item7, TextUtils.colorFormat("&8[&eBlock Drops&8]"))
        item7 = ItemUtils.setLore(item7, TextUtils.colorFormatList(listOf("&7Click to edit")))
        val blockDrops = Icon(item7)
        blockDrops.addClickAction {
            BlockDropsGui(id, cropConfiguration, player, plugin, cropDrops, locale)
        }
        gui.setIcon(41, blockDrops)

        /*
            CommandDrops icon
         */
        var item8 = ItemStack(Material.COMMAND_BLOCK)
        item8 = ItemUtils.setName(item8, TextUtils.colorFormat("&8[&eCommand Drops&8]"))
        item8 = ItemUtils.setLore(item8, TextUtils.colorFormatList(listOf("&7Click to edit")))
        val commandDrops = Icon(item8)
        commandDrops.addClickAction {
            CommandDropsGui(id, cropConfiguration, player, plugin, cropDrops, locale)
        }
        gui.setIcon(43, commandDrops)


        /*
            Structure settings
         */
        var item9 = ItemStack(Material.STRUCTURE_BLOCK)
        item9 = ItemUtils.rename(item9, TextUtils.colorFormat("&8[&eStructure settings&8]"))
        item9 = ItemUtils.setLore(item9, TextUtils.colorFormatList(listOf("&7Current structure: &cNONE", "&7Click to change.")))
        //item9 = ItemUtils.setLore(item9, TextUtils.colorFormatList(listOf("&7Current structure: &c${cropConfiguration.structures[0].structureName}", "&7Click to change.")))
        val structureIcon = Icon(item9)
        structureIcon.addClickAction {
//            AnvilGUI.Builder()
//                .plugin(plugin)
//                .text("<STRUCTURE NAME>")
//                .onComplete{ _, text ->
//                    //cropConfiguration.structureName = text
//                    return@onComplete AnvilGUI.Response.close()
//                }
//                .onClose { update() }
//                .open(player)


            player.sendMessage("Please enter <STRUCTURE>")
            val chat = AbstractChatUtil(player, {
                //cropConfiguration.commandDrops.add(it.message)
//                cropConfiguration.maxStrength = it.message.toInt()
            }, plugin)
            chat.setOnClose {
                update()
            }
        }
        gui.setIcon(22, structureIcon)


        /*
            Structure settings
         */
        var item10 = ItemStack(Material.REPEATING_COMMAND_BLOCK)
        item10 = ItemUtils.rename(item10, TextUtils.colorFormat("&8[&eCrop type&8]"))
        item10 = ItemUtils.setLore(item10, TextUtils.colorFormatList(listOf("&7Current type: &c${cropConfiguration.type}", "&7Click to change.")))
        val cropType = Icon(item10)
        cropType.addClickAction {

            player.sendMessage("Please enter ITEMS|STRUCTURE")
            val chat = AbstractChatUtil(player, {
                //cropConfiguration.commandDrops.add(it.message)
//                cropConfiguration.maxStrength = it.message.toInt()
                cropConfiguration.type = CropType.valueOf(it.message)
            }, plugin)
            chat.setOnClose {
                update()
            }

//            AnvilGUI.Builder()
//                .plugin(plugin)
//                .text("ITEMS|STRUCTURE")
//                .onComplete{ _, text ->
//                    cropConfiguration.type = CropType.valueOf(text)
//                    return@onComplete AnvilGUI.Response.close()
//                }
//                .onClose { update() }
//                .open(player)
        }
        gui.setIcon(31, cropType)



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