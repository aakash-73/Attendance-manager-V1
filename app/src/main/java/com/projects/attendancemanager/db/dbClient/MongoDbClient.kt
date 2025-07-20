package com.projects.attendancemanager.db.dbClient

import android.annotation.SuppressLint
import android.util.Log
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import org.bson.Document

object MongoDBClient {

    @SuppressLint("AuthLeak")
    private const val CONNECTION_STRING = "mongodb://reddyaakash0702:drrvbMRt2aMApijn@cluster0-shard-00-00.lqc1i.mongodb.net:27017," +
            "cluster0-shard-00-01.lqc1i.mongodb.net:27017," +
            "cluster0-shard-00-02.lqc1i.mongodb.net:27017/?ssl=true&replicaSet=atlas-wfy4qq-shard-0&authSource=admin&retryWrites=true&w=majority"

    private const val DATABASE_NAME = "Attendance_System"

    private val client: MongoClient by lazy {
        val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(CONNECTION_STRING))
            .build()
        MongoClients.create(settings)
    }

    val database: MongoDatabase by lazy {
        client.getDatabase(DATABASE_NAME)
    }

    fun testMongoConnection() {
        try {
            val result = database.runCommand(Document("ping", 1))
            Log.i("MongoDB", "✅ MongoDB connected: $result")
        } catch (e: Exception) {
            Log.e("MongoDB", "❌ MongoDB connection failed: ${e.message}")
        }
    }
}
