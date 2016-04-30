package com.riotgames.interview.hongkong.matchmaking.player;

import java.util.HashSet;

/** Defines info that can be extracted from a player or aggregation of players */
public abstract class PlayerComponent {
	/** Team assignation */
	private PlayerComposite teamAssigned;

	public PlayerComponent(){
		teamAssigned = null;
	}
	
	public abstract long getWins();

	public abstract long getLosses();

	/** Assigns this PlayerComponent a team */
	public void setTeamAssigned(PlayerComposite playerComposite){
		this.teamAssigned = playerComposite;
	}
	
	/** Returns PlayerComponent's assigned team */
	public PlayerComposite getTeamAssigned(){
		return teamAssigned;
	}

	/** Get all children Players */
	public abstract HashSet<Player> getChildrenPlayers();
}
