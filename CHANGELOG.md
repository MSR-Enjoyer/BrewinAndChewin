- This update is required both clientside and serverside.

## Changes
- Tipsy's sway is now lerped between values.
- Tweaked Tipsified text modifications to be more frequent and less set in stone.
- Tipsy is no longer consistently modified, opting for random modifications.
  - The value at which Tipsy is randomised is consistent between clients.

## Bugfixes
- Fixed Tipsified text stripping chat styling.
- Fixed Tipsified text not respecting spaces within usernames (will show up in nickname mods).
- Fixed block culling not respecting Tipsy swaying to a certain degree.
  - Sometimes, screen tears may happen, but I don't know enough about rendering to fix it :P.
- [FABRIC] Fixed server-sided Tipsy chatting not working with mods that run off Fabric API's chat event.