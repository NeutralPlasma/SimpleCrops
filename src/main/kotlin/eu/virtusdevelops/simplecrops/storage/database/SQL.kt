package eu.virtusdevelops.simplecrops.storage.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import eu.virtusdevelops.simplecrops.SimpleCrops
import java.io.File
import java.sql.Connection
import java.sql.SQLException

class SQL(plugin: SimpleCrops) : Storage() {
    private var hikari: HikariDataSource? = null
    private val plugin = plugin



    /**
     * Open SQL connection to file.
     */
    override fun openConnection() {
        val database = "DataBase"
        val path = plugin.dataFolder.path
//        val file = File("$path$database.db")
//        try {
//            if (!file.exists()) {
//                file.createNewFile()
//            }
//        } catch (error: Exception) {
//        }
        val config = HikariConfig()
        config.poolName = "Storage"
        config.driverClassName = "org.sqlite.JDBC"
        config.jdbcUrl = "jdbc:sqlite:$path/$database.db"
        config.connectionTestQuery = "SELECT 1"
        config.maxLifetime = 60000 // 60 Sec
        config.maximumPoolSize = 10 // 50 Connections (including idle connections)
        hikari = HikariDataSource(config)
    }

    /**
     * Close SQL connection.
     */
    override fun closeConnection() {
        hikari!!.close()
    }

    /**
     * Create table in database.
     *
     * @param tableName table name.
     * @param format    Table format.
     */
    @Throws(SQLException::class)
    override fun createTable(tableName: String, format: String) {
        hikari!!.connection.use { connection ->
            val statement = "CREATE TABLE IF NOT EXISTS $tableName ($format);"
            connection.prepareStatement(statement).use { preparedStatement -> preparedStatement.execute() }
        }
    }

    @get:Throws(SQLException::class)
    override val connection: Connection?
        get() = hikari!!.connection
}