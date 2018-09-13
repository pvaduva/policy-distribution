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

package org.onap.policy.distribution.reception.decoding.pdpx;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.onap.sdc.toscaparser.api.NodeTemplate;

/**
 * Decodes PDP-X policies from a CSAR file.
 */
public class PolicyDecoderCsarPdpx implements PolicyDecoder<Csar, PdpxPolicy> {

    private static final Logger LOGGER = FlexLogger.getLogger(PolicyDecoderCsarPdpx.class);

    @Override
    public Collection<PdpxPolicy> decode(final Csar csar) throws PolicyDecodingException {
        final List<PdpxPolicy> lPdpxPolicy = new ArrayList<>();
        final ISdcCsarHelper sdcCsarHelper = parseCsar(csar);
        final List<NodeTemplate> lnodeVf = sdcCsarHelper.getServiceVfList();
        LOGGER.debug("the size of Vf = " + lnodeVf.size());
        final ExtractFromNode extractFromNode = new ExtractFromNode();
        extractFromNode.setSdcCsarHelper(sdcCsarHelper);
        for (final NodeTemplate node : lnodeVf) {
            final PdpxPolicy ret = extractFromNode.extractInfo(node);
            if (ret != null) {
                lPdpxPolicy.add(ret);
            }
        }
        return lPdpxPolicy;
    }

    @Override
    public boolean canHandle(final PolicyInput policyInput) {
        return policyInput.getClass().isAssignableFrom(Csar.class);
    }

    /**
     * Parse the input Csar using SDC TOSCA parser.
     *
     * @param csar represents the service TOSCA Csar
     * @return the object to represents the content of input csar
     * @throws PolicyDecodingException if parse fails
     */
    public ISdcCsarHelper parseCsar(final Csar csar) throws PolicyDecodingException {
        ISdcCsarHelper sdcCsarHelper;
        try {
            final SdcToscaParserFactory factory = SdcToscaParserFactory.getInstance();
            LOGGER.debug("Csar File Path = " + csar.getCsarPath());
            final File csarFile = new File(csar.getCsarPath());
            sdcCsarHelper = factory.getSdcCsarHelper(csarFile.getAbsolutePath());
        } catch (final Exception exp) {
            final String message = "Failed passing the csar file";
            LOGGER.error(message, exp);
            throw new PolicyDecodingException(message, exp);
        }
        return sdcCsarHelper;
    }

    @Override
    public void configure(final String parameterGroupName) {}
}