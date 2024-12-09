package p02.pres;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class GameBoard extends JTable {
    private final GameBoardModel model;
    private final Image carLeft;
    private final Image carMiddle;
    private final Image carRight;
    private final Map<Integer, Image[]> images;
    private final Map<Integer, Integer> imageHeights;

    public GameBoard(Image carLeft, Image carMiddle, Image carRight, Map<Integer, Image[]> images, Map<Integer, Integer> imageHeights) {
        this.carLeft = carLeft;
        this.carMiddle = carMiddle;
        this.carRight = carRight;
        this.images = images;
        this.imageHeights = imageHeights;
        this.model = new GameBoardModel();
        this.setModel(model);
        this.setDefaultRenderer(Object.class, new GameBoardRenderer());
        this.setFocusable(false);
        updateRowHeights();
    }

    public void updateBoard(int[] track, int carPosition) {
        model.updateBoard(track, carPosition);
        updateRowHeights();
    }

    private void updateRowHeights() {
        for (Map.Entry<Integer, Integer> entry : imageHeights.entrySet()) {
            int adjustedRow = 6 - entry.getKey();
            setRowHeight(adjustedRow, entry.getValue());
        }
    }

    private class GameBoardModel extends AbstractTableModel {
        private final int[][] board;
        private int carPosition;

        public GameBoardModel() {
            board = new int[7][1];
        }

        public void updateBoard(int[] track, int carPosition) {
            for (int i = 0; i < track.length; i++) {
                board[i][0] = track[i];
                fireTableCellUpdated(i, 0);
            }
            this.carPosition = carPosition;
        }

        @Override
        public int getRowCount() {
            return board.length;
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return board[rowIndex][columnIndex];
        }

        public int getCarPosition() {
            return carPosition;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }
    }

    private class GameBoardRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            int val = (int) value;
            JLabel label = new JLabel();
            label.setOpaque(true);
            label.setHorizontalAlignment(JLabel.CENTER);

            int adjustedRow = 6 - row;

            if (adjustedRow == 0) {
                switch (model.getCarPosition()) {
                    case 0:
                        label.setIcon(new ImageIcon(carLeft));
                        break;
                    case 1:
                        label.setIcon(new ImageIcon(carMiddle));
                        break;
                    case 2:
                        label.setIcon(new ImageIcon(carRight));
                        break;
                }
            } else {
                Image img = images.getOrDefault(adjustedRow, new Image[15])[val];
                if (img == null) {
                    img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
                }
                label.setIcon(new ImageIcon(img));
            }

            return label;
        }
    }
}
