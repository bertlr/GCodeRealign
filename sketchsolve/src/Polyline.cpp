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
 * File:   Polyline.cpp
 * Author: Herbert Roider <herbert@roider.at>
 * 
 * Created on 3. März 2017, 17:22
 */

#include "Polyline.h"
#include "LineTo.h"
#include <cstdlib>

Polyline::Polyline() {
}

Polyline::Polyline(const Polyline& orig) {
}

Polyline::~Polyline() {
    for (LineTo *current : this->g) {
        delete current;
    }
}

int Polyline::ccw(point *p0, point *p1, point *p2) {
    double dx1, dx2, dy1, dy2;
    dx1 = *p1->x - *p0->x;
    dy1 = *p1->y - *p0->y;
    dx2 = *p2->x - *p0->x;
    dy2 = *p2->y - *p0->y;
    if (dx1 * dy2 > dy1 * dx2) {
        return 1;
    }
    if (dx1 * dy2 < dy1 * dx2) {
        return -1;
    }
    if ((dx1 * dx2 < 0) || (dy1 * dy2 < 0)) {
        return -1;
    }
    if ((dx1 * dx1 + dy1 * dy1) < (dx2 * dx2 + dy2 * dy2)) {
        return 1;
    }

    return 0;

}

LineTo* Polyline::addPoint(double x, Freedom fx, double y, Freedom fy) {
    if (g.size() > 0) {
        return NULL;
    }


    LineTo *lt = new LineTo();
    lt->type = POINT;
    lt->x.set(x, fx);
    lt->y.set(y, fy);

    g.push_back(lt);
    return lt;
}

LineTo* Polyline::addLine(double x, Freedom fx, double y,
        Freedom fy, double angle, Freedom fangle, bool tangent) {
    LineTo *lt = new LineTo();
    lt->type = LINE;
    lt->x.set(x, fx);
    lt->y.set(y, fy);
    lt->angle.set(angle, fangle);
    lt->tangent = tangent;

    g.push_back(lt);
    return lt;

}

LineTo* Polyline::addArc(double x, Freedom fx, double y, Freedom fy,
        double center_x, Freedom fcenter_x, double center_y, Freedom fcenter_y,
        double rad, Freedom frad,
        double start_angle, Freedom fstart_angle,
        double end_angle, Freedom fend_angle, bool tangent) {

    LineTo *lt = new LineTo();
    lt->type = ARC;
    lt->x.set(x, fx);
    lt->y.set(y, fy);
    //lt->angle.set(angle, fangle);
    lt->center_x.set(center_x, fcenter_x);
    lt->center_y.set(center_y, fcenter_y);
    lt->rad.set(rad, frad);
    lt->start_angle.set(start_angle, fstart_angle);
    lt->end_angle.set(end_angle, fend_angle);
    lt->tangent = tangent;

    g.push_back(lt);
    return lt;

}

int Polyline::solve() {
    int i = 0; // current index for parameters;
    int j = 0; // current constraint;
    constraint cons[100];

    for (int z = 0; z < 100; z++) {
        pparameters[z] = NULL;
    }

    LineTo *prev = NULL;

    for (LineTo *current : g) {

        if (current->type == ARC) {

            if (current->center_x.free == FREE) {
                pparameters[i] = current->center_x.p_v;
                i++;
            }

            if (current->center_y.free == FREE) {
                pparameters[i] = current->center_y.p_v;
                i++;
            }




            current->center->x = current->center_x.p_v;
            current->center->y = current->center_y.p_v;
        }


        if (current->x.free == FREE) {
            pparameters[i] = current->x.p_v;
            i++;
        }
        if (current->y.free == FREE) {
            pparameters[i] = current->y.p_v;
            i++;
        }


        current->end->x = current->x.p_v;
        current->end->y = current->y.p_v;


        if (prev != NULL) {

            if (current->type == LINE) {

                current->toEnd.p1 = *prev->end;
                current->toEnd.p2 = *current->end;

                if (current->angle.free == FIXED) {
                    cons[j].type = absAngle;
                    cons[j].line1 = current->toEnd;
                    cons[j].parameter = current->angle.p_v;
                    j++;


                }
                if (prev->tangent) {
                    if (prev->type == ARC && current->type == LINE) {
                        cons[j].type = perpendicular;
                        cons[j].line1 = prev->toEnd;
                        cons[j].line2 = current->toEnd;
                        j++;
                    } else if (prev->type == LINE && current->type == LINE) {
                        cons[j].type = parallel;
                        cons[j].line1 = prev->toEnd;
                        cons[j].line2 = current->toEnd;
                        j++;
                    }

                }



            } else if (current->type == ARC) {
                current->toCenter.p1 = *prev->end;
                current->toCenter.p2 = *current->center;


                current->toEnd.p1 = *current->center;
                current->toEnd.p2 = *current->end;

                int counterclockwise = this->ccw(prev->end, current->end, current->center);
                if (counterclockwise == 0) {
                    //cout << "no valid arc" << endl;
                    //exit(1);
                    return 3;
                }
                current->ccw = false;
                if(counterclockwise == 1){
                    current->ccw = true;
                }

                if (current->rad.free == FIXED) {

                    cons[j].type = lineLength;
                    cons[j].line1 = current->toCenter;
                    cons[j].parameter = current->rad.p_v;
                    j++;

                    cons[j].type = lineLength;
                    cons[j].line1 = current->toEnd;
                    cons[j].parameter = current->rad.p_v;
                    j++;


                } else {
                    cons[j].type = equalLegnth;
                    cons[j].line1 = current->toCenter;
                    cons[j].line2 = current->toEnd;
                    j++;
                }

                double angle_90_degree;
                // calc the angle from start angle and end angle for the lines to the center and from the center
                angle_90_degree = -M_PI / 2.0;
                if (current->ccw) {
                    angle_90_degree *= -1.0;
                }

                if (current->start_angle.free == FIXED) {
                    //*current->start_angle_perpendicular.p_v =  *current->start_angle.p_v + angle_90_degree;
                    current->start_angle_perpendicular.set(*current->start_angle.p_v + angle_90_degree, FIXED);
                    cons[j].type = absAngle;
                    cons[j].line1 = current->toCenter;
                    cons[j].parameter = current->start_angle_perpendicular.p_v;
                    j++;


                }
                if (current->end_angle.free == FIXED) {
                    current->end_angle_perpendicular.set(*current->end_angle.p_v - angle_90_degree, FIXED);

                    cons[j].type = absAngle;
                    cons[j].line1 = current->toEnd;
                    cons[j].parameter = current->end_angle_perpendicular.p_v;
                    j++;

                }

                if (prev->tangent) {
                    if (prev->type == ARC) {
                        cons[j].type = parallel;
                        cons[j].line1 = prev->toEnd;
                        cons[j].line2 = current->toCenter;
                        j++;
                    } else if (prev->type == LINE) {
                        cons[j].type = perpendicular;
                        cons[j].line1 = prev->toEnd;
                        cons[j].line2 = current->toCenter;
                        j++;
                    }

                }

            }

        }

        prev = current;
    }


    int sol;


    sol = ::solve(this->pparameters, i, cons, j, fine);
    return sol;


}
