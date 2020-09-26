package eu.virtusdevelops.simplecrops.storage.cropstorage

import java.util.*

data class CropData(
    val name: String,
    val gain: Int,
    val strength: Int,
    val placedBy: UUID,
    val id: String,
    var bonemeal: Int
)