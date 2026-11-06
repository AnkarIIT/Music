# Apple Music Design Specification (from v7.0.0 APK Analysis)

**Source**: `com.apple.android.music_7.0.0-1614_4arch_7dpi` (APKMirror)
**Package**: `com.apple.android.music`
**Date Analyzed**: 2026-11-05

---

## Color Palette

### Dark Theme (Primary)
| Token | Value | Usage |
|-------|-------|-------|
| `background` | `#262628` | Main app background (`apple_material_background_dark`) |
| `surface` | `#1C1C1E` | Cards, sheets (`secondary_background_color`) |
| `surface_variant` | `#2C2C2E` | Elevated surfaces (`tertiary_background_color`) |
| `surface_container` | `#1C1C1E` | Container backgrounds |
| `primary_accent` | `#F82C44` | Brand accent (from info.json) / `#FF3B30` (system_red) |
| `primary_blue` | `#0091FF` | Links, CTAs (`key_color_dark`, `system_blue`) |
| `on_background` | `#FFFFFF` | Primary text (`label_color_dark`) |
| `on_surface` | `#EBEBF5` | Primary text on surface (99% white) |
| `secondary_label` | `#99EBEBF5` | Secondary text (60% white) |
| `tertiary_label` | `#4DEBEBF5` | Tertiary text (30% white) |
| `quaternary_label` | `#29EBEBF5` | Disabled/placeholder (16% white) |
| `separator` | `#38383A` | Dividers, hairlines |
| `quaternary_fill` | `#2E767680` | Subtle fills (18% white with blue tint) |
| `tertiary_fill` | `#3D767680` | Slightly stronger fills |
| `primary_fill` | `#5C787880` | Stronger fills (36% white with blue tint) |
| `outline` | `#545458` | Outlines, strokes |
| `shadow` | `#000000` | Elevation shadows |

### Light Theme
| Token | Value | Usage |
|-------|-------|-------|
| `background` | `#F5F5F7` | Main background (`apple_material_background_light`) |
| `surface` | `#FFFFFF` | Cards |
| `surface_variant` | `#EFEFF4` | Elevated |
| `primary_accent` | `#FF3B30` | Brand red (`system_red`) |
| `primary_blue` | `#0088FF` | Links (`key_color_light`) |
| `on_background` | `#000000` | Primary text |
| `on_surface` | `#3C3C43` | Primary on surface |
| `secondary_label` | `#993C3C43` | Secondary (60% black) |
| `tertiary_label` | `#4D3C3C43` | Tertiary (30% black) |
| `separator` | `#EFEFEF` | Dividers |

### Semantic Aliases (for Compose ColorScheme mapping)
```
primary = primary_accent (#F82C44)
secondary = primary_blue (#0091FF dark / #0088FF light)
surface = surface
background = background
onPrimary = #FFFFFF
onSurface = on_surface
onBackground = on_background
outline = outline
surfaceContainer = surface_container
surfaceVariant = surface_variant
```

---

## Typography

**Font Family**: SF Pro / SF Pro Rounded (Apple proprietary — **cannot ship**)
**Substitute**: **Inter** (closest metric match) with `fontFeatureSettings: 'cv02, cv03, cv04, ss01'`

| Style | Size | Weight | Line Height | Letter Spacing | Use Case |
|-------|------|--------|-------------|----------------|----------|
| `displayLarge` | 57sp | 700 (Bold) | 64sp | -0.25sp | Hero titles |
| `displayMedium` | 45sp | 700 | 52sp | 0 | Large headlines |
| `displaySmall` | 36sp | 700 | 44sp | 0 | Section headers |
| `headlineLarge` | 32sp | 700 | 40sp | 0 | Screen titles |
| `headlineMedium` | 28sp | 700 | 36sp | 0 | Card titles |
| `headlineSmall` | 24sp | 700 | 32sp | 0 | Subsection headers |
| `titleLarge` | 22sp | 700 | 28sp | 0 | List item titles |
| `titleMedium` | 16sp | 600 (SemiBold) | 24sp | 0.15sp | Button text, emphasis |
| `titleSmall` | 14sp | 600 | 20sp | 0.1sp | Labels, chips |
| `bodyLarge` | 16sp | 400 (Regular) | 24sp | 0.5sp | Body text |
| `bodyMedium` | 14sp | 400 | 20sp | 0.25sp | Secondary body |
| `bodySmall` | 12sp | 400 | 16sp | 0.4sp | Captions, metadata |
| `labelLarge` | 14sp | 600 | 20sp | 0.1sp | Buttons, actions |
| `labelMedium` | 12sp | 600 | 16sp | 0.5sp | Small buttons |
| `labelSmall` | 11sp | 600 | 16sp | 0.5sp | Tiny labels |

**Key Apple Text Styles (mapped)**:
- `FootnoteEmphasized` → `bodySmall` + 600 weight (mini player artist)
- `Caption1Emphasized` → `labelSmall` + 600 weight (mini player shareplay)
- `Caption1` → `bodySmall` (mini player title)
- `Subhead` → `bodyMedium` (widgets)

---

## Spacing & Layout Grid

**Base Unit**: 4dp
**Content Padding**: 16dp (mobile), 24dp (tablet)
**Component Spacing**: 8dp (tight), 16dp (normal), 24dp (loose)

| Token | Value | Usage |
|-------|-------|-------|
| `space_xs` | 4dp | Icon gaps, tight |
| `space_sm` | 8dp | Standard component gap |
| `space_md` | 16dp | Screen padding, section gap |
| `space_lg` | 24dp | Large section gap |
| `space_xl` | 32dp | Hero sections |

---

## Corner Radius Scale

| Token | Value | Usage |
|-------|-------|-------|
| `corner_xs` | 3dp | Tiny chips, badges |
| `corner_s` | 5dp | Mini player thumbnail, small chips |
| `corner_m` | 7dp | Standard cards, buttons |
| `corner_l` | 10dp | Medium cards, dialogs |
| `corner_xl` | 14dp | Large cards, sheets |
| `corner_xxl` | 24dp | Now Playing artwork, hero cards |
| `corner_pill` | 9999dp | Full pill (play buttons, chips) |

---

## Key Component Specs

### Mini Player
- **Height**: 48dp
- **Max Width**: 600dp (tablet)
- **Horizontal Margin**: 16dp (`default_padding`)
- **Thumbnail**: 30dp × 30dp, 5dp radius (`corner_s`)
- **Buttons**: 32dp × 32dp (play, next)
- **Elevation**: Floating view elevation
- **Background**: `bg_apple_material` (material surface with subtle stroke)
- **Typography**: Title = `FootnoteEmphasized`, Artist = `Caption1`
- **Gap to Bottom Nav**: 8dp (`miniplayer_to_navtabs_spacing`)

### Bottom Navigation
- **Height**: 56dp
- **Elevation**: 8dp
- **Active Indicator**: 1px height, color `#1F767680` (subtle)
- **Icon Size**: 24dp (standard), 30dp (item)
- **Text**: 12sp inactive / 14sp active
- **Padding**: Top 9dp, Bottom 13dp
- **Horizontal Padding**: 12dp
- **Tabs**: 5 (Listen Now, Browse, Radio, Library, Search)

### Now Playing (Full Screen)
- **Artwork**: Full-bleed, 24dp radius (`corner_xxl`)
- **Background**: Artwork-derived gradient (palette extraction)
- **Artwork Size**: 540dp container (tablet), full-width mobile
- **Controls**: Centered, 72dp play button, 36dp skip buttons
- **Lyrics Sheet**: Bottom sheet, 48dp peek height
- **Queue Sheet**: Bottom sheet, drag handle

### Shared Element Transition
- **Type**: Fade (simple) — can upgrade to `changeImageTransform`
- **Transition Name**: Shared artwork between list → Now Playing

---

## Motion / Animation

| Animation | Duration | Easing | Usage |
|-----------|----------|--------|-------|
| Standard | 250ms | `cubic-bezier(0.4, 0, 0.2, 1)` | Most transitions |
| Emphasized | 350ms | `cubic-bezier(0.4, 0, 0.2, 1)` | Sheet open/close |
| Spring | 400ms | Spring(1.0, 0.8) | Drag, fling |
| Mini Player Expand | 300ms | `cubic-bezier(0.4, 0, 0.2, 1)` | Mini → Full |
| Crossfade | 150ms | Linear | Image loads, tab switch |

---

## Navigation Structure (5 Tabs)

| Index | Tab | Icon | Route | Notes |
|-------|-----|------|-------|-------|
| 0 | Listen Now | House (`tabbar_home`) | `/listen_now` | Personalized home |
| 1 | Browse | Grid (`ic_uitabbar_browse`) | `/browse` | New releases, charts, genres |
| 2 | Radio | Radio (`ic_uitabbar_radio`) | `/radio` | Apple Music 1, stations |
| 3 | Library | Books (`ic_uitabbar_library`) | `/library` | User library, playlists |
| 4 | Search | Magnifier (`ic_uitabbar_search`) | `/search` | Search + suggestions |

---

## API Endpoints (for MusicKit Integration)

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `https://amp-api.music.apple.com/v1/catalog/{storefront}/search/query` | GET | Search songs, albums, artists |
| `https://amp-api.music.apple.com/v1/catalog/{storefront}/stations?filter[identity]=personal` | GET | Personal radio stations |
| `https://amp-api.music.apple.com/v1/catalog/{storefront}/artists/{id}` | GET | Artist detail |
| `https://amp-api.music.apple.com/v1/catalog/{storefront}/albums/{id}` | GET | Album detail + tracks |
| `https://amp-api.music.apple.com/v1/me/library` | GET | User library (requires user token) |
| `gsd.apple.com` | Various | Device services, FairPlay |

**Auth**: Developer JWT (ES256) + optional User Token (Music User Token)

---

## Key Drawables / Assets Needed

| Asset | Spec | Source |
|-------|------|--------|
| Tab icons (5) | 24×24dp, outlined | Extract from APK `drawable-xxxhdpi` |
| Mini player play/pause/next | 32×32dp | Extract from APK |
| Now Playing controls | 36dp/72dp | Extract from APK |
| App icon | Adaptive (108×108) | Extract from APK `mipmap-xxxhdpi` |
| Placeholder artwork | 200×200 | Already have |

---

## Implementation Priority for Our App

### Phase 1: Theme Foundation (Day 1)
1. Update `Color.kt` with Apple palette (dark + light)
2. Update `Typography.kt` with Inter + Apple scale
3. Update `Theme.kt` with Material3 ColorScheme mapping
4. Add corner radius tokens, spacing tokens

### Phase 2: Navigation & Layout (Day 1-2)
1. 5-tab bottom navigation (add Radio tab)
2. MiniPlayer component with exact specs
3. Scaffold with CoordinatorLayout-like behavior

### Phase 3: Screens (Day 2-3)
1. Home → Listen Now (hero + shelves)
2. Browse screen (new releases, charts)
3. Radio screen (stub with Apple Music 1 branding)
4. Library (existing + Apple-style sections)
5. Search (overlay style with suggestions)

### Phase 4: Now Playing (Day 3)
1. Full-screen player with artwork gradient background
2. Lyrics/Queue toggle (already have)
3. Shared element transition (artwork)

### Phase 5: MusicKit Provider (Day 4)
1. `AppleMusicCatalogProvider` with JWT auth
2. Provider switch in Settings
3. 30-sec preview playback

---

## Legal / Compliance Notes

| Item | Status |
|------|--------|
| SF Pro fonts | ❌ Cannot ship — use Inter |
| SF Pro Rounded | ❌ Cannot ship — use Inter with `ss01` |
| Artwork / icons from APK | ⚠️ Extract for reference only, redraw |
| `amp-api.music.apple.com` | ✅ Public MusicKit API (requires Apple Dev account) |
| Private APIs (FairPlay, gsd) | ❌ Do not use |
| Apple Music branding | ❌ Do not use "Apple Music" name — "Music" is fine |

---

## Verification Checklist

- [ ] Dark theme matches `#262628` background
- [ ] Accent red `#F82C44` used for primary actions
- [ ] Blue `#0091FF` used for links
- [ ] Mini player 48dp, 30dp thumbnail, 5dp radius
- [ ] Bottom nav 56dp, 5 tabs, 8dp elevation
- [ ] 5-tab structure: Listen Now, Browse, Radio, Library, Search
- [ ] Artwork radius 24dp in Now Playing
- [ ] Inter font loads with correct weights
- [ ] 30-sec preview works with MusicKit JWT
- [ ] Provider switch toggles between YouTube / Apple Music