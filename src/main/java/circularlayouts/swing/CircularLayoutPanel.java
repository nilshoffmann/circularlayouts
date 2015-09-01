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
import circularlayouts.layout.RatioLayoutBuilder;
import circularlayouts.tracks.Track;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.dom.GenericDOMImplementation;
import org.jdesktop.core.animation.rendering.JRenderer;
import org.jdesktop.core.animation.rendering.JRendererTarget;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.KeyFrames;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.swing.animation.rendering.JActiveRenderer;
import org.jdesktop.swing.animation.rendering.JRendererFactory;
import org.jdesktop.swing.animation.rendering.JRendererPanel;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 *
 * @author Nils Hoffmann
 */
public class CircularLayoutPanel extends JComponent implements JRendererTarget<GraphicsConfiguration, Graphics2D>, MouseListener, MouseMotionListener, ComponentListener {

    private Map<String, Track<? extends IDrawable>> shapes = new LinkedHashMap<String, Track<? extends IDrawable>>();
    private List<Shape> hoverSelected = null;
    private Shape selection = null;
    private AffineTransform at = null;
    private double segmentSize = 45.0d;
    private ChartSettings chartSettings = new ChartSettings();
    private Paint selectionColor = Color.LIGHT_GRAY;
    private double angle = 0.0d, segmentAngle = 0.0d;
    private double sourceMargin = 0.0d;
    private double targetMargin = 5.0d;
    private double segmentMargin = 0.05d;
    private VolatilePaintingComponent painter = new VolatilePaintingComponent();
    private boolean useBuffer = true;
    private JRendererPanel rendererPanel;
    private JRenderer renderer;
    private Animator animator;
    private boolean suspend = true;

    private RatioLayoutBuilder layoutBuilder;

    public List<Double> buildKeyFrames(int numberOfKeyFrames) {
        Double[] frames = new Double[numberOfKeyFrames];
        for (int i = 0; i < numberOfKeyFrames; i++) {
            frames[i] = ((double) i + 1) * Math.PI * 2.0d / (double) numberOfKeyFrames;
        }
        return Arrays.asList(frames);
    }

    /**
     * Creates new form CircularLayoutComponent
     */
    public CircularLayoutPanel() {
        setLayout(new BorderLayout());
        rendererPanel = new JRendererPanel();
        add(rendererPanel, BorderLayout.CENTER);
        rendererPanel.setBackground(Color.white);
//        renderer = new JPassiveRenderer(rendererPanel, this, new SwingTimerTimingSource(500, TimeUnit.MILLISECONDS));
        renderer = JRendererFactory.getDefaultRenderer(rendererPanel, this, false);
        setPreferredSize(new Dimension(800, 600));
        setOpaque(true);
        setDoubleBuffered(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this);
        chartSettings.setInterpolation(ChartSettings.Interpolation.BILINEAR);
        chartSettings.setAntialiasing(ChartSettings.Antialiasing.ON);
        chartSettings.setFractionalMetrics(ChartSettings.FractionalMetrics.ON);
        chartSettings.setTextAntialiasing(ChartSettings.TextAntialiasing.DEFAULT);
        chartSettings.setDithering(ChartSettings.Dithering.DISABLE);
        chartSettings.setRendering(ChartSettings.Rendering.SPEED);
        KeyFrames.Builder<Double> builder = new KeyFrames.Builder<Double>(0.0d);
        builder.addFrames(buildKeyFrames(50));
        final KeyFrames<Double> frames = builder.build();
        TimingTarget tt = new TimingTargetAdapter() {
            @Override
            public void timingEvent(Animator source, double fraction) {
                angle = frames.getInterpolatedValueAt(fraction);
                renderer.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        repaint();
                    }
                });
            }
        };
        animator = new Animator.Builder(renderer.getTimingSource()).setDuration(5, TimeUnit.SECONDS).setRepeatCount(Animator.INFINITE).setRepeatBehavior(Animator.RepeatBehavior.LOOP).addTarget(tt).build();
        animator.start();
        animator.pause();
    }

    public ChartSettings getChartSettings() {
        return chartSettings;
    }

    public void setData(double[][] data) {
        layoutBuilder = new RatioLayoutBuilder(data);
        rebuildShapes();
    }

    public boolean isSuspend() {
        return suspend;
    }

    public void setSuspend(boolean suspend) {
        this.suspend = suspend;
        if (suspend) {
            animator.pause();
        } else {
            animator.resume();
        }
        renderer.invokeLater(new Runnable() {

            @Override
            public void run() {
                repaint();
            }
        });
    }

    public Point2D getMinDistPoint(Point2D reference, Point2D... other) {
        Point2D best = null;
        double minDist = Double.POSITIVE_INFINITY;
        for (Point2D p : other) {
            double dist = reference.distance(p);
            if (dist < minDist) {
                best = p;
                minDist = dist;
            }
        }
        return best;
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        rebuildShapes();
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        rebuildShapes();
    }

    public void setSegmentAngle(double segmentAngle) {
        this.segmentAngle = segmentAngle;
        rebuildShapes();
    }

    private void rebuildShapes() {
//        System.out.println("Rebuilding shapes");
        double centerx = getWidth() / 2.0d;
        double centery = getHeight() / 2.0d;
        double rx = (Math.min(getWidth(), getHeight()) / 2.0d) - (Math.min(getWidth(), getHeight()) / 10.0d);
        shapes.clear();
        layoutBuilder.addTracks(segmentAngle, rx, centerx, centery, segmentSize, targetMargin, segmentMargin, sourceMargin, shapes);
        painter.clear();
        painter.setBounds(getBounds());
        for (Track<? extends IDrawable> t : shapes.values()) {
            painter.add(t);
//            painter.addAll(t.getChildren());
        }
        renderer.invokeLater(new Runnable() {

            @Override
            public void run() {
                repaint();
            }
        });
    }

    public void saveToSVG(File f) {
        // Get a DOMImplementation.
        DOMImplementation domImpl
                = GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // Ask the test to render into the SVG Graphics2D implementation.
        painter.draw(svgGenerator, false);

        // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes
        Writer out;
        try {
            out = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
            svgGenerator.stream(out, useCSS);
        } catch (SVGGraphics2DIOException ex) {
            Logger.getLogger(CircularLayoutPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CircularLayoutPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CircularLayoutPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void renderSetup(GraphicsConfiguration d) {
    }

    @Override
    public void renderUpdate() {
        renderer.invokeLater(new Runnable() {

            @Override
            public void run() {
                repaint();
            }
        });
    }

    @Override
    public void render(Graphics2D g, int width, int height) {
        Insets insets = getInsets();
//		int width = getWidth() - insets.left - insets.right;
//		int height = getHeight() - insets.top - insets.bottom;
        Shape clip = g.getClip();
        g.setClip(new Rectangle(insets.left, insets.top, width, height));
        Graphics2D g2 = (Graphics2D) g.create();
        chartSettings.applySettings(g2);
        g2.setColor(Color.WHITE);
        g2.fillRect(insets.left, insets.top, width, height);
//        g2.setColor(Color.BLUE);
        AffineTransform original = g2.getTransform();
        at = AffineTransform.getRotateInstance(angle, width / 2.0d, height / 2.0d);
        g2.setTransform(at);
        Composite originalComposite = g2.getComposite();
        Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f);
        g2.setComposite(comp);
        if (shapes == null || shapes.isEmpty()) {
//            System.out.println("Rebuilding shapes!");
//            System.out.println("Shapes: " + shapes);
            rebuildShapes();
        } else {
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//            System.out.println("Painting!");
            painter.draw(g2, useBuffer);
            g2.setColor(Color.BLACK);
        }
        if (hoverSelected != null) {
            g2.setTransform(original);
            for (Shape s : hoverSelected) {
                Shape transformed = at.createTransformedShape(s);
                comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.66f);
                g2.setComposite(comp);
                g2.setPaint(selectionColor);
                g2.fill(transformed);
                if (selectionColor instanceof Color) {
                    g2.setPaint(((Color) selectionColor).darker());
                }
                g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(transformed);
            }
        }
        if (selection != null) {
            g2.setTransform(original);
            Shape transformed = at.createTransformedShape(selection);
            comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.66f);
            g2.setComposite(comp);
            g2.setPaint(selectionColor);
            g2.fill(transformed);
            if (selectionColor instanceof Color) {
                g2.setPaint(((Color) selectionColor).darker());
            }
            g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(transformed);
        }
        int i = 0;
        g2.setColor(Color.BLACK);
        g2.setTransform(at);
        g2.setTransform(original);
        g.setClip(clip);
        g2.dispose();
    }

    @Override
    public void renderShutdown() {
    }

    @Override
    public void mouseClicked(final MouseEvent me) {
        if (shapes != null && at != null) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    int x = me.getX();
                    int y = me.getY();
                    Point2D p = new Point2D.Double(x, y);
                    selection = null;
                    //simple linear hit test
                    //all hits are highlighted
                    for (IDrawable id : shapes.values()) {
                        Shape s = id.select(at, p);
                        if (s != null) {
                            selection = s;
                            break;
                        }
                    }
                    if (selection != null) {
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                        if (!suspend) {
                            animator.pause();
                        }
                    } else {
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        if (!suspend) {
                            animator.resume();
                        }
                    }
                    repaint();
                }
            };
            renderer.invokeLater(r);
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void mouseDragged(MouseEvent me) {
    }

    @Override
    public void mouseMoved(final MouseEvent me) {
        if (shapes != null && at != null) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    int x = me.getX();
                    int y = me.getY();
                    Point2D p = new Point2D.Double(x, y);
                    hoverSelected = new ArrayList<Shape>();
                    //simple linear hit test
                    //all hits are highlighted
                    for (IDrawable id : shapes.values()) {
                        Shape s = id.select(at, p);
                        if (s != null) {
                            hoverSelected.add(s);
                        }
                    }
                    if (!hoverSelected.isEmpty() || selection != null) {
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                        if (!suspend) {
                            animator.pause();
                        }
                    } else {
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        if (!suspend) {
                            animator.resume();
                        }
                    }
                    repaint();
                }
            };
            renderer.invokeLater(r);
        }
    }

    @Override
    public void componentResized(ComponentEvent ce) {
        rebuildShapes();
    }

    @Override
    public void componentMoved(ComponentEvent ce) {
    }

    @Override
    public void componentShown(ComponentEvent ce) {
        rebuildShapes();
    }

    @Override
    public void componentHidden(ComponentEvent ce) {
    }

    public void setTargetMargin(double margin) {
        this.targetMargin = margin;
        rebuildShapes();
    }

    public double getTargetMargin() {
        return targetMargin;
    }

    public void setSourceMargin(double margin) {
        this.sourceMargin = margin;
        rebuildShapes();
    }

    public double getSourceMargin() {
        return sourceMargin;
    }

    public void setSegmentMargin(double margin) {
        this.segmentMargin = margin;
        rebuildShapes();
    }

    public double getSegmentMargin() {
        return segmentMargin;
    }

    public void setSegmentSize(double segmentSize) {
        this.segmentSize = segmentSize;
        rebuildShapes();
    }

    public double getSegmentSize() {
        return segmentSize;
    }

}
