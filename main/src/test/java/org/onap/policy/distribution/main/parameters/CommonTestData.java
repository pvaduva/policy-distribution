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

package org.onap.policy.distribution.main.parameters;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to hold/create all parameters for test cases.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class CommonTestData {

    public static final String decoderType = "TOSCA";
    public static final String decoderClassName =
            "org.onap.policy.distribution.reception.decoding.pdpx.PolicyDecoderToscaPdpx";
    public static final String forwarderType = "PAPEngine";
    public static final String forwarderClassName =
            "org.onap.policy.distribution.forwarding.pap.engine.XacmlPapServletPolicyForwarder";
    public static final String receptionHandlerType = "SDCReceptionHandler";
    public static final String receptionHandlerClassName =
            "org.onap.policy.distribution.reception.handling.sdc.SdcReceptionHandler";

    /**
     * Returns an instance of ReceptionHandlerParameters for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the receptionHandlerParameters object
     */
    public Map<String, ReceptionHandlerParameters> getReceptionHandlerParameters(final boolean isEmpty) {
        final Map<String, ReceptionHandlerParameters> receptionHandlerParameters =
                new HashMap<String, ReceptionHandlerParameters>();
        if (!isEmpty) {
            final Map<String, PolicyDecoderParameters> policyDecoders = getPolicyDecoders(isEmpty);
            final Map<String, PolicyForwarderParameters> policyForwarders = getPolicyForwarders(isEmpty);
            final String receptionHandlerType = "SDC";
            final String receptionHandlerClassName =
                    "org.onap.policy.distribution.reception.handling.sdc.SdcReceptionHandler";
            final PluginHandlerParameters pHParameters = new PluginHandlerParameters(policyDecoders, policyForwarders);
            final ReceptionHandlerParameters rhParameters =
                    new ReceptionHandlerParameters(receptionHandlerType, receptionHandlerClassName, pHParameters);
            receptionHandlerParameters.put("SDCReceptionHandler", rhParameters);
        }
        return receptionHandlerParameters;
    }

    /**
     * Returns an instance of PluginHandlerParameters for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the pluginHandlerParameters object
     */
    public PluginHandlerParameters getPluginHandlerParameters(final boolean isEmpty) {
        final Map<String, PolicyDecoderParameters> policyDecoders = getPolicyDecoders(isEmpty);
        final Map<String, PolicyForwarderParameters> policyForwarders = getPolicyForwarders(isEmpty);
        final PluginHandlerParameters pluginHandlerParameters =
                new PluginHandlerParameters(policyDecoders, policyForwarders);
        return pluginHandlerParameters;
    }

    /**
     * Returns an instance of PolicyForwarderParameters for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the policyForwarders object
     */
    public Map<String, PolicyForwarderParameters> getPolicyForwarders(final boolean isEmpty) {
        final Map<String, PolicyForwarderParameters> policyForwarders =
                new HashMap<String, PolicyForwarderParameters>();
        if (!isEmpty) {
            final String forwarderType = "PAPEngine";
            final String forwarderClassName =
                    "org.onap.policy.distribution.forwarding.pap.engine.XacmlPapServletPolicyForwarder";
            final PolicyForwarderParameters pFParameters =
                    new PolicyForwarderParameters(forwarderType, forwarderClassName);
            policyForwarders.put("PAPEngineForwarder", pFParameters);
        }
        return policyForwarders;
    }

    /**
     * Returns an instance of PolicyDecoderParameters for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the policyDecoders object
     */
    public Map<String, PolicyDecoderParameters> getPolicyDecoders(final boolean isEmpty) {
        final Map<String, PolicyDecoderParameters> policyDecoders = new HashMap<String, PolicyDecoderParameters>();
        if (!isEmpty) {
            final PolicyDecoderParameters pDParameters = new PolicyDecoderParameters(decoderType, decoderClassName);
            policyDecoders.put("TOSCADecoder", pDParameters);
        }
        return policyDecoders;
    }
}