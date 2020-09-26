package eu.virtusdevelops.simplecrops.listeners

import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropLocation
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropStorage
import eu.virtusdevelops.simplecrops.util.CropUtil
import org.bukkit.Material
import org.bukkit.block.data.Directional
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDispenseEvent

class CropDispenseListener (private val cropStorage: CropStorage, private val cropDrops: CropDrops) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onDispense(event: BlockDispenseEvent){
        val block = event.block
        val data = block.blockData as Directional
        val targetBlock = block.getRelative(data.facing)
        val item = event.item
        if(item.type == Material.BONE_MEAL){
            if(CropUtil.isCrop(targetBlock)){
                val location = CropLocation(targetBlock.x, targetBlock.y, targetBlock.z, targetBlock.world.name)
                val crop = cropStorage.crops[location.toString()]
                if(crop != null){
                    event.isCancelled = true
                    /*if(cropDrops.handleBoneMeal(crop, targetBlock)){
                        item.amount = item.amount - 1
                    }*/
                }
            }
        }
    }
}