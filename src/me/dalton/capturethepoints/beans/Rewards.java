package me.dalton.capturethepoints.beans;

import java.util.LinkedList;
import java.util.List;

 
public class Rewards {
	private int winnerRewardCount;
	private int otherTeamRewardCount;
	private int expRewardForKillingEnemy = 0;
	private List<Items> winnerRewards = new LinkedList<Items>();
	private List<Items> looserRewards = new LinkedList<Items>();
	private List<Items> rewardsForKill = new LinkedList<Items>();
	private List<Items> rewardsForCapture = new LinkedList<Items>();
	
	public void setWinnerRewardCount(int count) {
		this.winnerRewardCount = count;
	}
	
	public int getWinnerRewardCount() {
		return this.winnerRewardCount;
	}
	
	public void setOtherTeamRewardCount(int count) {
		this.otherTeamRewardCount = count;
	}
	
	public int getOtherTeamRewardCount() {
		return this.otherTeamRewardCount;
	}
	
	public void setExpRewardForKillingEnemy(int amount) {
		this.expRewardForKillingEnemy = amount;
	}
	
	public int getExpRewardForKillingEnemy() {
		return this.expRewardForKillingEnemy;
	}
	
	public void setWinnerRewards(List<Items> rewards) {
		this.winnerRewards = rewards;
	}
	
	public List<Items> getWinnerRewards() {
		return this.winnerRewards;
	}
	
	public void setLooserRewards(List<Items> rewards) {
		this.looserRewards = rewards;
	}
	
	public List<Items> getLooserRewards() {
		return this.looserRewards;
	}
	
	public void setRewardsForKill(List<Items> rewards) {
		this.rewardsForKill = rewards;
	}
	
	public List<Items> getRewardsForKill() {
		return this.rewardsForKill;
	}
	
	public void setRewardsForCapture(List<Items> rewards) {
		this.rewardsForCapture = rewards;
	}
	
	public List<Items> getRewardsForCapture() {
		return this.rewardsForCapture;
	}
}