package com.riotgames.interview.hongkong.matchmaking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Set;

import org.junit.Test;

import com.riotgames.interview.hongkong.matchmaking.player.Player;

public class MatchmakerImplTest {


	/** Matches can't be found without players */ 
	@Test
	public void testFindMatchWithoutPlayers() {
		MatchmakerImpl matchmaker;
		try {
			matchmaker = new MatchmakerImpl();
		} catch (ConfigurationFailException e) {
			fail("Can't instantiate MatchmakerImpl: " + e.getMessage());
			return;
		}

		assertNull(matchmaker.findMatch(1));
		assertNull(matchmaker.findMatch(3));
		assertNull(matchmaker.findMatch(5));
		assertNull(matchmaker.findMatch(100));
		assertNull(matchmaker.findMatch(MatchmakerImpl.MAX_PLAYERS_PER_TEAM));
		assertNull(matchmaker.findMatch(MatchmakerImpl.MAX_PLAYERS_PER_TEAM+1));
		assertNull(matchmaker.findMatch(0));
		assertNull(matchmaker.findMatch(-1));
		assertNull(matchmaker.findMatch(-100));
	}
	
	/** Matches can't be found for bad cases even with players */ 
	@Test
	public void testFindMatchBadCasesWithPlayers() {
		MatchmakerImpl matchmaker;
		try {
			matchmaker = new MatchmakerImpl();
		} catch (ConfigurationFailException e) {
			fail("Can't instantiate MatchmakerImpl: " + e.getMessage());
			return;
		}

		// Add players
		for(Player player : SampleData.getPlayers())
			matchmaker.enterMatchmaking(player);
		
		// Bad cases
		assertNull(matchmaker.findMatch(MatchmakerImpl.MAX_PLAYERS_PER_TEAM+1));
		assertNull(matchmaker.findMatch(0));
		assertNull(matchmaker.findMatch(-1));
		assertNull(matchmaker.findMatch(-100));
		
		// Valid edge case
		assertNotNull(matchmaker.findMatch(MatchmakerImpl.MAX_PLAYERS_PER_TEAM));
	}

	/** Test player creation errors */
	@Test
	public void testEnterMatchmakingBadPlayers() {
		MatchmakerImpl matchmaker;
		try {
			matchmaker = new MatchmakerImpl();
		} catch (ConfigurationFailException e) {
			fail("Can't instantiate MatchmakerImpl: " + e.getMessage());
			return;
		}

		// Null players are simply ignored. An exception could be thrown instead but I want to respect the original interface  
		matchmaker.enterMatchmaking(null);
		
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
			matchmaker.enterMatchmaking(new Player("sadadsa", Player.MAX_WINS+1, 1));
			fail("Exception not thrown");
		} catch (PlayerFormatException e){
			assertEquals("Wins can't be over "+Player.MAX_WINS, e.getMessage());
		}

		try{
			matchmaker.enterMatchmaking(new Player("asdasd", 1, -1));
			fail("Exception not thrown");
		} catch (PlayerFormatException e){
			assertEquals("Losses can't be negative", e.getMessage());
		}

		try{
			matchmaker.enterMatchmaking(new Player("sadadsa", 1, Player.MAX_LOSSES+1));
			fail("Exception not thrown");
		} catch (PlayerFormatException e){
			assertEquals("Losses can't be over "+Player.MAX_LOSSES, e.getMessage());
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
	
	/** Test player creation at edge cases */
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
			matchmaker.enterMatchmaking(new Player("asdsasad", Player.MAX_WINS, 1));
			matchmaker.enterMatchmaking(new Player("asdsasad", 1, Player.MAX_LOSSES));
			matchmaker.enterMatchmaking(new Player("asdsasad", Player.MAX_WINS, Player.MAX_LOSSES));
		} catch (PlayerFormatException e){
			fail("No exceptions allowed! >_<" + e);
		}
	}
	
	/** Test basic 1v1 FindMatch WITHOUT concurrency. If I can't do this I'm a loser */
	@Test
	public void testFindMatch1v1NotConcurrent() {
		try{
			// Create matchmaker
			MatchmakerImpl matchmaker = new MatchmakerImpl();
			// Create 2 basic players
			Player p1 = new Player("p1", 100, 100);
			Player p2 = new Player("p2", 100, 100);
			
			// Enter p1
			matchmaker.enterMatchmaking(p1);
			
			// No match can be found yet
			assertNull(matchmaker.findMatch(1));

			// Enter p2
			matchmaker.enterMatchmaking(p2);
			Match matchFound = matchmaker.findMatch(1);
			assertNotNull(matchFound);
			
			Set<Player> team1 = matchFound.getTeam1();
			Set<Player> team2 = matchFound.getTeam2();
			
			assertTrue(team1!=null && team1.size()==1 && team2!=null && team2.size()==1);
			
			// Order is not relevant, but if team1 contains p1 then team2 contains p2 and vice versa
			if(team1.iterator().next() == p1)
				assertTrue(team2.iterator().next() == p2);
			else if(team1.iterator().next() == p2)
				assertTrue(team2.iterator().next() == p1);
			else
				fail();
				
		} catch(Exception e){
			e.printStackTrace();
			fail("No exceptions allowed! >_<: " + e);
		}
	}

	/** 
	 * Test several basic FindMatch in a row WITHOUT concurrency and a perfect match 
	 * */
	@Test
	public void testFindPerfectMatchNotConcurrent() {
		try{
			// Create matchmaker
			MatchmakerImpl matchmaker = new MatchmakerImpl();
		
			for(int n=1; n<=MatchmakerImpl.MAX_PLAYERS_PER_TEAM; ++n)
				testFindPerfectMatchNvNNotConcurrent(matchmaker, n);
			
		} catch(Exception e){
			e.printStackTrace();
			fail("No exceptions allowed! >_<: " + e);
		}
	}
	
	/** 
	 * Test a basic NvN FindMatch WITHOUT concurrency and a perfect match. 
	 * N random players are created and cloned in name, skill, etc. so teams have to be identical 
	 * */
	public void testFindPerfectMatchNvNNotConcurrent(MatchmakerImpl matchmaker, int n) {
		try{
			// Create a list of N players and another one with N copied players 
			ArrayList<Player> players = new ArrayList<Player>(n);
			ArrayList<Player> copyPlayers = new ArrayList<Player>(n);
			for(int i=1; i<=n; ++i){
				// Add an arbitrary random player to players
				Player p = new Player("p"+i, 10+i, 20+i);
				players.add(p);
				
				// And add a copy to copyPlayers
				copyPlayers.add(new Player(p));
			}

			for(int i=0; i<(n-1); ++i){
				matchmaker.enterMatchmaking(players.get(i));
				
				// No match can be found yet
				assertNull(matchmaker.findMatch(n));
				
				matchmaker.enterMatchmaking(copyPlayers.get(i));
				
				// No match can be found yet
				assertNull(matchmaker.findMatch(n));
			}
			
			// Enter last players
			matchmaker.enterMatchmaking(players.get(n-1));
			
			// No match can be found yet
			assertNull(matchmaker.findMatch(n));

			// Match should be found after adding last player
			matchmaker.enterMatchmaking(copyPlayers.get(n-1));
			Match matchFound = matchmaker.findMatch(n);
			assertNotNull(matchFound);
			
			// Teams should have identical size
			Set<Player> team1 = matchFound.getTeam1();
			Set<Player> team2 = matchFound.getTeam2();
			assertTrue(team1!=null && team1.size()==n && team2!=null && team2.size()==n);

			// And each team for each player should contain the original or the copy
			for(int i=0; i<n; ++i){
				if(team1.contains(players.get(i)))
					assertTrue(team2.contains(copyPlayers.get(i)));
					
				else if(team1.contains(copyPlayers.get(i)))
					assertTrue(team2.contains(players.get(i)));
					
				else
					fail();
			}
		} catch(Exception e){
			e.printStackTrace();
			fail("No exceptions allowed! >_<: " + e);
		}
	}
}
