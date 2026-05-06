package com.riotgames.interview.hongkong.matchmaking.matcher;

import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;
import com.riotgames.interview.hongkong.matchmaking.skill.SkillCalculator;

/** 
 * Basic implementation of a matcher that only takes skill into consideration
 * */
public class SimpleSkillBasedMatcher implements Matcher<PlayerComponent> {

	/** Algorithm to estimate player's skill */
	private final SkillCalculator<PlayerComponent> skillCalculator;

	public SimpleSkillBasedMatcher(SkillCalculator<PlayerComponent> skillCalculator) {
		this.skillCalculator = skillCalculator;
	}
	
	@Override
	public double getSimilarity(PlayerComponent one, PlayerComponent another) {
		// 1) Get skill difference in absolute value: 0 (same skill), 1 (greatest diff: one is pro and the other is n00b)
		// 2) Return difference from 1: 1 (same skill = greatest score), 0 (greatest diff = lowest score)
		return 1 - Math.abs(skillCalculator.getSkill(one) - skillCalculator.getSkill(another));
	}

	@Override
	public String toString() {
		return "SimpleSkillBasedMatcher [skillCalculator=" + skillCalculator + "]";
	}

}
