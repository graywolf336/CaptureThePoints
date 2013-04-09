package me.dalton.capturethepoints;

public class LanguageOptions {
	/*
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
	 */
	public String GAME_STARTED = "A Capture The Points game has started in the arena";
	public String GAME_ENDED = "A Capture The Points game has ended!";
	public String WINS = "wins!";
	public String CAPTURED = "captured";
	public String LOST = "lost";
	public String POINTS = "points";
	public String FINAL_SCORE = "final score";
	public String FINAL_POINTS = "final points";
	public String TIE_POINTS = "It's a tie! %WT teams have passed %WP points!";
	public String TIE_SCORE = "It's a tie! %WT teams have a score of %WS!";
	
	public String checks_NO_ARENA_BY_NAME = "Couldn't find an arena by that name.";
	public String checks_NO_ARENAS = "There are currently no arenas.";
	public String checks_NO_ARENA_NAME = "Couldn't find the name of the arena, please try again.";
	public String checks_INCORRECT_WORLD_SETUP_ADMIN = "The arena config is incorrect. The world \"%AW\" could not be found. Hint: your first world's name is \"%SFW\".";
	public String checks_INCORRECT_SETUP_USER = "Sorry, this arena has not been set up properly. Please tell an admin. %WII";
	public String checks_NO_LOBBY = "No lobby for the arena \"%AN\"";
	public String checks_NO_BOUNDARIES = "The arena's boundaries are not properly set for \"%AN\"";
	public String checks_NO_TEAM_SPAWNS = "There are currently no team spawns defined for \"%AN\"";
	public String checks_NOT_ENOUGH_TEAM_SPAWNS = "There is only one team spawn, minimum of two are needed for \"%AN\"";
	public String checks_INCORRECT_SPAWN_LOCATION = "The spawn point \"%SPN\" in the arena \"%AN\" is out of the arena boundaries. "
			+ "[Spawn is %SPX, %SPZ. Boundaries are %AX1<==>%AX2, %AZ1<==>%AZ2].";
	public String checks_NO_POINTS = "No points have been defined, therefore it is hard to play a game so I can't let you join.";
	public String checks_EDIT_MODE = "Sorry, this arena is currently in edit mode.";
	public String checks_DISABLED = "Sorry, this arena is currently disabled.";
	public String checks_FULL_ARENA = "Sorry, this arena is currently full of players. Try again in a little bit.";
	public String checks_GAME_ALREADY_STARTED = "A game has already started. You may not join.";
}
