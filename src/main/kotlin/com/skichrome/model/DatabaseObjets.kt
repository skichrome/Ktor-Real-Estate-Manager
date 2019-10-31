package com.skichrome.model

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime
import java.util.*

object Realty : Table()
{
    val id = long("id").primaryKey()
    val price = float("price")
    val surface = float("surface")
    val roomNumber = integer("room_number")
    val fullDescription = varchar("full_description", 256)
    val address = varchar("address", 128)
    val postCode = integer("postCode")
    val city = varchar("city", 64)
    val latitude = double("latitude").nullable()
    val longitude = double("longitude").nullable()
    val status = bool("status")
    val dateAdded = date("date_added")
    val dateSell = date("date_sell").nullable()
    val agentId = long("agent_id").references(Agent.agentId).index("agent_id_idx")
    val realtyTypeId = integer("realty_type_id").references(RealtyType.id).index("realty_type_idx")
}

object Poi : Table()
{
    val id = integer("poi_id").primaryKey()
    val name = varchar("name", 64)
}

object RealtyType : Table()
{
    val id = integer("realty_type_id").primaryKey()
    val name = varchar("name", 64)
}

object MediaReference : Table()
{
    val id = long("media_reference_id").primaryKey()
    val reference = varchar("reference", 128)
    val shortDesc = varchar("short_description", 256)
    val realtyId = long("realty_id").references(
            ref = Realty.id,
            onUpdate = ReferenceOption.CASCADE,
            onDelete = ReferenceOption.CASCADE
    ).index("media_ref_realty_id_idx")
}

object PoiRealty : Table()
{
    val realtyId = long("realty_id").primaryKey(0).references(Realty.id)
    val poiId = integer("poi_id").primaryKey(1).references(Poi.id)
}

object Agent : Table()
{
    val agentId = long("agent_id").primaryKey().autoIncrement()
    val name = varchar("name", 64)
    val lastUpdate = datetime("last_update").default(DateTime(System.currentTimeMillis()))
}

// ---------------------------------------------------------------------------------------------------------------------

data class PoiData(val id: Int, val name: String)
data class RealtyTypeData(val id: Int, val name: String)
data class MediaReferenceData(val id: Long, val reference: String, val shortDesc: String, val realtyId: Long)
data class PoiRealtyData(val realtyId: Long, val poiId: Int)
data class AgentData(val agentId: Long, val name: String, val lastUpdate: Date)

data class RealtyData(
        val id: Long,
        val price: Float,
        val surface: Float,
        val roomNumber: Int,
        val fullDescription: String,
        val address: String,
        val postCode: Int,
        val city: String,
        val latitude: Double?,
        val longitude: Double?,
        val status: Boolean,
        val dateAdded: DateTime,
        val dateSell: DateTime?,

        val agentId: Long,
        val realtyTypeId: Int
)