package eu.virtusdevelops.simplecrops.handlers.crophandler

data class StructureDropData(
    val structureName: String,
    var dropChance: Double,
    val min: Double = 0.0,
    val max: Double = 0.0
)
