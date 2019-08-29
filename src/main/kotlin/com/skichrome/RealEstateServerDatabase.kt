package com.skichrome

import com.skichrome.model.MediaReference
import com.skichrome.model.Poi
import com.skichrome.model.Realty
import com.skichrome.model.RealtyType
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object RealEstateServerDatabase
{
    private const val host = "jdbc:mysql://localhost:3306/real_estate"
    private const val driver = "com.mysql.jdbc.Driver"
    private const val username = "realestate"
    private const val pwd = "PASSWD"

    private val database: Database by lazy { Database.Companion.connect(driver, username, pwd) }

    fun initDB()
    {
        transaction(db = database)
        {
            SchemaUtils.create(Realty, Poi, RealtyType, MediaReference)
        }
    }
}