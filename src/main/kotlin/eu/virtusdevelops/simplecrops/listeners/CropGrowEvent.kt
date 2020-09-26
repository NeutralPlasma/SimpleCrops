package eu.virtusdevelops.simplecrops.listeners

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropLocation
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropStorage
import eu.virtusdevelops.simplecrops.util.CropUtil
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.BlockState
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.material.Directional

class CropGrowEvent(private val cropStorage: CropStorage, private val cropDrops: CropDrops) : Listener {

    private val nonBreakBlocks = mutableListOf(Material.AIR, Material.WATER)


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onCropGrow(event: BlockGrowEvent){
        val block = event.block
        if(block.type == Material.AIR){
            val base = CropUtil.getBaseBlock(block.getRelative(BlockFace.DOWN))
            if(base.type == Material.CACTUS){
                if(getsBroken(block)){
                    val cropLocation = CropLocation(base.x, base.y, base.z, base.world.name)
                    val crop = cropStorage.crops[cropLocation.toString()]
                    if(crop != null){

                        val player = Bukkit.getPlayer(crop.placedBy)
                        cropDrops.dropDrops(block, crop, player)
                        event.isCancelled = true
                    }
                }
            }else if(!CropUtil.isCrop(block)){
                val stem = CropUtil.getStemBlock(block)
                if(stem != null){
                    val cropLocation = CropLocation(stem.x, stem.y, stem.z, stem.world.name)
                    val crop = cropStorage.crops[cropLocation.toString()]
                    if(crop != null){
                        event.isCancelled = true
                        cropDrops.growBlocks(block, crop, Bukkit.getPlayer(crop.placedBy))
                    }
                }
            }
        }
    }

    private fun getsBroken(block: Block): Boolean{
        val north = block.getRelative(BlockFace.NORTH)
        val south = block.getRelative(BlockFace.SOUTH)
        val east = block.getRelative(BlockFace.EAST)
        val west = block.getRelative(BlockFace.WEST)

        if(!nonBreakBlocks.contains(north.type) || !nonBreakBlocks.contains(south.type) || !nonBreakBlocks.contains(east.type) || !nonBreakBlocks.contains(west.type)){
            return true
        }
        return false
    }
}