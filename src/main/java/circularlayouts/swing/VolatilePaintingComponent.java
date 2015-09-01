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
package circularlayouts.swing;

import circularlayouts.IDrawable;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.ImageCapabilities;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Nils Hoffmann
 */
public class VolatilePaintingComponent implements Shape {

    private VolatileImage buffer;
    private TexturePaint tp;
    private GraphicsConfiguration graphicsConfiguration;
    private List<IDrawable> drawables = new ArrayList<IDrawable>();
    private boolean dirty = true;
    private Shape bounds;

    public boolean addAll(Collection<? extends IDrawable> c) {
        return drawables.addAll(c);
    }

    @Override
    public boolean contains(double x, double y) {
        return bounds.contains(x, y);
    }

    @Override
    public boolean contains(Point2D p) {
        return bounds.contains(p);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        return bounds.intersects(x, y, w, h);
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        return bounds.intersects(r);
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        return bounds.contains(x, y, w, h);
    }

    @Override
    public boolean contains(Rectangle2D r) {
        return bounds.contains(r);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return bounds.getPathIterator(at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return bounds.getPathIterator(at, flatness);
    }

    public boolean add(IDrawable e) {
        boolean b = drawables.add(e);
        dirty = true;
        return b;
    }

    public boolean remove(Object o) {
        if (o instanceof IDrawable) {
            boolean b = drawables.remove((IDrawable) o);
            dirty = true;
            return b;
        }
        return false;
    }

    public void clear() {
        drawables.clear();
        dirty = true;
    }

    @Override
    public Rectangle getBounds() {
        return bounds.getBounds();
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
        dirty = true;
    }

    @Override
    public Rectangle getBounds2D() {
        return bounds.getBounds();
    }

    public void setBounds2D(Rectangle2D bounds) {
        this.bounds = bounds;
        dirty = true;
    }

    public GraphicsConfiguration getGraphicsConfiguration() {
        return graphicsConfiguration;
    }

    public void setGraphicsConfiguration(GraphicsConfiguration graphicsConfiguration) {
        this.graphicsConfiguration = graphicsConfiguration;
    }

    public void draw(Graphics2D g, boolean useBuffer) {
        if (!useBuffer) {
            paintContent(g);
            return;
        }
        if (graphicsConfiguration == null) {
            setGraphicsConfiguration(g.getDeviceConfiguration());
        }
        if (buffer == null || dirty) {
            createBuffer();
        }

        do {
            GraphicsConfiguration gc = graphicsConfiguration;
            int valCode = buffer.validate(gc);
            if (valCode == VolatileImage.IMAGE_INCOMPATIBLE) {
                createBuffer();
            }
            if (dirty) {
                Graphics2D offscreenGraphics = buffer.createGraphics();
                offscreenGraphics.setPaint(g.getPaint());
                offscreenGraphics.setColor(g.getColor());
                offscreenGraphics.setStroke(g.getStroke());
                offscreenGraphics.setBackground(g.getBackground());
                offscreenGraphics.setClip(g.getClip());
                offscreenGraphics.setComposite(g.getComposite());
                offscreenGraphics.setFont(g.getFont());
                offscreenGraphics.setRenderingHints(g.getRenderingHints());
                paintContent(offscreenGraphics);
                tp = new TexturePaint(buffer.getSnapshot(), new Rectangle2D.Double(0, 0, buffer.getWidth(), buffer.getHeight()));
                offscreenGraphics.dispose();
            }
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
            // paint from buffer as texture paint for edge anti-aliasing of image
            g.setPaint(tp);
            g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
        } while (buffer.contentsLost());
    }

    protected void paintContent(Graphics2D g2) {
//        System.out.println("Creating content!");
        for (IDrawable drawable : drawables) {
            drawable.draw(g2);
        }
        dirty = false;
    }

    // This method produces a new volatile image.
    private void createBuffer() {
        GraphicsConfiguration gc = getGraphicsConfiguration();
        //avoid negative size exception due to invalid bounds
        int width = Math.max(1, getBounds().width);
        int height = Math.max(1, getBounds().height);
        try {
            buffer = gc.createCompatibleVolatileImage(width, height, new ImageCapabilities(true), Transparency.TRANSLUCENT);
        } catch (AWTException ex) {
            try {
                buffer = gc.createCompatibleVolatileImage(width, height, new ImageCapabilities(true), Transparency.OPAQUE);
            } catch (AWTException ex1) {
                buffer = gc.createCompatibleVolatileImage(width, height, Transparency.OPAQUE);
            }
        }
        dirty = true;
    }
}
