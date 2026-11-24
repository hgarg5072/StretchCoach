# Community Routine Sharing & Creator Following

Add a dedicated **Community** hub in Stretch Coach allowing users to publish custom stretching routines, discover and follow mobility routines shared by friends and creators, and clone shared routines into their own personal library for immediate customization and offline use.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following directions were confirmed during initial planning and guide this implementation:

- **Confirmed Decision 1: Discovery & Feed**: A public community feed featuring trending, recent, and followed creator routines with quick search and filter by focus areas (Neck, Back, Hips, Full Body).
- **Confirmed Decision 2: Routine Cloning**: 1-click clone copies any shared routine into the user's local Room database and cloud backup as an independent custom routine, preserving all stretches and timings so it can be edited freely.
- **Confirmed Decision 3: Navigation Placement**: New **Community** tab in the main bottom navigation bar alongside *Routines* and *Timer*.
- **Confirmed Decision 4: Authentication & Author Profile**: Publishing and following require Google Sign-In; unauthenticated users can browse public routines and are prompted with a friendly bottom sheet to sign in when publishing, following, or cloning.

---

## 1. Overview & Core Concept

- **What It Does**: Users can share their mobility routines with family, friends, and the broader Stretch Coach community with a single tap. Other users can view routines, follow creators they like, and clone any routine directly into their own schedule as an editable routine.
- **Target Audience / Persona**: Individuals doing desk stretches, athletes, yoga enthusiasts, and friends or physical therapists sharing mobility workouts with clients and peers.
- **Key Value**: Eliminates the manual work of creating multi-stretch routines from scratch by allowing peer collaboration and easy sharing, while keeping all user data safe in both local Room storage and cloud Firestore.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Browsing the Community**:
   - User taps the **Community** tab in the bottom navigation bar.
   - Top search and category chips: "All", "Followed", "Desk Relief", "Back & Spine", "Lower Body", "Morning".
   - Cards display routine title, creator name/avatar badge, stretch count, total duration (e.g. `8 min • 6 stretches`), and copy count.
2. **Publishing a Routine**:
   - In the **Routines** tab or Routine detail screen, each custom routine has a "Share with Community" button.
   - A bottom sheet allows reviewing the routine preview, adding a brief description, and selecting "Public Share".
   - When published, it is stored in Firestore's `/public_routines` collection with the author's public display name.
3. **Following Creators**:
   - Tapping on a creator in any routine card shows their public profile (display name, routine count) with a "Follow" / "Following" button.
   - Followed creator routines filter into the "Followed" tab feed.
4. **Cloning a Routine**:
   - On any community routine, tap **"Clone & Use"**.
   - The app instantly creates an independent copy in the user's local Room database (`StretchDao`), notifies them with a snackbar ("Added to My Routines"), and triggers cloud sync for their personal backup.
   - The user can immediately play the timer or edit the stretch intervals.

### Visual Identity & Theme

- **Aesthetic Direction**: Active wellness aesthetic with clean Teal/Slate accents, high-contrast readable typography, and soft elevated cards.
- **Color Tokens**:
  - Primary: `TealPrimaryLight` (`#0D9488`) / `TealPrimaryDark` (`#2DD4BF`)
  - Accent / Hold: `CoralHold` (`#E11D48`)
  - Accent / Prep: `CyanPrep` (`#0284C7`)
  - Community Accent: `IndigoSecondaryLight` (`#4F46E5`) for creator badges and following states.
- **Component Styling**:
  - Community routine cards with rounded corners (16.dp), author avatar circle, duration pill, and prominent "Clone & Use" action button.
  - Category selector chips with M3 `FilterChip`.
  - Empty state when no routines match a query or following feed is empty with friendly action suggestions.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Public Feed Collection Structure**:
  - *Chosen Approach*: Dedicated top-level `/public_routines/{routineId}` collection with fields: `authorId`, `authorName`, `name`, `description`, `stretches`, `cloneCount`, `createdAt`, `updatedAt`.
  - *Why*: Allows secure, fast global reads without leaking private user routine collections, while security rules enforce that only the author can edit or delete their public routine.
  - *Alternatives Considered*: Reading directly from `/users/{userId}/custom_routines` with a boolean `isPublic`. Rejected because Firestore security rules and list queries perform much better and more securely with separated public and private collections.
- **Decision 2: User Following Representation**:
  - *Chosen Approach*: User's followed creators stored at `/users/{userId}/following/{creatorId}`.
  - *Why*: Fast client-side lookup for following status and isolation under the authenticated user's private collection.
- **Decision 3: Independent Clone Semantics**:
  - *Chosen Approach*: Cloning duplicates stretches into a brand-new local Room routine entity (`isCustom = true`).
  - *Why*: Gives users complete freedom to adjust hold durations, toggle voice cues, and reorder stretches without mutating the original creator's routine or depending on a continuous internet connection.

---

## 4. Technical Architecture & Data Strategy

```
┌──────────────────────────────────────────────────────────────┐
│                       Stretch Coach UI                       │
├──────────────────┬──────────────────────┬────────────────────┤
│   Routines Tab   │      Timer Tab       │   Community Tab    │
│  (Custom + Seed) │ (Active Audio/Timer) │  (Feed & Creators) │
└────────┬─────────┴──────────┬───────────┴─────────┬──────────┘
         │                    │                     │
         ▼                    ▼                     ▼
┌──────────────────────────────────────────────────────────────┐
│                       StretchViewModel                       │
│  - allRoutines Flow       - playerUiState Flow               │
│  - communityFeed Flow     - followedUsers Flow               │
│  - cloneCommunityRoutine  - publishRoutine / followCreator   │
└────────┬──────────────────────────────────────────┬──────────┘
         │                                          │
         ▼                                          ▼
┌───────────────────────────────────┐  ┌───────────────────────┐
│     StretchRepository & Room      │  │ Firestore & Auth      │
│  - StretchDao (SQLite)            │  │ - /users/{uid}/...    │
│  - RoutineEntity, StretchEntity   │  │ - /public_routines    │
│  - Dual-layer instant persistence │  │ - /users/{uid}/follows│
└───────────────────────────────────┘  └───────────────────────┘
```

### Data Models & Schemas

1. **Firestore Collections**:
   - `/public_routines/{routineId}`:
     - `routineId`: String
     - `authorId`: String (matches `request.auth.uid`)
     - `authorName`: String (e.g., "Hitesh G.")
     - `name`: String
     - `description`: String
     - `category`: String
     - `stretches`: List of stretch maps (`name`, `category`, `preparationSeconds`, `holdSeconds`, `prepCue`, `holdCue`)
     - `cloneCount`: Long
     - `createdAt`, `updatedAt`: Timestamp
   - `/users/{userId}/following/{creatorId}`:
     - `creatorId`: String
     - `creatorName`: String
     - `followedAt`: Timestamp

2. **Security Rules**:
   - Anyone authenticated can read `/public_routines`.
   - Creating/updating `/public_routines` requires `request.auth.uid == request.resource.data.authorId`.
   - Incrementing `cloneCount` allowed via atomic update.
   - `/users/{userId}/following` is strictly owner-accessible (`request.auth.uid == userId`).

3. **Android Components**:
   - `CommunityScreen.kt`: Filter chips, search bar, routine card feed, author details dialog, clone button.
   - `PublishRoutineDialog.kt`: Preview and confirmation dialog to share a local custom routine.
   - `FirestoreCommunityRepository.kt`: Feed querying, publishing, unpublishing, following, and clone counter updates.
