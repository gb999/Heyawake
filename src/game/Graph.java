package game;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Graph implements Serializable {
    /**
     * Grid Size
     */
    public final int S = 6;
    public final int N = S * S;
    public ArrayList<ArrayList<Cell>> cells;
    public ArrayList<ArrayList<Edge>> edges;

    public int getCellIndex(int row, int column) {
        return row * S + column;
    }
    public int getCellIndex(Cell c) {
        return getCellIndex(c.row, c.column);
    }

    /**
     * @param cellIndex
     * @return cell coordinates in (row, column) format
     */
    public Point cellIndexToCoordinate(int cellIndex) {
        return new Point(cellIndex / S, cellIndex % S);
    } 

    public Graph() {
        initCells();
        initEdges();
        printMatrix();
    }  

    private void initCells() { 
        cells = new ArrayList<>();
        for(int i = 0; i < S ; i++) {
            ArrayList<Cell> row = new ArrayList<>();
            for(int j = 0; j < S; j++) {
                row.add(new Cell(i,j));
            }
            cells.add(row);
        }
    }
    public void setEdge(int cell1, int cell2, Consumer<Edge> fn) {
        fn.accept(edges.get(cell1).get(cell2));
        fn.accept(edges.get(cell2).get(cell1));
    }


    /**
     * @param fn executes this function on all edges.
     */
    public void iterateEdges(Consumer<Edge> fn) {
        for(int i = 0; i < S; i++) {
            for(int j = 0; j < S - 1; j++) {
                // Vertical edges
                int cell1Index = getCellIndex(i, j);
                int rightNeighbourIndex = getCellIndex(i, j + 1);
                setEdge(cell1Index, rightNeighbourIndex, fn);
                
                // Horizontal edges
                int cell2Index = getCellIndex(j, i);
                int bottomNeighbourIndex = getCellIndex(j + 1, i);
                setEdge(cell2Index, bottomNeighbourIndex, fn);
            }
        }
    }

    private void initEdges(){
        edges = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            ArrayList<Edge> row = new ArrayList<>();
            for (int j = 0; j < N; j++) {
                row.add(new Edge());
            }
            edges.add(row);
        }
        iterateEdges((edge)->edge.setAreNeighbours(true));        
    }   

    public void printMatrix() {
        for(int i = 0; i <N; i++) {
            for (int j = 0; j <N; j++) {
                System.out.print(edges.get(i).get(j).areNeighbours() ? 1 + " " : 0 + " ");
            }
            System.out.println("");
        }
    }
}
