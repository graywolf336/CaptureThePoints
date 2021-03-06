package me.dalton.capturethepoints;

public class LanguageOptions {
	/*
	 * %EA = Economy amount
	 * %AW = arena world name
	 * %AN = arena name
	 * %AX1 = arena x1
	 * %AX2 = arena x2
	 * %AZ1 = arena z1
	 * %AZ2 = arena z2
	 * %WT = winning team sizes (number)
	 * %WP = winning points (number)
	 * %WS = winning score (number)
	 * %SFW = server's first world (default world)
	 * %SPN = spawn point name
	 * %SPX = spawn point X (number)
	 * %SPZ = spawn point Z (number)
	 * %WII = what is incorrect
	 * %PN = Player name
	 * %PT = Player team
	 * %WN = world name
	 * %WPS = world player size
	 * %CS = countdown seconds
	 * %OC = old color
	 * %OT = old team
	 * %NC = new color
	 * %NT = new team
	 * 
	 * //Colors
	 *  - &0 = black
	 *  - &1 = dark blue
	 *  - &2 = dark green
	 *  - &3 = dark auqa
	 *  - &4 = dark red
	 *  - &5 = purple
	 *  - &6 = gold
	 *  - &a = green
	 *  - &b = aqua
	 *  - &c = light red
	 *  - &d = light purple
	 *  - &e = yellow
	 *  - &f = white
	 */
	public String GAME_NOT_STARTED = "&cGame not started yet. Try just doing &b/ctp join <arena>";
	public String GAME_STARTED = "A Capture The Points game has started in the arena";
	public String GAME_ENDED = "A Capture The Points game has ended!";
	public String PLAYER_JOIN = "&a%PN&f joined a CTP game.";
	public String PLAYER_LEFT = "&a%PN&f left the CTP game!";
	public String LOBBY_JOIN = "&aJoined CTP lobby &6%AN&a.";
	public String READY_UP_REMINDER = "&dPlease choose your class and ready up, else you will be kicked from the lobby!";
	public String NOT_READY_KICK = "&dYou have been kicked from the lobby for not being ready on time.";
	public String WINS = "wins!";
	public String CAPTURED = "captured";
	public String LOST = "lost";
	public String POINTS = "points";
	public String FINAL_SCORE = "final score";
	public String FINAL_POINTS = "final points";
	public String TIE_POINTS = "It's a tie! %WT teams have passed %WP points!";
	public String TIE_SCORE = "It's a tie! %WT teams have a score of %WS!";
	public String SUCCESSFUL_PAYING_FOR_JOINING = "You were charged &a%EA&f for entering &a%AN&f arena.";
	public String NOT_ENOUGH_MONEY_FOR_JOINING = "&cYou dont have enough money to join arena!";
	public String CHANGING_ROLES_TOO_FAST = "&cYou can only change roles every 1 second!";
	public String FIRST_WORLD = "&cHint: The first world's name is \"%SFW\".";
	public String PREVIOUS_GAME_TERMINATED = "&fA previous Capture The Points game has been terminated.";
	public String ARENA_NAME_LIST = "&bThe current arenas are:";
	public String ALREADY_PLAYING = "&cYou are already playing game!";
	public String NOT_PLAYING = "&cYou are not in the game!";
	public String START_COUNTDOWN = "Game starting in %CS seconds..";
	public String END_COUNTDOWN = "The game has ended! Returning you to where you was in %CS seconds..";
	public String NO_PERMISSION = "&cYou do not have permission to do that.";
	public String PLAYERS = "players";
	public String TEAMS = "Teams";
	public String TRY = "&bTry";
	public String TEAM_BALANCE_MOVE_TO_LOBBY = "&a%PN&f was moved to the lobby! [Team balancing]";
	public String TEAM_BALANCE_CHANGE_TEAMS = "%PN&f changed teams from %OC%OT&f to %NC%NT&f! [Team balancing]";
	public String GAME_ENDED_TOO_FEW_PLAYERS = "The game has stopped because there are too few players. %TC%TN&f wins with a final score of %WS!";
	public String NO_PLAYERS_LEFT = "No players left, resetting the game.";
	public String STANDS_MESSAGE = "Welcome to the stands! Enjoy the game.";
	public String NO_MORE_LIVES = "You lost your last life, you have died!";
	public String REMAINING_LIVES = "Lives remaining:";
	public String PLAYER_LOST_LAST_LIFE = "%PN lost their last life and was moved to the stands!";
	
	public String checks_NO_ARENA_BY_NAME = "&cCouldn't find an arena by that name.";
	public String checks_NO_ARENAS = "&cThere are currently no arenas.";
	public String checks_NO_ARENA_NAME = "&cCouldn't find the name of the arena, please try again.";
	public String checks_INCORRECT_WORLD_SETUP_ADMIN = "&cThe arena config is incorrect. The world \"%AW\" could not be found. Hint: your first world's name is \"%SFW\".";
	public String checks_INCORRECT_SETUP_USER = "&cSorry, this arena has not been set up properly. Please tell an admin. %WII";
	public String checks_NO_LOBBY = "&cNo lobby for the arena \"%AN\"";
	public String checks_NO_STANDS = "&cNo stand defined for the arena \"%AN\"";
	public String checks_NO_BOUNDARIES = "&cThe arena's boundaries are not properly set for \"%AN\"";
	public String checks_NO_SUITABLE_WORLD = "&cYou do not have an arena that will accomodate %WPS players. Please change your min/max player settings.";
	public String checks_NO_TEAM_SPAWNS = "&cThere are currently no team spawns defined for \"%AN\"";
	public String checks_NO_WORLD_FOUND = "&c%WN is not a recognised world.";
	public String checks_NOT_ENOUGH_TEAM_SPAWNS = "&cThere is only one team spawn, minimum of two are needed for \"%AN\"";
	public String checks_INCORRECT_SPAWN_LOCATION = "&cThe spawn point \"%SPN\" in the arena \"%AN\" is out of the arena boundaries. "
			+ "[Spawn is %SPX, %SPZ. Boundaries are %AX1<==>%AX2, %AZ1<==>%AZ2].";
	public String checks_NO_POINTS = "&cNo points have been defined, therefore it is hard to play a game so I can't let you join.";
	public String checks_EDIT_MODE = "&cSorry, this arena is currently in edit mode.";
	public String checks_DISABLED = "&cSorry, this arena is currently disabled.";
	public String checks_FULL_ARENA = "&cSorry, this arena is currently full of players. Try again in a little bit.";
	public String checks_GAME_ALREADY_STARTED = "&cA game has already started. You may not join.";
	public String checks_PLAYER_IN_VEHICLE = "&cBanned for ever... Nah, just don't join while riding something.";
	public String checks_PLAYER_SLEEPING = "&cBanned for life... Nah, just don't join from a bed ;)";
	public String checks_NO_EXISTING_TEAMS = "&cThere are no existing teams to join.";
}
