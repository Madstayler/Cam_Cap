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
    private Mat det_feat_scene = new Mat();
    private Mat det_feat_object = new Mat();
    private MatOfDMatch matches = new MatOfDMatch();
    private MatOfKeyPoint keypoints_scene = new MatOfKeyPoint();
    private MatOfKeyPoint keypoints_object = new MatOfKeyPoint();
    private LinkedList<DMatch> good_matches = new LinkedList<>();
    private MatOfDMatch good_match_mat = new MatOfDMatch();
    private ORB orb_HZ = ORB.create();

    public void Merge(CamExecutor cam_exec) {
        
    }

    public CamExecutor() {
//        ORB orb_HZ = ORB.create();
        orb_HZ.setMaxFeatures(500);
        detector = FeatureDetector.create(5);
        descriptor = DescriptorExtractor.create(3);
        matcher = DescriptorMatcher.create(2);
    }

    public CamExecutor(int det, int dis, int match) {
        detector = FeatureDetector.create(det);
        descriptor = DescriptorExtractor.create(dis);
        matcher = DescriptorMatcher.create(match);
    }

    public void Execute(Mat frame, Mat I) {
//        detector.detect(frame, keypoints_scene);
//        descriptor.compute(frame, keypoints_scene, det_feat_scene);
        getOrb_HZ().detect(frame, getKeypoints_scene());
        getOrb_HZ().compute(frame, getKeypoints_scene(), getDet_feat_scene());

        getOrb_HZ().detect(I, getKeypoints_object());
        getOrb_HZ().compute(I, getKeypoints_object(), getDet_feat_object());
//        detector.detect(I, keypoints_object);
//        descriptor.compute(I, keypoints_object, det_feat_object);

        getMatcher().match(getDet_feat_scene(), getDet_feat_object(), getMatches());
    }

    public void ExecuteScene(Mat frame) {
        getOrb_HZ().detect(frame, getKeypoints_scene());
        getOrb_HZ().compute(frame, getKeypoints_scene(), getDet_feat_scene());
    }

    public void ExecuteObject(Mat I) {
        getOrb_HZ().detect(I, getKeypoints_object());
        getOrb_HZ().compute(I, getKeypoints_object(), getDet_feat_object());
    }

    public void ExecuteMatch() {
        getMatcher().match(getDet_feat_scene(), getDet_feat_object(), getMatches());
    }

    public void FindGood() {
        double max_dist = 0;
        double min_dist = 99;

        List<DMatch> matchesList = getMatches().toList();
        for (int i = 0; i < getDet_feat_scene().rows(); i++) {
            double dist = matchesList.get(i).distance;
            if (dist < min_dist) {
                min_dist = dist;
            }
            if (dist > max_dist) {
                max_dist = dist;
            }
        }

        try {
            for (int i = 0; i < getDet_feat_object().rows(); i++) {
                if (matchesList.get(i).distance < 3 * min_dist) {
                    this.getGood_matches().addLast(matchesList.get(i));
                }
            }
        } catch (Exception e) {
            System.err.println("Fail to find good matches: " + e);
        }
        this.getGood_match_mat().fromList(getGood_matches());
    }

    public void DeleteOdds() {
        LinkedList<DMatch> good_matches_temp = new LinkedList<>();
        List<KeyPoint> keypoints1_List = getKeypoints_scene().toList();
        Rect temp_rect;
        int count;

        for (int i = 0; i < getGood_matches().size(); i++) {
            count = 0;
            temp_rect = new Rect((int) keypoints1_List.get(getGood_matches().get(i).queryIdx).pt.x - 50,
                    (int) keypoints1_List.get(getGood_matches().get(i).queryIdx).pt.y - 50, 100, 100);
            for (int j = 0; j < getGood_matches().size(); j++) {
                if (keypoints1_List.get(getGood_matches().get(j).queryIdx).pt.inside(temp_rect)) {
                    count++;
                }
            }
            if (this.getGood_matches().size() - count <= (int) this.getGood_matches().size() / 2) {
                good_matches_temp.addLast(this.getGood_matches().get(i));
            }
        }
        System.out.println(good_matches_temp.size() + " " + this.getGood_matches().size());
        this.setGood_matches(good_matches_temp);
        this.getGood_match_mat().fromList(this.getGood_matches());
    }

    public MatOfPoint2f FindObject() {
        List<KeyPoint> keypoints2_List = this.getKeypoints_object().toList();

        LinkedList<Point> objList = new LinkedList<>();
        this.getGood_matches().stream().forEach((good_matche) -> {
            objList.addLast(keypoints2_List.get(good_matche.trainIdx).pt);
        });
        MatOfPoint2f obj = new MatOfPoint2f();
        obj.fromList(objList);
        return obj;
    }

    public MatOfPoint2f FindScene() {
        List<KeyPoint> keypoints1_List = this.getKeypoints_scene().toList();

        LinkedList<Point> sceneList = new LinkedList<>();
        this.getGood_matches().stream().forEach((good_matche) -> {
            sceneList.addLast(keypoints1_List.get(good_matche.queryIdx).pt);
        });
        MatOfPoint2f scene = new MatOfPoint2f();
        scene.fromList(sceneList);
        return scene;
    }

    /**
     * @return the detector
     */
    public FeatureDetector getDetector() {
        return detector;
    }

    /**
     * @param detector the detector to set
     */
    public void setDetector(FeatureDetector detector) {
        this.detector = detector;
    }

    /**
     * @return the descriptor
     */
    public DescriptorExtractor getDescriptor() {
        return descriptor;
    }

    /**
     * @param descriptor the descriptor to set
     */
    public void setDescriptor(DescriptorExtractor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * @return the matcher
     */
    public DescriptorMatcher getMatcher() {
        return matcher;
    }

    /**
     * @param matcher the matcher to set
     */
    public void setMatcher(DescriptorMatcher matcher) {
        this.matcher = matcher;
    }

    /**
     * @return the det_feat_scene
     */
    public Mat getDet_feat_scene() {
        return det_feat_scene;
    }

    /**
     * @param det_feat_scene the det_feat_scene to set
     */
    public void setDet_feat_scene(Mat det_feat_scene) {
        this.det_feat_scene = det_feat_scene;
    }

    /**
     * @return the det_feat_object
     */
    public Mat getDet_feat_object() {
        return det_feat_object;
    }

    /**
     * @param det_feat_object the det_feat_object to set
     */
    public void setDet_feat_object(Mat det_feat_object) {
        this.det_feat_object = det_feat_object;
    }

    /**
     * @return the matches
     */
    public MatOfDMatch getMatches() {
        return matches;
    }

    /**
     * @param matches the matches to set
     */
    public void setMatches(MatOfDMatch matches) {
        this.matches = matches;
    }

    /**
     * @return the keypoints_scene
     */
    public MatOfKeyPoint getKeypoints_scene() {
        return keypoints_scene;
    }

    /**
     * @param keypoints_scene the keypoints_scene to set
     */
    public void setKeypoints_scene(MatOfKeyPoint keypoints_scene) {
        this.keypoints_scene = keypoints_scene;
    }

    /**
     * @return the keypoints_object
     */
    public MatOfKeyPoint getKeypoints_object() {
        return keypoints_object;
    }

    /**
     * @param keypoints_object the keypoints_object to set
     */
    public void setKeypoints_object(MatOfKeyPoint keypoints_object) {
        this.keypoints_object = keypoints_object;
    }

    /**
     * @return the good_matches
     */
    public LinkedList<DMatch> getGood_matches() {
        return good_matches;
    }

    /**
     * @param good_matches the good_matches to set
     */
    public void setGood_matches(LinkedList<DMatch> good_matches) {
        this.good_matches = good_matches;
    }

    /**
     * @return the good_match_mat
     */
    public MatOfDMatch getGood_match_mat() {
        return good_match_mat;
    }

    /**
     * @param good_match_mat the good_match_mat to set
     */
    public void setGood_match_mat(MatOfDMatch good_match_mat) {
        this.good_match_mat = good_match_mat;
    }

    /**
     * @return the orb_HZ
     */
    public ORB getOrb_HZ() {
        return orb_HZ;
    }

    /**
     * @param orb_HZ the orb_HZ to set
     */
    public void setOrb_HZ(ORB orb_HZ) {
        this.orb_HZ = orb_HZ;
    }
}
