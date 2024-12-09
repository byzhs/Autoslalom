package p02.pres;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class TrackPanel extends JPanel {
    private Image trackImage;

    public TrackPanel() {
        try {
            File trackFile = new File("C:/Users/Lenovo/Desktop/auto2/src/Rows/deco.png");
            if (trackFile.exists()) {
                trackImage = ImageIO.read(trackFile);
                if (trackImage != null) {
                    System.out.println("Track image loaded successfully.");
                } else {
                    System.out.println("Track image is null after loading.");
                }
            } else {
                System.out.println("Track image file does not exist: " + trackFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading track image.");
        }
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (trackImage != null) {
            g.drawImage(trackImage, 0, 0, getWidth(), getHeight(), this);
            System.out.println("Drawing track image.");
        } else {
            System.out.println("Track image is null.");
        }
    }
}
