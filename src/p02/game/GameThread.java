package p02.game;

public class GameThread extends Thread {
    private static GameThread instance;
    private boolean running;
    private long interval;
    private final Board board;

    private GameThread(Board board) {
        this.board = board;
        this.interval = 1000;
    }

    public static synchronized GameThread getInstance(Board board) {
        if (instance == null) {
            instance = new GameThread(board);
        }
        return instance;
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                Thread.sleep(interval);
                board.tick();
                adjustInterval();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopThread() {
        running = false;
        instance = null;
    }

    private void adjustInterval() {
        if (interval > 200) {
            interval -= 10;
        }
    }

    public void resetInterval() {
        interval = 1000;
    }
}
