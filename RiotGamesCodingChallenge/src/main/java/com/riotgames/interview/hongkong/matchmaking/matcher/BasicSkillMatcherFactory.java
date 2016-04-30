package com.riotgames.interview.hongkong.matchmaking.matcher;

import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;
import com.riotgames.interview.hongkong.matchmaking.skill.SkillCalculator;

/** 
 * MatcherFactory that creates BasicSkillMatcher
 * */
public class BasicSkillMatcherFactory implements AbstractMatcherFactory<PlayerComponent> {

	private SkillCalculator<PlayerComponent> skillCalculator;

	public BasicSkillMatcherFactory(SkillCalculator<PlayerComponent> skillCalculator){
		this.skillCalculator = skillCalculator;
	}
	
	@Override
	public Matcher<PlayerComponent> createMatcher() {
		return new BasicSkillMatcher(skillCalculator);
	}

}
