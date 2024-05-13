import javax.swing.JFrame;

/**
 * The main class for the Snake game.
 * This class sets up the game window.
 */
public class SnakeGame extends JFrame {

    public SnakeGame() {
        initUI();
    }

    /**
     * Initializes the game window.
     */
    private void initUI() {
        add(new GameBoard()); // Adds the GameBoard to the JFrame
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null); // Centers the window on the screen
    }

    /**
     * The main method to run the game.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            JFrame ex = new SnakeGame(); // Creates an instance of SnakeGame
            ex.setVisible(true); // Makes the game window visible
        });
    }
}
