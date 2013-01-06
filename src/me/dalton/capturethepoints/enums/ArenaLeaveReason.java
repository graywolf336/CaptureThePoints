package me.dalton.capturethepoints.enums;

/**
 * Reasons for why the player left a CTP game.
 * 
 * @author graywolf336
 * @since 1.5.0-b104
 * @version 1.0.0
 *
 */
public enum ArenaLeaveReason {
	/**
	 * The game has ended and there are no more points to be taken or the time has expired, etc.
	 */
	ARENA_END,
	/**
	 * Player get's kicked from the server.
	 */
	PLAYER_KICK,
	/**
	 * Player typed the command <em>/ctp leave</em> command.
	 */
	PLAYER_LEAVE_COMMAND,
	/**
	 * Player got kicked from the arena by an admin who typed the command <em>/ctp kick</em>
	 */
	PLAYER_KICK_COMMAND,
	/**
	 * Player respawned but we stilled had something stored about them.
	 */
	PLAYER_RESPAWN,
	/**
	 * Player tried to teleport out of the arena somehow, usually this happens when players try to glitch out items.
	 */
	PLAYER_TELEPORT,
	/**
	 * Player closed their Minecraft <em>client</em> or their <em>client</em> crashed.
	 */
	PLAYER_QUIT,
	/**
	 * The server is shutting down, or sadly someone issued the command /reload.
	 */
	SERVER_STOP,
	/**
	 * Default reason, only happens when we have no clue what happened or any error happened.
	 */
	UNKNOWN;
}
