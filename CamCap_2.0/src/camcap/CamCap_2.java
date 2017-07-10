/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camcap;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.Image;
import org.opencv.features2d.Features2d;
import org.opencv.videoio.*;

public class CamCap_2 {

    public static VideoCapture capture = null;
    public static Viewer window = null;
    public static CamExecutor sceneExec = null;
    public static Mat I, J = null;
    public static Image im2;

    public static void FindRobot(Image img, CamExecutor object, Mat frame) {
        CamExecutor cam_exec = new CamExecutor();
//        cam_exec.Execute(frame, I);
        cam_exec.setDet_feat_scene(sceneExec.getDet_feat_scene());
        cam_exec.setKeypoints_scene(sceneExec.getKeypoints_scene()); 
//        cam_exec.ExecuteObject(I);
        cam_exec.setDet_feat_object(object.getDet_feat_object());
        cam_exec.setKeypoints_object(object.getKeypoints_object()); 
        cam_exec.ExecuteMatch();
        System.out.println("Keypoints found: " + cam_exec.getKeypoints_scene().toList().size());
        cam_exec.FindGood();
        //cam_exec.DeleteOdds();
        MatOfPoint2f obj = cam_exec.FindObject();
        MatOfPoint2f scene = cam_exec.FindScene();
        Mat frame_temp = new Mat();
        MatOfByte drawnMatches = new MatOfByte();
        Features2d.drawMatches(frame, cam_exec.getKeypoints_scene(), I, cam_exec.getKeypoints_object(),
                cam_exec.getGood_match_mat(), frame_temp, new Scalar(255, 0, 0),
                new Scalar(0, 0, 255), drawnMatches, Features2d.DRAW_RICH_KEYPOINTS);

        im2 = Viewer.toBufferedImage(frame_temp);

//        Image img = Viewer.toBufferedImage(frame);
        try {
            HomographyFinder hom_fin = new HomographyFinder();
            Mat H = hom_fin.FindHom(obj, scene);
           
            double[][] var = hom_fin.FindBorders(H, I);
                        
            Robot bot = new Robot();
            Point botPos = bot.FindPosition(var);
            
            window.DrawHomography(img, var, botPos);
            System.out.println("Homography found");
        } catch (Exception e) {
            System.err.println("Fail to make homography " + e);
        }
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        capture = new VideoCapture(0);
        capture.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 480);
        capture.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 640);
        if (!capture.isOpened()) {
            System.out.println("Error");
        } else {
            Mat frame = new Mat();

            I = Imgcodecs.imread("data/bat1.png");
            if (I.empty()) {
                System.err.println("Error opening image");
            }
            I = Viewer.convertToGray(I);
            CamExecutor objectExec1 = new CamExecutor();
            objectExec1.ExecuteObject(I);

            J = Imgcodecs.imread("data/ww1.png");
            if (I.empty()) {
                System.err.println("Error opening image");
            }
            J = Viewer.convertToGray(J);
            CamExecutor objectExec2 = new CamExecutor();
            objectExec2.ExecuteObject(J);                     

            window = new Viewer();
            Viewer wind = new Viewer();
            while (window.getWindow().isVisible() == true) {

                long time1 = System.nanoTime();
                capture.read(frame);
                frame = Viewer.convertToGray(frame);

                sceneExec = new CamExecutor();
                sceneExec.ExecuteScene(frame);

                Image img = Viewer.toBufferedImage(frame);

                FindRobot(img, objectExec1,frame);
                FindRobot(img, objectExec2,frame);

                window.Repaint(img);
                wind.Repaint(im2);

                long time2 = System.nanoTime();
                System.out.println("FPS = " + 1 / ((time2 - time1) / 1e9));
            }
        }
    }
}
