package logic;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import logic.gameobjects.Graph;

/**
 * Shared logic of the Game and Editor
 */
public abstract class Core {
    protected Graph graph;
    public Graph getGraph() {
        return graph;
    }

    protected Core() {
        graph = new Graph();
    }

    /**
     * Called when a cell is clicked on the cnavas
     * @param row
     * @param column
     */
    public abstract void cellClicked(int row, int column); 

    /**
     * Called when an edge is clicked on the canvas (only in editor wall mode)
     * @param edgeIndex
     */
    public void edgeClicked(int neighbour1Index, int neighbour2Index) {}

    static final String FILENAME = "levels.ser";
    public void saveGraph() {
        List<Graph> graphs = Core.loadGraphs();
        if(graphs == null) graphs = new ArrayList<>();
        graphs.add(this.graph);
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(FILENAME));
            // Write the object to the file
            oos.writeObject(graphs);
        } catch (IOException e) {
            System.err.println("Error writing the object to the file: " + e.getMessage());
        } finally {
            try {
                oos.close();
            } catch(Exception e) {

            }
        }
    }
    
    public static List<Graph> loadGraphs() {
        ArrayList<Graph> graphs = new ArrayList<>();
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(FILENAME));
            graphs = (ArrayList<Graph>)ois.readObject();

        } catch (IOException e) {
            System.err.println("Error loading file: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading file: " + e.getMessage());
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
        }
        
        return graphs;
    }
}
