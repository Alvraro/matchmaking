package com.riotgames.interview.hongkong.matchmaking.matcher;

/** Abstract factory that creates Matcher objects */
public interface AbstractMatcherFactory<T> {
	
	/** Creates a Matcher */
	Matcher<T> createMatcher();
	
}
