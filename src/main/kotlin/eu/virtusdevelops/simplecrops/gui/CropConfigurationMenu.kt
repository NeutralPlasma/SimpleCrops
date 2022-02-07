package eu.virtusdevelops.simplecrops.gui

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropConfiguration
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropType
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.locale.Locales
import eu.virtusdevelops.virtuscore.gui.Icon
import eu.virtusdevelops.virtuscore.gui.Paginator
import eu.virtusdevelops.virtuscore.utils.AbstractChatUtil
import eu.virtusdevelops.virtuscore.utils.ItemUtils
import eu.virtusdevelops.virtuscore.utils.TextUtils
import org.bukkit.Material
import org.bukkit.entity.Player

class CropConfigurationMenu(private val id: String, private val cropConfiguration: CropConfiguration, private val player: Player,
                            private val plugin: SimpleCrops, private val cropDrops: CropDrops, private val locale: LocaleHandler
) {

    private val pag = Paginator(player,
        listOf(10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34),
        TextUtils.colorFormat(TextUtils.formatString(
            locale.getLocale(Locales.MAIN_GUI_TITLE),
            "{id}:${id}")),
        54)


    init {

        load()
    }


    fun load(){

        val icons = mutableListOf<Icon>()
        icons.add(editNameIcon())
        icons.add(editGainIcon())
        icons.add(editStrengthIcon())
        icons.add(switchDropNaturallyIcon())
        icons.add(switchDuplicateIcon())
        icons.add(duplicateChanceIcon())
        icons.add(dropChanceIcon())
        icons.add(levelUpChanceIcon())
        icons.add(customBoneMealIcon())
        icons.add(editTypeIcon())
        icons.add(itemsDropsMenuIcon())
        icons.add(commandDropsMenuIcon())
        icons.add(blockDropsMenuIcon())
        icons.add(structuresMenuIcon())

        // add drops menus
        pag.setIcons(icons)
        pag.page()
    }



    private fun structuresMenuIcon(): Icon{
        val item = ItemUtils.create(Material.CHEST, "&8[&bCrop structures&8]", listOf(
            "&7Click to edit"
        ))
        val icon = Icon(item)
        icon.addClickAction {
            StructuresDropsGui(id, cropConfiguration, player, plugin, cropDrops, locale)
        }
        return icon
    }

    private fun blockDropsMenuIcon(): Icon{
        val item = ItemUtils.create(Material.CHEST, "&8[&bCrop blocks drops&8]", listOf(
            "&7Click to edit"
        ))
        val icon = Icon(item)
        icon.addClickAction {
            BlockDropsGui(id, cropConfiguration, player, plugin, cropDrops, locale)
        }
        return icon
    }


    private fun commandDropsMenuIcon(): Icon{
        val item = ItemUtils.create(Material.COMMAND_BLOCK, "&8[&bCrop command drops&8]", listOf(
            "&7Click to edit"
        ))
        val icon = Icon(item)
        icon.addClickAction {
            CommandDropsGui(id, cropConfiguration, player, plugin, cropDrops, locale)
        }
        return icon
    }

    private fun itemsDropsMenuIcon(): Icon{
        val item = ItemUtils.create(Material.CHEST, "&8[&bCrop item drops&8]", listOf(
            "&7Click to edit"
        ))
        val icon = Icon(item)
        icon.addClickAction {
            ItemDropsGui(id, cropConfiguration, player, plugin, cropDrops, locale)
        }
        return icon
    }


    private fun customBoneMealIcon(): Icon{
        val item = ItemUtils.create(Material.BOOK, "&8[&bCrop bonemeal&8]", listOf(
            "&7L-Click to change state",
            "&7R-Click to change value",
            "&7Currenty: ${if (cropConfiguration.useBoneMeal) "&aEnabled" else "&cDisabled"}",
            "&7Current value: &e${cropConfiguration.boneMeal}"
        ))
        val icon = Icon(item)

        icon.addLeftClickAction {
            cropConfiguration.useBoneMeal = !cropConfiguration.useBoneMeal
            cropDrops.updateBonemealCrop(id)
            update()
        }

        icon.addRightClickAction {
            player.sendMessage("Please enter bonemeal amount:")
            val chat = AbstractChatUtil(player, {
                cropConfiguration.boneMeal = it.message.toInt()
                cropDrops.updateBonemealCrop(id)
            }, plugin)

            chat.setOnClose {
                update()
            }
        }

        return icon
    }


    private fun levelUpChanceIcon(): Icon{
        val item = ItemUtils.create(Material.BOOK, "&8[&bCrop level up chance&8]", listOf(
            "&7Click to change",
            "&7Currently: &e${cropConfiguration.levelUpChance}%"
        ))
        val icon = Icon(item)
        icon.addClickAction {
            player.sendMessage("Please enter level up chance:")
            val chat = AbstractChatUtil(player, {
                cropConfiguration.levelUpChance = it.message.toDouble()
                cropDrops.updateCropData(id)
            }, plugin)

            chat.setOnClose {
                update()
            }
        }
        return icon
    }

    private fun dropChanceIcon(): Icon{
        val item = ItemUtils.create(Material.BOOK, "&8[&bCrop drop naturally chance&8]", listOf(
            "&7Click to change",
            "&7Currently: &e${cropConfiguration.dropChance}%"
        ))
        val icon = Icon(item)
        icon.addClickAction {
            player.sendMessage("Please enter drop naturally chance:")
            val chat = AbstractChatUtil(player, {
                cropConfiguration.dropChance = it.message.toDouble()
                cropDrops.updateCropData(id)
            }, plugin)

            chat.setOnClose {
                update()
            }
        }
        return icon
    }

    private fun duplicateChanceIcon(): Icon{
        val item = ItemUtils.create(Material.BOOK, "&8[&bCrop duplicate chance&8]", listOf(
            "&7Click to change",
            "&7Currently: &e${cropConfiguration.duplicateChance}%"
        ))
        val icon = Icon(item)
        icon.addClickAction {
            player.sendMessage("Please enter duplication chance:")
            val chat = AbstractChatUtil(player, {
                cropConfiguration.duplicateChance = it.message.toDouble()
                cropDrops.updateCropData(id)
            }, plugin)

            chat.setOnClose {
                update()
            }
        }
        return icon
    }

    private fun switchDuplicateIcon(): Icon{
        val item = ItemUtils.create(Material.BOOK, "&8[&bCrop duplicate&8]", listOf(
            "&7Click to switch",
            "&7Currently: ${if (cropConfiguration.duplicate) "&aEnabled" else "&cDisabled"}"
        ))
        val icon = Icon(item)
        icon.addClickAction {
            cropConfiguration.duplicate = !cropConfiguration.duplicate
            cropDrops.updateCropData(id)
            update()
        }
        return icon

    }

    private fun switchDropNaturallyIcon(): Icon{
        val item = ItemUtils.create(Material.BOOK, "&8[&bCrop drop naturally&8]", listOf(
            "&7Click to switch",
            "&7Currently: ${if (cropConfiguration.dropNaturally) "&aEnabled" else "&cDisabled"}"
        ))
        val icon = Icon(item)
        icon.addClickAction {
            cropConfiguration.dropNaturally = !cropConfiguration.dropNaturally
            cropDrops.updateCropData(id)
            update()
        }
        return icon

    }

    private fun editStrengthIcon(): Icon{
        val item = ItemUtils.create(Material.BOOK, "&8[&bCrop strength&8]", listOf(
            "&7L-Click to change minimum",
            "&7R-Click to change maximum",
            "&7Current min: &e${cropConfiguration.minStrength}",
            "&7Current max: &e${cropConfiguration.maxStrength}"
        ))
        val icon = Icon(item)

        icon.addLeftClickAction {
            player.sendMessage("Please enter minimum strength:")
            val chat = AbstractChatUtil(player, {
                cropConfiguration.minStrength = it.message.toInt()
                cropDrops.updateCropData(id)
            }, plugin)

            chat.setOnClose {
                update()
            }
        }

        icon.addRightClickAction {
            player.sendMessage("Please enter maximum strength:")
            val chat = AbstractChatUtil(player, {
                cropConfiguration.maxStrength = it.message.toInt()
                cropDrops.updateCropData(id)
            }, plugin)

            chat.setOnClose {
                update()
            }
        }

        return icon
    }

    private fun editGainIcon(): Icon{
        val item = ItemUtils.create(Material.BOOK, "&8[&bCrop gain&8]", listOf(
            "&7L-Click to change minimum",
            "&7R-Click to change maximum",
            "&7Current min: &e${cropConfiguration.minGain}",
            "&7Current max: &e${cropConfiguration.maxGain}"
        ))
        val icon = Icon(item)

        icon.addLeftClickAction {
            player.sendMessage("Please enter minimum gain:")
            val chat = AbstractChatUtil(player, {
                cropConfiguration.minGain = it.message.toInt()
                cropDrops.updateCropData(id)
            }, plugin)

            chat.setOnClose {
                update()
            }
        }

        icon.addRightClickAction {
            player.sendMessage("Please enter maximum gain:")
            val chat = AbstractChatUtil(player, {
                cropConfiguration.maxGain = it.message.toInt()
                cropDrops.updateCropData(id)
            }, plugin)

            chat.setOnClose {
                update()
            }
        }

        return icon
    }

    private fun editTypeIcon(): Icon{
        val item = ItemUtils.create(Material.NAME_TAG, "&8[&bCrop type&8]", listOf(
            "&7Click to change crop type",
            "&7Current type: &e${cropConfiguration.type}"
        ))

        val icon = Icon(item)
        icon.addClickAction {
            player.sendMessage("Please enter type (STRUCTURE, ITEMS):")
            val chat = AbstractChatUtil(player, {
                cropConfiguration.type = CropType.valueOf(it.message.toUpperCase())
                cropDrops.updateCropData(id)

            }, plugin)
            chat.setOnClose {
                update()
            }
        }
        return icon
    }


    private fun editNameIcon(): Icon{
        val editName = ItemUtils.create(Material.NAME_TAG, "&8[&bCrop name&8]", listOf("&7Click to change crop name", "&7Current name: ${cropConfiguration.name}"))
        val editnameIcon = Icon(editName)
        editnameIcon.addClickAction {
            player.sendMessage("Please enter name:")
            val chat = AbstractChatUtil(player, {
                //cropConfiguration.commandDrops.add(it.message)
                cropConfiguration.name = it.message
                player.sendMessage(cropDrops.cropConfigurations[id]!!.name)
                cropDrops.updateCropName(id)

            }, plugin)
            chat.setOnClose {
                update()
            }
        }
        return editnameIcon
    }


    private fun update(){
        pag.name = TextUtils.colorFormat(TextUtils.formatString(
            locale.getLocale(Locales.MAIN_GUI_TITLE),
            "{id}:${id}", "{name}:${cropConfiguration.name}"))
        load()
        pag.page()
    }

}