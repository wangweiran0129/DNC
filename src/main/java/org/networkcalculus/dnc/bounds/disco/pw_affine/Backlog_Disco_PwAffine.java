/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2011 - 2018 Steffen Bondorf
 * Copyright (C) 2017 - 2018 The DiscoDNC contributors
 * Copyright (C) 2019+ The DNC contributors
 *
 * http://networkcalculus.org
 *
 *
 * The Deterministic Network Calculator (DNC) is free software;
 * you can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package org.networkcalculus.dnc.bounds.disco.pw_affine;

import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.Curve_ConstantPool;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.num.Num;

public final class Backlog_Disco_PwAffine {
	public static Num derive(ArrivalCurve arrival_curve, ServiceCurve service_curve) {
		if (arrival_curve.equals(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get())) {
			return Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
		}
		if (service_curve.isDelayedInfiniteBurst()) {
			return arrival_curve.f(service_curve.getLatency());
		}
		if (service_curve.equals(Curve_ConstantPool.ZERO_SERVICE_CURVE.get()) // We know from above that the
				// arrivals are not zero.
				|| arrival_curve.getUltAffineRate().gt(service_curve.getUltAffineRate())) {
			return Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
		}
		
		return Num.getUtils(Calculator.getInstance().getNumBackend()).max
    				(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero(), 
    				Curve.getUtils().getMaxVerticalDeviation(service_curve, arrival_curve));
	}
}
