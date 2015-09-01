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

import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 *
 * @author Nils Hoffmann
 */
public class ChartSettings {

    public enum Antialiasing {

        DEFAULT, OFF, ON;

        public static void applyHint(Antialiasing a, Graphics2D g) {
            Object value = null;
            switch (a) {
                case OFF:
                    value = RenderingHints.VALUE_ANTIALIAS_OFF;
                    break;
                case ON:
                    value = RenderingHints.VALUE_ANTIALIAS_ON;
                    break;
                default:
                    value = RenderingHints.VALUE_ANTIALIAS_DEFAULT;
            }
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, value);
        }
    };

    public enum Interpolation {

        BICUBIC, BILINEAR, NEAREST_NEIGHBOR;

        public static void applyHint(Interpolation a, Graphics2D g) {
            Object value = null;
            switch (a) {
                case BICUBIC:
                    value = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
                    break;
                case BILINEAR:
                    value = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
                    break;
                default:
                    value = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
            }
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, value);
        }
    };

    public enum Rendering {

        DEFAULT, QUALITY, SPEED;

        public static void applyHint(Rendering a, Graphics2D g) {
            Object value = null;
            switch (a) {
                case QUALITY:
                    value = RenderingHints.VALUE_RENDER_QUALITY;
                    break;
                case SPEED:
                    value = RenderingHints.VALUE_RENDER_SPEED;
                    break;
                default:
                    value = RenderingHints.VALUE_RENDER_DEFAULT;
            }
            g.setRenderingHint(RenderingHints.KEY_RENDERING, value);
        }
    };

    public enum AlphaInterpolation {

        DEFAULT, QUALITY, SPEED;

        public static void applyHint(AlphaInterpolation a, Graphics2D g) {
            Object value = null;
            switch (a) {
                case QUALITY:
                    value = RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY;
                    break;
                case SPEED:
                    value = RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED;
                    break;
                default:
                    value = RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT;
            }
            g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, value);
        }
    };

    public enum TextAntialiasing {

        DEFAULT, GASP;

        public static void applyHint(TextAntialiasing a, Graphics2D g) {
            Object value = null;
            switch (a) {
                case GASP:
                    value = RenderingHints.VALUE_TEXT_ANTIALIAS_GASP;
                    break;
                default:
                    value = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
            }
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, value);
        }
    };

    public enum FractionalMetrics {

        DEFAULT, OFF, ON;

        public static void applyHint(FractionalMetrics a, Graphics2D g) {
            Object value = null;
            switch (a) {
                case OFF:
                    value = RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
                    break;
                case ON:
                    value = RenderingHints.VALUE_FRACTIONALMETRICS_ON;
                    break;
                default:
                    value = RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT;
            }
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, value);
        }
    };

    public enum Dithering {

        DEFAULT, DISABLE, ENABLE;

        public static void applyHint(Dithering a, Graphics2D g) {
            Object value = null;
            switch (a) {
                case DISABLE:
                    value = RenderingHints.VALUE_DITHER_DISABLE;
                    break;
                case ENABLE:
                    value = RenderingHints.VALUE_DITHER_ENABLE;
                    break;
                default:
                    value = RenderingHints.VALUE_DITHER_DEFAULT;
            }
            g.setRenderingHint(RenderingHints.KEY_DITHERING, value);
        }
    };
    private Antialiasing antialiasing = Antialiasing.DEFAULT;
    private Interpolation interpolation = Interpolation.NEAREST_NEIGHBOR;
    private Rendering rendering = Rendering.DEFAULT;
    private AlphaInterpolation alphaInterpolation = AlphaInterpolation.DEFAULT;
    private TextAntialiasing textAntialiasing = TextAntialiasing.DEFAULT;
    private FractionalMetrics fractionalMetrics = FractionalMetrics.DEFAULT;
    private Dithering dithering = Dithering.DEFAULT;

    public ChartSettings() {
    }

    public ChartSettings(ChartSettings settings) {
        this.antialiasing = settings.antialiasing;
        this.interpolation = settings.interpolation;
        this.rendering = settings.rendering;
        this.alphaInterpolation = settings.alphaInterpolation;
        this.textAntialiasing = settings.textAntialiasing;
        this.fractionalMetrics = settings.fractionalMetrics;
        this.dithering = settings.dithering;
    }

    public Antialiasing getAntialiasing() {
        return antialiasing;
    }

    public void setAntialiasing(Antialiasing antialiasing) {
        this.antialiasing = antialiasing;
    }

    public Interpolation getInterpolation() {
        return interpolation;
    }

    public void setInterpolation(Interpolation interpolation) {
        this.interpolation = interpolation;
    }

    public Rendering getRendering() {
        return rendering;
    }

    public void setRendering(Rendering rendering) {
        this.rendering = rendering;
    }

    public AlphaInterpolation getAlphaInterpolation() {
        return alphaInterpolation;
    }

    public void setAlphaInterpolation(AlphaInterpolation alphaInterpolation) {
        this.alphaInterpolation = alphaInterpolation;
    }

    public TextAntialiasing getTextAntialiasing() {
        return textAntialiasing;
    }

    public void setTextAntialiasing(TextAntialiasing textAntialiasing) {
        this.textAntialiasing = textAntialiasing;
    }

    public FractionalMetrics getFractionalMetrics() {
        return fractionalMetrics;
    }

    public void setFractionalMetrics(FractionalMetrics fractionalMetrics) {
        this.fractionalMetrics = fractionalMetrics;
    }

    public Dithering getDithering() {
        return dithering;
    }

    public void setDithering(Dithering dithering) {
        this.dithering = dithering;
    }

    public void applySettings(Graphics2D g) {
        Antialiasing.applyHint(antialiasing, g);
        Interpolation.applyHint(interpolation, g);
        Rendering.applyHint(rendering, g);
        AlphaInterpolation.applyHint(alphaInterpolation, g);
        TextAntialiasing.applyHint(textAntialiasing, g);
        FractionalMetrics.applyHint(fractionalMetrics, g);
        Dithering.applyHint(dithering, g);
    }

}
