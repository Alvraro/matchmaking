package com.riotgames.interview.hongkong.matchmaking.matcher;

import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;
import com.riotgames.interview.hongkong.matchmaking.skill.SkillCalculator;

/** 
 * Basic implementation of a matcher that only takes skill into consideration
 * */
public class BasicSkillMatcher implements Matcher<PlayerComponent> {

	/** Skill calculator object */
	private SkillCalculator<PlayerComponent> skillCalculator;

	public BasicSkillMatcher(SkillCalculator<PlayerComponent> skillCalculator) {
		this.skillCalculator = skillCalculator;
	}
	
	@Override
	public double getSimilarity(PlayerComponent one, PlayerComponent another) {
		// 1) Get skill difference in absolute value: 0 (same skill), 1 (greatest diff: one is pro and the other is n00b)
		// 2) Return difference from 1: (same skill = greatest similiarity), 0 (greatest diff = lowest similarity)
		return 1 - Math.abs(skillCalculator.getSkill(one) - skillCalculator.getSkill(another));
	}
	
}
