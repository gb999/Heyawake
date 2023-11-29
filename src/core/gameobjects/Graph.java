package core.gameobjects;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;


public class Graph implements Serializable {
    /**
     * Grid Size
     */
    public final int S = 6;
    public final int N = S * S;

    
    public ArrayList<ArrayList<Cell>> cells;
    
    /**
     * Adjacency matrix of edges. Only edges between neighbouring cells are used!
     */
    public ArrayList<ArrayList<Edge>> edges;

    public Graph() {
        initCells();
        initEdges();
    }  

    /**
     * A Pair represents two ends of an Edge. Stores the index of the cells on the ends of the Edge.  
     */
    static public class Pair {
        public int end1;
        public int end2;
        public Pair(int p1, int p2) {
            this.end1 = p1;
            this.end2 = p2;
        }
    }

    public int getCellIndex(int row, int column) {
        return row * S + column;
    }

    public int getCellIndex(Cell c) {
        return getCellIndex(c.row, c.column);
    }

    public Cell getCell(int cellIndex) {
        Point coords = cellIndexToCoordinate(cellIndex);
        return cells.get(coords.x).get(coords.y);
    }


    /**
     * @param cellIndex
     * @return cell coordinates in (row, column) format
     */
    public Point cellIndexToCoordinate(int cellIndex) {
        return new Point(cellIndex / S, cellIndex % S);
    } 

    /**
     * Executes given function on edge defined by two cell indices.
     * @param cell1 index of first end of the edge
     * @param cell2 index of second end of the edge
     * @param fn function to execute on edge
     */
    public void acceptEdge(int cell1, int cell2, Consumer<Edge> fn) {
        fn.accept(edges.get(cell1).get(cell2));
        fn.accept(edges.get(cell2).get(cell1));
    }

    /**
     * Executes given function on each cell. 
     * @param fn function to execute
     */
    public void forEachCell(Consumer<Cell> fn) {
        for(int i = 0; i < S ; i++) {
            for(int j = 0; j < S; j++) {
                fn.accept(cells.get(i).get(j));
            }
        }
    }
    
    public void forEachHorizontalEdge(BiConsumer<Edge, Pair> fn) {
        for(int i = 0; i < S; i++) {
            for(int j = 0; j < S - 1; j++) {
                // Horizontal edges
                int cell2Index = getCellIndex(j, i);
                int bottomNeighbourIndex = getCellIndex(j + 1, i);
                acceptEdge(cell2Index, bottomNeighbourIndex, e -> fn.accept(e, new Pair(cell2Index, bottomNeighbourIndex)));
            }
        }    
    }
    
    /**
     * 
     * @param fn
     */
    public void forEachVerticalEdge(BiConsumer<Edge, Pair> fn) {
        for(int i = 0; i < S; i++) {
            for(int j = 0; j < S - 1; j++) {
                // Vertical edges
                int cell1Index = getCellIndex(i, j);
                int rightNeighbourIndex = getCellIndex(i, j + 1);
                acceptEdge(cell1Index, rightNeighbourIndex, e -> fn.accept(e, new Pair(cell1Index, rightNeighbourIndex)));
            }
        }    
    }

    


    /**
     * @param fn executes this function on all edges.
     */
    public void forEachEdge(Consumer<Edge> fn) {
        forEachEdge((e, pair) -> fn.accept(e));
    }

    /**
     * Iterates over all edges and executes function on all valid edges in the graph.
     * @param fn function to be executed on the edges
     */
    public void forEachEdge(BiConsumer<Edge, Pair> fn) {
        for(int i = 0; i < S; i++) {
            for(int j = 0; j < S - 1; j++) {
                // Vertical edges
                int cell1Index = getCellIndex(i, j);
                int rightNeighbourIndex = getCellIndex(i, j + 1);
                acceptEdge(cell1Index, rightNeighbourIndex, e -> fn.accept(e, new Pair(cell1Index, rightNeighbourIndex)));
                
                // Horizontal edges
                int cell2Index = getCellIndex(j, i);
                int bottomNeighbourIndex = getCellIndex(j + 1, i);
                acceptEdge(cell2Index, bottomNeighbourIndex, e -> fn.accept(e, new Pair(cell2Index, bottomNeighbourIndex)));
            }
        }
    }


    /**
     * Executes function on each neighbour of the given cell.
     * @param cellIndex index of the given cell.
     * @param fn function to execute on each edge. It takes an edge as an argument.
     * */
    public void iterateNeighbours(int cellIndex, Consumer<Edge> fn) {
        iterateNeighbours(cellIndex, e -> fn.accept(e));
    }

    /**
     * Executes function on each neighbour of the given cell.
     * @param cellIndex index of the given cell.
     * @param fn function to execute on each edge. It takes two arguments, an Edge, and an Integer. 
     * The Integer represents the index of the neighbour in the current iteration.   
     */
    public void iterateNeighbours(int cellIndex, BiConsumer<Edge, Integer> fn) {
        int i = 0;
        for(Edge e: edges.get(cellIndex)) {
            if(e.areNeighbours)
                fn.accept(e, i);
            i++;
        }
    }

    /**
     * Performs flood fill starting from cellIndex. Executes function on each cell.
     * Flooding stops on hitting walls 
     * @param cellIndex starting cell index
     * @param fn is executed on each cell
     */
    public void floodFill(int cellIndex, Consumer<Cell> fn) {
        conditionalFloodFill(cellIndex, e -> e.isWall, fn);
    }
    
    /**
     * Performs flood fill starting from cellIndex. Executes function on each cell.
     * Flood fill stops when stopCondition is met. 
     * @param cellIndex starting cell index
     * @param stopCondition flooding stops if this condition is met
     * @param fn is executed on each cell
     * @return an array of booleans with size N, representing if cell had been explored.
     */
    public boolean[] conditionalFloodFill(int cellIndex, Predicate<Edge> stopCondition, Consumer<Cell> fn) {
        boolean[] filled = new boolean[N];
        filled[cellIndex] = true;
        fn.accept(getCell(cellIndex));
        _conditionalFloodFill(cellIndex, filled, stopCondition, fn);
        return filled;
    }

    private void _conditionalFloodFill(int cellIndex, boolean[] filled,  Predicate<Edge> stopCondition, Consumer<Cell> fn) {
        iterateNeighbours(cellIndex, (e, i)-> {
            if(stopCondition.test(e) || filled[i]) return;
                filled[i] = true;
                fn.accept(getCell(i));
                _conditionalFloodFill(i, filled, stopCondition, fn);
            });

    }

    /**
     * Returns the first cell from the graph. 
     * Which meets given condition. 
     * @param pred condition to meet
     * @return first cell meeting the condition or null if there is none.
     */
    public Cell findAny(Predicate<Cell> pred) {
        for(int i = 0; i < S; i++) {
            for(int j = 0 ; j < S; j++) {
                Cell c = cells.get(i).get(j);
                if(pred.test(c)) return c;
            }
        }
        return null;
    }

    /**
     * Initializes all edges of the graph.
     * Edges are stored in an adjacency matrix. 
     * 2 Cells are connected by an edge if they share a side. 
     * Only these edges are used by the program.
     * TODO: Switch data structure to a sparse matrix.
     */
    protected void initEdges(){
        edges = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            ArrayList<Edge> row = new ArrayList<>();
            for (int j = 0; j < N; j++) {
                row.add(new Edge());
            }
            edges.add(row);
        }
        forEachEdge((edge) -> edge.setAreNeighbours(true));        
    }   

    /**
     * Initializes cells of graph.
     */
    protected void initCells() { 
        cells = new ArrayList<>();
        for(int i = 0; i < S ; i++) {
            ArrayList<Cell> row = new ArrayList<>();
            for(int j = 0; j < S; j++) {
                row.add(new Cell(i,j));
            }
            cells.add(row);
        }
    }
}
