package org.ggp.base.player.gamer.statemachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.SortedMap;
import java.util.Set;
import java.util.Iterator;
import java.util.concurrent.TimeoutException;

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

		OrderedMap<Move,Node> children;
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

        //returns the leaf node of the tree whose children
        //will be added to the tree, and from which the
        //current simulation will be run
	public Node selection(Node node) throws TimeoutException{
            if(node.children.isEmpty())
            {
                return node;
            }

            for(Move childKey : node.children.keySet())
            {
                child=node.children.get(childkey);
                if(child.children.isEmpty())
                {
                    return child;
                }
            }

            int score=0;
            Node result = node;

            for(Move childKey : node.children.keySet())
            {
                child=node.children.get(childKey);
                int newScore = selector(child);
                if(newScore>score)
                {
                    score=newScore;
                    result = child;
                }

            }

            return result;
	}

    public int selector(Node node)
    {
    	Random rand = new Random();
        return rand.nextInt(100);
    }


    /*function expand (node)
    {var actions = findlegals(role,node.state,game);
     for (var i=0; i<actions.length; i++)
         {var newstate = simulate(seq(actions[i]),state);
          var newnode = makenode(newstate,0,0,node,seq());
          node.children[node.children.length]=newnode};
     return true} */
	public void expansion() throws TimeoutException{

	}

	public void simulation() throws TimeoutException{

	}

	public void backpropogation() throws TimeoutException{

	}

}
