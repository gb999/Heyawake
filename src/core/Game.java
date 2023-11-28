package core;



import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import game.Cell;
import game.Edge;
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
        setEdges(clickedCell);

        checkRules(clickedCell);
        
    }
    protected void setEdges(Cell clickedCell) {
        int clickedCellIndex = graph.getCellIndex(clickedCell);
        graph.iterateNeighbours(graph.getCellIndex(clickedCell), (e,i) -> {
            Cell checkedCell = graph.getCell(i);
            graph.setEdge(clickedCellIndex, i, b -> {
                b.areBlackNeighbours = (clickedCell.state == Cell.State.BLACK && checkedCell.state == Cell.State.BLACK);  
                b.areWhitekNeighbours = (clickedCell.state == Cell.State.WHITE && checkedCell.state == Cell.State.WHITE);  
            });
        });
    }
    

    protected void checkRules(Cell clickedCell) {
        // Check rules

        boolean result;
        checkBlackCountInRoom(clickedCell);
        
        checkAdjacentBlackCells();
        

        if(allPainted()) 
            result = areWhiteCellsInterconnected();
        
        checkWhiteLines();

    }

    class WhiteLineFSM {
        boolean wereWhiteNeighbours = false;
        Set<Cell> lastCells = new HashSet<>();
        int lastCell1Index; 
        int lastCell2Index; 
        Edge lastEdge = null;
        

        public void nextState(Edge e, Cell c1, Cell c2) {
            if(lastCell1Index == graph.getCellIndex(c1) && lastCell2Index == graph.getCellIndex(c2)) return;
            lastCell1Index = graph.getCellIndex(c1);
            lastCell2Index = graph.getCellIndex(c2);
            lastEdge = e;
            System.out.println(graph.getCellIndex(c1) + " " + graph.getCellIndex(c2));
            if(!e.isWall) {
                lastCells.clear();
                wereWhiteNeighbours = false;
                return;
            }

            if(wereWhiteNeighbours) {
                if(e.areWhitekNeighbours) {
                    // WHITE LINE FOUND
                    lastCells.add(c1);
                    lastCells.add(c2);
                    setError();
                    return;
                } else{
                    wereWhiteNeighbours = false;
                    lastCells.clear();
                    return;
                }
            } else {
                if(e.areWhitekNeighbours) {
                    wereWhiteNeighbours = true;
                    lastCells.add(c1); 
                    lastCells.add(c2); 
                } else {
                    lastCells.clear();
                    wereWhiteNeighbours = false;
                    return;
                    // Nothing
                }
            }
        }

        public void setError() {
            lastCells.forEach(c -> c.cellError = true);

        }
    }

    private WhiteLineFSM whiteLineFSM = new WhiteLineFSM();
    private void checkWhiteLines() {
        graph.forEachHorizontalEdge((e, p) -> {
            whiteLineFSM.nextState(e, graph.getCell(p.end1), graph.getCell(p.end2));
        });
        graph.forEachVerticalEdge((e, p) -> {
            whiteLineFSM.nextState(e, graph.getCell(p.end1), graph.getCell(p.end2));
        });
    }

    protected boolean allPainted() {
        return graph.findAny(Cell::unpainted) == null;
    }

    /**
     * Starts flood fill from any white cell.
     * Counts all white cells on board. 
     * If not all white cells had been reached by floodfill, then white cells are not interconnected.
     * @return true if all white cells are interconnected
     */
    private boolean areWhiteCellsInterconnected() {
        Cell firstWhite = graph.findAny(Cell::white);
        if(firstWhite == null) return true;
        int firstWhiteIndex = graph.getCellIndex(firstWhite);
        boolean[] filled = graph.conditionalFloodFill(firstWhiteIndex, Predicate.not(Edge::areWhitekNeighbours), (e)->{});
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


    protected class AdjacentBlackCheck {
      
        boolean isErroneous = false;

        public void setErroneous(boolean isErroneous) {
            this.isErroneous = true;
        }

        Cell prevCell = null;
        public void setPrevCell(Cell c) {
            prevCell = c;
        }
        public void setPrevCellError(boolean b) {
            if(prevCell != null)    
                prevCell.cellError = b;
        }

    }
    
    private void checkAdjacentBlackCells() {
        graph.forEachCell(cell->cell.cellError = false);

        graph.forEachEdge((edge, endpoints) -> {
            if(edge.areBlackNeighbours) {
                graph.getCell(endpoints.end1).cellError = true;
                graph.getCell(endpoints.end2).cellError = true;
            }
        });

    }

    protected class BlackCountCheck {
        int expectedBlackCount = 0;
        int currentBlackCount = 0;

        Cell counterCell;
        void increaseBlackCount() { currentBlackCount++; };
        void setExpectedBlackCount(Cell c) { expectedBlackCount = c.blackCount; counterCell = c;};

        boolean check() {
            if(expectedBlackCount == 0) 
                return true;

            return expectedBlackCount == currentBlackCount;
        }
        public void setNumberError(boolean b) {
            if(counterCell != null) counterCell.numberError = b;
        }
    } 

    protected void checkBlackCountInRoom(Cell clickedCell) {
        int cellIndex = graph.getCellIndex(clickedCell);
        BlackCountCheck roomCheck = new BlackCountCheck();
        graph.floodFill(cellIndex, cell -> {
            if(cell.state == Cell.State.BLACK) roomCheck.increaseBlackCount();;
            if(cell.blackCount != 0) roomCheck.setExpectedBlackCount(cell); 
        });
        roomCheck.setNumberError(!roomCheck.check());

    }



    @Override
    public final void edgeClicked(int neighbour1Index, int neighbour2Index) {
        throw new UnsupportedOperationException("Edges should not be clicked inside game!");
    }

}
