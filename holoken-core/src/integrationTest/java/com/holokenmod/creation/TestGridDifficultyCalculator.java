package com.holokenmod.creation;

import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridSize;
import com.holokenmod.options.GameOptionsVariant;
import com.holokenmod.options.GameVariant;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestGridDifficultyCalculator {
	@Disabled
	@RepeatedTest(20)
	void testDifficulty() {
		GridCreator creator = new GridCreator(new GameVariant(
				new GridSize(9, 9),
				GameOptionsVariant.createClassic()));
		
		Grid grid = creator.createRandomizedGridWithCages();
		
		System.out.println(new GridDifficultyCalculator(grid).calculate());
	}
	
	@Disabled
	@Test
	void calculateValues() {
		List<Double> difficulties = Collections.synchronizedList(new ArrayList<>());
		
		ExecutorService pool = Executors.newFixedThreadPool(12);
		
		for (int i = 0; i < 1000; i++) {
			pool.submit(() -> {
				GridCalculator creator = new GridCalculator(new GameVariant(
						new GridSize(9, 9),
						GameOptionsVariant.createClassic()));
				Grid grid = creator.calculate();
				
				difficulties.add(new GridDifficultyCalculator(grid).calculate());
				System.out.print(".");
			});
		}
		
		try {
			pool.shutdown();
			pool.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		Collections.sort(difficulties);
		
		System.out.println(difficulties.size());
		System.out.println("50: " + difficulties.get(49));
		System.out.println("333: " + difficulties.get(332));
		System.out.println("667: " + difficulties.get(666));
		System.out.println("950: " + difficulties.get(949));
	}
}
