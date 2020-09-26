package eu.virtusdevelops.simplecrops.storage.cropstorage

data class CropLocation(
    val x: Int,
    val y: Int,
    val z: Int,
    val world: String
){

    override fun toString(): String {
        return "$x:$y:$z:$world"
    }
}