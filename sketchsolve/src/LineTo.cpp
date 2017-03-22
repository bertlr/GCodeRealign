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
 * File:   LineTo.cpp
 * Author: Herbert Roider <herbert@roider.at>
 * 
 * Created on 24. Februar 2017, 19:49
 */

#include "LineTo.h"

LineTo::LineTo() {
   
    center = new point;
    end = new point;
    
//    point origin, end;
//    double zero = 0;
//    double one = 1.0;
//    origin.x = &zero;
//    origin.y = &zero;
//    end.x = &one;
//    end.y = &zero;
    //horizontalLine.p1 = origin;
    //horizontalLine.p2 = end;
    
}

LineTo::LineTo(const LineTo& orig) {
    this->angle = orig.angle;
    
}

LineTo::~LineTo() {
    
    delete center;
    delete end;
    center = NULL;
    end = NULL;
}

