package com.riotgames.interview.hongkong.matchmaking.skill;

import java.util.Random;

import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;

/** 
 * Naive implementation of a skill calculator that returns random skill  
 * */
public class RandomSkillCalculator implements SkillCalculator<PlayerComponent>{

	private Random random;

	public RandomSkillCalculator(Random random) {
		this.random = random;
	}

	@Override
	public double getSkill(PlayerComponent one) {
		return random.nextDouble();
	}

	@Override
	public String toString() {
		return "RandomSkillCalculator []";
	}

}
