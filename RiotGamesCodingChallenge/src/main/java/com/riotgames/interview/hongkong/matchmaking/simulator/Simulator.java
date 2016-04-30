package com.riotgames.interview.hongkong.matchmaking.simulator;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.riotgames.interview.hongkong.matchmaking.Match;
import com.riotgames.interview.hongkong.matchmaking.PlayerFormatException;
import com.riotgames.interview.hongkong.matchmaking.SampleData;
import com.riotgames.interview.hongkong.matchmaking.matchmaker.DefaultMatchmakerFactory;
import com.riotgames.interview.hongkong.matchmaking.matchmaker.Matchmaker;
import com.riotgames.interview.hongkong.matchmaking.player.Player;
import com.riotgames.interview.hongkong.matchmaking.stats.StatsExtractor;

public class Simulator {
	private static final int PLAYERS_PER_TEAM = 5;
	
	private static final int PLAYER_BASE_SIZE = 100;
	
	/** Number of matches to generate */
	private static final int NUM_MATCHES = 10;

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
		Random random = new Random(new Date().getTime());

		// Create matchmaker
		Matchmaker matchmaker = new DefaultMatchmakerFactory().createMatchMaker();

		// Create simulator
		Simulator simulator = new Simulator(matchmaker, PLAYERS_PER_TEAM, PLAYER_BASE_SIZE);
		
		// Execute
		simulator.execute(NUM_MATCHES, System.out, random);
	}

	private void execute(int numMatches, PrintStream out, Random random) throws PlayerFormatException {
		// Create stats extractor
		StatsExtractor statsExtractor = new StatsExtractor(); 
		
		// Read sample player database
		List<Player> samplePlayerList = SampleData.getPlayers();
		Collections.shuffle(samplePlayerList, random);
		
		// And add them to the player reserve
		HashSet<Player> playerReserve = new HashSet<Player>(samplePlayerList);
		
		// Take initial number of random players from the reserve to enter the matchmaker
		Iterator<Player> it = playerReserve.iterator();
		for(int i=0; i<playerBaseSize; ++i){
			// Insert player into the matchmaker
			matchmaker.enterMatchmaking(new Player(it.next()));
			
			// Then remove it from set to avoid introducing the same player
			it.remove();
		}
		
		// At this point, matchmaker has PLAYER_BASE_SIZE players
		
		// Repeat for each desired match
		for(int numMatch=0; numMatch<numMatches; ++numMatch){
			// Find match
			Match match = matchmaker.findMatch(playersPerTeam);

			// Matchmaker removes (2*PLAYERS_PER_TEAM) matched players so we need to replace them with random ones 
			it = playerReserve.iterator();
			for(int i=0; i<2*playersPerTeam; ++i){
				// Insert player into the matchmaker
				matchmaker.enterMatchmaking(new Player(it.next()));
				
				// Then remove it from set to avoid introducing the same player
				it.remove();
			}
			
			// Extract stats from match found
			statsExtractor.registerMatch(match);
			
			// We assume match ends so matched players are eligible again for future random extractions
			playerReserve.addAll(match.getTeam1());
			playerReserve.addAll(match.getTeam2());
		}
		
		// Print simulation stats
		statsExtractor.printStats(out, true);
	}
}
