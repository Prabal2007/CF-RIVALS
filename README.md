 # CF Rivals
​
 A minimal Android app to track your Codeforces progress against a rival. It's built for those who need a little extra motivation by seeing exactly what their friends (or enemies) are solving.
​
 ## Features
 - **Compare Solves:** Automatically filters and shows problems that your rival has solved but you haven't.
 - **Profile Integration:** Uses the Codeforces API to fetch real-time submission data.
 - **Clean UI:** Simple navigation between your home feed, battle logs, and settings.
 - **Direct Links:** Jump straight to the problems you're missing.

 ## Tech Stack
 - **Language:** Kotlin
 - **Networking:** Retrofit + Gson for Codeforces API integration.
 - **Async:** Kotlin Coroutines & Lifecycle scopes.
 - **UI:** ViewBinding, Material Components, and ScrollViews for responsive layouts.
 - **Images:** Coil for loading profile pictures.

 ## Video and Screenshots
 - **Screenshot Link:**
 - **Video Link:** https://drive.google.com/drive/folders/1_MhcAy6aW_XuI25DXJzvsKS-3PwfzbOV?usp=sharing
​
 ## How it works
 The app fetches the `user.status` for both handles and performs a set difference on the "OK" (solved) status. The resulting list is what shows up in your Battle Log.
​
 ---
 *Developed for competitive programmers who thrive on rivalry.*
