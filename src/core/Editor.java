package core;


public class Editor extends Core {
    /**
     * Stores the mode of the editor
     */
    public Mode mode;
    /**
     * Stores a number
     * When the editor accepts a click for a cell, it writes this number on it
     */
    public int blackCellCount = -1;

    /**
     * The default mode for the editor is wall painting
     */
    public Editor() {
        super();
        this.mode = Mode.WALL;
    }

    /**
     * When the editor is in BLACKCELL mode, the editor writes this number to the cells blackCount variable
     *
     * @param row    The clicked cells row
     * @param column The clicked cells column
     */
    @Override
    public void cellClicked(int row, int column) {
        graph.cells.get(row).get(column).blackCount = blackCellCount;
    }

    /**
     * When the editor accepts a click on an edge in WALL mode, it sets the edges wall variable to the inverse
     *
     * @param neighbour1Index The first neighbours index in the neighbour matrix
     * @param neighbour2Index The second neighbours index in the neighbour matrix
     */
    @Override
    public void edgeClicked(int neighbour1Index, int neighbour2Index) {
        graph.acceptEdge(neighbour1Index, neighbour2Index, e -> e.isWall = !e.isWall);
    }

    /**
     * Stores the mode of the editor
     */
    public enum Mode {BLACKCELL, WALL}

}