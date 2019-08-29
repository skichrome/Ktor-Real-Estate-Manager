package com.skichrome.model

import org.jetbrains.exposed.sql.Table
import java.util.*

object Realty : Table()
{
    val id = long("id").primaryKey().autoIncrement()
    val price = float("price")
    val surface = float("surface")
    val roomNumber = integer("roomNumber")
    val fullDescription = varchar("fullDescription", 256)
    val address = varchar("address", 128)
    val postCode = integer("postCode")
    val city = varchar("city", 64)
    val status = bool("status")
    val dateAdded = date("dateAdded")
    val dateSell = date("dateSell").nullable()
    val agent = varchar("agent", 64)
}

object Poi : Table()
{
    val id = long("poiId").primaryKey().autoIncrement()
    val type = varchar("type", 64)
    val realtyId = long("realtyId").references(Realty.id).index("poi_realty_id_idx")
}

object RealtyType : Table()
{
    val id = long("realtyTypeId").primaryKey().autoIncrement()
    val name = varchar("name", 64)
    val realtyId = long("realtyId").references(Realty.id).index("realty_type_realty_id_idx")
}

object MediaReference : Table()
{
    val id = long("mediaReferenceId").primaryKey().autoIncrement()
    val reference = varchar("reference", 128)
    val shortDesc = varchar("shortDesc", 256)
    var realtyId = long("realtyId").references(Realty.id).index("media_ref_realty_id_idx")
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