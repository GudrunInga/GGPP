package org.ggp.base.player.gamer.statemachine;

import java.util.ArrayList;

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
	public int bestValue = 0;
	public int moveCount = 0;
	public long stopTime;
        public boolean singlePlayerMode;

	@Override
	public void stateMachineMetaGame(long timeout)throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
                
		long startTime = System.currentTimeMillis();
		stopTime = timeout - 500;
		stateMachine = getStateMachine();
                //check whether to play as single player game or not
                if(stateMachine.getroles().size()==1)
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
                    //solve assuming multiplayer
                    
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
		if(isChecked(node))
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
		depth++;
		try{
			for(Move child : stateMachine.getLegalMoves(node, getRole()))
			{
				ArrayList<Move> nextMove = new ArrayList<Move>();
				nextMove.add(child);
				ArrayList<Move> childMove = (ArrayList<Move>)movesMade.clone();
				childMove.add(child);
				explore(stateMachine.getNextState(node, nextMove), depth, maxDepth, childMove);
			}
		}
		catch(Exception e){
			System.out.println(e);
			System.out.println("Either no successor state or no legal moves");

		}

		// Depth = 1 is the root because we have incremented depth in the method
		if(depth == 1)
		{
			//System.out.println("depth == 0");
			maxDepth++;
			//System.out.println("Max depth:" + maxDepth);
			visitedState = new ArrayList<Integer>();
			ArrayList<Move> noMoves = new ArrayList<Move>();
			explore(node, 0, maxDepth, noMoves);
		}
	}

	public boolean isChecked(MachineState node)
	{
		//return true if node has been visited
		if(visitedState.contains(node.hashCode()))
		{
			return true;
		}
		return false;
	}

	public void evaluate(MachineState node, ArrayList<Move> currentPath)
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

		}
		catch (Exception e){
			System.out.println("State should be terminal, but no defined goal");
			System.out.println("I shouldn't be here, something must have gone horribly wrong");
		}

	}

	// Best solution found
	public boolean isSolved()
	{
		return bestValue == 100;
	}






}
