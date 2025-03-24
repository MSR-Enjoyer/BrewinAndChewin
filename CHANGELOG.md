- This update is required both clientside and serverside.

## Changes
- Tweaked Tipsified text modifications to be more frequent and less set in stone.
- Tipsy is no longer consistently modified, opting for random modifications.
  - The value at which Tipsy is randomised is consistent between clients.

## Bugfixes
- Fixed Tipsified text stripping chat styling.
- Fixed Tipsified text not respecting spaces within usernames (will show up in nickname mods).
- Fixed block culling not respecting Tipsy swaying.
- [FABRIC] Fixed server-sided Tipsy chatting not working with mods that run off Fabric API's chat event.