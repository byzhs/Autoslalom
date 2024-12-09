package p02.pres;

import p02.game.*;

import javax.swing.*;

public class CounterPanel extends JPanel {
    private final SevenSegmentDigit hundreds;
    private final SevenSegmentDigit tens;
    private final SevenSegmentDigit ones;

    public CounterPanel() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        hundreds = new SevenSegmentDigit();
        tens = new SevenSegmentDigit();
        ones = new SevenSegmentDigit();

        ones.addListener(new SevenSegmentDigitListener() {
            @Override
            public void start(StartEvent e) {
            }

            @Override
            public void plusOne(PlusOneEvent e) {
                tens.incrementDigit();
            }

            @Override
            public void reset(ResetEvent e) {
                tens.resetDigit();
            }
        });

        tens.addListener(new SevenSegmentDigitListener() {
            @Override
            public void start(StartEvent e) {
            }

            @Override
            public void plusOne(PlusOneEvent e) {
                hundreds.incrementDigit();
            }

            @Override
            public void reset(ResetEvent e) {
                hundreds.resetDigit();
            }
        });

        add(hundreds);
        add(tens);
        add(ones);
    }

    public SevenSegmentDigit getHundreds() {
        return hundreds;
    }

    public SevenSegmentDigit getTens() {
        return tens;
    }

    public SevenSegmentDigit getOnes() {
        return ones;
    }

    public void incrementCounter() {
        ones.incrementDigit();
    }

    public void fireResetEvent(ResetEvent event) {
        hundreds.resetDigit();
        tens.resetDigit();
        ones.resetDigit();
    }
}
