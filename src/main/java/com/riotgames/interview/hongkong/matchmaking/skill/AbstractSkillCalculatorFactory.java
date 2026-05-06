package com.riotgames.interview.hongkong.matchmaking.skill;

/** Abstract factory that creates SkillCalculator objects */
public interface AbstractSkillCalculatorFactory<T> {

	/** Creates a SkillCalculator */
	SkillCalculator<T> createSkillCalculator();
	
}
