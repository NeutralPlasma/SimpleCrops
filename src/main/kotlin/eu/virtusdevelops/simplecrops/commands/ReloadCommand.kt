package eu.virtusdevelops.simplecrops.commands

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.locale.Locales
import eu.virtusdevelops.virtuscore.command.AbstractCommand
import eu.virtusdevelops.virtuscore.utils.TextUtils
import org.bukkit.command.CommandSender

class ReloadCommand(private val plugin: SimpleCrops, private val locale: LocaleHandler) : AbstractCommand(CommandType.BOTH, false, "reload") {

    override fun runCommand(commandSender: CommandSender, vararg args: String): ReturnType {
        plugin.reload()
        commandSender.sendMessage(TextUtils.colorFormat(locale.getLocale(Locales.RELOAD_SUCCESS)))
        return ReturnType.SUCCESS
    }



    override fun onTab(commandSender: CommandSender, vararg args: String): List<String> {
        return mutableListOf()
    }

    override fun getPermissionNode(): String {
        return "simplecrops.command.reload"
    }

    override fun getSyntax(): String {
        return "/simplecrops reload"
    }

    override fun getDescription(): String {
        return "Reloads configuration files"
    }

}

