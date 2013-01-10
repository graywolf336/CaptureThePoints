package me.dalton.capturethepoints.util;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.Team;

public class ArenaUtils {
    /**
     * Returns a Team from the color provided.
     * 
     * @param ctp CaptureThePoints instance
     * @param arena The arena to check
     * @param color The color of the team to get
     * @return The Team from the color given.
     * @since 1.5.0-b122
     */
    public static Team getTeamFromColor(CaptureThePoints ctp, String arena, String color) {
    	for (Team t : ctp.getArena(arena).getTeams())
    		if(t.getColor().equalsIgnoreCase(color))
    			return t;
    	
    	return null;
    }
}
