package me.dalton.capturethepoints.commands;

import me.dalton.capturethepoints.CaptureThePoints;
import org.bukkit.ChatColor;

public class JoinCommand extends CTPCommand {
   
    /** Allows player to join ctp game. Starts a new one if one isn't running already. */
    public JoinCommand(CaptureThePoints instance) {
        super.ctp = instance;
        super.aliases.add("join");
        super.aliases.add("j");
        super.aliases.add("play");
        super.notOpCommand = true;
        super.requiredPermissions = new String[]{"ctp.*", "ctp.play", "ctp.admin", "ctp.join"};
        super.senderMustBePlayer = true;
        super.minParameters = 2;
        super.maxParameters = 3;
        super.usageTemplate = "/ctp join <arena>";
    }

    @Override
    public void perform() {
    	if(!ctp.getArenaMaster().isPlayerInAnArena(player.getName())) {
    		if(parameters.size() == 2)
    			ctp.moveToLobby(ctp.getArenaMaster().getSelectedArena(), player);
    		else if(ctp.getArenaMaster().isArena(parameters.get(2)))
        		ctp.moveToLobby(ctp.getArenaMaster().getArena(parameters.get(2)), player);
        	else
        		sendMessage(ChatColor.RED + "That arena doesn't exist, try another one.");
            return;
        }else {
            sendMessage(ChatColor.RED + "You are already playing game!");
            return;
        }
    }
}