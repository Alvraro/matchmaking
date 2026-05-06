package com.riotgames.interview.hongkong.matchmaking.player;

import java.util.HashSet;

import com.riotgames.interview.hongkong.matchmaking.PlayerFormatException;
import com.riotgames.interview.hongkong.matchmaking.SampleData;

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
 * I'm assuming:
 * - Wins/Losses must be in a specific range (see PlayerComponent.MAX_WINS & PlayerComponent.MAX_LOSSES). Otherwise it's an error
 * - Player names doesn't need to be unique but there must be a non-empty one
 */
public class Player extends PlayerComponent {
	/** Max number of wins a player can have. More than that it's probably an error */
	public final static long MAX_WINS = 1000000; // 1.000.000 wins? Really?
	
	/** Max number of losses a player can have. More than that it's probably an error */
	public final static long MAX_LOSSES = 1000000; // 1.000.000 losses? REALLY? Pls uninstall! :D

	/** Max level a player can have */
	public static final int MAX_LEVEL = 18;

	/** Min level a player can have */
	public static final int MIN_LEVEL = 1;

	/** Max acceptable time (ms) to be assigned a match */
	private static final int MAX_ACCEPTABLE_MATCHMAKING_TIME = 5000;
	
	/** Global ID counter to assign players an unique ID */
	private static int nextId=1;

	/** Player's unique ID */
	private final int id;
	
	/** Player's name */
    private final String name;
    
    /** Player's wins */
    private final long wins;
    
    /** Player's losses */
    private final long losses;

    /** Time (ms) at which the player enters the matchmaking system */
	private Long matchmakingEnterTime;

    /** Time (ms) at which the player exits the matchmaking system because he/she was assigned to a Match */
	private Long matchmakingExitTime;

	private int level;

	/** Time (ms) the player has been waiting in the matchmaking system since last checkpoint */
	private double matchmakingTime;

    /** Basic constructor */
    public Player(String name, long wins, long losses, int level) throws PlayerFormatException {
    	// Assign player an unique id and increase global counter for next one
    	this.id = nextId++;
    	
        this.name = name;
        this.wins = wins;
        this.losses = losses;
        this.level = level;

        // Check errors
    	checkName(name);
    	checkWins(wins);
    	checkLosses(losses);
    	checkLevel(level);
        
    	// Initialize matchmaking time info
        matchmakingEnterTime = null;
        matchmakingExitTime = null;
        matchmakingTime = 0;
    }

	/** Copy constructor */ 
	public Player(Player p) throws PlayerFormatException {
		this(p.name, p.wins, p.losses, p.level);
	}

    /** A PlayerTeam is hashed by its player set */
    @Override
    public int hashCode() {
    	return id;
    }
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Player))
			return false;
		
		// ID is unique
		return
				id == ((Player)obj).id;
	}
    
	/** Perform integrity/profanity checks over the player's name */
	private void checkName(String name) throws PlayerFormatException {
		if(name == null)
			throw new PlayerFormatException(this, "Name can't be null");

		if(name.isEmpty())
			throw new PlayerFormatException(this, "Name can't be empty");
		
		// TODO If player names are created here, check profanity!
	}

	/** Perform integrity checks over the player's number of wins */
	private void checkWins(long wins) throws PlayerFormatException {
		if(wins < 0)
			throw new PlayerFormatException(this, "Wins ("+wins+") can't be negative");
			
		if(wins > MAX_WINS)
			throw new PlayerFormatException(this, "Wins ("+wins+") can't be over " + MAX_WINS);
	}
	
	/** Perform integrity checks over the player's number of losses */
    private void checkLosses(long losses) throws PlayerFormatException {
		if(losses < 0)
			throw new PlayerFormatException(this, "Losses ("+losses+") can't be negative");
			
		if(losses > MAX_LOSSES)
			throw new PlayerFormatException(this, "Losses ("+losses+") can't be over " + MAX_LOSSES);
	}
	
    /** Perform integrity checks over the player's level 
     * @throws PlayerFormatException */
    private void checkLevel(int level) throws PlayerFormatException {
		if(level > MAX_LEVEL)
			throw new PlayerFormatException(this, "Level ("+level+") can't be over " + MAX_LEVEL);
		
		if(level < MIN_LEVEL)
			throw new PlayerFormatException(this, "Level ("+level+") can't be below " + MIN_LEVEL);
	}
    
	public String getName() {
        return name;
    }

    @Override
    public long getWins() {
        return wins;
    }

    @Override
    public long getLosses() {
        return losses;
    }

    @Override
	public Long getOldestMatchmakingEnterTime() {
		return getMatchmakingEnterTime();
	}

	public Long getMatchmakingEnterTime() {
		return matchmakingEnterTime;
	}
    
	public void setMatchmakingEnterTime(Long matchmakingEnterTime) {
		this.matchmakingEnterTime = matchmakingEnterTime;
		updateMatchMakingTime(matchmakingEnterTime);
	}
	
	public Long getMatchmakingExitTime() {
		return matchmakingExitTime;
	}

	public void setMatchmakingExitTime(Long matchmakingExitTime) {
		this.matchmakingExitTime = matchmakingExitTime;
	}

	@Override
	public HashSet<Player> getChildrenPlayers() {
		// Returns a HashSet with itself
		HashSet<Player> ret = new HashSet<Player>(1);
		ret.add(this);
		return ret;
	}

	@Override
	public String toString() {
		return "Player [id=" + id + ", name=" + name + ", wins=" + wins + ", losses=" + losses + "]";
	}

	@Override
	public double getNormalizedLevel() {
		// Normalize level by its max possible value
		return ((double)level) / MAX_LEVEL;
	}

	/** Update matchmakingTime at a certain checkpoint time. We do so to refresh time for all players at once */
	private void updateMatchMakingTime(long checkpointTime){
		// He/she entered
		if(matchmakingEnterTime != null){
			// And he/she also exited
			if(matchmakingExitTime != null)
				// So we can calculate the final value
				matchmakingTime = matchmakingExitTime - matchmakingEnterTime;
				
			// But he/she didn't exit so we update at the checkpoint time given
			else
				matchmakingTime = checkpointTime - matchmakingEnterTime;
		}
		
		// He/she is not in the matchmaking system yet!
		else
			matchmakingTime = 0;
	}
	
	@Override
	public double getNormalizedMatchmakingTime() {
		// Over a certain limit we consider all matchmaking waiting times as the acceptable maximum
		double limitedMatchmakingTime = Math.min(matchmakingTime, MAX_ACCEPTABLE_MATCHMAKING_TIME); 
		
		// Return normalized value
		return limitedMatchmakingTime / MAX_ACCEPTABLE_MATCHMAKING_TIME;
	}

	@Override
	public void updateDynamicFields(long checkpointTime) {
		updateMatchMakingTime(checkpointTime);
	}
    
}
