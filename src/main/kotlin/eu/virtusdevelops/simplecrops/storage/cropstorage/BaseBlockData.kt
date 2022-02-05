package eu.virtusdevelops.simplecrops.storage.cropstorage

import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.util.Vector
import java.util.*

data class BaseBlockData(
    val name: String,
    val gain: Int,
    val strength: Int,
    val placedBy: UUID,
    val id: String
)