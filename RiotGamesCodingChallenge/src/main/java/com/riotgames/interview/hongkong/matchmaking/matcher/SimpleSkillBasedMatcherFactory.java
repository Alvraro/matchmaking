package com.riotgames.interview.hongkong.matchmaking.matcher;

import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;
import com.riotgames.interview.hongkong.matchmaking.skill.SkillCalculator;

/** 
 * MatcherFactory that creates SimpleSkillBasedMatcher
 * */
public class SimpleSkillBasedMatcherFactory implements AbstractMatcherFactory<PlayerComponent> {

	/** Algorithm to estimate player's skill */
	private SkillCalculator<PlayerComponent> skillCalculator;

	public SimpleSkillBasedMatcherFactory(SkillCalculator<PlayerComponent> skillCalculator){
		this.skillCalculator = skillCalculator;
	}
	
	@Override
	public Matcher<PlayerComponent> createMatcher() {
		return new SimpleSkillBasedMatcher(skillCalculator);
	}

}
