package me.dalton.capturethepoints.util;

import me.dalton.capturethepoints.CaptureThePoints;

public class MoneyUtils {
	private CaptureThePoints ctp;
	
	public MoneyUtils(CaptureThePoints ctp) {
		this.ctp = ctp;
	}
	
    /** Check if the player can afford this price */
    public boolean canPay(String player, int price) {
        return (price != Integer.MAX_VALUE && ctp.getArenaMaster().getArenaPlayerIsIn(player).getPlayerData(player).getMoney() >= price);
    }

    /** Deduct the price from the player's account. Returns boolean whether play had enough funds to do so. */
    public boolean chargeAccount(String player, int price) {
        if (ctp.getArenaMaster().getArenaPlayerIsIn(player).getPlayerData(player).getMoney() >= price) {
        	ctp.getArenaMaster().getArenaPlayerIsIn(player).getPlayerData(player).setMoney(
        			ctp.getArenaMaster().getArenaPlayerIsIn(player).getPlayerData(player).getMoney() - price);
            return true;
        }
        return false;
    }
}
