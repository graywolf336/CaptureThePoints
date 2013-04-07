package me.dalton.capturethepoints.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.LanguageOptions;

public class LangTools {
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
		
		try {
            lang.options().copyDefaults(true);
            lang.save(langFile);
        } catch (IOException ex) {
            ex.printStackTrace();
            ctp.logSevere("Unable to save the language file, please see the StackTrace above.");
        }
	}
}
