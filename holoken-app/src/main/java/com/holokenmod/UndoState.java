package com.holokenmod;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public class UndoState {
	
	private final GridCell cell;
	private final int userValue;
	private final SortedSet<Integer> possibles;
	private final boolean batch;
	
	public UndoState(final GridCell cell,
					 final int userValue,
					 final SortedSet<Integer> Possibles,
					 final boolean batch) {
		this.cell = cell;
		this.userValue = userValue;
		this.possibles = copySet(Possibles);
		this.batch = batch;
	}
	
	public GridCell getCell() {
		return this.cell;
	}
	
	public int getUserValue() {
		return this.userValue;
	}
	
	public SortedSet<Integer> getPossibles() {
		return this.possibles;
	}
	
	public boolean isBatch() {
		return this.batch;
	}
	
	private SortedSet<Integer> copySet(final SortedSet<Integer> oldSet) {
		return Collections.synchronizedSortedSet(new TreeSet<>(oldSet));
	}
}