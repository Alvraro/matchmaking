package com.riotgames.interview.hongkong.matchmaking.matcher;

/**
 * Matching algorithm. Calculates a similarity score between two objects 
 * */
public interface MatchingAlgorithm<T> {

	/** Similarity function. It should return a value between 0 (no match at all) and 1 (perfect match) */
	public double getSimilarity(T one, T another);
	
}