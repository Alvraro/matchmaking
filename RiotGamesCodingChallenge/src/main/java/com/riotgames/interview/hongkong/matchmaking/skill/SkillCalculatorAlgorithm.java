package com.riotgames.interview.hongkong.matchmaking.skill;

/**
 * Skill calculator algorithm. Calculates skill of something
 * */
public interface SkillCalculatorAlgorithm<T> {
	
	public double getSkill(T one);
	
}
