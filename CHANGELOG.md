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
- Intoxication now affects all sources of saturation.
- Rewrote Tipsy chat scrambling to make it more consistent across players, and to follow the original logic.
  - Tipsified chat messages now count as modified messages on the client, and you are now able to be prompted to see the original by hovering over the chat.
- Re-organised the creative tab.
- Added EMI integration.

## Internal
- Swapped config solution from Forge config to Greenhouse Config.
  - There is no config screen at the moment. This will be added later.
- Updated recipe code to match 1.21.1 specifics, such as allowing NeoForge fluid ingredients.
  - Unfortunately, I cannot extensively document the changes right now, as they swept across the entire mod. Hopefully I'll get to working on the wiki soon - Pug.
- Swapped fluid ingredients for BnC Fermenting recipes to use fluid tags.
- Removed the maximum of 10000mB for Keg capacity in the configuration.

## Bugfixes
- Fixed keg eating fluid inputs when extracting more than the output slot's stack amount.