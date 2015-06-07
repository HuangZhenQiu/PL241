package edu.uci.eecs.compiler.frontend;

import edu.uci.eecs.compiler.model.Program;
import edu.uci.eecs.compiler.frontend.Scanner;

import edu.uci.eecs.compiler.optimization.CommonSubexpressionEliminator;
import edu.uci.eecs.compiler.optimization.ConstantEvaluator;
import edu.uci.eecs.compiler.optimization.DeadCodeEliminator;
import edu.uci.eecs.compiler.optimization.UnreachableCodeEliminator;

import java.io.FileWriter;
import java.io.IOException;

public class Compiler {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		if(args.length <= 1) {
			System.out.println("Please input file name to compile and execute!");
			System.exit(-1);
		}
		
		Program program = parseAndBuildSSA(args[0]);
		FileWriter writer = new FileWriter(args[1]);
		String graph = program.toGraph();
		System.out.println(graph);
		writer.write(graph);
		writer.close();
	}
	
	
	private static Program parseAndBuildSSA(String fileName){
		Scanner scanner = new Scanner(fileName);
		Program program = new Program();
		Parser parser = new Parser(scanner, program);
		return parser.parseProgram();
	}
	
	private static void optimize(Program program) {
		ConstantEvaluator evaluator = new ConstantEvaluator(program);
		CommonSubexpressionEliminator csEliminator = new CommonSubexpressionEliminator(program);
		DeadCodeEliminator dcEliminator = new DeadCodeEliminator(program);
		UnreachableCodeEliminator ucEliminator = new UnreachableCodeEliminator(program);
		
		
		csEliminator.optimize();
		dcEliminator.optimize();
		ucEliminator.optimize();
		
	}

}
