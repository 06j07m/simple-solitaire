/*
 * Card.java
 * Mona Liu
 * 
 * Card object for solitaire
 * Has properties: suit and number, image icon, whether it's face up,
 * whether it's red. 
 * Has methods to draw the card, flip it, and get the data
*/

import java.awt.Image;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import java.util.Random;

public class Card {
    // width and height of a card (same for all cards)
    public static final int HEIGHT = 72, WIDTH = 50;

    // Back of card and blank placeholder image (same for all cards)
    private static Image cardBack, noCard;

    // Suit of card (1 - Spades, 2 - Hearts, 3 - Diamonds, 4 - Clubs)
    // Number of card (1 - Ace, 2 to 10, 11 - Jack, 12 - Queen, 13 - King)
    private int suit, number;

    // Card is face up and red (if it's not red its black)
    private boolean faceUp, isRed;

    // Image of card (based on suit/number)
    private Image icon;

    // New random number generator
    private Random rand;

    /*
     * CONSTRUCTOR
     * Takes a 2d array of possible values (boolean) and chooses
     * a random one out of them
     */
    public Card(boolean[][] cardBank) {

        // Make new random number generator
        rand = new Random();

        // Generate random suit and number
        int randSuit = rand.nextInt(1, 5);
        int randNum = rand.nextInt(1, 14);

        // If card is already dealt, generate another
        while (cardBank[randSuit][randNum] == false) {
            randSuit = rand.nextInt(1, 5);
            randNum = rand.nextInt(1, 14);
        }

        suit = randSuit;
        number = randNum;

        // Set isred to true if the card is hearts or diamonds, and false if it's spades
        // or clubs
        isRed = randSuit == 2 || randSuit == 3 ? true : false;

        // Starts face down
        faceUp = false;

        // Get image based on data and scale it
        String iconFile = "rsc/img/cards/" + Integer.toString(number) + "_" + Integer.toString(suit) + ".png";
        icon = new ImageIcon(iconFile).getImage().getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);

        // Get other images
        noCard = new ImageIcon("rsc/img/cards/card_blank.png").getImage().getScaledInstance(WIDTH, HEIGHT,
                Image.SCALE_SMOOTH);
        cardBack = new ImageIcon("rsc/img/cards/card_back.png").getImage().getScaledInstance(WIDTH, HEIGHT,
                Image.SCALE_SMOOTH);
    }

    /* Return suit */
    public int getSuit() {
        return suit;
    }

    /* Return number */
    public int getNumber() {
        return number;
    }

    /* Return boolean represengint whether it's face up */
    public boolean isFaceUp() {
        return faceUp;
    }

    /* Return whetehr card suit is red (true) or black(false) */
    public boolean isRed() {
        return isRed;
    }

    /* Change faceUp and faceDown */
    public void flip() {
        faceUp = !faceUp;
    }

    /* Draw placeholder */
    public static void drawBlank(Graphics g, int x, int y) {
        g.drawImage(noCard, x, y, null);
    }

    /* Draw image */
    public void draw(Graphics g, int x, int y) {
        if (faceUp) {
            g.drawImage(icon, x, y, null);
        } else {
            g.drawImage(cardBack, x, y, null);
        }
    }
}
