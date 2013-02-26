package me.dalton.capturethepoints.commands;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.Arena;
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
        if (ctp.getArenaMaster().getArenas().isEmpty()) {
            sendMessage(ChatColor.RED + "There are currently no arenas, please create one first.");
            return;
        }
        
        if(ctp.getArenaMaster().getArena(parameters.get(2)) == null) {
        	sendMessage(ChatColor.RED + "Please enter a valid arena name to start.");
        	return;
        }
        
        if (ctp.getArenaMaster().getArena(parameters.get(2)).getLobby() == null) {
            sendMessage(ChatColor.RED + "Please create arena lobby");
            return;
        }
        
        Arena arena = ctp.getArenaMaster().getArena(parameters.get(2));
        Lobby lobby = ctp.getArenaMaster().getArena(parameters.get(2)).getLobby();
        int readypeople = lobby.countReadyPeople();
            
        if (!arena.isPreGame()) {
            sendMessage(ChatColor.RED + "A game has already been started.");
            return;
        }
        
        // The maximum number of players must be greater than the players already playing.
        if (arena.getMaxPlayers() > arena.getPlayersPlaying().size()) {                
            if (arena.getConfigOptions().exactTeamMemberCount) {
                if (readypeople / arena.getTeams().size() >= 1 && readypeople >= arena.getMinPlayers()) {
                    if (lobby.hasUnreadyPeople()) {
                        String message = readypeople % arena.getTeams().size() == 1 ? "Starting game." : "Starting game. Caution: Someone may be left at lobby due to uneven teams.";
                        sendMessage(ChatColor.GREEN + message);
                        ctp.getArenaUtil().moveToSpawns(arena);
                    } else {
                        sendMessage(ChatColor.RED + "There are unready people: " + lobby.getUnreadyPeople());
                        return;
                    }
                }
            } else if ((readypeople == arena.getPlayersData().size()) && readypeople >= arena.getMinPlayers()) {
                sendMessage(ChatColor.GREEN + "Starting game.");
                ctp.getArenaUtil().moveToSpawns(arena);
            }
        } else {
            sendMessage(ChatColor.RED + "The arena is full.");
            return;
        }
    }
}