package eu.virtusdevelops.simplecrops.storage.cropstorage

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.storage.database.StorageHandler
import org.bukkit.Bukkit
import java.util.*

class CropStorage(private val plugin: SimpleCrops, private val storage: StorageHandler){
    var crops: MutableMap<String, CropData> = mutableMapOf()
    var updatedMap: MutableMap<String, UpdateData> = mutableMapOf()


    private val TABLENAME = "CropStorage"


    init {
        try{
            storage.createTable(TABLENAME, "location TEXT, name TEXT, gain INT, strength INT, placedBy STRING, id TEXT, bonemeal INT")
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
            val cropsdata = updatedMap.toMutableMap()
            updatedMap.clear()
            if (connection != null) {
                for (data: UpdateData in cropsdata.values) {

                    if(data.remove){
                        val statement2 = "DELETE FROM $TABLENAME WHERE location = ?"
                        connection.prepareStatement(statement2).use { preparedStatement ->
                            preparedStatement.setString(1, data.cropLocation.toString())
                            preparedStatement.execute()
                        }
                        plugin.logger.info("Removed: " + data.cropLocation.toString())
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
                        plugin.logger.info("Added: " + data.cropLocation.toString())
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

}