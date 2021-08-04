package eu.virtusdevelops.simplecrops.commands

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.handlers.StructureHandler
import eu.virtusdevelops.simplecrops.listeners.BlockInteractListener
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.multiblock.MultiBlockStructure
import eu.virtusdevelops.virtuscore.command.AbstractCommand
import eu.virtusdevelops.virtuscore.utils.TextUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.libs.org.codehaus.plexus.util.FileUtils.filename
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption


class TestCommand(private val blockInteractListener: BlockInteractListener, private val locale: LocaleHandler) :
    AbstractCommand(CommandType.PLAYER_ONLY, false, "struct") {

    override fun runCommand(commandSender: CommandSender, vararg args: String): ReturnType {
        val player = commandSender as Player


        if(blockInteractListener.contains(player)){
            blockInteractListener.removePlayer(player)
        }else{
            blockInteractListener.addPlayer(player)
            player.inventory.addItem(ItemStack(Material.STICK))
        }



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

