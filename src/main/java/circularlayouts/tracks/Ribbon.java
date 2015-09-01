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
import static circularlayouts.PolarCoordinates.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A ribbon connects two separate parts either within one Segment or between two
 * segments.
 *
 * @author Nils Hoffmann
 */
public class Ribbon implements IDrawable {

    private Paint fill = Color.BLUE;
    private Paint outline = Color.DARK_GRAY;
    private Shape s;
    private Rectangle2D bounds;
    private IDrawable sourceConnector;
    private IDrawable targetConnector;
    private Segment source;
    private Segment target;
    private boolean dirty;
    private boolean selected;

    public Ribbon(double sourceRadius, double targetRadius, double centerx, double centery, double localStartAngle0, double localEndAngle0, double localStartAngle1, double localEndAngle1) {
        s = createConnectionSegmentShape(sourceRadius, targetRadius, centerx, centery, localStartAngle0, localEndAngle0, localStartAngle1, localEndAngle1);
    }

    public Ribbon(double sourceRadius, double targetRadius, double centerx, double centery, Segment source, Segment target, double sourceStartAngle, double sourceEndAngle, double targetStartAngle, double targetEndAngle) {
        this.source = source;
        this.target = target;
        s = createConnectionSegmentShape(sourceRadius, targetRadius - 10, centerx, centery, sourceStartAngle, sourceEndAngle, targetStartAngle, targetEndAngle);
        sourceConnector = new Segment("s", targetRadius - 10, targetRadius, centerx, centery, sourceStartAngle, sourceEndAngle, this.target.getFill(), this.target.getOutline());
//        targetConnector = new Segment("t", sourceRadius, targetRadius-absRad, centerx, centery, targetStartAngle, targetEndAngle, this.source.getFill(), this.source.getOutline());
        bounds = s.getBounds2D();
        bounds.add(sourceConnector.getBounds2D());
//        bounds.add(targetConnector.getBounds2D());
    }

    public Ribbon(double sourceRadius, double targetRadius, double centerx, double centery, Segment source, Segment target) {
        this.source = source;
        this.target = target;
        s = createConnectionSegmentShape(sourceRadius, targetRadius, centerx, centery, source.getStartAngle(), source.getEndAngle(), target.getStartAngle(), target.getEndAngle());
        sourceConnector = new Segment("s", sourceRadius, targetRadius, centerx, centery, source.getStartAngle(), source.getEndAngle(), this.target.getFill(), this.target.getOutline());
//        targetConnector = new Segment("t", sourceRadius+absRad, targetRadius-absRad, centerx, centery, target.getStartAngle(), target.getEndAngle(), this.source.getFill(), this.source.getOutline());
        bounds = s.getBounds2D();
        bounds.add(sourceConnector.getBounds2D());
//        bounds.add(targetConnector.getBounds2D());
    }

    public void setSourceSegment(Segment source) {
        this.source = source;
        this.dirty = true;
    }

    public void setTargetSegment(Segment target) {
        this.target = target;
        this.dirty = true;
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

    public static Shape createConnectionSegmentShape(double sourceRadius, double targetRadius, double centerx, double centery, double sourceStartAngle, double sourceEndAngle, double targetStartAngle, double targetEndAngle) {
        GeneralPath gp = new GeneralPath();
        Arc2D arc1 = new Arc2D.Double();
        arc1.setArcByCenter(centerx, centery, targetRadius, toDeg(sourceStartAngle), toDeg(sourceEndAngle) - toDeg(sourceStartAngle), Arc2D.OPEN);
        Arc2D arc2 = new Arc2D.Double();
        arc2.setArcByCenter(centerx, centery, sourceRadius, toDeg(targetStartAngle), toDeg(targetEndAngle) - toDeg(targetStartAngle), Arc2D.OPEN);
        gp.moveTo(arc1.getEndPoint().getX(), arc1.getEndPoint().getY());
        gp.quadTo(centerx, centery, arc2.getStartPoint().getX(), arc2.getStartPoint().getY());
        gp.lineTo(arc2.getEndPoint().getX(), arc2.getEndPoint().getY());
        gp.quadTo(centerx, centery, arc1.getStartPoint().getX(), arc1.getStartPoint().getY());
        gp.lineTo(arc1.getEndPoint().getX(), arc1.getEndPoint().getY());
        gp.append(arc1, false);
        gp.append(arc2, false);
        gp.closePath();
        return new Area(gp);
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
        if (sourceConnector != null) {
            sourceConnector.draw(g2);
        }
        if (targetConnector != null) {
            targetConnector.draw(g2);
        }
        g2.setColor(current);
        dirty = false;
    }

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
        if (sourceConnector != null) {
            transf = at.createTransformedShape(sourceConnector);
            if (transf.contains(point.getX(), point.getY())) {
                return this;
            }
        }
        if (targetConnector != null) {
            transf = at.createTransformedShape(targetConnector);
            if (transf.contains(point.getX(), point.getY())) {
                return this;
            }
        }
        return null;
    }
}
