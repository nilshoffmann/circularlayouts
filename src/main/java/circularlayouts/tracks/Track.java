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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * A track is a container for drawable objects that logically belong to one
 * layer / level of the plot.
 *
 * @author Nils Hoffmann
 * @param <T> a track for a specific sub type of {@link IDrawable}
 */
public class Track<T extends IDrawable> implements IDrawable {

    private final List<T> drawables;
    private final Shape shape;
    private Paint fill;
    private Paint outline;

    public Track(Point2D.Double center, double innerRadius, double outerRadius, List<T> drawables) {
        this.drawables = drawables;
        this.shape = createShape(center, innerRadius, outerRadius);
    }

    public List<T> getChildren() {
        return this.drawables;
    }

    private Shape createShape(Point2D.Double center, double innerRadius, double outerRadius) {
        Shape outer = new Ellipse2D.Double(center.x - outerRadius, center.y - outerRadius, outerRadius * 2, outerRadius * 2);
        Shape inner = new Ellipse2D.Double(center.x - innerRadius, center.y - innerRadius, innerRadius * 2, innerRadius * 2);
        Area a = new Area(outer);
        a.subtract(new Area(inner));
        return a;
    }

    @Override
    public void draw(Graphics2D g2) {
//        System.out.println("Repainting track");
        g2.setPaint(fill);
        g2.fill(this);
        g2.setPaint(outline);
        g2.fill(this);

        for (T t : drawables) {
            t.draw(g2);
        }
    }

    @Override
    public void setFill(Paint paint) {
        this.fill = paint;
    }

    @Override
    public Paint getFill() {
        return fill;
    }

    @Override
    public void setOutline(Paint color) {
        this.outline = color;
    }

    @Override
    public Paint getOutline() {
        return outline;
    }

    @Override
    public Rectangle getBounds() {
        return shape.getBounds();
    }

    @Override
    public Rectangle2D getBounds2D() {
        return shape.getBounds2D();
    }

    @Override
    public boolean contains(double x, double y) {
        return shape.contains(x, y);
    }

    @Override
    public boolean contains(Point2D p) {
        return shape.contains(p);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        return shape.intersects(x, y, w, h);
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        return shape.intersects(r);
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        return shape.contains(x, y, w, h);
    }

    @Override
    public boolean contains(Rectangle2D r) {
        return shape.contains(r);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return shape.getPathIterator(at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return shape.getPathIterator(at, flatness);
    }

    @Override
    public boolean isDirty() {
//		for(IDrawable d:this) {
//			if(d.isDirty()) {
//				return true;
//			}
//		}
        return false;
    }

    @Override
    public Shape select(AffineTransform at, Point2D point) {
        for (IDrawable d : drawables) {
            Shape s = d.select(at, point);
            if (s != null) {
                return s;
            }
        }
        return null;
    }

}
