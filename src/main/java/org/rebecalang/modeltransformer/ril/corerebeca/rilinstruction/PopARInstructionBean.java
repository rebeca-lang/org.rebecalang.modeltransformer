package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

public class PopARInstructionBean extends InstructionBean {

	private int numberOfPops;
	private boolean breakOrContinuePOP;
	public PopARInstructionBean(int numberOfPops, boolean breakOrContinuePOP) {
		super();
		this.numberOfPops = numberOfPops;
		this.breakOrContinuePOP = breakOrContinuePOP;
	}

	public PopARInstructionBean() {
		this(1, false);
	}

	@Override
	public String toString() {
		return "POP " + getNumberOfPops() + " " + isBreakOrContinuePOP();
	}

	public int getNumberOfPops() {
		return numberOfPops;
	}

	public void setNumberOfPops(int numberOfPops) {
		this.numberOfPops = numberOfPops;
	}

	public boolean isBreakOrContinuePOP() {
		return breakOrContinuePOP;
	}

	public void setBreakOrContinuePOP(boolean breakOrContinuePOP) {
		this.breakOrContinuePOP = breakOrContinuePOP;
	}
}
