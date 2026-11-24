const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const DATABASE_ID = "ai-studio-android-stretchc-e03c86f2-0daa-415a-81b3-6bcb91bb144d";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function getAliceDb() {
  return testEnv.authenticatedContext(ALICE_UID).firestore(DATABASE_ID);
}

function getBobDb() {
  return testEnv.authenticatedContext(BOB_UID).firestore(DATABASE_ID);
}

function getUnauthDb() {
  return testEnv.unauthenticatedContext().firestore(DATABASE_ID);
}

test("Unauthenticated user cannot read or write routines", async () => {
  const unauthDb = getUnauthDb();
  const ref = unauthDb.collection("users").doc(ALICE_UID).collection("custom_routines").doc("routine_1");
  await assertFails(ref.get());
  await assertFails(ref.set({
    userId: ALICE_UID,
    name: "Morning Wakeup",
    stretches: [],
    createdAt: new Date(),
    updatedAt: new Date(),
  }));
});

test("Alice can create a valid routine in her own collection", async () => {
  const aliceDb = getAliceDb();
  const ref = aliceDb.collection("users").doc(ALICE_UID).collection("custom_routines").doc("routine_1");
  const now = new Date();
  await assertSucceeds(ref.set({
    userId: ALICE_UID,
    name: "Morning Wakeup",
    description: "Daily stretching routine",
    isCustom: true,
    stretches: [
      { name: "Neck Stretch", category: "Neck", preparationSeconds: 8, holdSeconds: 30 }
    ],
    createdAt: now,
    updatedAt: now,
  }));
});

test("Alice cannot create routine with mismatched userId", async () => {
  const aliceDb = getAliceDb();
  const ref = aliceDb.collection("users").doc(ALICE_UID).collection("custom_routines").doc("routine_1");
  const now = new Date();
  await assertFails(ref.set({
    userId: BOB_UID, // Mismatched userId
    name: "Hacked Routine",
    stretches: [],
    createdAt: now,
    updatedAt: now,
  }));
});

test("Bob cannot read or write Alice's routine", async () => {
  const aliceDb = getAliceDb();
  const bobDb = getBobDb();
  const aliceRef = aliceDb.collection("users").doc(ALICE_UID).collection("custom_routines").doc("routine_1");
  const now = new Date();
  await assertSucceeds(aliceRef.set({
    userId: ALICE_UID,
    name: "Alice's Secret Routine",
    stretches: [],
    createdAt: now,
    updatedAt: now,
  }));

  const bobAccessRef = bobDb.collection("users").doc(ALICE_UID).collection("custom_routines").doc("routine_1");
  await assertFails(bobAccessRef.get());
  await assertFails(bobAccessRef.set({
    userId: ALICE_UID,
    name: "Bob's Overwrite",
    stretches: [],
    createdAt: now,
    updatedAt: now,
  }));
  await assertFails(bobAccessRef.delete());
});

test("Alice can publish routine to public_routines", async () => {
  const aliceDb = getAliceDb();
  const publicRef = aliceDb.collection("public_routines").doc("pub_1");
  const now = new Date();
  await assertSucceeds(publicRef.set({
    authorId: ALICE_UID,
    authorName: "Alice Yogi",
    name: "Desk Neck Freedom",
    description: "5 minute relief for computer workers",
    category: "Desk",
    stretches: [
      { name: "Chin Tuck", category: "Neck", preparationSeconds: 5, holdSeconds: 25 }
    ],
    cloneCount: 0,
    createdAt: now,
    updatedAt: now,
  }));
});

test("Bob can read Alice's public routine and increment cloneCount", async () => {
  const aliceDb = getAliceDb();
  const bobDb = getBobDb();
  const alicePubRef = aliceDb.collection("public_routines").doc("pub_1");
  const now = new Date();
  await assertSucceeds(alicePubRef.set({
    authorId: ALICE_UID,
    authorName: "Alice Yogi",
    name: "Desk Neck Freedom",
    description: "5 minute relief",
    category: "Desk",
    stretches: [
      { name: "Chin Tuck", category: "Neck", preparationSeconds: 5, holdSeconds: 25 }
    ],
    cloneCount: 0,
    createdAt: now,
    updatedAt: now,
  }));

  const bobPubRef = bobDb.collection("public_routines").doc("pub_1");
  // Bob can read
  await assertSucceeds(bobPubRef.get());
  // Bob can increment cloneCount
  await assertSucceeds(bobPubRef.update({
    cloneCount: 1,
  }));
  // Bob cannot modify Alice's routine title
  await assertFails(bobPubRef.update({
    name: "Bob's Hacked Title",
  }));
  // Bob cannot delete Alice's public routine
  await assertFails(bobPubRef.delete());
});

test("Bob can follow Alice in his following collection", async () => {
  const bobDb = getBobDb();
  const followRef = bobDb.collection("users").doc(BOB_UID).collection("following").doc(ALICE_UID);
  const now = new Date();
  await assertSucceeds(followRef.set({
    creatorId: ALICE_UID,
    creatorName: "Alice Yogi",
    followedAt: now,
  }));

  // Alice cannot write into Bob's following collection
  const aliceAccessRef = getAliceDb().collection("users").doc(BOB_UID).collection("following").doc(ALICE_UID);
  await assertFails(aliceAccessRef.delete());
});
