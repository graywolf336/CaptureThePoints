package me.dalton.capturethepoints.util;

import me.dalton.capturethepoints.CaptureThePoints;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Permissions {
    /**  
     * Test whether a command sender can use this CTP command.
     * 
     * @param sender The sender issuing the command
     * @param notOpCommand Set to true if anyone can use the command, else false if the command issuer has to be an op or CTP admin.
     * @param permissions The permissions to check against that are associated with the command.
     * @return True if sender has permission, else false. 
     */
    public static boolean canAccess (CommandSender sender, boolean notOpCommand, String[] permissions) {
        if (sender instanceof ConsoleCommandSender) {
            return true;
        } else if (!(sender instanceof Player)) {
            return false;
        } else {
            return canAccess((Player) sender, notOpCommand, permissions);
        }
    }

    /**  
     * Test whether a player can use this CTP command.
     * 
     * @param p The player issuing the command
     * @param notOpCommand Set to true if anyone can use the command, else false if the command issuer has to be an op or CTP admin.
     * @param permissions The permissions to check against that are associated with the command.
     * @return True if player has permission, else false. 
     */
    public static boolean canAccess (Player p, boolean notOpCommand, String[] permissions) {
        if (permissions == null) {
            return true;
        }

        if (CaptureThePoints.UsePermissions) {
            for (String perm : permissions) {
                if (CaptureThePoints.permission.has(p, perm)) {
                    return true;
                }
            }
        } else {
            if (notOpCommand)
                return true;
            else
            	return p.isOp();
        }
        
        return false; //fail safe
    }
}
