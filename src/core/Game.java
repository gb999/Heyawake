package core;


import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import core.gameobjects.Cell;
import core.gameobjects.Edge;
import core.gameobjects.Graph;

/**
 * Game logic
 */
public class Game extends Core {
    
    public Game() {
        super();
    }

    public Game(Graph g) {
        graph = g;
        graph.forEachCell(c->{
            if(c.blackCount > 0) c.numberError = true; // Initializes number errors
        });
    }

    /**
     * Updates happen when a cell is clicked on the board.
     */
    @Override
    public void cellClicked(int row, int column) {
        Cell clickedCell = graph.cells.get(row).get(column);
        clickedCell.nextState(); 
        setEdges(clickedCell);
        checkRules(clickedCell);
    }

    /**
     * Sets the relations between neighbouring cells based on the clicked cell state.
     * @param clickedCell
     */
    protected void setEdges(Cell clickedCell) {
        int clickedCellIndex = graph.getCellIndex(clickedCell);
        graph.iterateNeighbours(graph.getCellIndex(clickedCell), (e,i) -> {
            Cell checkedCell = graph.getCell(i);
            graph.acceptEdge(clickedCellIndex, i, b -> {
                b.areBlackNeighbours = (clickedCell.state == Cell.State.BLACK && checkedCell.state == Cell.State.BLACK);  
                b.areWhitekNeighbours = (clickedCell.state == Cell.State.WHITE && checkedCell.state == Cell.State.WHITE);  
            });
        });
    }
    
    /**
     * Checks rules. Sets errors of cells and numbers.
     * @param clickedCell
     */
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

    protected boolean ended = false;
    public boolean hasEnded() {
        return ended;
    }
    private void endGame() {
        ended = true;
    }

    /**
     * Helper class for checking if there are white lines going through 3 or more rooms. 
     * It's a Finite State Machine. 
     */
    private static class WhiteLineFSM {
        boolean wasWhiteWall = false;
        Set<Cell> lastCells = new HashSet<>();
        int lastCell1Index; 
        int lastCell2Index; 
        boolean error = false;
        Graph graph = null;
        
        WhiteLineFSM(Graph g) {
            graph = g;
        }

        /**
         * Checks if there is a white line going through 3 or more rooms.
         * Sets connected cells erroneous if there are
         * Expects edges of a row or a column to be fed in order.
         * @param e edge 
         * @param c1 one and of the edge
         * @param c2 another end of the edge
         */
        public void nextState(Edge e, Cell c1, Cell c2) {
            if(lastCell1Index == graph.getCellIndex(c1) && lastCell2Index == graph.getCellIndex(c2)) return;

            if(lastCell1Index != graph.getCellIndex(c1) && lastCell1Index != graph.getCellIndex(c2)
            && lastCell2Index != graph.getCellIndex(c1) && lastCell2Index != graph.getCellIndex(c2)){
                error = false;
                wasWhiteWall = false;
                lastCells.clear();
            } 
            lastCell1Index = graph.getCellIndex(c1);
            lastCell2Index = graph.getCellIndex(c2);

            
            if(!wasWhiteWall) {
                if(e.areWhitekNeighbours && e.isWall) {
                    wasWhiteWall = true;
                    lastCells.add(c1);
                    lastCells.add(c2);
                } else if (e.areWhitekNeighbours && !e.isWall) {
                    lastCells.add(c1);
                    lastCells.add(c2);
                } 
            } else if (wasWhiteWall){
                if(e.areWhitekNeighbours && e.isWall) {
                    lastCells.add(c1);
                    lastCells.add(c2);
                    error = true;
                } else if(e.areWhitekNeighbours) {
                    lastCells.add(c1);
                    lastCells.add(c2);
                } else if (!e.areWhitekNeighbours) {
                    error = false;
                    wasWhiteWall = false;
                    lastCells.clear();
                }
            }
        }

        /**
         * If machine is in error state stes the error for the stored line of cells.
         */
        public void setErrors() {
            lastCells.forEach(c -> {
                if(error) c.cellError =  true;
            });
        }
    }

    private WhiteLineFSM whiteLineFSM = new WhiteLineFSM(this.graph);

    /**
     * Checks if there are white lines which go through 3 or more rooms.
     * Sets cells erroneous if there are.
     */
    protected void checkWhiteLines() {
        graph.forEachHorizontalEdge((e, p) -> {
            whiteLineFSM.nextState(e, graph.getCell(p.end1), graph.getCell(p.end2));
            whiteLineFSM.setErrors();
        });
        graph.forEachVerticalEdge((e, p) -> {
            whiteLineFSM.nextState(e, graph.getCell(p.end1), graph.getCell(p.end2));
            whiteLineFSM.setErrors();
        });
    }

    /**
     * @return true if all cells are painted
     */
    protected boolean allPainted() {
        return graph.findAny(Cell::unpainted) == null;
    }

    /**
     * Checks if all white cells on the board are interconnected
     * Starts flood fill from a white cell.
     * Counts all white cells on board. 
     * If not all white cells had been reached by floodfill, then white cells are not interconnected.
     * @return true if all white cells are interconnected
     */
    private boolean areWhiteCellsInterconnected() {
        Cell firstWhite = graph.findAny(Cell::white);
        if(firstWhite == null) return true;
        int firstWhiteIndex = graph.getCellIndex(firstWhite);
        boolean[] filled = graph.conditionalFloodFill(firstWhiteIndex, Predicate.not(Edge::areWhiteNeighbours), e -> {});
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

    /**
     * Checks if there are adjacent black cells on the board. 
     * Sets them erroneous if there are.
     */
    protected void checkAdjacentBlackCells() {
        graph.forEachCell(cell->cell.cellError = false);

        graph.forEachEdge((edge, endpoints) -> {
            if(edge.areBlackNeighbours) {
                graph.getCell(endpoints.end1).cellError = true;
                graph.getCell(endpoints.end2).cellError = true;
            }
        });

    }

    /**
     * Helper class for checking number of black cells in a room. 
     */
    private static class BlackCountCheck {
        int expectedBlackCount = 0; // Number of expected black cells in the currently investigated room
        int currentBlackCount = 0; 

        Cell counterCell; // Cell which stores the expected number of black cells
        void increaseBlackCount() { currentBlackCount++; }
        void setExpectedBlackCount(Cell c) { expectedBlackCount = c.blackCount; counterCell = c;}

        boolean check() {
            if(expectedBlackCount == -1) 
                return true;

            return expectedBlackCount == currentBlackCount;
        }
        /**
         * Sets number error of the counter cell. 
         */  
        public void setNumberError(boolean b) {
            if(counterCell != null) counterCell.numberError = b;
        }
    } 

    /**
     * Checks if the number of black cells in a room. 
     * If there is too many or too few sets number error in the cell containing the expected number of black cells.
     * @param clickedCell
     */
    protected void checkBlackCountInRoom(Cell clickedCell) {
        int cellIndex = graph.getCellIndex(clickedCell);
        BlackCountCheck roomCheck = new BlackCountCheck();
        graph.floodFill(cellIndex, cell -> {
            if(cell.state == Cell.State.BLACK) roomCheck.increaseBlackCount();
            if(cell.blackCount != -1) roomCheck.setExpectedBlackCount(cell); 
        });
        roomCheck.setNumberError(!roomCheck.check());

    }

}