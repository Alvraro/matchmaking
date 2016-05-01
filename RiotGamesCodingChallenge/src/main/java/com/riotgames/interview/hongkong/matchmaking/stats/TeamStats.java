package com.riotgames.interview.hongkong.matchmaking.stats;

public class TeamStats {
	// Stats about team players' victory rates
	private final double maxVictoryRate;
	private final double minVictoryRate;
	private final double avgVictoryRate;
	private final double stdDevVictoryRate;
	
	// Stats about team players' queue staying time
	private final double maxQueueTime;
	private final double minQueueTime;
	private final double avgQueueTime;
	//private final double stdDevQueueTime; // TODO
	
	public TeamStats(
			double maxVictoryRate, double minVictoryRate, double avgVictoryRate, double stdDevVictoryRate,
			double maxQueueTime, double minQueueTime, double avgQueueTime) {
		this.maxVictoryRate = maxVictoryRate;
		this.minVictoryRate = minVictoryRate;
		this.avgVictoryRate = avgVictoryRate;
		this.stdDevVictoryRate = stdDevVictoryRate;
		this.maxQueueTime = maxQueueTime;
		this.minQueueTime = minQueueTime;
		this.avgQueueTime = avgQueueTime;
		//this.stdDevQueueTime = stdDevQueueTime;
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

	public double getMaxQueueTime() {
		return maxQueueTime;
	}

	public double getMinQueueTime() {
		return minQueueTime;
	}

	public double getAvgQueueTime() {
		return avgQueueTime;
	}

	/*public double getStdDevQueueTime() {
		return stdDevQueueTime;
	}*/

}
