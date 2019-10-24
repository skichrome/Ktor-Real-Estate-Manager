package com.skichrome.model

import com.skichrome.utils.DB_PASSWORD
import com.skichrome.utils.DB_PROD_URL
import com.skichrome.utils.DB_USERNAME
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

object DbFactory
{
    private const val DB_DRIVER = "com.mysql.jdbc.Driver"

    private val db by lazy {
        Database.connect(url = DB_PROD_URL, driver = DB_DRIVER, user = DB_USERNAME, password = DB_PASSWORD)
    }

    fun initDb(): String
    {
        transaction(db = db) {
            addLogger(StdOutSqlLogger)
            SchemaUtils.createMissingTablesAndColumns(Agent, RealtyType)
            SchemaUtils.createMissingTablesAndColumns(Realty)
            SchemaUtils.createMissingTablesAndColumns(MediaReference, Poi)
            SchemaUtils.createMissingTablesAndColumns(PoiRealty)
        }
        return "success"
    }
}