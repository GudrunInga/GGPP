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
	//TODO Global variable that stores the best solution so far.
/*
 * Implement searching for a solution of the game.
 * I suggest to use iterative deepening depth-first search and a global variable that
 * stores the best solution found so far.
 * The Gamer class you created has a method getCurrentState() to get the current state of the
 * game and getStateMachine() to get a state machine representing the game.
 * This state machine can be used to get legal moves, compute successor states, ...,
 * that you will need to implement the search.
 */
/*
 * When implementing the search,
 * make sure that you stop searching before the time is up!
 * NEVER GO OVER THE TIME LIMIT!
 */

/*
 * When the search finishes, output the time it took (e.g., using System.out.println(...)).
 */

// Check if your implementation is correct by running the tests.

/*
 * Check if your implementation is fast enough
 * (is able to solve the games below in the given time).
 */
	/*
	 * Override the stateMachineMetaGame(..)-method to change what the player does
	 * after receiving a start message (before the game starts)
	 */
	// Get the state machine
	public StateMachine stateMachine = getStateMachine();
	public ArrayList<Integer> visitedState;
	public ArrayList<Move> bestPath;
	public int bestValue = 0;
	public int moveCount = 0;
	public long stopTime;

	//Gleymum að gera lista af moves sem við höfum gert

	@Override
	public void stateMachineMetaGame(long timeout)throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		stopTime = System.currentTimeMillis() + timeout - 500;
		//Byrja að ná í initial state - hoping it works
		MachineState start = stateMachine.getInitialState();

		//lista/array af MachineStates sem eru þau state sem við erum búin að heimsækja
		ArrayList<Move> noMoves = new ArrayList<Move>();
		// Erum ekki búin að heimsækja state nema að vera búin að heimsækja börnin
		explore(start, 0, 0, noMoves);
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
		// TODO Auto-generated method stub
		//Telja moves af því að við
		//labba niður gamla bestPath,
		stopTime = System.currentTimeMillis() + timeout - 500;

		if(!isSolved())
		{
			MachineState currState = getCurrentState();
			for(Move move : bestPath)
			{
				ArrayList<Move> nextMove = new ArrayList<Move>();
				nextMove.add(move);
				currState = stateMachine.getNextState(currState, nextMove);
			}
			explore(currState, 0, 0, new ArrayList<Move>());

		}
		if (bestPath.isEmpty())
		{
			return stateMachine.getRandomMove(getCurrentState(), getRole());
		}
		else
		{
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
		if(System.currentTimeMillis() >= stopTime)
		{
			return;
		}
		// Geymir best path, breytir ef finnur nýtt best path
		if(isChecked(node))
		{
			return;
		}
		if(stateMachine.isTerminal(node))
		{
			evaluate(node, movesMade);
		}
		if(isSolved())
		{
			return;
		}
		if (depth == maxDepth)
		{
			return;
		}

		visitedState.add(node.hashCode());
		depth++;
		try{
			for(Move child : stateMachine.getLegalMoves(node, getRole()))
			{
				ArrayList<Move> nextMove = new ArrayList<Move>();
				nextMove.add(child);
				movesMade.add(child);
				explore(stateMachine.getNextState(node, nextMove), depth, maxDepth, movesMade);

			}
		}
		catch(Exception e){
			System.out.println(e);
			System.out.println("Either no successor state or no legal moves");
		}


		if(depth == 0)
		{
			visitedState = new ArrayList<Integer>();
			ArrayList<Move> noMoves = new ArrayList<Move>();
			explore(node, depth, ++maxDepth, noMoves);
		}
	}

	public boolean isChecked(MachineState node)
	{
		//return true if node has been visited
		if(visitedState.contains(node))
		{
			return true;
		}
		return false;
	}

	public void evaluate(MachineState node, ArrayList<Move> currentPath)
	{
		try{
			int value = stateMachine.getGoal(node, getRole());
			if (value > bestValue)
			{
				bestPath = currentPath;
				bestValue = value;
				moveCount = 0;
				// If we improve the best path then the bestPath was found from out curr location and so
				// we have moved 0 steps through it
			}

		}
		catch (Exception e){
			System.out.println("State should be terminal, but no defined goal");
			System.out.println("I shouldn't be here, something must have gone horribly wrong");
		}

	}

	public boolean isSolved()
	{
		return bestValue == 100;
	}






}
