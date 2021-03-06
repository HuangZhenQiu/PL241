package edu.uci.eecs.compiler.frontend;

import edu.uci.eecs.compiler.model.Program;
import edu.uci.eecs.compiler.representation.Block;
import edu.uci.eecs.compiler.representation.SSAInstruction;
import edu.uci.eecs.compiler.representation.SSAInstruction.*;
import edu.uci.eecs.compiler.representation.Block.EntryBlock;
import edu.uci.eecs.compiler.representation.Block.JoinBlock;
import edu.uci.eecs.compiler.representation.Block.ExitBlock;
import edu.uci.eecs.compiler.util.ComparsionOperatorType;
import edu.uci.eecs.compiler.util.ComputationOperatorType;
import edu.uci.eecs.compiler.util.Token;
import edu.uci.eecs.compiler.model.Method;
import edu.uci.eecs.compiler.model.State;

import java.util.ArrayList;
import java.util.List;

/**
 *  One round parse top to down to construct SSAInstruction and Control Flow Graph.
 * @author Peter
 *
 */
public class Parser {
	
	private Scanner scanner;
	private Program currentProgram;
	private Block currentBlock;
	private Method currentMethod;
	private State currentState;
	
	public Parser(Scanner scanner, Program program) {
		this.scanner = scanner;
		this.currentProgram = program;
	}
	
	private boolean expect(Token token) {
		
		return scanner.expectToken(token);
	}
	
	private void consume(Token token) {
		
		if(!expect(token)) {
			System.out.println("Can't consume the token:" + token.name() + " " + scanner.getLineNumber());
			return;
		}
		scanner.nextToken();
	}
	
	/*
	 * Add fields into symbol tables, global and local
	 */
	private void addVariableIntoTable(boolean isGlobal, String name, int lineNumber, ArrayList<Integer> dimentions){
		
		if(isGlobal) { //It is variable for whole program
			
			if(dimentions == null ) {
				//add initilaize value the store the value into global memory for static global variables
				new StoreVariableSSAInstruction(currentBlock, name, SSAInstruction.createInitialValue());
			} else {
				new DeclareArraySSAInstruction(currentBlock, name, dimentions);
			}
			currentProgram.addSymbol(name, lineNumber, dimentions);
			
		} else { //It is variable for current method
			if(dimentions == null) {
				currentState.setVariable(name, SSAInstruction.createInitialValue());
			} else {
				new DeclareArraySSAInstruction(currentBlock, name, dimentions);
			}
			currentMethod.addSymbol(name, lineNumber, dimentions);
		}
		
	}
	
	public Program parseProgram() {
		parseComputation();
		return currentProgram;
	}
	
	/**
	 * 
	 * return null for variable, LoadArray for array assignment
	 * @return SSAInstruction
	 */
	private SSAInstruction parseDesignator(boolean isAssignment) {
		SSAInstruction instruction = null;
		if (expect(Token.IDENTIFIER)) {
			String name = scanner.getCurrentContent();
			consume(Token.IDENTIFIER);
			
			if(!expect(Token.OPENBRACKET)) {
				if(!isAssignment) {
					instruction = getVariable(name);
				}
			} else {
				int currentDimensionIndex = 1;
				instruction = new LoadArraySSAInstruction(currentBlock, name);
				while(expect(Token.OPENBRACKET)){
					consume(Token.OPENBRACKET);
					SSAInstruction expression = parseExpression();
					int base = calculateIndexBase(name, currentDimensionIndex);
					instruction =  new IndexSSAInstruction(currentBlock, instruction, expression, base);
					consume(Token.CLOSEBRACKET);
					currentDimensionIndex ++;
				}
				
				if(!isAssignment) {
					instruction =  new LoadSSAInstruction(currentBlock, instruction);
				}
			}
		}
		return instruction;
	}
	
	private int calculateIndexBase(String name, int index) {
		List<Integer> dimentions;
		if(currentProgram.hasVariable(name)) {
			dimentions = currentProgram.getDimention(name);
		} else {
			dimentions =currentMethod.getDimension(name);
		}
		
		int base = 1;
		for (int i = index; i < dimentions.size(); i++) {
			base *= dimentions.get(i);
		}
		
		return base;
	}
	
	private SSAInstruction getVariable(String name) {
		SSAInstruction instruction = null;
		if(currentProgram.hasVariable(name)) {
			//load global variable;
			instruction = new LoadVariableSSAInstruction(currentBlock, name);
		} else {
			instruction = currentState.getVariable(name);
		}
		
		return instruction;
	}
	
	
	// designator | number | (expression) | functionCall
	private SSAInstruction parseFactor() {
		SSAInstruction instruction = parseDesignator(false);
		if(instruction == null) {
			if(expect(Token.NUMBER)) {
				instruction = new ConstantSSAInstruction(currentBlock, scanner.getCurrentNumber());
				consume(Token.NUMBER);
				return instruction;
			} else if(expect(Token.OPENPARENTHESES)) {
				consume(Token.OPENPARENTHESES);
				instruction = parseExpression();
				consume(Token.CLOSEPARENTHESES);
				return instruction;
			} else if(expect(Token.CALL)) {
				instruction = parseFunctionCall();
				return instruction;
			}
			
		} else {
			return instruction;
		}
		
		return instruction;
	}
	
	/**
	 *  factor {( * | /) factor}
	 */
	private SSAInstruction parseTerm() {
		SSAInstruction instruction = parseFactor();
		while(expect(Token.TIMES) || expect(Token.DIVIDE)) {
			if(expect(Token.TIMES)) {
				consume(Token.TIMES);
				SSAInstruction right = parseFactor();
				instruction = new ComputationSSAInstruction(currentBlock,
						ComputationOperatorType.TIMES, instruction, right);
			} else {
				consume(Token.DIVIDE);
				SSAInstruction right = parseFactor();
				instruction = new ComputationSSAInstruction(currentBlock,
						ComputationOperatorType.DIVIDE, instruction, right);
			}
		}
		
		return instruction;
	}
	
	/**
	 * term { (+ | -) term}
	 * 
	 * @return
	 */
	private SSAInstruction parseExpression() {
		SSAInstruction instruction = parseTerm();
		while(expect(Token.PLUS) || expect(Token.MINUS)) {
			if(expect(Token.PLUS)) {
				consume(Token.PLUS);
				SSAInstruction right = parseTerm();
				instruction = new ComputationSSAInstruction(currentBlock,
						ComputationOperatorType.ADD, instruction, right);
			} else {
				consume(Token.MINUS);
				SSAInstruction right = parseTerm();
				instruction = new ComputationSSAInstruction(currentBlock,
						ComputationOperatorType.MINUS, instruction, right);
			}
		}
		return instruction;
	}
	 
	/**
	 * expression op expression
	 * @return
	 */
	private ConditionSSAInstruction parseRelation() {
		SSAInstruction left = parseExpression();
		ComparsionOperatorType type= parseComparisonOperator();
		SSAInstruction right = parseExpression();
		
		return new ConditionSSAInstruction(currentBlock, type, left, right);
	}
	
	private ComparsionOperatorType parseComparisonOperator() {
		if(expect(Token.EQUAL)) {
			consume(Token.EQUAL);
			return ComparsionOperatorType.EQUAL;
		} else if(expect(Token.NEQUAL)) {
			consume(Token.NEQUAL);
			return ComparsionOperatorType.NEQUAL;
		} else if(expect(Token.LESS)) {
			consume(Token.LESS);
			return ComparsionOperatorType.LESS;
		} else if(expect(Token.LEQUAL)) {
			consume(Token.LEQUAL);
			return ComparsionOperatorType.LEQ;
		} else if(expect(Token.GREATER)) {
			consume(Token.GREATER);
			return ComparsionOperatorType.LARGER;
		} else if(expect(Token.GEQUAL)) {
			consume(Token.GEQUAL);
			return ComparsionOperatorType.GEQ;
		} else {
			throw new RuntimeException("Unexpected ComparisionOperator!!!");
		}
	}
	
	// let designator < - expression
	private void parseAssignment() {
		consume(Token.LET);
		String name = scanner.getCurrentContent();
		SSAInstruction dest = parseDesignator(true);
		consume(Token.ASSIGNMENT);
		SSAInstruction value = parseExpression();
		if(dest == null) { // it is a simple variable assignment
			storeSymbolValue(name, value);
		} else { //it is a array assignment
			SSAInstruction store = new StoreSSAInstruction(currentBlock, dest, value);
			//the instruction is added into current block
		}
	}
	
	//rewrite the value for variable in current state
	private void storeSymbolValue(String name, SSAInstruction instruction) {
		
		if(currentProgram.hasVariable(name)) {
			new StoreVariableSSAInstruction(currentBlock, name, instruction);
		} else {
			currentState.setVariable(name, instruction);
		}
	}
	
	// call identifier [ ( [expression { , expression } ] ) ]
	private SSAInstruction parseFunctionCall() {
		SSAInstruction instruction = null;
		
		if(expect(Token.CALL)) {
			consume(Token.CALL);
			List<SSAInstruction> arguments= new ArrayList<SSAInstruction>();
			String name = scanner.getCurrentContent();
			consume(Token.IDENTIFIER);
			if(expect(Token.OPENPARENTHESES)) {
				consume(Token.OPENPARENTHESES);
				if(name.equals("InputNum")) {
					instruction = new ReadSSAInstruction(currentBlock);
				} else if(name.equals("OutputNewLine")) {
					instruction = new WritelnSSAInstruction(currentBlock);
				} else if(name.equals("OutputNum")) {
					SSAInstruction operand = parseExpression();
					instruction = new WriteSSAInstruction(currentBlock, operand);
				} else {
					SSAInstruction argument = parseExpression();
					if(argument != null) {
						arguments.add(argument);
					}
					while(expect(Token.COMMA)) {
						consume(Token.COMMA);
						argument = parseExpression();
						arguments.add(argument);
					}
					
					instruction =  new CallSSAInstruction(currentBlock, name, arguments);
				}
				consume(Token.CLOSEPARENTHESES);
			}
			
			instruction =  new CallSSAInstruction(currentBlock, name, arguments);
		}
		
		return instruction;
	}
	
	/**
	 * if relation then statementSequence [ else statementSequence ] fi
	 */
	private void parseIfStatement() {
		
		if(expect(Token.IF)) {
			consume(Token.IF);
			Block relationBlock = currentBlock;
			Block thenBlock = new Block(currentMethod);
			Block elseBlock = new Block(currentMethod);
			JoinBlock joinBlock = new JoinBlock(currentMethod, false);
			
			//Process condition
			ConditionSSAInstruction relation = parseRelation();
			endCurrentBlockWithCondition(relation, thenBlock, elseBlock);
			State state = currentState.deepCopy();
			
			//Process the then scope
			consume(Token.THEN);
			beginBlock(thenBlock);
			parseStatementSequence();
			endBlockWithJump(joinBlock);  //end then scope
			
			//Process the else scope
			if(expect(Token.ELSE)) {
				consume(Token.ELSE);
				beginBlock(elseBlock);
				parseStatementSequence();
				endBlockWithJump(joinBlock);  //end else scope
				
			} else { //to simplify the code generation
				beginBlock(elseBlock);
				endBlockWithJump(joinBlock);
			}
			consume(Token.FI);
			enterJoinBlock(joinBlock);
			setConditionalDominator(relationBlock, thenBlock, elseBlock, joinBlock);
		}
		
	}
	
	private void enterJoinBlock(JoinBlock block) {
		currentBlock = block;
		insertPhiInstruction(block);
		currentBlock.setBeginState(currentState.deepCopy());
	}
	
	/**
	 * Set the dominator for if else blocks
	 * @param conditionBlock
	 * @param thenBlock
	 * @param elseBlock
	 * @param joinBlock
	 */
	private void setConditionalDominator(Block conditionBlock, Block thenBlock, Block elseBlock, JoinBlock joinBlock) {
		thenBlock.setDominator(conditionBlock);
		elseBlock.setDominator(conditionBlock);
		if(joinBlock.getPrecdentBlockNumber() == 1) {
			joinBlock.setDominator(thenBlock);
		} else {
			joinBlock.setDominator(conditionBlock);
		}
	}
	
	/**
	 * We need to insert Phi instruction for three types of structure:
	 * 1) If else 
	 * 2) While Loop
	 * 3) Return at the exit block
	 * @param block
	 */
	private void insertPhiInstruction(JoinBlock block) {
		int blockNumber = block.getPrecdentBlockNumber();
		if(blockNumber == 1) { // simple exit block with one return
			currentState = block.getPrecedentBlock(0).getEndState().deepCopy();
		} else if (blockNumber == 2) { //if and loop
			currentState = new State();
			Block thenBlock = block.getPrecedentBlock(0);
			Block elseBlock = block.getPrecedentBlock(1);
			
			//it is for local variables
			for(String symbol:currentMethod.getSymbols()) {
				addPhilForSymbol(symbol, block, thenBlock, elseBlock);
			}
			
		} else if (blockNumber > 3) { // the exit block with return values
			currentState = new State();
			PhiSSAInstruction phiForReturn= new PhiSSAInstruction(block, "return");
			block.addPhiInstruction(phiForReturn);
			currentState.setVariable("return", phiForReturn);
		}
	}
	
	private void addPhilForSymbol(String symbol, JoinBlock block, Block thenBlock, Block elseBlock) {
		SSAInstruction valueFromThen = thenBlock.getEndState().getVariable(symbol);
		SSAInstruction valueFromElse = elseBlock.getEndState().getVariable(symbol);
		if(!valueFromThen.equals(valueFromElse)) {
			PhiSSAInstruction phi = new PhiSSAInstruction(block, symbol);
			phi.addOperand(thenBlock, valueFromThen);
			phi.addOperand(elseBlock, valueFromElse);
			block.addPhiInstruction(phi);
			currentState.setVariable(symbol, phi);
		} else {
			currentState.setVariable(symbol, valueFromThen);
		}
	}
	
	private void endCurrentBlockWithCondition(ConditionSSAInstruction relation, Block thenBlock, Block elseBlock) {
		ConditionalBranchSSAInstruction instruction = new ConditionalBranchSSAInstruction(currentBlock, relation, thenBlock, elseBlock);
		currentBlock.setEndState(currentState.deepCopy());
		currentBlock.addSubsequentBlock(thenBlock);
		currentBlock.addSubsequentBlock(elseBlock);
	}
	
	
	/**
	 * while relation do statement od
	 * 
	 */
	private void parseWhileStatement() {
		if(expect(Token.WHILE)) {
			consume(Token.WHILE);
			JoinBlock join = new JoinBlock(currentMethod, true);
			endBlockWithJump(join);
			Block bodyBlock = new Block(currentMethod);
			Block exitBlock = new Block(currentMethod);
			join.setExitBlock(exitBlock);
			enterLoopJoinBlock(join);
			ConditionSSAInstruction condition = parseRelation();
			endCurrentBlockWithCondition(condition, bodyBlock, exitBlock);
			State beforeBody = currentState.deepCopy();
			
			consume(Token.DO);
			beginBlock(bodyBlock);
			parseStatementSequence();
			endBlockWithJump(join); // go to join if nessisary
			join.setBodyBlock(bodyBlock);
			consume(Token.OD);
			
			beginBranchBlock(exitBlock, beforeBody);
			bodyBlock.setDominator(join);
			exitBlock.setDominator(join);
		}
	}
	
	private void beginBranchBlock(Block block, State state) {
		currentBlock = block;
		currentState = state;
		block.setBeginState(currentState.deepCopy());
	}
	
	/**
	 * add phi for each symbals in advance, then remove some of them in processing body block
	 * @param join
	 */
	private void enterLoopJoinBlock(JoinBlock join) {
		join.setDominator(currentBlock);
		currentBlock = join;
		State joinState = new State();
		
		//create phi for all the symbols in current state
		for(String symbol: currentMethod.getSymbols()) {
			PhiSSAInstruction phi = new PhiSSAInstruction(currentBlock, symbol);
			joinState.addVariable(symbol, phi);
		}
		currentState = joinState;
		currentBlock.setBeginState(currentState.deepCopy());
	}
	
	/**
	 * returnStatement = return [expression]
	 */
	private void parseReturnStatement() {
		if(expect(Token.RETURN)) {
			consume(Token.RETURN);
			SSAInstruction value = parseExpression();
			currentState.addVariable("return", value);
			SSAInstruction branch = new UnconditionalBranchSSAInstruction(currentBlock, currentMethod.getExitBlock());
			currentBlock.addSubsequentBlock(currentMethod.getExitBlock());
			currentBlock.setEndState(currentState.deepCopy());
		}
	}
	
	/**
	 * statement = assignment | funcCall | ifStatement | whileStatement | returnStatement
	 */
	private void parseStatement() {
		if(expect(Token.LET)) {
			parseAssignment();
		} else if(expect(Token.CALL)) {
			parseFunctionCall();
		} else if(expect(Token.IF)) {
			parseIfStatement();
		} else if(expect(Token.WHILE)) {
			parseWhileStatement();
		} else if(expect(Token.RETURN)) {
			parseReturnStatement();
		}
	}
	
	/**
	 * statSequence = statement|{; statement}
	 */
	private void parseStatementSequence() {
		parseStatement();
		while(expect(Token.SEMICOLON)) {
			consume(Token.SEMICOLON);
			parseStatement();
		}
	}
	
	/**
	 * typeDecl = var | array | [number] { [number] }
	 */
	private ArrayList<Integer> parseTypeDeclaration() {
		ArrayList<Integer> arrayDimentions = null;
		if(expect(Token.VAR)) {
			consume(Token.VAR);
		} else if (expect(Token.ARRAY)){
			consume(Token.ARRAY);
			consume(Token.OPENBRACKET);
			consume(Token.NUMBER);
			arrayDimentions = new ArrayList<Integer>();
			arrayDimentions.add(scanner.getCurrentNumber());
			consume(Token.CLOSEBRACKET);
			
			//multiple dimentions
			while(expect(Token.OPENBRACKET)) {
				consume(Token.ARRAY);
				consume(Token.OPENBRACKET);
				consume(Token.NUMBER);
				arrayDimentions.add(scanner.getCurrentNumber());
				consume(Token.CLOSEBRACKET);
			}
			
		}
		
		return arrayDimentions;
	}
	
	/**
	 * typeDeclaration identifier { , identifier } ;
	 */
	private void parseVarDeclaration(boolean isGlobal) {
		
		while(expect(Token.VAR) || expect(Token.ARRAY)){
			ArrayList<Integer> dimentions = parseTypeDeclaration();
			String name = scanner.getCurrentContent();
			consume(Token.IDENTIFIER);
			addVariableIntoTable(isGlobal, name, scanner.getLineNumber(), dimentions);
			while(expect(Token.COMMA)) {
				consume(Token.COMMA);
				name = scanner.getCurrentContent();
				addVariableIntoTable(isGlobal, name, scanner.getLineNumber(), dimentions);
				consume(Token.IDENTIFIER);
			}
			consume(Token.SEMICOLON);
		}
	}
	
	/**
	 * funcDecl = (function | procedure) ident [formalParam] ";" funcBody ;
	 */
	private void parseFunctionDeclaration() {
		if(expect(Token.PROCEDURE) || expect(Token.FUNCTION)) {
			if(expect(Token.PROCEDURE)) {
				consume(Token.PROCEDURE);
				currentMethod = new Method(scanner.getCurrentContent(), false);
				consume(Token.IDENTIFIER);
			} else {
				consume(Token.FUNCTION);
				currentMethod = new Method(scanner.getCurrentContent(), true);
				consume(Token.IDENTIFIER);
			}
			currentProgram.addMethod(currentMethod);
			EntryBlock block = createEntryBlockForCurrentMethod();
			parseFormalParam(block);
			consume(Token.SEMICOLON);
			parseFunctionBody();
			consume(Token.SEMICOLON);
			parseFunctionDeclaration();
		}
	}
	
	
	/**
	 * When begin with a new function, create a new state for it.
	 * @return
	 */
	private EntryBlock createEntryBlockForCurrentMethod() {
		EntryBlock entryBlock = new EntryBlock(currentMethod);
		currentBlock = entryBlock;
		//SSAInstruction.createInitialValue(currentBlock);
		currentState = new State();
		return entryBlock;
	}
	
	/**
	 * formalParam = ( [ident {, ident}])
	 */
	private void parseFormalParam(EntryBlock block) {
		if(expect(Token.OPENPARENTHESES)) {
			consume(Token.OPENPARENTHESES);
			if(expect(Token.IDENTIFIER)) {
				addParameter(block, scanner.getCurrentContent(), scanner.getLineNumber());
				consume(Token.IDENTIFIER);
				while(expect(Token.COMMA)) {
					consume(Token.COMMA);
					if(expect(Token.IDENTIFIER)) {
						addParameter(block, scanner.getCurrentContent(), scanner.getLineNumber());
						consume(Token.IDENTIFIER);
					} else {
						//TODO
						//compile error message
					}
				}
			}
			consume(Token.CLOSEPARENTHESES);
		}
	}
	
	private void addParameter(EntryBlock block, String name, int lino) {
		currentMethod.addSymbol(name, lino, null); //
		ParameterSSAInstruction parameter = new ParameterSSAInstruction(currentBlock, name);
		block.addParameter(parameter);
		currentState.addVariable(name, parameter);
	}
	
	/**
	 * funcBody = {varDecl} { [stateSequence] }
	 */
	private void parseFunctionBody() {
		parseVarDeclaration(false);
		
		//TODO remove it if it is possible
		if(currentMethod.isFunction()) {
			this.addVariableIntoTable(false, "return", 0, null);
		}
		
		consume(Token.BEGIN);
		Block firstBlock = new Block(currentMethod);
		endBlockWithJump(firstBlock);
		firstBlock.setDominator(currentBlock);
		
		ExitBlock exitBlock = new ExitBlock(currentMethod);
		beginBlock(firstBlock);
		parseStatementSequence();
		endBlockWithJump(exitBlock);
		
		enterJoinBlock(exitBlock);
		consume(Token.END);
		//TODO update phils
		updateOperandsForPhi();
		endExitBlock(exitBlock);
	}
	
	/**
	 * 1) Add return instruction
	 * 2) Set dominator of the exit block
	 * @param exitBlock
	 */
	private void endExitBlock(ExitBlock exitBlock) {
		
		if(currentMethod.isFunction()) {
			SSAInstruction returnValue = currentState.getVariable("return");
			//TODO check whether the return value is valid
			if(returnValue != null) {
				new ReturnValueSSAInstruction(exitBlock, returnValue);
			} else {
				new RuntimeException("Function without return value");
			}
		} else {
			new ReturnSSAInstruction(exitBlock);
		}
		
		exitBlock.setEndState(currentState.deepCopy());
		
		if(exitBlock.getPrecdentBlockNumber() == 1) {
			exitBlock.setDominator(exitBlock.getPrecedentBlock(0));
		} else {
			exitBlock.setDominator(findCommonDominator(exitBlock.getPrecedentBlocks()));
		}
	}
	
	private Block findCommonDominator(List<Block> blocks){
		if(blocks == null || blocks.size() == 0) {
			return null;
			//throw new RuntimeException("No common dominotor for an empty block list");
		}
		
		Block dominator = blocks.get(0).getDominator();
		if(blocks.size() == 1) {
			return dominator;
		} else {
			while(dominator !=null) {
				if(dominateAll(dominator, blocks)){
					return dominator;
				}
				dominator = dominator.getDominator();
			}
		}
		
		throw new RuntimeException("No common dominotor!!!");
	}
	
	private boolean dominateAll(Block dominator, List<Block> blocks) {
		
		for(Block block : blocks) {
			if(!block.isDominater(dominator)){
				return false;
			}
		}
		
		return true;
	}
	
	private void beginBlock(Block block) {
		currentBlock = block;
		currentBlock.setBeginState(currentState.deepCopy());
	}
	
	private void endBlockWithJump(Block block) {
		//if jump to exit block is already there, we don't need to add jump again.
		if(!currentBlock.hasSubsequentBlock()) {
			new UnconditionalBranchSSAInstruction(currentBlock, block);
			currentBlock.addSubsequentBlock(block);
		}
		currentBlock.setEndState(currentState.deepCopy());
	}
	
	/**
	 * computation = "main" {varDecl} {funcDecl} {stateSequence} .
	 */
	private void parseComputation() {
		if(expect(Token.MAIN)) {
			consume(Token.MAIN);
			Method mainMethod = new Method("main", false);
			currentProgram.addMethod(mainMethod);
			currentMethod = mainMethod;
			EntryBlock entry = createEntryBlockForCurrentMethod();			
			parseVarDeclaration(true);
			
			State beforeFunction = currentState.deepCopy();
			parseFunctionDeclaration();
			
			//restore the states after function declaration
			currentMethod = mainMethod;
			currentState = beforeFunction;
			currentBlock = entry;
			Block block = new Block(currentMethod);
			endBlockWithJump(block);
			block.setDominator(entry);
			
			consume(Token.BEGIN);
			ExitBlock exitBlock = new ExitBlock(currentMethod); //it is already created and set to current method
			beginBlock(block);
			parseStatementSequence();
			endBlockWithJump(exitBlock);
			//TODO deal with phis
			enterJoinBlock(exitBlock);
			consume(Token.END);
			updateOperandsForPhi();
			endExitBlock(exitBlock);
		}	
	}
	
	private void updateOperandsForPhi() {
		for(Block block:currentMethod.getBlocks()) {
			if(block.isJoin()) {
				for(PhiSSAInstruction phi : ((JoinBlock)block).getAllPhis()) {
					phi.updateOperands();
				}
			}
		}
	}
}
