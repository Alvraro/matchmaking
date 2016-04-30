package com.riotgames.interview.hongkong.matchmaking;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.riotgames.interview.hongkong.matchmaking.matcher.MatchingAlgorithm;
import com.riotgames.interview.hongkong.matchmaking.player.Player;
import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;
import com.riotgames.interview.hongkong.matchmaking.player.PlayerTeam;
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
	private ArrayList<PlayerComponent> players;

	/** Player matching algorithm */
	private MatchingAlgorithm<PlayerComponent> matcher;

	/** Player skill calculator algorithm */
	private SkillCalculatorAlgorithm<PlayerComponent> skillCalc;

	/** Cached similarities between player pairs */ 
	private PriorityQueue<PlayerComponentPair> similarities;
	
	/** Logger for printing stuff */
	private static Logger logger = Logger.getLogger(MatchmakerImpl.class.toString());
	
	//PriorityQueue<>

	@SuppressWarnings("unchecked")
	public MatchmakerImpl() throws ConfigurationFailException {
		// Try to configure our matchmaker or epic fail
		try{
			players = new ArrayList<PlayerComponent>();
			
			similarities = new PriorityQueue<PlayerComponentPair>();
			
			/** Read properties file */		
			properties = new Properties();
			properties.load(new FileInputStream(MATCHMAKER_CONFIG_FILE));
			
			// TODO Find a cleaner way to initialize all this stuff which is far from beautiful and not really flexible :/
			skillCalc = (SkillCalculatorAlgorithm<PlayerComponent>) 
					Class.forName(properties.getProperty("skill.calculator.class")).newInstance();

			matcher = (MatchingAlgorithm<PlayerComponent>) 
					Class.forName(properties.getProperty("matching.algorithm.class")).
					getConstructor(SkillCalculatorAlgorithm.class).
					newInstance(skillCalc);
		}
		
		catch(Exception e){
			throw new ConfigurationFailException(e);
		}
	}

	public synchronized Match findMatch(int playersPerTeam) {
		try{
			// Sure I was sure to implement this :P
			
			// PlayersPerTeam must be > 0
			if(playersPerTeam <= 0){
				logger.warning("PlayersPerTeam must be > 0");
				return null;
			}
			
			// If there are not enough players to fill the teams, unless we want to use bots, there's little we can do ^_^
			if(players.size() < 2 * playersPerTeam){
				logger.warning((2 * playersPerTeam) + " players are needed to start a "+playersPerTeam+"v"+playersPerTeam+" game and there are only "+players.size());
				return null;
			}
	
			// Initialize teams
			PlayerTeam team1 = new PlayerTeam(playersPerTeam);
			PlayerTeam team2 = new PlayerTeam(playersPerTeam);
			
			// For each team member
			while(team1.getChildren().size() < playersPerTeam){
				// Choose the 2 most similar players
				PlayerComponentPair bestMatch = similarities.poll();
				
				// Ignore match if any of the players are already assigned to a team
				if(bestMatch.getOne().getTeamAssigned() != null || bestMatch.getAnother().getTeamAssigned() != null){
					continue;
				}
				
				// Put them in different teams
				team1.addPlayerComponent(bestMatch.getOne());
				team2.addPlayerComponent(bestMatch.getAnother());
			}
					
			return new Match(team1.getChildrenPlayers(), team2.getChildrenPlayers());
		} 
		
		catch(Exception e){
			logger.log(Level.SEVERE, "Error in findMatch algorithm", e);
			return null;
		}
	}

	public synchronized void enterMatchmaking(Player newPlayer) {
		// Calculate newPlayer similarities to the rest of players
		for(PlayerComponent player : players){
			double similarity = matcher.getSimilarity(player, newPlayer);
			similarities.add(new PlayerComponentPair(player, newPlayer, similarity));
		}
		
		// Store time at which he/she enters the system 
		newPlayer.setMatchmakingEnterTime(System.currentTimeMillis());
		
		// Store new player
		players.add(newPlayer);
	}

}
