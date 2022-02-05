package eu.virtusdevelops.simplecrops.storage.cropstorage

data class UpdateDataBlock(
    val cropData: BaseBlockData,
    val cropLocation: CropLocation,
    val remove: Boolean
)