package eu.virtusdevelops.simplecrops.listeners

import eu.virtusdevelops.simplecrops.handlers.ParticleHandler
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropType
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropLocation
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropStorage
import eu.virtusdevelops.simplecrops.util.CropUtil
import eu.virtusdevelops.simplecrops.util.CropUtil.Companion.getDirection
import eu.virtusdevelops.simplecrops.util.CropUtil.Companion.getStemBlock
import eu.virtusdevelops.simplecrops.util.CropUtil.Companion.isCrop
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Directional
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.world.StructureGrowEvent

class CropGrowEvent(private val cropStorage: CropStorage, private val cropDrops: CropDrops,
                    private val particles: ParticleHandler) : Listener {

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
            }else if(!block.isCrop()){
                val stem = block.getStemBlock()
                if(stem != null){
                    val cropLocation = CropLocation(stem.x, stem.y, stem.z, stem.world.name)
                    val crop = cropStorage.crops[cropLocation.toString()]
                    if(crop != null) {
                        event.isCancelled = true
                        cropDrops.growBlocks(block, crop, Bukkit.getPlayer(crop.placedBy))

                        val player = Bukkit.getPlayer(crop.placedBy)
                        if (player != null) {
                            //particles.playBoneMealParticle(player, stem.location)
                            particles.growEffect(player, stem.location)
                        }
                        // set stem direction
                        val direction = block.getDirection()
                        if(stem.type == Material.PUMPKIN_STEM)
                            stem.type = Material.ATTACHED_PUMPKIN_STEM
                        else
                            stem.type = Material.ATTACHED_MELON_STEM
                        val state = stem.blockData
                        if (state is Directional) {
                            state.facing = direction
                            stem.blockData = state
                        }
                    }
                }
            }
        }else{
            if(block.isCrop()){ // check if any other type of seed grew
                val cropLocation = CropLocation(block.x, block.y, block.z, block.world.name)
                val crop = cropStorage.crops[cropLocation.toString()]
                if(crop != null) {
                    val configuration = cropDrops.cropConfigurations[crop.id]
                    if (configuration?.type == CropType.STRUCTURE) {
                        if (CropUtil.isFullyGrown(block)) {
                            cropDrops.growStructure(block, crop)
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onCropStructureGrow(event: StructureGrowEvent){
        val base = event.location.block
        val cropLocation = CropLocation(base.x, base.y, base.z, base.world.name)
        val crop = cropStorage.crops[cropLocation.toString()]

        if(crop != null){
            val player = Bukkit.getPlayer(crop.placedBy)
            cropDrops.growStructure(base, crop)
            event.isCancelled = true
            event.location.block.type = Material.AIR
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