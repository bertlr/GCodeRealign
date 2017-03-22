/*
 * Copyright (C) 2014 by Herbert Roider <herbert@roider.at>
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
package org.roiderh.gcodeviewer;

import java.util.LinkedList;
import math.geom2d.Point2D;
import math.geom2d.circulinear.CirculinearElement2D;

/**
 *
 * @author Herbert Roider <herbert@roider.at>
 */
public class contourelement {
    // shape of the transition element:

    public enum Transition {
        CHAMFER, ROUND
    }
    // G0/G1 for lines and G2/G3 for arc

    public enum Shape {
        LINE, ARC
    }

    public enum Feed {
        RAPID, CUTTING
    }
    // only x=1 horizontal, only y=2 vertical
    public int axis_movement = 0;

    public enum Freedom {
        // See file: ConstraintedValue.h:
        FIXED(0), FREE(1);
        private int value;
        private Freedom(int _value) {
            this.value = _value;
        }
        private Freedom(boolean _value){
            if(_value) 
                this.value=1;
            else
                this.value=0;
            
        }
        public boolean getBoolValue(){
            if(value >0){
                return true;
            }              
            return false;
        }
        public int getIntValue(){
            return value;
        }
    }

    // holds the start and end point without transition element:
    public LinkedList<point> points = new LinkedList<>();
    // calculated start point with transition element:
    public Point2D start;
    // calculated end point with transition element:
    public Point2D end;
    // Transition Element chamfer or round between the current and the next element
    public Transition transistion_elem;
    // line or arc
    public Shape shape;
    public double transition_elem_size;
    // only rapid (G0) or cutting (G1)
    public Feed feed;
    // the real linenumber, not N-number or Lable
    public int linenumber;
    /**
     * If the shape was a arc (circle), the radius (CR or B)
     */
    public double radius;
    // for arc, ccw (counter clockwise) is true for G3, ccw=false for G2
    public boolean ccw = true;

    // The element as curve (line or arc) with transition element
    public CirculinearElement2D curve;
    // The transition element as curve (line or arc)
    public CirculinearElement2D transition_curve;

    
    // only for calculation:
    public double angle;
    public double startangle;
    public double endangle;
    
    // freedom of the endpoint
    public boolean x_free = false;
    public boolean y_free = false;
    
    
    // only for lines
    public boolean angle_free = true;

    // only for arc:
    public boolean radius_free = false;
    public boolean x_center_free = true;
    public boolean y_center_free = true;
    public boolean startangle_free = true;
    public boolean endangle_free = true;
    
    public boolean tangent = false;
    
    /**
     * convert boolean to the values of Freedom
     * @param b
     * @return integer @see Freedom
     */
    public static int BooltoInt(boolean b){
        if(b)
            return 1;
        return 0;
    }

}
