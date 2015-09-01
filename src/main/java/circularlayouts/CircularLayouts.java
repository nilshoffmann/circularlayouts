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
package circularlayouts;

import circularlayouts.swing.CircularLayoutComponent;
import java.awt.BorderLayout;
import java.awt.BufferCapabilities;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.ImageCapabilities;
import java.awt.image.ColorModel;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author Nils Hoffmann
 */
public class CircularLayouts {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
//                System.setProperty("sun.java2d.opengl", "True");
                GraphicsConfiguration[] gcs = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getConfigurations();
                int counter = 0;
                for (GraphicsConfiguration graphicsConfiguration : gcs) {
                    System.out.println("############################################");
                    System.out.println("Graphics Configuration " + counter++);
                    BufferCapabilities bc = graphicsConfiguration.getBufferCapabilities();
                    System.out.println("Buffer capabilities: page flip: " + bc.isPageFlipping() + " | multi buffer: " + bc.isMultiBufferAvailable() + " | full screen required: " + bc.isFullScreenRequired());
                    ImageCapabilities frontBuffer = bc.getFrontBufferCapabilities();
                    System.out.println("Front buffer image capabilities: accelerated: " + frontBuffer.isAccelerated() + " | true volatile: " + frontBuffer.isTrueVolatile());
                    ImageCapabilities backBuffer = bc.getBackBufferCapabilities();
                    System.out.println("Back buffer image capabilities: accelerated: " + backBuffer.isAccelerated() + " | true volatile: " + backBuffer.isTrueVolatile());
                    ColorModel cm = graphicsConfiguration.getColorModel(ColorModel.TRANSLUCENT);
                    System.out.println("Color model: has alpha: " + cm.hasAlpha() + " | " + cm);
                    GraphicsDevice gd = graphicsConfiguration.getDevice();
                    System.out.println("Graphics Device: accelerated memory: " + gd.getAvailableAcceleratedMemory() + " | " + gd);
                    ImageCapabilities ic = graphicsConfiguration.getImageCapabilities();
                    System.out.println("Image capabilities: accelerated: " + ic.isAccelerated() + " | true volatile: " + ic.isTrueVolatile());
                    System.out.println("############################################");
                }
                System.out.println("Available accelerated memory: " + GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getAvailableAcceleratedMemory());
                JFrame jf = new JFrame();
                jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                jf.setLayout(new BorderLayout());
                CircularLayoutComponent panel = new CircularLayoutComponent();
                panel.setData(createDistanceMatrix());
                jf.add(panel, BorderLayout.CENTER);
                jf.setLocationRelativeTo(null);
                jf.setVisible(true);
                jf.pack();
            }
        });
    }

    public static double[][] createDistanceMatrix() {
        int dims = 3;
        Random rg = new Random(891236l);
        double[][] data = new double[dims][dims];
        for (int i = 0; i < dims; i++) {
            data[i] = new double[dims];
            for (int j = 0; j < dims; j++) {
//                if(i==j) {
//                    data[i][j] = 0;
//                }else{
//                    double rand = rg.nextDouble();
//                    if (rand > 0.99) {
//                    if ((i==0 || i==3) && (j==1||j==2)) {
                if (Math.random() > 0.2) {
//                    if (data[i][j] == 0) {
                    data[i][j] = 5000000 * Math.random();
//                    data[j][i] = data[i][j];
                }else{
//                    data[i][j] = Double.NaN;
//                    data[j][i] = Double.NaN;
                }
//                    if (data[j][i] == 0) {
//                        data[j][i] = 5000000 * Math.random();
//                    }
//                        data[j][i] = Math.random();
//                        data[j][i] = 1.0d-data[i][j];
//                    }else{
//                        data[i][j] = 0;
//                        data[j][i] = 0;
//                    }
//                }
//                }
            }
        }
        System.out.println("Data is ready!");
        return data;
    }
}
