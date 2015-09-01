/* 
 * Copyright 2015 Nils Hoffmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package circularlayouts.layout;

import circularlayouts.IDrawable;
import circularlayouts.tracks.Ribbon;
import circularlayouts.tracks.Segment;
import circularlayouts.tracks.Ticks;
import circularlayouts.tracks.Track;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Nils Hoffmann
 */
public class RatioLayoutBuilder {

    private final double[][] data;

    public RatioLayoutBuilder(double[][] data) {
        this.data = data;
    }

    public static Shape createCurvedSegment(double startAngle, double endAngle, double innerRadius, double outerRadius, double centerx, double centery) {
        Arc2D.Double innerArc = new Arc2D.Double(Arc2D.OPEN);
        innerArc.setArcByCenter(centerx, centery, innerRadius + ((outerRadius - innerRadius) / 2.0d), Math.toDegrees(2 * Math.PI * startAngle), Math.toDegrees(2 * Math.PI * endAngle) - Math.toDegrees(2 * Math.PI * startAngle), Arc2D.OPEN);
        BasicStroke stroke = new BasicStroke((float) (outerRadius - innerRadius), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
        return stroke.createStrokedShape(innerArc);
    }

    public void addTracks(double startAngle, double rx, double centerx, double centery, double segmentSize, double targetMargin, double segmentMargin, double sourceMargin, Map<String, Track<? extends IDrawable>> shapes) {
        ArrayList<Segment> newshapes = new ArrayList<Segment>();
        ArrayList<Segment> newshapes2 = new ArrayList<Segment>();
        ArrayList<Ticks> newticks = new ArrayList<Ticks>();
        int n = data.length;
        double r = rx - segmentSize - targetMargin;
        double angleIncr = (1.0d / (double) n) * segmentMargin;
//        System.out.println("Angle increment: " + angleIncr);
        SegmentNormalizer normalizer = new SegmentNormalizer.Equal();
        double localAngle = startAngle;
        for (int i = 0; i < n; i++) {
            double localStartAngle = localAngle;
            double localEndAngle = localAngle + normalizer.getAngleForSegment(data, i);
            Color startColor = Color.getHSBColor(((float) i / (float) n), 0.5f, 0.9f);
            Segment cs = new Segment("segment" + i, rx - segmentSize, rx, centerx, centery, localStartAngle + angleIncr, localEndAngle - angleIncr, startColor, startColor.darker());
            newshapes.add(cs);
            Segment cs2 = new Segment("segment" + i, rx + 20, rx + 60, centerx, centery, localStartAngle + angleIncr, localEndAngle - angleIncr, startColor, startColor.darker());
            newshapes2.add(cs2);
            newticks.add(new Ticks("segment" + i + " ticks", rx, rx + 20, centerx, centery, localStartAngle + angleIncr, localEndAngle - angleIncr, Color.WHITE, Color.LIGHT_GRAY));
            localAngle = localEndAngle;
        }
        Point2D.Double center = new Point2D.Double(centerx, centery);
        Track<Segment> parts = new Track<Segment>(center, rx - segmentSize, rx, newshapes);
        shapes.put("Track 1", parts);
        shapes.put("Track 2", new Track<Ribbon>(center, rx - segmentSize - sourceMargin, r, createConnectionSegments(parts, rx - segmentSize - sourceMargin, r, centerx, centery)));
        shapes.put("Track 3", new Track<Ticks>(center, rx, rx + 20, newticks));
        shapes.put("Track 4", new Track<Segment>(center, rx + 20, rx + 60, newshapes2));
    }

    public double[] getRibbonSourceAngles(Segment sourceSegment, double[][] data, int sourceSegmentIndex) {
        Map<Integer, Integer> rankToIndex = new TreeMap<Integer, Integer>();
        List<Point2D> ranks = new ArrayList<>();
        double sum = Utils.getRowSum(data, sourceSegmentIndex);
        for (int i = 0; i < data[sourceSegmentIndex].length; i++) {
            Point2D p = new Point2D.Double(i, data[sourceSegmentIndex][i]/sum);
            System.out.println("Point: "+p);
            ranks.add(p);
        }
        Collections.sort(ranks, Collections.reverseOrder(new Comparator<Point2D>() {

            @Override
            public int compare(Point2D o1, Point2D o2) {
                return Double.compare(o1.getY(), o2.getY());
            }
        }));
        for(int i = 0;i<ranks.size();i++) {
            int index = (int)ranks.get(i).getX();
            rankToIndex.put(i, index);
            System.out.println("Rank: "+i+" -> "+index);
        }
        double angleRange = sourceSegment.getEndAngle() - sourceSegment.getStartAngle();
        double offset = 0.0;//sourceSegment.getStartAngle();
        double[] sourceAngleOffsets = new double[data[0].length + 1];
        double totalRatio = 0.0d;
        for (Integer rank : rankToIndex.keySet()) {
            int index = rankToIndex.get(rank);
            double ratio = getRibbonSourceRatio(data, sourceSegmentIndex, index);
//            System.out.println("Ratio: " + ratio);
            sourceAngleOffsets[index] = offset;
            offset += (ratio * (angleRange / 2.0d));
            totalRatio += ratio;
        }
//        System.out.println("Total ratio: " + totalRatio);
        sourceAngleOffsets[data[0].length] = angleRange / 2;
        return sourceAngleOffsets;
    }

    public double[] getRibbonTargetAngles(Segment targetSegment, double[][] data, int targetSegmentIndex) {
        Map<Integer, Integer> rankToIndex = new TreeMap<Integer, Integer>();
        List<Point2D> ranks = new ArrayList<>();
        double sum = Utils.getColumnSum(data, targetSegmentIndex);
        for (int i = 0; i < data[targetSegmentIndex].length; i++) {
            Point2D p = new Point2D.Double(i, data[i][targetSegmentIndex]/sum);
            System.out.println("Point: "+p);
            ranks.add(p);
        }
        Collections.sort(ranks, Collections.reverseOrder(new Comparator<Point2D>() {

            @Override
            public int compare(Point2D o1, Point2D o2) {
                return Double.compare(o1.getY(), o2.getY());
            }
        }));
        for(int i = 0;i<ranks.size();i++) {
            int index = (int)ranks.get(i).getX();
            rankToIndex.put(i, index);
            System.out.println("Rank: "+i+" -> "+index);
        }
        double angleRange = targetSegment.getEndAngle() - targetSegment.getStartAngle();
        double offset = angleRange / 2.0d;
        double[] targetAngleOffsets = new double[data[0].length + 1];
        for (Integer rank : rankToIndex.keySet()) {
            int index = rankToIndex.get(rank);
            double ratio = getRibbonTargetRatio(data, targetSegmentIndex, index);
//            System.out.println("Ratio: " + ratio);
            targetAngleOffsets[index] = offset;
            offset += (ratio * (angleRange / 2.0d));
        }
        targetAngleOffsets[data[0].length] = angleRange;
        return targetAngleOffsets;
    }

    public int getRibbonTargetRank(Map<Double, Integer> ribbonTargetRankMap, double[][] data, int targetSegmentIndex, int sourceSegmentIndex) {
        return ribbonTargetRankMap.get(data[sourceSegmentIndex][targetSegmentIndex]);
    }

    public Map<Double, Integer> getRibbonTargetRankMap(double[][] data, int targetSegmentIndex) {
        double[] values = new double[data[0].length];
        for (int i = 0; i < values.length; i++) {
            values[i] = data[i][targetSegmentIndex];
        }
        Map<Double, Integer> map = new TreeMap<Double, Integer>(Collections.reverseOrder(new Comparator<Double>() {

            @Override
            public int compare(Double o1, Double o2) {
                return Double.compare(o1, o2);
            }
        }));
        for (int i = 0; i < values.length; ++i) {
            map.put(values[i], i);
        }
        return map;
    }

    public Map<Double, Integer> getRibbonSourceRankMap(double[][] data, int sourceSegmentIndex) {
        double[] values = data[sourceSegmentIndex];
        Map<Double, Integer> map = new TreeMap<Double, Integer>(Collections.reverseOrder(new Comparator<Double>() {

            @Override
            public int compare(Double o1, Double o2) {
                return Double.compare(o1, o2);
            }
        }));
        for (int i = 0; i < values.length; ++i) {
            map.put(values[i], i);
        }
        return map;
    }

    public int getRibbonSourceRank(Map<Double, Integer> map, double[][] data, int sourceSegmentIndex, int targetSegmentIndex) {
        return map.get(data[sourceSegmentIndex][targetSegmentIndex]);
    }

    public double getRibbonSourceRatio(double[][] data, int sourceSegmentIndex, int targetSegmentIndex) {
        double rowSum = Utils.getRowSum(data, sourceSegmentIndex);
//        System.out.println("Row sum: " + rowSum);
        return data[sourceSegmentIndex][targetSegmentIndex] / rowSum;
    }

    public double getRibbonTargetRatio(double[][] data, int sourceSegmentIndex, int targetSegmentIndex) {
        double colSum = Utils.getColumnSum(data, targetSegmentIndex);
//        System.out.println("Col sum: " + colSum);
        return data[sourceSegmentIndex][targetSegmentIndex] / colSum;
    }

    public List<Ribbon> createConnectionSegments(Track<Segment> circleSegment, double sourceRadius, double targetRadius, double centerx, double centery) {
        ArrayList<Ribbon> connectionSegments = new ArrayList<Ribbon>();
        double[][] sourceOffsets = new double[data.length][data[0].length];
        double[][] targetOffsets = new double[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            sourceOffsets[i] = getRibbonSourceAngles(circleSegment.getChildren().get(i), data, i);
        }
        for (int j = 0; j < data[0].length; j++) {
            targetOffsets[j] = getRibbonTargetAngles(circleSegment.getChildren().get(j), data, j);
        }
        for (int i = 0; i < data.length; i++) {
            Segment rowSegment = circleSegment.getChildren().get(i);
//            System.out.println("Row has " + i + " has " + rowSum + " outgoing edges!");
//            System.out.println("Column " + i + " has " + rowSum + " incoming edges!");
            for (int j = 0; j < data[i].length; j++) {
                if (!Double.isNaN(data[i][j]) && !Double.isNaN(data[j][i])) {
                    Segment columnSegment = circleSegment.getChildren().get(j);
                    Connection c = new Connection();
                    c.sourceIndex = i;
                    c.targetIndex = j;
                    c.source = rowSegment;
                    c.target = columnSegment;
                    c.centerx = centerx;
                    c.centery = centery;
                    c.sourceValue = data[i][j];
                    c.targetValue = data[j][i];
                    c.sourceRadius = sourceRadius;
                    c.targetRadius = targetRadius;
                    c.sourceStartAngle = rowSegment.getStartAngle() + sourceOffsets[i][j];
                    c.sourceEndAngle = rowSegment.getStartAngle() + sourceOffsets[i][j + 1];
                    c.targetStartAngle = columnSegment.getStartAngle() + targetOffsets[j][i];
                    c.targetEndAngle = columnSegment.getStartAngle() + targetOffsets[j][i + 1];
//                    sourceStartAngle+=sourceOffsets[i][j];
                    try {
                        connectionSegments.add(c.createRibbon());
                    } catch (Exception | Error e) {
                        System.err.println("Could not create ribbon for " + i + ", " + j + " = " + data[i][j]);
                    }
                }
            }
        }
        return connectionSegments;
    }

    public class Connection {

        int sourceIndex, targetIndex;
        Segment source, target;
        double sourceValue, targetValue;
        double centerx, centery;
        double sourceRadius, targetRadius;
        double sourceStartAngle, sourceEndAngle;
        double targetStartAngle, targetEndAngle;
        Color fill, outline;
        boolean colorBySource = false;

        public Ribbon createRibbon() {

            Ribbon connection = new Ribbon(
                    targetRadius, sourceRadius, centerx, centery,
                    source, target,
                    sourceStartAngle, sourceEndAngle,
                    targetStartAngle, targetEndAngle);
            if (colorBySource) {
                connection.setFill(source.getFill());
                connection.setOutline(source.getOutline());
            } else {
                connection.setFill(target.getFill());
                connection.setOutline(target.getOutline());
            }
            return connection;
        }
    }
}
