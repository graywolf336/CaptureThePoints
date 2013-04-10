package me.dalton.capturethepoints.commands;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.Arena;

public class ListCommand extends CTPCommand {
	
	/** Allows player to view a list of all the arenas. */
    public ListCommand(CaptureThePoints instance) {
        super.ctp = instance;
        super.aliases.add("arenas");
        super.aliases.add("list");
        super.notOpCommand = true;
        super.requiredPermissions = new String[]{"ctp.*", "ctp.play", "ctp.admin"};
        super.senderMustBePlayer = true;
        super.minParameters = 2;
        super.maxParameters = 2;
        super.usageTemplate = "/ctp list";
    }
    
	@Override
	protected void perform() {
		if(!ctp.getArenaMaster().isPlayerInAnArena(player.getName())) {
			sendMessage(ctp.getLanguage().ARENA_NAME_LIST);
			
    		for(Arena a : ctp.getArenaMaster().getArenas())
    			if(ctp.getArenaMaster().getSelectedArena().getName().equalsIgnoreCase(a.getName()))
    				sendMessage("  -" + a.getName());
    			else
    				sendMessage("  -" + a.getName());
			return;
		}else {
			sendMessage(ctp.getLanguage().ALREADY_PLAYING);
			return;
		}
	}

}
