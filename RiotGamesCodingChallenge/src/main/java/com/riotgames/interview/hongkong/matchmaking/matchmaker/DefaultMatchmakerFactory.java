package com.riotgames.interview.hongkong.matchmaking.matchmaker;

import com.riotgames.interview.hongkong.matchmaking.matcher.BasicSkillMatcherFactory;
import com.riotgames.interview.hongkong.matchmaking.matcher.Matcher;
import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;
import com.riotgames.interview.hongkong.matchmaking.skill.BasicSkillCalculatorFactory;
import com.riotgames.interview.hongkong.matchmaking.skill.SkillCalculator;

public class DefaultMatchmakerFactory implements AbstractMatchmakerFactory {

	@Override
	public Matchmaker createMatchMaker() {
		// Firstly we create a SkillCalculator to calculate player skills
		BasicSkillCalculatorFactory skillCalculatorFactory = new BasicSkillCalculatorFactory();
		SkillCalculator<PlayerComponent> skillCalculator = skillCalculatorFactory.createSkillCalculator();
		
		// Then we create a Matcher
		BasicSkillMatcherFactory skillMatcherFactory = new BasicSkillMatcherFactory(skillCalculator);
		Matcher<PlayerComponent> matcher = skillMatcherFactory.createMatcher();
		
		// Finally, assemble the matchmaker
		return new MatchmakerImpl(skillCalculator, matcher);
	}

}
