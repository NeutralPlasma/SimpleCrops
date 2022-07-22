package eu.virtusdevelops.simplecrops.api

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropData
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropLocation
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropStorage
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

class SimpleCropsAPI {

    companion object {
        @JvmStatic
        val plugin: SimpleCrops = SimpleCrops.INSTANCE
        @JvmStatic
        val cropDrops: CropDrops = SimpleCrops.CropDropsAPI
        @JvmStatic
        val cropStorage: CropStorage = SimpleCrops.CropStorageAPI


        @JvmStatic
        fun getCrop(block: Block): CropData? {
            return cropStorage.crops[CropLocation(block.x, block.y, block.z, block.world.name).toString()]
        }


        @JvmStatic
        fun getCropDrops(block: Block): List<ItemStack> {
            val crop = cropStorage.crops[CropLocation(block.x, block.y, block.z, block.world.name).toString()]
            if (crop != null) {
                return cropDrops.getDrops(crop)
            }
            return emptyList()
        }
    }
}