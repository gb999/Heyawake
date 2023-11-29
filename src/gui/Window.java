package gui;

import core.Core;
import core.Editor;
import core.Game;
import game.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Window extends JFrame {
    private Graph selectedGraph = null;

    public Window() {
        initWindow();
        menu();
    }

    private void initWindow() {
        setSize(1200, 900);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

    }

    private void menu() {
        getContentPane().removeAll();
        initMenu();
        revalidate();
        repaint();
    }

    private void initMenu() {
        BorderLayout layout = new BorderLayout();
        layout.setVgap(200);
        setLayout(layout);
        JPanel menuContainer = new JPanel();
        JButton levelEditorButton = new JButton("Pályaszerkesztő");
        JButton playButton = new JButton("Játék!");

        levelEditorButton.addActionListener(e -> levelEditor());
        playButton.addActionListener(e -> game());

        menuContainer.add(levelEditorButton);
        menuContainer.add(playButton);

        JPanel levelContainer = new JPanel();
        levelContainer.setLayout(new BoxLayout(levelContainer, BoxLayout.X_AXIS));
        ArrayList<Graph> graphs = Core.loadGraphs();
        ArrayList<Canvas> canvasList = new ArrayList<>();

        graphs.forEach(graph -> {
            Canvas canv = new Canvas(graph);
            canvasList.add(canv);
            levelContainer.add(canv);
            levelContainer.add(Box.createHorizontalStrut(20));

            canv.setPreferredSize(new Dimension(150, 150));
            canv.repaint();
            canv.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setSelectedGraph(canv.graph);
                    selectCanvas(canvasList, canv);

                }
            });
        });

        JScrollPane levelScroller = new JScrollPane(levelContainer);


        levelScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        levelScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        add(menuContainer, BorderLayout.NORTH);
        add(levelScroller, BorderLayout.CENTER);
    }

    private void setSelectedGraph(Graph g) {
        if (selectedGraph == g) selectedGraph = null;
        else selectedGraph = g;
    }

    private void selectCanvas(ArrayList<Canvas> canvasList, Canvas canvas) {
        boolean prevState = canvas.selected;
        canvasList.forEach(canv -> canv.selected = false);
        canvas.selected = !prevState;
        canvasList.forEach(Component::repaint);
    }


    private void game() {
        getContentPane().removeAll();
        initGame();
        revalidate();
        repaint();
    }

    private void initGame() {
        Game game = selectedGraph == null ? new Game() : new Game(selectedGraph);
        Canvas canvas = new GameCanvas(game);
        add(canvas);
    }

    private void initLevelEditor() {
        Editor editor = new Editor();
        Canvas canvas = new EditorCanvas(editor);
        JButton wallModeButton = new JButton("Fal festés");
        JButton blackCellModeButton = new JButton("Fekete ccellák számának megadása");
        JTextField blackCountField = new JTextField();

        JButton saveButton = new JButton("Mentés");
        JButton cancelButton = new JButton("Mégsem");

        JPanel buttonContainer = new JPanel(new GridLayout(0, 1));

        buttonContainer.add(wallModeButton);
        buttonContainer.add(blackCellModeButton);
        buttonContainer.add(blackCountField);
        buttonContainer.add(saveButton);
        buttonContainer.add(cancelButton);

        wallModeButton.addActionListener(e -> editor.mode = Editor.Mode.WALL);

        blackCellModeButton.addActionListener(e -> {
            String blackCountStr = blackCountField.getText();
            try {
                editor.blackCellCount = Integer.parseInt(blackCountStr);
            } catch (NumberFormatException ex) {
                System.out.println("wrong number format");
            }
            editor.mode = Editor.Mode.BLACKCELL;
        });

        saveButton.addActionListener(e -> {
            editor.saveGraph();
            menu();
        });
        cancelButton.addActionListener(e -> menu());


        setLayout(new BorderLayout());
        add(canvas, BorderLayout.WEST);
        add(buttonContainer, BorderLayout.EAST);
    }

    private void levelEditor() {
        getContentPane().removeAll();
        initLevelEditor();
        revalidate();
        repaint();

    }
}