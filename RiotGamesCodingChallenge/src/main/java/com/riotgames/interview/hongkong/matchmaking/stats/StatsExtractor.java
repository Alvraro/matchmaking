package com.riotgames.interview.hongkong.matchmaking.stats;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Set;

import com.riotgames.interview.hongkong.matchmaking.Match;
import com.riotgames.interview.hongkong.matchmaking.player.Player;
import com.riotgames.interview.hongkong.matchmaking.simulator.Simulator;
import com.riotgames.interview.hongkong.matchmaking.skill.BasicSkillCalculator;

/**
 * Class to extract statistics/scores about an algorithm's performance so different approaches can be compared.
 * Scores are averaged over the number of matches executed.
 * 
 * - Balance score [0-1]: Difference between team1 and team2 in avgVictoryRate of their members.
 * An ideal value of 0 means that both teams have the same avg victory rate, so they're "balanced".  
 * 
 * - Abuse score [0-1]: Difference between the best player in victoryRate of one team and the worst of the other.
 * It's averaged over the 2 combinations: team1's best with team2's worst and vice versa.
 * The higher this value is the higher potential risk exists a pro player abusing a n00b player even if the teams are balanced.
 */
public class StatsExtractor {
	private final static String SEPARATOR = ";";
	
	/** List of registered match stats */
	private ArrayList<MatchStats> matchStats;

	/** Parent simulator to which statistics belong */
	private Simulator simulator;

	private long simulationTime;
	
	public StatsExtractor(Simulator simulator){
		this.simulator = simulator;
		matchStats = new ArrayList<MatchStats>();
	}
	
	public void registerMatch(Match match) {
		TeamStats team1Stats = getTeamStats(match.getTeam1());
		TeamStats team2Stats = getTeamStats(match.getTeam2());
		
		matchStats.add(new MatchStats(team1Stats, team2Stats));
	}

	private TeamStats getTeamStats(Set<Player> team) {
		double maxVictoryRate = Double.NEGATIVE_INFINITY;
		double minVictoryRate = Double.POSITIVE_INFINITY;
		double avgVictoryRate = 0;
		
		double maxQueueTime = Double.NEGATIVE_INFINITY;
		double minQueueTime = Double.POSITIVE_INFINITY;
		double avgQueueTime = 0;

		// We reuse basicSkillCalculator here as it gives exactly the victory rate we want
		BasicSkillCalculator skillCalculator = new BasicSkillCalculator();
		
		// Get max, min & avg
		for(Player player : team){
			double victoryRate = skillCalculator.getSkill(player);
						
			if(victoryRate > maxVictoryRate)
				maxVictoryRate = victoryRate;
			
			if(victoryRate < minVictoryRate)
				minVictoryRate = victoryRate;
			
			avgVictoryRate += victoryRate;
			
			double queueTime = player.getMatchmakingExitTime() - player.getMatchmakingEnterTime();
			
			if(queueTime > maxQueueTime)
				maxQueueTime = queueTime;
			
			if(queueTime < minQueueTime)
				minQueueTime = queueTime;
			
			avgQueueTime += queueTime;
		}
		avgVictoryRate /= team.size();
		avgQueueTime /= team.size();
		
		// Get stddev
		// TODO Repeat for QueueTime in case it's useful...
		double stdDevVictoryRate = 0;
		for(Player player : team){
			double diff = (skillCalculator.getSkill(player) - avgVictoryRate);
			stdDevVictoryRate += diff * diff;
		}
		stdDevVictoryRate /= team.size();
		stdDevVictoryRate = Math.sqrt(stdDevVictoryRate);
		
		return new TeamStats(
				maxVictoryRate, minVictoryRate, avgVictoryRate, stdDevVictoryRate,
				maxQueueTime, minQueueTime, avgQueueTime
		);
	}

	/** 
	 * Prints extracted stats
	 * @param out Desired output PrintStream
	 * @param printFullStats True to print stats per iteration plus summary. False to just print summary 
	 * */
	public void printStats(PrintStream out, boolean printFullStats){
		NumberFormat formatter = DecimalFormat.getNumberInstance();
		formatter.setMaximumFractionDigits(4);
		formatter.setMinimumFractionDigits(4);

		// Title
		out.println("Results in "+simulationTime+"ms for " + simulator);
		
		// Header
		out.print("numMatch"+SEPARATOR);
		out.print("balanceScore"+SEPARATOR);
		out.print("abuseScore"+SEPARATOR);
		out.print("avgQueueTime(ms)"+SEPARATOR);
		out.print("maxQueueTime(ms)");
		out.println();
		
		double avgBalanceScore = 0;
		double avgAbuseScore = 0;
		double simulationAvgQueueTime = 0;
		double simulationMaxQueueTime = Double.NEGATIVE_INFINITY;
		
		// For each match
		int numMatch = 1;
		for(MatchStats matchStats : matchStats){
			double matchBalanceScore = matchStats.getBalanceScore();
			double matchAbuseScore = matchStats.getAbuseScore();
			double matchAvgQueueTime = matchStats.getAvgQueueTime();
			double matchMaxQueueTime = matchStats.getMaxQueueTime();

			avgBalanceScore += matchBalanceScore;
			avgAbuseScore += matchAbuseScore;
			simulationAvgQueueTime += matchAvgQueueTime;
			
			if(printFullStats){
				out.print(numMatch++);
				out.print(SEPARATOR);
				
				out.print(formatter.format(matchBalanceScore));
				out.print(SEPARATOR);
				
				out.print(formatter.format(matchAbuseScore));
				out.print(SEPARATOR);
				
				out.print(formatter.format(matchAvgQueueTime));
				out.print(SEPARATOR);

				out.print(formatter.format(matchMaxQueueTime));
				
	 			out.println();
			}
			
			if(matchMaxQueueTime > simulationMaxQueueTime)
				simulationMaxQueueTime = matchMaxQueueTime;
		}
		
		// Average
		avgBalanceScore /= matchStats.size();
		avgAbuseScore /= matchStats.size();
		simulationAvgQueueTime /= matchStats.size();
		
		// Stddev
		double stddevBalanceScore = 0;
		double stddevAbuseScore = 0;
		double stddevQueueTime = 0;
		for(MatchStats matchStats : matchStats){
			double balanceDiff = matchStats.getBalanceScore() - avgBalanceScore;
			stddevBalanceScore += (balanceDiff * balanceDiff);

			double abuseDiff = matchStats.getAbuseScore() - avgAbuseScore;
			stddevAbuseScore += (abuseDiff * abuseDiff);
			
			double queueTimeDiff = matchStats.getAvgQueueTime() - simulationAvgQueueTime;
			stddevQueueTime += (queueTimeDiff * queueTimeDiff);
			
		}
		stddevBalanceScore /= matchStats.size();
		stddevBalanceScore = Math.sqrt(stddevBalanceScore);
		
		stddevAbuseScore /= matchStats.size();
		stddevAbuseScore = Math.sqrt(stddevAbuseScore);

		stddevQueueTime /= matchStats.size();
		stddevQueueTime = Math.sqrt(stddevQueueTime);
		
		out.print("avg(stddev) over " + matchStats.size() + " matches" + SEPARATOR);
		out.print(formatter.format(avgBalanceScore));
		out.print("("+formatter.format(stddevBalanceScore)+")");
		out.print(SEPARATOR);
		out.print(formatter.format(avgAbuseScore));
		out.print("("+formatter.format(stddevAbuseScore)+")");
		out.print(SEPARATOR);
		out.print(formatter.format(simulationAvgQueueTime));
		out.print("("+formatter.format(stddevQueueTime)+")");
		out.print(SEPARATOR);
		out.print(formatter.format(simulationMaxQueueTime));
		out.println();
		out.println();
	}

	public void registerSimulationTime(long simulationTime) {
		this.simulationTime = simulationTime;
	}
}
