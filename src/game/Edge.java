package game;

import java.io.Serializable;

public class Edge implements Serializable {
    public boolean isWall = false; 
    public boolean blackNeighbours = false; 
    public boolean whitekNeighbours = false; 
    public int cell1;
    public int cell2;
    
    public Edge() {
        cell1 = -1;
        cell2 = -1;
    }
    public void setEnds(int c1, int c2) {
        cell1 = c1;
        cell2 = c2;
    }

    boolean valid = false;
    public void setValid(boolean value) {
        valid = value;
    }
    public boolean isValid() {
        return valid;
        //return cell1 != -1 && cell2 != -1;
    }

}
