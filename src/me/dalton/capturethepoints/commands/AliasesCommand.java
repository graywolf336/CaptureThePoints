package me.dalton.capturethepoints.commands;

import me.dalton.capturethepoints.CaptureThePoints;

import org.bukkit.ChatColor;

public class AliasesCommand extends CTPCommand {
    
    /** Command for a helpful list of aliases. */
    public AliasesCommand(CaptureThePoints instance) {
        super.ctp = instance;
        super.aliases.add("alias");
        super.aliases.add("aliases");
        super.notOpCommand = true;
        super.senderMustBePlayer = false;
        super.minParameters = 2;
        super.maxParameters = 2;
        super.usageTemplate = "/ctp aliases";
    }

    @Override
    public void perform() {
        sendMessage(ChatColor.RED + "Aliases for some commands:");
            sendMessage(ChatColor.GREEN + "alias: " + ChatColor.WHITE + "aliases");
            sendMessage(ChatColor.GREEN + "help: " + ChatColor.WHITE + "?");
        if (ctp.getPermissions().canAccess(sender, true, new String[]{"ctp.*", "ctp.play", "ctp.admin"})) {
            sendMessage(ChatColor.GREEN + "build: " + ChatColor.WHITE + "b, create, make");
        }
        if (ctp.getPermissions().canAccess(sender, true, new String[]{"ctp.*", "ctp.play", "ctp.admin", "ctp.colors"})) {
            sendMessage(ChatColor.GREEN + "colors: " + ChatColor.WHITE + "colours, teams, players");
        }
        if (ctp.getPermissions().canAccess(sender, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.joinall"})) {
            sendMessage(ChatColor.GREEN + "joinall: " + ChatColor.WHITE + "jall");
        }
        if (ctp.getPermissions().canAccess(sender, true, new String[]{"ctp.*", "ctp.play", "ctp.admin"})) {
            sendMessage(ChatColor.GREEN + "join: " + ChatColor.WHITE + "j, play");
        }            
        if (ctp.getPermissions().canAccess(sender, true, new String[]{"ctp.*", "ctp.play", "ctp.admin"})) {
            sendMessage(ChatColor.GREEN + "leave: " + ChatColor.WHITE + "exit, part, quit");
        }
        if (ctp.getPermissions().canAccess(sender, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.pjoin"})) {
            sendMessage(ChatColor.GREEN + "pjoin: " + ChatColor.WHITE + "pj");
        }
        if (ctp.getPermissions().canAccess(sender, true, new String[]{"ctp.*", "ctp.admin", "ctp.rejoin"})) {
            sendMessage(ChatColor.GREEN + "rejoin: " + ChatColor.WHITE + "rj");
        }
        if (ctp.getPermissions().canAccess(sender, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.setarena", "ctp.admin.select", "ctp.admin.selectarena"})) {
            sendMessage(ChatColor.GREEN + "select: " + ChatColor.WHITE + "setarena, selectarena, arena");
        }
        if (ctp.getPermissions().canAccess(sender, false, new String[]{"ctp.*", "ctp.admin.start", "ctp.admin"})) {
            sendMessage(ChatColor.GREEN + "start: " + ChatColor.WHITE + "go");
        }
    }
}
