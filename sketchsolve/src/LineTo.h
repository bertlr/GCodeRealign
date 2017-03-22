/***************************************************************************
 *   Copyright (C) 2017 by Herbert Roider <herbert@roider.at>       *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

/* 
 * File:   LineTo.h
 * Author: Herbert Roider <herbert@roider.at>
 *
 * Created on 24. Februar 2017, 19:49
 */

#ifndef LINETO_H
#define LINETO_H

#include "ConstraintedValue.h"
#include "solve.h"

enum LineType {POINT=0, LINE=1, ARC=2};




class LineTo {
public:
    LineTo();
    LineTo(const LineTo& orig);
    virtual ~LineTo();
    
    LineType type;
    ConstraintedValue x;
    ConstraintedValue y;
    
    ConstraintedValue angle;
    
    ConstraintedValue delta_x;
    ConstraintedValue delta_y;
    
    // for arc:
    ConstraintedValue rad;
    
    ConstraintedValue center_x;
    ConstraintedValue center_y;
    // angle minus 90° (pi/2)
    ConstraintedValue start_angle;
    // angle plus 90° (pi/2)
    ConstraintedValue end_angle;
    // angle between tangent and radius 
    ConstraintedValue start_angle_perpendicular;
    // angle between tangent and radius
    ConstraintedValue end_angle_perpendicular;

    bool ccw; // for arc: counterclockwise 
    
    
    bool tangent;
    
    
    point *center;
    point *end;
    
    //line horizontalLine;
    line toCenter;
    line toEnd;
    
    
private:
     // for arc:
   
    
    

};

#endif /* LINETO_H */

