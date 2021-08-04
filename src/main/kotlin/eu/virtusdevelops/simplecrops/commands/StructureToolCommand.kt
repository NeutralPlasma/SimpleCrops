package eu.virtusdevelops.simplecrops.commands

import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.region.SelectionTool
import eu.virtusdevelops.virtuscore.command.AbstractCommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class TestCommand(private val selectionTool: SelectionTool, private val locale: LocaleHandler) :
    AbstractCommand(CommandType.PLAYER_ONLY, false, "struct") {

    override fun runCommand(commandSender: CommandSender, vararg args: String): ReturnType {
        val player = commandSender as Player


        player.inventory.addItem(selectionTool.getItem())



//        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin) {
//            val mbs = MultiBlockStructure.stringify(player.location.add(Vector(-2.0, -2.0, -2.0)), player.location.add(Vector(2.0, 2.0, 2.0)), Material.AIR)
//            player.sendMessage(TextUtils.colorFormat("Saving garbage garbig"))
//            try {
//                val path = Paths.get(plugin.dataFolder.toString() + "/structures/").resolve("test.dat")
//                Files.write(path, mbs.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//
//        }




        return ReturnType.SUCCESS
    }



    override fun onTab(commandSender: CommandSender, vararg args: String): List<String> {
        return emptyList()
    }

    override fun getPermissionNode(): String {
        return "simplecrops.command.struct"
    }

    override fun getSyntax(): String {
        return "/simplecrops struct"
    }

    override fun getDescription(): String {
        return "Setup command for structures"
    }

}

