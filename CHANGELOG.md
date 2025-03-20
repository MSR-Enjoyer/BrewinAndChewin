## Major Changes
- [FABRIC] Updated for Farmer's Delight Refabricated 3.0.0.
- Optimised temperature into not checking every tick. Temperature is now only checked every 4 seconds or whenever the Keg GUI is opened,

## Bugfixes
- Fixed the keg not removing items upon fermenting.
- Fixed keg emptying fluids multiple to the capacity amount, rather than the amount of the fluid in the tank.
- Fixed Tipsy chat scrambling applying to the wrong player on servers.
- Fixed incorrect rendering of Keg recipe book.
- Fixed incorrect item count in EMI Pouring recipes.
- [NEOFORGE] Fixed food anchored hud overlays from other mods being offset down into the food bar when Intoxication is applied. [#38](https://github.com/MerchantPug/BrewinAndChewin/issues/38)
- [FABRIC] Fixed a classloading issue preventing the ItemLike (Mojmap) interface from having mixins applied.
    - This issue is explained at [MehVahdJukaar/FarmersDelightRefabricated#77](https://github.com/MehVahdJukaar/FarmersDelightRefabricated/issues/77)
- [FABRIC] Fixed the keg not accepting items in cases.