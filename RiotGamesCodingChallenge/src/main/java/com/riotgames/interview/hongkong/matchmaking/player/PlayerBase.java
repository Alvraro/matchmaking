package com.riotgames.interview.hongkong.matchmaking.player;

import java.util.Collection;
import java.util.HashSet;

public class PlayerBase {
	/** Players blaming on us to start a f#%&! match :p */ 
	private HashSet<PlayerComponent> players;
	
	public PlayerBase(){
		players = new HashSet<PlayerComponent>();
	}

	/** Returns playerBase's size */
	public synchronized int size() {
		return players.size();
	}

	/** Removes all specified players */
	public synchronized void removeAll(Collection<PlayerComponent> playersToRemove) {
		players.removeAll(playersToRemove);
	}

	/** Returns current players */
	public synchronized HashSet<PlayerComponent> getPlayers() {
		return players;
	}

	/** Add a player */
	public synchronized void add(Player newPlayer) {
		players.add(newPlayer);
	}
}
