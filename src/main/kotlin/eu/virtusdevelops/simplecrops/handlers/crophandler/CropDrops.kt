package eu.virtusdevelops.simplecrops.handlers.crophandler


import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.handlers.ItemHandler
import eu.virtusdevelops.simplecrops.handlers.ParticleHandler
import eu.virtusdevelops.simplecrops.handlers.StructureHandler
import eu.virtusdevelops.simplecrops.storage.cropstorage.BaseBlockData
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropData
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropLocation
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropStorage
import eu.virtusdevelops.simplecrops.util.CropUtil
import eu.virtusdevelops.simplecrops.util.nbtutil.NBTUtil
import eu.virtusdevelops.virtuscore.VirtusCore
import eu.virtusdevelops.virtuscore.managers.FileManager
import eu.virtusdevelops.virtuscore.utils.ItemUtils
import eu.virtusdevelops.virtuscore.utils.PlayerUtils
import eu.virtusdevelops.virtuscore.utils.TextUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.nio.file.Path
import java.util.function.Consumer
import java.util.logging.Level
import javax.swing.plaf.basic.BasicScrollPaneUI
import kotlin.random.Random

class CropDrops(private val plugin : SimpleCrops,
                private val fileManager: FileManager,
                private val nbt : NBTUtil,
                private val cropStorage: CropStorage,
                private val itemHandler: ItemHandler,
                private var structureHandler: StructureHandler) {

    val cropConfigurations: MutableMap<String, CropConfiguration> = mutableMapOf()

    init {
        cacheConfiguration()

    }

    /*
        SAVING/UPDATING configuration functions
     */
    private fun updateCropDrops(id: String){
        val configuration = cropConfigurations[id]
        if(configuration != null){
            fileManager.getConfiguration("crops").set("seeds.$id.drops.blocks", configuration.blockDrops.map {"${it.material}:${it.chance}"})
            fileManager.getConfiguration("crops").set("seeds.$id.drops.commands", configuration.commandDrops.map {it})
            fileManager.getConfiguration("crops").set("seeds.$id.drops.items", configuration.itemDrops.map { "custom:${ItemUtils.encodeItem(it.item)}:${it.min}:${it.max}" })
            fileManager.saveFile("crops.yml")
        }
    }

    fun updateBonemealCrop(id: String){
        val configuration = cropConfigurations[id]
        if(configuration != null){
            fileManager.getConfiguration("crops").set("seeds.$id.bonemeal.amount", configuration.boneMeal)
            fileManager.getConfiguration("crops").set("seeds.$id.bonemeal.custom", configuration.useBoneMeal)
            fileManager.saveFile("crops.yml")
        }
    }


    fun updateCropName(id: String){
        val configuration = cropConfigurations[id]
        if(configuration != null){
            fileManager.getConfiguration("crops").set("seeds.$id.name", configuration.name)
            fileManager.saveFile("crops.yml")
        }
    }

    fun updateCropType(id: String, item: ItemStack){
        val configuration = cropConfigurations[id]
        if(configuration != null){
            fileManager.getConfiguration("crops").set("seeds.$id.seed-type", "custom:${ItemUtils.encodeItem(item)}")
            fileManager.saveFile("crops.yml")
        }
    }

    fun updateCropStructures(id: String){
        val configuration = cropConfigurations[id]
        if(configuration != null) {
            fileManager.getConfiguration("crops").set(
                "seeds.$id.drops.structures",
                configuration.structures.map { "${it.structureName}:${it.dropChance}" })
            fileManager.saveFile("crops.yml")
        }

    }

    fun updateCropData(id: String){
        updateCropDrops(id)
        updateCropName(id)
        updateCropStructures(id)

        val configuration = cropConfigurations[id]
        if(configuration != null){
            fileManager.getConfiguration("crops").set("seeds.$id.gain.min", configuration.minGain)
            fileManager.getConfiguration("crops").set("seeds.$id.gain.max", configuration.maxGain)
            fileManager.getConfiguration("crops").set("seeds.$id.strength.min", configuration.minStrength)
            fileManager.getConfiguration("crops").set("seeds.$id.strength.max", configuration.maxStrength)

            fileManager.getConfiguration("crops").set("seeds.$id.dropNaturally", configuration.dropNaturally)
            fileManager.getConfiguration("crops").set("seeds.$id.dropChance", configuration.dropChance)
            fileManager.getConfiguration("crops").set("seeds.$id.levelUPChance", configuration.levelUpChance)
            fileManager.getConfiguration("crops").set("seeds.$id.duplicate", configuration.duplicate)
            fileManager.getConfiguration("crops").set("seeds.$id.duplicateChance", configuration.duplicateChance)
            fileManager.getConfiguration("crops").set("seeds.$id.type", configuration.type.toString())
            fileManager.saveFile("crops.yml")
        }
    }


    /*
        All other functionality.
     */
    fun cacheConfiguration(){
        cropConfigurations.clear()

        val section = fileManager.getConfiguration("crops").getConfigurationSection("seeds")
        if (section != null) {
            for (cropID in section.getKeys(false)) {
                // NAME
                val name = section.getString("$cropID.name")

                var item = ItemStack(Material.BARRIER)
                val material = section.getString("$cropID.seed-type")

                if(material != null) {
                    val matdata = material.split(":")
                    if (matdata[0].equals("item", true)) {
                        val materialData = Material.getMaterial(matdata[1])
                        if (materialData != null) {
                            item = ItemStack(materialData)
                        }
                    } else if (matdata[0].equals("items", true)) {
                        item = itemHandler.getItem(matdata[1])
                    } else if (matdata[0].equals("custom", true)) {
                        item = ItemUtils.decodeItem(matdata[1])
                    }
                }


                val cropConfiguration = CropConfiguration(mutableListOf(), mutableListOf(), mutableListOf(),
                    name ?: "NULL", section.getInt("$cropID.gain.min"), section.getInt("$cropID.gain.max"),
                        section.getInt("$cropID.strength.min"), section.getInt("$cropID.strength.max"),
                    section.getBoolean("$cropID.bonemeal.custom"), section.getInt("$cropID.bonemeal.amount"),
                        section.getBoolean("$cropID.dropNaturally"), section.getDouble("$cropID.dropChance"),
                    section.getDouble("$cropID.duplicateChance"), section.getBoolean("$cropID.duplicate"),
                    CropType.valueOf(section.getString("$cropID.type", "ITEMS").orEmpty()) , mutableListOf(), item,
                    section.getDouble("$cropID.levelUPChance")
                )


                // ITEM DROPS
                for (dropDataRaw in section.getStringList("$cropID.drops.items")) {
                    val dropData = dropDataRaw.split(":")
                    if (dropData[0].contentEquals("item")) {
                        val mat = Material.getMaterial(dropData[1])
                        if (mat != null) {
                            val item = ItemStack(mat)
                            cropConfiguration.itemDrops.add(DropData(item, dropData[2].toInt(), dropData[3].toInt()))

                        }
                    } else if (dropData[0].equals("custom")) {
                        try {
                            cropConfiguration.itemDrops.add(DropData(ItemUtils.decodeItem(dropData[1]), dropData[2].toInt(), dropData[3].toInt()))
                        }catch (ignored: NullPointerException){}
                    }
                    //cropConfigurations[cropID] = cropConfiguration
                }


                // BLOCKS FOR MELONS/PUMPKINS
                var min = 0.0
                for (dropDataRaw in section.getStringList("$cropID.drops.blocks")) {
                    val dropData = dropDataRaw.split(":")
                    val mat = Material.getMaterial(dropData[0])
                    if(mat != null){
                        cropConfiguration.blockDrops.add(BlockDropData(mat, dropData[1].toDouble(), min, min+dropData[1].toDouble()))
                        min+=dropData[1].toDouble()
                        //cropConfigurations[cropID] = cropConfiguration
                    }
                }

                // COMMAND DROPS
                for (dropDataRaw in section.getStringList("$cropID.drops.commands")) {
                    cropConfiguration.commandDrops.add(dropDataRaw)
                }
                // structures
                min = 0.0
                for(structureDataRaw in section.getStringList("$cropID.drops.structures")) {
                    val splited = structureDataRaw.split(":")
                    val name2 = splited[0]
                    val chance = splited[1].toDouble()
                    val data = StructureDropData(name2, chance, min, min+chance)
                    min += chance
                    cropConfiguration.structures.add(data)
                }



                cropConfigurations[cropID] = cropConfiguration
            }
        }
//        plugin.logger.info("Loaded crop configurations...")
    }


    fun handleSnip(crop: CropData, block: Block, player: Player): Boolean{
        val configuration = cropConfigurations[crop.id]
        if(configuration != null){
            val snip = (configuration.minStrength .. configuration.maxStrength).random() < crop.strength
            val cropLocation = CropLocation(block.x, block.y, block.z, block.world.name)
            return if (!snip){
                cropStorage.removeCrop(cropLocation)
                block.type = Material.AIR
                false
            } else{
                dropSeed(crop.id, crop.gain, crop.strength, block.location, player)
                CropUtil.setAge(block, CropUtil.GrowthStage.FIRST)
                true
            }
        }
        return false
    }


    fun handleLevelUP(crop: CropData, block: Block): Boolean{
        val configuration = cropConfigurations[crop.id]
        val location = CropLocation(block.x, block.y, block.z, block.world.name)
        if(configuration != null){ /* add config level up */
            val rand = (0..100).random() < configuration.levelUpChance
            val rand2 = (0..100).random() < configuration.levelUpChance
            var gain = crop.gain
            var strength = crop.strength

            var updated = false

            if(crop.strength != configuration.maxStrength && rand){
                strength += 1
                updated = true
            }
            if(crop.gain != configuration.maxGain && rand2){
                gain += 1
                updated = true
            }

            if(gain != -1 || strength != -1){
                cropStorage.removeCrop(location)
                cropStorage.addCrop(
                    CropData(
                        crop.name,
                        gain,
                        strength,
                        crop.placedBy,
                        crop.id,
                        0
                    ), location)

                return updated
            }
            return false
        }
        return false
    }

    /**
     *
     */
    fun handleBoneMeal(crop: CropData, block: Block): Boolean{
        val configuration = cropConfigurations[crop.id]
        if(configuration != null){
            if(!configuration.useBoneMeal) return false

            return if(crop.bonemeal == configuration.boneMeal){
                //Bukkit.getConsoleSender().sendMessage("" + configuration.type)
                if(configuration.type == CropType.STRUCTURE){
                    //block.type = Material.AIR
                    growStructure(block, crop)
                }
                false
            }else{
                crop.bonemeal = crop.bonemeal+1

                val stage = CropUtil.getAge(block)
                if(stage == CropUtil.GrowthStage.SECOND && crop.bonemeal < configuration.boneMeal/2) crop.bonemeal = configuration.boneMeal/2
                if(stage == CropUtil.GrowthStage.THIRD) crop.bonemeal = configuration.boneMeal

                val value = crop.bonemeal.toDouble()/configuration.boneMeal.toDouble()

                if(value < 0.5 ) CropUtil.setAge(block, CropUtil.GrowthStage.FIRST)
                if(value >= 0.5 ) CropUtil.setAge(block, CropUtil.GrowthStage.SECOND)
                if(value >= 1.0){
                    if(configuration.type == CropType.STRUCTURE){
                        block.type = Material.AIR
                        growStructure(block, crop)
                    }else {
                        CropUtil.setAge(block, CropUtil.GrowthStage.THIRD)
                    }
                }
                true
            }
        }
        return false
    }


    fun handleBaseBlock(baseBlockData: BaseBlockData, block: Block, location: CropLocation, player: Player){
        cropStorage.removeBaseBlock(location)
        dropSeed(baseBlockData.id, baseBlockData.gain, baseBlockData.strength, block.location, player)
    }


    fun handleCrop(crop: CropData, block: Block, base : Block, duplication: Boolean = false, player: Player){
        val cropLocation = CropLocation(base.x, base.y, base.z, base.world.name)
//        val player = Bukkit.getPlayer(crop.placedBy)

        if(CropUtil.isMultiBlock(base)){
            var current = block
            val type = base.type
            if(current.location == base.location){
                dropSeed(crop, block.location, false, player)
                current.type = Material.AIR
                current = current.getRelative(BlockFace.UP)
                cropStorage.removeCrop(cropLocation)
            }
            while(current.type == type){
                dropDrops(current, crop, player)
                current.type = Material.AIR
                current = current.getRelative(BlockFace.UP)
            }
        }else{
            val chance : Double = 100.0 * Random.nextDouble()
            cropStorage.removeCrop(cropLocation)
            if(CropUtil.isFullyGrown(block)){
                dropDrops(block, crop, player)
                dropSeed(crop, block.location, chance >  (100 - cropConfigurations[crop.id]?.duplicateChance!!) && duplication, player)
            }else{
                dropSeed(crop, block.location, false, player)
            }

            block.type = Material.AIR
        }
    }


    fun createSeed(id : String,  gain : Int, strength: Int): ItemStack?{

        val configuration = cropConfigurations[id] ?: return null


        val name = configuration.name
        var item = configuration.seedItem.clone()


        val meta = item.itemMeta
        if(meta != null){
            meta.setDisplayName(TextUtils.colorFormat(name))
            var lore = meta.lore
            if(lore != null) {
                lore = TextUtils.formatList(lore, "{gain}:$gain", "{strength}:$strength")
                meta.lore = lore
            }
            item.itemMeta = meta
        }

        var currentStrength = strength
        val maxStrength = configuration.maxStrength
        var currentGain = gain
        val maxGain = configuration.maxGain

        if(strength > maxStrength){
            currentStrength = maxStrength
        }

        if(gain > maxGain){
            currentGain = maxGain
        }

        item = nbt.nbt.setString(item, "cropID", id)
        item = nbt.nbt.setInt(item, "gain", currentGain)
        item = nbt.nbt.setInt(item, "strength", currentStrength)


        return item

    }
    fun dropSeed(id : String, gain : Int, strength: Int, location: Location, player: Player){
        val itemStack = createSeed(id, gain, strength);
        if(itemStack != null && location.world != null) {
            PlayerUtils.giveItem(player, itemStack, false)
        }
    }

    fun dropSeed(crop: CropData, location: Location, duplicate: Boolean, player: Player){
        val itemStack = createSeed(crop.id, crop.gain, crop.strength);
        if(itemStack != null){
            if(duplicate){ itemStack.amount = 2}else{ itemStack.amount = 1 }
            PlayerUtils.giveItem(player, itemStack, false)
        }
    }

    fun dropRandomCrop(player: Player){
        var chance: Double
        for(cropConf in cropConfigurations){
            if(cropConf.value.dropNaturally) {
                chance = 100.0 * Random.nextDouble()
                if (chance > 100-cropConf.value.dropChance) {
                    createSeed(cropConf.key, 1, 1)?.let {
                        PlayerUtils.giveItem(player, it, false)
                    }
                    break
                }
            }
        }
    }


    fun dropDrops(block: Block, crop: CropData, player: Player?){
        val configuration = cropConfigurations[crop.id]

        if(configuration != null){
            for(dropData in configuration.itemDrops){
                val item = dropData.item
                item.amount = getAmount(dropData.min, dropData.max) + (crop.gain-1)
                if(player != null) {
                    PlayerUtils.giveItem(player, item, true)
                }else{
                    block.world.dropItemNaturally(block.location, item)
                }
            }
            for(command in configuration.commandDrops){
                if(player == null) break
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.name))
            }
        }
    }

    fun growStructure(block: Block, crop: CropData){
        val configuration = cropConfigurations[crop.id] ?: return

        val structureData = randomChance(configuration.structures)
        var offSet = fileManager.getConfiguration("structure_datas").getVector("structures.${structureData.structureName}")


        val structure = structureHandler.getStructure(structureData.structureName)

        if(offSet == null){
            offSet = Vector((structure?.dimensions?.get(0) ?: 0) / 2, 1, (structure?.dimensions?.get(2) ?: 0) / 2)
        }

        structure?.buildAsync(
            block.location,
            offSet.blockX,
            offSet.blockY,
            offSet.blockZ,
            0,
            false,
            6
        ) { }

        val cropLocation = CropLocation(block.x, block.y, block.z, block.world.name)
        cropStorage.addBaseBlock(BaseBlockData(
            crop.name,
            crop.gain,
            crop.strength,
            crop.placedBy,
            crop.id
        ), cropLocation)
        cropStorage.removeCrop(cropLocation)
    }

    
    fun getDrops(crop: CropData) : List<ItemStack>{
        val configuration = cropConfigurations[crop.id]

        if(configuration != null) {
            return configuration.itemDrops.map {
                it.item.amount = getAmount(it.min, it.max) + (crop.gain-1)
                it.item
            }
        }
        return emptyList()
    }


    // Grow blocks from crop on specific location
    fun growBlocks(block: Block, crop: CropData, player: Player?){
        val configuration = cropConfigurations[crop.id]
        if(configuration != null){
            val mat = randomChance(configuration.blockDrops)
            block.type = mat
        }
    }


    // Choose random material from list based on chance.
    private fun randomChance(list: MutableList<BlockDropData>) : Material{
        val chance : Double = 100.0 * Random.nextDouble()
        for(dat: BlockDropData in list){
            if(chance >= dat.min && chance < dat.max){
                return dat.material
            }
        }
        return list[0].material
    }


    // get random structure
    private fun randomChance(list: MutableList<StructureDropData>) : StructureDropData{
        val chance : Int = Random.nextInt(100)
        for (struct: StructureDropData in list){
            if(chance >= struct.min && chance < struct.max){
                return struct
            }
        }
        return list[0]
    }

    // rand between 2 numbers
    private fun getAmount(min: Int, max: Int): Int{
        return Random.nextInt(min, max+1)
    }


}