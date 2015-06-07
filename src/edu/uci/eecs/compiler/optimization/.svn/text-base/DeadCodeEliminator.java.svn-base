package edu.uci.eecs.compiler.optimization;

import edu.uci.eecs.compiler.model.Program;
import edu.uci.eecs.compiler.model.Method;

import edu.uci.eecs.compiler.representation.SSAInstruction;
import edu.uci.eecs.compiler.representation.Block;
import edu.uci.eecs.compiler.representation.SSAInstruction.*;

/**
 * 
 * Remove the code without usage
 * 
 * @author Peter
 *
 */

public class DeadCodeEliminator implements Optimizer {
	private Program program;
	
	public DeadCodeEliminator(Program program) {
		this.program = program;
	}
	
	
	public void optimize() {
		for(Method method: program.getMethods()) {
			for(Block block:method.getBlocks()) {
				for(SSAInstruction instruction: block.getInstructions()) {
					if(hasValue(instruction)) {
						if(!instruction.hasUsage()){
							block.removeInstruction(instruction);
						}
					}
				}
			}
			
		}
	}
	
	private boolean hasValue(SSAInstruction instruction) {
		if(instruction instanceof ConditionalBranchSSAInstruction ||
			instruction instanceof UnconditionalBranchSSAInstruction ||
			instruction instanceof StoreVariableSSAInstruction ||
			instruction instanceof StoreSSAInstruction ||
			instruction instanceof ReturnSSAInstruction ||
			instruction instanceof ReturnValueSSAInstruction ||
			instruction instanceof DeclareArraySSAInstruction ||
			instruction instanceof CallSSAInstruction ||
			instruction instanceof WriteSSAInstruction ||
			instruction instanceof WritelnSSAInstruction) {
			return false;
		}
		
		return true;
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
