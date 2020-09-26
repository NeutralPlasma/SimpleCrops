package eu.virtusdevelops.simplecrops.util

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Ageable

class CropUtil {
    companion object {
        fun isFullyGrown(block: Block): Boolean {
            val blockData = block.blockData
            if (blockData is Ageable) {
                val age: Ageable = blockData
                return age.age == age.maximumAge
            }
            return false
        }
        fun isCrop(block: Block): Boolean{
            val blockData = block.blockData
            if (blockData is Ageable) {
                return true
            }else{
                if(block.type.toString().equals("BAMBOO_SAPLING", true)){
                    return true
                }
            }
            return false
        }
        fun getBaseBlock(block: Block): Block{
            var bdata = block
            while(bdata.getRelative(BlockFace.DOWN).type == block.type){
                bdata = bdata.getRelative(BlockFace.DOWN)
            }
            return bdata
        }
        fun isMultiBlock(block: Block): Boolean{
            return (block.getRelative(BlockFace.DOWN).type == block.type) || (block.getRelative(BlockFace.UP).type == block.type)
        }
        fun setAge(block: Block, stage : GrowthStage){
            val blockData = block.blockData
            if (blockData is Ageable) {
                val age: Ageable = blockData
                when(stage) {
                    GrowthStage.FIRST -> age.age = 0
                    GrowthStage.SECOND -> age.age = age.maximumAge/2
                    GrowthStage.THIRD -> age.age = age.maximumAge
                }
                block.blockData = age
            }
        }
        fun getProgress(block: Block): Int{
            val blockData = block.blockData
            if (blockData is Ageable) {
                val age: Ageable = blockData
                return ((age.age.toDouble() / age.maximumAge.toDouble()) * 100.0).toInt()
            }
            return 0
        }

        fun getStemBlock(block: Block): Block?{
            var relative = block.getRelative(BlockFace.WEST)
            if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
                return relative
            }
            relative = block.getRelative(BlockFace.EAST)
            if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
                return relative
            }
            relative = block.getRelative(BlockFace.SOUTH)
            if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
                return relative
            }
            relative = block.getRelative(BlockFace.NORTH)
            if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
                return relative
            }
            return null
        }

    }

    enum class GrowthStage{
        FIRST,
        SECOND,
        THIRD
    }
}