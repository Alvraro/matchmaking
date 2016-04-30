package com.riotgames.interview.hongkong.matchmaking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.riotgames.interview.hongkong.matchmaking.player.Player;

public class MatchmakerImplTest {

	@Test
	public void testFindMatch() {
		MatchmakerImpl matchmaker;
		try {
			matchmaker = new MatchmakerImpl();
		} catch (ConfigurationFailException e) {
			fail("Can't instantiate MatchmakerImpl: " + e.getMessage());
			return;
		}

		// Games can't start without players
		assertNull(matchmaker.findMatch(1));
		assertNull(matchmaker.findMatch(3));
		assertNull(matchmaker.findMatch(5));
		assertNull(matchmaker.findMatch(100));
	}

	@Test
	public void testFindMatchEdgeCases() {
		MatchmakerImpl matchmaker;
		try {
			matchmaker = new MatchmakerImpl();
		} catch (ConfigurationFailException e) {
			fail("Can't instantiate MatchmakerImpl: " + e.getMessage());
			return;
		}

		assertNull(matchmaker.findMatch(0));
		assertNull(matchmaker.findMatch(-1));
		assertNull(matchmaker.findMatch(-100));
	}

	@Test
	public void testEnterMatchmakingBadPlayers() {
		MatchmakerImpl matchmaker;
		try {
			matchmaker = new MatchmakerImpl();
		} catch (ConfigurationFailException e) {
			fail("Can't instantiate MatchmakerImpl: " + e.getMessage());
			return;
		}

		try{
			matchmaker.enterMatchmaking(new Player(null, 1, 1));
			fail("Exception not thrown");
		} catch (PlayerFormatException e){
			assertEquals("Name can't be null", e.getMessage());
		}

		try{
			matchmaker.enterMatchmaking(new Player("", 1, 1));
			fail("Exception not thrown");
		} catch (PlayerFormatException e){
			assertEquals("Name can't be empty", e.getMessage());
		}
		
		try{
			matchmaker.enterMatchmaking(new Player("asdasd", -1, 1));
			fail("Exception not thrown");
		} catch (PlayerFormatException e){
			assertEquals("Wins can't be negative", e.getMessage());
		}

		try{
			matchmaker.enterMatchmaking(new Player("sadadsa", 1000001, 1));
			fail("Exception not thrown");
		} catch (PlayerFormatException e){
			assertEquals("Wins can't be over 1000000", e.getMessage());
		}

		try{
			matchmaker.enterMatchmaking(new Player("asdasd", 1, -1));
			fail("Exception not thrown");
		} catch (PlayerFormatException e){
			assertEquals("Losses can't be negative", e.getMessage());
		}

		try{
			matchmaker.enterMatchmaking(new Player("sadadsa", 1, 10000001));
			fail("Exception not thrown");
		} catch (PlayerFormatException e){
			assertEquals("Losses can't be over 1000000", e.getMessage());
		}

		for(SamplePlayer samplePlayer : SampleData.getSamplePlayers())
		try {
			matchmaker.enterMatchmaking(new Player(samplePlayer.getName(), samplePlayer.getWins(), samplePlayer.getLosses()));
		} catch (PlayerFormatException e) {
			String name = e.getPlayer().getName();
			
			if(name.equalsIgnoreCase("Ramon Huff"))
				assertEquals("Wins can't be negative", e.getMessage());
			
			else if(name.equalsIgnoreCase("Ann Owen"))
				assertEquals("Wins can't be over 1000000", e.getMessage());
			
			else if(name.equalsIgnoreCase("Kurt Obrien"))
				assertEquals("Losses can't be negative", e.getMessage());
			
			else fail("Unexpected exception");
		}
	}
	
	@Test
	public void testEnterMatchmakingEdgePlayers() {
		MatchmakerImpl matchmaker;
		try {
			matchmaker = new MatchmakerImpl();
		} catch (ConfigurationFailException e) {
			fail("Can't instantiate MatchmakerImpl: " + e.getMessage());
			return;
		}

		try{
			matchmaker.enterMatchmaking(new Player("asdsasad", 0, 1));			
			matchmaker.enterMatchmaking(new Player("asdsasad", 1, 0));
			matchmaker.enterMatchmaking(new Player("asdsasad", 0, 0));
			matchmaker.enterMatchmaking(new Player("asdsasad", 1000000, 1));
			matchmaker.enterMatchmaking(new Player("asdsasad", 1, 1000000));
			matchmaker.enterMatchmaking(new Player("asdsasad", 1000000, 1000000));
		} catch (PlayerFormatException e){
			fail("Exception thrown");
		}
	}
}
