package com.riotgames.interview.hongkong.matchmaking.matchmaker;

import com.riotgames.interview.hongkong.matchmaking.matcher.AbstractMatcherFactory;
import com.riotgames.interview.hongkong.matchmaking.matcher.Matcher;
import com.riotgames.interview.hongkong.matchmaking.matcher.SimpleSkillBasedMatcherFactory;
import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;
import com.riotgames.interview.hongkong.matchmaking.skill.AbstractSkillCalculatorFactory;
import com.riotgames.interview.hongkong.matchmaking.skill.BasicSkillCalculatorFactory;
import com.riotgames.interview.hongkong.matchmaking.skill.SkillCalculator;

/** 
 * MatchmakerFactory that creates a basic Matchmaker featuring:
 * - SimpleSkillBasedMatcher that calculates similarities based solely on player skill
 * - BasicSkillCalculator for basic skill estimations based on win/loss ratio
 * 
 * It's configured by weights that must be empirically adjusted
 */
public class SimpleSkillBasedMatchmakerFactory implements AbstractMatchmakerFactory {

	/** Special preference for long queued players */
	private boolean longQueuedPreference;

	public SimpleSkillBasedMatchmakerFactory() {
		this(false);
	}

	/** Creates factory with given special preference for long queued players */
	public SimpleSkillBasedMatchmakerFactory(boolean longQueuedPreference) {
		this.longQueuedPreference = longQueuedPreference;
	}

	@Override
	public Matchmaker createMatchMaker() {
		// Create a SkillCalculator to calculate player skills
		AbstractSkillCalculatorFactory<PlayerComponent> calculatorFactory = new BasicSkillCalculatorFactory();
		SkillCalculator<PlayerComponent> skillCalculator = calculatorFactory.createSkillCalculator();
		
		// Create a Matcher
		AbstractMatcherFactory<PlayerComponent> matcherFactory = new SimpleSkillBasedMatcherFactory(skillCalculator);
		Matcher<PlayerComponent> matcher = matcherFactory.createMatcher();
		
		// Finally, assemble the Matchmaker
		return new MatchmakerImpl(skillCalculator, matcher, longQueuedPreference);
	}

}
