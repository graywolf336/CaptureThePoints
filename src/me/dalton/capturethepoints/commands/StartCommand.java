package me.dalton.capturethepoints.commands;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.Lobby;

import org.bukkit.ChatColor;

public class StartCommand extends CTPCommand {
   
    /** Allows admin to manually start a ctp game. */
    public StartCommand(CaptureThePoints instance) {
        super.ctp = instance;
        super.aliases.add("start");
        super.aliases.add("go");
        super.notOpCommand = false;
        super.requiredPermissions = new String[]{"ctp.*", "ctp.admin.start", "ctp.admin"};
        super.senderMustBePlayer = false;
        super.minParameters = 3;
        super.maxParameters = 3;
        super.usageTemplate = "/ctp start <arena>";
    }

    @Override
    public void perform() {
        if (ctp.getArenas().isEmpty()) {
            sendMessage(ChatColor.RED + "There are currently no arenas, please create one first.");
            return;
        }
        
        if(ctp.getArena(parameters.get(2)) == null) {
        	sendMessage(ChatColor.RED + "Please enter a valid arena name to start.");
        	return;
        }
        
        if (ctp.getArena(parameters.get(2)).getLobby() == null) {
            sendMessage(ChatColor.RED + "Please create arena lobby");
            return;
        }
        
        Lobby lobby = ctp.getArena(parameters.get(2)).getLobby();
        int readypeople = lobby.countReadyPeople();
            
        if (!ctp.isPreGame()) {
            sendMessage(ChatColor.RED + "A game has already been started.");
            return;
        }
        
        // The maximum number of players must be greater than the players already playing.
        if (ctp.getArena(parameters.get(2)).getMaxPlayers() > ctp.getArena(parameters.get(2)).getPlayersPlaying(ctp).size()) {                
            if (ctp.getArena(parameters.get(2)).getConfigOptions().exactTeamMemberCount) {
                if (readypeople / ctp.getArena(parameters.get(2)).getTeams().size() >= 1 && readypeople >= ctp.getArena(parameters.get(2)).getMinPlayers()) {
                    if (lobby.hasUnreadyPeople()) {
                        String message = readypeople % ctp.getArena(parameters.get(2)).getTeams().size() == 1 ? "Starting game." : "Starting game. Caution: Someone may be left at lobby due to uneven teams.";
                        sendMessage(ChatColor.GREEN + message);
                        ctp.playerListener.moveToSpawns();
                    } else {
                        sendMessage(ChatColor.RED + "There are unready people: " + lobby.getUnreadyPeople());
                        return;
                    }
                }
            } else if ((readypeople == ctp.playerData.size()) && readypeople >= ctp.getArena(parameters.get(2)).getMinPlayers()) {
                sendMessage(ChatColor.GREEN + "Starting game.");
                ctp.playerListener.moveToSpawns();
            }
        } else {
            sendMessage(ChatColor.RED + "The arena is full.");
            return;
        }
    }
}