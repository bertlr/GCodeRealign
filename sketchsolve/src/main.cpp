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

#include <iostream>
#include <vector>
#include <math.h>
#include "solve.h"
#include "LineTo.h"
#include "Polyline.h"


using namespace std;

int main() {

    //Polyline *pl = new Polyline;
    std::vector<Polyline*> _polylines;



    //    LineTo start;
    //    start.type = POINT;
    //    start.x.set(0.0);
    //    start.x.free = FIXED;
    //    start.y.set(2.0);
    //    start.y.free = FIXED;
    //
    //    LineTo p1;
    //    p1.type = ARC;
    //    p1.x.set(3.0);
    //    p1.x.free = FREE;
    //    p1.y.set(1.0);
    //    p1.y.free = FREE;
    //    p1.center_x.set(0.0);
    //    p1.center_x.free = FREE;
    //    p1.center_y.set(-4.0);
    //    p1.center_y.free = FREE;
    //    p1.rad.set(6.0);
    //    p1.rad.free = Freedom::FIXED;
    //    p1.start_angle.set(0.0);
    //    p1.start_angle.free = FIXED;
    //    p1.end_angle.free = FREE;
    //    p1.tangent = true;
    //
    //
    //    LineTo p2;
    //    p2.type = ARC;
    //    p2.x.set(7.0);
    //    p2.x.free = FIXED;
    //    p2.y.set(0.0);
    //    p2.y.free = FIXED;
    //    p2.center_x.set(7.0);
    //    p2.center_x.free = FREE;
    //    p2.center_y.set(8.0);
    //    p2.center_y.free = FREE;
    //    p2.rad.set(5.0);
    //    p2.rad.free = FREE;
    //    p2.start_angle.free = FREE;
    //    p2.end_angle.set(0.0);
    //    p2.end_angle.free = FIXED;

    //----------------------------



    //    pl.addPoint(-1.0, FREE, 0.0, FIXED);
    //    pl.addArc(4.0, FIXED, -2.0, FIXED,
    //            3.0, FREE, 4.0, FREE,
    //            6.0, FREE,
    //            270.0 * M_PI / 180.0, FIXED,
    //            0.0, FIXED,
    //            true);
    //    pl.addLine(6.0, FREE, -2.0, FIXED, 0.0, FIXED, false);
    //    
    //    pl.addLine(9.0, FIXED, 0.0, FIXED, 45.0 * M_PI / 180.0, FIXED, false);


    //    pl.addPoint(-7.0, FIXED, 7.0, FREE);
    //    pl.addLine(-8.0, FREE, 6.0, FREE, 225.0*M_PI/180.0, FIXED, true);
    //    pl.addArc(-10.0, FIXED, 5.0, FIXED,
    //            -8.0, FREE, 5.0, FREE,
    //            2.0, FIXED,
    //            225.0 * M_PI / 180.0, FREE,
    //            270.0 * M_PI / 180.0, FIXED,
    //            true);


    for (int i = 0; i < 1; i++) {
        _polylines.push_back(new Polyline);

    }
    for (Polyline *pl : _polylines) {
//        pl->addPoint(0.0, FIXED, 0.0, FIXED);
//        pl->addLine(0.0, FREE, 2.0, FREE, 90 * M_PI / 180.0, FIXED, true);
//        pl->addArc(-2.0, FREE, 5.0, FIXED, -2.0, FREE, 2.0, FREE, 2.0, FREE, 90.0 * M_PI / 180.0, FREE, 180.0, FREE, false);

//        pl->addPoint(0.0, FIXED, 2.0, FIXED);
//        pl->addArc(3.0, FREE,  2.0, FREE, 
//                0.0, FREE, -4.0, FREE, 
//                6.0, FIXED,  
//                0.0 * M_PI / 180.0, FIXED, 
//                350.0 * M_PI / 180.0, FREE, 
//                true);
//        pl->addArc(7.0, FIXED, 0.0, FIXED, 
//                7.0, FREE, 16.0, FREE, 
//                8.0, FREE, 
//                350.0 * M_PI / 180.0, FREE, 
//                0.0, FIXED, 
//                false);

        
////////////////////////////////////////
// ccw:
//        pl->addPoint(4.0, FREE, 5, FIXED);
//        pl->addLine(2.0, FREE, 3.0, FREE, 225.0 * M_PI / 180.0, FIXED, true);
//        pl->addArc(0.0, FIXED,  0.0, FIXED, 
//                3.0, FREE, 0.0, FREE, 
//                3.0, FIXED,  
//                225.0 * M_PI / 180.0, FREE, 
//                270.0 * M_PI / 180.0, FIXED, 
//                true);
        
        pl->addPoint(-4.0, FREE, -5, FIXED);
        pl->addLine(-2.0, FREE, -3.0, FREE, 45.0 * M_PI / 180.0, FIXED, true);
        pl->addArc(0.0, FIXED,  0.0, FIXED, 
                -3.0, FREE, 0.0, FREE, 
                3.0, FIXED,  
                45.0 * M_PI / 180.0, FREE, 
                90.0 * M_PI / 180.0, FIXED, 
                true);

        
        
        
////////////////////////////////////////////        
// cw:
//        pl->addPoint(-4.0, FREE, 5, FIXED);
//        pl->addLine(-2.0, FREE, 3.0, FREE, 315.0 * M_PI / 180.0, FIXED, true);
//        pl->addArc(0.0, FIXED,  0.0, FIXED, 
//                -3.0, FREE, 0.0, FREE, 
//                3.0, FIXED,  
//                315.0 * M_PI / 180.0, FREE, 
//                270.0 * M_PI / 180.0, FIXED, 
//                true);

//        pl->addPoint(4.0, FREE, -5.0, FIXED);
//        pl->addLine(2.0, FREE, -3.0, FREE, 135.0 * M_PI / 180.0, FIXED, true);
//        pl->addArc(0.0, FIXED,  0.0, FIXED, 
//                3.0, FREE, 0.0, FREE, 
//                3.0, FIXED,  
//                135.0 * M_PI / 180.0, FREE, 
//                90.0 * M_PI / 180.0, FIXED, 
//                true);
//////////////////////////////////////



        

//        pl->addPoint(0.0, FIXED, 0.0, FIXED);
//        pl->addLine(-2.0, FREE, 0.0, FREE, 180.0 * M_PI / 180.0, FREE, true);
//        pl->addArc(-4.0, FIXED, 1.0, FIXED,
//                -2.0, FREE, 2.0, FREE,
//                2.0, FIXED,
//                180.0 * M_PI / 180.0, FREE,
//                135.0 * M_PI / 180.0, FIXED,
//                false); 
        
       
    }

    for (Polyline *pl : _polylines) {
        int solution = pl->solve();
        if (solution == succsess) {
            for (LineTo *current : pl->g) {

                if (current->type == POINT) {
                    std::cout << "start: " << *current->x.p_v << ", " << *current->y.p_v << endl;
                } else if (current->type == POINT || current->type == LINE) {
                    std::cout << "end: " << *current->x.p_v << ", " << *current->y.p_v << endl;

                } else {
                    std::cout << "center: " << *current->center_x.p_v << ", " << *current->center_y.p_v << endl;
                    std::cout << "end circle: " << *current->x.p_v << ", " << *current->y.p_v << endl;
                    std::cout << "raduis: " << *current->rad.p_v << endl;

                }
            }


        } else {
            std::cout << "--------------------------no valid solution---------------------------" << std::endl;
        }


    }

    std::cout << "--------------------------fertig---------------------------" << std::endl;





    return 0;
}

void debugprint(std::string s) {
    cout << s;
}
