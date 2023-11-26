package core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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
        System.out.println(row + ", "+ column);
    }

    @Override
    public void edgeClicked(int neighbour1Index, int neighbour2Index) {
        Edge e1 = graph.edges.get(neighbour1Index).get(neighbour2Index);
        Edge e2 = graph.edges.get(neighbour2Index).get(neighbour1Index);
        e1.isWall = !e1.isWall;
        e2.isWall = !e2.isWall;
        System.out.println("edge between " + neighbour1Index + ", " + neighbour2Index);
    }
    
    
}