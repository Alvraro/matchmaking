package com.riotgames.interview.hongkong.matchmaking;

import com.riotgames.interview.hongkong.matchmaking.player.Player;

public class PlayerFormatException extends Exception {
	private static final long serialVersionUID = 1L;
	private Player player;

	public PlayerFormatException(Player player, String message) {
		super(message);
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

}
