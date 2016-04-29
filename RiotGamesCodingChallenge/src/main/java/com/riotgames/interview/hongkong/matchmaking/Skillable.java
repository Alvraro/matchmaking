package com.riotgames.interview.hongkong.matchmaking;

/**
 * Represents something (player or team) that has "skill"
 * */
public interface Skillable {

	/** It must return skill as a [0,1] value: 0 for minimum skill, 1 for maximum */
	public float getSkill();

}
