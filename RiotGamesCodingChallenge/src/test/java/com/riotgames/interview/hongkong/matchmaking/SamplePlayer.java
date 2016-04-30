package com.riotgames.interview.hongkong.matchmaking;

public class SamplePlayer {

	private String name;
	private long wins;
	private long losses;

	public SamplePlayer(String name, long wins, long losses) {
		this.name = name;
		this.wins = wins;
		this.losses = losses;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getWins() {
		return wins;
	}

	public void setWins(long wins) {
		this.wins = wins;
	}

	public long getLosses() {
		return losses;
	}

	public void setLosses(long losses) {
		this.losses = losses;
	}

}
