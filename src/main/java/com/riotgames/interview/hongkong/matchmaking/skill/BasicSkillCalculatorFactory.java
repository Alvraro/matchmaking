package com.riotgames.interview.hongkong.matchmaking.skill;

import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;

public class BasicSkillCalculatorFactory implements AbstractSkillCalculatorFactory<PlayerComponent> {

	@Override
	public SkillCalculator<PlayerComponent> createSkillCalculator() {
		return new BasicSkillCalculator();
	}

}
