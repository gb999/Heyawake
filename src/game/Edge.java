package game;

import java.io.Serializable;

/**
 * Stores a single edge on the grid
 * Serializable for the saves
 */
public class Edge implements Serializable {

    /**
     * True if the edge is a wall on the grid
     */
    public boolean isWall = false;

    /**
     * True if the edge is between two cells, that are in BLACK state on the grid
     */
    public boolean areBlackNeighbours = false;

    /**
     * True if the edge is between two cells, that are in WHITE state on the grid
     */
    public boolean areWhiteNeighbours = false;

    /**
     * Its purpose is to indicate if the edge is a real edge on the grid
     * For the neighbour matrix every cell in it is filled with an edge, but not evey cell contains a real edge on the grid
     */
    public boolean areNeighbours = false;

    /**
     * Sets the validation of the edge
     *
     * @param value The value to be set
     */
    public void setAreNeighbours(boolean value) {
        areNeighbours = value;
    }

    /**
     * Is this a real edge on the grid?
     *
     * @return True if the edge is marked valid
     */
    public boolean areNeighbours() {
        return areNeighbours;
    }

    /**
     * Are both of the endpoints of the edge in BLACK state?
     *
     * @return True if both of the endpoints of the edge are in BLACK state
     */
    public boolean areBlackNeighbours() {
        return areBlackNeighbours;
    }

    /**
     * Are both of the endpoints of the edge in WHITE state?
     *
     * @return True if both of the endpoints of the edge are in WHITE state
     */
    public boolean areWhiteNeighbours() {
        return areWhiteNeighbours;
    }
}
