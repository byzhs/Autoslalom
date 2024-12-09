package p02.game;

public interface SevenSegmentDigitListener {
    void start(StartEvent e);
    void plusOne(PlusOneEvent e);
    void reset(ResetEvent e);
}
