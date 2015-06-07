package edu.uci.eecs.compiler.representation;

import edu.uci.eecs.compiler.util.SymbolType;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class SymbolTable {
	
	static public class TableRecord{
		public int lineNumber;
		public String name;
		public int version; //used for ssa assignment
		public SymbolType type;
		
		public TableRecord(String name, int lineNumber, SymbolType type) {
			this.name = name;
			this.lineNumber = lineNumber;
			this.type = type;
		}	
		
		public SymbolType getType() {
			return this.type;
		}
	}
	
	private HashMap<String, TableRecord> records;
	private HashMap<String, List<Integer>> dimensions;
	
	public SymbolTable() {
		this.records = new HashMap<String, TableRecord>();
		this.dimensions = new HashMap<String, List<Integer>>();
	}
	
	public Set<String> getSymbols() {
		return this.records.keySet();
	}
	
	public boolean isDeclared(String name) {
		return records.containsKey(name);
	}
	
	public SymbolType getType(String name) {
		
		assert isDeclared(name);
		return records.get(name).type;
	}
	
	public List<Integer> getDimension(String name) {
		
		assert getType(name) == SymbolType.ARRAY;
		return this.dimensions.get(name);
	}
	
	public void insertArray(String name, int lineNumber, List<Integer> dimentions) {
		
		TableRecord record = new TableRecord(name, lineNumber, SymbolType.ARRAY);
		this.records.put(name, record);
		this.dimensions.put(name, dimentions);
	}
	
	public void insertFunction(String name, int lineNumber) {
		
		TableRecord record = new TableRecord(name, lineNumber, SymbolType.FUNCTION);
		this.records.put(name, record);
	}
	
	
	public void insertVariable(String name, int lineNumber) {
		
		TableRecord record =  new TableRecord(name, lineNumber, SymbolType.INTEGER);
		this.records.put(name, record);
	}
	
	
	public List<String> getVariables() {
		List<String> variables = new ArrayList<String>();
		for(Entry<String, TableRecord> entry: records.entrySet()){
			if(entry.getValue().getType().equals(SymbolType.INTEGER)) {
				variables.add(entry.getKey());
			}
		}
		
		return variables;
	}
	
	public List<String> getArrays() {
		List<String> arrayNames = new ArrayList<String>();
		for(String name : dimensions.keySet()) {
			arrayNames.add(name);
		}
		return arrayNames;
	}
	
	public int getArraySize(String name) {
		int size = 0;
		List<Integer> dimention = dimensions.get(name);
		if(dimention != null && !dimention.isEmpty()) {
			size = dimention.get(0);
			for(int i = 1; i< dimention.size(); i++) {
				size *= dimention.get(i);
			}
		}
		return size;
	}

}
