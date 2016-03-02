package org.ggp.base.player.gamer.statemachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;
import java.util.Random;

import org.ggp.base.player.gamer.statemachine.sample.SampleGamer;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class MCTS_Player extends SampleGamer{

	private class Pair{
		Move move;
		Role role;

		public boolean equals(Pair pair){
			return this.move.equals(pair.move) && this.role.equals(pair.role);
		}
		@Override
		public int hashCode(){
			return move.hashCode()+role.hashCode();
		}
	}
	private class Node{
		//Quality value Q: the average score of action a for role r:
		HashMap<Pair, Integer > valueQ;
		//Number of simulations N(s, r, a): The number of simulations run with the action a played by role r:
		HashMap<Pair, Integer>  numSim;
		//Number of total visits N(s):
		int numVisits;

		//Children: Hvert node inniheldur children sem er:
		ArrayList<Node> children;
		//List of Parents
		ArrayList<Node> parents;
		//State:
		MachineState state;
	}
	//Geyma HashMap<MachineState, Node pathToNode> fyrir öll machinestates í trénu
	HashMap<MachineState, Node> knownStates;

	@Override
	public void stateMachineMetaGame(long timeout)throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
            

	}
	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {

				return null;
	}

	public void selection(Node node) throws TimeoutException{
            if(Node.children.isEmpty())
            {
                return node;
            }

            for(Node child:node.children)
            {
                if(child.children.isEmpty())
                {
                    return child;
                }
            }

            score=0;
            result=0;

            for(Node child:node.children)
            {
                int newScore=selector(child);
                if(newScore>score)
                {
                    score=newScore;
                    result=child;
                }
                    
            }

            return result;
	}

        public int selector(Node node)
        {
            return Random.nextInt(100);
        }

	public void expansion() throws TimeoutException{

	}

	public void simulation() throws TimeoutException{

	}

	public void backpropogation() throws TimeoutException{

	}

}
