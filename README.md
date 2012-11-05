CaptureThePoints
==========

# DO NOT USE THIS, AS IT WILL BREAK YOUR SERVER...i think

If for some reason you'd like to test this out, you can download the latest build at: http://graywolfsolutions.com:8080/job/CaptureThePoints/

### Update: 1.4.3 ###
* Rewrote the CTPPotionEffect to use Craftbukkit's APIs
* Removed a bunch of commented out code, if you want it back look up the stuff in the github history prior to this commit.
* Tried to get all the sendMessage to use the one on the main class, and to try and get all the other sendMessages to look the same across the board.
* Made when we setHealth to trigger a new regain health event, although I'm still finding these throughout the plugin.
* Made the plugin respect unix's hidden files, thanks to Psithief
* Made sure that UsePermissions was set to true if using Vault
* Made sure that only ops by default get the ctp.admin permission node
* Added an option to disallow regenerating health due to their hunger bar being satisfied
* Added a message to be displayed when the wasn't any arenas in the arenalist
* Temporary workaround for a NPE that only happens at certain times.
* Cleaned up how messages are logged to the console