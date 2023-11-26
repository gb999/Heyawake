package game;

import java.io.Serializable;


public class Edge implements Serializable {
    public boolean isWall = false; 
    public boolean areBlackNeighbours = false; 
    public boolean areWhitekNeighbours = false; 
    public boolean areRommates = false;
    public boolean areNeighbours = false;
    
    public void setAreNeighbours(boolean value) {
        areNeighbours = value;
    }
    public boolean areNeighbours() {
        return areNeighbours;
    }
}
