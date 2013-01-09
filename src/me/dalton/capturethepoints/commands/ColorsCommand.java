package me.dalton.capturethepoints.commands;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.Team;
import me.dalton.capturethepoints.util.Permissions;

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
        super.minParameters = 3;
        super.maxParameters = 3;
        super.usageTemplate = "/ctp colors <arena>";
    }

    @Override
    public void perform() {       
        if (Permissions.canAccess(player, false, new String[]{"ctp.*", "ctp.admin"})) {
            sendMessage(ChatColor.RED + "Admin: " + ChatColor.BLUE + "Available team colors:"); // Kj -- typo ;)
            sendMessage(ChatColor.GREEN + "WHITE, LIGHTGRAY, GRAY, BLACK, RED, ORANGE, YELLOW, LIME, LIGHTBLUE, GREEN, CYAN, BLUE, PURPLE, MAGENTA, PINK, BROWN");
        }
        
        if(ctp.getArenas().isEmpty()) {
        	sendMessage(ChatColor.RED + "There are currently no arenas, please create one first.");
        	return;
        }
        
        if(ctp.getArena(parameters.get(2)) == null) {
        	 sendMessage(ChatColor.RED + "Please enter a valid arena name to start.");
        	 sendMessage(ChatColor.RED + "Try one of these arenas: ");
        	 for(String a : ctp.getArenas().keySet())
        		 sendMessage(ChatColor.GOLD + "  - " + a);
        	 return;
        }
        
        if (ctp.getArena(parameters.get(2)).getTeams().size() > 0) {
            String theteams = "";
            for (int i = 0; i < ctp.getArena(parameters.get(2)).getTeams().size(); i++) {
                theteams = theteams + ctp.getArena(parameters.get(2)).getTeams().get(i).getChatColor() + ctp.getArena(parameters.get(2)).getTeams().get(i).getColor() + ChatColor.WHITE + ", "; // Kj -- added colour, changed team to team color (its name)
            }
            
            sendMessage("Teams: " + ChatColor.GREEN + theteams.toLowerCase().substring(0, theteams.length() - 2)); // minus ", " from end

            String playernames = "";
            ChatColor cc = ChatColor.GREEN;
            for (Team aTeam : ctp.getArena(parameters.get(2)).getTeams()) {
                cc = aTeam.getChatColor();
                playernames += cc;
                playernames += aTeam.getTeamPlayerNames(ctp);
                playernames += " ";
            }
            
            sendMessage(ChatColor.GREEN + String.valueOf(ctp.playerData.size()) + " players: " + playernames);
            return;
        }

        sendMessage(ChatColor.BLUE + "There are no existing teams to join.");
        return;
    }
}
