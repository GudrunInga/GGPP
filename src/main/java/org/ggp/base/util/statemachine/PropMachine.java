package org.ggp.base.util.statemachine;

import java.util.List;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.propnet.architecture.PropNet;
import org.ggp.base.util.propnet.factory.OptimizingPropNetFactory;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class PropMachine extends StateMachine{
	/** The underlying proposition network  */
    private PropNet propNet;
	@Override
	public void initialize(List<Gdl> description) {
		// TODO Auto-generated method stub
		try {
			//There is more implementation in SamplePropNetStateMachine, maybe we need that too?
			propNet = OptimizingPropNetFactory.create(description);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getGoal(MachineState state, Role role)
			throws GoalDefinitionException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isTerminal(MachineState state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Role> getRoles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MachineState getInitialState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Move> getLegalMoves(MachineState state, Role role)
			throws MoveDefinitionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MachineState getNextState(MachineState state, List<Move> moves)
			throws TransitionDefinitionException {
		// TODO Auto-generated method stub
		return null;
	}

}
