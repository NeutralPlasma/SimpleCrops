package eu.virtusdevelops.simplecrops.util

import eu.virtusdevelops.simplecrops.util.croputil.CropUtil
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Ageable
import org.bukkit.block.data.Directional
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
        /*
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
        */
        fun getBaseBlock(block: Block): Block{
            var bdata = block
            if(block.type.toString().equals("WEEPING_VINES" ,true)
                || block.type.toString().equals("WEEPING_VINES_PLANT" ,true)){
                // check upwards...
                while (bdata.getRelative(BlockFace.UP).type.toString().equals("WEEPING_VINES_PLANT", true)
                    || bdata.getRelative(BlockFace.UP).type.toString().equals("WEEPING_VINES", true)) {
                    bdata = bdata.getRelative(BlockFace.UP)
                }
            }else if(block.type.toString().equals("TWISTING_VINES" ,true)
                || block.type.toString().equals("TWISTING_VINES_PLANT" ,true)){
                // check downwards...
                while (bdata.getRelative(BlockFace.DOWN).type.toString().equals("TWISTING_VINES_PLANT", true)
                    || bdata.getRelative(BlockFace.DOWN).type.toString().equals( "TWISTING_VINES", true)) {
                    bdata = bdata.getRelative(BlockFace.DOWN)
                }
            }else {
                while (bdata.getRelative(BlockFace.DOWN).type == block.type) {
                    bdata = bdata.getRelative(BlockFace.DOWN)
                }
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
                return if((age.age.toDouble() / age.maximumAge.toDouble()) > 0.9){
                    GrowthStage.THIRD
                }else if((age.age.toDouble() / age.maximumAge.toDouble()) >= 0.5) {
                    GrowthStage.SECOND
                }else{
                    GrowthStage.FIRST
                }
            }else if(age is Sapling){
                return if((age.stage.toDouble() / age.maximumStage.toDouble()) > 0.9){
                    GrowthStage.THIRD
                }else if((age.stage.toDouble() / age.maximumStage.toDouble()) >= 0.45) {
                    GrowthStage.SECOND
                }else{
                    GrowthStage.FIRST
                }
            }else if(block.type.toString().equals("WEEPING_VINES_PLANT", true) || block.type.toString().equals("TWISTING_VINES_PLANT", true)){
                return GrowthStage.THIRD
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

        fun Block.isCrop(): Boolean {
            //val blockData = this.blockData
            if (
                this.type == Material.WHEAT
                || this.type == Material.POTATOES
                || this.type == Material.BEETROOTS
                || this.type == Material.CARROTS
                || this.type == Material.SWEET_BERRY_BUSH
                || this.type == Material.OAK_SAPLING
                || this.type == Material.SPRUCE_SAPLING
                || this.type == Material.BIRCH_SAPLING
                || this.type == Material.JUNGLE_SAPLING
                || this.type == Material.ACACIA_SAPLING
                || this.type == Material.DARK_OAK_SAPLING
                || this.type == Material.PUMPKIN_STEM
                || this.type == Material.MELON_STEM
                || this.type == Material.COCOA_BEANS
                || this.type.toString().equals("ATTACHED_PUMPKIN_STEM", true)
                || this.type.toString().equals("BAMBOO_SAPLING", true)
                || this.type.toString().equals("ATTACHED_MELON_STEM", true)
                || this.type.toString().equals("TWISTING_VINES_PLANT", true)
                || this.type.toString().equals("WEEPING_VINES_PLANT", true)) {
                return true
            }
            return false
        }

        fun Block.getStemBlock(): CropUtil.StemBlock? {
            var relative = getRelative(BlockFace.WEST)
            if (relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM) {
                return CropUtil.StemBlock(relative, BlockFace.EAST)
            }
            relative = getRelative(BlockFace.EAST)
            if (relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM) {
                return CropUtil.StemBlock(relative, BlockFace.WEST)
            }
            relative = getRelative(BlockFace.SOUTH)
            if (relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM) {
                return CropUtil.StemBlock(relative, BlockFace.NORTH)
            }
            relative = getRelative(BlockFace.NORTH)
            if (relative.type == Material.PUMPKIN_STEM || relative.type == Material.MELON_STEM) {
                return CropUtil.StemBlock(relative, BlockFace.SOUTH)
            }
            return null

        }
    }

    class StemBlock(val block: Block, val face: BlockFace) {
        fun update() {
            if (block.type == Material.PUMPKIN_STEM)
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

    enum class GrowthStage {
        FIRST,
        SECOND,
        THIRD
    }
}