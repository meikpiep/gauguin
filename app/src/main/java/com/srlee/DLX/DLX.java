package com.srlee.DLX;

import java.util.ArrayList;

public class DLX extends Object
{
	public enum SolveType {ONE, MULTIPLE, ALL}

    private DLXColumn root = new DLXColumn();
    private DLXColumn[] ColHdrs;
    private DLXNode[] Nodes;
    private DLXRow[] Rows;
    private int numcols, numrows, numnodes;
    private DLXNode lastnodeadded;
    private ArrayList<Integer> trysolution;
    private ArrayList<Integer> foundsolution;
    private int NumSolns, NumAttempts;
    protected boolean isValid;
    private int prev_rowidx = -1;
    private SolveType solvetype;

    public DLX()
    {
        trysolution = new ArrayList<Integer>();
        isValid = true;
    }

    public DLX(int nc, int nr, int nn)
    {
        Init(nc, nr, nn);
    }

    protected void Init(int nc, int nr, int nn)
    {
        numcols = nc;
        ColHdrs = new DLXColumn[numcols + 1];
        for (int c = 1; c <= numcols; c++)
            ColHdrs[c] = new DLXColumn();

        Nodes = new DLXNode[nn + 1];
        numnodes = 0;   // None allocated

        Rows = new DLXRow[nr + 1];
        numrows = 0;    // None allocated

        DLXColumn prev = root;
        for (int i = 1; i <= numcols; i++)
        {
            prev.SetRight(ColHdrs[i]);
            ColHdrs[i].SetLeft(prev);
            prev = ColHdrs[i];
        }
        root.SetLeft(ColHdrs[numcols]);
        ColHdrs[numcols].SetRight(root);
    }
    public int GetRowsInSolution() { return foundsolution.size(); }
    public int GetSolutionRow(int row)
    {
    	return foundsolution.get(row - 1);
    }
    private void CoverCol(DLXColumn coverCol)
    {
        LL2DNode i, j;
        coverCol.GetRight().SetLeft(coverCol.GetLeft());
        coverCol.GetLeft().SetRight(coverCol.GetRight());

        i = coverCol.GetDown();
        while (i != coverCol)
        {
            j = i.GetRight();
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
        LL2DNode i, j;

        i = uncoverCol.GetUp();
        while (i != uncoverCol)
        {
            j = i.GetLeft();
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
        int colNum = 0;

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
            ++colNum;
        }
        if (minsize==0)
            return null;
        else
            return mincol;
    }

    public void AddNode(int colidx, int rowidx)
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
            Rows[++numrows] = new DLXRow(Nodes[numnodes]);
            Nodes[numnodes].SetLeft(Nodes[numnodes]);
            Nodes[numnodes].SetRight(Nodes[numnodes]);
        }
        lastnodeadded = Nodes[numnodes];
    }

    public boolean GivenRow(int row)
    {
        return Given(Rows[row].FirstNode);
    }

    public boolean Given(DLXNode node)
    {
        DLXNode startNode = node;
        DLXNode currNode = startNode;
        do
        {
            DLXColumn ColHdr = currNode.GetColumn();
            // Check if this is still a valid column
            if (ColHdr.GetLeft().GetRight() != ColHdr)
                return false;
            CoverCol(ColHdr);
            currNode = (DLXNode)currNode.GetRight();
        } while (currNode != startNode);
        int i = currNode.GetRowIdx();
        trysolution.add(i);
        return true;
    }

    public boolean Given(int node)
    {
        return Given(Nodes[node]);
    }

    public int Solve(SolveType st)
    {
        if (!isValid)
            return -1;

        solvetype = st;
        NumSolns = 0;
        NumAttempts = 0;
        search(trysolution.size());
        return NumSolns;
    }
    
    private void search(int k)
    {
        DLXColumn chosenCol;
        LL2DNode r, j;

        if (root.GetRight() == root)
        {
        	foundsolution = new ArrayList<Integer>(trysolution);
            NumSolns++;
            return;
        }
        chosenCol = ChooseMinCol();
        if (chosenCol != null) {
            CoverCol(chosenCol);
            r = chosenCol.GetDown();

            while (r != chosenCol)
            {
                if (k >= trysolution.size())
                    trysolution.add(((DLXNode)r).GetRowIdx());
                else
                    trysolution.set(k, ((DLXNode)r).GetRowIdx());
                NumAttempts++;
                j = r.GetRight();
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
    return;
    }
}
