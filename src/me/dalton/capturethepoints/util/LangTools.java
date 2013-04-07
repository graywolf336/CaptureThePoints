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
		
		return lo;
	}
	
	private static void setLanguageOptions(CaptureThePoints ctp, File langFile) {
		FileConfiguration lang = ctp.getConfigTools().load(langFile);
		LanguageOptions lo = new LanguageOptions();
		
		if(!lang.contains("GAME_STARTED"))
			lang.set("GAME_STARTED", lo.GAME_STARTED);
		if(!lang.contains("GAME_ENDED"))
			lang.set("GAME_ENDED", lo.GAME_ENDED);
		
		try {
            lang.options().copyDefaults(true);
            lang.save(langFile);
        } catch (IOException ex) {
            ex.printStackTrace();
            ctp.logSevere("Unable to save the language file, please see the StackTrace above.");
        }
	}
}
