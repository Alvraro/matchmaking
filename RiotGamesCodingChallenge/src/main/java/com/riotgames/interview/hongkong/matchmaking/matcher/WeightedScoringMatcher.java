package com.riotgames.interview.hongkong.matchmaking.matcher;

import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;
import com.riotgames.interview.hongkong.matchmaking.skill.SkillCalculator;

/** 
 * Implementation of a matcher that uses weights to combine several scores
 * */
public class WeightedScoringMatcher implements Matcher<PlayerComponent> {

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
	public WeightedScoringMatcher(SkillCalculator<PlayerComponent> skillCalculator, 
			double skillWeight, double levelWeight, double queueWeight) {
		this.skillCalculator = skillCalculator;
		this.skillWeight = skillWeight;
		this.levelWeight = levelWeight;
		this.queueWeight = queueWeight;
	}

	@Override
	public double getSimilarity(PlayerComponent one, PlayerComponent another) {
		double skillScore = getSkillScore(one, another);
		double levelScore = getLevelScore(one, another);
		double queueScore = getQueueScore(one, another);
		
		return skillWeight * skillScore + levelWeight * levelScore + queueWeight * queueScore;
	}

	/**
	 * 1) Get skill difference in absolute value: 0 (same skill), 1 (greatest diff: one is pro and the other is n00b)
	 * 2) Return difference from 1: (same skill = greatest score), 0 (greatest diff = lowest score)
	 */
	private double getSkillScore(PlayerComponent one, PlayerComponent another) {
		return 1 - Math.abs(skillCalculator.getSkill(one) - skillCalculator.getSkill(another));
	}

	/**
	 * 1) Get normalized level difference in absolute value: 0 (same level), 1 (max level difference)
	 * 2) Invert value range: 1 (same level -> greatest score), 0 (max level difference -> lowest score)
	 */
	private double getLevelScore(PlayerComponent one, PlayerComponent another) {
		return 1 - Math.abs(one.getNormalizedLevel() - another.getNormalizedLevel());
	}

	/**
	 * 1) Get normalized oldest matchmaking enter time for both players: 1 (oldest -> greatest score), 0 (newest -> lowest score)
	 * 2) Return average 
	 */
	private double getQueueScore(PlayerComponent one, PlayerComponent another) {
		return 
				0.5 * (1 - one.getNormalizedMatchmakingTime()) + 
				0.5 * (1 - another.getNormalizedMatchmakingTime());
	}

	@Override
	public String toString() {
		return "WeightedScoringMatcher [skillCalculator=" + skillCalculator + ", skillWeight=" + skillWeight
				+ ", levelWeight=" + levelWeight + ", queueWeight=" + queueWeight + "]";
	}

}
