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
        super.minParameters = 4;
        super.maxParameters = 4;
        super.usageTemplate = "/ctp kick <arena> <player>";
    }

    @Override
    public void perform() {
        if (ctp.getArenaMaster().getArenas().isEmpty()) {
            sendMessage(ChatColor.RED + "There are currently no arenas, please create one first.");
            return;
        }
        
        if(ctp.getArenaMaster().getArena(parameters.get(2)) == null) {
        	sendMessage(ChatColor.RED + "Please enter a valid arena name to kick someone from.");
        	return;
        }
        
        if (ctp.getArenaMaster().getArena(parameters.get(2)).getLobby() == null) {
            sendMessage(ChatColor.RED + "Please create arena lobby");
            return;
        }
            
        Player bob = ctp.getServer().getPlayer(parameters.get(3));
        
        if (bob == null) {
            sendMessage(ChatColor.RED + "Could not find the online player " + ChatColor.GOLD + parameters.get(3) + ChatColor.RED +".");
            return;
        }
        
        if (ctp.getArenaMaster().isPlayerInAnArena(bob.getName())) {
            ctp.sendMessage(bob, ChatColor.GREEN + sender.getName() + ChatColor.WHITE + " kicked you from CTP game!");
            ctp.getArenaMaster().getArena(parameters.get(2)).leaveGame(bob, ArenaLeaveReason.PLAYER_KICK_COMMAND);
        } else {
            sendMessage(ChatColor.GOLD + parameters.get(3) + ChatColor.RED +" is not playing CTP!");
        }
        return;
    }
}