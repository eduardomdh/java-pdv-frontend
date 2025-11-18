package br.com.ui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class EllipseButton extends JButton {

    private Color hoverBackgroundColor;
    private Color pressedBackgroundColor;

    public EllipseButton(String text) {
        super(text);
        super.setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (getModel().isPressed()) {
            g2.setColor(pressedBackgroundColor != null ? pressedBackgroundColor : getBackground().darker());
        } else if (getModel().isRollover()) {
            g2.setColor(hoverBackgroundColor != null ? hoverBackgroundColor : getBackground().brighter());
        } else {
            g2.setColor(getBackground());
        }
        
        g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));

        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        Shape shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
        return shape.contains(x, y);
    }

    public void setHoverBackgroundColor(Color color) {
        this.hoverBackgroundColor = color;
    }

    public void setPressedBackgroundColor(Color color) {
        this.pressedBackgroundColor = color;
    }
}
