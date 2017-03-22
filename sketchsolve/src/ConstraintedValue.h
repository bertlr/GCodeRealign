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
 * File:   ConstraintedValue.h
 * Author: Herbert Roider <herbert@roider.at>
 *
 * Created on 24. Februar 2017, 21:57
 */

#ifndef CONSTRAINTEDVALUE_H
#define CONSTRAINTEDVALUE_H

enum Freedom { FIXED=0, FREE=1 };

class ConstraintedValue {
public:
    ConstraintedValue();
    ConstraintedValue(const ConstraintedValue& orig);
    virtual ~ConstraintedValue();
    public:
    //double v;
    double *p_v;
    void set(double _v, Freedom _f);  
    Freedom free;
private:

};

#endif /* CONSTRAINTEDVALUE_H */

