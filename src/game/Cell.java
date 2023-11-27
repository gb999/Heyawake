package game;

import java.io.Serializable;

public class Cell implements Serializable{
    public enum State {BLACK, WHITE, UNPAINTED}
    public State state = State.UNPAINTED;
    int row;
    int column;

    public int blackCount = 0;
    Cell(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public void nextState() {
        switch (state) {
            case UNPAINTED: state = State.WHITE; break;
            case WHITE: state = State.BLACK; break;
            case BLACK: state = State.UNPAINTED; break;
        }
    }
    
    public boolean cellError = false; // Set true if cell has to be painted red
    public boolean numberError = false; // Set true if number has to be painted red

    public boolean unpainted() {
        return state == State.UNPAINTED;
    }
    public boolean white() {
        return state == State.WHITE;
    }
}
