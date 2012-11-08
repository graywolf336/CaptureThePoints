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
        super.minParameters = 2;
        super.maxParameters = 2;
        super.usageTemplate = "/ctp start";
    }

    @Override
    public void perform() {
        if (ctp.mainArena == null) {
            sendMessage(ChatColor.RED + "Please create an arena first");
            return;
        }
        if (ctp.mainArena.getLobby() == null) {
            sendMessage(ChatColor.RED + "Please create arena lobby");
            return;
        }
        
        Lobby lobby = ctp.mainArena.getLobby();
        int readypeople = lobby.countReadyPeople();
            
        if (!ctp.isPreGame()) {
            sendMessage(ChatColor.RED + "A game has already been started.");
            return;
        }
        
        // The maximum number of players must be greater than the players already playing.
        if (ctp.mainArena.getMaxPlayers() > ctp.mainArena.getPlayersPlaying(ctp).size()) {                
            if (ctp.mainArena.getConfigOptions().exactTeamMemberCount) {
                if (readypeople / ctp.mainArena.getTeams().size() >= 1 && readypeople >= ctp.mainArena.getMinPlayers()) {
                    if (lobby.hasUnreadyPeople()) {
                        String message = readypeople % ctp.mainArena.getTeams().size() == 1 ? "Starting game." : "Starting game. Caution: Someone may be left at lobby due to uneven teams.";
                        sendMessage(ChatColor.GREEN + message);
                        ctp.playerListener.moveToSpawns();
                    } else {
                        sendMessage(ChatColor.RED + "There are unready people: " + lobby.getUnreadyPeople());
                        return;
                    }
                }
            } else if ((readypeople == ctp.playerData.size()) && readypeople >= ctp.mainArena.getMinPlayers()) {
                sendMessage(ChatColor.GREEN + "Starting game.");
                ctp.playerListener.moveToSpawns();
            }
        } else {
            sendMessage(ChatColor.RED + "The arena is full.");
            return;
        }
    }
}