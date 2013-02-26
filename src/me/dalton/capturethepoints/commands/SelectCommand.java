package me.dalton.capturethepoints.commands;

import java.io.IOException;
import me.dalton.capturethepoints.CaptureThePoints;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class SelectCommand extends CTPCommand {
   
    /** Allows admin to select an arena to play. */
    public SelectCommand(CaptureThePoints instance) {
        super.ctp = instance;
        super.aliases.add("setarena");
        super.aliases.add("selectarena");
        super.aliases.add("select");
        super.aliases.add("arena");
        super.notOpCommand = false;
        super.requiredPermissions = new String[]{"ctp.*", "ctp.admin", "ctp.admin.setarena", "ctp.admin.select", "ctp.admin.selectarena"};
        super.senderMustBePlayer = false;
        super.minParameters = 3;
        super.maxParameters = 3;
        super.usageTemplate = "/ctp select <Arena name>";
    }

    @Override
    public void perform() {
        String newarena = parameters.get(2);
        
        if (!ctp.getArenaMaster().isArena(newarena)) {
            sendMessage(ChatColor.RED + "Could not load arena " + ChatColor.GOLD + newarena + ChatColor.RED + " because the name cannot be found. Check your config file and existing arenas.");
            return;
        }

        if (ctp.getArenaMaster().getSelectedArena() != null && !ctp.getArenaMaster().getSelectedArena().getName().isEmpty()) {
            sendMessage(ChatColor.GREEN + "Changed selected arena from " + ctp.getArenaMaster().getSelectedArena().getName() + " to " + newarena + " to play.");
        } else {
            sendMessage(ChatColor.GREEN + "Selected " + newarena + " for playing.");
        }
        sendMessage(ChatColor.GREEN + "If you wanted to edit this arena instead, use " +ChatColor.WHITE+ "/ctp build selectarena <arena>");
        
        ctp.getArenaMaster().setSelectedArena(newarena);

        FileConfiguration config = ctp.getConfigTools().load();
        config.addDefault("Arena", newarena);
        try {
            config.options().copyDefaults(true);
            config.save(ctp.getGlobalConfig());
        } catch (IOException ex) {
            ex.printStackTrace();
            ctp.logSevere("Unable to save the global config file, please see the above StackTrace.");
        }

        return;
    }
}