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

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 *
 * @author Nils Hoffmann
 */
public interface IDrawable<T> extends Shape {

    public void draw(Graphics2D g2);

    public void setFill(Paint color);

    public Paint getFill();

    public void setOutline(Paint color);

    public Paint getOutline();

    public boolean isDirty();

    public Shape select(AffineTransform at, Point2D point);

}
