// mongodbClient.js
const { MongoClient } = require("mongodb");
require("dotenv").config();

const uri = process.env.MONGO_URI;
const dbName = process.env.MONGO_DB_NAME;

let mongoClient;
let db;

async function connectToMongo() {
    if (!mongoClient) {
        mongoClient = new MongoClient(uri, { useNewUrlParser: true, useUnifiedTopology: true });
        await mongoClient.connect();
        db = mongoClient.db(dbName);
        console.log("✅ Connected to MongoDB");
    }
    return db;
}

async function testMongoConnection() {
    try {
        const db = await connectToMongo();
        const result = await db.command({ ping: 1 });
        console.log("✅ MongoDB ping result:", result);
    } catch (error) {
        console.error("❌ MongoDB connection failed:", error.message);
    }
}

module.exports = {
    connectToMongo,
    testMongoConnection,
};
