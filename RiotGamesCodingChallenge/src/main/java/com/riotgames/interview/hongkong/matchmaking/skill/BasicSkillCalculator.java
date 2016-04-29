package com.riotgames.interview.hongkong.matchmaking.skill;

import com.riotgames.interview.hongkong.matchmaking.Player;

/** 
 * Basic implementation of a skill calculator that calculates skill by dividing numbers of wins by the total number of games  
 * */
public class BasicSkillCalculator implements SkillCalculatorAlgorithm<Player>{

	@Override
	public double getSkill(Player one) {
		long wins = one.getWins();
		long losses = one.getLosses();
		
		double total = wins + losses;
		
		// By convention, players with no games are assumed to be n00bs and are assigned skill=0
		if(total == 0)
			return 0;
		
		return wins / total;
	}

}
