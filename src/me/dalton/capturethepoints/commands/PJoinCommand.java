package me.dalton.capturethepoints.commands;

import me.dalton.capturethepoints.CaptureThePoints;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class PJoinCommand extends CTPCommand {
   
    /** Allows admin to force a player into playing a CTP game. */
    public PJoinCommand(CaptureThePoints instance) {
        super.ctp = instance;
        super.aliases.add("pjoin");
        super.aliases.add("pj");
        super.notOpCommand = false;
        super.requiredPermissions = new String[]{"ctp.*", "ctp.admin.pjoin", "ctp.admin"};
        super.senderMustBePlayer = false;
        super.minParameters = 4;
        super.maxParameters = 4; //0 1    2           3
        super.usageTemplate = "/ctp pjoin <player> <arena>";
    }

    @Override
    public void perform() {
        if (ctp.getArenaMaster().getArenas().isEmpty()) {
            sendMessage(ChatColor.RED + "Please create an arena first");
            return;
        }
        
        if(ctp.getArenaMaster().getArena(parameters.get(3)) == null) {
        	sendMessage(ChatColor.RED + "Please enter a valid arena to force this player to join.");
        	return;
        }
        
        if (ctp.getArenaMaster().getArena(parameters.get(3)).getLobby() == null) {
            sendMessage(ChatColor.RED + "Please create the lobby for the arena " + parameters.get(3));
            return;
        }
            
        Player bob = ctp.getServer().getPlayer(parameters.get(2));
        if (bob == null) {
            sendMessage(ChatColor.RED + "Could not find the online player " + ChatColor.GOLD + parameters.get(2) + ChatColor.RED +".");
            return;
        }
        
        if (!ctp.getArenaMaster().isPlayerInAnArena(bob)) {
            if (!(sender instanceof ConsoleCommandSender)) {
                // If the command issuer is not from console
                ctp.sendMessage(bob, ChatColor.GREEN + sender.getName() + ChatColor.WHITE + " forced you to join CTP!");
            }
            
            ctp.getArenaMaster().moveToLobby(ctp.getArenaMaster().getArena(parameters.get(3)), bob);
        } else {
            sendMessage(ChatColor.GOLD + parameters.get(2) + ChatColor.RED +" is already playing CTP!");
        }
        return;
    }
}