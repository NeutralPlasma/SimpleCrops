package eu.virtusdevelops.simplecrops.storage.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import eu.virtusdevelops.simplecrops.SimpleCrops
import java.sql.Connection
import java.sql.SQLException

class MySQL(plugin: SimpleCrops) : Storage() {
    private var hikari: HikariDataSource? = null
    private val plugin = plugin


    override fun openConnection() {
        val timeout = 1000L // timeout
        val poolsize = 10 // pool size max


        val config: HikariConfig = HikariConfig()
        config.dataSourceClassName = "com.mysql.cj.jdbc.MysqlDataSource"
        config.poolName = "Storage"
        config.maximumPoolSize = poolsize
        config.connectionTimeout = timeout
        config.addDataSourceProperty("serverName", plugin.config.getString("database.ip"))
        config.addDataSourceProperty("port", plugin.config.getString("database.port"))
        config.addDataSourceProperty("databaseName", plugin.config.getString("database.name"))
        config.addDataSourceProperty("user", plugin.config.getString("database.user"))
        config.addDataSourceProperty("password", plugin.config.getString("database.password"))
        config.addDataSourceProperty("useSSL", plugin.config.getBoolean("database.useSSL"))

        hikari = HikariDataSource(config)



    }

    override fun closeConnection() {
        hikari?.close()
    }

    /**
     * Create table in database.
     *
     * @param tableName table name.
     * @param format Table format.
     */
    @Throws(SQLException::class)
    override fun createTable(tableName: String, format: String) {
        hikari?.connection.use { connection ->
            val statement = "CREATE TABLE IF NOT EXISTS $tableName ($format);"
            connection?.prepareStatement(statement).use { preparedStatement -> preparedStatement?.execute() }
        }
    }

    @get:Throws(SQLException::class)
    override val connection: Connection?
        get() = hikari!!.connection

    init {
        openConnection()
    }
}