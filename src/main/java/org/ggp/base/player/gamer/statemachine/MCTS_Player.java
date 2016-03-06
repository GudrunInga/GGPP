package org.ggp.base.player.gamer.statemachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;
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

	private class Pair implements Comparable<Pair>{
		Move move;
		Role role;

		public Pair(Move move, Role role)
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

		@Override
		public int compareTo(Pair pair)
		{

			return 0;
		}
	}
	private class Node{
		//Quality value Q: the average score of action a for role r:
		HashMap<Pair, Integer > valueQ;
		//Number of simulations N(s, r, a): The number of simulations run with the action a played by role r:
		SortedMap<Pair, Integer>  numSim;
		//Number of total visits N(s):
		int numVisits;

		//Children: Hvert node inniheldur children sem er:

		//List of moves since we ask for list<list<Move>> legaljointmoves í expansion
		HashMap<List<Move>,Node> children; //Change this!!!!
		//List of Parents
		ArrayList<Node> parents;
		//State:
		MachineState state;

		//Constructor
		public Node(MachineState state, HashMap<Pair, Integer > valueQ,SortedMap<Pair, Integer>  numSim, int numVisits){
			this.valueQ = valueQ;
			this.numSim = numSim;
			this.numVisits = numVisits;
			this.state = state;
			this.children = new HashMap<List<Move>, Node>();
			this.parents = new ArrayList<Node>();

		}
	}
	//Geyma HashMap<MachineState, Node pathToNode> fyrir öll machinestates í trénu
	HashMap<MachineState, Node> knownStates;
	StateMachine stateMachine;
	long stoptime;
	Node root;

	@Override
	public void stateMachineMetaGame(long timeout)throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		stoptime = timeout - 100;
		stateMachine = getStateMachine();
		SortedMap<Pair, Integer> numSim = new ConcurrentSkipListMap<Pair, Integer>();
		HashMap<Pair, Integer> valueQ = new HashMap<Pair, Integer>();
		MachineState newState = stateMachine.getInitialState();
		for(Role role : stateMachine.getRoles()){
			for(Move move : stateMachine.getLegalMoves(newState, role)){
				Pair newPair = new Pair(move, role);
				numSim.put(newPair, 0);
				valueQ.put(newPair, 0);
			}
		}
		root = new Node(newState, valueQ, numSim, 0);
		try{
			while(true){
				Node selected = selection(root);
				expansion(selected);
				List<Move> firstMove = stateMachine.getRandomJointMove(selected.state);

				//TODO we are losing one simulation on the bottom node
				int value = simulation(stateMachine.getNextState(selected.state, firstMove));
				backpropogate(selected, value, firstMove);
			}
		}catch (TimeoutException e){
			System.out.println("Times up, lets' do this");
		}


	}
	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		stoptime = timeout - 500;
		MachineState currState = getCurrentState();
		//TODO knownStates, fletta upp í því með currState og fá nóðuna sem þar á við og
		// setja hana sem rót.

		int currMaxN = 0;
		Move currBestMove = stateMachine.getRandomMove(root.state, getRole());
		try{
			for(Move legal : stateMachine.getLegalMoves(root.state, getRole())){
				Pair pair = new Pair(legal, getRole());
				if(root.numSim.get(pair) > currMaxN){
					currMaxN = root.numSim.get(pair);
					currBestMove = legal;
				}
			}
			while(true){
				Node selected = selection(root);
				expansion(selected);
				List<Move> firstMove = stateMachine.getRandomJointMove(selected.state);

				//TODO we are losing one simulation on the bottom node
				int value = simulation(stateMachine.getNextState(selected.state, firstMove));
				backpropogate(selected, value, firstMove);
			}
		}catch (TimeoutException e){

			for(Move legal : stateMachine.getLegalMoves(root.state, getRole())){
				if(System.currentTimeMillis() >= timeout - 50){
					break;
				}
				Pair pair = new Pair(legal, getRole());
				if(root.numSim.get(pair) > currMaxN){
					currMaxN = root.numSim.get(pair);
					currBestMove = legal;
				}
			}
		}

		return currBestMove;
	}

        //returns the leaf node of the tree whose children
        //will be added to the tree, and from which the
        //current simulation will be run
	public Node selection(Node node) throws TimeoutException{
		if(System.currentTimeMillis() >= stoptime){
			throw new TimeoutException();
		}
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
            int newScore = selector(node, childKey);
            if(newScore>score)
            {
                score=newScore;
                result = child;
            }
        }
        return selection(result);
	}

    public int selector(Node node, List<Move> childkey)
    {
    	Random rand = new Random();
        return rand.nextInt(100);
        /*int sum = 0;
        List<Role> allRoles = stateMachine.getRoles();
        Map<Role, Integer> roleMap = stateMachine.getRoleIndices();
        int C = 40;
        for(Role role : allRoles){
        	Pair pair = new Pair(childkey.get(roleMap.get(role)), role);
        	sum += node.valueQ.get(pair) + C * Math.sqrt(Math.log(node.numVisits)/node.numSim.get(pair));
        					//C * is the uct exploration term
        }

        return sum;*/

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
		if(System.currentTimeMillis() >= stoptime){
			throw new TimeoutException();
		}
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

					SortedMap<Pair, Integer> numSim = new ConcurrentSkipListMap<Pair, Integer>();
					HashMap<Pair, Integer> valueQ = new HashMap<Pair, Integer>();
					for(Role role : stateMachine.getRoles()){
						for(Move move : stateMachine.getLegalMoves(newState, role)){
							Pair newPair = new Pair(move, role);
							numSim.put(newPair, 0);
							valueQ.put(newPair, 0);
						}
					}
					Node newNode = new Node(newState, valueQ, numSim, 0);
					newNode.parents.add(node);
					//add the newNode to node.children
					node.children.put(action, newNode);

				}
			}
		} catch (MoveDefinitionException | TransitionDefinitionException e) {
			System.out.println("No moves or no transitions");
			//e.printStackTrace();
		}

	}
	/*
	 * Run a simulated playout from C until a result is achieved.
	 */
	public int simulation(MachineState state) throws TimeoutException{
		if(System.currentTimeMillis() >= stoptime){
			throw new TimeoutException();
		}
		if(stateMachine.isTerminal(state)){
			try {
				return stateMachine.getGoal(state, getRole());
			} catch (GoalDefinitionException e) {
				System.out.println("Unreachable goal definition");
				e.printStackTrace();
			}
		}
		else{
			try {
				simulation(stateMachine.getRandomNextState(state));
			} catch (MoveDefinitionException e) {
				// TODO Auto-generated catch block
				System.out.println("No goddam moves allowed for non-terminal state");
				e.printStackTrace();
			} catch (TransitionDefinitionException e) {
				// TODO Auto-generated catch block
				System.out.println("No transition from non-terminal state");
				e.printStackTrace();
			}
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
	public void backpropogate(Node node, int score, Node callingNode) throws TimeoutException
	{
		if(System.currentTimeMillis() >= stoptime){
			throw new TimeoutException();
		}
		node.numVisits += 1;

		//Could be a problem here, what if node has two parents that have the same parent? Is that possible?

		for(List<Move> childKey : node.children.keySet()){
			Node child = node.children.get(childKey);
			if(child.equals(callingNode)){
				List<Role> allRoles = stateMachine.getRoles();
				Map<Role, Integer> roleMap = stateMachine.getRoleIndices();
				for(Role role : allRoles){
					Pair pair = new Pair(childKey.get(roleMap.get(role)), role);

					int value = node.numSim.get(pair);
					node.numSim.put(pair, value+1);

					int avgQ = node.valueQ.get(pair);
					avgQ = avgQ + ((score-avgQ)/node.numVisits);
					node.valueQ.put(pair, avgQ);
				}
				break;
			}
		}
		if(!node.parents.isEmpty()){
			for(Node parent : node.parents){
				backpropogate(parent, score, node);
			}
		}
	}

	public void backpropogate(Node node, int score, List<Move> moves) throws TimeoutException
	{
		if(System.currentTimeMillis() >= stoptime){
			throw new TimeoutException();
		}
		node.numVisits += 1;
		List<Role> allRoles = stateMachine.getRoles();
		Map<Role, Integer> roleMap = stateMachine.getRoleIndices();
		for(Role role : allRoles){
			Pair pair = new Pair(moves.get(roleMap.get(role)), role);

			int value = node.numSim.get(pair);
			node.numSim.put(pair, value+1);

			int avgQ = node.valueQ.get(pair);
			avgQ = avgQ + ((score-avgQ)/node.numVisits);
			node.valueQ.put(pair, avgQ);
		}
		if(!node.parents.isEmpty()){
			for(Node parent : node.parents){
				backpropogate(parent, score, node);
			}
		}
	}
}
