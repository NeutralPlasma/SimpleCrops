package eu.virtusdevelops.simplecrops.commands

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.gui.EditCropGui
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.locale.Locales
import eu.virtusdevelops.virtuscore.command.AbstractCommand
import eu.virtusdevelops.virtuscore.utils.PlayerUtil
import eu.virtusdevelops.virtuscore.utils.TextUtil
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EditCommand(private val cropDrops: CropDrops, private val locale: LocaleHandler, private val plugin: SimpleCrops) :
        AbstractCommand(CommandType.BOTH, false, "edit") {

    override fun runCommand(sender: CommandSender, vararg args: String): ReturnType {

        if(args.isNotEmpty()){
            val crop = args[0]
            if(cropDrops.cropConfigurations.containsKey(crop)){
                val configuration = cropDrops.cropConfigurations[crop]
                if(configuration != null) {
                    EditCropGui(crop, configuration, sender as Player, plugin, cropDrops, locale)
                    return ReturnType.SUCCESS
                }
            }
        }else{
            return ReturnType.SYNTAX_ERROR
        }
        return ReturnType.SUCCESS
    }



    override fun onTab(commandSender: CommandSender, vararg args: String): List<String> {
        if(args.size == 1){
            return cropDrops.cropConfigurations.map { it.key }
        }
        return listOf()
    }

    override fun getPermissionNode(): String {
        return "simplecrops.command.editor"
    }

    override fun getSyntax(): String {
        return "/simplecrops editor <crop>"
    }

    override fun getDescription(): String {
        return "Crop editor."
    }

}

