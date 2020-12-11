package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

import java.util.LinkedList;

public class NonDetValue {
	
	private LinkedList<Object> nonDetValues;
	private int selector;

	public NonDetValue() {
		super();
		this.selector = 0;
		this.nonDetValues = new LinkedList<Object>();
	}

	public LinkedList<Object> getNonDetValues() {
		return nonDetValues;
	}

	public void setNondetValues(LinkedList<Object> nondetValues) {
		this.nonDetValues = nondetValues;
	}

	public void addNonDetValue(Object newValue) {
		nonDetValues.add(newValue);
	}

	@Override
	public String toString() {
		String result = "{";
		for(Object value : nonDetValues)
			result += value + ", ";
		result = result.substring(0, result.length() - 2) + "}";
		return result;
	}

	public Object getValue() {
		return nonDetValues.get(selector);
	}
	
	public void reset() {
		selector = 0;
	}

	public void next() {
		selector++;
	}
	
	public boolean hasValue() {
		return selector != nonDetValues.size();
	}
	
	public boolean hasNext() {
		return selector < nonDetValues.size() - 1;
	}
}
