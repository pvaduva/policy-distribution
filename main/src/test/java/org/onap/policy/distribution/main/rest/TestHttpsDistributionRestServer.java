/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
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

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Test;
import org.onap.policy.common.endpoints.report.HealthCheckReport;
import org.onap.policy.common.utils.network.NetworkUtil;
import org.onap.policy.distribution.main.PolicyDistributionException;
import org.onap.policy.distribution.main.startstop.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to perform unit test of HealthCheckMonitor.
 *
 * @author Libo Zhu (libo.zhu@intel.com)
 */
public class TestHttpsDistributionRestServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestHttpsDistributionRestServer.class);
    private static final String ALIVE = "alive";
    private static final String SELF = "self";
    private static final String NAME = "Policy SSD";
    private static String KEYSTORE = System.getProperty("user.dir") + "/src/test/resources/ssl/policy-keystore";

    @Test
    public void testHttpsHealthCheckSuccess() {
        final String reportString = "Report [name=Policy SSD, url=self, healthy=true, code=200, message=alive]";
        try {
            final Main main = startDistributionService();
            final HealthCheckReport report = performHealthCheck();
            validateReport(NAME, SELF, true, 200, ALIVE, reportString, report);
            stopDistributionService(main);
        } catch (final Exception exp) {
            LOGGER.error("testHttpsHealthCheckSuccess failed", exp);
            fail("Test should not throw an exception");
        }
    }

    private Main startDistributionService() {
        final Properties systemProps = System.getProperties();
        systemProps.put("javax.net.ssl.keyStore", KEYSTORE);
        systemProps.put("javax.net.ssl.keyStorePassword", "Pol1cy_0nap");
        System.setProperties(systemProps);

        final String[] distributionConfigParameters = { "-c", "parameters/DistributionConfigParameters_Https.json" };
        return new Main(distributionConfigParameters);
    }

    private void stopDistributionService(final Main main) throws PolicyDistributionException {
        main.shutdown();
    }

    private HealthCheckReport performHealthCheck() throws Exception {

        final TrustManager[] noopTrustManager = new TrustManager[] { new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override
            public void checkClientTrusted(final java.security.cert.X509Certificate[] certs, final String authType) {}

            @Override
            public void checkServerTrusted(final java.security.cert.X509Certificate[] certs, final String authType) {}
        } };

        final SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, noopTrustManager, new SecureRandom());
        final ClientBuilder clientBuilder =
                ClientBuilder.newBuilder().sslContext(sc).hostnameVerifier((host, session) -> true);
        final Client client = clientBuilder.build();
        final HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("healthcheck", "zb!XztG34");
        client.register(feature);

        final WebTarget webTarget = client.target("https://localhost:6969/healthcheck");

        final Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        if (!NetworkUtil.isTcpPortOpen("localhost", 6969, 6, 10000L)) {
            throw new IllegalStateException("cannot connect to port 6969");
        }
        return invocationBuilder.get(HealthCheckReport.class);
    }

    private void validateReport(final String name, final String url, final boolean healthy, final int code,
            final String message, final String reportString, final HealthCheckReport report) {
        assertEquals(name, report.getName());
        assertEquals(url, report.getUrl());
        assertEquals(healthy, report.isHealthy());
        assertEquals(code, report.getCode());
        assertEquals(message, report.getMessage());
        assertEquals(reportString, report.toString());
    }
}
