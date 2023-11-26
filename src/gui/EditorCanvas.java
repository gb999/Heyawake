package gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import core.Editor;

public class EditorCanvas extends Canvas {
    Editor editor;
    EditorCanvas(Editor editor) {
        super(editor.graph);
        this.editor = editor;
    }

    @Override
    protected void mouseClicked(Point p) {
        if(editor.mode == Editor.Mode.BLACKCELL) {
            Point cell = canvasPositionToCellCoordinate(p);
            editor.cellClicked(cell.x, cell.y);
        } else if(editor.mode == Editor.Mode.WALL) {
            // get Edge
            int[] clickedEdgeNeighbours = getClickedEdge(p);
            if(clickedEdgeNeighbours != null) {
                editor.edgeClicked(clickedEdgeNeighbours[0], clickedEdgeNeighbours[1]);
            }
        }
    }

    /**
     * 
     * @param clickPos
     * @return clicked cell index and closest neighbouring cell index as a 2 element array
     */
    private int[] getClickedEdge(Point clickPos) {
        // Coordinates relative to top left of Cell
        int y = clickPos.x % CELLSIZE;
        int x = clickPos.y % CELLSIZE;

        // Distances from edges
        int dtop = y;
        int dbot = CELLSIZE - y;
        int dleft = x;
        int dright = CELLSIZE - x;

        // Find min distance edge
        ArrayList<Integer> dArr = new ArrayList<>(Arrays.asList(dtop, dleft, dbot, dright));
        int minDistanceIndex = 0;
        int minDistance = dArr.get(minDistanceIndex); 

        for(int i = 0; i < 4; i++) {
            if(dArr.get(i) < minDistance) {
                minDistance = dArr.get(i);
                minDistanceIndex = i;
            }
        }

        if(minDistance > 10) return null;

        // closest neighbouring cell to click
        int offsetX = 0; 
        int offsetY = 0; 
        switch (minDistanceIndex) {
            case 0: // top
                offsetY = -1;           
            break;
            case 1: //left
                offsetX = -1;
            break;
            case 2: //bottom
                offsetY = 1;
            break;
            case 3: //right
                offsetX = 1;
            break;
        }

        
        // find edge between the clicked cell and the closest neighbour
        Point clickedCell = canvasPositionToCellCoordinate(clickPos);
        Point closestNeighbour = new Point(clickedCell.x + offsetX, clickedCell.y + offsetY);

        if(closestNeighbour.getX() < 0 || closestNeighbour.getX() >= graph.S 
        || closestNeighbour.getY() < 0 || closestNeighbour.getY() >= graph.S) return null;
        
        int clickedCellIndex = graph.getCellIndex(clickedCell.x, clickedCell.y);
        int closestNeighbourIndex = graph.getCellIndex(closestNeighbour.x, closestNeighbour.y);

        
        int[] result = {clickedCellIndex, closestNeighbourIndex};
        return result;
    }
    
}
