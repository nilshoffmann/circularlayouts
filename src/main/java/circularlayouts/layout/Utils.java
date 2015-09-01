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
public class Utils {

    public static double rowCountTo(double[][] data, int row, int toColumn) {
        double sum = 0.0d;
        for (int i = 0; i <= toColumn; i++) {
            if (!Double.isNaN(data[row][i])) {
                sum++;
            }
        }
        return sum;
    }

    public static double colCountTo(double[][] data, int column, int toRow) {
        double sum = 0.0d;
        for (int i = 0; i <= toRow; i++) {
            if (!Double.isNaN(data[i][column])) {
                sum++;
            }
        }
        return sum;
    }

    public static double rowSumTo(double[][] data, int row, int toColumn) {
        double sum = 0.0d;
        for (int i = 0; i <= toColumn; i++) {
            if (!Double.isNaN(data[row][i])) {
                sum += data[row][i];
            }
        }
        return sum;
    }

    public static double colSumTo(double[][] data, int column, int toRow) {
        double sum = 0.0d;
        for (int i = 0; i <= toRow; i++) {
            if (!Double.isNaN(data[i][column])) {
                sum += data[i][column];
            }
        }
        return sum;
    }

    public static double getRowSum(double[][] data, int row) {
        double sum = 0.0d;
        for (int i = 0; i < data[row].length; i++) {
            if (!Double.isNaN(data[row][i])) {
                sum += data[row][i];
            }
        }
        return sum;
    }

    public static double getRowCount(double[][] data, int row) {
        double sum = 0.0d;
        for (int i = 0; i < data[row].length; i++) {
            if (!Double.isNaN(data[row][i])) {
                sum += 1;
            }
        }
        return sum;
    }

    public static double getColumnCount(double[][] data, int column) {
        double sum = 0.0d;
        for (int i = 0; i < data.length; i++) {
            if (!Double.isNaN(data[i][column])) {
                sum += 1;
            }
        }
        return sum;
    }

    public static double getColumnSum(double[][] data, int column) {
        double sum = 0.0d;
        for (int i = 0; i < data.length; i++) {
            if (!Double.isNaN(data[i][column])) {
                sum += data[i][column];
            }
        }
        return sum;
    }
}
