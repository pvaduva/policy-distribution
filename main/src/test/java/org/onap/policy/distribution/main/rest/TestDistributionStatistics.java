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

package org.onap.policy.distribution.main.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Test;
import org.onap.policy.common.utils.network.NetworkUtil;
import org.onap.policy.distribution.main.PolicyDistributionException;
import org.onap.policy.distribution.main.parameters.CommonTestData;
import org.onap.policy.distribution.main.parameters.RestServerParameters;
import org.onap.policy.distribution.main.startstop.Main;
import org.onap.policy.distribution.reception.statistics.DistributionStatisticsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to perform unit test of {@link DistributionRestController}.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class TestDistributionStatistics {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestDistributionStatistics.class);


    @Test
    public void testDistributionStatistics_200() {
        try {
            final Main main = startDistributionService();
            StatisticsReport report = getDistributionStatistics();
            validateReport(report, 0, 200);
            updateDistributionStatistics();
            report = getDistributionStatistics();
            validateReport(report, 1, 200);
            stopDistributionService(main);
            DistributionStatisticsManager.resetAllStatistics();
        } catch (final Exception exp) {
            LOGGER.error("testDistributionStatistics_200 failed", exp);
            fail("Test should not throw an exception");
        }
    }

    @Test
    public void testDistributionStatistics_500() throws InterruptedException {
        final RestServerParameters restServerParams = new CommonTestData().getRestServerParameters(false);
        restServerParams.setName(CommonTestData.DISTRIBUTION_GROUP_NAME);
        final DistributionRestServer restServer = new DistributionRestServer(restServerParams);
        try {
            restServer.start();
            final StatisticsReport report = getDistributionStatistics();
            validateReport(report, 0, 500);
            restServer.shutdown();
            DistributionStatisticsManager.resetAllStatistics();
        } catch (final Exception exp) {
            LOGGER.error("testDistributionStatistics_500 failed", exp);
            fail("Test should not throw an exception");
        }
    }

    private Main startDistributionService() {
        final String[] distributionConfigParameters = { "-c", "parameters/DistributionConfigParameters.json" };
        return new Main(distributionConfigParameters);
    }

    private void stopDistributionService(final Main main) throws PolicyDistributionException {
        main.shutdown();
    }

    private StatisticsReport getDistributionStatistics() throws InterruptedException, IOException {
        final ClientConfig clientConfig = new ClientConfig();

        final HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("healthcheck", "zb!XztG34");
        clientConfig.register(feature);

        final Client client = ClientBuilder.newClient(clientConfig);
        final WebTarget webTarget = client.target("http://localhost:6969/statistics");

        final Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        if (!NetworkUtil.isTcpPortOpen("localhost", 6969, 6, 10000L)) {
            throw new IllegalStateException("cannot connect to port 6969");
        }
        return invocationBuilder.get(StatisticsReport.class);
    }

    private void updateDistributionStatistics() {
        DistributionStatisticsManager.updateTotalDistributionCount();
        DistributionStatisticsManager.updateDistributionSuccessCount();
        DistributionStatisticsManager.updateDistributionFailureCount();
        DistributionStatisticsManager.updateTotalDownloadCount();
        DistributionStatisticsManager.updateDownloadSuccessCount();
        DistributionStatisticsManager.updateDownloadFailureCount();
    }

    private void validateReport(final StatisticsReport report, final int count, final int code) {
        assertEquals(code, report.getCode());
        assertEquals(count, report.getTotalDistributionCount());
        assertEquals(count, report.getDistributionSuccessCount());
        assertEquals(count, report.getDistributionFailureCount());
        assertEquals(count, report.getTotalDownloadCount());
        assertEquals(count, report.getDownloadSuccessCount());
        assertEquals(count, report.getDownloadFailureCount());
    }
}
