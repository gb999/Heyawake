package core;

import game.Cell;
import game.Graph;

public class Game extends Core {
    public Game() {
        super();
    }
    public Game(Graph g) {
        graph = g;
    }

    @Override
    public void cellClicked(int row, int column) {
        Cell clickedCell = graph.cells.get(row).get(column);
        clickedCell.nextState();
        // TODO: Change edge relations
    }

    @Override
    public final void edgeClicked(int neighbour1Index, int neighbour2Index) {
        throw new UnsupportedOperationException("Edges should not be clicked inside game!");
    }

}
