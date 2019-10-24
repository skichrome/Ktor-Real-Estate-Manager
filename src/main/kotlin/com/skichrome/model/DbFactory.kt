package com.skichrome.model

import com.skichrome.utils.DB_PASSWORD
import com.skichrome.utils.DB_PROD_URL
import com.skichrome.utils.DB_USERNAME
import org.jetbrains.exposed.sql.*
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
        insertRealtyTypes()
        insertPoi()
        return "success"
    }

    private fun insertRealtyTypes()
    {
        val realtyTypesName = listOf("Flat", "Penthouse", "Mansion", "House", "Duplex")
        transaction(db = db) {
            RealtyType.deleteAll()
            RealtyType.batchInsert(data = realtyTypesName, ignore = true) { name ->
                this[RealtyType.name] = name
            }
        }
    }

    private fun insertPoi()
    {
        val poiNames = listOf("School", "Gas Station", "Trade", "Park", "Cash Machine")
        transaction(db = db) {
            Poi.deleteAll()
            Poi.batchInsert(data = poiNames, ignore = false) { name ->
                this[Poi.name] = name
            }
        }
    }
}