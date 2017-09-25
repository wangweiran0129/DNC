/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta4 "Chimera".
 *
 * Copyright (C) 2013 - 2017 Steffen Bondorf
 * Copyright (C) 2017 The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de/index.php/projects/disco-dnc
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

package de.uni_kl.cs.disco.tests;

import de.uni_kl.cs.disco.nc.Analysis.Analyses;
import de.uni_kl.cs.disco.nc.AnalysisConfig.ArrivalBoundMethod;
import de.uni_kl.cs.disco.nc.AnalysisConfig.Multiplexing;
import de.uni_kl.cs.disco.nc.analyses.PmooAnalysis;
import de.uni_kl.cs.disco.nc.analyses.SeparateFlowAnalysis;
import de.uni_kl.cs.disco.nc.analyses.TotalFlowAnalysis;
import de.uni_kl.cs.disco.network.Flow;
import de.uni_kl.cs.disco.network.Network;
import de.uni_kl.cs.disco.numbers.Num;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(value = Parameterized.class)
public class FF_4S_1SC_4F_1AC_4P_Test extends DncTests {
    protected static final DncTestResults expected_results = new DncTestResults();
    protected static final DncTestResults expected_results_PMOOAB = new DncTestResults();
    private static FF_4S_1SC_4F_1AC_4P_Network test_network;
    private static Network network;
    private static Flow f0, f1, f2, f3;

    public FF_4S_1SC_4F_1AC_4P_Test(DncTestConfig test_config) {
        super(test_config);
    }

    @BeforeClass
    public static void createNetwork() {
        test_network = new FF_4S_1SC_4F_1AC_4P_Network();
        f0 = test_network.f0;
        f1 = test_network.f1;
        f2 = test_network.f2;
        f3 = test_network.f3;

        network = test_network.getNetwork();

        initializeBounds();
    }

    private static void initializeBounds() {
        expected_results.clear();

        Num factory = Num.getFactory();

        // TFA
        expected_results.setBounds(Analyses.TFA, Multiplexing.FIFO, f0, factory.create(3735, 32), factory.create(975));
        expected_results.setBounds(Analyses.TFA, Multiplexing.FIFO, f1, factory.create(77.5), factory.create(975));
        expected_results.setBounds(Analyses.TFA, Multiplexing.FIFO, f2, factory.create(1875, 32),
                factory.create(3975, 8));
        expected_results.setBounds(Analyses.TFA, Multiplexing.FIFO, f3, factory.create(845, 8), factory.create(975));
        expected_results.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f0, factory.create(1370, 3),
                factory.create(1400));
        expected_results.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f1, factory.create(395), factory.create(1400));
        expected_results.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f2, factory.create(1105, 6),
                factory.create(2075, 3));
        expected_results.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f3, factory.create(462.5),
                factory.create(1400));

        // SFA
        expected_results.setBounds(Analyses.SFA, Multiplexing.FIFO, f0, factory.create(1525, 16),
                factory.create(7825, 16));
        expected_results.setBounds(Analyses.SFA, Multiplexing.FIFO, f1, factory.create(575, 8),
                factory.create(2975, 8));
        expected_results.setBounds(Analyses.SFA, Multiplexing.FIFO, f2, factory.create(1695, 32),
                factory.create(8875, 32));
        expected_results.setBounds(Analyses.SFA, Multiplexing.FIFO, f3, factory.create(1405, 16),
                factory.create(7225, 16));
        expected_results.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f0, factory.create(580, 3),
                factory.create(5875, 6));
        expected_results.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f1, factory.create(345, 2),
                factory.create(875));
        expected_results.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f2, factory.create(1625, 18),
                factory.create(4175, 9));
        expected_results.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f3, factory.create(560, 3),
                factory.create(5675, 6));

        // PMOO
        expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f0, factory.create(650, 3),
                factory.create(6575, 6));
        expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f1, factory.create(345, 2),
                factory.create(875));
        expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f2, factory.create(305, 3),
                factory.create(3125, 6));
        expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f3, factory.create(1145, 6),
                factory.create(2900, 3));

        // PMOO Arrival Bounding yields worse cross-traffic arrivals!
        expected_results_PMOOAB.clear();

        // TFA
        expected_results_PMOOAB.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f0, factory.create(2765, 6),
                factory.create(8525, 6));
        expected_results_PMOOAB.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f1, factory.create(2395, 6),
                factory.create(8525, 6));
        expected_results_PMOOAB.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f3, factory.create(1400, 3),
                factory.create(8525, 6));

        // SFA
        expected_results_PMOOAB.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f0, factory.create(2345, 12),
                factory.create(11875, 12));
        expected_results_PMOOAB.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f1, factory.create(2095, 12),
                factory.create(10625, 12));

        // PMOO
        expected_results_PMOOAB.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f0, factory.create(2095, 12),
                factory.create(10625, 12));
        expected_results_PMOOAB.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f1, factory.create(875, 4),
                factory.create(4425, 4));
    }

    @Before
    public void reinitNetwork() {
        if (!super.reinitilize_test) {
            return;
        }

        test_network.reinitializeCurves();
        initializeBounds();
    }

    // --------------------Flow 0--------------------
    @Test
    public void f0_tfa() {
        setMux(network.getServers());

        if (test_config.arrivalBoundMethods().size() == 1
                && test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO)) { // Cannot be FIFO multiplexing
            // due to PMOO
            super.runTFAtest(new TotalFlowAnalysis(network, test_config), f0, expected_results_PMOOAB);
        } else {
            super.runTFAtest(new TotalFlowAnalysis(network, test_config), f0, expected_results);
        }
    }

    @Test
    public void f0_sfa() {
        setMux(network.getServers());

        if (test_config.arrivalBoundMethods().size() == 1
                && test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO)) {
            super.runSFAtest(new SeparateFlowAnalysis(network, test_config), f0, expected_results_PMOOAB);
        } else {
            super.runSFAtest(new SeparateFlowAnalysis(network, test_config), f0, expected_results);
        }
    }

    @Test
    public void f0_pmoo_arbMux() {
        setArbitraryMux(network.getServers());

        if (test_config.arrivalBoundMethods().size() == 1
                && test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO)) {
            super.runPMOOtest(new PmooAnalysis(network, test_config), f0, expected_results_PMOOAB);
        } else {
            super.runPMOOtest(new PmooAnalysis(network, test_config), f0, expected_results);
        }
    }

    @Test
    public void f0_sinktree_arbMux() {
        if (test_config.fullConsoleOutput()) {
            System.out.println("Analysis:\t\tTree Backlog Bound Analysis");
            System.out.println("Multiplexing:\t\tArbitrary");

            System.out.println("Flow of interest:\t" + f0.toString());
            System.out.println();

            System.out.println("--- Results: ---");
            System.out.println("Tree Backlog Bound calculation not applicable.");
        }
    }

    // --------------------Flow 1--------------------
    @Test
    public void f1_tfa() {
        setMux(network.getServers());

        if (test_config.arrivalBoundMethods().size() == 1
                && test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO)) {
            super.runTFAtest(new TotalFlowAnalysis(network, test_config), f1, expected_results_PMOOAB);
        } else {
            super.runTFAtest(new TotalFlowAnalysis(network, test_config), f1, expected_results);
        }
    }

    @Test
    public void f1_sfa() {
        setMux(network.getServers());

        if (test_config.arrivalBoundMethods().size() == 1
                && test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO)) {
            super.runSFAtest(new SeparateFlowAnalysis(network, test_config), f1, expected_results_PMOOAB);
        } else {
            super.runSFAtest(new SeparateFlowAnalysis(network, test_config), f1, expected_results);
        }
    }

    @Test
    public void f1_pmoo_arbMux() {
        setArbitraryMux(network.getServers());

        if (test_config.arrivalBoundMethods().size() == 1
                && test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO)) {
            super.runPMOOtest(new PmooAnalysis(network, test_config), f1, expected_results_PMOOAB);
        } else {
            super.runPMOOtest(new PmooAnalysis(network, test_config), f1, expected_results);
        }
    }

    @Test
    public void f1_sinktree_arbMux() {
        if (test_config.fullConsoleOutput()) {
            System.out.println("Analysis:\t\tTree Backlog Bound Analysis");
            System.out.println("Multiplexing:\t\tArbitrary");

            System.out.println("Flow of interest:\t" + f1.toString());
            System.out.println();

            System.out.println("--- Results: ---");
            System.out.println("Tree Backlog Bound calculation not applicable.");
        }
    }

    // --------------------Flow 2--------------------
    @Test
    public void f2_tfa() {
        setMux(network.getServers());
        super.runTFAtest(new TotalFlowAnalysis(network, test_config), f2, expected_results);
    }

    @Test
    public void f2_sfa() {
        setMux(network.getServers());
        super.runSFAtest(new SeparateFlowAnalysis(network, test_config), f2, expected_results);
    }

    @Test
    public void f2_pmoo_arbMux() {
        setArbitraryMux(network.getServers());
        super.runPMOOtest(new PmooAnalysis(network, test_config), f2, expected_results);
    }

    @Test
    public void f2_sinktree_arbMux() {
        if (test_config.fullConsoleOutput()) {
            System.out.println("Analysis:\t\tTree Backlog Bound Analysis");
            System.out.println("Multiplexing:\t\tArbitrary");

            System.out.println("Flow of interest:\t" + f2.toString());
            System.out.println();

            System.out.println("--- Results: ---");
            System.out.println("Tree Backlog Bound calculation not applicable.");
        }
    }

    // --------------------Flow 3--------------------
    @Test
    public void f3_tfa() {
        setMux(network.getServers());

        if (test_config.arrivalBoundMethods().size() == 1
                && test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO)) {
            super.runTFAtest(new TotalFlowAnalysis(network, test_config), f3, expected_results_PMOOAB);
        } else {
            super.runTFAtest(new TotalFlowAnalysis(network, test_config), f3, expected_results);
        }
    }

    @Test
    public void f3_sfa() {
        setMux(network.getServers());
        super.runSFAtest(new SeparateFlowAnalysis(network, test_config), f3, expected_results);
    }

    @Test
    public void f3_pmoo_arbMux() {
        setArbitraryMux(network.getServers());
        super.runPMOOtest(new PmooAnalysis(network, test_config), f3, expected_results);
    }

    @Test
    public void f3_sinktree_arbMux() {
        if (test_config.fullConsoleOutput()) {
            System.out.println("Analysis:\t\tTree Backlog Bound Analysis");
            System.out.println("Multiplexing:\t\tArbitrary");

            System.out.println("Flow of interest:\t" + f3.toString());
            System.out.println();

            System.out.println("--- Results: ---");
            System.out.println("Tree Backlog Bound calculation not applicable.");
        }
    }
}