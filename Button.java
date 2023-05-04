/*
 * Button.java
 * Mona Liu
 * 
 * A simple button which displays text and changes color if you hover your mouse over it.
 * Has properties for the button: an automatically created rectangle, text to display, and colours
 * for hovering state (mouse is on the button) and non hovering state.
 * Has methods to get the rectangle representing it and draw the text.
 */

import java.awt.Font;
import java.awt.font.*;
import java.awt.geom.AffineTransform;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

class Button {
    // position of button (text)
    private int x, y;

    // Size of rectangle that the button is
    private Rectangle rect;

    // Text to be displayed
    private String buttonText;

    // Font (system) to show the text in
    private Font buttonFont;

    // Colours
    private Color isHover, isNotHover;

    // Font render context and affine transform for making the rectangle
    AffineTransform at = new AffineTransform();
    FontRenderContext frc = new FontRenderContext(at, true, false);


    /* 
     * Creates button with specific text and font, colours for non-hover and hover, and
     * x and y position
     */
    public Button(String text, Font font, Color regularColour, Color hoverColor, int xPos, int yPos) {
        // Set text and font
        buttonText = text;
        buttonFont = font;
        x = xPos;
        y = yPos;

        // Make text layout and rectangle
        TextLayout textBox = new TextLayout(text, font, frc);
        rect = textBox.getPixelBounds(frc, x, y);
        rect.grow(5, 5);

        // Set colours
        isHover = hoverColor;
        isNotHover = regularColour;
    }

    public Rectangle getRect() {
        return rect;
    }

    public void draw(Graphics g, boolean hover) {
        g.setFont(buttonFont);
        // Set colour depending on state
        g.setColor(hover ? isHover : isNotHover);
        g.drawString(buttonText, x, y);
    }


}
