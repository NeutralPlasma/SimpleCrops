package eu.virtusdevelops.simplecrops.util.croputil

import org.bukkit.block.Block

interface CropUtil {

    fun getBaseBlock(block: Block): Block
    fun isMultiBlock(block: Block): Boolean
    fun isPlant(block: Block): Boolean
    fun getAge(block: Block): GrowthStage

    enum class GrowthStage{
        FIRST,
        SECOND,
        THIRD
    }
}