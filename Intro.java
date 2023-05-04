/*
 * Intro.java
 * Mona Liu
 * 
 * Intro screen that doubles as the "win" screen depending on whether you set the 
 * constructor boolean to true (regular/intro) or false (win/end). Has buttons to change
 * screens or exit.
 */

import java.awt.event.*;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.Timer;
import java.util.Scanner;
import java.io.*;

class Intro extends JPanel implements MouseMotionListener, MouseListener, ActionListener {
    private int mouseX, mouseY;

    // Background image, text, corner picture
    private Image bg, logo, decoration;

    // Position of logo and buttons
    private final int LOGO_X, LOGO_Y, BUTTON_X, BUTTON_Y, BUTTON_GAP, DECO_X, DECO_Y, SCORE_X, SCORE_Y;

    // array for buttons
    private Button[] buttons;

    // Boolean for game starting (eerything has been initialized) and timer
    private boolean gameStart;
    private Timer timer;

    // Array for highest scores and lastest
    private int[] highScores;
    private int score;

    private Solitaire mainScreen;

    // Boolean for whether it's home screen or not
    private boolean isHomeScreen;

    // Change type of intro screen depending on home or end screen
    public Intro(boolean homeScreen, Solitaire main) {
        mainScreen = main;
        isHomeScreen = homeScreen;

        gameStart = false;

        // Set locations of things
        BUTTON_X = 360;
        BUTTON_Y = 315;
        BUTTON_GAP = 70;

        SCORE_X = 120;
        SCORE_Y = 360;

        DECO_X = 500;
        DECO_Y = 420;

        // Load images
        bg = new ImageIcon("rsc/img/background_2.png").getImage().getScaledInstance(800, 700, Image.SCALE_SMOOTH);
        decoration = new ImageIcon("rsc/img/decoration.png").getImage().getScaledInstance(270, 238, Image.SCALE_SMOOTH);

        // Load high score
        highScores = new int[3];
        loadScore(highScores, score);

        if (isHomeScreen) {
            // Original image is 734 by 117
            logo = new ImageIcon("rsc/img/logo.png").getImage();

            LOGO_X = 25;
            LOGO_Y = 60;

            // Make buttons and add to array
            makeButtons("DRAW 1 GAME", "DRAW 3 GAME", "EXIT");
        } else {
            // // Original image 643 x 143
            logo = new ImageIcon("rsc/img/win.png").getImage().getScaledInstance(535, 119, Image.SCALE_SMOOTH);
            LOGO_X = 120;
            LOGO_Y = 50;
            makeButtons("HOME", "EXIT");
        }

        // Add listeners
        addMouseListener(this);
        addMouseMotionListener(this);

        // Start timer
        timer = new Timer(20, this);
        timer.start();

        gameStart = true;
    }

    /* Make button arrays */
    private void makeButtons(String... names) {
        // Make array lengths depeneding on how many arguments
        buttons = new Button[names.length];

        // Add buttons using text and make all hover false
        for (int i = 0; i < names.length; i++) {
            buttons[i] = new Button(names[i], new Font("Verdana", Font.BOLD, 40),
                    Color.WHITE, Color.GRAY, BUTTON_X, BUTTON_Y + BUTTON_GAP * i);
        }
    }

    /* Check whether mouse i hovering on any button */
    private int checkMouseOnButton() {
        // loop through rectangle array
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].getRect().contains(mouseX, mouseY)) {
                return i;
            }
        }
        return -1;
    }

    /* Click buttons results depending on screen */
    private void pressButton() {
        // Home screen
        if (isHomeScreen) {
            // click draw 1
            if (checkMouseOnButton() == 0) {
                mainScreen.newGame(1);
                return;
            }

            // Click draw 3
            else if (checkMouseOnButton() == 1) {
                mainScreen.newGame(3);
                return;
            }

            // Exit
            else if (checkMouseOnButton() == 2) {
                mainScreen.dispose();
                System.exit(0);
            }
        }

        // End screen

        // Click home
        if (checkMouseOnButton() == 0) {
            mainScreen.start();
            return;
        }

        // Exit
        if (checkMouseOnButton() == 1) {
            mainScreen.dispose();
            System.exit(0);
        }
    }

    /*
     * Reads score file and updates array and latest score
     */
    private void loadScore(int[] top, int latest) {
        try {
            Scanner reader = new Scanner(new FileReader("rsc/score.txt"));
            // Read Top scores
            for (int i = 0; i < 3; i++) {
                highScores[i] = reader.nextInt();
            }
            // Latest score
            score = reader.nextInt();

            reader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Error: score could not be loaded");
        }
    }

    /* Draws top 3 scores and score of the finished game if applicable */
    private void drawScores(Graphics g) {
        g.setColor(Color.WHITE);

        // Draw high scores
        g.setFont(new Font("Verdana", Font.BOLD, 30));
        g.drawString("Top Scores", SCORE_X, SCORE_Y - 50);
        g.setFont(new Font("Verdana", Font.PLAIN, 30));
        for (int i = 0; i < 3; i++) {
            g.drawString(i + 1 + ". " + highScores[i], SCORE_X, SCORE_Y + i * 40);
        }

        // Draw current score only if it's the end screen
        if (!isHomeScreen) {
            g.setFont(new Font("Verdana", Font.BOLD, 50));
            g.drawString("Score: ", LOGO_X + 125, LOGO_Y + 175);
            g.setFont(new Font("Verdana", Font.PLAIN, 50));
            g.drawString("" + score, LOGO_X + 320, LOGO_Y + 175);

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    /*
     * Get mouse coordinates
     */
    private void updateMouse(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        updateMouse(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        updateMouse(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        updateMouse(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        updateMouse(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        updateMouse(e);
        // Check for right click
        if (e.getButton() == 1) {
            pressButton();
        }
    }

    @Override
    public void paint(Graphics g) {
        if (gameStart) {
            g.drawImage(bg, 0, 0, null);
            g.drawImage(decoration, DECO_X, DECO_Y, null);

            g.drawImage(logo, LOGO_X, LOGO_Y, null);

            for (int i = 0; i < buttons.length; i++) {
                buttons[i].draw(g, checkMouseOnButton() == i);
            }

            drawScores(g);
        }
    }

}
