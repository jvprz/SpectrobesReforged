# Spectrobes Reforged

## v0.6.0-beta.1 — 2026-10-22

### Krawl encounters
- Replaced the single-Krawl kill milestone with one Complete a Vortex advancement, awarded only for encounter victory. Existing vortex completions retain their internal advancement ID.
- Improved terrain-following over one-block ledges, slabs and stairs, with client position interpolation.
- Added distinct battle-start and victory sound events using vanilla audio, with localized subtitles.
- Added subtle shadow/violet particles to the continuous vortex funnel.
- Scanner companions now hide during vortex combat and return after the enemies are defeated or the encounter is cancelled; saved stats remain intact.
- Added small mesh-rendered Krawl vortices that pursue nearby players and trigger encounters on contact.
- Added a finite three-Blova encounter, two-mineral reward, saved progress, cancellation and orphan cleanup.
- Added rare surface discovery for Survival players and a summon command for testing.
- Vortices dissolve on contact, spawn three Blova around the caught player and drop the final reward in front of them, with translated encounter messages.
- Added the translated Complete a Vortex advancement and vortex wiki guide.

### Added
- Survival crafting recipes for the white Prizmod and incubator.
- Shapeless Prizmod recoloring recipes for all sixteen colors.
- Recipe-book unlocks and a ten-node advancement tree, translated into all six supported languages.
- Action-based milestones for awakening, scanning, mineral feeding, Chroma and awakening all six supported species.
- Updated player wiki with equipment recipes and advancement requirements.

## v0.5.1-alpha

### Added
- Spanish, French, German, Italian and Brazilian Portuguese translations.
- Localized Prizmod tooltips, mineral descriptions, item and block names, and player messages.
- Automated checks for translation completeness and message placeholders.

### Fixed
- Spectrobe species reloads now apply changed definitions and remove deleted species.
- Invalid species JSON roots are reported without aborting the remaining definitions.

### Improved
- Scanner block searches are spread across ticks to reduce single-frame work.
- Scanner particles now update independently of frame rate.
- Reduced repeated entity searches and expired wave processing in scanner rendering.
- Unified Prizmod snapshot construction and separated incubator music logic.
- Translated team headings fit within the Prizmod interface.

### Compatibility
- Minecraft 1.21.1, NeoForge, Java 21.
- Requires GeckoLib for NeoForge 1.21.1.
- Alpha release.


## v0.3.0-alpha

### Added
- JSON driven spectrobe species system
- Mineral stat modification system
- Evolution mineral counter
- Particle and sound feedback when feeding minerals
- Species loader via datapack reload listener

### Improved
- Prizmod tooltip now shows stat base and mineral bonus
- Stats now clamp between base and max values
- Species registry now supports dynamic JSON loading

### Technical
- Removed hardcoded species logic
- Added SpectrobeSpeciesLoader
- Improved Prizmod data synchronization