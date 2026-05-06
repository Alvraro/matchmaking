package com.riotgames.interview.hongkong.matchmaking.player;

import java.util.HashSet;

/** 
 * Defines info that can be extracted from a player or team of players.
 * This is essentially a Composite pattern 
 * */
public abstract class PlayerComponent {
	/** Team assignation */
	private PlayerTeam teamAssigned;

	public PlayerComponent(){
		teamAssigned = null;
	}
	
	public abstract long getWins();

	public abstract long getLosses();

	/** Assigns this PlayerComponent a team */
	public void setTeamAssigned(PlayerTeam playerComposite){
		this.teamAssigned = playerComposite;
	}
	
	/** Returns PlayerComponent's assigned team */
	public PlayerTeam getTeamAssigned(){
		return teamAssigned;
	}

	/** Get all children Player objects contained: N for a PlayerTeam, 1 for a single Player */
	public abstract HashSet<Player> getChildrenPlayers();

	/** Get player's matchmaking enter time (ms) or team's oldest */
	public abstract Long getOldestMatchmakingEnterTime();
	
	/** Explicit hashCode implementation is mandatory */
	@Override
	public abstract int hashCode();
	
	/** Explicit equals implementation is mandatory */
	@Override
	public abstract boolean equals(Object obj);

	/** Get player's normalized level or team average: 1 = max level, 0 min */
	public abstract double getNormalizedLevel();

	/** Get player's normalized matchmaking enter time or team average: 1 = max time, 0 min */
	public abstract double getNormalizedMatchmakingTime();

	/**
	 * Update dynamic fields which can change over time.
	 * We receive a fixed checkpoint time instead of using System.currentTimeMillis()
	 * because otherwise there would be differences depending on when we update each player.
	 *   
	 * @param checkpointTime time (ms) considered to be the current time
	 */
	public abstract void updateDynamicFields(long checkPointTime);

}
