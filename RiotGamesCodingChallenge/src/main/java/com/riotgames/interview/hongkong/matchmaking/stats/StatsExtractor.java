package com.riotgames.interview.hongkong.matchmaking.stats;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Set;

import com.riotgames.interview.hongkong.matchmaking.Match;
import com.riotgames.interview.hongkong.matchmaking.player.Player;
import com.riotgames.interview.hongkong.matchmaking.skill.BasicSkillCalculator;

public class StatsExtractor {
	private final static String SEPARATOR = ";";
	
	/** List of registered match stats */
	private ArrayList<MatchStats> matchStats;
	
	public StatsExtractor(){
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
		}
		
		avgVictoryRate /= team.size();
		
		// Get stddev
		double stdDevVictoryRate = 0;
		for(Player player : team){
			double diff = (skillCalculator.getSkill(player) - avgVictoryRate);
			stdDevVictoryRate += diff * diff;
		}
		stdDevVictoryRate /= team.size();
		stdDevVictoryRate = Math.sqrt(stdDevVictoryRate);
		
		return new TeamStats(maxVictoryRate, minVictoryRate, avgVictoryRate, stdDevVictoryRate);
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
		
		// Header
		out.print("numMatch"+SEPARATOR);
		out.print("balanceScore"+SEPARATOR);
		out.println("abuseScore");
		
		double avgBalanceScore = 0;
		double avgAbuseScore = 0;
		
		// For each match
		int numMatch = 1;
		for(MatchStats matchStats : matchStats){
			double balanceScore = matchStats.getBalanceScore();
			double abuseScore = matchStats.getAbuseScore();

			avgBalanceScore += balanceScore;
			avgAbuseScore += abuseScore;
			
			if(printFullStats){
				out.print(numMatch++);
				out.print(SEPARATOR);
				
				out.print(formatter.format(matchStats.getBalanceScore()));
				out.print(SEPARATOR);
				
				out.print(formatter.format(matchStats.getAbuseScore()));
	 			
	 			out.println();
			}
		}
		
		// Average
		avgBalanceScore /= matchStats.size();
		avgAbuseScore /= matchStats.size();
		
		// Stddev
		double stddevBalanceScore = 0;
		double stddevAbuseScore = 0;
		for(MatchStats matchStats : matchStats){
			double balanceDiff = matchStats.getBalanceScore() - avgBalanceScore;
			stddevBalanceScore += (balanceDiff * balanceDiff);

			double abuseDiff = matchStats.getAbuseScore() - avgAbuseScore;
			stddevAbuseScore += (abuseDiff * abuseDiff);
		}
		stddevBalanceScore /= matchStats.size();
		stddevBalanceScore = Math.sqrt(stddevBalanceScore);
		
		stddevAbuseScore /= matchStats.size();
		stddevAbuseScore = Math.sqrt(stddevAbuseScore);
		
		out.println();
		out.print("-"+SEPARATOR);
		out.print(formatter.format(avgBalanceScore));
		out.print("("+formatter.format(stddevBalanceScore)+")");
		out.print(SEPARATOR);
		out.print(formatter.format(avgAbuseScore));
		out.print("("+formatter.format(stddevAbuseScore)+")");
	}
}
