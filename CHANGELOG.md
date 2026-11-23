# Changelog

All notable changes to **Trivia Atlas** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- GitHub Actions CI workflow (`.github/workflows/android-ci.yml`) to run JVM/Robolectric unit tests and assemble debug APKs on pushes and pull requests.
- GitHub Actions automated Release workflow (`.github/workflows/release.yml`) to build `.apk` packages, extract changelog & commit notes, generate SHA-256 checksums, and attach `.apk` files directly to GitHub Releases.

## [1.0.0] - 2026-11-23

### Added
- **Explore & Daily Expedition**: 11+ trivia categories, Atlas Mix, Multiple Choice Quiz, Guess Mode (1.5x XP with hints), and Picture Atlas visual trivia.
- **Daily Wikipedia, Search & Custom URL Quiz**: Daily 5-article Wikipedia carousel, live article search, full distraction-free article reader, and custom web URL article extraction.
- **On-Device Gemini AI (BYOK) + Automatic Instant Fallback**: Strict on-device personal Gemini API key vault, dynamic model dropdown discovery from the user's key, and automatic fallback to the local Instant Article Quiz engine.
- **Friend Battle Mode**: Real-time Firestore multiplayer rooms (up to 12 players), 6-character room codes, direct invitations, 30s/question live standings, shared post-battle review, and Wikipedia Search / Custom URL battle creation.
- **Daily Objectives & Streak Freezes**: 30 rotating per-user Daily Objectives with tap-to-launch actions, daily check-in streaks, and Streak Freezes (max 3) earned via 10,000+ daily XP, 3 consecutive days completing all 3 objectives, or Explorer Level-ups.
- **Global & Weekly Ranks**: Lifetime Explorer Levels paired with Weekly XP leaderboards resetting Sundays at 12:00 PM GMT with H-1 countdown display.
- **Offline Library & Localization**: Room database offline question caching, offline sync queue, English/Spanish/Indonesian support, and Auto/Daylight/Night Material 3 themes.
- **Profile About Window**: Developer attribution (`brillian11`), GitHub repository link, API documentation, and dependency credits.
