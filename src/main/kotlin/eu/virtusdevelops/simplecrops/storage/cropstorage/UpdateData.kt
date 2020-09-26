package eu.virtusdevelops.simplecrops.storage.cropstorage

data class UpdateData(
    val cropData: CropData,
    val cropLocation: CropLocation,
    val remove: Boolean
)