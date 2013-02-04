package me.dalton.capturethepoints.commands;

import me.dalton.capturethepoints.CaptureThePoints;
import org.bukkit.ChatColor;

public class LateJoinCommand extends CTPCommand {
   
    /** Allows player to join a ctp game that is already running. Does NOT start a new one. */
    public LateJoinCommand(CaptureThePoints instance) {
        super.ctp = instance;
        super.aliases.add("latejoin");
        super.aliases.add("lj");
        super.notOpCommand = true;
        super.requiredPermissions = new String[]{"ctp.*", "ctp.play", "ctp.admin", "ctp.latejoin"};
        super.senderMustBePlayer = true;
        super.minParameters = 3;
        super.maxParameters = 3;
        super.usageTemplate = "/ctp latejoin <arena>";
    }

    @Override
    public void perform() {
        if (!ctp.getArenaMaster().isPlayerInAnArena(player.getName())) {
            if (ctp.getArenaMaster().getArena(parameters.get(2)).isGameRunning()) {
                ctp.moveToLobby(player);
            } else {
                sendMessage(ChatColor.RED + "Game not started yet. Try just doing " + ChatColor.AQUA + "/ctp join <arena>");
            }
            return;
        }
        
        sendMessage(ChatColor.RED + "You are already playing game!");
        return;
    }
}