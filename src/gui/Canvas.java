package gui;

import game.Cell;
import game.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

public class Canvas extends JPanel {
    static final Color bgColor = new Color(100, 100, 100);
    static final Color green = new Color(0, 200, 0);
    static final Color errorRed = new Color(255, 0, 0, 150);
    static final HashMap<Cell.State, Color> colors = new HashMap<>();

    static {
        colors.put(Cell.State.UNPAINTED, bgColor);
        colors.put(Cell.State.WHITE, new Color(255, 255, 255));
        colors.put(Cell.State.BLACK, new Color(0, 0, 0, 150));
    }

    int canvasSideLength = 600;
    int cellLength;
    boolean selected = false;

    Graph graph;

    public Canvas(Graph graph) {
        setLayout(new FlowLayout());
        this.graph = graph;
        cellLength = canvasSideLength / graph.S;
        setPreferredSize(new Dimension(canvasSideLength, canvasSideLength));

        addMouseListener(new ClickListener(this));
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        canvasSideLength = (int) d.getWidth();
        cellLength = canvasSideLength / graph.S;
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        canvasSideLength = (int) preferredSize.getWidth();
        cellLength = canvasSideLength / graph.S;
    }

    /**
     * Children of this class have to override this to handle mouse clicks
     *
     * @param p
     */
    protected void mouseClicked(Point p) {
    }

    /**
     * @param p
     * @return Cell coordinate in (row, column) format
     */
    protected Point canvasPositionToCellCoordinate(Point p) {
        int row = (int) Math.floor(p.getY() / cellLength);
        int column = (int) Math.floor(p.getX() / cellLength);
        return new Point(row, column);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(bgColor);
        g2.fillRect(0, 0, canvasSideLength, canvasSideLength);
        g2.setColor(new Color(0, 0, 0));
        paintCells(g2);
        paintGrid(g2);
        paintWalls(g2);

        if (selected) {
            g2.setColor(new Color(0, 255, 0, 100));
            g2.fillRect(0, 0, canvasSideLength, canvasSideLength);
        }

        g2.dispose();
    }

    private void paintGrid(Graphics2D g2) {
        for (int i = 0; i < 5; i++) {
            int c = (i + 1) * cellLength;
            g2.drawLine(c, 0, c, canvasSideLength);
            g2.drawLine(0, c, canvasSideLength, c);
        }
    }

    private Rectangle getCellShape(int row, int column) {
        return new Rectangle(column * cellLength, row * cellLength, cellLength, cellLength);
    }

    private void paintCells(Graphics2D g2) {
        Color savedColor = g2.getColor();
        g2.setFont(g2.getFont().deriveFont((float) cellLength / 3));
        for (int i = 0; i < graph.S; i++) {
            for (int j = 0; j < graph.S; j++) {
                Cell cell = graph.cells.get(i).get(j);
                g2.setColor(colors.get(cell.state));
                g2.fill(getCellShape(i, j));


                if (cell.cellError) {
                    g2.setColor(errorRed);
                    g2.fill(getCellShape(i, j));
                }

                if (cell.numberError)
                    g2.setColor(errorRed);
                else
                    g2.setColor(green);

                if (cell.blackCount != 0) {
                    g2.drawString(Integer.toString(cell.blackCount), j * cellLength + cellLength / 2, i * cellLength + cellLength / 2);
                }
            }
        }
        g2.setColor(savedColor);
    }

    private void paintWalls(Graphics2D g2) {
        Stroke savedStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(3));
        for (int i = 0; i < graph.S; i++) {
            int S = graph.S;
            for (int j = 0; j < S - 1; j++) {
                // paint vertical edges
                if (graph.edges.get(i * S + j).get(i * S + j + 1).isWall)
                    g2.drawLine((j + 1) * cellLength, i * cellLength, (j + 1) * cellLength, i * cellLength + cellLength);

                // paint horizontal edges
                if (graph.edges.get(j * S + i).get(j * S + i + S).isWall)
                    g2.drawLine(i * cellLength, (j + 1) * cellLength, i * cellLength + cellLength, (j + 1) * cellLength);
            }
        }
        g2.setStroke(savedStroke);
    }

    class ClickListener extends MouseAdapter {
        Canvas canvas;

        ClickListener(Canvas canvas) {
            this.canvas = canvas;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            canvas.mouseClicked(new Point(e.getX(), e.getY()));
            repaint();
        }
    }
}