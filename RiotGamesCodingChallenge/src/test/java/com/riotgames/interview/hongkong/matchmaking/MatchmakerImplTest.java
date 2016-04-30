package com.riotgames.interview.hongkong.matchmaking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Iterator;
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
	}

	
	/** Matches can't be found for <= 0 players*/ 
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
			matchmaker.enterMatchmaking(new Player("asdsasad", 1000000, 1));
			matchmaker.enterMatchmaking(new Player("asdsasad", 1, 1000000));
			matchmaker.enterMatchmaking(new Player("asdsasad", 1000000, 1000000));
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
			
			assertTrue(team1.toArray()[0] == p1);
			assertTrue(team2.toArray()[0] == p2);	
		} catch(Exception e){
			e.printStackTrace();
			fail("No exceptions allowed! >_<: " + e);
		}
	}

	/** 
	 * Test several basic FindMatch WITHOUT concurrency and a perfect match 
	 * */
	@Test
	public void testFindPerfectMatchNotConcurrent() {
		for(int n=1; n<5; ++n)
			testFindPerfectMatchNvNNotConcurrent(n);
	}
	
	/** 
	 * Test a basic NvN FindMatch WITHOUT concurrency and a perfect match. 
	 * N random players are created and cloned in name, skill, etc. so teams have to be identical 
	 * */
	public void testFindPerfectMatchNvNNotConcurrent(int n) {
		try{
			// Create matchmaker
			MatchmakerImpl matchmaker = new MatchmakerImpl();

			// Create expected teams
			HashSet<Player> expectedTeam1 = new HashSet<Player>(n);
			HashSet<Player> expectedTeam2 = new HashSet<Player>(n);
			
			// Add n basic different players for both expected teams
			for(int i=1; i<=n; ++i){
				// Add an arbitrary random player to team1
				Player p = new Player("p"+i, 10+i, 20+i);
				expectedTeam1.add(p);
				
				// And add a copy to team2
				expectedTeam2.add(new Player(p));
			}

			// Array to store all matches found
			//ArrayList<Match> matches = new ArrayList<Match>();
			
			// Enter (n-1) players from each team
			Iterator<Player> it1 = expectedTeam1.iterator();
			Iterator<Player> it2 = expectedTeam2.iterator();

			for(int i=0; i<(n-1); ++i){
				matchmaker.enterMatchmaking(it1.next());
				
				// No match can be found yet
				assertNull(matchmaker.findMatch(n));
				
				matchmaker.enterMatchmaking(it2.next());
				
				// No match can be found yet
				assertNull(matchmaker.findMatch(n));
			}
			
			// Enter last players
			matchmaker.enterMatchmaking(it1.next());
			
			// No match can be found yet
			assertNull(matchmaker.findMatch(n));

			matchmaker.enterMatchmaking(it2.next());
			
			// Match found!
			Match matchFound = matchmaker.findMatch(n);
			assertNotNull(matchFound);
			
			// Teams should be the ones we were expecting!
			Set<Player> team1 = matchFound.getTeam1();
			Set<Player> team2 = matchFound.getTeam2();
			
			assertTrue(team1!=null && team1.size()==n && team2!=null && team2.size()==n);
			
			// Order is not relevant so we identify if team1==expectedTeam1 by inspecting one arbitrary player
			if(expectedTeam1.contains(team1.iterator().next())){
				assertEquals(expectedTeam1, team1);	
				assertEquals(expectedTeam2, team2);
			}
			
			else{
				assertEquals(expectedTeam1, team2);	
				assertEquals(expectedTeam2, team1);
			}
			
		} catch(Exception e){
			e.printStackTrace();
			fail("No exceptions allowed! >_<: " + e);
		}
	}
}
