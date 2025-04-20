# Note: This update will require an update on the server alongside the client.

## Changes
- Players in adventure mode may no longer interact with Coaster blocks.
- [INTERNAL] Updated tipsy seed sync to allow for /msg, /tell to share random seeds between sent messages (if you /tell yourself).

## Bugfixes
- [FABRIC] Fixed narrator not narrating Tipsified messages.
- Fixed /tell crashing the game when used whilst Tipsy. [#55](https://github.com/MerchantCalico/BrewinAndChewin/issues/55).
- Fixed a crash with JEI integration caused by referencing a client class on the server. [#56](https://github.com/MerchantCalico/BrewinAndChewin/issues/56).