package com.riotgames.interview.hongkong.matchmaking;

import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;

/** Bean class to store pairs of PlayerComponents ordered by its similarity from most similar to least */
public class PlayerComponentPair implements Comparable<PlayerComponentPair>{
	private final PlayerComponent one;
	private final PlayerComponent another;
	private final double similarity;
	
	public PlayerComponentPair(PlayerComponent one, PlayerComponent another, double similarity) {
		this.one = one;
		this.another = another;
		this.similarity = similarity;
	}

	public PlayerComponent getOne() {
		return one;
	}

	public PlayerComponent getAnother() {
		return another;
	}

	@Override
	public int compareTo(PlayerComponentPair o) {
		/** Inverted default natural order which is from min to max */
		return - Double.compare(similarity, o.similarity);
	}

	@Override
	public String toString() {
		return "PlayerComponentPair [one=" + one + ", another=" + another + ", similarity=" + similarity + "]";
	}

}
