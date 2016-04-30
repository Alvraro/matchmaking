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
}
