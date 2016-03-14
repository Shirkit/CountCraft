package com.shirkit.countcraft.api.count;

public interface IComplexCounter extends ICounter {

	public static final String COMPLEX_TAG = "complex";

	public boolean isComplex();

	public void setComplex(boolean complex);
}
