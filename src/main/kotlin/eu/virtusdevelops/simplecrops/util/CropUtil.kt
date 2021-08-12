package eu.virtusdevelops.simplecrops.util

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Ageable
import org.bukkit.block.data.type.Sapling

class CropUtil {
    companion object {
        fun isFullyGrown(block: Block): Boolean {
            val blockData = block.blockData
            if (blockData is Ageable) {
                return blockData.age == blockData.maximumAge
            }else if(blockData is Sapling){
                return false
            }
            return false
        }
        fun isCrop(block: Block): Boolean{
            val blockData = block.blockData
            if (blockData is Ageable
                || block.type.toString().equals("ATTACHED_PUMPKIN_STEM", true)
                || blockData is Sapling
                || block.type.toString().equals("BAMBOO_SAPLING", true)
                || block.type.toString().equals("ATTACHED_MELON_STEM", true)) {
                return true
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
            }else if(blockData is Sapling){
                when(stage) {
                    GrowthStage.FIRST -> blockData.stage = 0
                    GrowthStage.SECOND -> blockData.stage = blockData.maximumStage/2
                    GrowthStage.THIRD -> blockData.stage = blockData.maximumStage
                }
                block.blockData = blockData
            }
        }

        fun getAge(block: Block): GrowthStage {
            val age = block.blockData
            if (age is Ageable) {
                if((age.age.toDouble() / age.maximumAge.toDouble()) > 0.9){
                    return GrowthStage.THIRD
                }else if((age.age.toDouble() / age.maximumAge.toDouble()) >= 0.5) {
                    return GrowthStage.SECOND
                }else{
                    return GrowthStage.FIRST
                }
            }else if(age is Sapling){
                if((age.stage.toDouble() / age.maximumStage.toDouble()) > 0.9){
                    return GrowthStage.THIRD
                }else if((age.stage.toDouble() / age.maximumStage.toDouble()) >= 0.45) {
                    return GrowthStage.SECOND
                }else{
                    return GrowthStage.FIRST
                }
            }

            return GrowthStage.FIRST
        }
        fun getProgress(block: Block): Int{
            val blockData = block.blockData
            if (blockData is Ageable) {
                return ((blockData.age.toDouble() / blockData.maximumAge.toDouble()) * 100.0).toInt()
            }else if(blockData is Sapling){
                return ((blockData.stage.toDouble() / blockData.maximumStage.toDouble()) * 100.0).toInt()
            }
            return 0
        }

//        fun getStemBlock(block: Block): Block?{
//            var relative = block.getRelative(BlockFace.WEST)
//            if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
//                return relative
//            }
//            relative = block.getRelative(BlockFace.EAST)
//            if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
//                return relative
//            }
//            relative = block.getRelative(BlockFace.SOUTH)
//            if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
//                return relative
//            }
//            relative = block.getRelative(BlockFace.NORTH)
//            if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
//                return relative
//            }
//            return null
//        }

        fun Block.getStemBlock(): Block?{
            var relative = getRelative(BlockFace.WEST)
            if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
                return relative
            }
            relative = getRelative(BlockFace.EAST)
            if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
                return relative
            }
            relative = getRelative(BlockFace.SOUTH)
            if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
                return relative
            }
            relative = getRelative(BlockFace.NORTH)
            if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
                return relative
            }
            return null
        }

        fun Block.getDirection(): BlockFace{
            var relative = getRelative(BlockFace.WEST)
            if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
                return BlockFace.EAST
            }
            relative = getRelative(BlockFace.EAST)
            if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
                return BlockFace.WEST
            }
            relative = getRelative(BlockFace.SOUTH)
            if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
                return BlockFace.NORTH
            }
            relative = getRelative(BlockFace.NORTH)
            if(relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM){
                return BlockFace.SOUTH
            }
            return BlockFace.SOUTH
        }

    }

    enum class GrowthStage{
        FIRST,
        SECOND,
        THIRD
    }
}