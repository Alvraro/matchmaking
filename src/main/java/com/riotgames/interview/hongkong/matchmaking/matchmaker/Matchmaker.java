package com.riotgames.interview.hongkong.matchmaking.matchmaker;

import com.riotgames.interview.hongkong.matchmaking.Match;
import com.riotgames.interview.hongkong.matchmaking.player.Player;

public interface Matchmaker {
	/** Maximum players allowed per team */
	public static final int MAX_PLAYERS_PER_TEAM = 10; // Double Pentakill mode!
	
	/** Maximum players allowed to enter the Matchmaking system */
	public static final int MAX_PLAYERS = 9000;
	
    /**
     * <p>
     * Find a match with the given number of players per team.
     * </p>
     * 
     * @param playersPerTeam
     *            the number of players required in each team
     * @return an appropriate match or null if there is no appropriate match
     */
    Match findMatch(int playersPerTeam);

    /**
     * <p>
     * Add a player for matching.
     * </p>
     */
    void enterMatchmaking(Player player);
}
