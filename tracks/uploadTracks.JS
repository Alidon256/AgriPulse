const admin = require("firebase-admin");
const serviceAccount = require("./serviceAccountKey.json");
const tracks = require("./tracks.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

function generateRandomId(length = 20) {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}

async function uploadAllTracks() {
  const batch = db.batch();

  tracks.forEach(track => {
    const randomId = generateRandomId();
    const docRef = db.collection("tracks").doc(randomId);
    // Optionally, you can assign the generated id to the track object
    track.id = randomId;
    batch.set(docRef, track);
  });

  try {
    await batch.commit();
    console.log("✅ All tracks uploaded with generated unique IDs.");
  } catch (error) {
    console.error("❌ Error uploading tracks:", error);
  }
}

uploadAllTracks();
