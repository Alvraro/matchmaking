package com.riotgames.interview.hongkong.matchmaking;

import test.com.riotgames.interview.hongkong.matchmaking.SampleData;

/**
 * <p>
 * Representation of a player.
 * </p>
 * <p>
 * As indicated in the challenge description, feel free to augment the Player
 * class in any way that you feel will improve your final matchmaking solution.
 * <strong>Do NOT remove the name, wins, or losses fields.</strong> Also note
 * that if you make any of these changes, you are responsible for updating the
 * {@link SampleData} such that it provides a useful data set to exercise your
 * solution.
 * </p>
 */
public class Player implements Skillable {
	/** Max number of wins a player can have. More than that it's probably an error */
	private final static long MAX_WINS = 1000000; // 1.000.000 wins? Really?
	
	/** Max number of losses a player can have. More than that it's probably an error */
	private final static long MAX_LOSSES = 1000000; // 1.000.000 losses? REALLY? Pls uninstall! :D
	
	/** Global ID counter to assign players an unique ID */
	private static int nextId=0;

	/** Player's unique ID */
	private final int id;
	
	/** Player's name */
    private final String name;
    
    /** Player's wins */
    private final long wins;
    
    /** Player's losses */
    private final long losses;

    /** Time at which the player enters the match making */
	private Long matchmakingEnterTime;

    public Player(String name, long wins, long losses) throws FormatException {
    	// Assign player an unique id and increase global counter for next one
    	this.id = nextId++;
    	
    	checkName(name);
    	
    	checkWins(wins);
    	
    	checkLosses(losses);
    	
        this.name = name;
        this.wins = wins;
        this.losses = losses;
        
        matchmakingEnterTime = null;
    }

	private void checkName(String name) throws FormatException {
		if(name == null)
			throw new FormatException("Name can't be null");

		if(name.isEmpty())
			throw new FormatException("Name can't be empty");
		
		// TODO If player names are created here, check profanity!
	}

	private void checkWins(long wins) throws FormatException {
		if(wins < 0)
			throw new FormatException("Wins can't be negative");
			
		if(wins > MAX_WINS)
			throw new FormatException("Wins can't be over " + MAX_WINS);
	}
	
    private void checkLosses(long losses) throws FormatException {
		if(losses < 0)
			throw new FormatException("Losses can't be negative");
			
		if(losses > MAX_LOSSES)
			throw new FormatException("Losses can't be over " + MAX_LOSSES);
	}
	
	public String getName() {
        return name;
    }

    public long getWins() {
        return wins;
    }

    public long getLosses() {
        return losses;
    }

    @Override
    public int hashCode() {
    	return id;
    }

	public Long getMatchmakingEnterTime() {
		return matchmakingEnterTime;
	}

	public void setMatchmakingEnterTime(long matchmakingEnterTime) {
		this.matchmakingEnterTime = matchmakingEnterTime;
	}

	@Override
	public float getSkill() {
		// TODO Auto-generated method stub
		return 0;
	}
    
}
