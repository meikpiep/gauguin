package com.srlee.dlx;

import java.util.ArrayList;

public class DLX {
	private final DLXColumn root = new DLXColumn();
	private final ArrayList<Integer> trysolution = new ArrayList<>();
	private DLXColumn[] ColHdrs;
	private DLXNode[] Nodes;
	private int numnodes;
	private DLXNode lastnodeadded;
	private int NumSolns;
	private int prev_rowidx = -1;
	private SolveType solvetype;

	protected void Init(final int nc, final int nn) {
		ColHdrs = new DLXColumn[nc + 1];
		for (int c = 1; c <= nc; c++) {
			ColHdrs[c] = new DLXColumn();
		}
		
		Nodes = new DLXNode[nn + 1];
		numnodes = 0;   // None allocated
		
		DLXColumn prev = root;
		for (int i = 1; i <= nc; i++) {
			prev.R = ColHdrs[i];
			ColHdrs[i].L = prev;
			prev = ColHdrs[i];
		}
		root.L = ColHdrs[nc];
		ColHdrs[nc].R = root;
	}
	
	private void CoverCol(final DLXColumn coverCol) {
		coverCol.R.L = coverCol.L;
		coverCol.L.R = coverCol.R;
		
		LL2DNode i = coverCol.D;
		while (i != coverCol) {
			LL2DNode j = i.R;
			while (j != i) {
				j.D.U = j.U;
				j.U.D = j.D;
				((DLXNode) j).GetColumn().DecSize();
				j = j.R;
			}
			i = i.D;
		}
	}
	
	private void UncoverCol(final DLXColumn uncoverCol) {
		LL2DNode i = uncoverCol.U;
		
		while (i != uncoverCol) {
			LL2DNode j = i.L;
			while (j != i) {
				((DLXNode) j).GetColumn().IncSize();
				j.D.U = j;
				j.U.D = j;
				j = j.L;
			}
			i = i.U;
		}
		
		uncoverCol.R.L = uncoverCol;
		uncoverCol.L.R = uncoverCol;
	}
	
	private DLXColumn ChooseMinCol() {
		int minsize = Integer.MAX_VALUE;
		DLXColumn search, mincol;
		
		mincol = search = (DLXColumn) root.R;
		
		while (search != root) {
			if (search.GetSize() < minsize) {
				mincol = search;
				minsize = mincol.GetSize();
				if (minsize == 0) {
					break;
				}
			}
			search = (DLXColumn) search.R;
		}
		if (minsize == 0) {
			return null;
		} else {
			return mincol;
		}
	}
	
	void AddNode(final int colidx, final int rowidx) {
		Nodes[++numnodes] = new DLXNode(ColHdrs[colidx], rowidx);
		if (prev_rowidx == rowidx) {
			Nodes[numnodes].L = lastnodeadded;
			Nodes[numnodes].R = lastnodeadded.R;
			lastnodeadded.R = Nodes[numnodes];
			Nodes[numnodes].R.L = Nodes[numnodes];
		} else {
			prev_rowidx = rowidx;
			Nodes[numnodes].L = Nodes[numnodes];
			Nodes[numnodes].R = Nodes[numnodes];
		}
		lastnodeadded = Nodes[numnodes];
	}
	
	public int Solve(final SolveType st) {
		solvetype = st;
		NumSolns = 0;
		search(trysolution.size());
		return NumSolns;
	}
	
	private void search(final int k) {
		if (root.R == root) {
			NumSolns++;
			return;
		}
		final DLXColumn chosenCol = ChooseMinCol();
		if (chosenCol != null) {
			CoverCol(chosenCol);
			LL2DNode r = chosenCol.D;
			
			while (r != chosenCol) {
				if (k >= trysolution.size()) {
					trysolution.add(((DLXNode) r).GetRowIdx());
				} else {
					trysolution.set(k, ((DLXNode) r).GetRowIdx());
				}
				LL2DNode j = r.R;
				while (j != r) {
					CoverCol(((DLXNode) j).GetColumn());
					j = j.R;
				}
				search(k + 1);
				if (solvetype == SolveType.ONE && NumSolns > 0)   // Stop as soon as we find 1 solution
				{
					return;
				}
				if (solvetype == SolveType.MULTIPLE && NumSolns > 1)   // Stop as soon as we find multiple solutions
				{
					return;
				}
				j = r.L;
				while (j != r) {
					UncoverCol(((DLXNode) j).GetColumn());
					j = j.L;
				}
				r = r.D;
			}
			UncoverCol(chosenCol);
		}
	}
	
	public enum SolveType {ONE, MULTIPLE}
}