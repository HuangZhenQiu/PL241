package edu.uci.eecs.compiler.representation;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.compiler.model.Method;
import edu.uci.eecs.compiler.model.State;
import edu.uci.eecs.compiler.representation.SSAInstruction.*;
import edu.uci.eecs.compiler.util.Constant;

public class Block {
	private static int nextBlockId = 0;
	protected int id;
	protected Method method;
	protected State beginState;
	protected State endState;
	protected Block dominator;
	protected ArrayList<Block> precedentBlocks;
	protected ArrayList<Block> subsequentBlocks;
	protected ArrayList<SSAInstruction> instructions;
	
	protected boolean join;
	protected boolean exit;
	protected boolean entry;
	
	
	public Block(Method method) {
		this.id = nextBlockId ++;
		this.method = method;
		this.method.addBlock(this);
		this.precedentBlocks = new ArrayList<Block>();
		this.subsequentBlocks = new ArrayList<Block>();
		this.instructions = new ArrayList<SSAInstruction>();
		this.join = false;
		this.exit = false;
		this.entry = false;
	}
	
	public void removePrecedentBlock(Block block) {
		this.precedentBlocks.remove(block);
	}

	public void addPrecedentBlock(Block block) {
		this.precedentBlocks.add(block);
		block.addSubsequentBlock(block);
	}
	
	public void addSubsequentBlock(Block block) {
		this.subsequentBlocks.add(block);
		block.precedentBlocks.add(this);
	}
	
	public static int getNextBlockId() {
		return nextBlockId;
	}

	public static void setNextBlockId(int nextBlockId) {
		Block.nextBlockId = nextBlockId;
	}
	
	public void visit(BlockVisitor visitor) {
		visitor.visit(this);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public State getEndState() {
		return endState;
	}

	public void setEndState(State endState) {
		this.endState = endState;
	}

	public Block getDominator() {
		return dominator;
	}

	public void setDominator(Block dominator) {
		this.dominator = dominator;
	}

	public ArrayList<Block> getPrecedentBlocks() {
		return precedentBlocks;
	}
	
	public int getPrecdentBlockNumber() {
		return precedentBlocks.size();
	}

	public Block getPrecedentBlock(int i) {
		if(i< 0 || i > precedentBlocks.size()) {
			return null;
		}
		
		return precedentBlocks.get(i);
	}

	public void setPrecedentBlocks(ArrayList<Block> precedentBlocks) {
		this.precedentBlocks = precedentBlocks;
	}

	public ArrayList<Block> getSubsequentBlocks() {
		return subsequentBlocks;
	}
	
	public boolean hasSubsequentBlock() {
		return !subsequentBlocks.isEmpty();
	}

	public void setSubsequentBlocks(ArrayList<Block> subsequentBlocks) {
		this.subsequentBlocks = subsequentBlocks;
	}

	public ArrayList<SSAInstruction> getInstructions() {
		return instructions;
	}
	
	public SSAInstruction getInstruction(int position) {
		return this.instructions.get(position);
	}

	public void setInstructions(ArrayList<SSAInstruction> instructions) {
		this.instructions = instructions;
	}
	
	public void addInstruction(SSAInstruction instruction) {
		this.instructions.add(instruction);
	}
	
	public void removeInstruction(SSAInstruction instruction) {
		this.instructions.remove(instruction);
	}
	
	public void replaceInstruction(SSAInstruction oldInstruction, SSAInstruction newInstruction){
		for(SSAInstruction instruction: instructions) {
			if(instruction.equals(oldInstruction)) {
				instruction = newInstruction;
				return;
			}
		}
		return;
	}

	public State getBeginState() {
		return beginState;
	}

	public void setBeginState(State state) {
		this.beginState = state;
	}
	
	public boolean isJoin() {
		return join;
	}
	
	public boolean isExit() {
		return exit;
	}

	public boolean isEntry() {
		return entry;
	}
	
	public boolean isLoop() {	
		return false;
	}
	
	public boolean isDominater(Block block) {
		
		Block dominator = this.getDominator();
		while(dominator!=null){
			
			if(dominator.equals(block)) {
				return true;
			}
			dominator = dominator.getDominator();
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "Block " + id;
	}
	
	public String toGraph() {
		String blockContent= "	subgraph cluster" + id + "{ style=filled; color=yellow; node [fontsize = 12, shape=box, align=left]; " + Constant.LINE_SEPARATER;
		blockContent += "label = \"block " + id +"\";"+ Constant.LINE_SEPARATER;
		blockContent += "\"start" + id + "\" ->";
		for(SSAInstruction instruction : instructions) {
			if(instruction.equals(instructions.get(instructions.size()-1))) {
				if(instructions.size() == 1) {
					blockContent += "\"" + instruction.toString() + " \"";
				} else {
					blockContent += instruction.toString()+" \"";
				}
			} else if (instruction.equals(instructions.get(0))){
				blockContent +="\"" + instruction.toString() + "\\l";
			} else {
				blockContent += instruction.toString() + "\\l";
			}
			
		}
		blockContent += " -> \"end" + id + "\"";
		blockContent += ";}";
		return blockContent;
	}

	/**
	 * The first block for every method
	 * @author Peter
	 */
	static public class EntryBlock extends Block{
		
		private List<ParameterSSAInstruction> parameters;
		
		public EntryBlock(Method method) {
			super(method);
			method.setBeginBlock(this);
			this.entry = true;
			this.parameters =  new ArrayList<ParameterSSAInstruction>();
		}
		
		public void addParameter(ParameterSSAInstruction  param) {
			this.parameters.add(param);
		}
		
		@Override
		public String toString() {
			return "EntryBlock " + id;
		}
		
		public void visit(BlockVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	/**
	 * It store the phis for if-else and loop headers
	 * 
	 * @author Peter
	 *
	 */
	static public class JoinBlock extends Block{
		
		private boolean loop;
		private Block exitBlock;
		private Block bodyBlock;
		private List<PhiSSAInstruction> phis;

		public JoinBlock(Method method, boolean isLoop) {
			super(method);
			this.loop = isLoop;
			this.join = true;
			this.phis = new ArrayList<PhiSSAInstruction>();
		}
		
		@Override
		public boolean isLoop() {
			return loop;
		}
		
		public List<PhiSSAInstruction> getAllPhis() {
			return this.phis;
		}
		
		public void setExitBlock(Block exitBlock) {
			this.exitBlock = exitBlock;
		}
		
		public void setBodyBlock(Block bodyBlock) {
			this.bodyBlock = bodyBlock;
		}
		
		public void addPhiInstruction(PhiSSAInstruction phi) {
			phis.add(phi);
		}
		
		@Override
		public String toString() {
			return "JoinBlock " + this.id;
		}
		
		public void visit(BlockVisitor visitor) {
			visitor.visit(this);
		}
	}
	
	/**
	 * The block to handle with all the return parameters
	 * @author Peter
	 *
	 */
	static public class ExitBlock extends JoinBlock{
		
		public ExitBlock(Method method) {
			super(method, false);
			method.setExitBlock(this);
			this.exit = true;
		}
		
		
		@Override
		public String toString() {
			return "ExitBlock " + this.id;
		}
		
		public void visit(BlockVisitor visitor) {
			visitor.visit(this);
		}
	}

}
