package eu.virtusdevelops.simplecrops.listeners

import com.gmail.filoghost.holographicdisplays.api.Hologram
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropLocation
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropStorage
import eu.virtusdevelops.simplecrops.util.CropUtil
import eu.virtusdevelops.virtuscore.utils.HexUtil
import eu.virtusdevelops.virtuscore.utils.TextUtil
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleSneakEvent

class PlayerSneakListener(private val cropStorage: CropStorage, private val simpleCrops: SimpleCrops) : Listener {

    val enabled = mutableListOf<Player>()
    val activeHolograms = mutableMapOf<String, Hologram>()

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlayerSneak(event: PlayerToggleSneakEvent){
        if(enabled.contains(event.player)) {

            val blockFacting = event.player.getTargetBlock(null, 3)
            if (CropUtil.isCrop(blockFacting)) {
                val cropLocation = CropLocation(blockFacting.x, blockFacting.y, blockFacting.z, blockFacting.world.name)
                val crop = cropStorage.crops[cropLocation.toString()]
                if(crop != null && event.player.isSneaking){
                    if (activeHolograms[cropLocation.toString()] == null) {
                        val where = blockFacting.location.clone()
                        where.add(0.5, 2.0, 0.5)

                        val hologram = HologramsAPI.createHologram(simpleCrops, where)
                        hologram.insertTextLine(0, HexUtil.colorify(crop.name))
                        hologram.insertTextLine(1, HexUtil.colorify("&7Gain: &e${crop.gain}"))
                        hologram.insertTextLine(2, HexUtil.colorify("&7Strength: &e${crop.strength}"))
                        hologram.insertTextLine(3, HexUtil.colorify("&7Progress: &r${TextUtil.getProgressBar(
                            CropUtil.getProgress(blockFacting), 100, 10, '|', ChatColor.YELLOW, ChatColor.GRAY)}"))

                        activeHolograms[cropLocation.toString()] = hologram
                        Bukkit.getScheduler().runTaskLater(simpleCrops,Runnable {
                            activeHolograms.remove(cropLocation.toString())
                            hologram.delete()
                        }, 200)
                    }
                }
            }
        }
    }
}