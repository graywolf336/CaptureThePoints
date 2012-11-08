CaptureThePoints
==========

# DO NOT USE THIS, AS IT WILL BREAK YOUR SERVER...i think

If for some reason you'd like to test this out, you can download the latest build at: http://graywolfsolutions.com:8080/job/CaptureThePoints/

### Update: 1.4.5 ###
* Rewrote how potions are handled completely, let craftbukkit do it for us.
* Rewrote several classes and moved things around to be organized in the code.
* Removed a bunch of commented out code, if you want it back look up the stuff in the github history prior to this commit.
* Made when we setHealth to trigger a new regain health event, although I'm still finding these throughout the plugin.
* Made it so that when you first load the plugin (or you haven't made any arenas yet) it doesn't automatically generate an Arenas.yml file in the Arenas folder which in the past would throw errors and confuse you.
* Fixed permissions not being used
* Fixed the random chance of everyone getting ctp.admin node, now only ops get it
* Fixed the plugin not respecting unix hidden files, thanks to Psithief
* Fixed a lot of NPEs that only occur on the first run of the plugin
* Fixed NPEs being thrown when there weren't any arenas
* Added an option to disallow regenerating health due to their hunger bar being satisfied
* Cleaned up how messages are logged to the console
* Cleaned up the formatting of sending messages to the players, now the same across the plugin