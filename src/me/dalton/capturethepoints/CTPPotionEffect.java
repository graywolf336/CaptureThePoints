package me.dalton.capturethepoints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Humsas
 */
public class CTPPotionEffect {
    private int duration, strength;
    private PotionEffectType type;

    public CTPPotionEffect (int duration, int strength, PotionEffectType type) {
        this.duration = duration;
        this.strength = strength;
        this.type = type;
    }
    
    /**
     * The player to remove all the potions effects for.
     * 
     * @param player The player in which to remove potion effects from.
     */
    public static void removeAllEffectsNew(Player player) {
    	 Collection<PotionEffect> potions = player.getActivePotionEffects();
    	 
         for (PotionEffect pef : potions) {
         	removePotionEffectNew(player, pef.getType());
         }
    }
    
    /**
     * Provides a way to remove a potion effect from a player.
     * 
     * @param player Player in which to remove the PotionEffectType from.
     * @param type PotionEffectType to remove.
     */
    public static void removePotionEffectNew(Player player, PotionEffectType type) {
    	player.removePotionEffect(type);
    }
    
    /**
     * Provides a way to store the potion effects from the player.
     * 
     * @param player Player to store the effects for.
     * @return A List of the potion effects.
     */
	public static List<CTPPotionEffect> storePlayerPotionEffectsNew(Player player) {
        List<CTPPotionEffect> effects = new ArrayList<CTPPotionEffect>();
        
        Collection<PotionEffect> potions = player.getActivePotionEffects();
        
        for (PotionEffect potion : potions) {
        	effects.add(new CTPPotionEffect(potion.getDuration(), potion.getAmplifier(), potion.getType()));
        }
        
        return effects;
    }
	
	/**
	 * Provides a way to restore the potions that the player had.
	 * 
	 * @param player The player who we need to restore them back to.
	 * @param effects A List<CTPPotionEffect> of the potions that need to be restored.
	 */
	public static void restorePotionEffectsNew(Player player, List<CTPPotionEffect> effects) {
		for(CTPPotionEffect effect : effects) {
			player.addPotionEffect(new PotionEffect(effect.type, effect.duration, effect.strength));
		}
	}
}
