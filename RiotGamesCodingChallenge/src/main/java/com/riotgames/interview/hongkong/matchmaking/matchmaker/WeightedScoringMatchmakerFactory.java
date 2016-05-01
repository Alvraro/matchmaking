package com.riotgames.interview.hongkong.matchmaking.matchmaker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.riotgames.interview.hongkong.matchmaking.matcher.AbstractMatcherFactory;
import com.riotgames.interview.hongkong.matchmaking.matcher.Matcher;
import com.riotgames.interview.hongkong.matchmaking.matcher.WeightedScoringMatcherFactory;
import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;
import com.riotgames.interview.hongkong.matchmaking.skill.AbstractSkillCalculatorFactory;
import com.riotgames.interview.hongkong.matchmaking.skill.BasicSkillCalculatorFactory;
import com.riotgames.interview.hongkong.matchmaking.skill.SkillCalculator;

/** 
 * MatchmakerFactory that creates an experimental Matchmaker that uses:
 * - WeightedScoringMatcher that combines different scores to calculate similarities
 * - BasicSkillCalculator for basic skill estimations based on win/loss ratio
 * 
 * It's configured by weights that must be empirically adjusted
 */
public class WeightedScoringMatchmakerFactory implements AbstractMatchmakerFactory {
	/** Path to the Matchmaker's configuration file */
	private static final String CONFIG_FILE = "src/main/resources/WeightedScoringMatchmaker.properties";

	/** Configuration properties */
	private Properties properties;
	
	/** Special preference for long queued players */
	private boolean longQueuedPreference;

	/** Linear weight for skill score */
	private final double skillWeight;

	/** Linear weight for weight score */
	private final double levelWeight;

	/** Linear weight for queue score */
	private final double queueWeight;

	public WeightedScoringMatchmakerFactory() throws FileNotFoundException, IOException {
		/** Read default values from properties file */		
		properties = new Properties();
		properties.load(new FileInputStream(CONFIG_FILE));
		
		longQueuedPreference = Boolean.parseBoolean((String) properties.get("long.queued.preference"));
		skillWeight = Double.parseDouble((String) properties.get("skill.weight")); 
		levelWeight = Double.parseDouble((String) properties.get("level.weight")); 
		queueWeight = Double.parseDouble((String) properties.get("queue.weight"));
	}

	/** Creates factory with given special preference for long queued players */
	public WeightedScoringMatchmakerFactory(boolean longQueuedPreference, 
			double skillWeight, double levelWeight, double queueWeight) {
		
		this.longQueuedPreference = longQueuedPreference;
		this.skillWeight = skillWeight;
		this.levelWeight = levelWeight;
		this.queueWeight = queueWeight;
	}

	@Override
	public Matchmaker createMatchMaker() {
		// Create a SkillCalculator to calculate player skills
		AbstractSkillCalculatorFactory<PlayerComponent> calculatorFactory = new BasicSkillCalculatorFactory();
		SkillCalculator<PlayerComponent> skillCalculator = calculatorFactory.createSkillCalculator();
		
		// Create a Matcher
		AbstractMatcherFactory<PlayerComponent> matcherFactory = 
				new WeightedScoringMatcherFactory(
						skillCalculator, 
						skillWeight,
						levelWeight,
						queueWeight);
		
		Matcher<PlayerComponent> matcher = matcherFactory.createMatcher();
		
		// Finally, assemble the Matchmaker
		return new MatchmakerImpl(skillCalculator, matcher, longQueuedPreference);
	}

}
