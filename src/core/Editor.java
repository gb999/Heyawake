package core;


import game.Cell;
import game.Edge;

public class Editor extends Core {
    public enum Mode {BLACKCELL, WALL}
    public Mode mode;
    public int blackCellCount = 0;
    public Editor() {
        super();
        this.mode = Mode.WALL;
    }

    @Override
    public void cellClicked(int row, int column) {
        Cell clickedCell = graph.cells.get(row).get(column);
        clickedCell.blackCount = blackCellCount;
    }

    @Override
    public void edgeClicked(int neighbour1Index, int neighbour2Index) {
        graph.setEdge(neighbour1Index, neighbour2Index, e -> e.isWall = !e.isWall);
    }

    @Override
    public void saveGraph() {
        graph.floodFill(0, Cell::nextState);
        
        super.saveGraph();
    }



}