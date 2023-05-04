/*
 * Game.java
 * Mona Liu
 * 
 * Main game screen with gameplay components: deck and waste deck that can flip 1 or 
 * 3 cards at a time depending on the constructor, 4 foundation piles, and 7 tableau piles.
 * Also has a move counter, score counter, and restart button.
*/

import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.LinkedList;

class Game extends JPanel implements MouseMotionListener, MouseListener, ActionListener {
    // Locations of various piles of cards
    private final int DECK_X, DECK_Y, FOUND_X, FOUND_Y, TAB_X, TAB_Y, WASTE_X, WASTE_Y, FOUND_GAP, TAB_GAP;
    // Location of menu
    private final int MENU_X, MENU_Y;
    // Location of menu components RELATIVE TO MENU (text y - baseline of text)
    private final int TIMER_X, SCORE_X, MOVES_X, TEXT_Y;
    // Location of restart button
    private final int RESTART_X;

    private int mouseX, mouseY;

    // Array of tableaus 1-7 (1 - leftmost)
    private ArrayList<LinkedList<Card>> tableaus = new ArrayList<LinkedList<Card>>(7);

    // Array of foundation (4)
    private ArrayList<LinkedList<Card>> foundations = new ArrayList<LinkedList<Card>>(4);

    // Deck
    private LinkedList<Card> deck, wasteDeck;
    private int deckFlip, deckPasses;

    // Number of moves and score
    private int moves, score;

    // Button and boolean for whether it is being hovered on
    private Button restart;
    private boolean buttonHover;

    // Background image and restart button icon
    private Image bg;

    // List of cards that are currently bieng moved
    private LinkedList<Card> holding;

    // Pile that the card were taken from
    private LinkedList<Card> previousPile;

    // Boolean for game starting (eerything has been initialized) and timer
    private boolean gameStart;
    private Timer timer;

    Solitaire mainScreen;

    /*
     * CONSTRUCTOR: game with n-cards-flipped-at-a-time
     */
    public Game(int n, Solitaire main) {
        mainScreen = main;

        bg = new ImageIcon("rsc/img/background_2.png").getImage().getScaledInstance(800, 700, Image.SCALE_SMOOTH);

        // Don't start game yet
        gameStart = false;

        // Make empty tableaus and add to array
        for (int i = 0; i < 7; i++) {
            LinkedList<Card> t = new LinkedList<Card>();
            tableaus.add(t);
        }

        // Make empty foundations
        for (int i = 0; i < 4; i++) {
            LinkedList<Card> f = new LinkedList<Card>();
            foundations.add(f);
        }

        // Set locations of piles of cards
        DECK_X = 660;
        DECK_Y = 70;
        WASTE_X = 590;
        WASTE_Y = 70;

        TAB_X = 60;
        TAB_Y = 200;
        FOUND_X = 60;
        FOUND_Y = 70;

        TAB_GAP = 100;
        FOUND_GAP = 70;

        // Set locations of menu
        MENU_X = 0;
        MENU_Y = 0;

        TIMER_X = 20;
        MOVES_X = 220;
        SCORE_X = 420;
        TEXT_Y = 30;

        RESTART_X = 620;

        // Make deck
        deck = new LinkedList<Card>();
        wasteDeck = new LinkedList<Card>();
        deckFlip = n;
        deckPasses = 0;

        // Make holding pile (the cards being moved)
        holding = new LinkedList<Card>();

        // Make restart button
        restart = new Button("RESTART", new Font("Verdana", Font.BOLD, 20),
                Color.WHITE, Color.GRAY, RESTART_X, TEXT_Y);

        // Add listeners
        addMouseListener(this);
        addMouseMotionListener(this);

        // Start timer
        timer = new Timer(20, this);
        timer.start();

        makeNewGame();
        flipLast();
    }

    /* Randomly deal cards */
    public void makeNewGame() {
        // Clear deck, waste pile, all tableaus, all foundations
        deck.clear();
        wasteDeck.clear();
        for (LinkedList<Card> t : tableaus) {
            t.clear();
        }
        for (LinkedList<Card> f : foundations) {
            f.clear();
        }

        // Clear score and moves
        score = 0;
        moves = 0;

        // Make 2d array of possible card/suit values (starts at 1 but has extra 0)
        boolean[][] cardBank = new boolean[5][14];
        // "Add" possible values to array
        for (int s = 0; s < 5; s++) {
            for (int n = 1; n < 14; n++) {
                cardBank[s][n] = true;
            }
        }

        // Loop through tableaus
        for (int t = 0; t < 7; t++) {
            // Loop through number of cards in each tableau
            // (should have 1 more than the position of it)
            for (int c = 0; c < t + 1; c++) {
                // Make card and add to tableau
                Card newCard = new Card(cardBank);
                tableaus.get(t).add(newCard);

                // "Remove" card from bank to not add it twice
                cardBank[newCard.getSuit()][newCard.getNumber()] = false;
            }
        }

        // Add rest to deck (24 cards)
        for (int c = 0; c < 24; c++) {
            Card newCard = new Card(cardBank);
            cardBank[newCard.getSuit()][newCard.getNumber()] = false;
            deck.add(newCard);
        }

        flipLast();
        gameStart = true;

    }

    /* Check whether points should be subtracted for deck passes */
    private boolean additionalPass() {
        // 3 card mode: every 2 passes after 3 (4, 6, 8, etc)
        if (deckFlip == 3) {
            return deckPasses > 3 && deckPasses % 2 == 0;
        }

        // 1 card mode: every pass after 2
        return deckPasses > 2;

    }

    /* Advance position in the deck by n */
    private void turnDeck(int n) {
        // Flip over 1/3 cards
        for (int i = n; i > 0; i--) {
            wasteDeck.add(deck.removeLast());
            wasteDeck.getLast().flip();

            // If it goes back to the beginning
            if (deck.isEmpty()) {
                // Flip all cards back face down
                for (Card c : wasteDeck) {
                    c.flip();
                }

                // Move all cards back to deck
                deck.addAll(wasteDeck);
                wasteDeck.clear();

                // Increase # of passes
                deckPasses++;

                // Subtract score based on game mode
                if (additionalPass()) {
                    score -= 50;
                }
            }
        }
        // Increase moves
        moves++;
    }

    /* Flip over the last card on all tableaus */
    private void flipLast() {
        for (int i = 0; i < 7; i++) {
            // Don't flip if there are no cards
            if (tableaus.get(i).isEmpty()) {
                return;
            }
            // Flip the ones that are not face up
            if (!tableaus.get(i).getLast().isFaceUp()) {
                tableaus.get(i).getLast().flip();

                // Add score of 10 (only if it's not the game setup)
                if (gameStart) {
                    score += 10;
                }
            }
        }
    }

    /* Draw tableaus with the first one at (x, y) */
    private void drawTableaus(Graphics g, int x, int y) {
        // Loop through tableaus
        for (int t = 0; t < tableaus.size(); t++) {
            // Doesn't have cards -> draw placeholder
            if (tableaus.get(t).isEmpty()) {
                Card.drawBlank(g, x + TAB_GAP * t, y);
                continue;
            }

            // Otherwise Loop through cards and draw
            for (int c = 0; c < tableaus.get(t).size(); c++) {
                tableaus.get(t).get(c).draw(g, x + TAB_GAP * t, y + Card.HEIGHT / 3 * c);
            }
        }
    }

    /* Draw foundations with the first one at (x,y) */
    private void drawFoundations(Graphics g, int x, int y) {
        // Loop through foundations
        for (int f = 0; f < foundations.size(); f++) {
            // Doesn't have cards -> draw placeholder
            if (foundations.get(f).isEmpty()) {
                Card.drawBlank(g, x + FOUND_GAP * f, y);
                continue;
            }

            // Has cards -> draw cards
            foundations.get(f).getLast().draw(g, x + FOUND_GAP * f, y);
        }
    }

    /* Draw last card in deck and waste deck */
    private void drawDeck(Graphics g, int deckX, int deckY, int wasteX, int wasteY) {
        // Draw deck
        if (deck.isEmpty()) {
            Card.drawBlank(g, deckX, deckY);
        } else {
            deck.getLast().draw(g, deckX, deckY);
        }

        int wastecards = wasteDeck.size();
        // Draw waste deck (blank/placeholder if empty)
        if (wastecards == 0) {
            Card.drawBlank(g, wasteX, wasteY);
        } else {
            // Draw 2nd and 3rd last cards if they exist
            if (wastecards > 2) {
                wasteDeck.get(wastecards - 3).draw(g, wasteX - 60, wasteY);
            }
            if (wastecards > 1) {
                wasteDeck.get(wastecards - 2).draw(g, wasteX - 30, wasteY);
            }
            // Draw last card
            wasteDeck.getLast().draw(g, wasteX, wasteY);
        }

    }

    /* Draw cards that are being "held" */
    private void drawHolding(Graphics g, int x, int y) {
        for (int c = 0; c < holding.size(); c++) {
            holding.get(c).draw(g, x, y + Card.HEIGHT / 3 * c);
        }
    }

    /* Draw the menu bar (time, score, moves, restart button) */
    private void drawMenu(Graphics g, int x, int y) {
        // Menu bg
        g.setColor(new Color(50, 50, 50, 100));
        g.drawRect(x, y, 800, 40);

        g.setFont(new Font("Verdana", Font.BOLD, 20));

        // Restart button
        restart.draw(g, buttonHover);

        // Other text
        g.setColor(Color.WHITE);
        g.drawString("Moves: " + moves, x + MOVES_X, y + TEXT_Y);
        g.drawString("Score: " + score, x + SCORE_X, y + TEXT_Y);
    }

    // Take top card from waste deck
    private void takefromDeck() {
        holding.add(wasteDeck.removeLast());
        previousPile = wasteDeck;
    }

    /* Remove cards from pile starting at nth card */
    private void take(LinkedList<Card> pile, int n) {
        holding.addAll(pile.subList(n, pile.size()));
        pile.subList(n, pile.size()).clear();
        previousPile = pile;
    }

    /* Put card into a tableau or foundation pile */
    private void put(LinkedList<Card> pile) {
        pile.addAll(holding);
        holding.clear();
    }

    /* Make rectangle and return mouse collision with it */
    private boolean collideRect(Rectangle r) {
        return r.contains(mouseX, mouseY);
    }

    /* Return position of tableau that is being clicked, -1 if none */
    private int mouseOnTableau() {
        int x, y, w, h;
        // Loop through tableaus
        for (int t = 0; t < tableaus.size(); t++) {
            x = TAB_X + TAB_GAP * t;
            y = TAB_Y;
            w = Card.WIDTH;
            h = (tableaus.get(t).size() - 1) * Card.HEIGHT / 3 + Card.HEIGHT; // All cards except one are Half of card
                                                                              // height

            // Check collision with rectangle
            if (collideRect(new Rectangle(x, y, w, h))) {
                return t;
            }
        }
        return -1;
    }

    /* Return position of foundation that isbeing clicked and -1 if none */
    private int mouseOnFoundation() {
        int x, y, w, h;
        // Loop through tableaus
        for (int f = 0; f < foundations.size(); f++) {
            x = FOUND_X + FOUND_GAP * f;
            y = FOUND_Y;
            w = Card.WIDTH;
            h = Card.HEIGHT;

            // Check collision with rectangle
            if (collideRect(new Rectangle(x, y, w, h))) {
                return f;
            }
        }
        return -1;
    }

    /*
     * Return position of card that is being clicked, -1 if noen
     * t = position of tableau
     */
    private int mouseOnCard(int t) {
        int x, y, w, h;

        // Don't do anything if it's blank (no cards)
        if (tableaus.get(t).isEmpty()) {
            return -1;
        }

        // Loop through cards
        for (int c = 0; c < tableaus.get(t).size(); c++) {
            x = TAB_X + TAB_GAP * t;
            y = TAB_Y + c * Card.HEIGHT / 3;
            w = Card.WIDTH;

            // For all except the last one only 1/3 of the card is visibel
            h = Card.HEIGHT / 3;
            if (c == tableaus.get(t).size() - 1) { // Last card
                h = Card.HEIGHT;
            }

            if (collideRect(new Rectangle(x, y, w, h))) {
                return c;
            }
        }

        return -1;
    }

    /*
     * Returns true if the cards that are in holding can't be put onto the tableau
     */
    private boolean notAllowedTableau(LinkedList<Card> t) {
        // The last card on the pile
        Card baseCard;
        // The card you're trying to put down
        Card newCard = holding.get(0);

        // If the pile is empty you can't put anything except kings
        if (t.isEmpty()) {
            if (newCard.getNumber() != 13) {
                return true;
            }
            return false;
        }

        // If the pile has cards find the last card on the pile
        baseCard = t.getLast();

        // You can't put it if it's Not the same suit or Not the next biggest number
        if (baseCard.isRed() == newCard.isRed() || baseCard.getNumber() != newCard.getNumber() + 1) {
            return true;
        }

        // Otherwise you can put it
        return false;

    }

    /* Returns true if the cards in holding cant' be put onto the foundation */
    private boolean notAllowedFoundation(LinkedList<Card> f) {
        // The last card on the pile
        Card baseCard;
        // The card you're trying to put down
        Card newCard = holding.get(0);

        // You can't put down more than one card
        if (holding.size() > 1) {
            return true;
        }

        // If the pile is empty you can't put anything except aces
        if (f.isEmpty()) {
            if (newCard.getNumber() != 1) {
                return true;
            }
            // Aces can be put
            return false;
        }

        // If the pile has cards find the last card on the pile
        baseCard = f.getLast();

        // You can't put it if it's Not the same suit or Not the next biggest number
        if (baseCard.getSuit() != newCard.getSuit() || baseCard.getNumber() != newCard.getNumber() - 1) {
            return true;
        }

        // Otherwise you can put it
        return false;

    }

    /* Put a card down on a tableau */
    private void tryPutOnTableau(int t) {
        // Get pile that was clicked
        LinkedList<Card> newPile = tableaus.get(t);

        // Put card back if move is against the rules or if it's the same pile
        if (newPile == previousPile || notAllowedTableau(newPile)) {
            put(previousPile);
            return;
        }

        // Otherwise Put the card
        put(newPile);
        makeMove(previousPile, newPile);

        return;
    }

    /* PUt card down on a foundation */
    private void tryPutOnFoundation(int f) {
        // Set destination pile
        LinkedList<Card> newPile = foundations.get(f);

        // Put card back if move is against the rules or if it's the same pile
        if (newPile == previousPile || notAllowedFoundation(newPile)) {
            put(previousPile);
            return;
        }

        // Otherwise put card on the new pile
        put(newPile);
        makeMove(previousPile, newPile);

        return;
    }

    /* try to take a card / cards from a tableau given position */
    private void tryTakeFromTableau(int t) {

        // Can't take cards if tableau has no cards
        if (tableaus.get(t).isEmpty()) {
            return;
        }

        // Check all cards
        int toTake = mouseOnCard(t);

        // Don't do anything If card is not face up becasue you can't move it
        if (!tableaus.get(t).get(toTake).isFaceUp()) {
            return;
        }

        // Take card
        take(tableaus.get(t), toTake);
    }

    /* Try to take top card from a foundation given position */
    private void tryTakeFromFoundation(int f) {
        // Can't take if there's no card
        if (foundations.get(f).isEmpty()) {
            return;
        }

        // Otherwise take last card
        take(foundations.get(f), foundations.get(f).size() - 1);
    }

    /*
     * Add / subract score and add to moves depending on where the card moved from /
     * to
     */
    private void makeMove(LinkedList<Card> from, LinkedList<Card> to) {

        // Increase # of moves no matter what
        moves += 1;

        if (foundations.contains(to)) {
            // always +15 pts for moving to a foundation pile
            score += 15;
            return;
        }

        if (foundations.get(0) == from || foundations.get(1) == from ||
                foundations.get(2) == from || foundations.get(3) == from) {
            // always -20 pts for taking away from foundation
            score -= 20;
            return;
        }

        // Only other possible moves: tableau to tableau, deck to tableau
        // Both +5 pts
        score += 5;

    }

    /* Pop up dialog asking to restart */
    private void restart() {
        // List of options
        Object[] options = { "Menu", "Re-Deal", "Cancel" };
        // Get result of popup
        int restart = JOptionPane.showOptionDialog(this,
                "Progress will not be saved",
                "Restart?",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, options, options[2]);

        // Restart game if clicked "yes"
        if (restart == 1) {
            gameStart = false;
            makeNewGame();
        }

        // Go back to home if clicked "Menu"
        if (restart == 0) {
            mainScreen.start();
        }
    }

    /*
     * Writes score of game to file
     */
    private void saveHighScore() {
        try {
            Scanner reader = new Scanner(new FileReader("rsc/score.txt"));

            // Make array
            int[] highScores = new int[3];
            // Read Top scores
            for (int i = 0; i < 3; i++) {
                highScores[i] = reader.nextInt();
            }

            // Reorder list if score beat any of them
            for (int i = 0; i < 3; i++) {
                if (score > highScores[i]) {
                    for (int j = 2; j > i; j--) {
                        highScores[j] = highScores[j - 1];
                    }
                    highScores[i] = score;
                    break;
                }
            }

            reader.close();

            PrintWriter printer = new PrintWriter(new FileWriter("rsc/score.txt"));

            // Print all scores and current one at teh end
            for (int n : highScores) {
                printer.println(n);
            }
            printer.println(score);

            printer.close();
        }

        catch (FileNotFoundException ex) {
            System.out.println("Error: score could not be saved");
        }

        catch (IOException ex) {
            System.out.println("Error: score could not be saved");
        }
    }

    /* End game when all foundtions have 13 cards */
    private void gameEnded() {
        // Loop through all and check length (# of cards)
        for (LinkedList<Card> f : foundations) {
            // Any one is not full means game has not ended
            if (f.size() != 13) {
                return;
            }
        }
        saveHighScore();
        mainScreen.end();
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
        // If there are cards in the holding list you can't do anything till you put
        // them down
        if (!holding.isEmpty()) {
            return;
        }

        // Otherwise check for clicking on deck
        if (collideRect(new Rectangle(DECK_X, DECK_Y, Card.WIDTH, Card.HEIGHT))) {
            turnDeck(deckFlip);
            return;
        }

        // Pressed on WASTE deck -> pick up card from it
        else if (collideRect(new Rectangle(WASTE_X, WASTE_Y, Card.WIDTH, Card.HEIGHT))) {
            // Don't do anything if there aren't any cards yet
            if (wasteDeck.isEmpty()) {
                return;
            }
            // Otherwise take top card
            takefromDeck();
        }

        // Pressed on tableau area
        else if (mouseY >= 140) {
            // Check all tablaeaus
            int toTakeFrom = mouseOnTableau();

            // Don't do anything if no tableau was clicked
            if (toTakeFrom == -1) {
                return;
            }
            // Otherwise check for card clicking in the pile
            tryTakeFromTableau(toTakeFrom);
        }

        // Pressed in foundation area
        else if (mouseX < 550) {
            int toTakeFrom = mouseOnFoundation();

            // Don't do anything if no foundation was clicked
            if (toTakeFrom == -1) {
                return;
            }

            // Otherwise check for pile to take
            tryTakeFromFoundation(toTakeFrom);
        }

        // Prss restart button
        else if (buttonHover) {
            restart();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Check for putting down cards- only if there are cards in the "holding" list
        if (!holding.isEmpty()) {
            // Mouse is in the tableau area
            if (mouseY >= 140) {
                // Check for mouse release on any pile (not on blank spot)
                int toPutOn = mouseOnTableau();

                // Put card back if no tableau was "clicked"
                if (toPutOn == -1) {
                    put(previousPile);
                    return;
                }

                tryPutOnTableau(toPutOn);

            }

            // Mouse is in foundation area
            else if (mouseX < 550) {
                // Check for mouse release on any pile (not on blank spot)
                int toPutOn = mouseOnFoundation();

                // Put card back if nothing was "clicked"
                if (toPutOn == -1) {
                    put(previousPile);
                    return;
                }

                tryPutOnFoundation(toPutOn);
            }

            // If released mouse in random /blank location put the card back
            put(previousPile);

            // Flip over new last cards
            flipLast();
        }
    }

    @Override
    public void paint(Graphics g) {
        // Only draw game components after they are made
        if (gameStart) {
            g.drawImage(bg, 0, 0, null);
            drawTableaus(g, TAB_X, TAB_Y);
            drawDeck(g, DECK_X, DECK_Y, WASTE_X, WASTE_Y);
            drawFoundations(g, FOUND_X, FOUND_Y);
            buttonHover = collideRect(restart.getRect());
            drawMenu(g, MENU_X, MENU_Y);

            // Draw holding card if they exist
            if (holding.size() != 0) {
                drawHolding(g, mouseX - 10, mouseY - 10);
            }

            // Check for end game
            gameEnded();
        }

    }
}