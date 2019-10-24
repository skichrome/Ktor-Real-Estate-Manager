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
    val id = integer("poiId").primaryKey().autoIncrement()
    val name = varchar("name", 64)
//    val realtyId = long("realtyId").references(Realty.id).index("poi_realty_id_idx")
}

object RealtyType : Table()
{
    val id = integer("realtyTypeId").primaryKey().autoIncrement()
    val name = varchar("name", 64)
}

object MediaReference : Table()
{
    val id = long("mediaReferenceId").primaryKey()
    val reference = varchar("reference", 128)
    val shortDesc = varchar("shortDesc", 256)
    var realtyId = long("realtyId").references(
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
    val agentId = long("agentId").primaryKey()
    val name = varchar("name", 64)
}

// ---------------------------------------------------------------------------------------------------------------------

data class PoiData(private val id: Long, private val type: String)
data class RealtyTypeData(private val id: Long, private val name: String)
data class MediaReferenceData(private val id: Long, private val reference: String, private val shortDesc: String)

data class RealtyData(
        private val id: Long,
        private val price: Float,
        private val surface: Float,
        private val roomNumber: Int,
        private val fullDescription: String,
        private val address: String,
        private val postCode: Int,
        private val city: String,
        private val status: Boolean,
        private val dateAdded: Date,
        private val dateSell: Date,
        private val agent: String,

        private val poiList: List<PoiData>,
        private val realtyType: List<RealtyTypeData>,
        private val mediaReference: List<MediaReferenceData>
)