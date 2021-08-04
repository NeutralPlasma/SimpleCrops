package eu.virtusdevelops.simplecrops.commands

import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.region.SelectionTool
import eu.virtusdevelops.virtuscore.command.AbstractCommand
import eu.virtusdevelops.virtuscore.utils.TextUtils
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class StructureToolCommand(private val selectionTool: SelectionTool, private val locale: LocaleHandler) :
    AbstractCommand(CommandType.PLAYER_ONLY, false, "structuretool") {

    override fun runCommand(commandSender: CommandSender, vararg args: String): ReturnType {
        val player = commandSender as Player


        player.inventory.addItem(selectionTool.getItem())
        player.sendMessage(TextUtils.colorFormat("&aYou recieved tool."))





        return ReturnType.SUCCESS
    }



    override fun onTab(commandSender: CommandSender, vararg args: String): List<String> {
        return emptyList()
    }

    override fun getPermissionNode(): String {
        return "simplecrops.command.structure"
    }

    override fun getSyntax(): String {
        return "/simplecrops structuretool"
    }

    override fun getDescription(): String {
        return "Command to recieve structure tool"
    }

}

