package gui;

import core.Game;

import java.awt.*;

public class GameCanvas extends Canvas {
    Game game;

    public GameCanvas(Game game) {
        super(game.graph);
        this.game = game;
    }

    @Override
    protected void mouseClicked(Point p) {
        Point clickedCell = canvasPositionToCellCoordinate(p);
        game.cellClicked(clickedCell.x, clickedCell.y);
    }

}
