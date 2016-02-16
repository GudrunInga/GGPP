package org.ggp.base.player.gamer.statemachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ggp.base.player.gamer.statemachine.sample.SampleGamer;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class SinglePlayer extends SampleGamer {

/*
 * When the search finishes, output the time it took (e.g., using System.out.println(...)).
 */

// Check if your implementation is correct by running the tests.

	public StateMachine stateMachine;
	public ArrayList<Integer> visitedState = new ArrayList<Integer>(); // States that have already been visited, don't need to check those again
	public ArrayList<Move> bestPath;
	public HashMap<Integer, CacheNode> cache;
	//public ArrayList<Move> worstPath;
	public int bestValue = 0;
    public int worstValue =100;
	public int moveCount = 0;
	public long stopTime;
    public boolean singlePlayerMode;
    //documents the highest variety of different moves 
    //available at any single state yet discovered, used to normalise
    //mobility value function
    public int mostmoves=1;


	@Override
	public void stateMachineMetaGame(long timeout)throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{

		long startTime = System.currentTimeMillis();
		stopTime = timeout - 500;
		stateMachine = getStateMachine();
                //check whether to play as single player game or not
        if(stateMachine.getRoles().size()==1)
            singlePlayerMode=true;
        else
            singlePlayerMode=false;

		bestValue = 0;
		moveCount = 0;
		//Begin getting the initial state (root)
		MachineState start = stateMachine.getInitialState();

        if(singlePlayerMode)
        {
		    //lista/array af MachineStates sem eru þau state sem við erum búin að heimsækja
		    ArrayList<Move> noMoves = new ArrayList<Move>(); // Moves we have made (which here is no moves been made)

		    //Let the search begin
		    explore(start, 0, 1, noMoves);
		    System.out.println("Time taken for search " + (System.currentTimeMillis() - startTime)); //Output the time it took to search
		}
        else
        {
        	System.out.println("In multiplayer");
            //solve assuming multiplayer

            //search via minimax iterative deepening
        	cache = new HashMap<Integer, CacheNode>();
            ArrayList<Move> noMoves = new ArrayList<Move>(); // Moves we have made (which here is no moves been made)
            int i = 1;
            while(timeout - 500 > System.currentTimeMillis())
            {
            	//minimax(start,0,1,noMoves,true);
            	maxi(start, i, noMoves);
            	i++;
            	System.out.println("statemachinemetagame, depth: "+i);
            }
		    System.out.println("Time taken for search " + (System.currentTimeMillis() - startTime)); //Output the time it took to search
        }
	}

	/*
	 * Override the stateMachineSelectMove(..)-method.
	 * This method has to return a move for the current state.
	 * It will be called for each play message, i.e., for each step of the game.
	 */
	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {

		stopTime = timeout - 500;
		if(singlePlayerMode)
		{
			if(!isSolved())
			{
				MachineState currState = getCurrentState();

				if(bestPath != null)
				{
					for(Move move : bestPath)
					{
						ArrayList<Move> nextMove = new ArrayList<Move>();
						nextMove.add(move);
						currState = stateMachine.getNextState(currState, nextMove);
					}
				}
				//System.out.println("We are in SELECTMOVE!--------------------------------------------------------------------------\n");
				explore(currState, 0, 1, new ArrayList<Move>());
				//System.out.println(System.currentTimeMillis()); //Output the time it took to search

			}
			if (bestPath == null)
			{
				//System.out.println("We are in getRandomMove!!!!!");
				return stateMachine.getRandomMove(getCurrentState(), getRole());
			}
			else
			{
				//System.out.println("Size of bestPath " + bestPath.size());
				//System.out.println("MOVE COUNT    " + moveCount + " BEST VALUE " + bestValue);
				Move nextMove = bestPath.get(moveCount);
				moveCount++;
				return nextMove;
			}
		}
		else
		{
			MachineState node = getCurrentState();
			if(cache != null)
			{
				//System.out.println("Cache exists");
				if(cache.get(node.hashCode()) != null)
				{
					CacheNode cacheNode = cache.get(node.hashCode());
					if(cacheNode.node == node)
					{
						System.out.println("Best move " + cacheNode.bestMove);
						return cacheNode.bestMove;
					}
				}
				int i = 1;
	            while(timeout - 500 > System.currentTimeMillis())
	            {
	            	maxi(node, i, new ArrayList<Move>());
	            	i++;
	            }
				CacheNode searchCacheNode = cache.get(node.hashCode());
				if(searchCacheNode != null && searchCacheNode.node == node)
				{
					System.out.println("Best Move " + searchCacheNode.bestMove);
					return searchCacheNode.bestMove;
				}
				else
				{
					//System.out.println("The current state");
					//for(CacheNode c : cache.values())
					//{
					//	System.out.println(c.node);
					//}
					//System.out.println("The node we are searching for " + node);
					//System.out.println("cache !=  null, searchCacheNode == null or searchCacheNode.node != node" + searchCacheNode);
					System.out.println("We made random move");
					return stateMachine.getRandomMove(node, getRole());
				}
			}
			System.out.println("Last return statement, random move made");
			return stateMachine.getRandomMove(getCurrentState(),  getRole());
		}
	}

    public int maxi(MachineState node, int depth, ArrayList<Move> movesMade)
	{
		//System.out.println("Depth: " + depth + "MaxDepth: " + maxDepth);
		if(System.currentTimeMillis() >= stopTime)
		{
			System.out.println("Stop");
			return -1;
		}
		if(isSolved())
		{
			//System.out.println("Is Solved");
			return 100;
		}
		// Geymir best path, breytir ef finnur nýtt best path
		if(isChecked(node, depth)) // Passa að depth sem við geymum sé stærra en depth sem við leitum
		{
            //TODO:return value of node for this path
            //TODO:think about this, does it already happen in data structure
			//System.out.println("Already Checked");
			CacheNode cacheNode = cache.get(node.hashCode());
			if(cacheNode.node == node)
			{
				return cacheNode.bestValue;
			}
			System.out.println("isChecked, should not get here");
			return -2; //Should NOT be here
		}
		if(stateMachine.isTerminal(node))
		{
			System.out.println("Found Terminal ");
			cache.put(node.hashCode(), new CacheNode(bestValue, 1000000000, null, node)); //this is a terminal state so no moves are possible
																							//so null works as an optimal move
			System.out.println("made infinite depth cache entry");
			return evaluate(node, movesMade);

		}

		if (depth == 0)
		{
			//System.out.println("MaxDepth reached");
			CacheNode cacheNode = cache.get(node.hashCode());
			if(cacheNode != null && cacheNode.node == node)
			{
				return cacheNode.bestValue;
			}
			//System.out.println("if depth == 0, should not get to this return statement");
			return 0; //WTF happened?
		}

        //visitedState.add(node.hashCode());
		try{
			int currBestValue = 0;

                        
                        mostmoves =  max(mostmoves,stateMachine.getLegalMoves(node, getRole()).size());
			Move currBestMove = stateMachine.getLegalMoves(node, getRole()).get(0);
			for(Move child : stateMachine.getLegalMoves(node, getRole()))
			{
				ArrayList<Move> childMove = (ArrayList<Move>)movesMade.clone();
				childMove.add(child);
				int value = mini(node, child, depth-1, childMove);
				if(value > currBestValue)
				{
					currBestValue = value;
					currBestMove = child;
				}
			}

			//System.out.println("Node before caching " + node);
			cache.put(node.hashCode(), new CacheNode(currBestValue, depth , currBestMove, node));
			//System.out.println("Node after caching " + new CacheNode(currBestValue, depth , currBestMove, node).node);
			return currBestValue;
		}
		catch(Exception e){
			System.out.println(e);
			System.out.println("Either no successor state or no legal moves");
			System.exit(0);

		}
		System.out.println("At the end of maxi, should not get here");
		return -5;
	}
    public int mini(MachineState node,Move ourMove, int depth,ArrayList<Move> movesMade)
    {
        List<List<Move>> legalMoves;

		try {

			//vinna með worstValue locally og geyma move meðan við tékkum hvort það sé verra en versta so far.
			//síðan halda áfram?
			legalMoves = stateMachine.getLegalJointMoves(node, getRole(),ourMove);
			int currWorstValue = 100;
			List<Move> currWorstMoves = legalMoves.get(0);
			for(List<Move> moveSet:legalMoves)
	        {
				int value = maxi(stateMachine.getNextState(node, moveSet), depth, movesMade);
				if(value < currWorstValue)
				{
					currWorstValue = value;
					currWorstMoves = moveSet;
				}
	        }
			return currWorstValue;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
		return 0; //cause thats how we do it
    }
	/*
	 * If you want, you can override the
	 * stateMachineStop method to do something when the game is over.
	 * */
	/* @Override
	public void stateMachineStop()
	{
		//Optional, do not need to implement
	}
	 */

	//A að keyra á rót með depth = 0, maxDepth = 0 og movesMade tómt
	public void explore(MachineState node, int depth, int maxDepth, ArrayList<Move> movesMade)
	{
		//System.out.println("Depth: " + depth + "MaxDepth: " + maxDepth);
		if(System.currentTimeMillis() >= stopTime)
		{
			//System.out.println("Stop");
			return;
		}
		if(isSolved())
		{
			//System.out.println("Is Solved");
			return;
		}
		// Geymir best path, breytir ef finnur nýtt best path
		if(isChecked(node, depth))
		{
			//System.out.println("Already Checked");
			return;
		}
		if(stateMachine.isTerminal(node))
		{
			//System.out.println("Found Terminal");
			evaluate(node, movesMade);
		}

		if (depth == maxDepth)
		{
			//System.out.println("MaxDepth reached");
			return;
		}
		//System.out.println("I should get here!!!!!");
		visitedState.add(node.hashCode());
		try{
                        mostmoves =  max(mostmoves,stateMachine.getLegalMoves(node, getRole()).size());
			for(Move child : stateMachine.getLegalMoves(node, getRole()))
			{
				ArrayList<Move> nextMove = new ArrayList<Move>();
				nextMove.add(child);
				ArrayList<Move> childMove = (ArrayList<Move>)movesMade.clone();
				childMove.add(child);
				explore(stateMachine.getNextState(node, nextMove), depth+1, maxDepth, childMove);
			}
		}
		catch(Exception e){
			System.out.println(e);
			System.out.println("Either no successor state or no legal moves");

		}

		if(depth == 0)
		{
			//System.out.println("depth == 0");
			maxDepth++;
			//System.out.println("Max depth:" + maxDepth);
			visitedState = new ArrayList<Integer>();
			ArrayList<Move> noMoves = new ArrayList<Move>();
			explore(node, 0, maxDepth, noMoves);
		}
	}

	public boolean isChecked(MachineState node, int depth)
	{
		//return true if node has been visited
		if(singlePlayerMode)
		{
			if(visitedState.contains(node.hashCode()))
			{
				return true;
			}
			return false;
		}
		else
		{
			CacheNode tmp = cache.get(node.hashCode());
			if(tmp != null && tmp.node == node && tmp.depth >= depth)
			{
				return true;
			}
			return false;
		}
	}

	public int evaluate(MachineState node, ArrayList<Move> currentPath)
	{

            //bailout if we find losing play
		if(singlePlayerMode)
			{
			try{
				int value = stateMachine.getGoal(node, getRole());
				//System.out.println("VALUE   " + value);
				if (value > bestValue)
				{
					bestPath = currentPath;
					bestValue = value;
					moveCount = 0;
					// If we improve the best path then the bestPath was found from out curr location and so
					// we have moved 0 steps through it
					//System.out.println("BEST VALUE " + value+ "\n");
				}
			}catch (Exception e){
				System.out.println("State should be terminal, but no defined goal");
				System.out.println("I shouldn't be here, something must have gone horribly wrong");
			}
		}
		else
		{
			try{
				System.out.println(stateMachine.getGoal(node, getRole()));
				return stateMachine.getGoal(node, getRole());
			}catch (Exception e){
				System.out.println("State should be terminal, but no defined goal");
				System.out.println("I shouldn't be here, something must have gone horribly wrong");
			}
			System.out.println("Node " + node);
		}

		return 0;
	}

	// Best solution found
	public boolean isSolved()
	{
		return bestValue == 100;
	}

        //mobility based evaluation function
        //returns value in the range [0;100] representing
        //how many options the player has in this node compared to others
        public int mobilityValue(role,state)
        {
            //while search is running, keep track of most legal moves found in any single node,
            //use this to create a scalable ratio representing the mobility in a given state
            return 100*stateMachine.getLegalMoves(state,role)/mostmoves;
        }



        //rational mobility attempts to evaluate how much control we have over the game compared to our opponents (note that in multiplayer games, this
        //will be a relatively low value, so it may diminish by comparison once we find reasonable terminal states
        //this is approximated as the the ratio
        //returns value in the range [0;100] representing how large a piece of the branching factor at this node is attributable to this player,
        //which gives an interesting heuristic for how much control our player has over the current state of the game.
        public int rationalḾobilityValue(role,state)
        {
            return 100*stateMachine.getLegalMoves(state,role)/stateMachine.getLegalJointMoves(state);
        }
            






}