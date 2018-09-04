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
package org.onap.policy.distribution.model;

import java.util.Date;

/**
 * An optimization policy
 */
public class OptimizationPolicy implements Policy {

    private static final String OPTIMIZATION = "Optimization";
    private String policyName;
    private String policyDescription;
    private String policyConfigType;
    private String onapName;
    private String configBody;
    private String configBodyType;
    private Date timetolive;
    private String guard;
    private String riskLevel;
    private String riskType;

    @Override
    public String getPolicyName() {
        return policyName;
    }

    @Override
    public String getPolicyType() {
        return OPTIMIZATION;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyDescription() {
        return policyDescription;
    }

    public void setPolicyDescription(String policyDescription) {
        this.policyDescription = policyDescription;
    }

    public String getPolicyConfigType() {
        return policyConfigType;
    }

    public void setPolicyConfigType(String policyConfigType) {
        this.policyConfigType = policyConfigType;
    }

    public String getOnapName() {
        return onapName;
    }

    public void setOnapName(String onapName) {
        this.onapName = onapName;
    }

    public String getConfigBody() {
        return configBody;
    }

    public void setConfigBody(String configBody) {
        this.configBody = configBody;
    }

    public String getConfigBodyType() {
        return configBodyType;
    }

    public void setConfigBodyType(String configBodyType) {
        this.configBodyType = configBodyType;
    }

    public Date getTimetolive() {
        return timetolive;
    }

    public void setTimetolive(Date timetolive) {
        this.timetolive = timetolive;
    }

    public String getGuard() {
        return guard;
    }

    public void setGuard(String guard) {
        this.guard = guard;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }
}