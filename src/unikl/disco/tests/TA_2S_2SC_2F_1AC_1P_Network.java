/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.3 "Centaur".
 *
 * Copyright (C) 2013 - 2017 Steffen Bondorf
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

package unikl.disco.tests;

import unikl.disco.curves.ServiceCurve;
import unikl.disco.curves.ArrivalCurve;
import unikl.disco.network.Flow;
import unikl.disco.network.Network;
import unikl.disco.network.NetworkFactory;
import unikl.disco.network.Server;

/**
 * 
 * @author Steffen Bondorf
 *
 */
public class TA_2S_2SC_2F_1AC_1P_Network implements NetworkFactory {
	private static final int sc_R_0 = 10;
	private static final int sc_T_0 = 10;
	private static final int sc_R_1 = 6;
	private static final int sc_T_1 = 6;
	private static final double ac_r = 2.5;
	private static final double ac_b = 12.5;

	private static ServiceCurve service_curve_0 = ServiceCurve.createRateLatency( sc_R_0, sc_T_0 );
	private static ServiceCurve service_curve_1 = ServiceCurve.createRateLatency( sc_R_1, sc_T_1 );
	private static ArrivalCurve arrival_curve = ArrivalCurve.createTokenBucket( ac_r, ac_b );
	
	private Network network;
	protected Server s0, s1;
	protected Flow f0, f1;
	
	public TA_2S_2SC_2F_1AC_1P_Network() {
		network = createNetwork();
	}

	public Network getNetwork() {
		return network;
	}
	
	public Network createNetwork() {
		network = new Network();
		
		s0 = network.addServer( service_curve_0 );
		s0.setUseGamma( false );
		s0.setUseExtraGamma( false );
		
		s1 = network.addServer( service_curve_1 );
		s1.setUseGamma( false );
		s1.setUseExtraGamma( false );

		try {
			network.addLink( s0, s1 );
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException( e );
		}

		try {	
			f0 = network.addFlow( arrival_curve, s0, s1 );
			f1 = network.addFlow( arrival_curve, s0, s1 );
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException( e );
		}
		
		return network;
	}
	
	public void reinitializeCurves() {
		service_curve_0 = ServiceCurve.createRateLatency( sc_R_0, sc_T_0 );
		s0.setServiceCurve( service_curve_0 );
		
		service_curve_1 = ServiceCurve.createRateLatency( sc_R_1, sc_T_1 );
		s1.setServiceCurve( service_curve_1 );

		arrival_curve = ArrivalCurve.createTokenBucket( ac_r, ac_b );
		for( Flow flow : network.getFlows() ) {
			flow.setArrivalCurve( arrival_curve );
		}
	}
}