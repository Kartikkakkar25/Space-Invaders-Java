import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int tileSize = 32;
        int rows = 16;
        int columns = 16;
        int boardWidth = tileSize * columns;
        int boardHeight = tileSize * rows;

        JFrame frame = new JFrame("Space Invaders");
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SpaceInvaders spaceinvaders = new SpaceInvaders();
        frame.add(spaceinvaders);
        frame.pack();
        spaceinvaders.requestFocus();
        frame.setVisible(true);
    }
}