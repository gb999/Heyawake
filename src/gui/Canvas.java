package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.JPanel;

import core.gameobjects.Cell;
import core.gameobjects.Graph;

/**
 * Class for drawing the contents of a board on the screen.
 */
public  class Canvas extends JPanel {
    int SIDELENGTH = 600;
    int CELLSIZE = SIDELENGTH / 6;
    static final Color bgColor = new Color(100,100,100); 
    static final Color green = new Color(0,200,0); 
    static final Color errorRed = new Color(255, 0,0, 150); 

    static final HashMap<Cell.State, Color> colors = new HashMap<>(); 
    static {
        colors.put(Cell.State.UNPAINTED, bgColor);
        colors.put(Cell.State.WHITE, new Color(255,255,255));
        colors.put(Cell.State.BLACK, new Color(0,0,0,150));
    }


    boolean selected = false;

    Graph graph;
    public Canvas(Graph graph) {
        setLayout(new FlowLayout());
        this.graph = graph;
        CELLSIZE = SIDELENGTH / graph.S;
        setPreferredSize(new Dimension(SIDELENGTH, SIDELENGTH));

        addMouseListener(new ClickListener(this));
    }
    class ClickListener extends MouseAdapter {
        Canvas canvas;
        ClickListener(Canvas canvas) {
            this.canvas = canvas;
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            canvas.mouseClicked(new Point(e.getX(),e.getY()));
            repaint();
        }
    }
    
    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        SIDELENGTH = (int)d.getWidth();
        CELLSIZE = SIDELENGTH / graph.S;
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        SIDELENGTH = (int)preferredSize.getWidth();
        CELLSIZE = SIDELENGTH / graph.S;
    }

    /**
     * Children of this class have to override this to handle mouse clicks
     * @param p
     */
    protected void mouseClicked(Point p) {}; 

    /**
     * @param p point on board
     * @return true if point is on the board
     */
    protected boolean pointOnBoard(Point p) {
        return p.x >= 0 && p.x < SIDELENGTH  
            && p.y >= 0 && p.y < SIDELENGTH;
    }

    /**
     * Converts position on the canvas to a cell coordinate
     * @param p point on the canvas
     * @return Cell coordinate in (row, column) format
     */
    protected Point canvasPositionToCellCoordinate(Point p) {
        int row = (int)Math.floor(p.getY() / CELLSIZE);
        int column = (int)Math.floor(p.getX() / CELLSIZE);
        return new Point(row,column);
    } 

    @Override
    public void paint(Graphics g) {
        //this.setSize(SIDELENGTH,SIDELENGTH); // Must set size here
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(bgColor);
        g2.fillRect(0,0, SIDELENGTH, SIDELENGTH);
        g2.setColor(new Color(0,0,0));
        paintCells(g2);
        paintGrid(g2);
        paintWalls(g2);

        if(selected) {
            g2.setColor(new Color(0,255,0, 100));
            g2.fillRect(0,0, SIDELENGTH, SIDELENGTH);
        } 

        g2.dispose();
    }

    protected void paintGrid(Graphics2D g2) {
        for(int i = 0; i < 5; i++) {
            int c = (i+1) * CELLSIZE;
            g2.drawLine(c,0,c,SIDELENGTH);
            g2.drawLine(0, c,SIDELENGTH,c);
        }
    }

    protected Rectangle getCellShape(int row, int column) {
        return new Rectangle(column * CELLSIZE, row * CELLSIZE, CELLSIZE, CELLSIZE);
    }

    protected void paintCells(Graphics2D g2) {
        Color savedColor = g2.getColor();
        g2.setFont(g2.getFont().deriveFont((float)CELLSIZE/3));

        graph.forEachCell(cell -> {
            g2.setColor(colors.get(cell.state));
                int i = cell.getRow();
                int j = cell.getColumn();
                g2.fill(getCellShape(i, j));

                
                if(cell.cellError) {
                    g2.setColor(errorRed);
                    g2.fill(getCellShape(i, j));
                }

                if(cell.numberError) 
                    g2.setColor(errorRed);
                else 
                    g2.setColor(green);

                if(cell.blackCount != -1) {
                    g2.drawString(Integer.toString(cell.blackCount), j * CELLSIZE + CELLSIZE / 2, i * CELLSIZE + CELLSIZE / 2);
                }
        });

        g2.setColor(savedColor);
    }

    protected void paintWalls(Graphics2D g2) {
        Stroke savedStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(3));

        // It is easier to draw with for loops than with iterateEdges
        for(int i = 0; i < graph.S; i++) {
            int S = graph.S;
            for(int j = 0; j < S - 1; j++) {
                // paint vertical edges
                if(graph.edges.get(i * S + j).get(i * S + j + 1).isWall)
                    g2.drawLine((j+1) * CELLSIZE, i * CELLSIZE, (j+1) * CELLSIZE, i * CELLSIZE + CELLSIZE);
                    
                // paint horizontal edges
                if(graph.edges.get(j * S + i).get(j * S + i + S).isWall) 
                    g2.drawLine(i * CELLSIZE, (j+1) * CELLSIZE, i * CELLSIZE + CELLSIZE, (j+1) * CELLSIZE);
            }
        }
        g2.setStroke(savedStroke);
    }
}