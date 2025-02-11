## Changes
- Ported the mod to 1.21.1.
- The mod now supports NeoForge and Fabric under one codebase.
  - Multiloader probably won't happen for 1.20.1, please don't ask.

## Internal
- Swapped config solution from Forge config to Greenhouse Config.
- Updated recipe code to match 1.21.1 specifics, such as allowing NeoForge fluid ingredients.
  - Unfortunately, I cannot extensively document the changes right now, as they swept across the entire mod. Hopefully I'll get to working on the wiki soon - Pug.
- Removed the maximum of 10000mB for Keg capacity in the configuration.