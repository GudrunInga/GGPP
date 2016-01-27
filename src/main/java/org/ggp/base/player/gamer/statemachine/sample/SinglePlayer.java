package org.ggp.base.player.gamer.statemachine.sample;

import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class SinglePlayer extends SampleGamer {
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
	@Override
	public void stateMachineMetaGame(long timeout)throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{

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
		return null;
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
}
