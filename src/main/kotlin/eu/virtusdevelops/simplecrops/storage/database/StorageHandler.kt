package eu.virtusdevelops.simplecrops.storage.database

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.storage.database.MySQL
import eu.virtusdevelops.simplecrops.storage.database.SQL
import eu.virtusdevelops.simplecrops.storage.database.Storage
import java.sql.Connection
import java.sql.SQLException

class StorageHandler(plugin: SimpleCrops) {
    var storage: Storage? = null

    @get:Throws(SQLException::class)
    val connection: Connection?
        get() = storage?.connection

    @Throws(SQLException::class)
    fun createTable(tableName: String, format: String) {
        storage?.createTable(tableName, format)
    }

    fun closeConnection() {
        storage?.closeConnection()
    }

    init {
        storage = if (plugin.config.getBoolean("database.useMYSQL")) {
            MySQL(plugin)
        } else {
            SQL(plugin)
        }
        plugin.logger.info("Database initialization.")
        storage!!.openConnection()
    }
}