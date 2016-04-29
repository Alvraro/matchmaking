package com.riotgames.interview.hongkong.matchmaking.matcher;

import com.riotgames.interview.hongkong.matchmaking.Skillable;

/** 
 * Basic implementation of a matcher that only takes skill into consideration
 * */
public class BasicSkillMatcher implements MatchingAlgorithm<Skillable> {

	@Override
	public float getSimilarity(Skillable one, Skillable another) {
		return Math.abs(one.getSkill() - another.getSkill());
	}
	
}
