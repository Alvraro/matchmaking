package com.riotgames.interview.hongkong.matchmaking;

import java.util.Set;

import com.riotgames.interview.hongkong.matchmaking.player.Player;

public class Match {

    private final Set<Player> team1;
    private final Set<Player> team2;

    public Match(Set<Player> team1, Set<Player> team2) {
        this.team1 = team1;
        this.team2 = team2;
    }

    public Set<Player> getTeam1() {
        return team1;
    }

    public Set<Player> getTeam2() {
        return team2;
    }

}
