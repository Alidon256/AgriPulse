const fs = require('fs');
const https = require('https');
const path = require('path');

const PROJECT_ID = 'mindsetpulse-one';
const tracksPath = path.join(__dirname, 'tracks.json');
const tracks = JSON.parse(fs.readFileSync(tracksPath, 'utf8'));

const configFile = path.join(process.env.USERPROFILE || process.env.HOME, '.config', 'configstore', 'firebase-tools.json');
const config = JSON.parse(fs.readFileSync(configFile, 'utf8'));
const accessToken = config.tokens.access_token;

function fieldToFirestoreValue(val) {
    if (val === null || val === undefined) return { nullValue: null };
    if (typeof val === 'boolean') return { booleanValue: val };
    if (typeof val === 'number') {
        if (Number.isInteger(val)) return { integerValue: val.toString() };
        return { doubleValue: val };
    }
    if (typeof val === 'string') return { stringValue: val };
    if (Array.isArray(val)) {
        return {
            arrayValue: {
                values: val.map(fieldToFirestoreValue)
            }
        };
    }
    if (typeof val === 'object') {
        const fields = {};
        for (const [k, v] of Object.entries(val)) {
            fields[k] = fieldToFirestoreValue(v);
        }
        return { mapValue: { fields } };
    }
    return { stringValue: String(val) };
}

async function uploadTrack(track, token) {
    return new Promise((resolve, reject) => {
        const trackId = `track_${track.id}`;
        const fields = {};
        for (const [key, val] of Object.entries(track)) {
            if (key === 'id') {
                fields[key] = { stringValue: trackId };
            } else {
                fields[key] = fieldToFirestoreValue(val);
            }
        }

        const body = JSON.stringify({ fields });
        const options = {
            hostname: 'firestore.googleapis.com',
            path: `/v1/projects/${PROJECT_ID}/databases/(default)/documents/tracks/${trackId}`,
            method: 'PATCH',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
                'Content-Length': Buffer.byteLength(body)
            }
        };

        const req = https.request(options, (res) => {
            let data = '';
            res.on('data', chunk => data += chunk);
            res.on('end', () => {
                if (res.statusCode >= 200 && res.statusCode < 300) {
                    console.log(`[SUCCESS] Track '${track.title}' (id: ${trackId}) uploaded.`);
                    resolve(JSON.parse(data));
                } else {
                    console.error(`[ERROR] Failed to upload '${track.title}': ${res.statusCode} - ${data}`);
                    reject(new Error(data));
                }
            });
        });

        req.on('error', err => reject(err));
        req.write(body);
        req.end();
    });
}

async function run() {
    console.log(`Uploading ${tracks.length} tracks to Firestore project '${PROJECT_ID}'...`);
    for (const track of tracks) {
        await uploadTrack(track, accessToken);
    }
    console.log("🎉 All tracks uploaded successfully to Firestore!");
}

run().catch(err => {
    console.error("Fatal error uploading tracks:", err);
    process.exit(1);
});
