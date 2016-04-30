package com.riotgames.interview.hongkong.matchmaking.skill;

import java.util.Random;

import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;

public class RandomSkillCalculatorFactory implements AbstractSkillCalculatorFactory<PlayerComponent> {
	
	private Random random;

	public RandomSkillCalculatorFactory(Random random){
		this.random = random;
	}
	
	@Override
	public SkillCalculator<PlayerComponent> createSkillCalculator() {
		return new RandomSkillCalculator(random);
	}

}
