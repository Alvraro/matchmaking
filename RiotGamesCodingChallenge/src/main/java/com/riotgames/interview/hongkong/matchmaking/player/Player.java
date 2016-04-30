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

    /** Basic constructor */
    public Player(String name, long wins, long losses) throws PlayerFormatException {
    	// Assign player an unique id and increase global counter for next one
    	this.id = nextId++;
    	
        this.name = name;
        this.wins = wins;
        this.losses = losses;

    	checkName(name);
    	checkWins(wins);
    	checkLosses(losses);
        
        matchmakingEnterTime = null;
    }

    /** Copy constructor */ 
	public Player(Player p) throws PlayerFormatException {
		this(p.name, p.wins, p.losses);
	}

	private void checkName(String name) throws PlayerFormatException {
		if(name == null)
			throw new PlayerFormatException(this, "Name can't be null");

		if(name.isEmpty())
			throw new PlayerFormatException(this, "Name can't be empty");
		
		// TODO If player names are created here, check profanity!
	}

	private void checkWins(long wins) throws PlayerFormatException {
		if(wins < 0)
			throw new PlayerFormatException(this, "Wins can't be negative");
			
		if(wins > MAX_WINS)
			throw new PlayerFormatException(this, "Wins can't be over " + MAX_WINS);
	}
	
    private void checkLosses(long losses) throws PlayerFormatException {
		if(losses < 0)
			throw new PlayerFormatException(this, "Losses can't be negative");
			
		if(losses > MAX_LOSSES)
			throw new PlayerFormatException(this, "Losses can't be over " + MAX_LOSSES);
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
    
}
