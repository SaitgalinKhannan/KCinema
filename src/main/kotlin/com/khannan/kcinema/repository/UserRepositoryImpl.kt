package com.khannan.kcinema.repository

import org.springframework.stereotype.Repository
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

@Repository
class UserRepositoryImpl(db: DataSource) : UserRepository {
    private val connection: Connection = db.connection

    companion object {
        private const val CREATE_TABLE_USER =
            "CREATE TABLE IF NOT EXISTS cinemauser (userId INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY, lastName VARCHAR(50), firstName VARCHAR(50), middleName VARCHAR(50))"
    }

    init {
        try {
            connection.createStatement().use { statement ->
                statement.executeUpdate(CREATE_TABLE_USER)
            }
        } catch (e: SQLException) {
            println(e.message)
        }
    }
}