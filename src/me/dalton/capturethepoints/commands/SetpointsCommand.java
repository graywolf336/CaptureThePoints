package me.dalton.capturethepoints.commands;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.Team;

import org.bukkit.ChatColor;

public class SetpointsCommand extends CTPCommand {
   
    /** Allows admin to set the points/score that a team has. May screw up if points system is used rather than score. */
    public SetpointsCommand(CaptureThePoints instance) {
        super.ctp = instance;
        super.aliases.add("setpoints");
        super.notOpCommand = false;
        super.requiredPermissions = new String[]{"ctp.*", "ctp.admin.setpoints", "ctp.admin"};
        super.senderMustBePlayer = false;
        super.minParameters = 5;
        super.maxParameters = 5;
        super.usageTemplate = "/ctp setpoints <arena> <Teamcolor> <number>";
    }

    @Override
    public void perform() {
    	if(!ctp.getArenaMaster().getArenas().contains(parameters.get(2))) {
    		sendMessage(parameters.get(2) + " is not a valid arena name, please try again.");
    		return;
    	}
    	
        int points = 0;
        try {
            points = Integer.parseInt(parameters.get(4));
        } catch (Exception NumberFormatException) {
            sendMessage(ChatColor.RED + "Incorect number format. Usage: " + ChatColor.GREEN + "/ctp setpoints <arena> <Teamcolor> <number>");
            return;
        }

        Arena arena = ctp.getArenaMaster().getArena(parameters.get(2));
        
        if (arena.getConfigOptions().useScoreGeneration) {
            for (Team team : arena.getTeams()) {
                if (team.getColor().equalsIgnoreCase(parameters.get(3))) {
                    team.setScore(points);
                }
            }
            ctp.getArenaUtil().didSomeoneWin(arena);
        } else {
            for (Team team : arena.getTeams()) {
                if (team.getColor().equalsIgnoreCase(parameters.get(3))) {
                    team.setControlledPoints(points);
                }
            }
            ctp.getArenaUtil().didSomeoneWin(arena);
        }
        
        sendMessage(ChatColor.RED + "There is no such color!");
        return;
    }
}