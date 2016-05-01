package com.riotgames.interview.hongkong.matchmaking.matchmaker;

import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.riotgames.interview.hongkong.matchmaking.Match;
import com.riotgames.interview.hongkong.matchmaking.PlayerComponentPair;
import com.riotgames.interview.hongkong.matchmaking.matcher.Matcher;
import com.riotgames.interview.hongkong.matchmaking.player.Player;
import com.riotgames.interview.hongkong.matchmaking.player.PlayerBase;
import com.riotgames.interview.hongkong.matchmaking.player.PlayerComponent;
import com.riotgames.interview.hongkong.matchmaking.player.PlayerTeam;
import com.riotgames.interview.hongkong.matchmaking.skill.SkillCalculator;

/**
 * The matchmaking implementation that you will write.
 * Both interface's methods have been implemented so that they 'never' (;-) 'never', you know ;-)) throw exceptions.
 * In case of error -> check logger's output! otherwise let exceptions fly free.
 * On the other hand, MatchmakerImpl() constructor can complain if there are configuration problems.
 */
public class MatchmakerImpl implements Matchmaker {
	/** Path to the Matchmaker's configuration file */
	//private static final String MATCHMAKER_CONFIG_FILE = "src/main/resources/matchmaker.properties";

	/** Configuration properties */
	//private Properties properties;

	/** Current player base */
	private PlayerBase playerBase;

	/** Player matching algorithm */
	private Matcher<PlayerComponent> matcher;

	/** Player skill calculator algorithm */
	private SkillCalculator<PlayerComponent> skillCalculator;

	/** Cached similarities between player pairs */ 
	private PriorityQueue<PlayerComponentPair> similarities;

	/** Flag set to true if long queued players should have preference */
	private boolean longQueuedPreference;
	
	/** Logger for printing stuff */
	private static Logger logger = Logger.getLogger(MatchmakerImpl.class.toString());
	
	public MatchmakerImpl(SkillCalculator<PlayerComponent> skillCalculator, Matcher<PlayerComponent> matcher, boolean longQueuedPreference) {
		playerBase = new PlayerBase();
		
		similarities = new PriorityQueue<PlayerComponentPair>();
		
		/** Read properties file */		
		//properties = new Properties();
		//properties.load(new FileInputStream(MATCHMAKER_CONFIG_FILE));
		
		this.matcher = matcher;
		this.skillCalculator = skillCalculator;
		this.longQueuedPreference = longQueuedPreference;
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
			// TODO If more flexibility is desirable bring this stuff to a new AbstractFactory schema 
			PlayerTeam team1 = new PlayerTeam(playersPerTeam);
			PlayerTeam team2 = new PlayerTeam(playersPerTeam);
			long matchmakingStartTime = System.currentTimeMillis();
			
			// If there is preference for longest queued players
			if(longQueuedPreference){
				// Choose player queued for longest to avoid the possibility of infinite queue staying!
				PlayerComponent longestQueuedPlayer = playerBase.getLongestQueuedPlayer();
				
				// Add it to one team
				team1.addPlayerComponent(longestQueuedPlayer);

				// Find its best match
				// We can do it so by:
				// - Recalculating similarities: N matcher (potentially CPU cost expensive) evaluations
				// - Traversing cached similarities in the PriorityQueue: N^2 node visits (as PQ doesn't provide ordered traversing)
				// It's not clear which is the best way but finding the best is way more clearer :D
				// (and this is done only once so don't spend too much time thinking here :p)
				PlayerComponent bestMatch = null;
				double bestSimilarity = Double.NEGATIVE_INFINITY;
				
				// For each pair of players with similarity
				for(PlayerComponentPair pair : similarities){
					// If longestQueuedPlayer is present
					if(pair.getOne() == longestQueuedPlayer || pair.getAnother() == longestQueuedPlayer){
						double similarity = pair.getSimilarity();
						PlayerComponent one = pair.getOne();
						PlayerComponent another = pair.getAnother();
						
						// If it has better similarity
						if(similarity > bestSimilarity){
							// Update best similarity
							bestSimilarity = similarity;
							
							// Update bestMatch (to the other player in the pair)
							bestMatch = (one == longestQueuedPlayer) ? another : one;
						}
					}
				}
				
				// Health check
				if(bestMatch != null){
					// Add it to team2
					team2.addPlayerComponent(bestMatch);
				}

				// Check what you are doing wrong but proceed as if this had never happened o_O
				else{
					logger.severe("Player longestQueuedPlayer has not any similarities. Please check logic :(");
					team1 = new PlayerTeam(playersPerTeam);
				}
			}
			
			// While teams are not completed (we are filling both at the same time so we just need to check one) and there are similarities
			while((team1.getChildren().size() < playersPerTeam) && (similarities.size() > 0)){
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
				if(skillCalculator.getSkill(team1) > skillCalculator.getSkill(team2)){
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
				if(skillCalculator.getSkill(bestMatch.getOne()) > skillCalculator.getSkill(bestMatch.getAnother())){
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
			
			// Health check
			if(team1.getChildren().size() != playersPerTeam || team2.getChildren().size() != playersPerTeam){
				// Something went really wrong :|
				logger.severe("Matchmaking finished but teams are not completed. Please check logic :(");
				return null;
			}
			
			// Remove matched players from the player base
			playerBase.removeAll(team1.getChildren());
			playerBase.removeAll(team2.getChildren());
			
			// Deliver match! \m/
			return new Match(team1.getChildrenPlayers(), team2.getChildrenPlayers(), matchmakingStartTime, System.currentTimeMillis());
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
		
		if(playerBase.size() >= MAX_PLAYERS){
			logger.warning("Can't add more players at the moment");
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

	@Override
	public String toString() {
		return "MatchmakerImpl [matcher=" + matcher + ", skillCalculator=" + skillCalculator + ", longQueuedPreference="
				+ longQueuedPreference + "]";
	}
	
}
