package eu.virtusdevelops.simplecrops.listeners

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.handlers.ParticleHandler
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.handlers.hoehandler.HoeHandler
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropData
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropLocation
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropStorage
import eu.virtusdevelops.simplecrops.util.CropUtil
import eu.virtusdevelops.simplecrops.util.CropUtil.Companion.isCrop
import eu.virtusdevelops.simplecrops.util.nbtutil.NBTUtil
import eu.virtusdevelops.virtuscore.utils.BlockUtils
import eu.virtusdevelops.virtuscore.utils.PlayerUtils
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class NewCropInteractListener(private val cropStorage: CropStorage, private val cropDrops: CropDrops,
                              private val nbtUtil: NBTUtil, private val hoeHandler: HoeHandler,
                              private val plugin: SimpleCrops,
                              private val particles: ParticleHandler
) : Listener {


    private val allowedMaterials = listOf(Material.GRASS, Material.TALL_GRASS, Material.FERN, Material.AIR)


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onCropInteract(event: PlayerInteractEvent){

        val block = event.clickedBlock
        val player = event.player


        // useless event actions we dont care about.
        if(event.action == Action.RIGHT_CLICK_AIR || event.action == Action.LEFT_CLICK_AIR
            || event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK) return
        if(block == null) return // if invalid block just return
        val item : ItemStack = if(event.hand == EquipmentSlot.HAND){
            player.inventory.itemInMainHand
        }else{
            player.inventory.itemInOffHand
        }



        if(item.type.toString().contains("HOE") && (block.type == Material.GRASS_BLOCK || block.type == Material.DIRT))
            if (handleHoe(player, block, item)) return

        if(!block.isCrop() && block.type != Material.FARMLAND) return // if is not crop just return
        val location = CropLocation(block.x, block.y, block.z, block.world.name)
        val crop = cropStorage.crops[location.toString()] // return if not custom crop.
        event.isCancelled = true

        if(crop != null) {
            if (item.type.toString().contains("HOE"))
                if (handleHoeHarvest(player,block,item,crop)) return // handle hoe thing.
            if (item.type == Material.BONE_MEAL)
                if (handleBoneMeal(player,block,item,crop)) return
            if(item.type == Material.SHEARS)
                if (handleSnip(player,block,item,crop )) return
        }



        if(event.action == Action.PHYSICAL){ // jumping on farmland
            val interacted = block.getRelative(BlockFace.UP)
            val location2 = CropLocation(interacted.x, interacted.y, interacted.z, interacted.world.name)
            val crop2 = cropStorage.crops[location2.toString()]
            if (crop2 != null) {
                event.isCancelled = true
            }
        }

    }


    private fun handleSnip(player: Player, block: Block, item: ItemStack, crop: CropData): Boolean{
        if(!player.hasPermission("simplecrops.snipcrop")) return false
        if(!CropUtil.isMultiBlock(block) && CropUtil.isFullyGrown(block)){
            if(cropDrops.handleSnip(crop, block)){
                particles.playBreakParticles(player, block.location)
                player.playSound(block.location, Sound.BLOCK_CROP_BREAK, 1.0F, 1.0F)
                return true
            }else{
                particles.playBreakParticles(player, block.location)
                player.playSound(block.location, Sound.BLOCK_CROP_BREAK, 1.0F, 1.0F)
            }
        }
        return false
    }


    private fun handleBoneMeal(player: Player, block: Block, item: ItemStack, crop: CropData): Boolean{
        if(cropDrops.handleBoneMeal(crop, block)){
            //particles.playBoneMealParticle(player, block.location)
            //player.sendMessage("Bone mealing..")
            particles.growEffect(player, block.location)
            if(player.gameMode != GameMode.CREATIVE) item.amount = item.amount-1
            return true
        }
        return false
    }


    private fun handleHoe(player: Player, block: Block, item: ItemStack): Boolean{
        if(!player.hasPermission("simplecrops.multifarm")) return false
        if(!nbtUtil.nbt.hasValueString(item, "hoeID")) return false

        val id = nbtUtil.nbt.getString(item, "hoeID")
        val size = nbtUtil.nbt.getInt(item, "size")
        val uses = nbtUtil.nbt.getInt(item, "uses")
        if (uses == 1) {
            PlayerUtils.breakAnimation(player, item)
            player.inventory.setItemInMainHand(null)
        }

        val blocks = BlockUtils.getSquare(block, size / 2, 1)

        var update = false
        blocks.forEach { blocke ->
            if(blocke.type == Material.GRASS_BLOCK || blocke.type == Material.DIRT) {
                if (allowedMaterials.contains(blocke.getRelative(BlockFace.UP).type)) {

                    Bukkit.getScheduler().runTaskLater(plugin, Runnable{
                        blocke.setType(Material.FARMLAND, true)
                        val blockup = blocke.getRelative(BlockFace.UP)
                        if(blockup.type != Material.AIR){
                            blockup.breakNaturally()

                        }
                        player.playSound(blocke.location, Sound.ITEM_HOE_TILL, 1.0F, 1.0F)

                    }, (10L..20L).random())

                    update = true
                }
            }
        }
        if(update){
            hoeHandler.updateHoe(id, item, uses-1, size)
        }
        return true

    }

    private fun handleHoeHarvest(player: Player, block: Block, item: ItemStack, crop: CropData): Boolean{
        if(!player.hasPermission("simplecrops.fastharvest")) return false

        if(nbtUtil.nbt.hasValueString(item, "hoeID")){
            val id = nbtUtil.nbt.getString(item, "hoeID")
            val size = nbtUtil.nbt.getInt(item, "size")
            val uses = nbtUtil.nbt.getInt(item, "uses")
            if (uses == 1) {
                PlayerUtils.breakAnimation(player, item)
                player.inventory.setItemInMainHand(null)
            }
            var update = false
            val blocks = BlockUtils.getSquare(block, size / 2, 1)
            for (sblock in blocks) {
                if (sblock.isCrop() && !CropUtil.isMultiBlock(sblock)) {
                    if (CropUtil.isFullyGrown(sblock)) {
                        val newlocation = CropLocation(sblock.x, sblock.y, sblock.z, sblock.world.name)
                        val newcrop = cropStorage.crops[newlocation.toString()]
                        if (newcrop != null) {
                            val cPlayer = Bukkit.getPlayer(crop.placedBy)
                            if (cPlayer == null) {
                                cropDrops.dropDrops(sblock, newcrop, player)
                            } else {
                                cropDrops.dropDrops(sblock, newcrop, cPlayer)
                            }
                            if(cropDrops.handleLevelUP(newcrop, sblock)){
                                player.playSound(block.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
                            }
                            newcrop.bonemeal = 0
                            player.spawnParticle(Particle.CLOUD, sblock.x.toDouble() + 0.5, sblock.y.toDouble() + 0.2, sblock.z.toDouble() + 0.5, 5, 0.01, 0.0, 0.01, 0.02)
                            player.playSound(block.location, Sound.BLOCK_CROP_BREAK, 1.0f, 1.0f)
                            CropUtil.setAge(sblock, CropUtil.GrowthStage.FIRST)
                            update = true
                        }
                    }
                }
            }

            if (update) {
                hoeHandler.updateHoe(id, item, uses - 1, size)
            }
            return true
        }

        if(CropUtil.isFullyGrown(block)) {

            val cPlayer = Bukkit.getPlayer(crop.placedBy)
            if (cPlayer == null) {
                cropDrops.dropDrops(block, crop, player)
            } else {
                cropDrops.dropDrops(block, crop, cPlayer)
            }
            crop.bonemeal = 0
            player.spawnParticle(Particle.CLOUD, block.x.toDouble() + 0.5, block.y.toDouble() + 0.2, block.z.toDouble() + 0.5, 5, 0.01, 0.0, 0.01, 0.02)
            player.playSound(block.location, Sound.BLOCK_CROP_BREAK, 1.0f, 1.0f)
            CropUtil.setAge(block, CropUtil.GrowthStage.FIRST)

            return true
        }
        return true
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun mobInteract(event: EntityInteractEvent){
        val block = event.block.getRelative(BlockFace.UP)

        val location = CropLocation(block.x, block.y, block.z, block.world.name)
        val crop = cropStorage.crops[location.toString()]
        if (crop != null) {
            event.isCancelled = true
        }

    }

}