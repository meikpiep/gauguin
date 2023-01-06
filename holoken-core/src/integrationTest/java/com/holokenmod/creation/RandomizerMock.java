package com.holokenmod.creation;

import com.holokenmod.Randomizer;

public class RandomizerMock implements Randomizer {
	@Override
	public void discard() {
		//nothing to do
	}
	
	@Override
	public int nextInt(int maximumNumber) {
		return 0;
	}
	
	@Override
	public double nextDouble() {
		return 0;
	}
}
