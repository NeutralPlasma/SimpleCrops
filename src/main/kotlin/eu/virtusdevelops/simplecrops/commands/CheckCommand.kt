package eu.virtusdevelops.simplecrops.commands

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.listeners.PlayerSneakListener
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.virtuscore.command.AbstractCommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CheckCommand(private val plugin: SimpleCrops, private val locale: LocaleHandler, private val moveevent: PlayerSneakListener) :
    AbstractCommand(CommandType.PLAYER_ONLY, false, "check") {

    override fun runCommand(commandSender: CommandSender, vararg args: String): ReturnType {
        val player = commandSender as Player
        if(moveevent.enabled.contains(player)){
            moveevent.enabled.remove(player)
            commandSender.sendMessage("Okay removed.")
        }else{
            moveevent.enabled.add(player)
            commandSender.sendMessage("Okay added.")
        }
        return ReturnType.SUCCESS
    }



    override fun onTab(commandSender: CommandSender, vararg args: String): List<String> {
        return listOf()
    }

    override fun getPermissionNode(): String {
        return "simplecrops.command.check"
    }

    override fun getSyntax(): String {
        return "/simplecrops check"
    }

    override fun getDescription(): String {
        return "Enables crop checking."
    }

}

