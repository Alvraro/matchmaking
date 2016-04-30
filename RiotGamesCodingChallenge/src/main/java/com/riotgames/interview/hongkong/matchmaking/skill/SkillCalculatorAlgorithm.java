package com.riotgames.interview.hongkong.matchmaking.skill;

/**
 * Skill calculator algorithm. Calculates skill of something
 * */
public interface SkillCalculatorAlgorithm<T> {
	
	/** Returns skill of something. It must be between 0 (no skilled at all) and 1 (best skilled) */
	public double getSkill(T one);
	
}
