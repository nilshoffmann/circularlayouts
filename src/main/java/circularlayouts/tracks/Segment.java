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
package circularlayouts.tracks;

import circularlayouts.IDrawable;
import circularlayouts.PolarCoordinates;
import static circularlayouts.layout.RatioLayoutBuilder.createCurvedSegment;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Nils Hoffmann
 */
public class Segment implements IDrawable {

    private Paint fill = Color.BLUE;
    private Paint outline = Color.DARK_GRAY;
    private final double innerRadius;
    private final double outerRadius;
    private double centerx;
    private double centery;
    private final double startAngle;
    private final double endAngle;
    private boolean dirty = false;
    private String name = "";
    private Shape s;

    public Segment(String name, double innerRadius, double outerRadius, double centerx, double centery, double startAngle, double endAngle, Paint fill, Paint outline) {
        this.name = name;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.centerx = centerx;
        this.centery = centery;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        s = createCurvedSegment(startAngle, endAngle, innerRadius, outerRadius, centerx, centery);
        this.fill = fill;
        this.outline = outline;
    }

    public double getInnerRadius() {
        return innerRadius;
    }

    public double getOuterRadius() {
        return outerRadius;
    }

    public double getCenterx() {
        return centerx;
    }

    public double getCentery() {
        return centery;
    }

    public double getStartAngle() {
        return startAngle;
    }

    public double getEndAngle() {
        return endAngle;
    }

    @Override
    public Paint getFill() {
        return this.fill;
    }

    @Override
    public void setFill(Paint color) {
        this.fill = color;
        this.dirty = true;
    }

    @Override
    public Paint getOutline() {
        return this.outline;
    }

    @Override
    public void setOutline(Paint color) {
        this.outline = color;
        this.dirty = true;
    }

    public Shape getShape() {
        return this.s;
    }

    public void setCenterx(double centerx) {
        this.centerx = centerx;
        s = createCurvedSegment(startAngle, endAngle, innerRadius, outerRadius, centerx, centery);
        this.dirty = true;
    }

    public void setCentery(double centery) {
        this.centery = centery;
        s = createCurvedSegment(startAngle, endAngle, innerRadius, outerRadius, centerx, centery);
        this.dirty = true;
    }

    @Override
    public Rectangle getBounds() {
        return s.getBounds();
    }

    @Override
    public Rectangle2D getBounds2D() {
        return s.getBounds2D();
    }

    @Override
    public boolean contains(double x, double y) {
        return s.contains(x, y);
    }

    @Override
    public boolean contains(Point2D p) {
        return s.contains(p);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        return s.intersects(x, y, w, h);
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        return s.intersects(r);
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        return s.contains(x, y, w, h);
    }

    @Override
    public boolean contains(Rectangle2D r) {
        return s.contains(r);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return s.getPathIterator(at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return s.getPathIterator(at, flatness);
    }

    @Override
    public void draw(Graphics2D g2) {
        Color current = g2.getColor();
        g2.setPaint(fill);
        g2.fill(s);
        g2.setPaint(outline);
        g2.draw(s);
        g2.setColor(Color.BLACK);
        double cwidth = g2.getFontMetrics().stringWidth(name);
        double cheight = 10.0d;
        double ccenterx = PolarCoordinates.toCoordX(
                outerRadius + ((outerRadius - innerRadius) / 2) + cwidth,
                (startAngle + (endAngle - startAngle) / 2) * Math.PI * 2.0d,
                centerx);
        double ccentery = PolarCoordinates.toCoordY(
                outerRadius + ((outerRadius - innerRadius) / 2) + cheight,
                (startAngle + (endAngle - startAngle) / 2) * Math.PI * 2.0d,
                centery);
        Rectangle2D.Double r = new Rectangle2D.Double(
                ccenterx,
                ccentery, 1.0d, 1.0d);
        r.setFrameFromCenter(ccenterx, ccentery, ccenterx + cwidth, ccentery + cheight);
        AffineTransform at = AffineTransform.getTranslateInstance(r.getCenterX(), r.getCenterY());
//		at.concatenate(AffineTransform.getRotateInstance((startAngle + (endAngle - startAngle) / 2) * Math.PI * 2.0d, ccenterx, ccentery));
        at.concatenate(AffineTransform.getTranslateInstance(-r.getCenterX(), -r.getCenterY()));
        AffineTransform ot = g2.getTransform();
        g2.setTransform(at);
//		g2.draw(r);
//		g2.drawString(name, (float) r.getMinX(), (float) (r.getMinY() + g2.getFontMetrics().getLineMetrics(name, g2).getDescent()));
        g2.setTransform(ot);
//		Shape s = at.createTransformedShape(r);
//		at = AffineTransform.getTranslateInstance();
//		at.concatenate(AffineTransform.getTranslateInstance(-s.getBounds2D().getWidth()/2.0d, -s.getBounds2D().getHeight()/2.0d));
//		s = at.createTransformedShape(s);
//		g2.fill(s);
        g2.setColor(current);
        dirty = false;
    }
//	public Point2D getPointClosestToReference(Point2D reference, Rectangle2D.Double r) {
//		
//	}

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public Shape select(AffineTransform at, Point2D point) {
        Shape transf = at.createTransformedShape(s);
        if (transf.contains(point.getX(), point.getY())) {
            return this;
        }
        return null;
    }
}
