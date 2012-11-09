package me.dalton.capturethepoints.commands;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.PlayerData;

import org.bukkit.ChatColor;

public class StatsCommand extends CTPCommand {
   
    /** Allows players to view their kills, deaths, K/D, and money in-game. */
    public StatsCommand(CaptureThePoints instance) {
        super.ctp = instance;
        super.aliases.add("stats");
        super.aliases.add("stat");
        super.aliases.add("info");
        super.notOpCommand = true;
        super.requiredPermissions = new String[]{"ctp.*", "ctp.play", "ctp.admin", "ctp.stats"};
        super.senderMustBePlayer = true;
        super.minParameters = 2;
        super.maxParameters = 2;
        super.usageTemplate = "/ctp stats";
    }

    @Override
    public void perform() {
        if (!ctp.blockListener.isAlreadyInGame(player.getName())) {
            sendMessage(ChatColor.RED + "You are not in a CTP game!");
            return;
        }
        
        PlayerData pdata = ctp.playerData.get(player.getName());
        ChatColor cc = pdata.getTeam().getChatColor(), white = ChatColor.WHITE, green = ChatColor.GREEN;
        
        sendMessage(cc + "Your Stats: ");
        sendMessage(cc + "  Kills: " + white + pdata.getKills() + " (Streak: " + pdata.getKillsInARow() + ")");
        sendMessage(cc + "  Deaths: " + white + pdata.getDeaths() + " (Streak: " + pdata.getDeathsInARow() + ")");
        
        double kd = 0D; 
        if (pdata.getDeaths() == 0) {
           // Avoid divding by 0
            kd = pdata.getKills();
       
        } else {
           // Calculate KD normally
            double kills = (double)pdata.getKills() * 100D; // Example: 4 kills -> 400
            double deaths = (double)pdata.getDeaths(); // Example: 3 deaths
            kd = Math.round(kills/deaths); // Example: (400 / 3) = 133.333... Rounded -> 133.
            kd /= 100; // Example: 133 -> 1.33.
        }
        
        ChatColor goodKD = ChatColor.WHITE;
        if (kd < 0.5) {
            goodKD = ChatColor.RED;
        } else if (kd < 1.0) {
            goodKD = ChatColor.YELLOW;
        } else if (kd < 1.5) {
            goodKD = ChatColor.DARK_GREEN;
        } else {
            goodKD = ChatColor.GREEN;
        }
        
        sendMessage(cc + "  K/D: " + goodKD + String.valueOf(kd));
        sendMessage(cc + "  Money: " + green + pdata.getMoney());
        return;
    }
}