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

package org.networkcalculus.dnc.algebra.disco.pw_affine;

import java.util.HashSet;
import java.util.Set;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.algebra.disco.MinPlus_Disco_Configuration;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.Curve_ConstantPool;
import org.networkcalculus.dnc.curves.LinearSegment;
import org.networkcalculus.dnc.curves.MaxServiceCurve;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.utils.CheckUtils;
import org.networkcalculus.num.Num;

public abstract class Convolution_Disco_PwAffine {

    // ------------------------------------------------------------
    // Service Curves
    // ------------------------------------------------------------


    public static ServiceCurve convolve(ServiceCurve service_curve_1, ServiceCurve service_curve_2)
    {
        if(AnalysisConfig.enforceMultiplexingStatic() == AnalysisConfig.MultiplexingEnforcement.GLOBAL_FIFO)
        {
            return convolveFIFO(service_curve_1, service_curve_2);
        }

        else{
            return convolveARB(service_curve_1, service_curve_2);
        }
    }

    public static ServiceCurve convolveFIFO(ServiceCurve service_curve_0, ServiceCurve service_curve_1){

        switch (CheckUtils.inputDelayedInfiniteBurstCheck(service_curve_0, service_curve_1)) {
            case 1:
                return service_curve_1.copy();
            case 2:
                return service_curve_0.copy();
            case 3:
                return service_curve_0.copy();
            case 0:
            default:
        }

        ServiceCurve[] service_curves = {service_curve_0.copy(), service_curve_1.copy()};
        int[] i = {0, 0};

        Num total_latency = Num.getUtils(Calculator.getInstance().getNumBackend()).add(service_curve_0.getLatency(), service_curve_1.getLatency());

        for (int service_index = 0; service_index < 2; service_index++){
            Num latency = service_curves[service_index].getLatency();


            for(int index = 0; index < service_curves[service_index].getSegmentCount(); index++){
                // if(!service_curves[service_index].getSegment(index).getX().eqZero())
                service_curves[service_index].getSegment(index).setX(Num.getUtils(Calculator.getInstance().getNumBackend()).sub(service_curves[service_index].getSegment(index).getX(), latency));
            }
        }

        ServiceCurve result  = ((ServiceCurve) Curve.getUtils().shiftRight(Curve.getUtils().min(service_curves[0], service_curves[1]), total_latency));

        return result;
    }



    /**
     * Returns the convolution of two curve, which must be convex
     *
     * @param service_curve_1 The first curve to convolve with.
     * @param service_curve_2 The second curve to convolve with.
     * @return The convolved curve.
     */
    public static ServiceCurve convolveARB(ServiceCurve service_curve_1, ServiceCurve service_curve_2) {
        // null checks will be done by convolve_SC_SC_Generic( ... ).
        switch (CheckUtils.inputNullCheck(service_curve_1, service_curve_2)) {
            case 1:
                return service_curve_2.copy();
            case 2:
                return service_curve_1.copy();
            case 3:
                return Curve_ConstantPool.ZERO_SERVICE_CURVE.get();
            case 0:
            default:
                break;
        }

        // Shortcut: only go here if there is at least one delayed infinite burst
        if (service_curve_1.isDelayedInfiniteBurst() || service_curve_2.isDelayedInfiniteBurst()) {
            if (service_curve_1.isDelayedInfiniteBurst()
                    && service_curve_2.isDelayedInfiniteBurst()) {
                return Curve.getFactory().createDelayedInfiniteBurst(
                		Num.getUtils(Calculator.getInstance().getNumBackend()).add(service_curve_1.getLatency(), service_curve_2.getLatency()));
            }

            if (service_curve_1.isDelayedInfiniteBurst()) { // service_curve_2 is not a delayed infinite burst
                return Curve.getFactory().createServiceCurve(
                		Curve.getUtils().shiftRight(service_curve_2, service_curve_1.getLatency()));
            }

            if (service_curve_2.isDelayedInfiniteBurst()) { // service_curve_2 is not a delayed infinite burst
                return Curve.getFactory().createServiceCurve(
                		Curve.getUtils().shiftRight(service_curve_1, service_curve_2.getLatency()));
            }
        }

        ServiceCurve zero_service = Curve_ConstantPool.ZERO_SERVICE_CURVE.get();
        if (service_curve_1.equals(zero_service) || service_curve_2.equals(zero_service)) {
            return zero_service;
        }

        ServiceCurve result = Curve.getFactory().createServiceCurve();

        Num x = Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
        Num y = Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(); // Functions pass though the origin
        Num grad = Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
        LinearSegment s = LinearSegment.createLinearSegment(x, y, grad, false);
        result.addSegment(s);

        int i1 = (service_curve_1.isRealDiscontinuity(0)) ? 1 : 0;
        int i2 = (service_curve_2.isRealDiscontinuity(0)) ? 1 : 0;
        if (i1 > 0 || i2 > 0) {
            x = Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
            y = Num.getUtils(Calculator.getInstance().getNumBackend()).add(service_curve_1.fLimitRight(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero()),
                    service_curve_2.fLimitRight(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero()));
            grad = Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
            s = LinearSegment.createLinearSegment(x, y, grad, true);

            result.addSegment(s);
        }

        while (i1 < service_curve_1.getSegmentCount() || i2 < service_curve_2.getSegmentCount()) {
            if (service_curve_1.getSegment(i1).getGrad().lt(service_curve_2.getSegment(i2).getGrad())) {
                if (i1 + 1 >= service_curve_1.getSegmentCount()) {
                    result.getSegment(result.getSegmentCount() - 1).setGrad(service_curve_1.getSegment(i1).getGrad());
                    break;
                }

                x = Num.getUtils(Calculator.getInstance().getNumBackend()).add(result.getSegment(result.getSegmentCount() - 1).getX(),
                        (Num.getUtils(Calculator.getInstance().getNumBackend()).sub(service_curve_1.getSegment(i1 + 1).getX(),
                                service_curve_1.getSegment(i1).getX())));
                y = Num.getUtils(Calculator.getInstance().getNumBackend()).add(result.getSegment(result.getSegmentCount() - 1).getY(),
                        (Num.getUtils(Calculator.getInstance().getNumBackend()).sub(service_curve_1.getSegment(i1 + 1).getY(),
                                service_curve_1.getSegment(i1).getY())));
                grad = Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
                s = LinearSegment.createLinearSegment(x, y, grad, true);

                result.getSegment(result.getSegmentCount() - 1).setGrad(service_curve_1.getSegment(i1).getGrad());
                result.addSegment(s);

                i1++;
            } else {
                if (i2 + 1 >= service_curve_2.getSegmentCount()) {
                    result.getSegment(result.getSegmentCount() - 1).setGrad(service_curve_2.getSegment(i2).getGrad());
                    break;
                }

                x = Num.getUtils(Calculator.getInstance().getNumBackend()).add(result.getSegment(result.getSegmentCount() - 1).getX(),
                        (Num.getUtils(Calculator.getInstance().getNumBackend()).sub(service_curve_2.getSegment(i2 + 1).getX(),
                                service_curve_2.getSegment(i2).getX())));
                y = Num.getUtils(Calculator.getInstance().getNumBackend()).add(result.getSegment(result.getSegmentCount() - 1).getY(),
                        (Num.getUtils(Calculator.getInstance().getNumBackend()).sub(service_curve_2.getSegment(i2 + 1).getY(),
                                service_curve_2.getSegment(i2).getY())));
                grad = Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
                s = LinearSegment.createLinearSegment(x, y, grad, true);

                result.getSegment(result.getSegmentCount() - 1).setGrad(service_curve_2.getSegment(i2).getGrad());
                result.addSegment(s);

                i2++;
            }
        }

        Curve.getUtils().beautify(result);

        return result;
    }

    public static Set<ServiceCurve> convolve(Set<ServiceCurve> service_curves_1, Set<ServiceCurve> service_curves_2) {
        Set<ServiceCurve> results = new HashSet<ServiceCurve>();

        // An empty or null set does is not interpreted as a convolution with a null
        // curve.
        // Instead, the other set is return in case it is neither null or empty.
        Set<ServiceCurve> clone = new HashSet<ServiceCurve>();
        switch (CheckUtils.inputNullCheck(service_curves_1, service_curves_2)) {
            case 1:
                for (ServiceCurve sc : service_curves_2) {
                    clone.add(sc.copy());
                }
                return clone;
            case 2:
                for (ServiceCurve sc : service_curves_1) {
                    clone.add(sc.copy());
                }
                return clone;
            case 3:
                results.add(Curve_ConstantPool.ZERO_SERVICE_CURVE.get());
                return results;
            case 0:
            default:
                break;
        }
        switch (CheckUtils.inputEmptySetCheck(service_curves_1, service_curves_2)) {
            case 1:
                for (ServiceCurve sc : service_curves_2) {
                    clone.add(sc.copy());
                }
                return clone;
            case 2:
                for (ServiceCurve sc : service_curves_1) {
                    clone.add(sc.copy());
                }
                return clone;
            case 3:
                results.add(Curve_ConstantPool.ZERO_SERVICE_CURVE.get());
                return results;
            case 0:
            default:
                break;
        }

        for (ServiceCurve beta_1 : service_curves_1) {
            for (ServiceCurve beta_2 : service_curves_2) {
                results.add(convolve(beta_1, beta_2));
            }
        }

        return results;
    }

    // ------------------------------------------------------------
    // Arrival Curves
    // ------------------------------------------------------------
    public static ArrivalCurve convolve(ArrivalCurve arrival_curve_1, ArrivalCurve arrival_curve_2) {
        switch (CheckUtils.inputNullCheck(arrival_curve_1, arrival_curve_2)) {
            case 0:
                break;
            case 1:
                return arrival_curve_2.copy();
            case 2:
                return arrival_curve_1.copy();
            case 3:
                return Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get();
            default:
                break;
        }

        ArrivalCurve zero_arrival = Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get();
        if (arrival_curve_1.equals(zero_arrival) || arrival_curve_2.equals(zero_arrival)) {
            return zero_arrival;
        }

        ArrivalCurve infinite_arrivals = Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get();
        if (arrival_curve_1.equals(infinite_arrivals)) {
            return arrival_curve_2.copy();
        }
        if (arrival_curve_2.equals(infinite_arrivals)) {
            return arrival_curve_1.copy();
        }

        // Arrival curves are concave curves so we can do a minimum instead of a
        // convolution here.
        ArrivalCurve convolved_arrival_curve = Curve.getFactory()
                .createArrivalCurve(Curve.getUtils().min(arrival_curve_1, arrival_curve_2));
        return convolved_arrival_curve;
    }

    public static ArrivalCurve convolve(Set<ArrivalCurve> arrival_curves) {
        // Custom null and empty checks for this single argument method.
        if (arrival_curves == null || arrival_curves.isEmpty()) {
            return Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get();
        }
        if (arrival_curves.size() == 1) {
            return arrival_curves.iterator().next().copy();
        }

        ArrivalCurve arrival_curve_result = Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get();
        for (ArrivalCurve arrival_curve_2 : arrival_curves) {
            arrival_curve_result = convolve(arrival_curve_result, arrival_curve_2);
        }

        return arrival_curve_result;
    }

    // ------------------------------------------------------------
    // Maximum Service Curves
    // ------------------------------------------------------------

    /**
     * Returns the convolution of this curve, which must be (almost) concave, and
     * the given curve, which must also be (almost) concave.
     *
     * @param max_service_curve_1 The fist maximum service curve in the convolution.
     * @param max_service_curve_2 The second maximum service curve in the convolution.
     * @return The convolved maximum service curve.
     */
    public static MaxServiceCurve convolve(MaxServiceCurve max_service_curve_1, MaxServiceCurve max_service_curve_2) {
        switch (CheckUtils.inputNullCheck(max_service_curve_1, max_service_curve_2)) {
            case 0:
                break;
            case 1:
                return max_service_curve_2.copy();
            case 2:
                return max_service_curve_1.copy();
            case 3:
                return Curve.getFactory().createZeroDelayInfiniteBurstMSC();
            default:
        }

        if (MinPlus_Disco_Configuration.getInstance().exec_max_service_curve_checks()
                && (!max_service_curve_1.isAlmostConcave() || !max_service_curve_2.isAlmostConcave())) {
            throw new IllegalArgumentException("Both maximum service curves must be almost concave!");
        }

        Num latency_msc_1 = max_service_curve_1.getLatency();
        Num latency_msc_2 = max_service_curve_2.getLatency();

        if (latency_msc_1.equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity())) {
            return max_service_curve_2.copy();
        }
        if (latency_msc_2.equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity())) {
            return max_service_curve_1.copy();
        }

        // (min,plus)-algebraic proceeding here:
        // Remove latencies, act analog to the convolution of two arrival curves, and
        // the sum of the two latencies.
        ArrivalCurve ac_intermediate = convolve(
                Curve.getFactory()
                        .createArrivalCurve(Curve.getUtils().removeLatency(max_service_curve_1)),
                Curve.getFactory()
                        .createArrivalCurve(Curve.getUtils().removeLatency(max_service_curve_2)));
        MaxServiceCurve result = Curve.getFactory().createMaxServiceCurve(ac_intermediate);
        result = (MaxServiceCurve) Curve.getUtils().shiftRight(result,
        		Num.getUtils(Calculator.getInstance().getNumBackend()).add(latency_msc_1, latency_msc_2));
        Curve.getUtils().beautify(result);

        return result;
    }

    // ------------------------------------------------------------
    // Arrival Curves and Max Service Curves
    // ------------------------------------------------------------
    // The result is used like an arrival curve, yet it is not really one. This
    // inconsistency occurs because we need to consider MSC and SC in some order
    // during the output bound computation.
    public static Set<Curve> convolve_ACs_MSC(Set<ArrivalCurve> arrival_curves,
                                                      MaxServiceCurve maximum_service_curve) throws Exception {
        switch (CheckUtils.inputNullCheck(arrival_curves, maximum_service_curve)) {
            case 1:
                return new HashSet<Curve>();
            case 2:
                Set<Curve> clone = new HashSet<>();

                for (ArrivalCurve ac : arrival_curves) {
                    clone.add(ac.copy());
                }
                return clone;
            case 3:
                return new HashSet<Curve>();
            case 0:
            default:
                break;
        }
        if (arrival_curves.isEmpty()) {
            return new HashSet<Curve>();
        }

        Num msc_latency = maximum_service_curve.getLatency();
        Set<Curve> result = new HashSet<Curve>();

        // Similar to convolve_ACs_EGamma
        ArrivalCurve msc_as_ac = Curve.getFactory()
                .createArrivalCurve(Curve.getUtils().removeLatency(maximum_service_curve));
        // Abuse the ArrivalCurve class here for convenience.
        
        for (ArrivalCurve ac : arrival_curves) {
            result.add(Curve.getUtils().shiftRight(convolve(ac, msc_as_ac), msc_latency));
        }

        return result;
    }

    public static Set<ArrivalCurve> convolve_ACs_EGamma(Set<ArrivalCurve> arrival_curves,
                                                        MaxServiceCurve extra_gamma_curve) throws Exception {
        switch (CheckUtils.inputNullCheck(arrival_curves, extra_gamma_curve)) {
            case 0:
                break;
            case 1:
                return new HashSet<ArrivalCurve>();
            case 2:
                Set<ArrivalCurve> clone = new HashSet<ArrivalCurve>();

                for (ArrivalCurve ac : arrival_curves) {
                    clone.add(ac.copy());
                }
                return clone;
            case 3:
                return new HashSet<ArrivalCurve>();
            default:
        }
        if (arrival_curves.isEmpty()) {
            return new HashSet<ArrivalCurve>();
        }

        if (extra_gamma_curve.getLatency().gtZero()) {
            throw new Exception("Cannot convolve with an extra gamma curve with latency");
        }

        Set<ArrivalCurve> result = new HashSet<ArrivalCurve>();
        
        // Abuse the ArrivalCurve class here for convenience.
        ArrivalCurve extra_gamma_as_ac = Curve.getFactory().createArrivalCurve(extra_gamma_curve);
        for (ArrivalCurve ac : arrival_curves) {
            result.add(convolve(ac, extra_gamma_as_ac));
        }

        return result;
    }
}
