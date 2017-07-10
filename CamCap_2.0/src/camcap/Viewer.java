/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camcap;

import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Madstayler
 */
public class Viewer {

    private JFrame window = null;
    private ImageIcon icon = null;
    private JLabel lbl = null;

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
        setIcon(new ImageIcon(img));
        getLbl().setIcon(getIcon());
        getWindow().pack();
        getWindow().repaint();
    }

    public void DrawHomography(Image img, double[][] var, Point botPos) {
        img.getGraphics().drawLine((int) var[0][0], (int) var[0][1], (int) var[1][0], (int) var[1][1]);
        img.getGraphics().drawLine((int) var[1][0], (int) var[1][1], (int) var[2][0], (int) var[2][1]);
        img.getGraphics().drawLine((int) var[2][0], (int) var[2][1], (int) var[3][0], (int) var[3][1]);
        img.getGraphics().drawLine((int) var[3][0], (int) var[3][1], (int) var[0][0], (int) var[0][1]);
//        img.getGraphics().fillOval((int)botPos.x-10, (int)botPos.y-10, 20, 20);
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
        m.get(0, 0, b); 
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

    /**
     * @return the window
     */
    public JFrame getWindow() {
        return window;
    }

    /**
     * @param window the window to set
     */
    public void setWindow(JFrame window) {
        this.window = window;
    }

    /**
     * @return the icon
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    /**
     * @return the lbl
     */
    public JLabel getLbl() {
        return lbl;
    }

    /**
     * @param lbl the lbl to set
     */
    public void setLbl(JLabel lbl) {
        this.lbl = lbl;
    }

}
