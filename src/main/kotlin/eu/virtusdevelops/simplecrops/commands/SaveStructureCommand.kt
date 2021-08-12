package eu.virtusdevelops.simplecrops.commands

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.handlers.StructureHandler
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.multiblock.MultiBlockStructure
import eu.virtusdevelops.simplecrops.region.SelectionTool
import eu.virtusdevelops.simplecrops.util.VectorUtils
import eu.virtusdevelops.virtuscore.command.AbstractCommand
import eu.virtusdevelops.virtuscore.managers.FileManager
import eu.virtusdevelops.virtuscore.utils.TextUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.math.abs


class SaveStructureCommand(private val selectionTool: SelectionTool, private val simpleCrops: SimpleCrops,
                           private val structureHandler: StructureHandler,
                            private val fileManager: FileManager) :
    AbstractCommand(CommandType.PLAYER_ONLY, true, "savestructure") {

    override fun runCommand(commandSender: CommandSender, vararg args: String): ReturnType {
        val player = commandSender as Player


        val data = selectionTool.getLocations(player.uniqueId)



        if(data[0] == null){
            player.sendMessage(TextUtils.colorFormat("&cYou're missing one of selections..."))
            return ReturnType.FAILURE
        }else if(data[1] == null){
            player.sendMessage(TextUtils.colorFormat("&cYou're missing one of selections..."))
            return ReturnType.FAILURE
        }else if(data[2] == null){
            player.sendMessage(TextUtils.colorFormat("&cYou're missing offset selection..."))
            return ReturnType.FAILURE
        }

        val first = Vector(data[0]!!.blockX, data[0]!!.blockY, data[0]!!.blockZ)
        val second = Vector(data[1]!!.blockX, data[1]!!.blockY, data[1]!!.blockZ)
        var third = Vector(data[2]!!.blockX, data[2]!!.blockY, data[2]!!.blockZ)


        third = VectorUtils.calculateOffSet(first, second, third)
        fileManager.getConfiguration("structure_datas").set("structures.${args[0]}", third)
        fileManager.saveFile("structure_datas.yml")


        Bukkit.getScheduler().runTaskAsynchronously(simpleCrops, Runnable {
            val mbs = MultiBlockStructure.stringify(data[0], data[1], Material.AIR)
            player.sendMessage(TextUtils.colorFormat("&aSaving structure file and its offset..."))
            try {
                val path = Paths.get(simpleCrops.dataFolder.toString() + "/structures/").resolve(args[0] + ".dat")
                Files.write(path, mbs.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
                structureHandler.loadStructure(args[0])
                player.sendMessage(TextUtils.colorFormat("&aDone."))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })




        return ReturnType.SUCCESS
    }




    override fun onTab(commandSender: CommandSender, vararg args: String): List<String> {
        return listOf("<name>")
    }

    override fun getPermissionNode(): String {
        return "simplecrops.command.structure"
    }

    override fun getSyntax(): String {
        return "/simplecrops savestructure <name>"
    }

    override fun getDescription(): String {
        return "Save structure command"
    }

}

