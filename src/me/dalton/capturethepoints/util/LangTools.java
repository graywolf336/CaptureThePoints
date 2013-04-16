package me.dalton.capturethepoints.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.LanguageOptions;

public class LangTools {
	private static String ch = "checks.";
	
	public static String getColorfulMessage(String message) {
		return  message.replaceAll("(?i)&([0-9abcdefklmnor])", "\u00A7$1");
	}
	
	public static LanguageOptions getLanguageOptions(CaptureThePoints ctp) {
		setLanguageOptions(ctp, new File(ctp.getMainDirectory() + File.separator + "language_" + ctp.getGlobalConfigOptions().language + ".yml"));
		
		FileConfiguration lang = ctp.getConfigTools().load(new File(ctp.getMainDirectory() + File.separator + "language_" + ctp.getGlobalConfigOptions().language + ".yml"));
		LanguageOptions lo = new LanguageOptions();
		
		lo.GAME_NOT_STARTED = getColorfulMessage(lang.getString("GAME_NOT_STARTED", lo.GAME_NOT_STARTED));
		lo.GAME_STARTED = getColorfulMessage(lang.getString("GAME_STARTED", lo.GAME_STARTED));
		lo.GAME_ENDED = getColorfulMessage(lang.getString("GAME_ENDED", lo.GAME_ENDED));
		lo.PLAYER_JOIN = getColorfulMessage(lang.getString("PLAYER_JOIN", lo.PLAYER_JOIN));
		lo.PLAYER_LEFT = getColorfulMessage(lang.getString("PLAYER_LEFT", lo.PLAYER_LEFT));
		lo.LOBBY_JOIN = getColorfulMessage(lang.getString("LOBBY_JOIN", lo.LOBBY_JOIN));
		lo.READY_UP_REMINDER = getColorfulMessage(lang.getString("READY_UP_REMINDER", lo.READY_UP_REMINDER));
		lo.NOT_READY_KICK = getColorfulMessage(lang.getString("NOT_READY_KICK", lo.NOT_READY_KICK));
		lo.WINS = getColorfulMessage(lang.getString("WINS", lo.WINS));
		lo.CAPTURED = getColorfulMessage(lang.getString("CAPTURED", lo.CAPTURED));
		lo.LOST = getColorfulMessage(lang.getString("LOST", lo.LOST));
		lo.POINTS = getColorfulMessage(lang.getString("POINTS", lo.POINTS));
		lo.FINAL_SCORE = getColorfulMessage(lang.getString("FINAL_SCORE", lo.FINAL_SCORE));
		lo.FINAL_POINTS = getColorfulMessage(lang.getString("FINAL_POINTS", lo.FINAL_POINTS));
		lo.TIE_POINTS = getColorfulMessage(lang.getString("TIE_POINTS", lo.TIE_POINTS));
		lo.TIE_SCORE = getColorfulMessage(lang.getString("TIE_SCORE", lo.TIE_SCORE));
		lo.FIRST_WORLD = getColorfulMessage(lang.getString("FIRST_WORLD", lo.FIRST_WORLD));
		lo.PREVIOUS_GAME_TERMINATED = getColorfulMessage(lang.getString("PREVIOUS_GAME_TERMINATED", lo.PREVIOUS_GAME_TERMINATED));
		lo.ARENA_NAME_LIST = getColorfulMessage(lang.getString("ARENA_NAME_LIST", lo.ARENA_NAME_LIST));
		lo.ALREADY_PLAYING = getColorfulMessage(lang.getString("ALREADY_PLAYING", lo.ALREADY_PLAYING));
		lo.NOT_PLAYING = getColorfulMessage(lang.getString("NOT_PLAYING", lo.NOT_PLAYING));
		lo.START_COUNTDOWN = getColorfulMessage(lang.getString("START_COUNTDOWN", lo.START_COUNTDOWN));
		lo.END_COUNTDOWN = getColorfulMessage(lang.getString("END_COUNTDOWN", lo.END_COUNTDOWN));
		lo.NO_PERMISSION = getColorfulMessage(lang.getString("NO_PERMISSION", lo.NO_PERMISSION));
		lo.TEAM_BALANCE_MOVE_TO_LOBBY = getColorfulMessage(lang.getString("TEAM_BALANCE_MOVE_TO_LOBBY", lo.TEAM_BALANCE_MOVE_TO_LOBBY));
		lo.TEAM_BALANCE_CHANGE_TEAMS = getColorfulMessage(lang.getString("TEAM_BALANCE_CHANGE_TEAMS", lo.TEAM_BALANCE_CHANGE_TEAMS));
		lo.GAME_ENDED_TOO_FEW_PLAYERS = getColorfulMessage(lang.getString("GAME_ENDED_TOO_FEW_PLAYERS", lo.GAME_ENDED_TOO_FEW_PLAYERS));
		lo.NO_PLAYERS_LEFT = getColorfulMessage(lang.getString("NO_PLAYERS_LEFT", lo.NO_PLAYERS_LEFT));
		
		//Messages for checking the arenas.
		lo.checks_NO_ARENA_BY_NAME = getColorfulMessage(lang.getString(ch + "NO_ARENA_BY_NAME", lo.checks_NO_ARENA_BY_NAME));
		lo.checks_NO_ARENAS = getColorfulMessage(lang.getString(ch + "NO_ARENAS", lo.checks_NO_ARENAS));
		lo.checks_NO_ARENA_NAME = getColorfulMessage(lang.getString(ch + "NO_ARENA_NAME", lo.checks_NO_ARENA_NAME));
		lo.checks_INCORRECT_WORLD_SETUP_ADMIN = getColorfulMessage(lang.getString(ch + "INCORRECT_WORLD_SETUP_ADMIN", lo.checks_INCORRECT_WORLD_SETUP_ADMIN));
		lo.checks_INCORRECT_SETUP_USER = getColorfulMessage(lang.getString(ch + "INCORRECT_SETUP_USER", lo.checks_INCORRECT_SETUP_USER));
		lo.checks_NO_LOBBY = getColorfulMessage(lang.getString(ch + "NO_LOBBY", lo.checks_NO_LOBBY));
		lo.checks_NO_BOUNDARIES = getColorfulMessage(lang.getString(ch + "NO_BOUNDARIES", lo.checks_NO_BOUNDARIES));
		lo.checks_NO_SUITABLE_WORLD = getColorfulMessage(lang.getString(ch + "NO_SUITABLE_WORLD", lo.checks_NO_SUITABLE_WORLD));
		lo.checks_NO_TEAM_SPAWNS = getColorfulMessage(lang.getString(ch + "NO_TEAM_SPAWNS", lo.checks_NO_TEAM_SPAWNS));
		lo.checks_NO_WORLD_FOUND = getColorfulMessage(lang.getString(ch + "NO_WORLD_FOUND", lo.checks_NO_WORLD_FOUND));
		lo.checks_NOT_ENOUGH_TEAM_SPAWNS = getColorfulMessage(lang.getString(ch + "NOT_ENOUGH_TEAM_SPAWNS", lo.checks_NOT_ENOUGH_TEAM_SPAWNS));
		lo.checks_INCORRECT_SPAWN_LOCATION = getColorfulMessage(lang.getString(ch + "INCORRECT_SPAWN_LOCATION", lo.checks_INCORRECT_SPAWN_LOCATION));
		lo.checks_NO_POINTS = getColorfulMessage(lang.getString(ch + "NO_POINTS", lo.checks_NO_POINTS));
		lo.checks_EDIT_MODE = getColorfulMessage(lang.getString(ch + "EDIT_MODE", lo.checks_EDIT_MODE));
		lo.checks_DISABLED = getColorfulMessage(lang.getString(ch + "DISABLED", lo.checks_DISABLED));
		lo.checks_FULL_ARENA = getColorfulMessage(lang.getString(ch + "FULL_ARENA", lo.checks_FULL_ARENA));
		lo.checks_GAME_ALREADY_STARTED = getColorfulMessage(lang.getString(ch + "GAME_ALREADY_STARTED", lo.checks_GAME_ALREADY_STARTED));
		lo.checks_PLAYER_IN_VEHICLE = getColorfulMessage(lang.getString(ch + "PLAYER_IN_VEHICLE", lo.checks_PLAYER_IN_VEHICLE));
		lo.checks_PLAYER_SLEEPING = getColorfulMessage(lang.getString(ch + "PLAYER_SLEEPING", lo.checks_PLAYER_SLEEPING));
		lo.checks_NO_EXISTING_TEAMS = getColorfulMessage(lang.getString(ch + "NO_EXISTING_TEAMS", lo.checks_NO_EXISTING_TEAMS));
		
		//Joining messages
		lo.SUCCESSFUL_PAYING_FOR_JOINING = getColorfulMessage(lang.getString("SUCCESSFUL_PAYING_FOR_JOINING", lo.SUCCESSFUL_PAYING_FOR_JOINING));
		lo.NOT_ENOUGH_MONEY_FOR_JOINING = getColorfulMessage(lang.getString("NOT_ENOUGH_MONEY_FOR_JOINING", lo.NOT_ENOUGH_MONEY_FOR_JOINING));
		
		//Etc messages
		lo.CHANGING_ROLES_TOO_FAST = getColorfulMessage(lang.getString("CHANGING_ROLES_TOO_FAST", lo.CHANGING_ROLES_TOO_FAST));
		lo.PLAYERS = getColorfulMessage(lang.getString("PLAYERS", lo.PLAYERS));
		lo.TEAMS = getColorfulMessage(lang.getString("TEAMS", lo.TEAMS));
		lo.TRY = getColorfulMessage(lang.getString("TRY", lo.TRY));
		
		return lo;
	}
	
	private static void setLanguageOptions(CaptureThePoints ctp, File langFile) {
		FileConfiguration lang = ctp.getConfigTools().load(langFile);
		LanguageOptions lo = new LanguageOptions();
		
		if(!lang.contains("GAME_NOT_STARTED"))
			lang.set("GAME_NOT_STARTED", lo.GAME_NOT_STARTED);
		if(!lang.contains("GAME_STARTED"))
			lang.set("GAME_STARTED", lo.GAME_STARTED);
		if(!lang.contains("GAME_ENDED"))
			lang.set("GAME_ENDED", lo.GAME_ENDED);
		if(!lang.contains("PLAYER_JOIN"))
			lang.set("PLAYER_JOIN", lo.PLAYER_JOIN);
		if(!lang.contains("PLAYER_LEFT"))
			lang.set("PLAYER_LEFT", lo.PLAYER_LEFT);
		if(!lang.contains("LOBBY_JOIN"))
			lang.set("LOBBY_JOIN", lo.LOBBY_JOIN);
		if(!lang.contains("READY_UP_REMINDER"))
			lang.set("READY_UP_REMINDER", lo.READY_UP_REMINDER);
		if(!lang.contains("NOT_READY_KICK"))
			lang.set("NOT_READY_KICK", lo.NOT_READY_KICK);
		if(!lang.contains("WINS"))
			lang.set("WINS", lo.WINS);
		if(!lang.contains("CAPTURED"))
			lang.set("CAPTURED", lo.CAPTURED);
		if(!lang.contains("LOST"))
			lang.set("LOST", lo.LOST);
		if(!lang.contains("POINTS"))
			lang.set("POINTS", lo.POINTS);
		if(!lang.contains("FINAL_SCORE"))
			lang.set("FINAL_SCORE", lo.FINAL_SCORE);
		if(!lang.contains("FINAL_POINTS"))
			lang.set("FINAL_POINTS", lo.FINAL_POINTS);
		if(!lang.contains("TIE_POINTS"))
			lang.set("TIE_POINTS", lo.TIE_POINTS);
		if(!lang.contains("TIE_SCORE"))
			lang.set("TIE_SCORE", lo.TIE_SCORE);
		if(!lang.contains("FIRST_WORLD"))
			lang.set("FIRST_WORLD", lo.FIRST_WORLD);
		if(!lang.contains("PREVIOUS_GAME_TERMINATED"))
			lang.set("PREVIOUS_GAME_TERMINATED", lo.PREVIOUS_GAME_TERMINATED);
		if(!lang.contains("ARENA_NAME_LIST"))
			lang.set("ARENA_NAME_LIST", lo.ARENA_NAME_LIST);
		if(!lang.contains("ALREADY_PLAYING"))
			lang.set("ALREADY_PLAYING", lo.ALREADY_PLAYING);
		if(!lang.contains("NOT_PLAYING"))
			lang.set("NOT_PLAYING", lo.NOT_PLAYING);
		if(!lang.contains("START_COUNTDOWN"))
			lang.set("START_COUNTDOWN", lo.START_COUNTDOWN);
		if(!lang.contains("END_COUNTDOWN"))
			lang.set("END_COUNTDOWN", lo.END_COUNTDOWN);
		if(!lang.contains("NO_PERMISSION"))
			lang.set("NO_PERMISSION", lo.NO_PERMISSION);
		if(!lang.contains("TEAM_BALANCE_MOVE_TO_LOBBY"))
			lang.set("TEAM_BALANCE_MOVE_TO_LOBBY", lo.TEAM_BALANCE_MOVE_TO_LOBBY);
		if(!lang.contains("TEAM_BALANCE_CHANGE_TEAMS"))
			lang.set("TEAM_BALANCE_CHANGE_TEAMS", lo.TEAM_BALANCE_CHANGE_TEAMS);
		if(!lang.contains("GAME_ENDED_TOO_FEW_PLAYERS"))
			lang.set("GAME_ENDED_TOO_FEW_PLAYERS", lo.GAME_ENDED_TOO_FEW_PLAYERS);
		if(!lang.contains("NO_PLAYERS_LEFT"))
			lang.set("NO_PLAYERS_LEFT", lo.NO_PLAYERS_LEFT);
		
		//Messages for checking the arenas.
		if(!lang.contains(ch + "NO_ARENA_BY_NAME"))
			lang.set(ch + "NO_ARENA_BY_NAME", lo.checks_NO_ARENA_BY_NAME);
		if(!lang.contains(ch + "NO_ARENAS"))
			lang.set(ch + "NO_ARENAS", lo.checks_NO_ARENAS);
		if(!lang.contains(ch + "NO_ARENA_NAME"))
			lang.set(ch + "NO_ARENA_NAME", lo.checks_NO_ARENA_NAME);
		if(!lang.contains(ch + "INCORRECT_WORLD_SETUP_ADMIN"))
			lang.set(ch + "INCORRECT_WORLD_SETUP_ADMIN", lo.checks_INCORRECT_WORLD_SETUP_ADMIN);
		if(!lang.contains(ch + "INCORRECT_SETUP_USER"))
			lang.set(ch + "INCORRECT_SETUP_USER", lo.checks_INCORRECT_SETUP_USER);
		if(!lang.contains(ch + "NO_LOBBY"))
			lang.set(ch + "NO_LOBBY", lo.checks_NO_LOBBY);
		if(!lang.contains(ch + "NO_BOUNDARIES"))
			lang.set(ch + "NO_BOUNDARIES", lo.checks_NO_BOUNDARIES);
		if(!lang.contains(ch + "NO_SUITABLE_WORLD"))
			lang.set(ch + "NO_SUITABLE_WORLD", lo.checks_NO_SUITABLE_WORLD);
		if(!lang.contains(ch + "NO_TEAM_SPAWNS"))
			lang.set(ch + "NO_TEAM_SPAWNS", lo.checks_NO_TEAM_SPAWNS);
		if(!lang.contains(ch + "NO_WORLD_FOUND"))
			lang.set(ch + "NO_WORLD_FOUND", lo.checks_NO_WORLD_FOUND);
		if(!lang.contains(ch + "NOT_ENOUGH_TEAM_SPAWNS"))
			lang.set(ch + "NOT_ENOUGH_TEAM_SPAWNS", lo.checks_NOT_ENOUGH_TEAM_SPAWNS);
		if(!lang.contains(ch + "INCORRECT_SPAWN_LOCATION"))
			lang.set(ch + "INCORRECT_SPAWN_LOCATION", lo.checks_INCORRECT_SPAWN_LOCATION);
		if(!lang.contains(ch + "NO_POINTS"))
			lang.set(ch + "NO_POINTS", lo.checks_NO_POINTS);
		if(!lang.contains(ch + "EDIT_MODE"))
			lang.set(ch + "EDIT_MODE", lo.checks_EDIT_MODE);
		if(!lang.contains(ch + "DISABLED"))
			lang.set(ch + "DISABLED", lo.checks_DISABLED);
		if(!lang.contains(ch + "FULL_ARENA"))
			lang.set(ch + "FULL_ARENA", lo.checks_FULL_ARENA);
		if(!lang.contains(ch + "GAME_ALREADY_STARTED"))
			lang.set(ch + "GAME_ALREADY_STARTED", lo.checks_GAME_ALREADY_STARTED);
		if(!lang.contains(ch + "PLAYER_IN_VEHICLE"))
			lang.set(ch + "PLAYER_IN_VEHICLE", lo.checks_PLAYER_IN_VEHICLE);
		if(!lang.contains(ch + "PLAYER_SLEEPING"))
			lang.set(ch + "PLAYER_SLEEPING", lo.checks_PLAYER_SLEEPING);
		if(!lang.contains(ch + "NO_EXISTING_TEAMS"))
			lang.set(ch + "NO_EXISTING_TEAMS", lo.checks_NO_EXISTING_TEAMS);
		
		//Joining messages
		if(!lang.contains("SUCCESSFUL_PAYING_FOR_JOINING"))
			lang.set("SUCCESSFUL_PAYING_FOR_JOINING", lo.SUCCESSFUL_PAYING_FOR_JOINING);
		if(!lang.contains("NOT_ENOUGH_MONEY_FOR_JOINING"))
			lang.set("NOT_ENOUGH_MONEY_FOR_JOINING", lo.NOT_ENOUGH_MONEY_FOR_JOINING);
		
		//Etc messages
		if(!lang.contains("CHANGING_ROLES_TOO_FAST"))
			lang.set("CHANGING_ROLES_TOO_FAST", lo.CHANGING_ROLES_TOO_FAST);
		if(!lang.contains("PLAYERS"))
			lang.set("PLAYERS", lo.PLAYERS);
		if(!lang.contains("TEAMS"))
			lang.set("TEAMS", lo.TEAMS);
		if(!lang.contains("TRY"))
			lang.set("TRY", lo.TRY);
		
		try {
            lang.options().copyDefaults(true);
            lang.save(langFile);
        } catch (IOException ex) {
            ex.printStackTrace();
            ctp.logSevere("Unable to save the language file, please see the StackTrace above.");
        }
	}
}
