package com.riotgames.interview.hongkong.matchmaking.matcher;

import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;
import com.riotgames.interview.hongkong.matchmaking.skill.SkillCalculator;

/** 
 * MatcherFactory that creates WeightedScoringMatcher which combines scores in a linear basis
 * */
public class WeightedScoringMatcherFactory implements AbstractMatcherFactory<PlayerComponent> {

	/** Algorithm to estimate player's skill */
	private final SkillCalculator<PlayerComponent> skillCalculator;
	
	/** Linear weight for skill score */
	private final double skillWeight;

	/** Linear weight for weight score */
	private final double levelWeight;

	/** Linear weight for queue score */
	private final double queueWeight;	
	
	/**
	 * Class constructor
	 * 
	 * NOTE: Weights do not need to sum 1
	 * 
	 * @param skillCalculator Algorithm to estimate player's skill
	 * @param skillWeight Weight (>= 0) for the skill part of the score
	 * @param levelWeight Weight (>= 0) for the level part of the score
	 * @param queueWeight Weight (>= 0) for the queue part of the score
	 */
	public WeightedScoringMatcherFactory(SkillCalculator<PlayerComponent> skillCalculator, 
			double skillWeight, double levelWeight, double queueWeight) {
		this.skillCalculator = skillCalculator;
		this.skillWeight = skillWeight;
		this.levelWeight = levelWeight;
		this.queueWeight = queueWeight;
	}
	
	@Override
	public Matcher<PlayerComponent> createMatcher() {
		return new WeightedScoringMatcher(skillCalculator, skillWeight, levelWeight, queueWeight);
	}

}
