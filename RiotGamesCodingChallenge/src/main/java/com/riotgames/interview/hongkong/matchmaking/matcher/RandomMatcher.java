package com.riotgames.interview.hongkong.matchmaking.matcher;

import java.util.Random;

import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;

/** 
 * Naive implementation of a matcher that makes random matches
 * */
public class RandomMatcher implements Matcher<PlayerComponent> {

	private Random random;

	public RandomMatcher(Random random) {
		this.random = random;
	}
	
	@Override
	public double getSimilarity(PlayerComponent one, PlayerComponent another) {
		return random.nextDouble();
	}

	@Override
	public String toString() {
		return "RandomMatcher []";
	}

}
