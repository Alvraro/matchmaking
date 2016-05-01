package com.riotgames.interview.hongkong.matchmaking.player;

import java.util.HashSet;
import java.util.logging.Logger;

import com.riotgames.interview.hongkong.matchmaking.MatchmakingException;

/** Represents an aggregate (aka "team") of already paired players */
public class PlayerTeam extends PlayerComponent {
	/** Average wins of contained players */
	private long averageWins;
	
	/** Average losses of contained players */
	private long averageLosses;

	/** Set of contained players */
	private HashSet<PlayerComponent> children;

	/** Logger for printing stuff */
	private static Logger logger = Logger.getLogger(PlayerTeam.class.toString());
	
	/** Create a PlayerComposite with a known initial capacity */
	public PlayerTeam(int initialSize){
		averageWins = 0;
		averageLosses = 0;

		children = new HashSet<PlayerComponent>(initialSize);
	}
	
	/** Adds a new PlayerComponent as child of this aggregate */
	public boolean addPlayerComponent(PlayerComponent newPlayerComponent) throws MatchmakingException {
		if(children.contains(newPlayerComponent)){
			logger.warning("Rejecting adding twice the same PlayerComponent");
			return false;
		}
		
		// Recalculate and cache fields to avoid full recalculations with every getXXX call :s
		
		// First get current totals
		long totalWins = averageWins * children.size();
		long totalLosses = averageLosses * children.size(); 
		
		// Add new player's values
		totalWins += newPlayerComponent.getWins();
		totalLosses += newPlayerComponent.getLosses();

		// Calculate new averages rounding the results (it's ok to lose some precision)
		averageWins = Math.round(totalWins / (children.size() + 1.0));
		averageLosses = Math.round(totalLosses / (children.size() + 1.0));
		
		// Notify player about team assignment
		newPlayerComponent.setTeamAssigned(this);
		
		// We already checked that newPlayerComponent is not contained, so this should never fail
		if(!children.add(newPlayerComponent))
			// Just in case...
			throw new MatchmakingException("Can't add PlayerComponent");
		
		return true;
	}

	/** Getter for children PlayerComponents */
	public HashSet<PlayerComponent> getChildren() {
		return children;
	}
	
	@Override
	public HashSet<Player> getChildrenPlayers() {
		// At least we'll have one Player per child
		HashSet<Player> players = new HashSet<Player>(children.size());
		
		// For each PlayerComponent child
		for(PlayerComponent child : children){
			players.addAll(child.getChildrenPlayers());
		}
		
		return players;
	}
		
	@Override
	public long getWins() {
		return averageWins;
	}

	@Override
	public long getLosses() {
		return averageLosses;
	}

	@Override
	public String toString() {
		return "PlayerTeam [averageWins=" + averageWins + ", averageLosses=" + averageLosses + ", children=" + children	+ "]";
	}

	@Override
	public Long getOldestMatchmakingEnterTime() {
		long oldestTime = Long.MAX_VALUE;
		
		// For each PlayerComponent child
		for(PlayerComponent child : children){
			Long childOldestTime = child.getOldestMatchmakingEnterTime();
			if(childOldestTime < oldestTime)
				oldestTime = childOldestTime; 
		}
		
		return oldestTime;
	}

}
