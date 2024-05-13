import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Timer;
import java.util.Random;
import java.io.*;

import java.awt.Image;
import javax.swing.ImageIcon;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * The GameBoard class contains the game logic and rendering.
 * It extends JPanel and implements ActionListener to handle game updates.
 */

public class GameBoard extends JPanel implements ActionListener {

    private final int B_WIDTH = 1000; // Width of the game board
    private final int B_HEIGHT = 800; // Height of the game board
    private final int DOT_SIZE = 15; // Size of the snake segment and ball
    private final int ALL_DOTS = 1000*800; // Maximum number of possible snake segments
    private final int RAND_POS = 29; // Random position range for the ball
    private final int DELAY = 60; // Delay for the game timer

    private final int x[] = new int[ALL_DOTS]; // X positions of the snake
    private final int y[] = new int[ALL_DOTS]; // Y positions of the snake

    private int dots; // Current length of the snake
    private int ball_x; // X position of the ball
    private int ball_y; // Y position of the ball

    private boolean leftDirection = false; // Snake moving left
    private boolean rightDirection = true; // Snake moving right
    private boolean upDirection = false; // Snake moving up
    private boolean downDirection = false; // Snake moving down
    private boolean inGame = true; // Game state

    private int score = 0;

    private Image ball;
    private Image segment;


    private Timer timer;

    public GameBoard() {
        setDoubleBuffered(true);
        initBoard();
    }
    

    /**
     * Initializes the game board.
     */

    private void initBoard() {
        addKeyListener(new TAdapter()); // Adds key listener for snake movement
        setBackground(Color.white); // Sets the background color
        setFocusable(true); // Ensures the panel can gain focus

        setPreferredSize(new Dimension(B_WIDTH/2, B_HEIGHT/2)); // Sets the size of the game board
        loadImages(); // Load images
        initGame();
    }

    private void playSound(String soundFile) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundFile).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadImages() {
        ImageIcon ballIcon = new ImageIcon("ball.png");
        ball = ballIcon.getImage();
    
        ImageIcon segmentIcon = new ImageIcon("snake.png");
        segment = segmentIcon.getImage();
    }

    /**
     * Initializes the game state.
     */

    private void initGame() {
        dots = 4; // Initial length of the snake
    
        for (int z = 0; z < dots; z++) {
            x[z] = B_WIDTH/2 - z * DOT_SIZE; // Adjust the spacing to match DOT_SIZE
            y[z] = B_HEIGHT/2;
        }
    
        locateBall(); // Places the ball at a random position
    
        timer = new Timer(DELAY, this); // Creates a timer to control game updates
        timer.start(); // Starts the timer
    }
    

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (inGame) {
            drawObjects(g); // Draws the snake and ball if the game is ongoing
        } else {
            gameOver(g); // Displays the game over message
        }

        Toolkit.getDefaultToolkit().sync(); // Ensures the graphics are updated correctly
    }

    /**
     * Draws the snake and ball.
     * @param g The Graphics object for drawing.
     */

    private void drawObjects(Graphics g) {

        g.drawImage(ball, ball_x, ball_y, this);

        for (int z = 0; z < dots; z++) {
            g.drawImage(segment, x[z], y[z], this);
        }
    
        // Draw the score
        g.setColor(Color.black);
        g.drawString("Score: " + score, 10, 10);
    }
    

    /**
     * Displays the game over message.
     * @param g The Graphics object for drawing.
     */

    private void gameOver(Graphics g) {
        String msg = "Game Over";
        g.setColor(Color.black);
        g.drawString(msg, (B_WIDTH - getFontMetrics(g.getFont()).stringWidth(msg)) / 2, B_HEIGHT / 2);
    
        // Draw the final score
        g.drawString("Score: " + score, (B_WIDTH - getFontMetrics(g.getFont()).stringWidth("Score: " + score)) / 2, B_HEIGHT / 2 + 20);
        playSound("gameover.wav"); // Play sound on game over
    }
    

    /**
     * Checks if the snake has eaten the ball.
     */

    private void checkBall() {
        if ((x[0] == ball_x) && (y[0] == ball_y)) {
            dots++;
            score += 10; // Increment score by 10
            playSound("eat.wav"); // Play sound when ball is eaten
            locateBall();
        }
    }
    

    /**
     * Moves the snake.
     */
    private void move() {
        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)]; // Moves the position of each segment
            y[z] = y[(z - 1)];
        }

        if (leftDirection) {
            x[0] -= DOT_SIZE; // Moves the snake left
        }

        if (rightDirection) {
            x[0] += DOT_SIZE; // Moves the snake right
        }

        if (upDirection) {
            y[0] -= DOT_SIZE; // Moves the snake up
        }

        if (downDirection) {
            y[0] += DOT_SIZE; // Moves the snake down
        }
    }

    /**
     * Checks for collisions.
     */

    private void checkCollision() {
        System.out.println("X coord is: " +  x[0]);
        System.out.println("Y coord is: " + y[0]);
        
        for (int z = dots; z > 1; z--) {
            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false; // Ends the game if the snake collides with itself
            }
        }
    
        if (y[0] >= B_HEIGHT - DOT_SIZE || y[0] < 0 || x[0] >= B_WIDTH - DOT_SIZE || x[0] < 0) {
            inGame = false; // Ends the game if the snake hits the border
        }
    
        if (!inGame) {
            timer.stop(); // Stops the timer if the game is over
        }
    }
    

    /**
     * Places the ball at a random position.
     */
    private void locateBall() {
        Random random = new Random();
        ball_x = random.nextInt(RAND_POS) * DOT_SIZE; // Random X position for the ball
        ball_y = random.nextInt(RAND_POS) * DOT_SIZE; // Random Y position for the ball
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkBall(); // Checks if the snake has eaten the ball
            checkCollision(); // Checks for collisions
            move(); // Moves the snake
        }

        repaint(); // Repaints the game board
    }

    /**
     * Handles keyboard inputs for controlling the snake.
     */
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true; // Moves the snake left
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true; // Moves the snake right
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true; // Moves the snake up
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true; // Moves the snake down
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}
