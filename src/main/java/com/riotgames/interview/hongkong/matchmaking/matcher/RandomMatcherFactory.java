package com.riotgames.interview.hongkong.matchmaking.matcher;

import java.util.Random;

import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;

/** 
 * RandomMatcherFactory that creates a naive RandomMatcher
 * */
public class RandomMatcherFactory implements AbstractMatcherFactory<PlayerComponent> {

	private Random random;

	public RandomMatcherFactory(Random random){
		this.random = random;
	}
	
	@Override
	public Matcher<PlayerComponent> createMatcher() {
		return new RandomMatcher(random);
	}

}
