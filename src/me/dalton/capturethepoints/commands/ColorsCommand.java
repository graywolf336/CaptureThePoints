package me.dalton.capturethepoints.commands;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.Team;

import org.bukkit.ChatColor;

public class ColorsCommand extends CTPCommand {
    
    /** Fetches the available teams, and the players on each team if a game has started. */
    public ColorsCommand(CaptureThePoints instance) {
        super.ctp = instance;
        super.aliases.add("colors");
        super.aliases.add("colours");
        super.aliases.add("players");
        super.aliases.add("teams");
        super.notOpCommand = true;
        super.requiredPermissions = new String[]{"ctp.*", "ctp.play", "ctp.admin", "ctp.colors"};
        super.senderMustBePlayer = true;
        super.minParameters = 2;
        super.maxParameters = 3;
        super.usageTemplate = "/ctp colors <arena>";
    }

    @Override
    public void perform() {
    	if(parameters.size() == 2) {
    		if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin"})) {
                sendMessage(ChatColor.RED + "Admin: " + ChatColor.BLUE + "Available team colors:"); // Kj -- typo ;)
                sendMessage(ChatColor.GREEN + "WHITE, LIGHTGRAY, GRAY, BLACK, RED, ORANGE, YELLOW, LIME, LIGHTBLUE, GREEN, CYAN, BLUE, PURPLE, MAGENTA, PINK, BROWN");
                return; //if only colors, then just send colors
            }else {
            	usageError();
            	return;
            }
    	}

        if(ctp.getArenaMaster().getArenas().isEmpty()) {
        	sendMessage(ctp.getLanguage().checks_NO_ARENAS);
        	return;
        }
        
        if(ctp.getArenaMaster().getArena(parameters.get(2)) == null) {
        	 sendMessage(ctp.getLanguage().checks_NO_ARENA_NAME);
        	 sendMessage(ctp.getLanguage().ARENA_NAME_LIST);
        	 for(Arena a : ctp.getArenaMaster().getArenas())
        		 sendMessage(ChatColor.GOLD + "  - " + a.getName());
        	 return;
        }
        
        if (ctp.getArenaMaster().getArena(parameters.get(2)).getTeams().size() > 0) {
            String theteams = "";
            for (int i = 0; i < ctp.getArenaMaster().getArena(parameters.get(2)).getTeams().size(); i++)
                theteams = theteams + ctp.getArenaMaster().getArena(parameters.get(2)).getTeams().get(i).getChatColor() + ctp.getArenaMaster().getArena(parameters.get(2)).getTeams().get(i).getColor() + ChatColor.WHITE + ", "; // Kj -- added colour, changed team to team color (its name)
            
            sendMessage(ctp.getLanguage().TEAMS + ": " + ChatColor.GREEN + theteams.toLowerCase().substring(0, theteams.length() - 2)); // minus ", " from end

            String playernames = "";
            ChatColor cc = ChatColor.GREEN;
            for (Team aTeam : ctp.getArenaMaster().getArena(parameters.get(2)).getTeams()) {
                cc = aTeam.getChatColor();
                playernames += cc;
                playernames += aTeam.getTeamPlayerNames(ctp.getArenaMaster().getArena(parameters.get(2)));
                playernames += " ";
            }
            
            sendMessage(ChatColor.GREEN
            		+ "" + ctp.getArenaMaster().getArena(parameters.get(2)).getPlayersData().size()
            		+ ctp.getLanguage().PLAYERS + ": " + playernames);
            return;
        }

        sendMessage(ctp.getLanguage().checks_NO_EXISTING_TEAMS);
        return;
    }
}
