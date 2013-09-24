package me.dalton.capturethepoints.commands;

import me.dalton.capturethepoints.CaptureThePoints;

import org.bukkit.entity.Player;

public class JoinAllCommand extends CTPCommand {
   
    /** Grabs EVERYONE on the server and puts them into a new ctp game. Stops any already in progress. */
    public JoinAllCommand(CaptureThePoints instance) {
        super.ctp = instance;
        super.aliases.add("joinall");
        super.aliases.add("jall");
        super.notOpCommand = false;
        super.requiredPermissions = new String[]{"ctp.*", "ctp.admin", "ctp.admin.joinall"};
        super.senderMustBePlayer = false;
        super.minParameters = 2;
        super.maxParameters = 2;
        super.usageTemplate = "/ctp joinall";
    }

    @Override
    public void perform() {
        if (sender instanceof Player) {
            String error = ctp.getArenaMaster().checkArena(ctp.getArenaMaster().getSelectedArena(), player);
            if (!error.isEmpty()) {
                sendMessage(error);
                return;
            }
        } else {
            if (ctp.getArenaMaster().getSelectedArena() == null) {
                sendMessage(ctp.getLanguage().checks_NO_ARENAS);
                return;
            }
            if (ctp.getArenaMaster().getSelectedArena().getLobby() == null) {
                sendMessage(ctp.getLanguage().checks_NO_LOBBY.replaceAll("%AN", ctp.getArenaMaster().getSelectedArena().getName()));
                return;
            }
        }
            
        if (ctp.getArenaMaster().getSelectedArena().getStatus().isRunning())
            ctp.getArenaMaster().getSelectedArena().endGame(false, false);//Don't give rewards as the game ended prematurely.
        
        int numberofplayers = ctp.getServer().getOnlinePlayers().length;
        ctp.getArenaMaster().chooseSuitableArena(numberofplayers); // Choose a suitable arena based on the number of players on the server.

        for (Player p : ctp.getServer().getOnlinePlayers())
        	ctp.getArenaMaster().moveToLobby(ctp.getArenaMaster().getSelectedArena(), p);

        return;
    }
}