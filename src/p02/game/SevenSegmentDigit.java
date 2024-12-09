package p02.game;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SevenSegmentDigit extends JPanel {
    private int digit;
    private final List<SevenSegmentDigitListener> listeners;

    public SevenSegmentDigit() {
        this.digit = 0;
        this.listeners = new ArrayList<>();
    }

    public void addListener(SevenSegmentDigitListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SevenSegmentDigitListener listener) {
        listeners.remove(listener);
    }

    public void setDigit(int digit) {
        this.digit = digit;
        repaint();
    }

    public int getDigit() {
        return digit;
    }

    public void incrementDigit() {
        digit++;
        if (digit > 9) {
            digit = 0;
            firePlusOneEvent();
        }
        repaint();
    }

    public void resetDigit() {
        digit = 0;
        repaint();
    }

    private void fireStartEvent() {
        StartEvent event = new StartEvent(this);
        for (SevenSegmentDigitListener listener : listeners) {
            listener.start(event);
        }
    }

    private void firePlusOneEvent() {
        PlusOneEvent event = new PlusOneEvent(this);
        for (SevenSegmentDigitListener listener : listeners) {
            listener.plusOne(event);
        }
    }

    private void fireResetEvent() {
        ResetEvent event = new ResetEvent(this);
        for (SevenSegmentDigitListener listener : listeners) {
            listener.reset(event);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.RED);

        int w = getWidth();
        int h = getHeight();
        int d = 10;

        if (digit != 1 && digit != 4) {
            g.fillRect(d, 0, w - 2 * d, d);
        }

        if (digit != 1 && digit != 2 && digit != 3 && digit != 7) {
            g.fillRect(0, d, d, h / 2 - d);
        }

        if (digit != 5 && digit != 6) {
            g.fillRect(w - d, d, d, h / 2 - d);
        }

        if (digit != 0 && digit != 1 && digit != 7) {
            g.fillRect(d, h / 2 - d / 2, w - 2 * d, d);
        }

        if (digit == 0 || digit == 2 || digit == 6 || digit == 8) {
            g.fillRect(0, h / 2, d, h / 2 - d);
        }

        if (digit != 2) {
            g.fillRect(w - d, h / 2, d, h / 2 - d);
        }

        if (digit != 1 && digit != 4 && digit != 7) {
            g.fillRect(d, h - d, w - 2 * d, d);
        }
    }
}
