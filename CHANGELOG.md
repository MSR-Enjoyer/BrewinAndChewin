## Changes
- Ported the mod to 1.21.1.
- The mod now supports NeoForge and Fabric under one codebase.
  - Multiloader probably won't happen for 1.20.1, please don't ask.
- Made improvements to vanilla recipe book integration.
- Removed the fallback fluid rendering from the keg's tooltip if an item isn't present. You're likely to have an associated item.
- Renamed certain item tags to match conventional names closer.
  - `brewinandchewin:cheese_wedges` -> `brewinandchewin:foods/cheese_wedge`
  - `brewinandchewin:horror_meats` -> `brewinandchewin:foods/horror_meat`
  - `brewinandchewin:raw_meats` -> `brewinandchewin:foods/jerky_meat`
  - `brewinandchewin:pizza_toppings` -> `brewinandchewin:foods/pizza_topping`
- Delayed damage from Tipsy now bypasses most forms of damage reduction.

## Internal
- Swapped config solution from Forge config to Greenhouse Config.
- Updated recipe code to match 1.21.1 specifics, such as allowing NeoForge fluid ingredients.
  - Unfortunately, I cannot extensively document the changes right now, as they swept across the entire mod. Hopefully I'll get to working on the wiki soon - Pug.
- Removed the maximum of 10000mB for Keg capacity in the configuration.