package com.skichrome

import kotlinx.io.errors.IOException
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

object XmlParser
{
    private lateinit var fileOutputStream: FileOutputStream

    private const val xmlUrl = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml"
    private const val filePath = "bce-xml.xml"

    private fun downloadXml(): String
    {
        val url = URL(xmlUrl)
        try
        {
            val readableByteChannel = Channels.newChannel(url.openStream())
            fileOutputStream = FileOutputStream(filePath)
            val writeChannel = fileOutputStream.channel

            writeChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE)
        } catch (io: IOException)
        {
            val errorMsg = "an error occured when opening the file : \n\n $io"
            System.err.println("an error occured when opening the file")
            return errorMsg
        } finally
        {
            if (this::fileOutputStream.isInitialized)
                fileOutputStream.close()
        }
        return ""
    }

    fun getDataFromXml(): Map<String, Float>?
    {
        downloadXml()

        val factory = DocumentBuilderFactory.newInstance()
        try
        {
            val documentBuilder = factory.newDocumentBuilder()
            val document = documentBuilder.parse(File(filePath))

            val itemList: NodeList = document.getElementsByTagName("Cube")
            val out = mutableMapOf<String, Float>()

            for (i in 0..itemList.length)
            {
                val currency = itemList.item(i)?.attributes?.getNamedItem("currency")?.nodeValue ?: "NULL"
                val rate = itemList.item(i)?.attributes?.getNamedItem("rate")?.nodeValue?.toFloat() ?: -1f

                if (currency.contains("USD"))
                {
                    out[currency] = rate
                    val euroEquivalent: Float = 1f / rate
                    out["EUR"] = euroEquivalent
                }
            }
            return out

        } catch (e: ParserConfigurationException)
        {
            e.printStackTrace()
        } catch (e: IOException)
        {
            e.printStackTrace()
        } catch (e: Exception)
        {
            e.printStackTrace()
        }
        return null
    }
}