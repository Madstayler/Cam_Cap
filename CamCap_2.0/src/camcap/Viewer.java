/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camcap;

import static java.awt.Color.red;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

/**
 *
 * @author Madstayler
 */
public class Viewer {

    public JFrame window = null;
    public ImageIcon icon = null;
    public JLabel lbl = null;

    private void Stop() {
        CamCap_2.capture.release();
        System.exit(0);
    }

    public Viewer() {
        window = new JFrame("Camera");
        lbl = new JLabel();
        window.add(lbl);
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        window.setLocation(0, 0);
        window.setVisible(true);

        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Stop();
            }
        });
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Stop();
            }
        });
    }

    public void Repaint(Image img) {
        icon = new ImageIcon(img);
        lbl.setIcon(icon);
        window.pack();
        window.repaint();
    }

    public void DrawHomography(Image img, double[][] var) {
        img.getGraphics().drawLine((int) var[0][0], (int) var[0][1], (int) var[1][0], (int) var[1][1]);
        img.getGraphics().drawLine((int) var[1][0], (int) var[1][1], (int) var[2][0], (int) var[2][1]);
        img.getGraphics().drawLine((int) var[2][0], (int) var[2][1], (int) var[3][0], (int) var[3][1]);
        img.getGraphics().drawLine((int) var[3][0], (int) var[3][1], (int) var[0][0], (int) var[0][1]);
    }

    public static Mat convertToGray(Mat mat) {
        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
        return gray;
    }

    public static Image toBufferedImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

}
