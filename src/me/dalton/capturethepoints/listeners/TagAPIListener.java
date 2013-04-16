package me.dalton.capturethepoints.listeners;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.PlayerData;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.kitteh.tag.PlayerReceiveNameTagEvent;
import org.kitteh.tag.TagAPI;

public class TagAPIListener implements Listener {
	private CaptureThePoints ctp;
	
	public TagAPIListener(CaptureThePoints plugin) {
		ctp = plugin;
	}
	
	@EventHandler
	public void onNameTag(PlayerReceiveNameTagEvent event) {
		if(!ctp.getArenaMaster().isPlayerInAnArena(event.getPlayer())) return; //if the player receiving this is not playing, don't send.
		if(!ctp.getArenaMaster().isPlayerInAnArena(event.getNamedPlayer())) return; //check if the player who's tag update we are getting is playing
		PlayerData pd = ctp.getArenaMaster().getPlayerData(event.getNamedPlayer());
		if(pd.getTeam() == null) return;
		
		event.setTag(pd.getTeam().getChatColor() + event.getNamedPlayer().getName());
	}
	
	public static void refreshTag(Player player) {
		TagAPI.refreshPlayer(player);
	}
}
