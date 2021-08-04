package eu.virtusdevelops.simplecrops.listeners


import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropLocation
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropStorage
import eu.virtusdevelops.simplecrops.util.CropUtil
import eu.virtusdevelops.simplehologram.API.SimpleHologramsAPI
import eu.virtusdevelops.simplehologram.hologram.Hologram
import eu.virtusdevelops.virtuscore.utils.HexUtil
import eu.virtusdevelops.virtuscore.utils.TextUtils
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
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
                        where.add(0.5, 0.5, 0.5)
                        val holo2 = SimpleHologramsAPI.createHologram("crop:${cropLocation}", 10, where, listOf(HexUtil.colorify(crop.name),
                                HexUtil.colorify("&7Gain: &e${crop.gain}"),
                                HexUtil.colorify("&7Strength: &e${crop.strength}"),
                                HexUtil.colorify("&7Progress: &r${TextUtils.getProgressBar(
                                        CropUtil.getProgress(blockFacting), 100, 10, '|', ChatColor.YELLOW, ChatColor.GRAY)}"
                                        )))
                        holo2.register(event.player)



                        activeHolograms[cropLocation.toString()] = holo2
                        Bukkit.getScheduler().runTaskLater(simpleCrops,Runnable {
                            activeHolograms.remove(cropLocation.toString())
                            SimpleHologramsAPI.removeHologram("crop:${cropLocation}")
                        }, 200)
                    }else{
                        val holo2 = activeHolograms[cropLocation.toString()]
                        holo2?.destroyClass()
                        activeHolograms.remove(cropLocation.toString())
                    }
                }
            }
        }
    }
}