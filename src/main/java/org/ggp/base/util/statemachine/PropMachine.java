package org.ggp.base.util.statemachine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.propnet.architecture.Component;
import org.ggp.base.util.propnet.architecture.PropNet;
import org.ggp.base.util.propnet.architecture.components.Proposition;
import org.ggp.base.util.propnet.factory.OptimizingPropNetFactory;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class PropMachine extends StateMachine{
	/** The underlying proposition network  */
    private PropNet propNet; //From SamplePropNetStateMachine
    /** The topological ordering of the propositions */
    private List<Proposition> ordering; //From SamplePropNetStateMachine
    /** The player roles */
    private List<Role> roles; //From SamplePropNetStateMachine
    //For getting  base propositions: propNet.getBasePropositions()
    //For getting input propositions: propNet.getInputPropositions()



	@Override
	public void initialize(List<Gdl> description) {
		try {
			/*From SamplePropNetStateMachine*/
			propNet = OptimizingPropNetFactory.create(description);
			roles = propNet.getRoles();
            ordering = getOrdering();

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

    /**
     * Computes the goal for a role in the current state.
     * Should return the value of the goal proposition that
     * is true for that role. If there is not exactly one goal
     * proposition true for that role, then you should throw a
     * GoalDefinitionException because the goal is ill-defined.
     */
	@Override
	public int getGoal(MachineState state, Role role)
			throws GoalDefinitionException {
		Map<Role, Set<Proposition>> n = propNet.getGoalPropositions();
		 // TODO: Compute the goal for role in state.

		for(Proposition p : propNet.getPropositions()){
			for(Proposition k : n.get(role))
			{
				System.out.println("Proposition k " + k);

			}
		}
	//Það sem við þurfum mögulega að nota:
		propNet.getGoalPropositions();
		//if there is more or less than one goal then:
			//throw new GoalDefinitionException(the machinestate, role);

		return -1;
	}

    /**
     * Computes if the state is terminal. Should return the value
     * of the terminal proposition for the state.
     */
	@Override
	public boolean isTerminal(MachineState state) {
		// TODO: Compute whether the MachineState is terminal.
		/*
		 * Can use ?
		 * A reference to the single, unique, TerminalProposition.
		 * private final Proposition terminalProposition;
		 *
		 */
		return false;
	}

	@Override
	public List<Role> getRoles() {
		return roles;
	}

    /**
     * Returns the initial state. The initial state can be computed
     * by only setting the truth value of the INIT proposition to true,
     * and then computing the resulting state.
     */
	@Override
	public MachineState getInitialState() {
		 // TODO: Compute the initial state.
		//Proposition init = propNet.getInitProposition();
		Set<Component> components = propNet.getComponents();
		String delims = "[\";,]";
		String init = "";
		for(Component x : components){
			//System.out.println(x);

			String s = x.getOutputs().toString();

			if(s.contains("init")){
				//System.out.println(s);
				String[] tokens = s.split(delims);
				System.out.println(x.getValue());
				for(String token : tokens){
					if(token.contains("init")){
						//System.out.println(token);
						//init += token + " ";
						init += token + " ";
					}
				}
				break;
			}

		}
		//System.out.println("This is init" + init);

		//TODO: Create a sett of gdl sentences from the string init, then create a new machine state with that as input
		//MachineState initState = new MachineState(init);


		return null;
	}

    /**
     * Computes the legal moves for role in state.
     */
	@Override
	public List<Move> getLegalMoves(MachineState state, Role role)
			throws MoveDefinitionException {
		// TODO: Compute legal moves.
		return null;
	}

    /**
     * Computes the next state given state and the list of moves.
     */
	@Override
	public MachineState getNextState(MachineState state, List<Move> moves)
			throws TransitionDefinitionException {
        // TODO: Compute the next state.
		return null;
	}

	 /**
     * This should compute the topological ordering of propositions.
     * Each component is either a proposition, logical gate, or transition.
     * Logical gates and transitions only have propositions as inputs.
     *
     * The base propositions and input propositions should always be exempt
     * from this ordering.
     *
     * The base propositions values are set from the MachineState that
     * operations are performed on and the input propositions are set from
     * the Moves that operations are performed on as well (if any).
     *
     * @return The order in which the truth values of propositions need to be set.
     */
	 //From SamplePropNetStateMachine
    public List<Proposition> getOrdering()
    {
        // List to contain the topological ordering.
        List<Proposition> order = new LinkedList<Proposition>();

        // All of the components in the PropNet
        List<Component> components = new ArrayList<Component>(propNet.getComponents());

        // All of the propositions in the PropNet.
        List<Proposition> propositions = new ArrayList<Proposition>(propNet.getPropositions());

        // TODO: Compute the topological ordering.

        return order;
    }
}
