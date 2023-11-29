package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import core.Game;

public class GameCanvas extends Canvas {
    Game game;
    public GameCanvas(Game game) {
        super(game.graph);
        this.game = game;
    }

    @Override
    protected void mouseClicked(Point p) {
        if(!pointOnBoard(p)) return; 
        Point clickedCell = canvasPositionToCellCoordinate(p);
        game.cellClicked(clickedCell.x,clickedCell.y);
        repaint();
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if(game.hasEnded()) {
            // Not drawing... ??
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(255,255,255));
            g2.fillRect(0, 0, SIDELENGTH, SIDELENGTH);
        }
    }

}
