package com.riotgames.interview.hongkong.matchmaking.stats;

public class TeamStats {
	
	private double maxVictoryRate;
	private double minVictoryRate;
	private double avgVictoryRate;
	private double stdDevVictoryRate;

	public TeamStats(double maxVictoryRate, double minVictoryRate, double avgVictoryRate, double stdDevVictoryRate) {
		this.maxVictoryRate = maxVictoryRate;
		this.minVictoryRate = minVictoryRate;
		this.avgVictoryRate = avgVictoryRate;
		this.stdDevVictoryRate = stdDevVictoryRate;
	}

	public double getMaxVictoryRate() {
		return maxVictoryRate;
	}

	public double getMinVictoryRate() {
		return minVictoryRate;
	}

	public double getAvgVictoryRate() {
		return avgVictoryRate;
	}

	public double getStdDevVictoryRate() {
		return stdDevVictoryRate;
	}

}
