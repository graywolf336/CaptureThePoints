package me.dalton.capturethepoints.commands;

import java.util.ArrayList;
import java.util.List;
import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.Arena;
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
        if (!ctp.getArenaMaster().isPlayerInAnArena(player)) {
            sendMessage(ChatColor.RED + "You must be playing a game to get who's on your team!");
            return;
        }
        
        Arena arena = ctp.getArenaMaster().getArenaPlayerIsIn(player);
        
        if (arena.getTeams().size() <= 0) {
            sendMessage(ChatColor.RED + "There are no teams - has a game been started?");
            return;
        }
        
        if (arena.getPlayerData(player).getTeam() == null) {
            sendMessage(ChatColor.RED + "You have not yet been assigned a team!");
            return;
        }
        
        PlayerData data = arena.getPlayerData(player);
        String teamcolour = data.getTeam().getColor().trim();
        
        List<String> playernames = new ArrayList<String>();
        ChatColor cc = ChatColor.GREEN;
        for (Team aTeam : arena.getTeams()) {
            if (teamcolour.equalsIgnoreCase(aTeam.getColor())) {
                cc = aTeam.getChatColor();
                playernames = aTeam.getTeamPlayerNames(arena);
            }
        }
        
        sendMessage(ChatColor.GREEN + String.valueOf(playernames.size()) + cc + " teammates: " + playernames);
        if (!arena.getConfigOptions().useScoreGeneration) {
            sendMessage(ChatColor.GREEN + "Your team controls " + cc + data.getTeam().getControlledPoints() + ChatColor.GREEN + " points!");
        } else {
            sendMessage(ChatColor.GREEN + "Your team has a score of: " + cc + data.getTeam().getScore() + "!");
        }
        return;
    }
}
