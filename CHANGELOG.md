# Spectrobes Reforged

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