package eu.virtusdevelops.simplecrops.util.croputil

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Ageable
import org.bukkit.block.data.Directional
import org.bukkit.block.data.type.Sapling

interface CropUtil {

    fun getBaseBlock(block: Block): Block
    fun isMultiBlock(block: Block): Boolean
    fun isPlant(block: Block): Boolean
    fun getAge(block: Block): GrowthStage



    fun getProgress(block: Block): Int{
        val blockData = block.blockData
        if (blockData is Ageable) {
            return ((blockData.age.toDouble() / blockData.maximumAge.toDouble()) * 100.0).toInt()
        }else if(blockData is Sapling){
            return ((blockData.stage.toDouble() / blockData.maximumStage.toDouble()) * 100.0).toInt()
        }
        return 0
    }

    fun Block.getStemBlock(): StemBlock?{
        var relative = getRelative(BlockFace.WEST)
        if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
            return StemBlock(relative, BlockFace.EAST)
        }
        relative = getRelative(BlockFace.EAST)
        if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
            return StemBlock(relative, BlockFace.WEST)
        }
        relative = getRelative(BlockFace.SOUTH)
        if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
            return StemBlock(relative, BlockFace.NORTH)
        }
        relative = getRelative(BlockFace.NORTH)
        if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
            return StemBlock(relative, BlockFace.SOUTH)
        }
        return null
    }


    class StemBlock(val block: Block, val face: BlockFace){
        fun update(){
            if(block.type == Material.PUMPKIN_STEM)
                block.type = Material.ATTACHED_PUMPKIN_STEM
            else
                block.type = Material.ATTACHED_MELON_STEM
            val state = block.blockData
            if (state is Directional) {
                state.facing = face
                block.blockData = state
            }
        }
    }

    enum class GrowthStage{
        FIRST,
        SECOND,
        THIRD
    }
}