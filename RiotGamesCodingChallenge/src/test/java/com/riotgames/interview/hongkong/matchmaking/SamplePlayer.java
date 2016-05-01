package com.riotgames.interview.hongkong.matchmaking;

import com.riotgames.interview.hongkong.matchmaking.player.Player;

public class SamplePlayer {
	
	/** Total (fake) necessary games to reach max level */
	private static final long TOTAL_GAMES_FOR_MAX_LEVEL = 1000;
	
	private String name;
	private long wins;
	private long losses;
	private int level;

	public SamplePlayer(String name, long wins, long losses) {
		this.name = name;
		this.wins = wins;
		this.losses = losses;
		
		level = estimateLevel();
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

	/** Estimates player level for a SamplePlayer based on total games played */
	private int estimateLevel() {
		// Max level if the sample player has more than a ceratin number of games
		long totalGames = wins + losses;
		if(totalGames >= TOTAL_GAMES_FOR_MAX_LEVEL)
			return Player.MAX_LEVEL;
		
		else if (totalGames == 0)
			return Player.MIN_LEVEL;
		
		// 1) totalGames at this point -> [1, TOTAL - 1]
		// 2) / TOTAL -> (0, 1)
		// 3) * (MAX - MIN) -> (0, MAX - MIN)
		// 4) + MIN -> (MIN, MAX)
		return (int) ((totalGames / (double)TOTAL_GAMES_FOR_MAX_LEVEL) * (Player.MAX_LEVEL - Player.MIN_LEVEL) + Player.MIN_LEVEL); 
	}

	public int getLevel() {
		return level;
	}	
}
