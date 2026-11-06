# Apple Music API Inventory & Protocol Report (Deliverable D3)

Technical recon report mapping Apple Music's API endpoints, authentication flows, and headers observed in official client APK analysis.

## 1. Endpoints & Base URLs
- **Catalog API Base**: `https://api.music.apple.com/v1/catalog/`
- **Me (User Account) API Base**: `https://api.music.apple.com/v1/me/`
- **Amp API Gateway**: `https://amp-api.music.apple.com/v1/`
- **Playback / Streaming Edge**: `https://audio-ssl.itunes.apple.com/` / `https://audio.itunes.apple.com/`

## 2. Authentication & Header Signatures
- **Developer Token (`Authorization: Bearer <JWT>`)**:
  - Signed using ES256 algorithm with a private key (`.p8`) generated in Apple Developer Account.
  - Payload claims: `iss` (Team ID), `iat`, `exp` (max 6 months).
- **Media-User-Token (`Music-User-Token: <token>`)**:
  - Acquired via iOS/Android system authentication (`CloudService` / `AuthenticationServices`).
  - Required for personalized endpoints (`/v1/me/library`, `/v1/me/recommendations`).
- **Standard Required Headers**:
  - `Origin: https://music.apple.com`
  - `Referer: https://music.apple.com/`
  - `User-Agent: Mozilla/5.0 (Android; Mobile)...`
  - `Accept: application/json`

## 3. Observed vs. Inferred Boundaries
- **Observed (Public Web API)**: Catalog search, album tracks lookup, chart rankings, and 30-second audio previews.
- **Inferred (Private Endpoints)**: Full-length lossless/Atmos audio stream decryption and FairPlay DRM key exchange.
- **Boundary Statement**: Full-length lossless playback from a third-party client without a valid subscribed `Music-User-Token` and DRM decoders violates Apple ToS and is out of scope for legal client integration. We implement official Catalog API search & 30-sec preview playback, with fallback/primary integration via our existing extraction repository.
