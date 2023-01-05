package com.holokenmod;

public interface Randomizer {
	void discard();
	
	int nextInt(int maximumNumber);
	
	double nextDouble();
}
