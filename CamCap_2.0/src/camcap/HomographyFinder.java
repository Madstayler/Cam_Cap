/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camcap;

import org.opencv.calib3d.Calib3d;
import static org.opencv.calib3d.Calib3d.RANSAC;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;

/**
 *
 * @author Madstayler
 */
public class HomographyFinder {

    public HomographyFinder() {

    }

    public Mat FindHom(MatOfPoint2f obj, MatOfPoint2f scene) {
        Mat H = null;
        try {
            H = Calib3d.findHomography(obj, scene, RANSAC, 3);
        } catch (Exception e) {
            System.err.println("Fail to find homography: " + e);
        }
        return H;
    }

    public double[][] FindBorders(Mat H, Mat I) {
        double[][] var = new double[4][4];
        try {
            Mat tmp_corners = new Mat(4, 1, CvType.CV_32FC2);
            Mat scene_corners = new Mat(4, 1, CvType.CV_32FC2);

            tmp_corners.put(0, 0, new double[]{0, 0});
            tmp_corners.put(1, 0, new double[]{I.cols(), 0});
            tmp_corners.put(2, 0, new double[]{I.cols(), I.rows()});
            tmp_corners.put(3, 0, new double[]{0, I.rows()});

            Core.perspectiveTransform(tmp_corners, scene_corners, H);

            var[0] = scene_corners.get(0, 0);
            var[1] = scene_corners.get(1, 0);
            var[2] = scene_corners.get(2, 0);
            var[3] = scene_corners.get(3, 0);
        } catch (Exception e) {
            System.err.println("Fail to find borders: "+ e);
        }
        return var;
    }

}
