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

/**
 *
 * @author Nils Hoffmann
 */
public abstract class SegmentNormalizer {

    public abstract double getAngleForSegment(double[][] data, int segment);

    public static final class Equal extends SegmentNormalizer {

        @Override
        public double getAngleForSegment(double[][] data, int segment) {
            return 1.0d / (double) data.length;
        }

    }

    public static final class ProportionalToRowSum extends SegmentNormalizer {

        @Override
        public double getAngleForSegment(double[][] data, int segment) {
            double allSum = 0.0d;
            for (int i = 0; i < data.length; i++) {
                allSum += Utils.getRowSum(data, i);
            }
            double rowSum = Utils.getRowSum(data, segment);
            return rowSum / allSum;
        }
    }

    public static final class ProportionalToColumnSum extends SegmentNormalizer {

        @Override
        public double getAngleForSegment(double[][] data, int segment) {
            double allSum = 0.0d;
            for (int i = 0; i < data[0].length; i++) {
                allSum += Utils.getColumnSum(data, i);
            }
            double colSum = Utils.getColumnSum(data, segment);
            return colSum / allSum;
        }
    }
}
