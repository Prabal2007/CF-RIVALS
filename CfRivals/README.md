# 🏆 CF Rivals

<p align="center">
  <strong>COMPARE • COMPETE • IMPROVE</strong>
</p>

<p align="center">
  A Codeforces rivalry dashboard that turns competitive-programming statistics into a personalized catch-up challenge.
</p>

<p align="center">
  <a href="https://github.com/Prabal2007/CF-RIVALS">
    <img src="https://img.shields.io/badge/GitHub-CF%20Rivals-181717?style=for-the-badge&logo=github" alt="GitHub">
  </a>
  <img src="https://img.shields.io/badge/Version-1.0.0-4F8CFF?style=for-the-badge" alt="Version 1.0.0">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android" alt="Android">
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin" alt="Kotlin">
  <img src="https://img.shields.io/badge/Codeforces-API-FF5C6C?style=for-the-badge&logo=codeforces" alt="Codeforces API">
</p>

<p align="center">
  <a href="#-the-idea">Idea</a> •
  <a href="#-features">Features</a> •
  <a href="#-how-the-battle-works">Battle</a> •
  <a href="#-architecture">Architecture</a> •
  <a href="#-testing">Testing</a> •
  <a href="#-roadmap">Roadmap</a>
</p>

---

## ⚡ The Idea

Competitive programming becomes more motivating when you have someone to compete with.

**CF Rivals** turns a Codeforces rivalry into a measurable and actionable dashboard.

Enter your Codeforces handle and your rival's handle, then compare:

**Rating → Solved Problems → Overall Comparison → Problems to Catch Up**

> 🎯 **Don't just see where your rival is. See what you need to do next.**

---

## 🚀 At a Glance

| 🏠 HOME | ⚔️ BATTLE LOG | ⚙️ SETTINGS |
|:---:|:---:|:---:|
| Compare performance | Find catch-up problems | Manage handles |
| 📊 Rating | 🎯 Problems to catch up | 👤 Handle validation |
| 🧩 Solved problems | 🔥 Rival progress | 💾 Local preferences |
| 📈 Dominance | 🔄 Manual refresh | 🧹 Clear handles |

---

## ✨ Features

<details>
<summary><strong>📊 Rival Comparison</strong></summary>

<br>

The Home screen compares you and your rival using:

- Codeforces rating
- Unique solved problems
- Rating difference
- Solved-problem difference
- Overall comparison
- Dominance visualization

The comparison is calculated locally from the data retrieved from Codeforces.

</details>

<details>
<summary><strong>⚔️ Battle Log</strong></summary>

<br>

The Battle Log finds problems that:

- Your rival has successfully solved
- You have not successfully solved
- Have an accepted submission from your rival
- Have not already been counted as solved by you
- Are shown only once even when duplicate submissions exist

This converts your rival's solved problems into a personalized catch-up list.

</details>

<details>
<summary><strong>👤 Smart Handle Management</strong></summary>

<br>

Settings validates both Codeforces handles before saving them.

The application prevents:

- Empty handles
- Invalid Codeforces handles
- The same handle being used for both players

Handles are stored locally using `SharedPreferences`.

</details>

<details>
<summary><strong>🧠 Duplicate-Safe Problem Tracking</strong></summary>

<br>

Codeforces users can submit the same problem multiple times.

CF Rivals identifies a problem using its contest ID and problem index and counts a successfully solved problem only once.

</details>

<details>
<summary><strong>🔄 Session Caching</strong></summary>

<br>

CF Rivals uses an in-memory `CFSessionCache` to avoid unnecessarily requesting the same Codeforces data every time the user navigates between screens during the current app session.

Cached data includes:

- Your Codeforces user information
- Rival Codeforces user information
- Your submissions
- Rival submissions
- The handles associated with the cached data

Changing or clearing handles invalidates the session cache.

> ℹ️ This is a session cache, not a persistent database. It is cleared when the application process is recreated.

</details>

<details>
<summary><strong>🌐 Codeforces Integration</strong></summary>

<br>

The application communicates with the Codeforces API through Retrofit to retrieve:

- User information
- Ratings
- Submission history
- Solved-problem information

</details>

<details>
<summary><strong>🛡️ Error & Offline Handling</strong></summary>

<br>

The application handles common failure states such as:

- No internet connection
- DNS/network failures
- API request failures
- Invalid handles
- Empty states
- Loading states
- Retry states
- Fragment lifecycle changes

The application does not provide persistent offline Codeforces data in v1.0.

</details>

<details>
<summary><strong>🌙 Light & Dark Themes</strong></summary>

<br>

The UI supports both light and dark themes while maintaining the CF Rivals visual identity.

The visual language uses:

- 🔵 Blue for primary actions and identity
- 🟢 Green for your progress
- 🔴 Red for rival-related information
- 🌙 Dark surfaces for the competitive interface

</details>

---

## ⚔️ How the Battle Works

Suppose you have solved:

**YOU**

`A` · `B` · `C`

And your rival has solved:

**RIVAL**

`B` · `C` · `D` · `E`

CF Rivals determines the set difference between the rival's uniquely solved problems and your uniquely solved problems.

### 🎯 Result

Your catch-up list becomes:

`D` · `E`

> 🔥 **Your rival solved it. You haven't. Now it's your move.**

---

## 📈 Comparison Model

CF Rivals compares multiple dimensions instead of relying on a single number.

### Rating

The application compares your Codeforces rating with your rival's rating.

### Solved Problems

Only uniquely solved problems are counted. Multiple accepted submissions for the same problem do not inflate the count.

### Overall Comparison

The application combines rating and solved-problem performance into the application's overall comparison calculation.

### Dominance

The Home screen converts the comparison result into a percentage-style visualization so the relative result can be understood quickly.

---

## 🧠 Core Business Logic

The important calculations are separated from the Android UI so they can be tested independently.

### `SolvedProblemCalculator`

Responsible for:

- Filtering accepted submissions
- Identifying unique problems
- Preventing duplicate counting
- Returning unique solved-problem counts

### `RivalComparisonCalculator`

Responsible for:

- Rating difference
- Solved-problem difference
- Rating comparison
- Solved-problem comparison
- Overall comparison
- Dominance percentage

### `BattleLogCalculator`

Responsible for:

- Finding problems solved by the rival
- Removing problems already solved by the user
- Ignoring unsuccessful submissions
- Removing duplicate rival submissions

---

## 🏗️ Architecture

CF Rivals follows an MVVM-oriented architecture with separate business-logic components.

### Architecture Flow

    Codeforces API
          ↓
       Retrofit
          ↓
      ViewModel
          ↓
    CFSessionCache
          ↓
    Business Logic
          ↓
      Android UI

The business-logic layer contains:

- `SolvedProblemCalculator`
- `RivalComparisonCalculator`
- `BattleLogCalculator`

### Main Responsibilities

| Layer | Responsibility |
|---|---|
| **API** | Communicate with Codeforces |
| **Retrofit** | HTTP/API abstraction |
| **ViewModel** | Manage screen state and data loading |
| **CFSessionCache** | Reuse fetched data during the app session |
| **Business Logic** | Perform comparison and problem calculations |
| **Fragments** | Display application state |
| **SharedPreferences** | Store Codeforces handles locally |

---

## 🔄 Data Flow

    User
      ↓
    Settings
      ↓
    Codeforces Handles
      ↓
    Codeforces API
      ↓
    Retrofit
      ↓
    ViewModel
      ↓
    CFSessionCache
      ↓
    Business Logic
      ↓
    Home / Battle Log

### Normal Navigation

When the required data already exists in `CFSessionCache`, the application can reuse it instead of immediately requesting the same data again.

### Manual Refresh

When the user explicitly refreshes the Battle Log, fresh submission data is requested and the cache is updated.

### Handle Changes

When handles are changed or cleared, the session cache is invalidated so stale data is not reused for a different rivalry.

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| **Kotlin** | Primary programming language |
| **Android SDK** | Mobile application platform |
| **Material UI** | UI components and theming |
| **View Binding** | Type-safe UI access |
| **Fragments** | Screen architecture |
| **ViewModel** | Lifecycle-aware state management |
| **Retrofit 2** | Codeforces API communication |
| **Kotlin Coroutines** | Asynchronous operations |
| **Coil 3** | Profile image loading |
| **SharedPreferences** | Local handle storage |
| **JUnit** | Unit testing |
| **Gradle** | Build automation |

---

## 🧪 Testing

The project contains automated unit tests for the core business logic.

The test suite covers:

- ✅ Unique solved-problem calculation
- ✅ Duplicate accepted submissions
- ✅ Rejected submission filtering
- ✅ Rating comparison
- ✅ Solved-problem comparison
- ✅ Equal comparison cases
- ✅ Overall comparison
- ✅ Battle Log set difference
- ✅ Duplicate rival submissions
- ✅ Empty submission lists
- ✅ Edge cases

### Run Tests

    ./gradlew test

### Build Debug APK

    ./gradlew assembleDebug

### Check for Whitespace Errors

    git diff --check

---

## 🌐 API & Data Loading

CF Rivals currently retrieves Codeforces data through the Codeforces API.

### v1.0 Data Architecture

The v1.0 implementation uses:

- **Retrofit** for API communication
- **Kotlin Coroutines** for asynchronous requests
- **ViewModels** for lifecycle-aware data loading
- **SharedPreferences** for Codeforces handle storage
- **CFSessionCache** for in-memory session caching
- **Dedicated calculators** for business logic

The application does **not** use Room or a persistent database for Codeforces submission data in v1.0.

### What the Cache Solves

Without caching, navigating between screens could repeatedly request the same Codeforces data.

The session cache allows already-fetched user and submission data to be reused while the application process remains alive.

### What the Cache Does Not Solve

Because `CFSessionCache` is in memory:

- It is not persistent storage.
- It does not survive process recreation.
- It does not provide long-term offline Codeforces data.
- It is not a replacement for Room or a repository/data layer.

Those concerns are part of the planned v2.0 architecture.

---

## 🛡️ Stability

The v1.0 implementation was manually tested across the main application flows.

### Verified Areas

- Settings handle validation
- Invalid handle handling
- Same-handle validation
- Handle clearing
- Home comparison
- Battle Log loading
- Battle Log catch-up calculation
- Manual refresh
- Retry after failure
- Network-unavailable behavior
- Screen navigation
- Light theme
- Dark theme
- Cache invalidation after handle changes
- Empty states
- Loading states

The application also uses lifecycle-aware handling for asynchronous work to reduce unnecessary crashes during Fragment lifecycle changes.

---

## 📱 Screenshots

Screenshots are intentionally not embedded yet because the repository currently does not contain finalized screenshot assets.

Once screenshots are added, the recommended gallery is:

| Home | Battle Log | Settings |
|:---:|:---:|:---:|
| Comparison dashboard | Catch-up problems | Handle management |

Recommended assets:

    docs/assets/home.png
    docs/assets/battle-log.png
    docs/assets/settings.png

---

## 🎬 Demo

A demo GIF or short screen recording can be added after the final release assets are prepared.

Recommended location:

    docs/assets/cf-rivals-demo.gif

Suggested demo flow:

**Launch → Settings → Enter Handles → Home → Comparison → Battle Log → Refresh**

---

## 🚀 Getting Started

### Requirements

- Android Studio
- Android SDK
- JDK compatible with the project's Gradle/Android configuration
- Internet connection for Codeforces API requests

### Clone the Repository

    git clone https://github.com/Prabal2007/CF-RIVALS.git

### Open the Project

Open the cloned project in Android Studio and allow Gradle synchronization to complete.

### Configure Codeforces Handles

1. Launch the application.
2. Open **Settings**.
3. Enter your Codeforces handle.
4. Enter your rival's Codeforces handle.
5. Save the handles.
6. Open **Home**.
7. View the comparison.
8. Open **Battle Log** to find problems to catch up on.

---

## 📂 Project Structure

    CF-RIVALS/
    │
    ├── CfRivals/
    │   └── app/
    │       └── src/
    │           ├── main/
    │           │   ├── java/com/example/cfrivals/
    │           │   │   ├── Adapter/
    │           │   │   ├── Api/
    │           │   │   ├── Fragments/
    │           │   │   └── Models/
    │           │   │
    │           │   └── res/
    │           │
    │           └── test/
    │               └── java/
    │
    └── README.md

---

## 🗺️ Roadmap

### 🏆 v1.0.0 — Stable MVP

- [x] Project foundation
- [x] Codeforces API integration
- [x] Handle management
- [x] Handle validation
- [x] Rival comparison
- [x] Unique solved-problem calculation
- [x] Battle Log
- [x] Error handling
- [x] Offline failure handling
- [x] Manual refresh
- [x] Light/Dark themes
- [x] UI polish
- [x] Core business-logic tests
- [x] Session-based API caching
- [x] v1.0.0 release preparation

### 🔮 v2.0 — Next Major Milestone

- [ ] Real authentication
- [ ] Google Sign-In
- [ ] Firestore integration
- [ ] Room database
- [ ] Repository-based data architecture
- [ ] Persistent caching
- [ ] Better synchronization
- [ ] Analytics
- [ ] Enhanced rivalry system
- [ ] More detailed statistics
- [ ] Improved competitive-programming insights

---

## 📦 Release

### 🏆 CF Rivals v1.0.0

**Version:** `1.0.0`

**Status:** Stable MVP

Version 1.0.0 represents the first stable milestone of CF Rivals, including rival comparison, Battle Log catch-up tracking, validation, error handling, UI polish, and automated business-logic tests.

The GitHub Release and APK will be attached after the final release commit and tag are created.

---

## ⚠️ Current Limitations

- Codeforces data requires an internet connection when it is not already available in the current session cache.
- Codeforces API availability can affect data loading.
- Profile images depend on the availability of the external Codeforces image server.
- v1.0 does not use Room for persistent Codeforces submission caching.
- The session cache does not survive application-process recreation.
- Advanced authentication is not included in v1.0.
- Cloud synchronization is not included in v1.0.
- Persistent offline Codeforces data is not yet available.

---

## 🔮 Why v2.0?

The v1.0 architecture establishes the core rivalry experience.

The next architectural step is moving from a lightweight API-driven application toward a more persistent data system.

The planned direction includes:

**Authentication**

→ **Cloud identity**

→ **Repository layer**

→ **Room database**

→ **Persistent caching**

→ **Synchronization**

→ **Richer rivalry analytics**

This will allow CF Rivals to evolve from a comparison dashboard into a more complete competitive-programming rivalry platform.

---

## ⭐ Support

If you find CF Rivals useful or interesting, consider giving the repository a ⭐ on GitHub.

Every rivalry needs a challenger.

---

## 👨‍💻 Author

**Prabal Verma**

Built with 💻 and ☕ for the Competitive Programming community.

---

<p align="center">
  <strong>🏆 COMPARE. COMPETE. IMPROVE.</strong>
</p>