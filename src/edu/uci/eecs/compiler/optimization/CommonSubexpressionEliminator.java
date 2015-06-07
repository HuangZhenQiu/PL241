package edu.uci.eecs.compiler.optimization;

import edu.uci.eecs.compiler.model.Program;
import edu.uci.eecs.compiler.representation.SSAInstruction.*;
import edu.uci.eecs.compiler.util.ComparsionOperatorType;
import edu.uci.eecs.compiler.util.ComputationOperatorType;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class CommonSubexpressionEliminator implements Optimizer {
	private Program program;
	private HashMap<ComparsionOperatorType, List<ConditionSSAInstruction>> conditionMap;
	private HashMap<ComputationOperatorType, List<ComputationOperatorType>> computationMap;
	
	public CommonSubexpressionEliminator(Program program) {
		this.program = program;
	}

	public void optimize() {
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
