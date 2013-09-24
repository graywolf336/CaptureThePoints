package me.dalton.capturethepoints.commands;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.Arena;

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
    		if(parameters.size() == 2) //Only did /ctp join, so send them to the default arena
    			ctp.getArenaMaster().getSelectedArena().joinLobby(player);
    		else if(ctp.getArenaMaster().isArena(parameters.get(2))) //Did /ctp j <arena>, so send them to the selected one if it is an arena
    			ctp.getArenaMaster().getArena(parameters.get(2)).joinLobby(player);
        	else {
        		sendMessage(ctp.getLanguage().ARENA_NAME_LIST);
        		for(Arena a : ctp.getArenaMaster().getArenas())
        			if(ctp.getArenaMaster().getSelectedArena().getName().equalsIgnoreCase(a.getName()))
        				sendMessage("  -" + a.getName());
        			else
        				sendMessage("  -" + a.getName());
        	}
            return;
        }else {
            sendMessage(ctp.getLanguage().ALREADY_PLAYING);
            return;
        }
    }
}