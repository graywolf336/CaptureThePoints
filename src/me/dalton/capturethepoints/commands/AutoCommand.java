package me.dalton.capturethepoints.commands;

import me.dalton.capturethepoints.CaptureThePoints;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class AutoCommand extends CTPCommand {
    private String worldname = "";
    
    /** This command will bring all players on a world into a random lobby which is guaranteed to hold everyone (if not, use the already selected arena) */
    public AutoCommand(CaptureThePoints instance) {
        this(instance, "");
    }
    
    /** If the world name is supplied */
    public AutoCommand(CaptureThePoints instance, String worldname) {
        this.worldname = worldname;
        super.ctp = instance;
        super.aliases.add("auto");
        super.notOpCommand = false;
        super.requiredPermissions = new String[]{"ctp.*", "ctp.admin", "ctp.admin.auto"};
        super.senderMustBePlayer = false;
        super.minParameters = 3;
        super.maxParameters = 3;
        super.usageTemplate = "/ctp auto <worldname|this>";
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
        
        if (this.worldname.isEmpty())
            this.worldname = parameters.get(2);

        if (this.worldname.equalsIgnoreCase("this") && player != null)
            this.worldname = player.getWorld().getName();

        World world = ctp.getServer().getWorld(worldname);
        if (world == null) {
            sendMessage(ctp.getLanguage().checks_NO_WORLD_FOUND.replaceAll("%WN", worldname));
            sendMessage(ctp.getLanguage().FIRST_WORLD.replaceAll("%SFW", ctp.getServer().getWorlds().get(0).getName()));
            return;
        }

        if (ctp.getArenaMaster().hasSuitableArena(world.getPlayers().size())) {
            ctp.getArenaMaster().chooseSuitableArena(world.getPlayers().size()); // Choose a suitable arena based on the number of players in the world.
        } else {
            sendMessage(ctp.getLanguage().checks_NO_SUITABLE_WORLD.replaceAll("%WPS", String.valueOf(world.getPlayers().size())));
            return;
        }
        
        if (ctp.getArenaMaster().getSelectedArena().getStatus().isRunning()) {
            sendMessage(ctp.getLanguage().PREVIOUS_GAME_TERMINATED);
            ctp.getArenaMaster().getSelectedArena().endGame(false, false);//Don't give rewards as we have ended the game prematurely.
        }

        for (Player p : world.getPlayers())
            ctp.getArenaMaster().getSelectedArena().joinLobby(p);

        return;
    }
}