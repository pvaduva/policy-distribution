/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Copyright (C) 2019 Nordix Foundation.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.distribution.main.startstop;

import java.util.Arrays;

import org.onap.policy.distribution.main.PolicyDistributionException;
import org.onap.policy.distribution.main.parameters.DistributionParameterGroup;
import org.onap.policy.distribution.main.parameters.DistributionParameterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class initiates ONAP Policy Framework policy distribution.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    // The policy distribution Activator that activates the policy distribution service
    private DistributionActivator activator;

    // The parameters read in from JSON
    private DistributionParameterGroup parameterGroup;

    /**
     * Instantiates the policy distribution service.
     *
     * @param args the command line arguments
     */
    public Main(final String[] args) {
        final String argumentString = Arrays.toString(args);
        LOGGER.info("Starting policy distribution service with arguments - {}", argumentString);

        // Check the arguments
        final DistributionCommandLineArguments arguments = new DistributionCommandLineArguments();
        try {
            // The arguments return a string if there is a message to print and we should exit
            final String argumentMessage = arguments.parse(args);
            if (argumentMessage != null) {
                LOGGER.info(argumentMessage);
                return;
            }

            // Validate that the arguments are sane
            arguments.validate();
        } catch (final PolicyDistributionException e) {
            LOGGER.error("start of policy distribution service failed", e);
            return;
        }

        // Read the parameters
        try {
            parameterGroup = new DistributionParameterHandler().getParameters(arguments);
        } catch (final Exception e) {
            LOGGER.error("start of policy distribution service failed", e);
            return;
        }

        // Now, create the activator for the policy distribution service
        activator = new DistributionActivator(parameterGroup);

        // Start the activator
        try {
            activator.initialize();
        } catch (final PolicyDistributionException e) {
            LOGGER.error("start of policy distribution service failed, used parameters are " + Arrays.toString(args),
                    e);
            return;
        }

        // Add a shutdown hook to shut everything down in an orderly manner
        Runtime.getRuntime().addShutdownHook(new PolicyDistributionShutdownHookClass());
        LOGGER.info("Started policy distribution service");
    }

    /**
     * Get the parameters specified in JSON.
     *
     * @return the parameters
     */
    public DistributionParameterGroup getParameters() {
        return parameterGroup;
    }

    /**
     * Shut down Execution.
     *
     * @throws PolicyDistributionException on shutdown errors
     */
    public void shutdown() throws PolicyDistributionException {
        // clear the parameterGroup variable
        parameterGroup = null;

        // clear the distribution activator
        if (activator != null) {
            activator.terminate();
        }
    }

    /**
     * The Class PolicyDistributionShutdownHookClass terminates the policy distribution service when its run method is
     * called.
     */
    private class PolicyDistributionShutdownHookClass extends Thread {
        /*
         * (non-Javadoc)
         *
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            try {
                // Shutdown the policy distribution service and wait for everything to stop
                activator.terminate();
            } catch (final PolicyDistributionException e) {
                LOGGER.warn("error occured during shut down of the policy distribution service", e);
            }
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {
        new Main(args);
    }
}
