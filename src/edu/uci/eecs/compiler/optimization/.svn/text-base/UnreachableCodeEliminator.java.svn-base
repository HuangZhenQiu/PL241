package edu.uci.eecs.compiler.optimization;

import edu.uci.eecs.compiler.model.Program;
import edu.uci.eecs.compiler.model.Method;
import edu.uci.eecs.compiler.representation.Block;
import edu.uci.eecs.compiler.representation.Block.JoinBlock;
import edu.uci.eecs.compiler.representation.SSAInstruction;
import edu.uci.eecs.compiler.representation.SSAInstruction.*;

/**
 * 1 code after branch in a block
 * 2 the condition is content, no branch to particular block, remove the entire block.
 * 
 * @author Peter
 *
 */
public class UnreachableCodeEliminator implements Optimizer{
	
	private Program program;
	
	public UnreachableCodeEliminator(Program program) {
		this.program = program;
	}
	
	public void optimize() {
		removeUnreachableBlocks();
	}
	
	/**
	 * 
	 * remove blocks without precedent blocks
	 */
	private void removeUnreachableBlocks() {
		for(Method method : program.getMethods()) {
			for(Block block : method.getBlocks()) {
				if(block.getPrecdentBlockNumber() == 0 && !block.isEntry()) {
					for(Block successor : block.getSubsequentBlocks()) {
						if(successor instanceof JoinBlock) {
							for(PhiSSAInstruction phi: ((JoinBlock)successor).getAllPhis()) {
								phi.removeOperandForBlock(block);
							}
						}
					}	
					method.removeBlock(block);
				} else {
					removeUnreachableInstructions(block);
				}
			}
		}
	}
	
	/**
	 * remove all the instructions that is behind a unconditional branch.
	 * 
	 * @param block
	 */
	private void removeUnreachableInstructions(Block block) {
		boolean remove = false;
		for(SSAInstruction instruction : block.getInstructions()) {
			if(instruction instanceof UnconditionalBranchSSAInstruction) {
				remove = true;
			} else if(remove) {
				System.out.print("Remove Unreachable Instruction: " + instruction);
				block.removeInstruction(instruction);
			}
			
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
