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

/**
 *
 * @author Nils Hoffmann
 */
public class PolarCoordinates {

    public static double toCoordX(double radius, double theta, double centerx) {
        if (theta > 0) {
            return centerx + (radius * Math.cos(theta));
        }
        return centerx + (radius * Math.cos(-theta));
    }

    public static double toCoordY(double radius, double theta, double centery) {
        if (theta > 0) {
            return centery + (radius * Math.sin(theta));
        }
        return centery + (radius * Math.sin(-theta));
    }

    public static double toCircleCoordX(double radius, double x, double centerx) {
        return centerx + (radius * Math.cos(2 * x * Math.PI));
    }

    public static double toCircleCoordY(double radius, double y, double centery) {
        return centery + (radius * Math.sin(2 * y * Math.PI));
    }

    public static double toDeg(double angle) {
        return Math.toDegrees(2 * Math.PI * angle);
    }
}
