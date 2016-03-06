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
		List<Move> move;
		Role role;

		public Pair(List<Move> move, Role role)
		{
			this.move = move;
			this.role = role;
		}
		@Override
		public boolean equals(Object pair){
			//Tekið úr lab2 solution ARTI - megum við það?
			if (! (pair instanceof Pair )) {
				return false ;
			}
			Pair p = (Pair) pair;
			return this.move.equals(p.move) && this.role.equals(p.role);
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

		//List of moves since we ask for list<list<Move>> legaljointmoves í expansion
		SortedMap<List<Move>,Node> children;
		//List of Parents
		ArrayList<Node> parents;
		//State:
		MachineState state;

		//Constructor
		public Node(MachineState state, HashMap<Pair, Integer > valueQ,HashMap<Pair, Integer>  numSim, int numVisits){
			this.valueQ = valueQ;
			this.numSim = numSim;
			this.numVisits = numVisits;
			this.state = state;

		}
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
            //Change from Move to List<Move>, blame expansion
            for(List<Move> childKey : node.children.keySet())
            {
                Node child =node.children.get(childKey);
                if(child.children.isEmpty())
                {
                    return child;
                }
            }

            int score=0;
            Node result = node;
            //Change from Move to List<Move>, blame expansion
            for(List<Move> childKey : node.children.keySet())
            {
                Node child = node.children.get(childKey);
                int newScore = selector(child);
                if(newScore>score)
                {
                    score=newScore;
                    result = child;
                }
            }
            //Should we call it from here?
            expansion(result);
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
			/*
			 * If L is a not a terminal node (i.e. it does not end the game)
			 * then create one or more child nodes and select one C.
			 */
			if(!stateMachine.isTerminal(node.state)){
				//All possible legal moves
				List<List<Move>> actions = stateMachine.getLegalJointMoves(node.state);

				//How should we select what child we will simulate?
				//Should we use list of joint moves instead of just one move
				for(List<Move> action : actions){

					MachineState newState = stateMachine.getNextState(node.state, action);

					Pair newPair = new Pair(action, getRole());
					HashMap<Pair, Integer> numSim = new HashMap<Pair, Integer>();
					numSim.put(newPair, 0);
					HashMap<Pair, Integer> valueQ = new HashMap<Pair, Integer>();
					valueQ.put(newPair, 0);

					Node newNode = new Node(newState, valueQ, numSim, 0);

					int value = simulation(newState);
					backpropogate(newNode, value, action);
					//add the newNode to node.children
					node.children.put(action, newNode);
				}
			}
		} catch (MoveDefinitionException | TransitionDefinitionException | GoalDefinitionException e) {
			System.out.println("No joint legal moves or no actions i nor goal value for tht state");
			//e.printStackTrace();
		}

	}
	/*
	 * Run a simulated playout from C until a result is achieved.
	 */
	public int simulation(MachineState state) throws TimeoutException, GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException{
		if(stateMachine.isTerminal(state)){
			return stateMachine.getGoal(state, getRole());
		}
		else{
			simulation(stateMachine.getRandomNextState(state));
		}
		return 0;
	}

	/* From chapter 8
	 * function backpropagate (node,score)
	 * 	{node.visits = node.visits+1;
	 * 	node.utility = node.utility+score;
	 * 	if (node.parent) {backpropagate(node.parent,score)};
	 * 	return true}
	 */
	public void backpropogate(Node node, int score, List<Move> action) throws TimeoutException{
		node.numVisits += 1;
		if(node.parents == null){
			return;
		}
		//Could be a problem here, what if node has two parents that have the same parent? Is that possible?
		for(Node parent : node.parents){
			backpropogate(parent, score, action);
		}

		Pair pair = new Pair(action, getRole());

		int value = node.numSim.get(pair);
		node.numSim.put(pair, value+1);

		//average_new = average_old + ((value-average_old)/size_new)
		int avgQ = node.valueQ.get(pair);
		avgQ = avgQ + ((score-avgQ)/node.numVisits);
		node.valueQ.put(pair, avgQ);

		//Need to check if node.valueQ.pair equals roleAction ?
	}

}
