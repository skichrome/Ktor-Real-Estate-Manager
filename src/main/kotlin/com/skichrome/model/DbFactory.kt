package com.skichrome.model

import com.skichrome.utils.DB_PASSWORD
import com.skichrome.utils.DB_PROD_URL
import com.skichrome.utils.DB_USERNAME
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
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
            SchemaUtils.createMissingTablesAndColumns(Agent, RealtyType)
            SchemaUtils.createMissingTablesAndColumns(Realty)
            SchemaUtils.createMissingTablesAndColumns(MediaReference, Poi)
            SchemaUtils.createMissingTablesAndColumns(PoiRealty)
        }
        insertRealtyTypes()
        insertPoi()
        return "success"
    }

    // --------- Insert ---------

    private fun insertRealtyTypes()
    {
        val realtyTypesName = listOf(
                Pair(1, "Flat"),
                Pair(2, "Penthouse"),
                Pair(3, "Mansion"),
                Pair(4, "House"),
                Pair(5, "Duplex")
        )

        transaction(db = db) {
            RealtyType.batchInsert(data = realtyTypesName, ignore = true) { realtyTypeToInsert ->
                this[RealtyType.id] = realtyTypeToInsert.first
                this[RealtyType.name] = realtyTypeToInsert.second
            }
        }
    }

    private fun insertPoi()
    {
        val poiName = listOf(
                Pair(1, "School"),
                Pair(2, "Gas Station"),
                Pair(3, "Trade"),
                Pair(4, "Park"),
                Pair(5, "Cash Machine")
        )

        transaction(db = db) {
            Poi.batchInsert(data = poiName, ignore = true) { poiToInsert ->
                this[Poi.id] = poiToInsert.first
                this[Poi.name] = poiToInsert.second
            }
        }
    }

    // --------- Select All ---------

    fun getAllRealtyTypes(): List<RealtyTypeData>?
    {
        val resultList: MutableList<RealtyTypeData> = mutableListOf()
        transaction(db = db) {
            RealtyType.selectAll().forEach {
                resultList.add(RealtyTypeData(
                        id = it[RealtyType.id],
                        name = it[RealtyType.name]
                ))
            }
        }
        return resultList
    }

    fun getAllPoi(): List<PoiData>?
    {
        val resultList: MutableList<PoiData> = mutableListOf()
        transaction(db = db) {
            Poi.selectAll().forEach {
                resultList.add(PoiData(
                        id = it[Poi.id],
                        name = it[Poi.name]
                ))
            }
        }
        return resultList
    }
}