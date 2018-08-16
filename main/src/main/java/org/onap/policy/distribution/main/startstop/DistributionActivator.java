/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

import java.util.HashMap;
import java.util.Map;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.PolicyForwardingException;
import org.onap.policy.distribution.main.PolicyDistributionException;
import org.onap.policy.distribution.main.parameters.DistributionParameterGroup;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.distribution.reception.handling.AbstractReceptionHandler;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerParameters;

/**
 * This class wraps a distributor so that it can be activated as a complete service together with all its distribution
 * and forwarding handlers.
 */
public class DistributionActivator {
    // The logger for this class
    private static final Logger LOGGER = FlexLogger.getLogger(DistributionActivator.class);

    // The parameters of this policy distribution activator
    private final DistributionParameterGroup distributionParameterGroup;

    // The map of reception handlers initialized by this distribution activator
    private final Map<String, AbstractReceptionHandler> receptionHandlersMap = new HashMap<>();

    /**
     * Instantiate the activator for policy distribution as a complete service.
     *
     * @param distributionParameterGroup the parameters for the distribution service
     */
    public DistributionActivator(final DistributionParameterGroup distributionParameterGroup) {
        this.distributionParameterGroup = distributionParameterGroup;
    }

    /**
     * Initialize distribution as a complete service.
     *
     * @throws PolicyDistributionException on errors in initializing the service
     */
    @SuppressWarnings("unchecked")
    public void initialize() throws PolicyDistributionException {
        LOGGER.debug("Policy distribution starting as a service . . .");
        registerToParameterService(distributionParameterGroup);
        for (final ReceptionHandlerParameters rHParameters : distributionParameterGroup.getReceptionHandlerParameters()
                .values()) {
            try {
                final Class<AbstractReceptionHandler> receptionHandlerClass =
                        (Class<AbstractReceptionHandler>) Class.forName(rHParameters.getReceptionHandlerClassName());
                final AbstractReceptionHandler receptionHandler = receptionHandlerClass.newInstance();
                receptionHandler.initialize(rHParameters.getName());
                receptionHandlersMap.put(rHParameters.getName(), receptionHandler);
            } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException
                    | PolicyDecodingException | PolicyForwardingException exp) {
                throw new PolicyDistributionException(exp.getMessage(), exp);
            }
        }
        LOGGER.debug("Policy distribution started as a service");
    }

    /**
     * Terminate policy distribution.
     *
     * @throws PolicyDistributionException on termination errors
     */
    public void terminate() throws PolicyDistributionException {
        try {
            for (final AbstractReceptionHandler handler : receptionHandlersMap.values()) {
                handler.destroy();
            }
            receptionHandlersMap.clear();
            deregisterToParameterService(distributionParameterGroup);
        } catch (final Exception exp) {
            LOGGER.error("Policy distribution service termination failed", exp);
            throw new PolicyDistributionException(exp.getMessage(), exp);
        }
    }

    /**
     * Get the parameters used by the activator.
     *
     * @return the parameters of the activator
     */
    public DistributionParameterGroup getParameterGroup() {
        return distributionParameterGroup;
    }

    /**
     * Method to register the parameters to Common Parameter Service.
     *
     * @param distributionParameterGroup
     */
    public void registerToParameterService(final DistributionParameterGroup distributionParameterGroup) {
        ParameterService.register(distributionParameterGroup);
        for (final ReceptionHandlerParameters params : distributionParameterGroup.getReceptionHandlerParameters()
                .values()) {
            params.setName(distributionParameterGroup.getName());
            params.getPluginHandlerParameters().setName(distributionParameterGroup.getName());
            ParameterService.register(params);
            ParameterService.register(params.getPluginHandlerParameters());
        }
    }

    /**
     * Method to deregister the parameters from Common Parameter Service.
     *
     * @param distributionParameterGroup
     */
    public void deregisterToParameterService(final DistributionParameterGroup distributionParameterGroup) {
        ParameterService.deregister(distributionParameterGroup.getName());
        for (final ReceptionHandlerParameters params : distributionParameterGroup.getReceptionHandlerParameters()
                .values()) {
            params.setName(distributionParameterGroup.getName());
            params.getPluginHandlerParameters().setName(distributionParameterGroup.getName());
            ParameterService.deregister((params.getName()));
            ParameterService.deregister((params.getPluginHandlerParameters().getName()));
        }
    }
}
