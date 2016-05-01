package com.riotgames.interview.hongkong.matchmaking.matchmaker;

import com.riotgames.interview.hongkong.matchmaking.player.PlayerTeam;

/**
 * Bean class that stores initial teams
 */
public class InitialTeams {
	
	private final PlayerTeam team1;
	private final PlayerTeam team2;
	
	public InitialTeams(PlayerTeam team1, PlayerTeam team2) {
		super();
		this.team1 = team1;
		this.team2 = team2;
	}

	public PlayerTeam getTeam1() {
		return team1;
	}

	public PlayerTeam getTeam2() {
		return team2;
	}

}
