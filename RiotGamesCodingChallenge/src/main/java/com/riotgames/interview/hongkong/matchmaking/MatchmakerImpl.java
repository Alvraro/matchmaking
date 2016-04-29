package com.riotgames.interview.hongkong.matchmaking;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

import com.riotgames.interview.hongkong.matchmaking.matcher.MatchingAlgorithm;
import com.riotgames.interview.hongkong.matchmaking.skill.SkillCalculatorAlgorithm;

/**
 * The matchmaking implementation that you will write.
 */
public class MatchmakerImpl implements Matchmaker {
	/** Path to the Matchmaker's configuration file */
	private static final String MATCHMAKER_CONFIG_FILE = "src/main/resources/matchmaker.properties";

	/** Configuration properties */
	private Properties properties;

	/** Players blaming on us to start the f#%&! match :p */ 
	private ArrayList<Player> players;

	/** Player matching algorithm */
	private MatchingAlgorithm<Player> matcher;

	/** Player skill calculator algorithm */
	private SkillCalculatorAlgorithm<Player> skillCalc;

	//PriorityQueue<>

	@SuppressWarnings("unchecked")
	public MatchmakerImpl() throws ConfigurationFailException {
		// Try to configure our matchmaker or epic fail
		try{
			/** Read properties file */		
			properties = new Properties();
			properties.load(new FileInputStream(MATCHMAKER_CONFIG_FILE));
			
			// TODO Find a cleaner way to initialize stuff
			matcher = (MatchingAlgorithm<Player>) 
					Class.forName(properties.getProperty("matching.algorithm.class")).newInstance();
			
			skillCalc = (SkillCalculatorAlgorithm<Player>) 
					Class.forName(properties.getProperty("skill.calculator.class")).newInstance();
		}
		
		catch(Exception e){
			throw new ConfigurationFailException(e);
		}
	}

	public synchronized Match findMatch(int playersPerTeam) {
		// Sure I was sure to implement this :D
		
		// If there are not enough players to fill the teams, unless we want to use bots, there's little we can do ^_^
		if(players.size() < 2 * playersPerTeam)
			return null;
		
		// We start choosing the most similar players
		
		return null;
	}

	public synchronized void enterMatchmaking(Player player) {
		// Store player
		players.add(player);

		// Store time at which he/she enters the system 
		player.setMatchmakingEnterTime(System.currentTimeMillis());
	}

}
