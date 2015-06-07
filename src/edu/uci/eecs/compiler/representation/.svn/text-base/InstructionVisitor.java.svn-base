package edu.uci.eecs.compiler.representation;

import edu.uci.eecs.compiler.representation.SSAInstruction.*;

/**
 * 
 *  *    Two Operands Related
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
 *        LoadArraySSAInstruction
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
 * @author Peter
 *
 */
public interface InstructionVisitor {

	public void visit(ComputationSSAInstruction instruction);
	
	public void visit(ConditionSSAInstruction instruction);
	
	public void visit(ConditionalBranchSSAInstruction instruction);
	
	public void visit(UnconditionalBranchSSAInstruction instruction);
	
	public void visit(DeclareArraySSAInstruction instruction);
	
	public void visit(IndexSSAInstruction instruction);
	
	public void visit(LoadSSAInstruction instruction);
	
	public void visit(LoadVariableSSAInstruction instruction);
	
	public void visit(LoadArraySSAInstruction instruction);
	
	public void visit(StoreVariableSSAInstruction instruction);
	
	public void visit(StoreSSAInstruction instruction);
	
	public void visit(ParameterSSAInstruction instruction);
	
	public void visit(CallSSAInstruction instruction);
	
	public void visit(ReturnSSAInstruction instruction);
	
	public void visit(ReturnValueSSAInstruction instruction);
	
	public void visit(ReadSSAInstruction instruction);
	
	public void visit(WriteSSAInstruction instruction);
	
	public void visit(WritelnSSAInstruction instruction);
	
	public void visit(PhiSSAInstruction instruction);
	
	public void visit(ConstantSSAInstruction instruction);
}
