package com.appdynamics.util

import mu.KotlinLogging
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object StarWarsFilms : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val sequelId = integer("sequel_id").uniqueIndex()
    val name = varchar("name", 50)
    val director = varchar("director", 50)
}

object Players : Table() {
    val sequelId = integer("sequel_id")
        .uniqueIndex()
        .references(StarWarsFilms.sequelId)
    val name = varchar("name", 50)
}

class DatabaseFactory {

    private val log = KotlinLogging.logger { }

    fun exec() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(StarWarsFilms)

            StarWarsFilms.insert {
                it[name] = "The Last Jedi"
                it[sequelId] = 8
                it[director] = "Rian Johnson"
            }

            val query = StarWarsFilms.selectAll()
            query.forEach {
                log.info { it }
            }
        }

    }

//    private val appConfig = HoconApplicationConfig(ConfigFactory.load())
//    private val dbUrl = appConfig.property("db.jdbcUrl").getString()
//    private val dbUser = appConfig.property("db.dbUser").getString()
//    private val dbPassword = appConfig.property("db.dbPassword").getString()
//
//    fun init() {
//        Database.connect(hikari())
//    }
//
//    private fun hikari(): HikariDataSource {
//        val config = HikariConfig()
//        config.driverClassName = "org.postgresql.Driver"
//        config.jdbcUrl = dbUrl
//        config.username = dbUser
//        config.password = dbPassword
//        config.maximumPoolSize = 3
//        config.isAutoCommit = false
//        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
//        config.validate()
//        return HikariDataSource(config)
//    }
}
