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
     * 
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

    private void initCells(){ 
        cells = new ArrayList<>();
        for(int i = 0; i < S ; i++) {
            ArrayList<Cell> row = new ArrayList<>();
            for(int j = 0; j < S; j++) {
                row.add(new Cell(i,j));
            }
            cells.add(row);
        }
    }

    /**
     * @param fn executes this function on all edges.
     */
    public void iterateEdges(Consumer<Edge> fn) {
        for(int i = 0; i < S; i++) {
            for(int j = 0; j < S - 1; j++) {
                // Vertical edges
                // Edge between (this) cell and cell on the right side of it 
                // i * S + j: index of this cell
                // i * S + j + 1: index of cell to the right 
                fn.accept(edges.get(i * S + j).get(i * S + j + 1));
                fn.accept(edges.get(i * S + j + 1).get(i * S + j));
                
                
                // Horizontal edges
                // j * S + i: index of a cell
                // j * S + i + S: index of a cell below
                fn.accept(edges.get(j * S + i).get(j * S + i + S)); 
                fn.accept(edges.get(j * S + i + S).get(j * S + i));
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
        iterateEdges((edge)->edge.setValid(true));        
    }   


    public void printMatrix() {
        for(int i = 0; i <N; i++) {
            for (int j = 0; j <N; j++) {
                System.out.print(edges.get(i).get(j).isValid() ? 1 + " " : 0+ " ");
            }
            System.out.println("");
        }
    }
    
}
