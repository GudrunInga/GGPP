package org.ggp.base.player.gamer.statemachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
			//Tekið úr lab2 solution ARTI
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
		List<Pair> pairIndex;
		List<Integer> valueQ;

		//Number of simulations N(s, r, a): The number of simulations run with the action a played by role r:
		List<Integer> numSim;
		//Number of total visits N(s):
		int numVisits;

		//Children: Hvert node inniheldur children sem er:
		List<List<Move>> childIndex;
		List<Node> children;
		//List of Parents
		ArrayList<Node> parents;
		//State:
		MachineState state;

		//Constructor
		public Node(MachineState state, List<Pair> pairIndex, List<Integer> valueQ, List<Integer>  numSim, int numVisits){
			this.pairIndex = pairIndex;
			this.valueQ = valueQ;
			this.numSim = numSim;
			this.numVisits = numVisits;
			this.state = state;
			this.childIndex = new ArrayList<List<Move>>();
			this.children = new ArrayList<Node>();
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
		knownStates = new HashMap<MachineState, Node>();
		List<Integer> numSim = new ArrayList<Integer>();
		List<Pair> pairIndex = new ArrayList<Pair>();
		List< Integer> valueQ = new ArrayList<Integer>();
		MachineState newState = stateMachine.getInitialState();
		for(Role role : stateMachine.getRoles()){
			for(Move move : stateMachine.getLegalMoves(newState, role)){
				Pair newPair = new Pair(move, role);
				numSim.add(0);
				pairIndex.add(newPair);
				valueQ.add(0);
			}
		}
		root = new Node(newState, pairIndex, valueQ, numSim, 0);
		try{
			while(true){

				Node selected = selection(root);
				if(stateMachine.isTerminal(selected.state)){
					backpropogate(selected, stateMachine.getGoal(selected.state, getRole()), stateMachine.getRandomJointMove(selected.state));
					continue;
				}
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
		//System.out.println(currState);

		List<Pair> pairIndex = new ArrayList<Pair>();
		List<Integer> numSim = new ArrayList<Integer>();
		List<Integer> valueQ = new ArrayList<Integer>();
		for(Role role : stateMachine.getRoles()){
			for(Move move : stateMachine.getLegalMoves(currState, role)){
				Pair newPair = new Pair(move, role);
				pairIndex.add(newPair);
				numSim.add(0);
				valueQ.add(0);
			}
		}
		root = new Node(currState, pairIndex, valueQ, numSim, 0);

		int currMaxN = 0;
		Move currBestMove = stateMachine.getRandomMove(root.state, getRole());
		try{
			for(int i = 0; i < root.numSim.size(); i++){
				if(root.numSim.get(i) > currMaxN){
					currMaxN = root.numSim.get(i);
					currBestMove = root.pairIndex.get(i).move;
				}
			}

			while(true){
				Node selected = selection(root);
				if(stateMachine.isTerminal(selected.state)){
					backpropogate(selected, stateMachine.getGoal(selected.state, getRole()), stateMachine.getRandomJointMove(selected.state));
					continue;
				}
				expansion(selected);
				List<Move> firstMove = stateMachine.getRandomJointMove(selected.state);

				//TODO we are losing one simulation on the bottom node
				int value = simulation(stateMachine.getNextState(selected.state, firstMove));
				backpropogate(selected, value, firstMove);
			}
		}catch (TimeoutException e){

			for(int i = 0; i < root.numSim.size(); i++){
				if(System.currentTimeMillis() >= timeout - 50){
					break;
				}
				if(root.numSim.get(i) > currMaxN){
					currMaxN = root.numSim.get(i);
					currBestMove = root.pairIndex.get(i).move;
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
        for(int i = 0; i < node.children.size(); i++)
        {
            Node child = node.children.get(i);
            if(child.children.isEmpty())
            {
                return child;
            }
        }

        int score=0;
        Node result = node;
        //Change from Move to List<Move>, blame expansion
        for(int i = 0; i < node.children.size(); i++){
            Node child = node.children.get(i);
            int newScore = selector(node, node.childIndex.get(i));
            if(newScore>score)
            {
                score=newScore;
                result = child;
            }
        }
        return selection(result);
	}

    public int selector(Node node, List<Move> moveList)
    {
    	//Random rand = new Random();
        //return rand.nextInt(100);
        int sum = 0;
        List<Role> allRoles = stateMachine.getRoles();
        Map<Role, Integer> roleMap = stateMachine.getRoleIndices();
        int C = 40;

        for(Role role : allRoles){
        	int index = 0;
        	Pair pair = new Pair(moveList.get(roleMap.get(role)), role);
        	for(int i = 0; i < node.pairIndex.size(); i++){
        		if(pair == node.pairIndex.get(i)){
        			index = i;
        			break;
        		}
        	}
        	sum += node.valueQ.get(index) + C * Math.sqrt(Math.log(node.numVisits)/node.numSim.get(index));
        					//C * is the uct exploration term
        }

        return sum;

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
                    if(knownStates.containsKey(newState))
                    {
                        Node foundNode = knownStates.get(newState);
                        foundNode.parents.add(node);
                        node.childIndex.add(action);
                        node.children.add(foundNode);
                    }
                    else
                    {
					    List<Pair> pairIndex = new ArrayList<Pair>();
					    List<Integer> numSim = new ArrayList<Integer>();
					    List<Integer> valueQ = new ArrayList<Integer>();
					    for(Role role : stateMachine.getRoles()){
					    	//Error here in tic tac toe, no legal moves for o player (board is full)
					    	/*if(stateMachine.isTerminal(newState)){ //SKÍTALAUSN, er eiginlega ekki lausn
					    		break;
					    	}*/
						    for(Move move : stateMachine.getLegalMoves(newState, role)){
							    Pair newPair = new Pair(move, role);
							    pairIndex.add(newPair);
							    numSim.add(0);
							    valueQ.add(0);
						    }
					    }
					    Node newNode = new Node(newState, pairIndex, valueQ, numSim, 0);
                        knownStates.put(newState,newNode);
					    newNode.parents.add(node);
					    //add the newNode to node.children
					    node.childIndex.add(action);
					    node.children.add(newNode);
                    }
				}

			}
			else{
				return;
			}
		} catch (MoveDefinitionException | TransitionDefinitionException e) {
			System.out.println("No moves or no transitions");
			e.printStackTrace();
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
        for(int i = 0; i < node.children.size(); i++)
        {
            Node child = node.children.get(i);
            if(child.equals(callingNode))
            {
            	List<Role> allRoles = stateMachine.getRoles();
				Map<Role, Integer> roleMap = stateMachine.getRoleIndices();
				for(Role role : allRoles){
					int index = 0;
					Pair pair = new Pair(node.childIndex.get(i).get(roleMap.get(role)), role);
					for(int k = 0; k < node.numSim.size(); k++){
						if(pair.equals(node.pairIndex.get(k))){
							index = k;
							break;
						}
					}
					int value = node.numSim.get(index);
					node.pairIndex.add(pair);
					node.numSim.add(value+1);

					int avgQ = node.valueQ.get(index);
					avgQ = avgQ + ((score-avgQ)/node.numVisits);
					node.valueQ.add(avgQ);
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
			int index = 0;
			for(int i = 0; i < node.pairIndex.size(); i++){
				if(pair.equals(node.pairIndex.get(i))){
					index = i;
					break;
				}
			}
			int value = node.numSim.get(index);
			node.pairIndex.add(pair);
			node.numSim.add(value+1);

			int avgQ = node.valueQ.get(index);
			avgQ = avgQ + ((score-avgQ)/node.numVisits);
			node.valueQ.add(avgQ);
		}
		if(!node.parents.isEmpty()){
			for(Node parent : node.parents){
				backpropogate(parent, score, node);
			}
		}
	}
}
