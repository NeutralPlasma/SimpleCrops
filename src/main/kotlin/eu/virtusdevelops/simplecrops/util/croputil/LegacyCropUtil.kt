package eu.virtusdevelops.simplecrops.util.croputil

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Ageable
import org.bukkit.block.data.type.Sapling

class LegacyCropUtil: CropUtil {
    override fun getBaseBlock(block: Block): Block{
        var temp: Block = block
        while(temp.type == temp.getRelative(BlockFace.DOWN).type){
            temp = temp.getRelative(BlockFace.DOWN)
        }
        return temp
    }

    override fun isMultiBlock(block: Block): Boolean {
        return (block.getRelative(BlockFace.DOWN).type == block.type) || (block.getRelative(BlockFace.UP).type == block.type)
    }

    override fun isPlant(block: Block): Boolean {
        val blockData = block.blockData
        return (blockData is Ageable
                || blockData is Sapling
                || block.type == Material.ATTACHED_MELON_STEM
                || block.type == Material.ATTACHED_PUMPKIN_STEM)
    }

    override fun getAge(block: Block): CropUtil.GrowthStage {
        val age = block.blockData
        if (age is Ageable) {
            return if((age.age.toDouble() / age.maximumAge.toDouble()) > 0.9){
                CropUtil.GrowthStage.THIRD
            }else if((age.age.toDouble() / age.maximumAge.toDouble()) >= 0.5) {
                CropUtil.GrowthStage.SECOND
            }else{
                CropUtil.GrowthStage.FIRST
            }
        }else if(age is Sapling){
            return if((age.stage.toDouble() / age.maximumStage.toDouble()) > 0.9){
                CropUtil.GrowthStage.THIRD
            }else if((age.stage.toDouble() / age.maximumStage.toDouble()) >= 0.45) {
                CropUtil.GrowthStage.SECOND
            }else{
                CropUtil.GrowthStage.FIRST
            }
        }
        return CropUtil.GrowthStage.FIRST
    }

}