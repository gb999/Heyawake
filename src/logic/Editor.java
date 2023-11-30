package logic;


import logic.gameobjects.Cell;
import logic.gameobjects.Graph;

/**
 * Class for editing boards. Doesn't check validity of boards.
 */
public class Editor extends Core {
    public enum Mode {BLACKCELL, WALL}
    public Mode mode;
    public int blackCellCount = -1;
    public Editor() {
        super();
        this.mode = Mode.WALL;
    }
    public Editor(Graph g) {
        super();
        graph = g;
        this.mode = Mode.WALL;
    }

    @Override
    public void cellClicked(int row, int column) {
        Cell clickedCell = graph.cells.get(row).get(column);
        clickedCell.blackCount = blackCellCount;

    }

    @Override
    public void edgeClicked(int neighbour1Index, int neighbour2Index) {
        graph.acceptEdge(neighbour1Index, neighbour2Index, e -> e.isWall = !e.isWall);
    }

    @Override
    public void saveGraph() {
        super.saveGraph();
    }
}