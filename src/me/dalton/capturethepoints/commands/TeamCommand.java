package me.dalton.capturethepoints.commands;

import java.util.ArrayList;
import java.util.List;
import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.PlayerData;
import me.dalton.capturethepoints.beans.Team;

import org.bukkit.ChatColor;

public class TeamCommand extends CTPCommand {
    
    /** Allows players to view players on their team. */
    public TeamCommand(CaptureThePoints instance) {
        super.ctp = instance;
        super.aliases.add("team");
        super.aliases.add("myteam");
        super.notOpCommand = true;
        super.requiredPermissions = new String[]{"ctp.*", "ctp.play", "ctp.admin", "ctp.team"};
        super.senderMustBePlayer = true;
        super.minParameters = 2;
        super.maxParameters = 2;
        super.usageTemplate = "/ctp team";
    }

    @Override
    public void perform() {
        if (ctp.mainArena.getTeams().size() <= 0) {
            sendMessage(ChatColor.RED + "There are no teams - has a game been started?");
            return;
        }
        
        if (!ctp.blockListener.isAlreadyInGame(player.getName()) || ctp.playerData.get(player.getName()) == null) {
            sendMessage(ChatColor.RED + "You must be playing a game to get who's on your team!");
            return;
        }
        
        if (!ctp.blockListener.isAlreadyInGame(player.getName()) || ctp.playerData.get(player.getName()).getTeam() == null) {
            sendMessage(ChatColor.RED + "You have not yet been assigned a team!");
            return;
        }
        
        PlayerData data = ctp.playerData.get(player.getName());
        String teamcolour = data.getTeam().getColor().trim();
        
        List<String> playernames = new ArrayList<String>();
        ChatColor cc = ChatColor.GREEN;
        for (Team aTeam : ctp.mainArena.getTeams()) {
            if (teamcolour.equalsIgnoreCase(aTeam.getColor())) {
                cc = aTeam.getChatColor();
                playernames = aTeam.getTeamPlayerNames(ctp);
            }
        }
        
        sendMessage(ChatColor.GREEN + String.valueOf(playernames.size()) + cc + " teammates: " + playernames);
        if (!ctp.mainArena.getConfigOptions().useScoreGeneration) {
            sendMessage(ChatColor.GREEN + "Your team controls " + cc + ctp.playerData.get(player.getName()).getTeam().getControlledPoints() + ChatColor.GREEN + " points!");
        } else {
            sendMessage(ChatColor.GREEN + "Your team has a score of: " + cc + ctp.playerData.get(player.getName()).getTeam().getScore() + "!");
        }
        return;
    }
}
