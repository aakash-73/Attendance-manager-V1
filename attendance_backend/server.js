// server.js
const express = require("express");
const { connectToMongo, testMongoConnection } = require("./mongodbClient");

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json()); // middleware to parse JSON

// Test MongoDB connection on startup
testMongoConnection();

// ✅ NEW: Test route to verify data is stored in MongoDB
app.get("/api/test-db-connection", async (req, res) => {
    try {
        const db = await connectToMongo();
        const collection = db.collection("AttendanceRecords");

        const records = await collection.find({}).limit(5).toArray();

        res.status(200).json({
            message: "Fetched records from AttendanceRecords",
            count: records.length,
            data: records,
        });
    } catch (err) {
        console.error("❌ Error fetching records:", err);
        res.status(500).json({ error: "Failed to fetch records", detail: err.message });
    }
});

// POST: Attendance Sync
app.post("/api/attendance", async (req, res) => {
    try {
        const db = await connectToMongo();
        const collection = db.collection("AttendanceRecords");

        const records = req.body; // expects array of attendance records or single object

        if (!records || (Array.isArray(records) && records.length === 0)) {
            return res.status(400).json({ error: "Invalid or empty payload." });
        }

        const insertResult = Array.isArray(records)
            ? await collection.insertMany(records)
            : await collection.insertOne(records);

        res.status(200).json({
            message: "Attendance record(s) inserted successfully",
            inserted: insertResult.insertedCount || 1,
        });
    } catch (err) {
        console.error("❌ Error inserting attendance:", err);
        res.status(500).json({ error: "Failed to insert attendance", detail: err.message });
    }
});

// POST: User Sync
app.post("/api/users", async (req, res) => {
    try {
        const db = await connectToMongo();
        const collection = db.collection("users");

        const user = req.body;

        if (!user || !user.username || !user.email) {
            return res.status(400).json({ error: "Invalid user payload" });
        }

        // Convert `_id` to ObjectId if present
        if (user._id && typeof user._id === "string") {
            user._id = new ObjectId(user._id);
        }

        const result = await collection.insertOne(user);
        res.status(200).json({ message: "User inserted", id: result.insertedId });
    } catch (err) {
        console.error("❌ Error inserting user:", err);
        res.status(500).json({ error: "Failed to insert user", detail: err.message });
    }
});


const { ObjectId } = require("mongodb");

app.delete("/api/users/:id", async (req, res) => {
    try {
        const db = await connectToMongo();
        const collection = db.collection("users");

        if (!ObjectId.isValid(req.params.id)) {
            return res.status(400).json({ error: "Invalid user ID" });
        }

        const result = await collection.deleteOne({ _id: new ObjectId(req.params.id) });

        if (result.deletedCount === 0) {
            return res.status(404).json({ error: "User not found" });
        }

        res.status(200).json({ message: "User deleted" });
    } catch (err) {
        res.status(500).json({ error: "Failed to delete user", detail: err.message });
    }
});

const { sendResetEmail } = require("./resetPassword");

// POST: Reset Password
app.post("/api/reset-password", async (req, res) => {
  try {
    const { email } = req.body;

    if (!email) {
      return res.status(400).json({ error: "Email is required" });
    }

    const db = await connectToMongo();
    const usersCollection = db.collection("users");

    // Find user by email (case-insensitive)
    const user = await usersCollection.findOne({ email: { $regex: new RegExp(`^${email}$`, "i") } });

    if (!user) {
      return res.status(404).json({ error: "User not registered" });
    }

    await sendResetEmail(user.email, user.username || user.name || "User");

    res.status(200).json({ message: "Reset link sent successfully" });
  } catch (error) {
    console.error("❌ Error in reset-password:", error);
    res.status(500).json({ error: "Failed to send reset link", detail: error.message });
  }
});

const bcrypt = require('bcrypt');

app.post("/api/reset-password/submit", async (req, res) => {
    const { email, newPassword } = req.body;
    const db = await connectToMongo();
    const users = db.collection("users");

    if (!email || !newPassword) {
        return res.status(400).json({ error: "Email and new password are required" });
    }

    try {
        const saltRounds = 10;
        const hashedPassword = await bcrypt.hash(newPassword, saltRounds);

        const result = await users.updateOne({ email }, { $set: { password: hashedPassword } });

        if (result.matchedCount === 0) {
            return res.status(404).json({ error: "User not found" });
        }

        res.status(200).json({ 
            message: "Password updated successfully",
            hashedPassword  // 🔁 Return the hashed password
        });
    } catch (error) {
        console.error("Error updating password:", error);
        res.status(500).json({ error: "Internal server error" });
    }
});

app.listen(PORT, () => {
    console.log(`🚀 Server running at http://localhost:${PORT}`);
});
