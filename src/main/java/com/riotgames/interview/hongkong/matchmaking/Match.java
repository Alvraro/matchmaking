package com.riotgames.interview.hongkong.matchmaking;

import java.util.Set;

import com.riotgames.interview.hongkong.matchmaking.player.Player;

public class Match {

    private final Set<Player> team1;
    private final Set<Player> team2;
    
	private final long matchmakingStartTime;
	private final long matchmakingExitTime;
    
    public Match(Set<Player> team1, Set<Player> team2, long matchmakingStartTime, long matchmakingExitTime) {
        this.team1 = team1;
        this.team2 = team2;
        this.matchmakingStartTime = matchmakingStartTime;
        this.matchmakingExitTime = matchmakingExitTime;
        
        // Notify players they have match!
        notifyPlayers();
    }

    public Set<Player> getTeam1() {
        return team1;
    }

    public Set<Player> getTeam2() {
        return team2;
    }

    /** Notify players they have match so they have left the evil queue of waiting of horrible death */
    private void notifyPlayers(){
    	for(Player player : team1)
    		player.setMatchmakingExitTime(matchmakingExitTime);
    	
    	for(Player player : team2)
    		player.setMatchmakingExitTime(matchmakingExitTime);
    }

	public long getMatchmakingStartTime() {
		return matchmakingStartTime;
	}

	public long getMatchmakingExitTime() {
		return matchmakingExitTime;
	}
}
