/*
 * Copyright (C) 2017 by Herbert Roider <herbert@roider.at>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package contoursolveinterface;

/**
 *   /usr/java/jdk1.8.0_91/bin/javah contoursolveinterface.Contour
 *
 *
 * @author Herbert Roider <herbert@roider.at>
 */
public class Contour {

    static {
        System.loadLibrary("contoursolve");
    }

    public native int create();

    public native int addPoint(int handle, double x, int fx, double y, int fy);

    public native int addLine(int handle, double x, int fx, double y, int fy,
            double angle, int fangle,
            boolean tangent);

    public native int addArc(int handle, double x, int fx, double y, int fy,
            double center_x, int fcenter_x, double center_y, int fcenter_y,
            double rad, int frad,
            double start_angle, int fstart_angle,
            double end_angle, int fend_angle,
            boolean tangent);
    /**
     * 
     * @param handle
     * @return 0 success, 1 no Solution 
     */
    public native int solve(int handle);

    public native double[] getSolution(int handle, int index);
    
    public native int size(int handle);

}
