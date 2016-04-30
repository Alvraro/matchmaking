package com.riotgames.interview.hongkong.matchmaking;

import java.io.FileInputStream;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.riotgames.interview.hongkong.matchmaking.matcher.MatchingAlgorithm;
import com.riotgames.interview.hongkong.matchmaking.player.Player;
import com.riotgames.interview.hongkong.matchmaking.player.PlayerBase;
import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;
import com.riotgames.interview.hongkong.matchmaking.player.PlayerTeam;
import com.riotgames.interview.hongkong.matchmaking.skill.SkillCalculatorAlgorithm;

/**
 * The matchmaking implementation that you will write.
 * Both interface's methods have been implemented so that they 'never' (;-) 'never', you know ;-)) throw exceptions.
 * In case of error -> check logger's output! otherwise let exceptions fly free.
 * On the other hand, MatchmakerImpl() constructor can complain if there are configuration problems.
 */
public class MatchmakerImpl implements Matchmaker {
	/** Path to the Matchmaker's configuration file */
	private static final String MATCHMAKER_CONFIG_FILE = "src/main/resources/matchmaker.properties";

	/** Maximum players per team */
	public static final int MAX_PLAYERS_PER_TEAM = 10; // Double Pentakill mode!
	
	/** Configuration properties */
	private Properties properties;

	/** Current player base */
	private PlayerBase playerBase;

	/** Player matching algorithm */
	private MatchingAlgorithm<PlayerComponent> matcher;

	/** Player skill calculator algorithm */
	private SkillCalculatorAlgorithm<PlayerComponent> skillCalc;

	/** Cached similarities between player pairs */ 
	private PriorityQueue<PlayerComponentPair> similarities;
	
	/** Logger for printing stuff */
	private static Logger logger = Logger.getLogger(MatchmakerImpl.class.toString());
	
	@SuppressWarnings("unchecked") // Reflection warnings are ugly :|
	public MatchmakerImpl() throws ConfigurationFailException {
		// Try to configure our matchmaker or epic fail
		try{
			playerBase = new PlayerBase();
			
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
			
			// PlayersPerTeam must be <= MAX_PLAYERS_PER_TEAM
			if(playersPerTeam > MAX_PLAYERS_PER_TEAM){
				logger.warning("PlayersPerTeam must be <= 0");
				return null;
			}
			
			// If there are not enough players to fill the teams, unless we want to use bots, there's little we can do ^_^
			if(playerBase.size() < 2 * playersPerTeam){
				logger.warning((2 * playersPerTeam) + " players are needed to start a "+playersPerTeam+"v"+playersPerTeam+" game and there are only "+playerBase.size());
				return null;
			}
	
			// Initialize teams
			PlayerTeam team1 = new PlayerTeam(playersPerTeam);
			PlayerTeam team2 = new PlayerTeam(playersPerTeam);
			
			// While teams are not completed (we are filling both at the same time so we just need to check one)
			while(team1.getChildren().size() < playersPerTeam){
				// Choose the 2 most similar players
				PlayerComponentPair bestMatch = similarities.poll();
				
				// Ignore match if any of the players is already assigned to a team
				// Doing this we do no have to traverse similarity matrix to remove appearances of a player when he/she's matched
				if(bestMatch.getOne().getTeamAssigned() != null || bestMatch.getAnother().getTeamAssigned() != null){
					continue;
				}
				
				// Determine which team has the highest and lowest skill
				PlayerTeam highestTeam;
				PlayerTeam lowesTeam;
				if(skillCalc.getSkill(team1) > skillCalc.getSkill(team2)){
					highestTeam = team1;
					lowesTeam = team2;
				}
				else{
					highestTeam = team2;
					lowesTeam = team1;			
				}

				// And do the same for players
				PlayerComponent highestPlayer;
				PlayerComponent lowestPlayer;
				if(skillCalc.getSkill(bestMatch.getOne()) > skillCalc.getSkill(bestMatch.getAnother())){
					highestPlayer = bestMatch.getOne();
					lowestPlayer = bestMatch.getAnother();
				}
				else{
					highestPlayer = bestMatch.getAnother();
					lowestPlayer = bestMatch.getOne();			
				}
				
				// Put them in different yet balanced teams
				// The player with lowest skill to the team with highest skill and vice versa
				highestTeam.addPlayerComponent(lowestPlayer);
				lowesTeam.addPlayerComponent(highestPlayer);
			}
				
			// Remove matched players from the player base
			playerBase.removeAll(team1.getChildren());
			playerBase.removeAll(team2.getChildren());
			
			// Deliver match! \m/
			return new Match(team1.getChildrenPlayers(), team2.getChildrenPlayers());
		} 
		
		catch(Exception e){
			logger.log(Level.SEVERE, "Error in findMatch", e);
			return null;
		}
	}

	public synchronized void enterMatchmaking(Player newPlayer) {
		if(newPlayer==null){
			logger.warning("Can't add a null player");
			return;
		}
		
		try{
			// Calculate newPlayer similarities to the rest of current players/teams in progress
			for(PlayerComponent player : playerBase.getPlayers()){
				double similarity = matcher.getSimilarity(player, newPlayer);
				similarities.add(new PlayerComponentPair(player, newPlayer, similarity));
			}
			
			// Store time at which he/she enters the system 
			newPlayer.setMatchmakingEnterTime(System.currentTimeMillis());
			
			// Store new player
			playerBase.add(newPlayer);
		} catch(Exception e){
			logger.log(Level.SEVERE, "Error in enterMatchmaking");
			return;
		}
	}

}
