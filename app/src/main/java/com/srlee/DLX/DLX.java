package com.srlee.DLX;

import java.util.ArrayList;

public class DLX {
	public enum SolveType {ONE, MULTIPLE}

    private final DLXColumn root = new DLXColumn();
    private DLXColumn[] ColHdrs;
    private DLXNode[] Nodes;
    private int numnodes;
    private DLXNode lastnodeadded;
    private final ArrayList<Integer> trysolution;
    private int NumSolns;
    protected boolean isValid;
    private int prev_rowidx = -1;
    private SolveType solvetype;

    DLX()
    {
        trysolution = new ArrayList<>();
        isValid = true;
    }

    protected void Init(int nc, int nn)
    {
        ColHdrs = new DLXColumn[nc + 1];
        for (int c = 1; c <= nc; c++)
            ColHdrs[c] = new DLXColumn();

        Nodes = new DLXNode[nn + 1];
        numnodes = 0;   // None allocated

        DLXColumn prev = root;
        for (int i = 1; i <= nc; i++)
        {
            prev.SetRight(ColHdrs[i]);
            ColHdrs[i].SetLeft(prev);
            prev = ColHdrs[i];
        }
        root.SetLeft(ColHdrs[nc]);
        ColHdrs[nc].SetRight(root);
    }

    private void CoverCol(DLXColumn coverCol)
    {
        coverCol.GetRight().SetLeft(coverCol.GetLeft());
        coverCol.GetLeft().SetRight(coverCol.GetRight());

        LL2DNode i = coverCol.GetDown();
        while (i != coverCol)
        {
            LL2DNode j = i.GetRight();
            while (j != i)
            {
                j.GetDown().SetUp(j.GetUp());
                j.GetUp().SetDown(j.GetDown());
                ((DLXNode)j).GetColumn().DecSize();
                j = j.GetRight();
            }
            i = i.GetDown();
        }
    }
    private void UncoverCol(DLXColumn uncoverCol)
    {
        LL2DNode i = uncoverCol.GetUp();

        while (i != uncoverCol)
        {
            LL2DNode j = i.GetLeft();
            while (j != i)
            {
                ((DLXNode)j).GetColumn().IncSize();
                j.GetDown().SetUp(j);
                j.GetUp().SetDown(j);
                j = j.GetLeft();
            }
            i = i.GetUp();
        }

        uncoverCol.GetRight().SetLeft(uncoverCol);
        uncoverCol.GetLeft().SetRight(uncoverCol);
    }

    private DLXColumn ChooseMinCol()
    {
        int minsize = Integer.MAX_VALUE;
        DLXColumn search, mincol;

        mincol = search = (DLXColumn)root.GetRight();

        while (search != root)
        {
            if (search.GetSize() < minsize)
            {
                mincol = search;
                minsize = mincol.GetSize();
                if (minsize == 0)
                {
                    break;
                }
            }
            search = (DLXColumn)search.GetRight();
        }
        if (minsize==0)
            return null;
        else
            return mincol;
    }

    void AddNode(int colidx, int rowidx)
    {
        Nodes[++numnodes] = new DLXNode(ColHdrs[colidx], rowidx);
        if (prev_rowidx == rowidx)
        {
            Nodes[numnodes].SetLeft(lastnodeadded);
            Nodes[numnodes].SetRight(lastnodeadded.GetRight());
            lastnodeadded.SetRight(Nodes[numnodes]);
            Nodes[numnodes].GetRight().SetLeft(Nodes[numnodes]);
        }
        else
        {
        	prev_rowidx = rowidx;
            Nodes[numnodes].SetLeft(Nodes[numnodes]);
            Nodes[numnodes].SetRight(Nodes[numnodes]);
        }
        lastnodeadded = Nodes[numnodes];
    }

    public int Solve(SolveType st)
    {
        if (!isValid)
            return -1;

        solvetype = st;
        NumSolns = 0;
        search(trysolution.size());
        return NumSolns;
    }
    
    private void search(int k)
    {
        if (root.GetRight() == root)
        {
            NumSolns++;
            return;
        }
        DLXColumn chosenCol = ChooseMinCol();
        if (chosenCol != null) {
            CoverCol(chosenCol);
            LL2DNode r = chosenCol.GetDown();

            while (r != chosenCol)
            {
                if (k >= trysolution.size())
                    trysolution.add(((DLXNode)r).GetRowIdx());
                else
                    trysolution.set(k, ((DLXNode)r).GetRowIdx());
                LL2DNode j = r.GetRight();
                while (j != r)
                {
                    CoverCol(((DLXNode)j).GetColumn());
                    j = j.GetRight();
                }
                search(k + 1);
                if (solvetype == SolveType.ONE && NumSolns > 0)   // Stop as soon as we find 1 solution
                    return;
                if (solvetype == SolveType.MULTIPLE && NumSolns > 1)   // Stop as soon as we find multiple solutions
                    return;
                j = r.GetLeft();
                while (j != r)
                {
                    UncoverCol(((DLXNode)j).GetColumn());
                    j = j.GetLeft();
                }
                r = r.GetDown();
            }
            UncoverCol(chosenCol);
        }
    }
}
