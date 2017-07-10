/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camcap;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;

/**
 *
 * @author Madstayler
 */
public class Robot {

    private Point position = null;
    private Mat marker = null;

    public void Robot() {
        marker = Imgcodecs.imread("data/test3.png");
        if (marker.empty()) {
            System.err.println("Error opening image");
        }
        marker = Viewer.convertToGray(marker);
    }

    public void Robot(Mat I) {
        marker = I;
    }

    public Point FindPosition(double[][] var) {
        Point tempPos = new Point();
        double y1 = var[0][1] - var[2][1];
        double y2 = var[1][1] - var[3][1];
        double x1 = var[0][0] - var[2][0];
        double x2 = var[1][0] - var[3][0];
        double d = y1 * x2 - y2 * x1;
        double c1 = 0;
        double c2 = 0;
        if (d != 0) {
            c1 = var[2][1] * var[0][0] - var[2][0] * var[0][1];
            c2 = var[1][0] * var[3][1] - var[3][0] * var[1][1];

            tempPos.x = (x1 * c2 - x2 * c1) / d;
            tempPos.y = (y2 * c1 - y1 * c2) / d;
        }
        return tempPos;
    }

    /**
     * @return the position
     */
    public Point getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(Point position) {
        this.position = position;
    }

    /**
     * @return the marker
     */
    public Mat getMarker() {
        return marker;
    }

    /**
     * @param marker the marker to set
     */
    public void setMarker(Mat marker) {
        this.marker = marker;
    }
}
