package core.gameobjects;

import java.io.Serializable;

/**
 * The Edge class represents relations between two cells.
 */
public class Edge implements Serializable {
    public boolean isWall = false; 
    public boolean areBlackNeighbours = false; 
    public boolean areWhitekNeighbours = false; 
    public boolean areNeighbours = false;
    
    public void setAreNeighbours(boolean value) {
        areNeighbours = value;
    }
    public boolean areNeighbours() {
        return areNeighbours;
    }
    public boolean areBlackNeighbours() {
        return areBlackNeighbours;
    }
    public boolean areWhiteNeighbours() {
        return areWhitekNeighbours;
    }
}
