package com.holokenmod.creation;

import com.holokenmod.RandomSingleton;

import java.util.Collections;
import java.util.List;

public class RandomPossibleDigitsShuffler implements PossibleDigitsShuffler {
	@Override
	public void shufflePossibleDigits(List<Integer> possibleDigits) {
		if (!possibleDigits.isEmpty()) {
			Collections.shuffle(possibleDigits, RandomSingleton.getInstance().getRandom());
		}
	}
}
