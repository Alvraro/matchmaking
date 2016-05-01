package com.riotgames.interview.hongkong.matchmaking.matchmaker;

import java.util.Random;

import com.riotgames.interview.hongkong.matchmaking.matcher.AbstractMatcherFactory;
import com.riotgames.interview.hongkong.matchmaking.matcher.Matcher;
import com.riotgames.interview.hongkong.matchmaking.matcher.RandomMatcherFactory;
import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;
import com.riotgames.interview.hongkong.matchmaking.skill.AbstractSkillCalculatorFactory;
import com.riotgames.interview.hongkong.matchmaking.skill.RandomSkillCalculatorFactory;
import com.riotgames.interview.hongkong.matchmaking.skill.SkillCalculator;

/** 
 * MatchmakerFactory that creates a naive Matchmaker that uses:
 * - RandomMatcher for random similarities
 * - RandomSkillCalculator for random skill estimations
 * 
 * Useful for benchmarking purposes.
 */
public class RandomMatchmakerFactory implements AbstractMatchmakerFactory {

	private Random random;

	public RandomMatchmakerFactory(Random random){
		this.random = random;
	}
	
	@Override
	public Matchmaker createMatchMaker() {
		// Create a random SkillCalculator to calculate player skills
		AbstractSkillCalculatorFactory<PlayerComponent> calculatorFactory = new RandomSkillCalculatorFactory(random);
		SkillCalculator<PlayerComponent> skillCalculator = calculatorFactory.createSkillCalculator();
		
		// Create a random Matcher
		AbstractMatcherFactory<PlayerComponent> matcherFactory = new RandomMatcherFactory(random);
		Matcher<PlayerComponent> matcher = matcherFactory.createMatcher();
		
		// Finally, assemble the matchmaker
		return new MatchmakerImpl(skillCalculator, matcher, false);
	}

}
