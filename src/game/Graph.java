package game;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;


/**
 * Stores a single graph
 * Serializable because of saves
 */
public class Graph implements Serializable {

    /**
     * Stores the number of the cells in a row or column in the grid
     */
    public final int S = 6;

    /**
     * Number of cells in the grid
     */
    public final int N = S * S;

    /**
     * Stores the cells in an arraylist
     * Cells can be identified by they column and row variable
     */
    public ArrayList<ArrayList<Cell>> cells;

    /**
     * The edges of the grid are stored in a neighbour matrix
     * Every cell in it contains an edge, but only the ones with the areNeighbours set to tue is considered a real edge
     */
    public ArrayList<ArrayList<Edge>> edges;

    /**
     * The constructor initializes the grids cells and edges
     */
    public Graph() {
        initCells();
        initEdges();
    }

    /**
     * Returns the index of a cell identified by the row and column id
     * @param row The cells y coordinate
     * @param column The cells x coordinate
     * @return The cells index in the neighbour matrix
     */
    public int getCellIndex(int row, int column) {
        return row * S + column;
    }

    /**
     * Returns the index of a cell
     * @param c The cell which ones index it wants to be determined
     * @return The cells index in the neighbour matrix
     */
    public int getCellIndex(Cell c) {
        return getCellIndex(c.row, c.column);
    }

    /**
     * Returns a cell determined by his index in the neighbour matrix
     * @param cellIndex The index of the cell in the neighbour matrix
     * @return The cell determined by the index
     */
    public Cell getCell(int cellIndex) {
        Point coords = cellIndexToCoordinate(cellIndex);
        return cells.get(coords.x).get(coords.y);
    }

    /**
     * Returns the cell coordinates in (row, column) format
     * @param cellIndex The cells index in the neighbour matrix
     * @return The cells coordinates in (row, column) format
     */
    public Point cellIndexToCoordinate(int cellIndex) {
        return new Point(cellIndex / S, cellIndex % S);
    }

    /**
     * Initializes the cells of the graph
     */
    private void initCells() {
        cells = new ArrayList<>();
        for (int i = 0; i < S; i++) {
            ArrayList<Cell> row = new ArrayList<>();
            for (int j = 0; j < S; j++) {
                row.add(new Cell(i, j));
            }
            cells.add(row);
        }
    }

    /**
     * Accepts a function on an edge identified by 2 cell indexes
     * @param cell1 The first cells index in the neighbour matrix
     * @param cell2 The second cells index in the neighbour matrix
     * @param fn The function that needs to be performed
     */
    public void acceptEdge(int cell1, int cell2, Consumer<Edge> fn) {
        fn.accept(edges.get(cell1).get(cell2));
        fn.accept(edges.get(cell2).get(cell1));
    }

    /**
     * Perform a function on all the cells in the graph
     * @param fn The function that needs to be performed
     */
    public void forEachCell(Consumer<Cell> fn) {
        for (int i = 0; i < S; i++) {
            for (int j = 0; j < S; j++) {
                fn.accept(cells.get(i).get(j));
            }
        }
    }

    /**
     * Performs a function on each horizontal edge
     * A horizontal edge is an edge that is between 2 cells that are each others bottom and top neighbours
     * @param fn The function that needs to be performed
     */
    public void forEachHorizontalEdge(BiConsumer<Edge, Pair> fn) {
        for (int i = 0; i < S; i++) {
            for (int j = 0; j < S - 1; j++) {
                int cell2Index = getCellIndex(j, i);
                int bottomNeighbourIndex = getCellIndex(j + 1, i);
                acceptEdge(cell2Index, bottomNeighbourIndex, e -> fn.accept(e, new Pair(cell2Index, bottomNeighbourIndex)));
            }
        }
    }

    /**
     * Performs a function on each horizontal edge
     * A vertical edge is an edge that is between 2 cells that are each others right and left neighbours
     * @param fn The function that needs to be performed
     */
    public void forEachVerticalEdge(BiConsumer<Edge, Pair> fn) {
        for (int i = 0; i < S; i++) {
            for (int j = 0; j < S - 1; j++) {
                int cell1Index = getCellIndex(i, j);
                int rightNeighbourIndex = getCellIndex(i, j + 1);
                acceptEdge(cell1Index, rightNeighbourIndex, e -> fn.accept(e, new Pair(cell1Index, rightNeighbourIndex)));
            }
        }
    }

    /**
     * Performs a function on all the edges in the neighbour matrix
     * To do this it performs it in 2 stages
     *  First on the vertical edges and then on the horizontal edges
     * @param fn The function that needs to be performed
     */
    public void forEachEdge(Consumer<Edge> fn) {
        for (int i = 0; i < S; i++) {
            for (int j = 0; j < S - 1; j++) {
                // Vertical edges
                int cell1Index = getCellIndex(i, j);
                int rightNeighbourIndex = getCellIndex(i, j + 1);
                acceptEdge(cell1Index, rightNeighbourIndex, fn);

                // Horizontal edges
                int cell2Index = getCellIndex(j, i);
                int bottomNeighbourIndex = getCellIndex(j + 1, i);
                acceptEdge(cell2Index, bottomNeighbourIndex, fn);
            }
        }
    }

    /**
     * Performs a function on all the edges endpoints (cells) in the neighbour matrix with 2 lambda variables
     * To do this it performs it in 2 stages
     *  First on the vertical edges and then on the horizontal edges
     * @param fn The function that needs to be performed
     */
    public void forEachEdge(BiConsumer<Edge, Pair> fn) {
        for (int i = 0; i < S; i++) {
            for (int j = 0; j < S - 1; j++) {
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
     * Iterates on every neighbour of a cell
     * @param cellIndex The index of the cell in the neighbour matrix
     * @param fn The function that needs to be performed on the neighbours
     */
    public void iterateNeighbours(int cellIndex, BiConsumer<Edge, Integer> fn) {
        int i = 0;
        for (Edge e : edges.get(cellIndex)) {
            if (e.areNeighbours)
                fn.accept(e, i);
            i++;
        }
    }

    /**
     * Initializes all the edges in the neighbour matrix
     * The valid edges will be indicated by the areNeighbours attribute of the edge
     */
    private void initEdges() {
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
     * Recursively paints cells by neighbouring
     * @param cellIndex Current cells index in the neighbour matrix
     * @param filled Array of the cells, element is true if the cell was already painted
     * @param fn Function to call on the cell
     */
    private void _floodFill(int cellIndex, boolean[] filled, Consumer<Cell> fn) {
        iterateNeighbours(cellIndex, (e, i) -> {
            if (e.isWall || filled[i]) return;
            filled[i] = true;
            fn.accept(getCell(i));
            _floodFill(i, filled, fn);
        });
    }

    /**
     * Fills cells by fn
     * @param cellIndex Index of the starting cell in the neighbour matrix
     * @param fn The function to call on the cell
     */
    public void floodFill(int cellIndex, Consumer<Cell> fn) {
        boolean[] filled = new boolean[N];
        filled[cellIndex]=true;
        fn.accept(getCell(cellIndex));
        _floodFill(cellIndex, filled, fn);
    }

    /**
     * Fills recursively whit external stopping condition
     * @param cellIndex The current cells index in the neighbouring matrix
     * @param filled Array of the cells, element is true if the cell was already painted
     * @param stopCondition External stopping condition
     * @param fn Function to call on cell
     */
    private void _conditionalFloodFill(int cellIndex, boolean[] filled, Predicate<Edge> stopCondition, Consumer<Cell> fn) {
        iterateNeighbours(cellIndex, (e, i) -> {
            if (stopCondition.test(e) || filled[i]) return;
            filled[i] = true;
            fn.accept(getCell(i));
            _conditionalFloodFill(i, filled, stopCondition, fn);
        });
    }

    /**
     * Fills cells conditionally by fn
     * @param cellIndex The starting cells index in the neighbouring matrix
     * @param stopCondition Stopping condition for the called recursion
     * @param fn Function to call on cell
     * @return Returns the array that contains the fillings
     */
    public boolean[] conditionalFloodFill(int cellIndex, Predicate<Edge> stopCondition, Consumer<Cell> fn) {
        boolean[] filled = new boolean[N];
        filled[cellIndex] = true;
        fn.accept(getCell(cellIndex));
        _conditionalFloodFill(cellIndex, filled, stopCondition, fn);
        return filled;
    }

    /**
     * Finds the first occurring instance
     * @param pred The predicate that needs fulfilled
     * @return The first cell that fulfills the predicate
     */
    public Cell findAny(Predicate<Cell> pred) {
        for (int i = 0; i < S; i++) {
            for (int j = 0; j < S; j++) {
                Cell c = cells.get(i).get(j);
                if (pred.test(c)) return c;
            }
        }
        return null;
    }

    /**
     * Stores two indexes of cells
     * Used to store endpoints of an edge
     */
    public static class Pair {
        public final int end1;
        public final int end2;

        Pair(int p1, int p2) {
            this.end1 = p1;
            this.end2 = p2;
        }
    }
}
