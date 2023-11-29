package core;

import game.Cell;
import game.Edge;
import game.Graph;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class Game extends Core {
    private final WhiteLineFSM whiteLineFSM = new WhiteLineFSM();

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
        setEdges(clickedCell);
        checkRules(clickedCell);
    }

    protected void setEdges(Cell clickedCell) {
        int clickedCellIndex = graph.getCellIndex(clickedCell);
        graph.iterateNeighbours(graph.getCellIndex(clickedCell), (e, i) -> {
            Cell checkedCell = graph.getCell(i);
            graph.acceptEdge(clickedCellIndex, i, b -> {
                b.areBlackNeighbours = (clickedCell.state == Cell.State.BLACK && checkedCell.state == Cell.State.BLACK);
                b.areWhiteNeighbours = (clickedCell.state == Cell.State.WHITE && checkedCell.state == Cell.State.WHITE);
            });
        });
    }

    protected void checkRules(Cell clickedCell) {
        checkBlackCountInRoom(clickedCell);

        checkAdjacentBlackCells();

        checkWhiteLines();

        if(allPainted()) {
            areWhiteCellsInterconnected();
            if(graph.findAny(c -> (c.cellError || c.numberError)) == null)
                endGame();
        }
    }

    public boolean ended = false;
    private void endGame() {
        ended = true;
    }

    private void checkWhiteLines() {
        graph.forEachHorizontalEdge((e, p) -> {
            whiteLineFSM.nextState(e, graph.getCell(p.end1), graph.getCell(p.end2));
            whiteLineFSM.setErrors();
        });
        graph.forEachVerticalEdge((e, p) -> {
            whiteLineFSM.nextState(e, graph.getCell(p.end1), graph.getCell(p.end2));
            whiteLineFSM.setErrors();
        });
    }

    protected boolean allPainted() {
        return graph.findAny(Cell::isUnpainted) == null;
    }

    /**
     * Starts flood fill from any white cell.
     * Counts all white cells on board.
     * If not all white cells had been reached by floodfill, then white cells are not interconnected.
     *
     * @return true if all white cells are interconnected
     */
    private boolean areWhiteCellsInterconnected() {
        Cell firstWhite = graph.findAny(Cell::white);
        if(firstWhite == null) return true;
        int firstWhiteIndex = graph.getCellIndex(firstWhite);
        boolean[] filled = graph.conditionalFloodFill(firstWhiteIndex, Predicate.not(Edge::areWhiteNeighbours), (e)->{});
        int fillCount = 0;
        for(boolean b : filled) {
            if(b) fillCount++;
        }

        int whiteCount = 0;
        for(int i = 0; i < graph.S; i++) {
            for(int j = 0; j < graph.S; j++) {
                if(graph.cells.get(i).get(j).white()) whiteCount++;
            }
        }

        return fillCount == whiteCount;
    }

    private void checkAdjacentBlackCells() {
        graph.forEachCell(cell -> cell.cellError = false);

        graph.forEachEdge((edge, endpoints) -> {
            if (edge.areBlackNeighbours) {
                graph.getCell(endpoints.end1).cellError = true;
                graph.getCell(endpoints.end2).cellError = true;
            }
        });

    }

    protected void checkBlackCountInRoom(Cell clickedCell) {
        int cellIndex = graph.getCellIndex(clickedCell);
        BlackCountCheck roomCheck = new BlackCountCheck();
        graph.floodFill(cellIndex, cell -> {
            if (cell.state == Cell.State.BLACK) roomCheck.increaseBlackCount();
            if (cell.blackCount > -1) roomCheck.setExpectedBlackCount(cell);
        });
        roomCheck.setNumberError(!roomCheck.check());

    }

    @Override
    public final void edgeClicked(int neighbour1Index, int neighbour2Index) {
        throw new UnsupportedOperationException("Edges should not be clicked inside game!");
    }

    protected static class BlackCountCheck {
        int expectedBlackCount = -1;
        int currentBlackCount = 0;

        Cell counterCell;

        void increaseBlackCount() {
            currentBlackCount++;
        }

        void setExpectedBlackCount(Cell c) {
            expectedBlackCount = c.blackCount;
            counterCell = c;
        }

        boolean check() {
            if (expectedBlackCount == -1)
                return true;

            return expectedBlackCount == currentBlackCount;
        }

        public void setNumberError(boolean b) {
            if (counterCell != null) counterCell.numberError = b;
        }
    }

    class WhiteLineFSM {
        boolean wasWhiteWall = false;
        Set<Cell> lastCells = new HashSet<>();
        int lastCell1Index;
        int lastCell2Index;
        boolean error = false;

        public void nextState(Edge e, Cell c1, Cell c2) {
            if (lastCell1Index == graph.getCellIndex(c1) && lastCell2Index == graph.getCellIndex(c2)) return;

            if (lastCell1Index != graph.getCellIndex(c1) && lastCell1Index != graph.getCellIndex(c2)
                && lastCell2Index != graph.getCellIndex(c1) && lastCell2Index != graph.getCellIndex(c2)) {
                error = false;
                wasWhiteWall = false;
                lastCells.clear();
            }

            lastCell1Index = graph.getCellIndex(c1);
            lastCell2Index = graph.getCellIndex(c2);

            if (!wasWhiteWall) {
                if (e.areWhiteNeighbours) {
                    if (e.isWall) {
                        wasWhiteWall = true;
                        addLastCells(c1,c2);
                    }
                } else {
                    addLastCells(c1,c2);
                }
            } else {
                if (e.areWhiteNeighbours) {
                    if (e.isWall) {
                        addLastCells(c1,c2);
                        error = true;
                    }
                } else {
                    error = false;
                    wasWhiteWall = false;
                    lastCells.clear();
                }
            }
        }
        public void addLastCells(Cell c1, Cell c2){
            lastCells.add(c1);
            lastCells.add(c2);
        }

        public void setErrors() {
            lastCells.forEach(c -> {
                if (error) c.cellError = true;
            });

        }
    }
}
