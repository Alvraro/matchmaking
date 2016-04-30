package com.riotgames.interview.hongkong.matchmaking.matcher;

import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;
import com.riotgames.interview.hongkong.matchmaking.skill.SkillCalculatorAlgorithm;

/** 
 * Basic implementation of a matcher that only takes skill into consideration
 * */
public class BasicSkillMatcher implements MatchingAlgorithm<PlayerComponent> {

	/** Skill calculator object */
	private SkillCalculatorAlgorithm<PlayerComponent> skillCalculator;

	public BasicSkillMatcher(SkillCalculatorAlgorithm<PlayerComponent> skillCalculator) {
		this.skillCalculator = skillCalculator;
	}
	
	@Override
	public double getSimilarity(PlayerComponent one, PlayerComponent another) {
		return Math.abs(skillCalculator.getSkill(one) - skillCalculator.getSkill(another));
	}
	
}
