package me.dalton.capturethepoints.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionManagement {    
    /**
     * The player to remove all the potions effects for.
     * 
     * @param player The player in which to remove potion effects from.
     */
    public static void removeAllEffects(Player player) {
         for (PotionEffect pef : player.getActivePotionEffects()) {
         	player.removePotionEffect(pef.getType());
         }
    }
    
    /**
     * Provides a way to store the potion effects from the player.
     * 
     * @param player Player to store the effects for.
     * @return A List of the potion effects.
     */
	public static List<PotionEffect> storePlayerPotionEffects(Player player) {
        List<PotionEffect> effects = new ArrayList<PotionEffect>();
        
        Collection<PotionEffect> potions = player.getActivePotionEffects();
        
        for (PotionEffect potion : potions) {
        	effects.add(potion);
        }
        
        return effects;
    }
	
	/**
	 * Provides a way to restore the potions that the player had.
	 * 
	 * @param player The player who we need to restore them back to.
	 * @param effects A List<CTPPotionEffect> of the potions that need to be restored.
	 */
	public static void restorePotionEffects(Player player, List<PotionEffect> effects) {
		for(PotionEffect effect : effects) {
			player.addPotionEffect(effect);
		}
	}
	
	/**
	 * Provides an easy to access way of seeing if a potion effect is harmful or not.
	 * 
	 * @param effect The potioneffect to check.
	 * @return True if it harms the player, false if not.
	 */
    public static boolean isHarmful(PotionEffect effect) {
		
    	PotionEffectType type = effect.getType();

    	if (type.equals(PotionEffectType.HARM)){
    		return true;
    	}
    	if (type.equals(PotionEffectType.HEAL)){
    		return false;
    	}
    	if (type.equals(PotionEffectType.WEAKNESS)){
    		return true;
    	}
    	if (type.equals(PotionEffectType.REGENERATION)){
    		return false;
    	}
    	if (type.equals(PotionEffectType.INCREASE_DAMAGE)){
    		return false;
    	}
    	if (type.equals(PotionEffectType.SPEED)){
    		return false;
    	}
    	if (type.equals(PotionEffectType.SLOW)){
    		return true;
    	}
    	if (type.equals(PotionEffectType.POISON)){
    		return true;
    	}
    	
		return false;
	}
}
