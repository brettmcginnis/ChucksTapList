# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Chuck's Tap List is a Kotlin Multiplatform Mobile (KMM) app that displays beer tap lists for multiple Chuck's Hop Shop locations in Seattle. The app fetches data from a REST API and displays it in a Jetpack Compose UI.

## Architecture

- **KMM Structure**: Shared business logic in `kmm/` module, Android-specific UI in `app/`
- **UI Framework**: Jetpack Compose with Material Design
- **Dependency Injection**: Koin for DI across platforms
- **Networking**: Ktor client for HTTP requests
- **State Management**: StateFlow with ViewModels following MVVM pattern
- **Stores**: Three Chuck's locations (Greenwood, Central District, Seward Park) with different menu endpoints

## Key Components

- `ChucksStore`: Enum defining the three store locations with API endpoints and calendar IDs
- `TapListViewModel`: Manages tap list state and API calls
- `FoodTruckViewModel`: Handles food truck event data from Google Calendar API
- `ChucksApi`: REST API client for fetching tap data from `https://taplists.web.app`
- `MainActivity`: Single activity with Compose UI and animated store selection

## Build Commands

```bash
# Build the project (excluding detekt - has style issues to fix)
./gradlew build -x detekt

# Build debug APK only
./gradlew assembleDebug

# Run Android app (requires connected device/emulator)
./gradlew installDebug

# Run unit tests
./gradlew test

# Run all tests (including iOS)
./gradlew allTests

# Run lint checks
./gradlew lint

# Run detekt static analysis (currently has some issues)
./gradlew detekt

# Auto-fix detekt style issues
./gradlew detekt --auto-correct

# Clean build artifacts
./gradlew clean
```

## Code Quality

The project uses:
- **Detekt**: Static analysis with auto-correction enabled
- **Android Lint**: Standard Android linting
- Configuration in `config/detekt/detekt.yml`
- Auto-correction enabled for detekt rules

## Development Notes

- API key for Google Calendar stored in environment variable `GoogleApiKey` or `local.properties` as `calendar_key`
- Signing configuration uses environment variables for release builds
- The app filters invalid tap entries (entries without letters, starting with "_" or "-", or missing prices)
- Chrome Custom Tabs integration for external links (Untappd, food truck URLs)
- Shake detection feature for enhanced user interaction