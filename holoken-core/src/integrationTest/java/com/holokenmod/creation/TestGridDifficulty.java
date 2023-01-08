package com.holokenmod.creation;

import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridSize;
import com.holokenmod.options.GameOptionsVariant;
import com.holokenmod.options.GameVariant;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

public class TestGridDifficulty {
	@Disabled
	@RepeatedTest(20)
	void testDifficulty() {
		GridCreator creator = new GridCreator(new GameVariant(
				new GridSize(9, 9),
				GameOptionsVariant.createClassic()));
		
		Grid grid = creator.createRandomizedGridWithCages();
		
		System.out.println(new GridDifficulty(grid).calculate());
	}
	
	@Disabled
	@Test
	void calculateValues() {
		ArrayList<BigInteger> difficulties = new ArrayList<>();
		
		for (int i = 0; i < 10000; i++) {
			GridCreator creator = new GridCreator(new GameVariant(
					new GridSize(9, 9),
					GameOptionsVariant.createClassic()));
			Grid grid = creator.createRandomizedGridWithCages();
			
			difficulties.add(new GridDifficulty(grid).calculate());
			System.out.print(".");
		}
		
		Collections.sort(difficulties);
		
		System.out.println(difficulties.size());
		System.out.println("333: " + difficulties.get(3332));
		System.out.println("667: " + difficulties.get(6666));
	}
}
