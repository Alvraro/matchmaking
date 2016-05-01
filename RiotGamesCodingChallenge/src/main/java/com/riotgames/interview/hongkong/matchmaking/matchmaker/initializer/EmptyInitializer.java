package com.riotgames.interview.hongkong.matchmaking.matchmaker.initializer;

import com.riotgames.interview.hongkong.matchmaking.player.PlayerTeam;

/**
 *	Simplest implementation of TeamInitializer that just creates empty teams :) 
 */
public class EmptyInitializer implements TeamInitializer {

	@Override
	public InitialTeams getInitialTeams() {
		return new InitialTeams(new PlayerTeam(0), new PlayerTeam(0));
	}
	
}
