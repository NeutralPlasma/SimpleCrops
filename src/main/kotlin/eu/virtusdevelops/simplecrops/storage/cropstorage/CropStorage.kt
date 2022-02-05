package eu.virtusdevelops.simplecrops.storage.cropstorage

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.storage.database.StorageHandler
import org.bukkit.Bukkit
import java.util.*

class CropStorage(private val plugin: SimpleCrops, private val storage: StorageHandler){
    var crops: MutableMap<String, CropData> = mutableMapOf()
    var baseBlocks: MutableMap<String, BaseBlockData> = mutableMapOf()


    var updatedMap: MutableMap<String, UpdateData> = mutableMapOf()
    var updatedMap2: MutableMap<String, UpdateDataBlock> = mutableMapOf()


    private val TABLENAME = "CropStorage"
    private val TABLENAME2 = "BaseBlockStorage"


    init {
        try{
            storage.createTable(TABLENAME, "location TEXT, name TEXT, gain INT, strength INT, placedBy STRING, id TEXT, bonemeal INT")
            storage.createTable(TABLENAME2, "location TEXT, name TEXT, gain INT, strength INT, placedBy STRING, id TEXT")
        } catch (error: Exception) {
            error.printStackTrace()
        }
        cacheData()

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable{
            syncData()
        }, 0, 200L)
    }


    fun syncData(){
        storage.connection.use { connection ->
            val cropsdata = updatedMap.values.toList()
            val blocksdata = updatedMap2.values.toList()
            updatedMap.clear()
            updatedMap2.clear()
            if (connection != null) {
                for (data: UpdateData in cropsdata) {

                    if(data.remove){
                        val statement2 = "DELETE FROM $TABLENAME WHERE location = ?"
                        connection.prepareStatement(statement2).use { preparedStatement ->
                            preparedStatement.setString(1, data.cropLocation.toString())
                            preparedStatement.execute()
                        }
                        plugin.logger.info("Removed crop: " + data.cropLocation.toString())
                    }else{
                        val statement2 = "INSERT INTO $TABLENAME (location," +
                                " name," +
                                " gain," +
                                " strength," +
                                " placedBy," +
                                " id," +
                                " bonemeal) VALUES (?, ?, ?, ?, ?, ?, ?);"

                        connection.prepareStatement(statement2).use { preparedStatement ->
                            preparedStatement.setString(1, data.cropLocation.toString())
                            preparedStatement.setString(2, data.cropData.name)
                            preparedStatement.setInt(3, data.cropData.gain)
                            preparedStatement.setInt(4, data.cropData.strength)
                            preparedStatement.setString(5, data.cropData.placedBy.toString())
                            preparedStatement.setString(6, data.cropData.id)
                            preparedStatement.setInt(7, data.cropData.bonemeal)
                            preparedStatement.execute()
                        }
                        plugin.logger.info("Added crop: " + data.cropLocation.toString())
                    }

                }


                for (data: UpdateDataBlock in blocksdata) {

                    if(data.remove){
                        val statement2 = "DELETE FROM $TABLENAME2 WHERE location = ?"
                        connection.prepareStatement(statement2).use { preparedStatement ->
                            preparedStatement.setString(1, data.cropLocation.toString())
                            preparedStatement.execute()
                        }
                        plugin.logger.info("Removed baseblock: " + data.cropLocation.toString())
                    }else{
                        val statement2 = "INSERT INTO $TABLENAME2 (location," +
                                " name," +
                                " gain," +
                                " strength," +
                                " placedBy," +
                                " id ) VALUES (?, ?, ?, ?, ?, ?);"

                        connection.prepareStatement(statement2).use { preparedStatement ->
                            preparedStatement.setString(1, data.cropLocation.toString())
                            preparedStatement.setString(2, data.cropData.name)
                            preparedStatement.setInt(3, data.cropData.gain)
                            preparedStatement.setInt(4, data.cropData.strength)
                            preparedStatement.setString(5, data.cropData.placedBy.toString())
                            preparedStatement.setString(6, data.cropData.id)
                            preparedStatement.execute()
                        }
                        plugin.logger.info("Added baseblock: " + data.cropLocation.toString())
                    }

                }

            }
        }
    }

    private fun cacheData(){
        storage.connection.use { connection ->
            if(connection != null){
                val statement = "SELECT * FROM $TABLENAME;"
                connection.prepareStatement(statement).use { preparedStatement ->
                    var amount = 0
                    val resultSet = preparedStatement.executeQuery()
                    while (resultSet.next()){
                        amount++
                        try{
                            val data = CropData(resultSet.getString("name"),
                                resultSet.getInt("gain"),
                                resultSet.getInt("strength"),
                                UUID.fromString(resultSet.getString("placedBy")),
                                resultSet.getString("id"),
                                resultSet.getInt("bonemeal"))
                            val location = resultSet.getString("location")
                            crops[location] = data
                        }catch (ignored: Exception){ }
                    }
                    Bukkit.getLogger().info("Loaded $amount crops from database.")
                }

                val statement2 = "SELECT * FROM $TABLENAME2;"
                connection.prepareStatement(statement2).use { preparedStatement ->
                    var amount = 0
                    val resultSet = preparedStatement.executeQuery()
                    while (resultSet.next()){
                        amount++
                        try{
                            val data = BaseBlockData(resultSet.getString("name"),
                                resultSet.getInt("gain"),
                                resultSet.getInt("strength"),
                                UUID.fromString(resultSet.getString("placedBy")),
                                resultSet.getString("id"))
                            val location = resultSet.getString("location")
                            baseBlocks[location] = data
                        }catch (ignored: Exception){ }
                    }
                    Bukkit.getLogger().info("Loaded $amount base blocks from database.")
                }

            }
        }
    }

    fun addCrop(crop: CropData, location: CropLocation){
        if(crops[location.toString()] == null){


            updatedMap.remove("$location:add") // removes any previous statements
            updatedMap["$location:add"] =  UpdateData(crop, location, false) // adds new one


            crops[location.toString()] = crop
        }
    }

    fun removeCrop(location: CropLocation){
        val crop = crops[location.toString()]
        if(crop != null) {

            updatedMap.remove("$location:remove") // removes any previous statements
            updatedMap["$location:remove"] =  UpdateData(crop, location, true) // adds new one

            crops.remove(location.toString())
        }
    }



    fun addBaseBlock(block: BaseBlockData, location: CropLocation){
        if(baseBlocks[location.toString()] == null){


            updatedMap2.remove("$location:add") // removes any previous statements
            updatedMap2["$location:add"] = UpdateDataBlock(block, location, false) // adds new one


            baseBlocks[location.toString()] = block
        }
    }

    fun removeBaseBlock(location: CropLocation){
        val baseBlock = baseBlocks[location.toString()]
        if(baseBlock != null){
            // add sql to remove it
            updatedMap2.remove("$location:remove") // removes any previous statements
            updatedMap2["$location:remove"] =  UpdateDataBlock(baseBlock, location, true) // adds new one

            baseBlocks.remove(location.toString())
        }
    }

}