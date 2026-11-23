const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
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

function buildValidProfile(uid, ts) {
  return {
    userId: uid,
    displayName: "Atlas Explorer",
    handle: "explorer_1",
    avatarId: "compass",
    bio: "Chasing world trivia",
    totalScore: 420,
    highScore: 180,
    gamesPlayed: 3,
    totalCorrect: 24,
    totalAnswered: 30,
    bestStreak: 7,
    favoriteCategory: "science",
    createdAt: ts,
    updatedAt: ts,
  };
}

function buildValidLeaderboard(uid, ts) {
  return {
    userId: uid,
    displayName: "Atlas Explorer",
    handle: "explorer_1",
    avatarId: "compass",
    totalScore: 420,
    highScore: 180,
    gamesPlayed: 3,
    accuracyPercent: 80,
    bestStreak: 7,
    createdAt: ts,
    updatedAt: ts,
  };
}

test("Unauthenticated user: cannot read or write any collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const ts = new Date(Date.now() - 2000);

  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set(buildValidProfile(ALICE_UID, ts))
  );
  await assertFails(
    unauthDb.collection("leaderboard").where("totalScore", ">=", 0).get()
  );
  await assertFails(
    unauthDb.collection("leaderboard").doc(ALICE_UID).set(buildValidLeaderboard(ALICE_UID, ts))
  );
});

test("UserProfile: owner create, partial update, and negative security bounds", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 10000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(buildValidProfile(ALICE_UID, created))
  );

  // Partial update on mutable profile fields
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      displayName: "Nova Scholar",
      avatarId: "owl",
      bio: "Updated bio",
      totalScore: 600,
      highScore: 210,
      gamesPlayed: 4,
      totalCorrect: 32,
      totalAnswered: 40,
      bestStreak: 9,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject immutable createdAt mutation
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject ghost field injection
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isAdmin: true,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject invalid avatarId enum
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      avatarId: "dragon",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject future timestamp
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      displayName: "Future Cheater",
      updatedAt: new Date(Date.now() + 60000),
    })
  );
});

test("UserProfile & QuizSessions: cross-user isolation enforced", async () => {
  const bobTs = new Date(Date.now() - 10000);
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const db = context.firestore();
    await db.collection("users").doc(BOB_UID).set(buildValidProfile(BOB_UID, bobTs));
    await db
      .collection("users")
      .doc(BOB_UID)
      .collection("quiz_sessions")
      .doc("sess_1")
      .set({
        category: "history",
        difficulty: "medium",
        mode: "standard",
        score: 140,
        correctCount: 7,
        totalQuestions: 10,
        bestStreak: 4,
        durationSeconds: 75,
        playedOffline: false,
        createdAt: bobTs,
      });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).update({ displayName: "Hacked" })
  );
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("quiz_sessions").get()
  );
  await assertFails(
    aliceDb
      .collection("users")
      .doc(BOB_UID)
      .collection("quiz_sessions")
      .doc("sess_2")
      .set({
        category: "science",
        difficulty: "easy",
        mode: "standard",
        score: 100,
        correctCount: 5,
        totalQuestions: 5,
        bestStreak: 5,
        durationSeconds: 30,
        playedOffline: true,
        createdAt: new Date(Date.now() - 1000),
      })
  );
});

test("QuizSessions: owner can create & list, cannot update existing session", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ts = new Date(Date.now() - 2000);

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("quiz_sessions")
      .doc("sess_1")
      .set({
        category: "picture_atlas",
        difficulty: "all",
        mode: "picture",
        score: 240,
        correctCount: 8,
        totalQuestions: 10,
        bestStreak: 5,
        durationSeconds: 92,
        playedOffline: true,
        createdAt: ts,
      })
  );

  // List subcollection succeeds for owner (including when empty or populated)
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("quiz_sessions")
      .orderBy("createdAt", "desc")
      .limit(20)
      .get()
  );

  // Update is forbidden on immutable session logs
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("quiz_sessions")
      .doc("sess_1")
      .update({ score: 9999 })
  );

  // Guess mode session should also succeed
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("quiz_sessions")
      .doc("sess_guess_1")
      .set({
        category: "science",
        difficulty: "medium",
        mode: "guess",
        score: 360,
        correctCount: 3,
        totalQuestions: 5,
        bestStreak: 2,
        durationSeconds: 45,
        playedOffline: false,
        createdAt: ts,
      })
  );

  // Reject impossible correctCount > totalQuestions
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("quiz_sessions")
      .doc("sess_2")
      .set({
        category: "science",
        difficulty: "hard",
        mode: "standard",
        score: 300,
        correctCount: 12,
        totalQuestions: 10,
        bestStreak: 10,
        durationSeconds: 50,
        playedOffline: false,
        createdAt: ts,
      })
  );
});

test("Leaderboard: owner writes, filtered global query succeeds, cross-user writes fail", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const ts = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb.collection("leaderboard").doc(ALICE_UID).set(buildValidLeaderboard(ALICE_UID, ts))
  );
  await assertSucceeds(
    bobDb.collection("leaderboard").doc(BOB_UID).set({
      ...buildValidLeaderboard(BOB_UID, ts),
      displayName: "Bob Quizzer",
      totalScore: 890,
      highScore: 300,
    })
  );

  // Exact Kotlin query: whereGreaterThanOrEqualTo("totalScore", 0L).orderBy("totalScore", DESCENDING).limit(50)
  await assertSucceeds(
    aliceDb
      .collection("leaderboard")
      .where("totalScore", ">=", 0)
      .orderBy("totalScore", "desc")
      .limit(50)
      .get()
  );

  // Unfiltered query without totalScore constraint must fail
  await assertFails(aliceDb.collection("leaderboard").get());

  // Alice cannot update or delete Bob's leaderboard entry
  await assertFails(
    aliceDb.collection("leaderboard").doc(BOB_UID).update({
      totalScore: 0,
      updatedAt: new Date(Date.now() - 1000),
    })
  );
  await assertFails(aliceDb.collection("leaderboard").doc(BOB_UID).delete());

  // Reject invalid accuracy > 100
  await assertFails(
    aliceDb.collection("leaderboard").doc(ALICE_UID).update({
      accuracyPercent: 150,
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

function buildSampleQuestions() {
  return [
    {
      id: "q_1",
      category: "science",
      difficulty: "medium",
      questionText: "What is the chemical symbol for gold?",
      correctAnswer: "Au",
      allOptions: ["Au", "Ag", "Gd", "Go"],
      pictureQuestion: false,
      imageUrl: "",
      imageDrawableName: "",
      explanation: "Au comes from the Latin word aurum.",
    },
  ];
}

function buildValidBattleRoom(roomCode, hostUid, ts) {
  return {
    roomCode,
    hostUserId: hostUid,
    hostDisplayName: "Atlas Explorer",
    hostAvatarId: "compass",
    topics: ["science", "history"],
    difficulty: "medium",
    mode: "standard",
    questionCount: 5,
    languageCode: "en",
    status: "waiting",
    memberUids: [hostUid],
    questions: buildSampleQuestions(),
    createdAt: ts,
    updatedAt: ts,
  };
}

function buildValidParticipant(uid, name, ts) {
  return {
    userId: uid,
    displayName: name,
    handle: `handle_${uid.slice(0, 6)}`,
    avatarId: "owl",
    score: 0,
    correctCount: 0,
    answeredCount: 0,
    currentStreak: 0,
    bestStreak: 0,
    durationSeconds: 0,
    finished: false,
    createdAt: ts,
    updatedAt: ts,
  };
}

test("BattleRooms & Participants: host create, friend atomic join, start, score update, finish, and security bounds", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const charlieDb = testEnv.authenticatedContext("charlie_789").firestore();
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const roomCode = "ATLAS7";
  const ts = new Date(Date.now() - 10000);

  // Unauthenticated cannot read or create room
  await assertFails(unauthDb.collection("battle_rooms").doc(roomCode).get());
  await assertFails(
    unauthDb.collection("battle_rooms").doc(roomCode).set(buildValidBattleRoom(roomCode, ALICE_UID, ts))
  );

  // Alice creates room + her own participant doc in an atomic batch
  const createBatch = aliceDb.batch();
  createBatch.set(aliceDb.collection("battle_rooms").doc(roomCode), buildValidBattleRoom(roomCode, ALICE_UID, ts));
  createBatch.set(
    aliceDb.collection("battle_rooms").doc(roomCode).collection("participants").doc(ALICE_UID),
    buildValidParticipant(ALICE_UID, "Alice Host", ts)
  );
  await assertSucceeds(createBatch.commit());

  // List query scoped to memberUids succeeds; unfiltered list fails
  await assertSucceeds(
    aliceDb.collection("battle_rooms").where("memberUids", "array-contains", ALICE_UID).get()
  );
  await assertFails(aliceDb.collection("battle_rooms").get());

  // Non-member Charlie cannot read Alice's participant doc
  await assertFails(
    charlieDb.collection("battle_rooms").doc(roomCode).collection("participants").doc(ALICE_UID).get()
  );
  // Collection list on participants is always denied
  await assertFails(
    aliceDb.collection("battle_rooms").doc(roomCode).collection("participants").get()
  );

  // Bob joins waiting room atomically with his participant doc
  const joinTs = new Date(Date.now() - 5000);
  const joinBatch = bobDb.batch();
  joinBatch.update(bobDb.collection("battle_rooms").doc(roomCode), {
    memberUids: [ALICE_UID, BOB_UID],
    updatedAt: joinTs,
  });
  joinBatch.set(
    bobDb.collection("battle_rooms").doc(roomCode).collection("participants").doc(BOB_UID),
    buildValidParticipant(BOB_UID, "Bob Challenger", joinTs)
  );
  await assertSucceeds(joinBatch.commit());

  // Now Bob (a member) can read Alice's participant doc
  await assertSucceeds(
    bobDb.collection("battle_rooms").doc(roomCode).collection("participants").doc(ALICE_UID).get()
  );

  // Bob cannot start the battle (only host Alice can) or tamper with questions
  await assertFails(
    bobDb.collection("battle_rooms").doc(roomCode).update({
      status: "active",
      updatedAt: new Date(Date.now() - 2000),
    })
  );
  await assertFails(
    aliceDb.collection("battle_rooms").doc(roomCode).update({
      questions: [],
      updatedAt: new Date(Date.now() - 2000),
    })
  );

  // Host Alice starts the battle
  await assertSucceeds(
    aliceDb.collection("battle_rooms").doc(roomCode).update({
      status: "active",
      updatedAt: new Date(Date.now() - 2000),
    })
  );

  // New joiners cannot join once battle is active
  await assertFails(
    charlieDb.collection("battle_rooms").doc(roomCode).update({
      memberUids: [ALICE_UID, BOB_UID, "charlie_789"],
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Wrong answers can yield negative Battle XP within [-50000, 50000], while < -50000 is rejected
  await assertSucceeds(
    aliceDb.collection("battle_rooms").doc(roomCode).collection("participants").doc(ALICE_UID).update({
      score: -50,
      correctCount: 0,
      answeredCount: 1,
      currentStreak: 0,
      bestStreak: 0,
      durationSeconds: 9,
      finished: false,
      updatedAt: new Date(Date.now() - 1000),
    })
  );
  await assertFails(
    aliceDb.collection("battle_rooms").doc(roomCode).collection("participants").doc(ALICE_UID).update({
      score: -60000,
      updatedAt: new Date(Date.now() - 900),
    })
  );

  // Bob updates his own score and finishes; Alice cannot update Bob's participant doc
  await assertFails(
    aliceDb.collection("battle_rooms").doc(roomCode).collection("participants").doc(BOB_UID).update({
      score: 0,
      updatedAt: new Date(Date.now() - 1000),
    })
  );
  await assertSucceeds(
    bobDb.collection("battle_rooms").doc(roomCode).collection("participants").doc(BOB_UID).update({
      score: 120,
      correctCount: 1,
      answeredCount: 1,
      currentStreak: 1,
      bestStreak: 1,
      durationSeconds: 14,
      finished: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Once Bob's participant doc is finished, further edits are locked
  await assertFails(
    bobDb.collection("battle_rooms").doc(roomCode).collection("participants").doc(BOB_UID).update({
      score: 9999,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Mark room finished -> terminal state lock prevents reopening
  await assertSucceeds(
    aliceDb.collection("battle_rooms").doc(roomCode).update({
      status: "finished",
      updatedAt: new Date(Date.now() - 500),
    })
  );
  await assertFails(
    aliceDb.collection("battle_rooms").doc(roomCode).update({
      status: "active",
      updatedAt: new Date(Date.now() - 200),
    })
  );
});

test("BattleInvites: host creates invite, invitee accepts/queries, third party denied", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const charlieDb = testEnv.authenticatedContext("charlie_789").firestore();
  const roomCode = "ROOM99";
  const inviteId = `${roomCode}_${BOB_UID}`;
  const ts = new Date(Date.now() - 8000);

  await assertSucceeds(
    aliceDb.collection("battle_rooms").doc(roomCode).set(buildValidBattleRoom(roomCode, ALICE_UID, ts))
  );

  const invitePayload = {
    roomId: roomCode,
    roomCode,
    hostUserId: ALICE_UID,
    hostDisplayName: "Alice Host",
    hostAvatarId: "compass",
    inviteeUserId: BOB_UID,
    inviteeDisplayName: "Bob Challenger",
    topics: ["science", "history"],
    difficulty: "medium",
    mode: "standard",
    questionCount: 5,
    status: "pending",
    createdAt: ts,
    updatedAt: ts,
  };

  // Bob cannot forge an invite from Alice
  await assertFails(bobDb.collection("battle_invites").doc(inviteId).set(invitePayload));

  // Alice creates direct invite to Bob
  await assertSucceeds(aliceDb.collection("battle_invites").doc(inviteId).set(invitePayload));

  // Bob's scoped query succeeds; unfiltered list fails; Charlie cannot read Bob's invite
  await assertSucceeds(
    bobDb.collection("battle_invites").where("inviteeUserId", "==", BOB_UID).get()
  );
  await assertFails(bobDb.collection("battle_invites").get());
  await assertFails(charlieDb.collection("battle_invites").doc(inviteId).get());

  // Invitee Bob accepts the invite; ghost fields fail; re-updating after accept fails
  await assertFails(
    bobDb.collection("battle_invites").doc(inviteId).update({
      status: "accepted",
      hacked: true,
      updatedAt: new Date(Date.now() - 2000),
    })
  );
  await assertSucceeds(
    bobDb.collection("battle_invites").doc(inviteId).update({
      status: "accepted",
      updatedAt: new Date(Date.now() - 1000),
    })
  );
  await assertFails(
    bobDb.collection("battle_invites").doc(inviteId).update({
      status: "declined",
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

function buildValidWeeklyEntry(uid, cycleId, ts, weeklyXp = 420) {
  return {
    userId: uid,
    cycleId,
    displayName: "Atlas Explorer",
    handle: "explorer_1",
    avatarId: "compass",
    weeklyXp,
    highScore: 180,
    gamesPlayed: 3,
    correctCount: 24,
    answeredCount: 30,
    accuracyPercent: 80,
    bestStreak: 7,
    lifetimeXp: 14200,
    level: 2,
    createdAt: ts,
    updatedAt: ts,
  };
}

test("WeeklyRankings: Sunday 12:00 GMT cycle isolation, query, ownership, and negative validation", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const cycleId = "2026-11-15_1200Z";
  const nextCycleId = "2026-11-22_1200Z";
  const ts = new Date(Date.now() - 5000);

  // Unauthenticated reads/writes fail
  await assertFails(
    unauthDb.collection("weekly_rankings").doc(cycleId).collection("entries").where("weeklyXp", ">=", 0).get()
  );

  // Invalid cycleId format fails
  await assertFails(
    aliceDb.collection("weekly_rankings").doc("invalid-cycle").collection("entries").doc(ALICE_UID)
      .set(buildValidWeeklyEntry(ALICE_UID, "invalid-cycle", ts))
  );

  // Mismatched cycleId field fails
  await assertFails(
    aliceDb.collection("weekly_rankings").doc(cycleId).collection("entries").doc(ALICE_UID)
      .set(buildValidWeeklyEntry(ALICE_UID, nextCycleId, ts))
  );

  // Alice and Bob write valid weekly ranking entries in current cycle
  await assertSucceeds(
    aliceDb.collection("weekly_rankings").doc(cycleId).collection("entries").doc(ALICE_UID)
      .set(buildValidWeeklyEntry(ALICE_UID, cycleId, ts, 420))
  );
  await assertSucceeds(
    bobDb.collection("weekly_rankings").doc(cycleId).collection("entries").doc(BOB_UID)
      .set(buildValidWeeklyEntry(BOB_UID, cycleId, ts, 960))
  );

  // Exact Kotlin query: whereGreaterThanOrEqualTo("weeklyXp", 0L).orderBy("weeklyXp", DESCENDING).limit(50)
  await assertSucceeds(
    aliceDb.collection("weekly_rankings").doc(cycleId).collection("entries")
      .where("weeklyXp", ">=", 0)
      .orderBy("weeklyXp", "desc")
      .limit(50)
      .get()
  );

  // Unfiltered query without weeklyXp constraint fails
  await assertFails(
    aliceDb.collection("weekly_rankings").doc(cycleId).collection("entries").get()
  );

  // Next cycle starts empty until players score after reset
  const nextCycleSnap = await assertSucceeds(
    aliceDb.collection("weekly_rankings").doc(nextCycleId).collection("entries")
      .where("weeklyXp", ">=", 0)
      .orderBy("weeklyXp", "desc")
      .limit(50)
      .get()
  );
  if (!nextCycleSnap.empty) {
    throw new Error("Expected new weekly cycle to start empty");
  }

  // Owner updates own weekly score + lifetime level cleanly
  await assertSucceeds(
    aliceDb.collection("weekly_rankings").doc(cycleId).collection("entries").doc(ALICE_UID).update({
      weeklyXp: 1050,
      lifetimeXp: 31500,
      level: 3,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Alice cannot tamper with Bob's weekly entry
  await assertFails(
    aliceDb.collection("weekly_rankings").doc(cycleId).collection("entries").doc(BOB_UID).update({
      weeklyXp: 0,
      updatedAt: new Date(Date.now() - 500),
    })
  );
  await assertFails(
    aliceDb.collection("weekly_rankings").doc(cycleId).collection("entries").doc(BOB_UID).delete()
  );
});

test("UserProfile: daily streak & max-3 streak freeze validation", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 10000);

  // Legacy doc without streak fields succeeds, then can update streak fields up to max 3 freezes
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(buildValidProfile(ALICE_UID, created))
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      dailyStreak: 5,
      bestDailyStreak: 7,
      streakFreezes: 3,
      lastActiveDay: "2026-11-22",
      lastFreezeUsedDay: "2026-11-20",
      dailyXpEarned: 10250,
      dailyXpDate: "2026-11-22",
      dailyXpFreezeGrantedDate: "2026-11-22",
      objectiveDayKey: "2026-11-22",
      completedObjectiveIds: ["geo_10_10", "hard_mix_70", "wiki_70"],
      dailyRoundsCompleted: 4,
      dailyObjectiveStreak: 3,
      lastObjectiveCompletedDay: "2026-11-22",
      objectiveFreezeGrantedDate: "2026-11-22",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject > 3 completedObjectiveIds
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      completedObjectiveIds: ["a", "b", "c", "d"],
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject negative dailyObjectiveStreak
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      dailyObjectiveStreak: -1,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject streakFreezes > 3
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      streakFreezes: 4,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject negative dailyStreak
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      dailyStreak: -1,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject negative dailyXpEarned
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      dailyXpEarned: -10,
      updatedAt: new Date(Date.now() - 500),
    })
  );
});


