package me.dalton.capturethepoints.commands;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.util.Permissions;

import org.bukkit.ChatColor;

public class HelpCommand extends CTPCommand {
    
    /** Help command. Also displays if just "/ctp" is typed. */
    public HelpCommand(CaptureThePoints instance) {
        super.ctp = instance;
        super.aliases.add("help");
        super.aliases.add("?");
        super.notOpCommand = true;
        super.senderMustBePlayer = false;
        super.minParameters = 1;
        super.maxParameters = 3;
        super.usageTemplate = "/ctp help [pagenumber]";
    }

    @Override
    public void perform() {
        int size = parameters.size();
        // ctp = parameters.get(0)
        //String arg = size > 1 ? parameters.get(1) : "help"; // Kj -- grab the arguments with null -> empty checking. If only /ctp build, assume help.
        String arg2 = size > 2 ? parameters.get(2) : "";
        String pagenumber = arg2;
        
        if (parameters.size() == 1 || pagenumber.isEmpty() || pagenumber.equals("1")) {
            sendMessage(ChatColor.RED + "CTP Commands: " + ChatColor.GOLD + " Page 1/2");
            sendMessage(ChatColor.DARK_GREEN + "/ctp help [pagenumber] " + ChatColor.WHITE + "- view this menu.");
            
            if (Permissions.canAccess(sender, true, new String[]{"ctp.*", "ctp.play", "ctp.admin"})) {
                sendMessage(ChatColor.GREEN + "/ctp aliases " + ChatColor.WHITE + "- list of helpful command aliases");
            }
            if (Permissions.canAccess(sender, false, new String[]{"ctp.*", "ctp.auto", "ctp.admin"})) {
                sendMessage(ChatColor.GREEN + "/ctp auto <worldname>" + ChatColor.WHITE + "- start ctp with all players in a world.");
            }
            if (Permissions.canAccess(sender, true, new String[]{"ctp.*", "ctp.play", "ctp.admin"})) {
                sendMessage(ChatColor.GREEN + "/ctp build help " + ChatColor.WHITE + "- arena editing commands list");
            }
            if (Permissions.canAccess(sender, true, new String[]{"ctp.*", "ctp.play", "ctp.admin"})) {
                sendMessage(ChatColor.GREEN + "/ctp colors " + ChatColor.WHITE + "- available colors and senders in-game");
            }
            if (Permissions.canAccess(sender, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.joinall"})) {
                sendMessage(ChatColor.GREEN + "/ctp joinall " + ChatColor.WHITE + "- make all players join the game");
            }
            if (Permissions.canAccess(sender, true, new String[]{"ctp.*", "ctp.play", "ctp.admin"})) {
                sendMessage(ChatColor.GREEN + "/ctp join " + ChatColor.WHITE + "- join the game");
            }
            if (Permissions.canAccess(sender, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.kick"})) {
                sendMessage(ChatColor.GREEN + "/ctp kick <sender> " + ChatColor.WHITE + "- kicks player from the game");
            }
            if (Permissions.canAccess(sender, true, new String[]{"ctp.*", "ctp.play", "ctp.admin"})) {
                sendMessage(ChatColor.GREEN + "/ctp leave " + ChatColor.WHITE + "- leave the game");
            }
        } else if (pagenumber.equals("2")) {
            sendMessage(ChatColor.RED + "CTP Commands: " + ChatColor.GOLD + " Page 2/2");
            
            if (Permissions.canAccess(sender, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.pjoin"})) {
                sendMessage(ChatColor.GREEN + "/ctp pjoin <sender> " + ChatColor.WHITE + "- makes this player join the game");
            }
            if (Permissions.canAccess(sender, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.reload"})) {
                sendMessage(ChatColor.GREEN + "/ctp reload " + ChatColor.WHITE + "- reload CTP config files");
            }
            if (Permissions.canAccess(sender, true, new String[]{"ctp.*", "ctp.play", "ctp.admin", "ctp.rejoin"})) {
                sendMessage(ChatColor.GREEN + "/ctp rejoin " + ChatColor.WHITE + "- join a game if one has started.");
            }
            /*
            if (Permissions.canAccess(sender, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.save"})) {
                sendMessage(ChatColor.GREEN + "/ctp save " + ChatColor.WHITE + "- save");
            }
             */
            if (Permissions.canAccess(sender, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.setpoints"})) {
                sendMessage(ChatColor.GREEN + "/ctp setpoints <TeamColor> <number> " + ChatColor.WHITE + "- Set the chosen team's points/score");
            }
            if (Permissions.canAccess(sender, true, new String[]{"ctp.*", "ctp.admin", "ctp.play"})) {
                sendMessage(ChatColor.GREEN + "/ctp stats " + ChatColor.WHITE + "- get your in-game stats");
            }
            if (Permissions.canAccess(sender, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.stop"})) {
                sendMessage(ChatColor.GREEN + "/ctp stop " + ChatColor.WHITE + "- stops already running game");
            }
            if (Permissions.canAccess(sender, true, new String[]{"ctp.*", "ctp.admin", "ctp.play"})) {
                sendMessage(ChatColor.GREEN + "/ctp team  " + ChatColor.WHITE + "- gets the members on your team");
            }
            if (Permissions.canAccess(sender, false, new String[]{"ctp.*", "ctp.admin"})) {
                sendMessage(ChatColor.GREEN + "/ctp version  " + ChatColor.WHITE + "- check this plugin's version");
            }
        }
    }
}
