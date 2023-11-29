package core;

import game.Graph;

import java.io.*;
import java.util.ArrayList;

/**
 * An extendable core class for basic operations of graphs
 */
public abstract class Core {
    /**
     * The name of the tile that stores the playable fields
     */
    static final String fileName = "levels.ser";

    public Graph graph;

    /**
     * Creates a new graph to the core
     */
    protected Core() {
        graph = new Graph();
    }

    /**
     * Loads the playable fields from file
     *
     * @return The list of the playable fields
     */
    public static ArrayList<Graph> loadGraphs() {
        ArrayList<Graph> graphs = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            graphs = (ArrayList<Graph>) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading file: " + e.getMessage());
        }

        return graphs;
    }

    /**
     * Called when a cell is clicked on the canvas, when extended, class has to implement this method
     *
     * @param row    The clicked cells row
     * @param column The clicked cells column
     */
    public abstract void cellClicked(int row, int column);

    /**
     * Called when an edge is clicked on the canvas (only in wall editor mode), when extended, class has to implement this method
     *
     * @param neighbour1Index The first neigbours index in the neigbour matrix
     * @param neighbour2Index The second neigbours index in the neigbour matrix
     */
    public abstract void edgeClicked(int neighbour1Index, int neighbour2Index);

    /**
     * Saves a graph
     */
    public void saveGraph() {
        ArrayList<Graph> graphs = Core.loadGraphs(); //Loads the stored graphs
        if (graphs == null) graphs = new ArrayList<>(); //If there was none creates a new List for the graphs
        graphs.add(this.graph);
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(fileName));
            oos.writeObject(graphs); //Tries to write ot the fields
        } catch (IOException e) {
            System.err.println("Error writing the object to the file: " + e.getMessage());
        } finally {
            try {
                assert oos != null;
                oos.close();
            } catch (Exception e) {
                System.err.println("Error writing the object to the file: " + e.getMessage());
            }
        }
    }
}
