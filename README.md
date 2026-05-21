# Smart Expense Tracker

Smart Expense Tracker is a Kotlin Android app using MVVM, Room, and Jetpack Compose to add, edit, search, and filter expenses by category, track monthly budgets, view analytics charts, and export reports to PDF/CSV with offline storage and a modern UI.

## Features
- Add, edit, delete expenses
- Categories: Food, Travel, Bills, Shopping, Health
- Search and filter by category
- Monthly budget alerts
- Analytics with pie charts
- Export to PDF and CSV
- Dark mode support

## Tech Stack
- Kotlin, Coroutines, Flow
- Jetpack Compose, Material 3
- Room Database
- Navigation Component
- DataStore Preferences

## Setup
1. Open the project in Android Studio.
2. Sync Gradle.
3. Run the app on an emulator or device.

## Notes
- Exports are saved to the Downloads folder.
- If you target Android 9 or below, storage permission may be requested for export.
