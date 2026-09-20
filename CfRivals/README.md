# CF Rivals Tracker 🏆

CF Rivals Tracker is a high-octane Android application designed for competitive programmers to track and compare their Codeforces statistics in real-time. Featuring a sleek dark/neon "e-sports" aesthetic, the app provides a focused environment for rivals to battle for rating dominance.

## ✨ Features

- **Real-time Stat Comparison**: Instant comparison of Codeforces ratings and problem count between you and your rival.
- **Battle Log**: Identify specific problems that your rival has solved but you haven't yet, giving you a clear roadmap to catch up.
- **Dominance Bar**: A dynamic visual representation of who is currently leading the battle based on weighted rating and solved count metrics.
- **Dark/Neon Aesthetic**: A consistent, forced dark theme designed for long coding sessions and a competitive feel.
- **Robust Networking**: Handles Codeforces API calls with coroutines and Retrofit, including comprehensive error handling for timeouts and invalid handles.

## 🛠 Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel) with ViewBinding
- **Networking**: Retrofit 2 & Coroutines
- **Image Loading**: Coil 3
- **UI Components**: Material 3 Design
- **Local Storage**: SharedPreferences

## 🚀 Getting Started

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/Prabal-Verma/CF-RIVALS.git
    ```
2.  **Open in Android Studio**:
    Import the project and let Gradle sync complete.
3.  **Configure Handles**:
    Navigate to the **Settings** tab and enter your Codeforces handle and your rival's handle.
4.  **Track and Dominate**:
    Head to the **Home** tab to see the live comparison and the **Battle Log** to see missing problems.

## 📸 Screen Shots

*(Add screenshots of your dark/neon UI here to showcase the aesthetic)*

## 🐛 Bug Fixes & Stability

- Resolved critical `NullPointerException` in `HomeFragment` by implementing lifecycle-aware null checks for ViewBinding.
- Enhanced networking resilience to handle `codeforces.com` connection timeouts gracefully.

---
Built with 💻 and ☕ for the Competitive Programming community.
