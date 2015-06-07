package edu.uci.eecs.compiler.register;

public class Location {

	private boolean isRegister;
	private int registerId;
	private int stackSlotNum;
	
	public boolean isRegister() {
		return this.isRegister;
	}
	
	public boolean isStackSlot(){
		return !this.isRegister;
	}
	
	public int getRegister() {
		return this.registerId;
	}
	
	public int getStackSlot() {
		return this.stackSlotNum;
	}
	
	public Location(boolean isRegister, int id) {
		this.isRegister = isRegister;
		if(isRegister) {
			this.registerId = id;
		} else {
			this.stackSlotNum = id;
		}
	}
	
	public boolean equals(Location location) {
		if(this.isRegister == location.isRegister) {
			if((this.isRegister && this.registerId == location.registerId) ||
					(!this.isRegister && this.stackSlotNum == location.stackSlotNum)){
				return true;
			}
		}
		return false;
	}
}
