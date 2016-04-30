package com.riotgames.interview.hongkong.matchmaking.stats;

public class MatchStats {

	private TeamStats team1Stats;
	private TeamStats team2Stats;

	public MatchStats(TeamStats team1Stats, TeamStats team2Stats) {
		this.team1Stats = team1Stats;
		this.team2Stats = team2Stats;
	}

	public TeamStats getTeam1Stats() {
		return team1Stats;
	}

	public TeamStats getTeam2Stats() {
		return team2Stats;
	}

	/**
	 * Returns balance score as the difference between team1 and team2 in avgVictoryRate
	 * An ideal value of 0 means that both teams have the same avg victory rate, so they're "balanced"  
	 * */
	public double getBalanceScore() {
		return Math.abs(team1Stats.getAvgVictoryRate() - team2Stats.getAvgVictoryRate());
	}
	
	/**
	 * 
	 * Returns abuse score as the diff between the best player in victoryRate of one team and the worst of the other.
	 * It's averaged over the 2 combinations: team1's best with team2's worst and vice versa.
	 * The higher this value is the higher potential risk exists a pro player abusing a n00b player even if the teams are balanced.
	 * */
	public double getAbuseScore() {
		return 
				(Math.abs(team1Stats.getMaxVictoryRate() - team2Stats.getMinVictoryRate())) +
				(Math.abs(team2Stats.getMaxVictoryRate() - team1Stats.getMinVictoryRate())) / 2;
	}
}
