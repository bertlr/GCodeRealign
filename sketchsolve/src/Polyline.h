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
 * File:   Polyline.h
 * Author: Herbert Roider <herbert@roider.at>
 *
 * Created on 3. März 2017, 17:22
 */

#ifndef POLYLINE_H
#define POLYLINE_H


#include <iostream>
#include <vector>
#include <math.h>
#include "solve.h"
#include "LineTo.h"

class Polyline {
public:




    Polyline();
    Polyline(const Polyline& orig);
    virtual ~Polyline();


    LineTo* addPoint(double x, Freedom fx, double y, Freedom fy);
    LineTo* addLine(double x, Freedom fx, double y,
            Freedom fy, double angle, Freedom fangle, bool tangent);

    LineTo* addArc(double x, Freedom fx, double y, Freedom fy,
            double center_x, Freedom fcenter_x, double center_y, Freedom fcenter_y,
            double rad, Freedom frad,
            double start_angle, Freedom fstart_angle,
            double end_angle, Freedom fend_angle, bool tangent);

    int solve();


    
    
    std::vector<LineTo*> g;



private:
    int ccw(point *p0, point *p1, point *p2);
    double *pparameters[100];
};

#endif /* POLYLINE_H */

