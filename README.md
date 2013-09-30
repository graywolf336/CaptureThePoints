CaptureThePoints
==========

_Status of this branch's builds:_ [![Build Status](http://git.graywolf336.com:8080/job/CaptureThePoints/badge/icon)](http://git.graywolf336.com:8080/job/CaptureThePoints/)

### Language ###
Need to completely re-think this entire thing as how it is currently setup is just horrible and I am not proud of it at all. You can tell it was rushed and not thought through. 

### Update: 1.5.0 ###
* Support multiple arenas, easier `/ctp j <arena>`
* Organized code and recoded a large percentage of the plugin, better performance and more features
* Add custom events
* - CTPEndEvent
* - CTPPlayerDeathEvent
* - CTPPlayerJoinEvent
* - CTPPlayerLeaveEvent
* - CTPPointCaptureEvent
* - CTPShopPurchaseEvent
* - CTPStartEvent
* Sign update when restoring an arena (force the client to update), thanks to Bubelbub
* Moved some config options, you will have to manually reset them (sorry)
* - `MaximumPlayers` has been moved under `GlobalSettings.GameMode.Players.MaximumPlayers`
* - `MinimumPlayers` has been moved under `GlobalSettings.GameMode.Players.MinimumPlayers`
* Changed some commands to fix the new system
* - `/ctp join <arena>` if no `<arena>` is given, we will send them to the default arena or the one arena (if there is only one)
* - `/ctp kick <arena> <player`
* - `/ctp start <arena>`
* - `/ctp colors <arena>` if no `<arena>` is given, it will list out all your arenas for you.
* Fixed a bug that could allow for glitching out items
* Fixed a bug that allowed users to teleport out of the lobby using ender pearls.
* Fixed the conflicting nature of Multiverse Inventories, we clear their inventory before we teleport and after.
* Fixed an NPE that could happen if a role didn't have any items
* Allowing blocks to be broke is now false.
* Allowing blocks to be placed is now false.
* Added a config option to allow explosions breaking blocks, defaults to false
* Added support for Bukkit's player max health option, now it can be higher than 20 (requires a newer version of bukkit).
* Added support for adding colors in the kill streak messages.
* Added support for TagAPI, player's name tags will relate to their team color
* Added support for items to have a custom name and custom lore!
* Added config option to keep the inventory on respawning, including bought items
* Added eggs are grenades if enabled in the config.
* Added count downs to starting and stopping
* - Configurable per arena
* - Power is configurable per arena
* Added support for player lives (not done)
* - Config: `GlobalSettings.GameMode.Players.Lives.Enabled`
* - And `GlobalSettings.GameMode.Players.Lives.Amount`
* - With this you need to set stands players teleport to when they lose their lives
* Added support for setting the players client side time per arena
* - Config option is under `GlobalSettings.GameMode.Players.Time.Enabled` and also `GlobalSettings.GameMode.Players.Time.Value`
* - Only four values are accepted (might change if enough people request it) `dawn`, `midday`, `dusk`, and `midnight`


### Update: 1.4.5 ###
* Rewrote how potions are handled completely, let craftbukkit do it for us.
* Rewrote several classes and moved things around to be organized in the code.
* Removed a bunch of commented out code, if you want it back look up the stuff in the github history prior to this commit.
* Removed as many storing of player instances as I can see atm
* Made when we setHealth to trigger a new regain health event, although I'm still finding these throughout the plugin.
* Made it so that when you first load the plugin (or you haven't made any arenas yet) it doesn't automatically generate an Arenas.yml file in the Arenas folder which in the past would throw errors and confuse you.
* Fixed permissions not being used
* Fixed the random chance of everyone getting ctp.admin node, now only ops get it
* Fixed the plugin not respecting unix hidden files, thanks to Psithief
* Fixed a lot of NPEs that only occur on the first run of the plugin
* Fixed a couple NPEs being thrown when there weren't any arenas (still finding some more)
* Fixed a NPE thrown when trying to balance teams when there are no players.
* Fixed a NPE thrown when we try to restore role items but a player has taken off their armor
* Added an option to disallow regenerating health due to their hunger bar being satisfied
* Added a message of how many arenas were loaded.
* Added an option to allow players to break their own wool in a point they have captured.
* Added a command to change the amount of points needed to win
* Cleaned up how messages are logged to the console
* Cleaned up the formatting of sending messages to the players, now the same across the plugin