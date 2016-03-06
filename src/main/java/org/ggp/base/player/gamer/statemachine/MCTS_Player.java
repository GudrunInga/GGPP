package org.ggp.base.player.gamer.statemachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.concurrent.TimeoutException;

import org.ggp.base.player.gamer.statemachine.sample.SampleGamer;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class MCTS_Player extends SampleGamer{

        //perhaps change hashmap to just storing Nodes instead of paths to Nodes
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

		SortedMap<Move,Node> children;
		//List of Parents
		ArrayList<Node> parents;
		//State:
		MachineState state;
	}
	//Geyma HashMap<MachineState, Node pathToNode> fyrir öll machinestates í trénu
	HashMap<MachineState, Node> knownStates;
	StateMachine stateMachine;

	@Override
	public void stateMachineMetaGame(long timeout)throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		stateMachine = getStateMachine();

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
                Node child =node.children.get(childKey);
                if(child.children.isEmpty())
                {
                    return child;
                }
            }

            int score=0;
            Node result = node;

            for(Move childKey : node.children.keySet())
            {
                Node child = node.children.get(childKey);
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


    /*From chapter 8
     * function expand (node){
     * 	var actions = findlegals(role,node.state,game);
     * 	for (var i=0; i<actions.length; i++){
     * 		var newstate = simulate(seq(actions[i]),state);
     * 		var newnode = makenode(newstate,0,0,node,seq());
     * 		node.children[node.children.length]=newnode
     * 	};
     * 	return true
     * }
     */
	public void expansion(Node node) throws TimeoutException{
		try {
			List<List<Move>> actions = stateMachine.getLegalJointMoves(node.state);
			for(int i = 0; i < actions.size(); i++){
				MachineState newState; // = simulate something
				Node newNode = new Node();
				//Set the values in the new Node
				//add the newNode to node.children
			}
		} catch (MoveDefinitionException e) {
			System.out.println("No joint legal moves");
			//e.printStackTrace();
		}

	}

	public void simulation() throws TimeoutException{

	}

	/* From chapter 8
	 * function backpropagate (node,score)
	 * 	{node.visits = node.visits+1;
	 * 	node.utility = node.utility+score;
	 * 	if (node.parent) {backpropagate(node.parent,score)};
	 * 	return true}
	 */
	public void backpropogate(Node node, int score) throws TimeoutException{
		node.numVisits = node.numVisits + 1;
		Pair roleAction = new Pair();

		//Need to check if node.valueQ.pair equals roleAction ?
		node.valueQ.replace(roleAction, node.valueQ.get(roleAction) + score);
		if(node.parents != null){
			for(Node parentNode : node.parents){
				backpropogate(parentNode, score);
			}
		}
	}

}
