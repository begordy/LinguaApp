package com.cs407.lingua

import android.content.Context
import com.google.gson.Gson
import java.io.InputStream

class Utils() {
    companion object {
        private fun readJSONFromAssets(): String? {
            val context: Context = MainActivity.applicationContext()

            var json: String? = null
            try {
                val inputStream: InputStream = context.assets.open("phoneticsGlossings.json")
                json = inputStream.bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
            return json
        }

        fun parseJSON(): List<WordData> { // used by DataLoader
            val jsonString = readJSONFromAssets()
            return Gson().fromJson(jsonString, Array<WordData>::class.java).asList()
        }
    }
}