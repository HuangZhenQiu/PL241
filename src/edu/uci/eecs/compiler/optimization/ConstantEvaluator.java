package edu.uci.eecs.compiler.optimization;

import edu.uci.eecs.compiler.model.Program;
import edu.uci.eecs.compiler.model.Method;
import edu.uci.eecs.compiler.representation.Block;
import edu.uci.eecs.compiler.representation.SSAInstruction;
import edu.uci.eecs.compiler.representation.SSAInstruction.*;

/**
 * 1 Propagate the constant instructions
 * 2 Evaluate the Conditional Branch if the condition is predefined.
 *   change to unconditional branch, if it is possible. 
 * 
 * @author Peter
 *
 */
public class ConstantEvaluator implements Optimizer {
	
	private Program program;
	
	public ConstantEvaluator(Program program) {
		this.program = program;
	}
	
	public void optimize() {
		for(Method method: program.getMethods()) {
			for(Block block:method.getBlocks()) {
				for(SSAInstruction instruction: block.getInstructions()) {
					if(instruction instanceof BinarySSAInstruction) {
						if(instruction instanceof ComputationSSAInstruction) {
							ComputationSSAInstruction cmp = (ComputationSSAInstruction)instruction;
							if(cmp.getLeftOperand() instanceof ConstantSSAInstruction &&
									cmp.getRightOperand() instanceof ConstantSSAInstruction) {
								int result = 0;
								int leftValue = ((ConstantSSAInstruction)cmp.getLeftOperand()).getValue();
								int rightValue = ((ConstantSSAInstruction)cmp.getRightOperand()).getValue();
								switch (cmp.getType()) {
									case ADD:
										result = leftValue + rightValue;
										break;
									case MINUS:
										result = leftValue - rightValue;
										break;
									case TIMES:
										result = leftValue * rightValue;
										break;
									case DIVIDE:
										result = leftValue / rightValue;
									default:
										throw new RuntimeException("Unexpected Binary Computation Operator Type!");
								}
								cmp.replace(new ConstantSSAInstruction(null, result));
								
							}
							

						} else if(instruction instanceof ConditionSSAInstruction) {
							ConditionSSAInstruction condition = (ConditionSSAInstruction)instruction;
							if(condition.getLeftOperand() instanceof ConstantSSAInstruction &&
									condition.getRightOperand() instanceof ConstantSSAInstruction) {
								int result = 0;
								int leftValue = ((ConstantSSAInstruction)condition.getLeftOperand()).getValue();
								int rightValue = ((ConstantSSAInstruction)condition.getRightOperand()).getValue();
								switch (condition.getType()) {
									case EQUAL:
										result = leftValue == rightValue ? 1 : 0;
										break;
									case NEQUAL:
										result = leftValue == rightValue ? 0 : 1;
										break;
									case LESS:
										result = leftValue < rightValue ? 1 : 0;
										break;
									case LARGER:
										result = leftValue > rightValue ? 1 : 0;
										break;
									case LEQ:
										result = leftValue <= rightValue ? 1 : 0;
										break;
									case GEQ:
										result = leftValue >= rightValue ? 1 : 0;
										break;
									default:
										throw new RuntimeException("Unexpected Binary Computation Operator Type!");
								}
								condition.replace(new ConstantSSAInstruction(null, result));
							}
						}
					} else if (instruction instanceof ConditionalBranchSSAInstruction) {
						/*
						 * If an Conditional Branch Instruction as a constants condition after constant propagation, then
						 * we can know which way to branch and replace it to uncondition branch.
						 * It helps to find more unreachable blocks.
						 */
						ConditionalBranchSSAInstruction conditionBranch = (ConditionalBranchSSAInstruction) instruction;
						if(conditionBranch.getCondition() instanceof ConstantSSAInstruction) {
							 int value = ((ConstantSSAInstruction)conditionBranch.getCondition()).getValue();
							 Block branchBlock = conditionBranch.getBranchBlock();
							 Block fallThroughBlock = conditionBranch.getFallThroughBlock();
							 UnconditionalBranchSSAInstruction branch  = null;
							 if(value == 1) {
								 branch = new UnconditionalBranchSSAInstruction(null, branchBlock);
								 fallThroughBlock.removePrecedentBlock(conditionBranch.getBlock());
								 conditionBranch.replaceInPlace(branch);
							 } else {
								 branch = new UnconditionalBranchSSAInstruction(null, fallThroughBlock);
								 branchBlock.removePrecedentBlock(conditionBranch.getBlock());
								 conditionBranch.replaceInPlace(branch);
							 }
						}
					}
					
				}
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
