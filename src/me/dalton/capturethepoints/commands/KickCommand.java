package me.dalton.capturethepoints.commands;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.enums.ArenaLeaveReason;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KickCommand extends CTPCommand {
   
    /** Allows an admin to kick a player from a CTP game. */
    public KickCommand(CaptureThePoints instance) {
        super.ctp = instance;
        super.aliases.add("kick");
        super.notOpCommand = false;
        super.requiredPermissions = new String[]{"ctp.*", "ctp.admin.kick", "ctp.admin"};
        super.senderMustBePlayer = false;
        super.minParameters = 3;
        super.maxParameters = 3;
        super.usageTemplate = "/ctp kick <player>";
    }

    @Override
    public void perform() {            
        Player bob = ctp.getServer().getPlayer(parameters.get(2));
        
        if (bob == null) {
            sendMessage(ChatColor.RED + "Could not find the online player " + ChatColor.GOLD + parameters.get(2) + ChatColor.RED +".");
            return;
        }
        
        if (ctp.getArenaMaster().isPlayerInAnArena(bob.getName())) {
            ctp.sendMessage(bob, ChatColor.GREEN + sender.getName() + ChatColor.WHITE + " kicked you from a CTP game!");
            ctp.getArenaMaster().getArenaPlayerIsIn(bob).leaveGame(bob, ArenaLeaveReason.PLAYER_KICK_COMMAND);
        } else {
            sendMessage(ChatColor.GOLD + parameters.get(2) + ChatColor.RED +" is not playing CTP!");
        }
        return;
    }
}