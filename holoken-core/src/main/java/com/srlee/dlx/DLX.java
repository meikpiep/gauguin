package com.srlee.dlx;

import java.util.ArrayList;

public class DLX {
	private final DLXColumn root = new DLXColumn();
	private final ArrayList<Integer> trysolution = new ArrayList<>();
	private DLXColumn[] ColHdrs;
	private DLXNode[] Nodes;
	private int numnodes;
	private DLXNode lastNodeAdded;
	private int numberOfSolutions;
	private int previousRow = -1;
	private SolveType solvetype;

	protected void init(final int numberOfColumns, final int numberOfNodes) {
		ColHdrs = new DLXColumn[numberOfColumns + 1];
		for (int c = 1; c <= numberOfColumns; c++) {
			ColHdrs[c] = new DLXColumn();
		}
		
		Nodes = new DLXNode[numberOfNodes + 1];
		numnodes = 0;   // None allocated
		
		DLXColumn prev = root;
		for (int i = 1; i <= numberOfColumns; i++) {
			prev.right = ColHdrs[i];
			ColHdrs[i].left = prev;
			prev = ColHdrs[i];
		}
		root.left = ColHdrs[numberOfColumns];
		ColHdrs[numberOfColumns].right = root;
	}
	
	private void coverColumn(final DLXColumn column) {
		column.right.left = column.left;
		column.left.right = column.right;
		
		LL2DNode i = column.down;
		while (i != column) {
			LL2DNode j = i.right;
			while (j != i) {
				j.down.up = j.up;
				j.up.down = j.down;
				((DLXNode) j).getColumn().decrementSize();
				j = j.right;
			}
			i = i.down;
		}
	}
	
	private void uncoverColumn(final DLXColumn column) {
		LL2DNode i = column.up;
		
		while (i != column) {
			LL2DNode j = i.left;
			while (j != i) {
				((DLXNode) j).getColumn().incrementSize();
				j.down.up = j;
				j.up.down = j;
				j = j.left;
			}
			i = i.up;
		}
		
		column.right.left = column;
		column.left.right = column;
	}
	
	private DLXColumn ChooseMinCol() {
		int minsize = Integer.MAX_VALUE;
		DLXColumn search, mincol;
		
		mincol = search = (DLXColumn) root.right;
		
		while (search != root) {
			if (search.getSize() < minsize) {
				mincol = search;
				minsize = mincol.getSize();
				if (minsize == 0) {
					break;
				}
			}
			search = (DLXColumn) search.right;
		}
		if (minsize == 0) {
			return null;
		} else {
			return mincol;
		}
	}
	
	protected void addNode(final int column, final int row) {
		Nodes[++numnodes] = new DLXNode(ColHdrs[column], row);
		if (previousRow == row) {
			Nodes[numnodes].left = lastNodeAdded;
			Nodes[numnodes].right = lastNodeAdded.right;
			lastNodeAdded.right = Nodes[numnodes];
			Nodes[numnodes].right.left = Nodes[numnodes];
		} else {
			previousRow = row;
			Nodes[numnodes].left = Nodes[numnodes];
			Nodes[numnodes].right = Nodes[numnodes];
		}
		lastNodeAdded = Nodes[numnodes];
	}
	
	public int Solve(final SolveType st) {
		solvetype = st;
		numberOfSolutions = 0;
		
		search(trysolution.size());
		
		return numberOfSolutions;
	}
	
	private void search(final int k) {
		if (root.right == root) {
			numberOfSolutions++;
			return;
		}
		final DLXColumn chosenCol = ChooseMinCol();
		if (chosenCol != null) {
			coverColumn(chosenCol);
			LL2DNode r = chosenCol.down;
			
			while (r != chosenCol) {
				if (k >= trysolution.size()) {
					trysolution.add(((DLXNode) r).getRow());
				} else {
					trysolution.set(k, ((DLXNode) r).getRow());
				}
				LL2DNode j = r.right;
				while (j != r) {
					coverColumn(((DLXNode) j).getColumn());
					j = j.right;
				}
				search(k + 1);
				if (solvetype == SolveType.ONE && numberOfSolutions > 0)   // Stop as soon as we find 1 solution
				{
					return;
				}
				if (solvetype == SolveType.MULTIPLE && numberOfSolutions > 1)   // Stop as soon as we find multiple solutions
				{
					return;
				}
				j = r.left;
				while (j != r) {
					uncoverColumn(((DLXNode) j).getColumn());
					j = j.left;
				}
				r = r.down;
			}
			uncoverColumn(chosenCol);
		}
	}
	
	public enum SolveType {ONE, MULTIPLE}
}