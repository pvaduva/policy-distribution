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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;
import org.junit.Test;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.distribution.forwarding.parameters.PolicyForwarderParameters;
import org.onap.policy.distribution.reception.parameters.PluginHandlerParameters;
import org.onap.policy.distribution.reception.parameters.PolicyDecoderParameters;

/**
 * Class to perform unit test of PluginHandlerParameters.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class TestPluginHandlerParameters {
    CommonTestData commonTestData = new CommonTestData();

    @Test
    public void testPluginHandlerParameters() {
        final Map<String, PolicyDecoderParameters> policyDecoders = commonTestData.getPolicyDecoders(false);
        final Map<String, PolicyForwarderParameters> policyForwarders = commonTestData.getPolicyForwarders(false);
        final PluginHandlerParameters pHParameters = new PluginHandlerParameters(policyDecoders, policyForwarders);
        final GroupValidationResult validationResult = pHParameters.validate();
        assertEquals(policyDecoders.get(CommonTestData.DUMMY_DECODER_KEY),
                        pHParameters.getPolicyDecoders().get(CommonTestData.DUMMY_DECODER_KEY));
        assertEquals(policyForwarders.get(CommonTestData.DUMMY_ENGINE_FORWARDER_KEY),
                        pHParameters.getPolicyForwarders().get(CommonTestData.DUMMY_ENGINE_FORWARDER_KEY));
        assertTrue(validationResult.isValid());
    }

    @Test
    public void testPluginHandlerParameters_NullPolicyDecoders() {
        try {
            final Map<String, PolicyForwarderParameters> policyForwarders = commonTestData.getPolicyForwarders(false);
            final PluginHandlerParameters pHParameters = new PluginHandlerParameters(null, policyForwarders);
            pHParameters.validate();
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains("map parameter \"policyDecoders\" is null"));
        }
    }

    @Test
    public void testPluginHandlerParameters_NullPolicyForwarders() {
        try {
            final Map<String, PolicyDecoderParameters> policyDecoders = commonTestData.getPolicyDecoders(false);
            final PluginHandlerParameters pHParameters = new PluginHandlerParameters(policyDecoders, null);
            pHParameters.validate();
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains("map parameter \"policyForwarders\" is null"));
        }
    }

    @Test
    public void testPluginHandlerParameters_EmptyPolicyDecoders() {
        final Map<String, PolicyDecoderParameters> policyDecoders = commonTestData.getPolicyDecoders(true);
        final Map<String, PolicyForwarderParameters> policyForwarders = commonTestData.getPolicyForwarders(false);
        final PluginHandlerParameters pHParameters = new PluginHandlerParameters(policyDecoders, policyForwarders);
        GroupValidationResult result = pHParameters.validate();
        assertFalse(result.isValid());
        assertTrue(result.getResult().endsWith("must have at least one policy decoder\n"));
    }

    @Test
    public void testPluginHandlerParameters_EmptyPolicyForwarders() {
        final Map<String, PolicyForwarderParameters> policyForwarders = commonTestData.getPolicyForwarders(true);
        final Map<String, PolicyDecoderParameters> policyDecoders = commonTestData.getPolicyDecoders(false);
        final PluginHandlerParameters pHParameters = new PluginHandlerParameters(policyDecoders, policyForwarders);
        GroupValidationResult result = pHParameters.validate();
        assertFalse(result.isValid());
        assertTrue(result.getResult().endsWith("must have at least one policy forwarder\n"));
    }
}
