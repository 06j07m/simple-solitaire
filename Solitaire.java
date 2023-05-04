/*
 * Solitaire.java
 * Mona Liu
 * 
 * Window for Solitaire game. Has multiple screens and uses a card layout to choose which 
 * one to show. Has methods that are called from inside the screen classes to switch screens.
 */

import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;

public class Solitaire extends JFrame {
	CardLayout crd;

	Intro intro;
	Game game;
	Intro outro;

	/* CONSTRUCTOR: new solitaire game */
	public Solitaire() {
		super("Solitaire");

		// Set window size
		setPreferredSize(new Dimension(800, 700));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);

		// Make card layout
		crd = new CardLayout();
		setLayout(crd);

		// Start with intro
		start();

		setVisible(true);
	}

	/* Shows intro screen */
	public void start() {
		intro = new Intro(true, this);
		add("intro", intro);
		pack();
		crd.show(getContentPane(), "intro");
	}

	/* Shows new game (flip 1 or flip 3) depending on number given */
	public void newGame(int n) {
		game = new Game(n, this);
		add("game", game);
		pack();
		crd.show(getContentPane(), "game");
	}

	/* Shows win screen */
	public void end() {
		outro = new Intro(false, this);
		add("outro", outro);
		pack();
		crd.show(getContentPane(), "outro");
	}

	public static void main(String args[]) {
		new Solitaire();
	}
}
