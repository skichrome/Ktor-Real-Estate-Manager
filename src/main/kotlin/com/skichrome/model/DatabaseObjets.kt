package com.skichrome.model

import org.jetbrains.exposed.sql.Table

object Realty : Table()
{
    val id = long("id").primaryKey(0)
    val price = float("price")
    val currency = integer("price_currency")
    val surface = float("surface")
    val roomNumber = integer("room_number")
    val fullDescription = varchar("full_description", 2048)
    val address = varchar("address", 256)
    val postCode = integer("post_code")
    val city = varchar("city", 128)
    val latitude = double("latitude").nullable()
    val longitude = double("longitude").nullable()
    val status = bool("status")
    val dateAdded = long("date_added")
    val dateSell = long("date_sell").nullable()
    val agentId = long("agent_id").references(Agent.agentId).index("agent_id_idx").primaryKey(1)
    val realtyTypeId = integer("realty_type_id").references(RealtyType.id).index("realty_type_idx")
}

object Poi : Table()
{
    val id = integer("poi_id").primaryKey()
    val name = varchar("name", 128)
}

object RealtyType : Table()
{
    val id = integer("realty_type_id").primaryKey()
    val name = varchar("name", 128)
}

object MediaReference : Table()
{
    val id = long("media_reference_id").primaryKey(0)
    val agentId = long("agent_id").primaryKey(1).references(Agent.agentId)
    val reference = varchar("reference", 512).index(isUnique = true)
    val serverReference = varchar("server_reference", 512).index(isUnique = true)
    val shortDesc = varchar("short_description", 128)
    val realtyId = long("realty_id").references(ref = Realty.id).index("media_ref_realty_id_idx")
}

object PoiRealty : Table()
{
    val realtyId = long("realty_id").primaryKey(0).references(Realty.id)
    val agentId = long("agent_id").primaryKey(1).references(Agent.agentId)
    val poiId = integer("poi_id").primaryKey(2).references(Poi.id)
}

object Agent : Table()
{
    val agentId = long("agent_id").primaryKey().autoIncrement()
    val name = varchar("name", 128)
    val lastUpdate = long("last_database_update")
}

// ---------------------------------------------------------------------------------------------------------------------

data class PoiData(val id: Int, val name: String)
data class RealtyTypeData(val id: Int, val name: String)
data class MediaReferenceData(val id: Long, val agent_id: Long, val reference: String, val short_desc: String, val realty_id: Long)
data class PoiRealtyData(val realty_id: Long, val agent_id: Long, val poi_id: Int)
data class AgentData(val agent_id: Long, val name: String, val last_database_update: Long)

data class RealtyData(
        val id: Long,
        val price: Float,
        val price_currency: Int,
        val surface: Float,
        val room_number: Int,
        val full_description: String,
        val address: String,
        val post_code: Int,
        val city: String,
        val latitude: Double?,
        val longitude: Double?,
        val status: Boolean,
        val date_added: Long,
        val date_sell: Long?,

        val agent_id: Long,
        val realty_type_id: Int
)