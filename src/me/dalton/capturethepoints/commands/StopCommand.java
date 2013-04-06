package me.dalton.capturethepoints.commands;

import me.dalton.capturethepoints.CaptureThePoints;

public class StopCommand extends CTPCommand {
   
    /** Allows admin to stop a CTP game. */
    public StopCommand(CaptureThePoints instance) {
        super.ctp = instance;
        super.aliases.add("stop");
        super.notOpCommand = false;
        super.requiredPermissions = new String[]{"ctp.*", "ctp.admin.stop", "ctp.admin"};
        super.senderMustBePlayer = false;
        super.minParameters = 3;
        super.maxParameters = 3;
        super.usageTemplate = "/ctp stop <arena>";
    }

    @Override
    public void perform() {
    	if(!ctp.getArenaMaster().isArena(parameters.get(2))) {
    		sendMessage(parameters.get(2) + " is not a valid arena name, please try again.");
    		return;
    	}
    	
        ctp.getArenaMaster().getArena(parameters.get(2)).endGame(false, true);//Don't give rewards as we have stopped the game.
        return;
    }
}