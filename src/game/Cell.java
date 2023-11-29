package game;

import java.io.Serializable;

/**
 * Stores the data of a single cell
 * It is Serializable for the saves
 */
public class Cell implements Serializable {
    /**
     * Stores the state of the cell
     * Default is UNPAINTED
     * The color of the cell can be made out of it
     */
    public State state = State.UNPAINTED;

    /**
     * Stores that how much cell must be painted BLACK in the same room as the cell is in
     */
    public int blackCount = 0;

    /**
     * When the game detect a fault in the solving of the graph, the variable is set true and must be painted with red
     */
    public boolean cellError = false;

    /**
     * When there is too many BLACK cells in a room, it is set to true, and must be painted red
     */
    public boolean numberError = false;

    /**
     * Stores the y coordinate of the cell on the grid
     */
    int row;

    /**
     * Stores the x coordinate of the cell on the grid
     */
    int column;

    /**
     * A cell must contain the coordinates on the grid, without it has none representation value
     *
     * @param row    the y coordinate of the cell on the grid
     * @param column the x coordinate of the cell on the grid
     */
    Cell(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * The next state logic of the color of the cell
     * Only in play mode
     * When the cell is clicked, it sets the color to the next state
     */
    public void nextState() {
        switch (state) {
            case UNPAINTED:
                state = State.WHITE;
                break;
            case WHITE:
                state = State.BLACK;
                break;
            case BLACK:
                state = State.UNPAINTED;
                break;
        }
    }

    /**
     * Is the cell UNPAINTED?
     *
     * @return True if the cell is UNPAINTED
     */
    public boolean isUnpainted() {
        return state == State.UNPAINTED;
    }

    /**
     * Is the cell WHITE?
     *
     * @return True if the cell is WHITE
     */
    public boolean white() {
        return state == State.WHITE;
    }

    /**
     * Stores the state of a cell
     * A color can be associated with every state
     */
    public enum State {BLACK, WHITE, UNPAINTED}
}
