package logic.gameobjects;

import java.io.Serializable;

/**
 * Represents a cell on the board. 
 */
public class Cell implements Serializable{
    public enum State {BLACK, WHITE, UNPAINTED}
    public State state = State.UNPAINTED;
    protected int row;
    public int getRow() {
        return row;
    }

    protected int column;

    public int getColumn() {
        return column;
    }

    /**
     * Expected number of black cells in the room. 
     * Only one cell should have a non negative blackCount per room. 
     * If all cells are set to -1 in a room, any number of cells can be painted black inside. 
     */
    public int blackCount = -1;
    
    Cell(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Sets 
     */
    public void nextState() {
        switch (state) {
            case UNPAINTED: state = State.WHITE; break;
            case WHITE: state = State.BLACK; break;
            case BLACK: state = State.UNPAINTED; break;
        }
    }
    
    /**
     * Set to true when cell has to be painted red. 
     */ 
    public boolean cellError = false; 
    
    /**
     * Set to true when number has to be painted red
     * Signals that room has too many or too few cells painted black.   
     */ 
    public boolean numberError = false; 


    public boolean unpainted() {
        return state == State.UNPAINTED;
    }
    public boolean white() {
        return state == State.WHITE;
    }

}
