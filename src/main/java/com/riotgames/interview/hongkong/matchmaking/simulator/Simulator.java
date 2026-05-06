package com.riotgames.interview.hongkong.matchmaking.simulator;

import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import com.riotgames.interview.hongkong.matchmaking.Match;
import com.riotgames.interview.hongkong.matchmaking.PlayerFormatException;
import com.riotgames.interview.hongkong.matchmaking.SampleData;
import com.riotgames.interview.hongkong.matchmaking.matchmaker.DefaultMatchmakerFactory;
import com.riotgames.interview.hongkong.matchmaking.matchmaker.Matchmaker;
import com.riotgames.interview.hongkong.matchmaking.matchmaker.RandomMatchmakerFactory;
import com.riotgames.interview.hongkong.matchmaking.matchmaker.SimpleSkillBasedMatchmakerFactory;
import com.riotgames.interview.hongkong.matchmaking.matchmaker.WeightedScoringMatchmakerFactory;
import com.riotgames.interview.hongkong.matchmaking.player.Player;
import com.riotgames.interview.hongkong.matchmaking.stats.StatsExtractor;

public class Simulator {
	private static final int PLAYERS_PER_TEAM = 5;
	
	private static final int PLAYER_BASE_SIZE = 100;
	
	/** Number of matches to generate */
	private static final int NUM_MATCHES = 10000;

	/** Number (smaller) of matches to generate for costly set-ups */
	private static final int NUM_MATCHES_SMALL = 3000;
	
	/** Weight increment for WeightedScoringMatchmaker tests */
	private static final double WEIGHT_INCREMENT = 0.25;

	/** Matchmaker to simulate */
	private Matchmaker matchmaker;
	
	/** Fixed size of the teams generated */
	private int playersPerTeam;
	
	/** We simulate a constant base size of players as the solution is not expected to be perfect at first */
	private int playerBaseSize;
	
	public Simulator(Matchmaker matchmaker, int playersPerTeam, int playerBaseSize) {
		this.matchmaker = matchmaker;
		this.playersPerTeam = playersPerTeam;
		this.playerBaseSize = playerBaseSize;
	}

	public static void main(String[] args) throws Exception {
		// Initialize pseudo-random number generator
		Random random = new Random(System.currentTimeMillis());

		// Create and simulate default matchmaker
		Matchmaker matchmaker = new DefaultMatchmakerFactory().createMatchMaker();
		Simulator simulator = new Simulator(matchmaker, PLAYERS_PER_TEAM, PLAYER_BASE_SIZE);
		simulator.execute(NUM_MATCHES, System.out, random);
				
		// Create and simulate random matchmaker
		matchmaker = new RandomMatchmakerFactory(random).createMatchMaker();
		simulator = new Simulator(matchmaker, PLAYERS_PER_TEAM, PLAYER_BASE_SIZE);
		simulator.execute(NUM_MATCHES, System.out, random);
		
		// Create and simulate SimpleSkillBasedMatchmaker without special preference for long queued players
		matchmaker = new SimpleSkillBasedMatchmakerFactory(false).createMatchMaker();
		simulator = new Simulator(matchmaker, PLAYERS_PER_TEAM, PLAYER_BASE_SIZE);
		simulator.execute(NUM_MATCHES, System.out, random);

		// Create and simulate SimpleSkillBasedMatchmakerFactory with special preference for long queued players 
		matchmaker = new SimpleSkillBasedMatchmakerFactory(true).createMatchMaker();
		simulator = new Simulator(matchmaker, PLAYERS_PER_TEAM, PLAYER_BASE_SIZE);
		simulator.execute(NUM_MATCHES_SMALL, System.out, random);
		
		// Create and simulate some WeightedScoringMatchmaker with different weight combinations
		for(double skillWeight = 0.0; skillWeight <= 1.0; skillWeight += WEIGHT_INCREMENT)
			for(double levelWeight = 0.0; levelWeight <= (1.0 - skillWeight); levelWeight += WEIGHT_INCREMENT){
				// Complete to 1.0
				double queueWeight = 1.0 - skillWeight - levelWeight;
				
				matchmaker = new WeightedScoringMatchmakerFactory(false, skillWeight, levelWeight, queueWeight).createMatchMaker();
				simulator = new Simulator(matchmaker, PLAYERS_PER_TEAM, PLAYER_BASE_SIZE);
				simulator.execute(NUM_MATCHES, System.out, random);
			}
	}

	private void execute(int numMatches, PrintStream out, Random random) throws PlayerFormatException {
		// Create stats extractor
		StatsExtractor statsExtractor = new StatsExtractor(this); 
		
		// Create player reserve from sample database
		LinkedList<Player> playerReserve = new LinkedList<Player>(SampleData.getPlayers());
		
		// Shuffle it
		Collections.shuffle(playerReserve);

		// Simulation start
		long simulationStart = System.currentTimeMillis();
		
		// Take initial number of random players from the reserve to enter the matchmaker
		for(int i=0; i<playerBaseSize; ++i){
			// Draw a player from the tail
			Player player = playerReserve.pollLast();

			// Insert player into the matchmaker
			matchmaker.enterMatchmaking(new Player(player));
		}
		
		// At this point, matchmaker has PLAYER_BASE_SIZE players
		
		// Repeat for each desired match
		for(int numMatch=0; numMatch<numMatches; ++numMatch){
			// Find match
			Match match = matchmaker.findMatch(playersPerTeam);

			// Matchmaker removes (2*PLAYERS_PER_TEAM) matched players so we need to replace them with random ones 
			for(int i=0; i<2*playersPerTeam; ++i){
				// Draw a player from the tail
				Player player = playerReserve.pollLast();

				// Insert player into the matchmaker
				matchmaker.enterMatchmaking(new Player(player));
			}
			
			// Extract stats from match found
			statsExtractor.registerMatch(match);
			
			// We assume match ends so matched players are eligible again for future random extractions
			playerReserve.addAll(match.getTeam1());
			playerReserve.addAll(match.getTeam2());
			
			// Shuffle the reserve
			Collections.shuffle(playerReserve);
		}
		
		// Simulation start
		long simulationEnd = System.currentTimeMillis();
		statsExtractor.registerSimulationTime(simulationEnd - simulationStart);
		
		// Print simulation stats
		statsExtractor.printStats(out, false);
	}

	@Override
	public String toString() {
		return "Simulator [matchmaker=" + matchmaker + ", playersPerTeam=" + playersPerTeam + ", playerBaseSize="
				+ playerBaseSize + "]";
	}
	
}
