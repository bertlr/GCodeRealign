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
 * File:   ConstraintedValue.cpp
 * Author: Herbert Roider <herbert@roider.at>
 * 
 * Created on 24. Februar 2017, 21:57
 */

#include "ConstraintedValue.h"
#include <cstdlib>

ConstraintedValue::ConstraintedValue() {
    this->free = Freedom::FIXED;
    this->p_v = NULL;
}

ConstraintedValue::ConstraintedValue(const ConstraintedValue& orig) {
    this->p_v = NULL;
    if (orig.p_v != NULL) {
        this->p_v = new double;
        *this->p_v = *orig.p_v;
    }
    this->free = orig.free;

}

ConstraintedValue::~ConstraintedValue() {
    if (this->p_v != NULL)
        delete this->p_v;
}

void ConstraintedValue::set(double _v, Freedom _f) {
    this->p_v = new double;
    *this->p_v = _v;
    this->free = _f;
    //this->v = _v;

}

