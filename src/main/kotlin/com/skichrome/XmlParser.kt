package com.skichrome

import jdk.internal.org.xml.sax.SAXException
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

    fun downloadXml(): String
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

    fun getDataFromXml(): String
    {
        val factory = DocumentBuilderFactory.newInstance()

        try
        {
            val documentBuilder = factory.newDocumentBuilder()
            val document = documentBuilder.parse(File(filePath))

            val itemList: NodeList = document.getElementsByTagName("Cube")
            val cur = arrayListOf<String>()
            val rates = arrayListOf<String>()
            var out = ""

            for (i in 0..itemList.length)
            {
                out += "$i \t ${itemList.item(i)?.attributes?.getNamedItem("currency") ?: "NULL"}\n"

//                val strCur: String = itemList.item(i).attributes.getNamedItem("currency").nodeValue
//                val strRates: String = itemList.item(i).attributes.getNamedItem("rate").nodeValue
//                cur.add(strCur)
//                rates.add(strRates)
            }

            return "$out \t ${cur.size} \t ${rates.size}"

        } catch (e: ParserConfigurationException)
        {
            e.printStackTrace()
        } catch (e: SAXException)
        {
            e.printStackTrace()
        } catch (e: IOException)
        {
            e.printStackTrace()
        }
        return "KO"
    }
}