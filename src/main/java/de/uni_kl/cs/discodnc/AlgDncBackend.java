/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2018+ The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://discodnc.cs.uni-kl.de
 *
 *
 * The Disco Deterministic Network Calculator (DiscoDNC) is free software;
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

package de.uni_kl.cs.discodnc;

import de.uni_kl.cs.discodnc.algebra.MinPlus;
import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.LinearSegment;

public interface AlgDncBackend {
	MinPlus getMinPlus();
	
	Curve getCurveFactory();
	
	LinearSegment.Builder getLinearSegmentFactory();

    @Override
    String toString();
    
    default String assembleString(String curve_backend_name, String min_plus_name) {
    	StringBuffer name = new StringBuffer();

        name.append("CurveBackend");
        name.append(":");
        name.append(curve_backend_name);
        
        name.append(", ");

        name.append("AlegraBackend");
        name.append(":");
        name.append(min_plus_name);
    	
    	return name.toString();
    }
	
	default void checkDependencies() {
		
	}
}