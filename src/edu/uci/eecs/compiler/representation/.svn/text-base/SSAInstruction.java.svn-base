package edu.uci.eecs.compiler.representation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import edu.uci.eecs.compiler.model.UsageList;
import edu.uci.eecs.compiler.util.ComputationOperatorType;
import edu.uci.eecs.compiler.util.ComparsionOperatorType;

/**
 * Instructions included:
 *    
 *    Basic Class
 *       SSAInstruction
 *       
 *    Two Operands Related
 *        BinarySSAInstruction   <abstract class>
 *    	  ConditionSSAInstruction
 *        ComputationSSAInstruction
 *    
 *    Branch Related
 *        ConditionalBranchSSAInstruction
 *        UnConditionalBranchSSAInstruction
 *    
 *    
 *    
 *    Load Store Related
 *    	  IndexSSAInstruction
 *        LoadSSAInstruction
 *        LoadVariableSSAInstruction
 *        LoadAddressSSAInstruction
 *        StoreVariableInstruction
 *        StoreSSAInstruction
 *    
 *    
 *    Function Call Related:
 *        ParameterSSAInstruction
 *        CallSSAInstruction
 *        ReturnSSAInstruction
 *        ReturnValueInstruction
 *        
 *        //native calls
 *        ReadSSAInstruction
 *        WriteSSAInstruction
 *        WritelnSSAInstruction
 *    
 *    Phi Related
 *        PhiSSAInstruction    
 * 
 * 
 * @author Peter Huang
 *
 */


public class SSAInstruction {
	
	protected int number;
	protected Block block;
	protected UsageList usageList;
	
	protected boolean isPlaced;

	//global index for generating the index number for each instruction.
	protected static int nextInstructionNumber;
	
	public SSAInstruction(Block block) {
		this.block = block;
		if(this.block != null){
			this.block.addInstruction(this);
		}
		this.usageList = new UsageList(this);
		this.number = nextInstructionNumber++;
	}
	
	public int getInstructionId() {
		return this.number;
	}
	
	public boolean hasUsage() {
		return !usageList.isEmpty();
	}
	
	public Block getBlock() {
		return block;
	}
	
	/**
	 * Try to replace the operands for instructions that used this instruction
	 * @param originalInstruction
	 * @param newInstruction
	 */
	public void replaceUsage(SSAInstruction originalInstruction, SSAInstruction newInstruction){
		// It should be override by subclasses
	}
	
	public void addUsage(SSAInstruction instruction) {
		this.usageList.addUsage(instruction);
	}
	
	public void removeUsage(SSAInstruction instruction) {
		this.usageList.removeUsage(instruction);
	}
	
	public void visit(InstructionVisitor visitor) {
		//Base Class no need
	}
	
	/**
	 * Replace the original instructions with new Instruction
	 * for Common Subexpression Elimination
	 * @param newInstruction
	 */
	public void replace(SSAInstruction newInstruction) {
		usageList.notifyUsages(newInstruction);
		block.removeInstruction(this);
	}
	
	public void replaceInPlace(SSAInstruction newInstruction) {
		newInstruction.block = this.block;
		newInstruction.number = this.number;
		block.replaceInstruction(this, newInstruction);
		
	}
	
	@Override
	public String toString() {
		return this.number + " := ";
	}
	
	/****************** Instructions for binary computation *******************/
	//Instructions for computations + - * / 
	public static class BinarySSAInstruction extends SSAInstruction {
		protected SSAInstruction leftOperand;
		protected SSAInstruction rightOperand;
		
		protected BinarySSAInstruction (Block block,
				SSAInstruction leftOperand, SSAInstruction rightOperand) {
			super(block);
			this.leftOperand = leftOperand;
			this.rightOperand = rightOperand;
			this.leftOperand.addUsage(this);
			this.rightOperand.addUsage(this);
			
		}

		public SSAInstruction getLeftOperand() {
			return leftOperand;
		}

		public void setLeftOperand(SSAInstruction leftOperand) {
			this.leftOperand = leftOperand;
		}

		public SSAInstruction getRightOperand() {
			return rightOperand;
		}

		public void setRightOperand(SSAInstruction rightOperand) {
			this.rightOperand = rightOperand;
		}
		
		public void replaceUsage(SSAInstruction originalInstruction, SSAInstruction newInstruction) {
			
			if(leftOperand.equals(originalInstruction)) {
				leftOperand = newInstruction;
			} else if (rightOperand.equals(originalInstruction)) {
				rightOperand = newInstruction;
			}
		}
		
		public void visit(InstructionVisitor visitor) {
			//Base class no need
		}
		
	}
	
	//Instructions for equal nequal ...
	public static class ConditionSSAInstruction extends BinarySSAInstruction {
		private ComparsionOperatorType type;
		
		public ConditionSSAInstruction(Block block, ComparsionOperatorType comparisonType,
				SSAInstruction leftOperand, SSAInstruction rightOperand){
			super(block, leftOperand, rightOperand);
			this.type = comparisonType;
		}

		public ComparsionOperatorType getType() {
			return type;
		}
		
		@Override
		public String toString() {
			return super.toString() + leftOperand.number + type + rightOperand.number;
		}
		
		public void visit(InstructionVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	//Instructions for + - * /
	public static class ComputationSSAInstruction extends BinarySSAInstruction {
		
		private ComputationOperatorType type;
		public ComputationSSAInstruction(Block block, ComputationOperatorType computationType,
				SSAInstruction leftOperand, SSAInstruction rightOperand){
			super(block, leftOperand, rightOperand);
			this.type = computationType;
		}
		
		public ComputationOperatorType getType() {
			return type;
		}
		
		@Override
		public String toString() {
			return super.toString() + leftOperand.number + type + rightOperand.number;
		}
		
		public void visit(InstructionVisitor visitor) {
			visitor.visit(this);
		}
		
	}
	
	/****************** Instructions for Branch *******************/
	public static class ConditionalBranchSSAInstruction extends SSAInstruction {
		
		private SSAInstruction condition;
		private Block branchBlock;
		private Block fallThroughBlock;
		
		public ConditionalBranchSSAInstruction(Block block, ConditionSSAInstruction condition, Block bralock,
				Block fallThroughBlock){
			super(block);
			this.condition =condition;
			this.branchBlock = bralock;
			this.fallThroughBlock = fallThroughBlock;
		}
		
		public Block getBranchBlock() {
			return this.branchBlock;
		}
		
		public Block getFallThroughBlock() {
			return this.fallThroughBlock;
		}
		
		public SSAInstruction getCondition() {
			return condition;
		}
		
		@Override
		public void replaceUsage(SSAInstruction original, SSAInstruction newInstruction) {
			
			if(condition.equals(original)) {
				condition = newInstruction;
			}
		}
		
		@Override
		public String toString() {
			return super.toString() + "Branch on " + condition.number +" -> " + branchBlock  + " | " + fallThroughBlock;
		}
		
		public void visit(InstructionVisitor visitor) {
			visitor.visit(this);
		}
		
	}
	
	public static class UnconditionalBranchSSAInstruction extends SSAInstruction {
		private Block branchBlock;
		
		public UnconditionalBranchSSAInstruction(Block block, Block branchBlock) {
			super(block);
			this.branchBlock = branchBlock;
			
		}
		
		@Override
		public String toString() {
			return super.toString() + "Branch " + branchBlock;
		}
		
		public void visit(InstructionVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	/****************** Instructions for Load and Store *******************/
	
	public static class LoadSSAInstruction extends SSAInstruction{
		private SSAInstruction address;
		
		public LoadSSAInstruction(Block block, SSAInstruction address) {
			super(block);
			this.address = address;
		}
		
		@Override
		public void replaceUsage(SSAInstruction original, SSAInstruction newInstruction) {
			if(address == original) {
				original.removeUsage(this);
				address = newInstruction;
			}
		}
		
		@Override
		public String toString() {
			return super.toString() + "LoadAddress " + address.number;
		}
		
		public void visit(InstructionVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	//Use to load a variable
	public static class LoadVariableSSAInstruction extends SSAInstruction {
		private String identifier;
		
		public LoadVariableSSAInstruction(Block block, String identifier) {
			super(block);
			this.identifier = identifier;
		}
		
		@Override
		public String toString() {
			return super.toString() + "LoadVariable" + identifier;
		}
		
		public void visit(InstructionVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	//Use to load a variable in array
	public static class LoadArraySSAInstruction extends SSAInstruction{
		private String name;
		
		public LoadArraySSAInstruction(Block block, String arrayName) {
			super(block);
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		@Override
		public String toString() {
			return super.toString() + "LoadArray " + name;
		}
		
		public void visit(InstructionVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	public static class DeclareArraySSAInstruction extends SSAInstruction {
		private String name;
		private ArrayList<Integer> dimentions;
		
		public DeclareArraySSAInstruction(Block block, String name, ArrayList<Integer> dimentions) {
			super(block);
			this.name = name;
			this.dimentions = dimentions;
		}
		
		public String getName() {
			return name;
		}
		
		public int getDimentionNumber() {
			return dimentions.size();
		}
		
		public int getDimention(int i) {
			return dimentions.get(i);
		}
		
		public ArrayList<Integer> getDimentions() {
			return dimentions;
		}
		
		@Override
		public String toString() {
			return super.toString() + "DelareArry " + name;
		}
		
		public void visit(InstructionVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	//Use for load from array, records the postion of particular dimention
	public static class IndexSSAInstruction extends SSAInstruction {
		private int multipler;
		private SSAInstruction reference;
		private SSAInstruction expression;
		
		public IndexSSAInstruction(Block block, SSAInstruction reference, SSAInstruction expression, int multipler) {
			super(block);
			this.reference = reference;
			this.expression = expression;
			this.multipler = multipler;
		}

		public int getMultipler() {
			return multipler;
		}

		public SSAInstruction getReference() {
			return reference;
		}

		public SSAInstruction getExpression() {
			return expression;
		}
		
		//replace the expression for the index instruction
		public void replaceUsage(SSAInstruction originalInstruction, SSAInstruction newInstruction) {
			
			if(expression.equals(originalInstruction)) {
				this.expression = newInstruction;
			}
		}
		
		@Override
		public String toString() {
			return super.toString() + "Ref " + reference.number + " Expression " + expression.number;
		}
		
		public void visit(InstructionVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	public static class StoreSSAInstruction extends SSAInstruction {
		private SSAInstruction address;
		private SSAInstruction value;
		
		public StoreSSAInstruction(Block block, SSAInstruction address, SSAInstruction value) {
			super(block);
			this.address = address;
			this.value = value;
			address.addUsage(this);
			value.addUsage(this);
		}
		
		@Override
		public String toString() {
			return super.toString() + "Store Address " + address.number + " Value " + value.number; 
		}
		
		public void visit(InstructionVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	public static class StoreVariableSSAInstruction extends SSAInstruction {
		private String name;
		private SSAInstruction value;
		
		public StoreVariableSSAInstruction(Block block, String name, SSAInstruction value) {
			super(block);
			this.name = name;
			this.value = value;
			value.addUsage(this);
		}
		
		@Override
		public String toString() {
			return super.toString() + "Store Variable " + name + "Value " + value.number;
		}
		
		public void visit(InstructionVisitor visitor) {
			visitor.visit(this);
		}
	}

	/****************** Instructions for Phi function *******************/
	
	public static class PhiSSAInstruction extends SSAInstruction{
		private String name;
		private HashMap<Block,  SSAInstruction> operands;
		
		public PhiSSAInstruction(Block block, String name) {
			super(block);
			this.name = name;
			this.operands = new HashMap<Block, SSAInstruction>();
		}
		
		public String getVariableName() {
			return this.name;
		}
		
		public void updateOperands() {
			for(Block precedent : block.getPrecedentBlocks()){
				SSAInstruction instruction = precedent.getEndState().getVariable(name);
				if(instruction!=null) {
					operands.put(precedent, instruction);
				}
			}
		}
		
		public void addOperand(Block block, SSAInstruction instruction) {
			if (!operands.containsKey(block)) {
				operands.put(block, instruction);
				instruction.addUsage(this);
			} else {
				operands.get(block).removeUsage(this);
				operands.put(block, instruction);
			}
		}
		
		public void replaceUsage(SSAInstruction original, SSAInstruction newInstruction) {
			if(operands.containsValue(original)) {
				original.removeUsage(this);
				for(SSAInstruction instruction : operands.values()) {
					if (instruction.equals(original)) {
						instruction = newInstruction;
					}
				}
			}
		}
		
		public Collection<SSAInstruction> getOperands() {
			return operands.values();
		}
		
		public boolean hasOperand(SSAInstruction instruction) {
			return operands.containsValue(instruction);
		}
		
		public SSAInstruction getOperandForBlock(Block block) {
			return operands.get(block);
		}
		
		public void removeOperandForBlock(Block block) {
			this.operands.remove(block);
		}
		
		public String getOperandString() {
			String content ="";
			for(Entry<Block, SSAInstruction> entry : operands.entrySet()) {
				content += "(" + entry.getKey().getId() +"."+ entry.getValue().number + ")";
			}
			return content;
		}
		
		@Override
		public String toString() {
			return super.toString() + "Phi " + name +"{" + getOperandString() + "}";
		}
		
		public void visit(InstructionVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	/****************** Instructions for function call *******************/
	
	public static class ParameterSSAInstruction extends SSAInstruction{
		private String name;
		public ParameterSSAInstruction(Block block, String name) {
			super(block);
			this.name = name;
		}
		
		@Override
		public String toString() {
			return super.toString() + "Parameter "+ name;
		}
		
		public void visit(InstructionVisitor visitor) {
			visitor.visit(this);
		}
	}

	public static class CallSSAInstruction extends SSAInstruction{
		private String methodName;
		private List<SSAInstruction> parameters;
		
		public CallSSAInstruction(Block block, String name, List<SSAInstruction> parameters) {
			super(block);
			this.methodName = name;
			this.parameters = parameters;
			for(SSAInstruction instruction : parameters) {
				instruction.addUsage(this);
			}
		}
		
		public List<SSAInstruction> getParameters() {
			return parameters;
		}
		
		public int getParameterSize() {
			return parameters.size();
		}
		
		public SSAInstruction getParmater(int i) {
			if(i < parameters.size())
				return parameters.get(i);
			return null;
		}
		
		public void replaceUsage(SSAInstruction original, SSAInstruction newInstruction) {

			for(SSAInstruction instruction : parameters) {
				if (instruction.equals(original)) {
					instruction = newInstruction;
					newInstruction.addUsage(this);
				}
			}
		}
		
		@Override
		public String toString() {
			return super.toString() + " Call " + methodName;
		}
		
		public void visit(InstructionVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	public static class ReturnSSAInstruction extends SSAInstruction{
		public ReturnSSAInstruction(Block block) {
			super(block);
		}
		
		@Override
		public String toString() {
			return super.toString() + "Return";
		}
		
		public void visit(InstructionVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	public static class ReturnValueSSAInstruction extends SSAInstruction{
		private SSAInstruction value;
		
		public ReturnValueSSAInstruction(Block block, SSAInstruction value) {
			super(block);
			this.value = value;
			value.addUsage(this);
		}
		
		public void replaceUsage(SSAInstruction original, SSAInstruction newInstruction) {
			if(original.equals(value)) {
				value = newInstruction;
				newInstruction.addUsage(this);
			}
		}
		
		@Override
		public String toString() {
			return super.toString() + "Return " + value.number;
		}
		
		public void visit(InstructionVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	/****************** Instructions for nativ function call *******************/
	public static class ReadSSAInstruction extends SSAInstruction{
		public ReadSSAInstruction(Block block) {
			super(block);
		}
		
		@Override
		public String toString() {
			return super.toString() + "Read";
		}
	}
	
	public static class WriteSSAInstruction extends SSAInstruction{
		private SSAInstruction operand;
		public WriteSSAInstruction(Block block, SSAInstruction operand) {
			super(block);
			this.operand = operand;
			operand.addUsage(this);
		}
		
		public void replaceUsage(SSAInstruction original, SSAInstruction newInstruction) {
			if(operand.equals(operand)) {
				operand = newInstruction;
				newInstruction.addUsage(this);
			}
		}
		
		@Override
		public String toString() {
			return super.toString() + "Write " + operand.number;
		}
		
		public void visit(InstructionVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	public static class WritelnSSAInstruction extends SSAInstruction {
		public WritelnSSAInstruction(Block block) {
			super(block);
		}
		
		@Override
		public String toString() {
			return super.toString() + "Writeln ";
		}
		
		public void visit(InstructionVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	public static class ConstantSSAInstruction extends SSAInstruction {
		private int value;
		
		public ConstantSSAInstruction(Block block, int value) {
			super(block);
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return super.toString() + "Constant " + value;
		}
		
		public void visit(InstructionVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	public static ConstantSSAInstruction createInitialValue() {
		return new ConstantSSAInstruction(null, 0);
	}

}
