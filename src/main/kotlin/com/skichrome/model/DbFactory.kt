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

    // --------- DB Initialisation ---------

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

    // --------- Insert (SERVER ONLY) ---------

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

    // --------- Insert (FROM API) ---------

    fun insertRealtyList(newRealty: List<RealtyData>)
    {
        transaction(db = db) {
            Realty.batchInsert(ignore = true, data = newRealty) { realtyToInsert ->
                this[Realty.id] = realtyToInsert.id
                this[Realty.status] = realtyToInsert.status
                this[Realty.agentId] = realtyToInsert.agentId
                this[Realty.address] = realtyToInsert.address
                this[Realty.city] = realtyToInsert.city
                this[Realty.dateAdded] = realtyToInsert.dateAdded
                this[Realty.dateSell] = realtyToInsert.dateSell
                this[Realty.fullDescription] = realtyToInsert.fullDescription
                this[Realty.latitude] = realtyToInsert.latitude
                this[Realty.longitude] = realtyToInsert.longitude
                this[Realty.postCode] = realtyToInsert.postCode
                this[Realty.price] = realtyToInsert.price
                this[Realty.realtyTypeId] = realtyToInsert.realtyTypeId
                this[Realty.roomNumber] = realtyToInsert.roomNumber
                this[Realty.surface] = realtyToInsert.surface
            }
        }
    }

    fun insertAgentList(newAgent: List<AgentData>)
    {
        transaction(db = db) {
            Agent.batchInsert(ignore = true, data = newAgent) { agentToInsert ->
                this[Agent.agentId] = agentToInsert.agentId
                this[Agent.name] = agentToInsert.name
            }
        }
    }

    fun insertPoiRealtyList(poiRealty: List<PoiRealtyData>)
    {
        transaction(db = db) {
            PoiRealty.batchInsert(ignore = true, data = poiRealty) { poiRealtyToInsert ->
                this[PoiRealty.poiId] = poiRealtyToInsert.poiId
                this[PoiRealty.realtyId] = poiRealtyToInsert.realtyId
            }
        }
    }

    fun insertMediaReferenceList(mediaReferences: List<MediaReferenceData>)
    {
        transaction(db = db) {
            MediaReference.batchInsert(ignore = true, data = mediaReferences) { mediaRefToInsert ->
                this[MediaReference.id] = mediaRefToInsert.id
                this[MediaReference.realtyId] = mediaRefToInsert.realtyId
                this[MediaReference.reference] = mediaRefToInsert.reference
                this[MediaReference.shortDesc] = mediaRefToInsert.shortDesc
            }
        }
    }

    // --------- Select All ---------

    fun getAllRealtyTypes(): List<RealtyTypeData>
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

    fun getAllPoi(): List<PoiData>
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

    fun getAllAgents(): List<AgentData>
    {
        val resultList: MutableList<AgentData> = mutableListOf()
        transaction(db = db) {
            Agent.selectAll().forEach {
                resultList.add(AgentData(
                        agentId = it[Agent.agentId],
                        name = it[Agent.name],
                        lastUpdate = it[Agent.lastUpdate].toDate()
                ))
            }
        }
        return resultList
    }

    fun getAllPoiRealty(): List<PoiRealtyData>
    {
        val resultList: MutableList<PoiRealtyData> = mutableListOf()
        transaction(db = db) {
            PoiRealty.selectAll().forEach {
                resultList.add(PoiRealtyData(
                        poiId = it[PoiRealty.poiId],
                        realtyId = it[PoiRealty.realtyId]
                ))
            }
        }
        return resultList
    }

    fun getAllMediaReference(): List<MediaReferenceData>
    {
        val resultList: MutableList<MediaReferenceData> = mutableListOf()
        transaction(db = db) {
            MediaReference.selectAll().forEach {
                resultList.add(MediaReferenceData(
                        realtyId = it[MediaReference.realtyId],
                        id = it[MediaReference.id],
                        reference = it[MediaReference.reference],
                        shortDesc = it[MediaReference.shortDesc]
                ))
            }
        }
        return resultList
    }

    fun getAllRealty(): List<RealtyData>
    {
        val resultList: MutableList<RealtyData> = mutableListOf()
        transaction(db = db) {
            Realty.selectAll().forEach {
                resultList.add(RealtyData(
                        id = it[Realty.id],
                        status = it[Realty.status],
                        agentId = it[Realty.agentId],
                        address = it[Realty.address],
                        city = it[Realty.city],
                        dateAdded = it[Realty.dateAdded],
                        dateSell = it[Realty.dateSell],
                        fullDescription = it[Realty.fullDescription],
                        latitude = it[Realty.latitude],
                        longitude = it[Realty.longitude],
                        postCode = it[Realty.postCode],
                        price = it[Realty.price],
                        realtyTypeId = it[Realty.realtyTypeId],
                        roomNumber = it[Realty.roomNumber],
                        surface = it[Realty.surface]
                ))
            }
        }
        return resultList
    }

    // --------- Select ---------

    fun getAgentById(id: Long): AgentData?
    {
        var result: AgentData? = null
        transaction(db = db) {
            Agent.select { Agent.agentId eq id }
                    .firstOrNull {
                        result = AgentData(agentId = it[Agent.agentId], name = it[Agent.name], lastUpdate = it[Agent.lastUpdate].toDate())
                        return@firstOrNull true
                    }
        }
        return result
    }
}