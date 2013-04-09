package me.dalton.capturethepoints.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.LanguageOptions;

public class LangTools {
	private static String ch = "checks.";
	
	public static LanguageOptions getLanguageOptions(CaptureThePoints ctp) {
		setLanguageOptions(ctp, new File(ctp.getMainDirectory() + File.separator + "language_" + ctp.getGlobalConfigOptions().language + ".yml"));
		
		FileConfiguration lang = ctp.getConfigTools().load(new File(ctp.getMainDirectory() + File.separator + "language_" + ctp.getGlobalConfigOptions().language + ".yml"));
		LanguageOptions lo = new LanguageOptions();
		
		lo.GAME_STARTED = lang.getString("GAME_STARTED", lo.GAME_STARTED);
		lo.GAME_ENDED = lang.getString("GAME_ENDED", lo.GAME_ENDED);
		lo.WINS = lang.getString("WINS", lo.WINS);
		lo.CAPTURED = lang.getString("CAPTURED", lo.CAPTURED);
		lo.LOST = lang.getString("LOST", lo.LOST);
		lo.POINTS = lang.getString("POINTS", lo.POINTS);
		lo.FINAL_SCORE = lang.getString("FINAL_SCORE", lo.FINAL_SCORE);
		lo.FINAL_POINTS = lang.getString("FINAL_POINTS", lo.FINAL_POINTS);
		lo.TIE_POINTS = lang.getString("TIE_POINTS", lo.TIE_POINTS);
		lo.TIE_SCORE = lang.getString("TIE_SCORE", lo.TIE_SCORE);
		
		//Messages for checking the arenas.
		lo.checks_NO_ARENA_BY_NAME = lang.getString(ch + "NO_ARENA_BY_NAME", lo.checks_NO_ARENA_BY_NAME);
		lo.checks_NO_ARENAS = lang.getString(ch + "NO_ARENAS", lo.checks_NO_ARENAS);
		lo.checks_NO_ARENA_NAME = lang.getString(ch + "NO_ARENA_NAME", lo.checks_NO_ARENA_NAME);
		lo.checks_INCORRECT_WORLD_SETUP_ADMIN = lang.getString(ch + "INCORRECT_WORLD_SETUP_ADMIN", lo.checks_INCORRECT_WORLD_SETUP_ADMIN);
		lo.checks_INCORRECT_SETUP_USER = lang.getString(ch + "INCORRECT_SETUP_USER", lo.checks_INCORRECT_SETUP_USER);
		lo.checks_NO_LOBBY = lang.getString(ch + "NO_LOBBY", lo.checks_NO_LOBBY);
		lo.checks_NO_BOUNDARIES = lang.getString(ch + "NO_BOUNDARIES", lo.checks_NO_BOUNDARIES);
		lo.checks_NO_TEAM_SPAWNS = lang.getString(ch + "NO_TEAM_SPAWNS", lo.checks_NO_TEAM_SPAWNS);
		lo.checks_NOT_ENOUGH_TEAM_SPAWNS = lang.getString(ch + "NOT_ENOUGH_TEAM_SPAWNS", lo.checks_NOT_ENOUGH_TEAM_SPAWNS);
		lo.checks_INCORRECT_SPAWN_LOCATION = lang.getString(ch + "INCORRECT_SPAWN_LOCATION", lo.checks_INCORRECT_SPAWN_LOCATION);
		lo.checks_NO_POINTS = lang.getString(ch + "NO_POINTS", lo.checks_NO_POINTS);
		lo.checks_EDIT_MODE = lang.getString(ch + "EDIT_MODE", lo.checks_EDIT_MODE);
		lo.checks_DISABLED = lang.getString(ch + "DISABLED", lo.checks_DISABLED);
		lo.checks_FULL_ARENA = lang.getString(ch + "FULL_ARENA", lo.checks_FULL_ARENA);
		lo.checks_GAME_ALREADY_STARTED = lang.getString(ch + "GAME_ALREADY_STARTED", lo.checks_GAME_ALREADY_STARTED);
		
		return lo;
	}
	
	private static void setLanguageOptions(CaptureThePoints ctp, File langFile) {
		FileConfiguration lang = ctp.getConfigTools().load(langFile);
		LanguageOptions lo = new LanguageOptions();
		
		if(!lang.contains("GAME_STARTED"))
			lang.set("GAME_STARTED", lo.GAME_STARTED);
		if(!lang.contains("GAME_ENDED"))
			lang.set("GAME_ENDED", lo.GAME_ENDED);
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
		if(!lang.contains(ch + "NO_TEAM_SPAWNS"))
			lang.set(ch + "NO_TEAM_SPAWNS", lo.checks_NO_TEAM_SPAWNS);
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
		
		try {
            lang.options().copyDefaults(true);
            lang.save(langFile);
        } catch (IOException ex) {
            ex.printStackTrace();
            ctp.logSevere("Unable to save the language file, please see the StackTrace above.");
        }
	}
}
