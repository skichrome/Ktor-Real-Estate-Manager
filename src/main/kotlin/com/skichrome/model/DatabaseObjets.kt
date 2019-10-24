package com.skichrome.model

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import java.util.*

object Realty : Table()
{
    val id = long("id").primaryKey()
    val price = float("price")
    val surface = float("surface")
    val roomNumber = integer("roomNumber")
    val fullDescription = varchar("fullDescription", 256)
    val address = varchar("address", 128)
    val postCode = integer("postCode")
    val city = varchar("city", 64)
    val latitude = double("latitude").nullable()
    val longitude = double("longitude").nullable()
    val status = bool("status")
    val dateAdded = date("dateAdded")
    val dateSell = date("dateSell").nullable()
    val agentId = long("agent_id").references(Agent.agentId).index("agent_id_idx")
    val realtyTypeId = integer("realty_type_id").references(RealtyType.id).index("realty_type_idx")
}

object Poi : Table()
{
    val id = integer("poiId").primaryKey()
    val name = varchar("name", 64)
}

object RealtyType : Table()
{
    val id = integer("realtyTypeId").primaryKey()
    val name = varchar("name", 64)
}

object MediaReference : Table()
{
    val id = long("mediaReferenceId").primaryKey()
    val reference = varchar("reference", 128)
    val shortDesc = varchar("shortDesc", 256)
    val realtyId = long("realtyId").references(
            ref = Realty.id,
            onUpdate = ReferenceOption.CASCADE,
            onDelete = ReferenceOption.CASCADE
    ).index("media_ref_realty_id_idx")
}

object PoiRealty : Table()
{
    val realtyId = long("realtyId").primaryKey(0).references(Realty.id)
    val poiId = integer("poiId").primaryKey(1).references(Poi.id)
}

object Agent : Table()
{
    val agentId = long("agentId").primaryKey().autoIncrement()
    val name = varchar("name", 64)
}

// ---------------------------------------------------------------------------------------------------------------------

data class PoiData(val id: Int, val name: String)
data class RealtyTypeData(val id: Int, val name: String)
data class MediaReferenceData(val id: Long, val reference: String, val shortDesc: String)

data class AgentData(val agentId: Long, val name: String)

data class RealtyData(
        val id: Long,
        val price: Float,
        val surface: Float,
        val roomNumber: Int,
        val fullDescription: String,
        val address: String,
        val postCode: Int,
        val city: String,
        val status: Boolean,
        val dateAdded: Date,
        val dateSell: Date,
        val agent: String,

        val poiList: List<PoiData>,
        val realtyType: List<RealtyTypeData>,
        val mediaReference: List<MediaReferenceData>
)