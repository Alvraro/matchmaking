package com.riotgames.interview.hongkong.matchmaking.matchmaker;

/** Abstract factory that creates Matchmaker objects */
public interface AbstractMatchmakerFactory {
	
	/** Creates a MatchMaker */
	public Matchmaker createMatchMaker();
	
}
