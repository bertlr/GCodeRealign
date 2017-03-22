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

#include "contoursolveinterface_Contour.h"
#include "Polyline.h"
#include <iostream>

//#include <stdlib.h>

std::vector<Polyline*> polylines;

JNIEXPORT jint JNICALL Java_contoursolveinterface_Contour_create(JNIEnv *env, jobject obj) {
    //printf("Hello_World_create  !!!\n");
    Polyline *p = new Polyline;
    polylines.push_back(p);
    //polylines.push_back(p);
    int index = polylines.size() - 1;
    return index;

}

JNIEXPORT jint JNICALL Java_contoursolveinterface_Contour_addPoint(JNIEnv *env, jobject obj, jint handle, jdouble x, jint fx, jdouble y, jint fy) {
    //printf("Hello World addPoint!!!!\n");
    Polyline *p = polylines[handle];
    p->addPoint(x, static_cast<Freedom> (fx), y, static_cast<Freedom> (fy));

    return polylines[handle]->g.size();


}

/*
 * Class:     jnitest_Polyline
 * Method:    addLine
 * Signature: (IDIDIDIZ)I
 */
JNIEXPORT jint JNICALL Java_contoursolveinterface_Contour_addLine(JNIEnv *env, jobject obj, jint handle, jdouble x, jint fx, jdouble y, jint fy, jdouble angle, jint fangle, jboolean tangent) {
    //printf("Hello World addLine!!!!\n");
    Polyline *p = polylines[handle];
    p->addLine(x, static_cast<Freedom> (fx), y, static_cast<Freedom> (fy), angle, static_cast<Freedom> (fangle), tangent);

    return polylines[handle]->g.size();


}

JNIEXPORT jint JNICALL Java_contoursolveinterface_Contour_addArc
(JNIEnv *env, jobject obj, jint handle, jdouble x, jint fx, jdouble y, jint fy, jdouble center_x, jint fcenter_x, jdouble center_y, jint fcenter_y, jdouble rad, jint frad, jdouble start_angle, jint fstart_angle, jdouble end_angle, jint fend_angle, jboolean tangent) {
    Polyline *p = polylines[handle];
    p->addArc(x, static_cast<Freedom> (fx),
            y, static_cast<Freedom> (fy),
            center_x, static_cast<Freedom> (fcenter_x),
            center_y, static_cast<Freedom> (fcenter_y),
            rad, static_cast<Freedom> (frad),
            start_angle, static_cast<Freedom> (fstart_angle),
            end_angle, static_cast<Freedom> (fend_angle),
            tangent);

    return polylines[handle]->g.size();

}

/*
 * Class:     jnitest_Polyline
 * Method:    solve
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_contoursolveinterface_Contour_solve
(JNIEnv *env, jobject obj, jint handle) {
    Polyline *p = polylines[handle];

    for (unsigned int i = 0; i < p->g.size(); i++) {
        std::cout << p->g[i]->type;
        std::cout << ", x:" << *p->g[i]->x.p_v << " free:" << p->g[i]->x.free << ", y:" << *p->g[i]->y.p_v << " free:" << p->g[i]->y.free;
        if (p->g[i]->type == 1) {
            std::cout << ", angle:" << *p->g[i]->angle.p_v << " free:" << p->g[i]->angle.free;
        }
        std::cout << std::endl;
    }



    int ret = p->solve();
    if (ret != 0) {
        std::cout << "-----------------------Error----------------------------" << std::endl;
        for (unsigned int i = 0; i < p->g.size(); i++) {
            std::cout << p->g[i]->type;
            std::cout << ", x:" << *p->g[i]->x.p_v << " free:" << p->g[i]->x.free << ", y:" << *p->g[i]->y.p_v << " free:" << p->g[i]->y.free;
            if (p->g[i]->type == 1) {
                std::cout << ", angle:" << *p->g[i]->angle.p_v << " free:" << p->g[i]->angle.free;
            }
            std::cout << std::endl;
        }



    }
    return ret;

}

/*
 * Class:     jnitest_Polyline
 * Method:    getSolution
 * Signature: (I)[[D
 */
JNIEXPORT jdoubleArray JNICALL Java_contoursolveinterface_Contour_getSolution
(JNIEnv *env, jobject obj, jint handle, jint index) {
    Polyline *p = polylines[handle];

    LineTo *p_lt = p->g[index];


    double data[3];
    data[0] = *p_lt->end->x;
    data[1] = *p_lt->end->y;
    data[2] = 0.0;
    if (p_lt->type == ARC) {
        double center_x = *p_lt->center->x;
        double center_y = *p_lt->center->y;
        double rad = hypot(data[0] - center_x, data[1] - center_y);
        if (p_lt->ccw) {
            rad *= -1;
        }
        data[2] = rad;
    }


    jdoubleArray line_element = env->NewDoubleArray(3);
    if (NULL == line_element) return NULL;
    if (index >= p->g.size()) return NULL;
    env->SetDoubleArrayRegion(line_element, 0, 3, data);

    return line_element;

}

JNIEXPORT jint JNICALL Java_contoursolveinterface_Contour_size
(JNIEnv *env, jobject obj, jint handle) {
    return polylines[handle]->g.size();

}

