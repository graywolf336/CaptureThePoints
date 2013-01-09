CaptureThePoints
==========

_Status of this branch's builds: **BROKEN**_

If for some reason you'd like to test this out, you can download the latest build at: http://graywolfsolutions.com:8080/job/CaptureThePoints/

### Branch: newArenas ###
* The way we handle more than one arena will be improved
* Joining arenas will be done with **/ctp j \<arena\>**

#### ToDo ####
* Store PlayerData per arena name in a hashmap<string, playerdata> where string is the arena name
* Store a variable for the default arena for when players don't type just _/ctp j_

#### Done ####
* Add a method to get the arenas
* Add a method to get a certain arena
* Fix the kick command
* Fix the start command

### Update: 1.5.0 ###
* Add three custom events
* - CTPPlayerLeaveEvent
* - CTPPlayerJoinEvent
* - CTPPlayerDeathEvent
* Force clients to get the sign updates that we do when restoring an arena, thanks to Bubelbub

### Update: 1.4.5 ###
* Rewrote how potions are handled completely, let craftbukkit do it for us.
* Rewrote several classes and moved things around to be organized in the code.
* Removed a bunch of commented out code, if you want it back look up the stuff in the github history prior to this commit.
* Removed as many storing of player instances as I can see atm, __I probably did break the plugin__
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