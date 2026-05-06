README.txt

Álvaro Valera Vázquez (2016-05-02 Coding Challenge)
alvaro.valera.vazquez@gmail.com

Here is my work for the coding challenge. Sorry for any typos. It's been a long weekend :(

-----------------
Mathmaking System
-----------------

-----------
Assumptions
-----------

I assumed the game is for 2 teams and an arbitrary max number of players per team of 10. 

The other big assumption made here is that the core (the PlayerBase) is synchronized, so no concurrency. I'm sorry, folks :)
Player insertions and match finding has to be done sequentially. I asked but I wasn't replied :( So I stayed simple and focus on other things :)

The rest of assumptions (e.g. max reasonable number victories/loses, max number of players, etc.) they are - I hope - 
reasonably well-documented throughout the code.

--------------
The Algorithms
--------------
I went for a simple matchmaking algorithm based on a Matcher and a SkillCalculator.
For each team member, the Matcher chooses 2 players based on their similarity (the better the similarity is, the sooner they're chosen).
The most skilled player, according to the SkillCalculator, is assigned to the team with lowest average skill, and vice versa.
Similarity is calculated and cached when a player enters the system. It's the slowest part of the process.
It could be optimized by dividing similarity calculations in different threads or virtual machines in the cloud. Figures later...

Matchmaker is instantiated through an AbstractMatchmakerFactory which configures the specific desired strategy.

There are basically 3 alternatives tested:
- Random
- SimpleSkillBased
- WeightedScoring

The first one, Random, just provides a Naive baseline to compare the other REAL algorithms. Don't use in Production.
Players gonna hate :P

SimpleSkillBased is the first real approach. It calculates similarity as the difference in player skill. No tricks. But it works.
It allows (see factory documentation) a way to define a simple bias for the longestQueuedPlayer. Activating this flag forces Matchmaker
choosing this player as the first member of one of the teams. Its purpose is to provide a hard-coded way to avoid death starvation in queues.
Figures on this are funny, I promise :D

WeightedScoring is more interesting. It calculates 3 scores and combines them linearly using weights:
- SkillScore
- LevelScore
- QueueScore

SkillScore is the same used in SimpleSkillBased. 

LevelScore calculates "level" (which is estimated for the data samples as a function of the total games played) difference.

QueueScore is an attempt to boost the similarity for players staying in queue for longer. The longer they have stayed the greater
this score is. It's not working very well but I'm not sure if there are bugs and/or the concept behind it is bad.

For all strategies tested a SkillCalculator based on the win rate (wins / (wins + losses)) has been used 
except for RandomMatchmaker which also uses a RandomSkillCalculator.

I wish I have had time to develop a cleverer algorithm which takes into account the differences INSIDE a team and a ranked system, 
at least to prevent n00b players being matched at all with pro players which is not good :|

I think it's the weakest part of my system. I rage a bit because I drawed this algorithm concept on a whiteboard the very first day. 
I finally decided to go to a simpler but more robust algorithm so I had time to gather statistics and write a decent code base.

-----------------
Production ready!
-----------------
I provide a DefaultMatchmakerFactory that chooses the best algorithm found and tested so far.
You can choose your favourite Matchmaker, Matcher and SkillCalculation or even attaching new ones using this AbstractFactory pattern.
I hope is clear enough. :)

Please notice package names are slightly different from template project given!!! That's usually not a good idea in Production, 
but I'm not expecting my code is going to be put on top of LOL servers either :P

--------------------
Other (boring) stuff
--------------------
There are some JUnit cases for testing several stuff and also to perform some stress tests.

About machine requirements, using an Intel Core i7 5700 HQ 2,7Ghz and 2GB RAM for Java Memory Heap, the following config is supported:
MAX_PLAYERS = 9000
MAX_PLAYERS_PER_TEAM = 10
(attached there's a sample profiling summary: JavaVisualVM_9000players_1v1.jpg)
It's not very optimized but it wasn't my objective, anyway. I preferred to focus on the science rather than on low-level stuff.

I built everything using a local Git repository.

--------------------
Other (cool) stuff
--------------------
There's also a simulator and a stats gathering module. Figures! Wiiiiii! ^^

The simulator has a Main function and it's very easy to use if you want to try it.

Basically I wanted to simulate a constant player base of 100 players in the system looking for 5v5 games. As soon as the players are matched,
a replacement for all of them is chosen from the sample data base. The process is repeated up to 10000 times.

I decided not to extract stats from the ramp-up part of the system as, by the given instructions, the algorithm is not expected 
to give good results at first.

I had time to extract and analyse the following stats, which are averaged over all the matches found:
- avgBalanceScore
- avgAbuseScore
- queueTime
- maxQueueTime

BalanceScore is the average difference between a team's average player skill and the other's: 0 is the best possible value.

AbuseScore is the average difference between the most skilled player of one team and the worst of the other: 0 is the best possible value.

QueueTime is the average time a player waits since it enters the system until he/she is matched.

MaxQueueTime is the max value for the previous metric.

Key conclusions follows:
- Random algorithm is bad:
	- Gives a 13% of difference in avg team's player skill
	- AbuseScore raises up to an 87%. It's not really that far from the best solutions which give no less than 70%
	- QueueTime is 4 ms. Not bad, but best solutions give less than 1 ms
	- MaxQueueTime, 3 s, is almost about the time the simulation lasts, 4.3s. That's bad
	
- SimpleSkillBased is acceptable:
	- Nearly gives the best balanceScore: 0.1%
	- AbuseScore / QueueTime / MaxQueueTime similar to Random
	
- SimpleSkillBased with preference for longQueuedPlayers makes players stay longer in queues :D
	- It's terribly SLOW: While the rest of simulations needed 1s-4s for 10000 matches, this one needed 67s for just 3000
	- BalanceScore / AbuseScore are similar to the original version
	- QueueTime is so bad: 222 ms. The over cost of traversing the PriorityQueue to find a match for the longestQueuedPlayer is way too much
	- MaxQueueTime is in the middle: around 2s. The interesting point is that this is the max time in a simulation that took 67s, so relatively
	this is one of the best figures for this stat

- WeightedScoring: Several weight configurations were tested and with proper values this is the BEST approach.
	- I chose 0.5 as both skillWeight and levelWeight
	- BalanceScore: It's not as good as SimpleSkillBased but almost: 0.42% vs 0.1%
	- AbuseScore: Slightly better than SimpleSkillBased: 73% vs 84%
	- QueueTime / MaxQueueTime: They are also better but results were very dependent on the specific execution

--------------
Final thoughts
--------------	
More experiments and metrics would be needed to determine relevant stats about queue time. It should've been extracted from ALL players,
not only players matched, and it should've also been averaged over different simulations.

Also, queueWeight didn't work as expected so I remove from the final build.

Finally, to be more fair, AbuseScore should be tackled. Even if the teams are balanced, a pro player can line up with a noob and ruin the matchmaking.

And that's all, THANKS if you have read up to this.

Best regards,
Álvaro Valera Vázquez