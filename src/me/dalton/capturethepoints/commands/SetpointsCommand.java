package me.dalton.capturethepoints.commands;

import me.dalton.capturethepoints.CaptureThePoints;
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
        super.minParameters = 4;
        super.maxParameters = 4;
        super.usageTemplate = "/ctp setpoints <Teamcolor> <number>";
    }

    @Override
    public void perform() {
        int points = 0;
        try {
            points = Integer.parseInt(parameters.get(3));
        } catch (Exception NumberFormatException) {
            sendMessage(ChatColor.RED + "Incorect number format. Usage: " + ChatColor.GREEN + "/ctp setpoints <Teamcolor> <number>");
            return;
        }

        if (ctp.mainArena.getConfigOptions().useScoreGeneration) {
            for (Team team : ctp.mainArena.getTeams()) {
                if (team.getColor().equalsIgnoreCase(parameters.get(2))) {
                    team.setScore(points);
                }
            }
            ctp.blockListener.didSomeoneWin();
        } else {
            for (Team team : ctp.mainArena.getTeams()) {
                if (team.getColor().equalsIgnoreCase(parameters.get(2))) {
                    team.setControlledPoints(points);
                }
            }
            ctp.blockListener.didSomeoneWin();
        }
        
        sendMessage(ChatColor.RED + "There is no such color!");
        return;
    }
}