package me.dalton.capturethepoints.commands;

import org.bukkit.ChatColor;

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
			String msg = "The current arenas are: ";
    		for(Arena a : ctp.getArenaMaster().getArenas())
    			if(ctp.getArenaMaster().getSelectedArena().getName().equalsIgnoreCase(a.getName()))
    				sendMessage("  -" + ChatColor.ITALIC + a.getName());
    			else
    				sendMessage("  -" + a.getName());
			
			sendMessage(msg);
			return;
		}else {
			sendMessage(ChatColor.RED + "You're currently playing a game, focus on playing.");
			return;
		}
	}

}
