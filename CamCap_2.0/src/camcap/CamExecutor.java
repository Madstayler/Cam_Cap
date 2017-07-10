/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camcap;

import java.util.LinkedList;
import java.util.List;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.ORB;

/**
 *
 * @author Madstayler
 */
public class CamExecutor {

    private FeatureDetector detector;
    private DescriptorExtractor descriptor;
    private DescriptorMatcher matcher;
    public Mat det_feat_scene = new Mat();
    public Mat det_feat_object = new Mat();
    public MatOfDMatch matches = new MatOfDMatch();
    public MatOfKeyPoint keypoints_scene = new MatOfKeyPoint();
    public MatOfKeyPoint keypoints_object = new MatOfKeyPoint();
    public LinkedList<DMatch> good_matches = new LinkedList<>();
    public MatOfDMatch good_match_mat = new MatOfDMatch();
    public ORB orb_HZ = ORB.create();


    public CamExecutor() {
//        ORB orb_HZ = ORB.create();
        this.orb_HZ.setMaxFeatures(5000);
        this.detector = FeatureDetector.create(5);
        this.descriptor = DescriptorExtractor.create(3);
        this.matcher = DescriptorMatcher.create(2);
    }

    public CamExecutor(int det, int dis, int match) {
        this.detector = FeatureDetector.create(det);
        this.descriptor = DescriptorExtractor.create(dis);
        this.matcher = DescriptorMatcher.create(match);
    }

    public void Execute(Mat frame, Mat I) {
//        detector.detect(frame, keypoints_scene);
//        descriptor.compute(frame, keypoints_scene, det_feat_scene);
        this.orb_HZ.detect(frame, keypoints_scene);
        this.orb_HZ.compute(frame, keypoints_scene, det_feat_scene);
        
        this.orb_HZ.detect(I, keypoints_object);
        this.orb_HZ.compute(I, keypoints_object, det_feat_object);
//        detector.detect(I, keypoints_object);
//        descriptor.compute(I, keypoints_object, det_feat_object);

        this.matcher.match(det_feat_scene, det_feat_object, matches);
    }

    public void ExecuteScene(Mat frame) {
        this.orb_HZ.detect(frame, keypoints_scene);
        this.orb_HZ.compute(frame, keypoints_scene, det_feat_scene);
    }
    
    public void ExecuteObject(Mat I) {
        this.orb_HZ.detect(I, keypoints_object);
        this.orb_HZ.compute(I, keypoints_object, det_feat_object);
    }
    
    public void ExecuteMatch() {
        this.matcher.match(det_feat_scene, det_feat_object, matches);
    }
    
    public void FindGood() {
        double max_dist = 0;
        double min_dist = 99;

        List<DMatch> matchesList = this.matches.toList();
        for (int i = 0; i < this.det_feat_scene.rows(); i++) {
            double dist = matchesList.get(i).distance;
            if (dist < min_dist) {
                min_dist = dist;
            }
            if (dist > max_dist) {
                max_dist = dist;
            }
        }

        try {
            for (int i = 0; i < this.det_feat_object.rows(); i++) {
                if (matchesList.get(i).distance < 3 * min_dist) {
                    this.good_matches.addLast(matchesList.get(i));
                }
            }
        } catch (Exception e) {
            System.err.println("Fail to find good matches: " + e);
        }
        this.good_match_mat.fromList(good_matches);
    }

    public void DeleteOdds() {
        LinkedList<DMatch> good_matches_temp = new LinkedList<>();
        List<KeyPoint> keypoints1_List = keypoints_scene.toList();
        Rect temp_rect;
        int count;

        for (int i = 0; i < good_matches.size(); i++) {
            count = 0;
            temp_rect = new Rect((int) keypoints1_List.get(good_matches.get(i).queryIdx).pt.x - 50,
                    (int) keypoints1_List.get(good_matches.get(i).queryIdx).pt.y - 50, 100, 100);
            for (int j = 0; j < good_matches.size(); j++) {
                if (keypoints1_List.get(good_matches.get(j).queryIdx).pt.inside(temp_rect)) {
                    count++;
                }
            }
            if (this.good_matches.size() - count <= (int) this.good_matches.size() / 2) {
                good_matches_temp.addLast(this.good_matches.get(i));
            }
        }
       System.out.println(good_matches_temp.size() + " " + this.good_matches.size());
        this.good_matches = good_matches_temp;
        this.good_match_mat.fromList(this.good_matches);
    }

    public MatOfPoint2f FindObject() {
        List<KeyPoint> keypoints2_List = this.keypoints_object.toList();

        LinkedList<Point> objList = new LinkedList<>();
        this.good_matches.stream().forEach((good_matche) -> {
            objList.addLast(keypoints2_List.get(good_matche.trainIdx).pt);
        });
        MatOfPoint2f obj = new MatOfPoint2f();
        obj.fromList(objList);
        return obj;
    }

    public MatOfPoint2f FindScene() {
        List<KeyPoint> keypoints1_List = this.keypoints_scene.toList();

        LinkedList<Point> sceneList = new LinkedList<>();
        this.good_matches.stream().forEach((good_matche) -> {
            sceneList.addLast(keypoints1_List.get(good_matche.queryIdx).pt);
        });
        MatOfPoint2f scene = new MatOfPoint2f();
        scene.fromList(sceneList);
        return scene;
    }
}
