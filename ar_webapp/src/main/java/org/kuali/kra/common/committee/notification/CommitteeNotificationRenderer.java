/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.common.committee.notification;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.common.committee.bo.CommitteeBase;
import org.kuali.kra.common.notification.NotificationRendererBase;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.service.KcPersonService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;

import java.util.Map;

/**
 * Renders fields for the IRB notifications.
 */
public class CommitteeNotificationRenderer extends NotificationRendererBase {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 6334584979829689495L;

    private CommitteeBase committee;

    private transient BusinessObjectService businessObjectService;
    private transient KcPersonService kcPersonService;

    /**
     * Constructs an IRB notification renderer.
     *
     * @param CommitteeBase
     */
    public CommitteeNotificationRenderer(CommitteeBase committee) {
        this.committee = committee;
    }

    /**
     * {@inheritDoc}
     *
     * @see
     * org.kuali.kra.common.notification.NotificationRenderer#getReplacementParameters()
     */
    public Map<String, String> getDefaultReplacementParameters() {
        String[] replacementParameters = CommitteeReplacementParameters.REPLACEMENT_PARAMETERS;

        Map<String, String> params = super.getDefaultReplacementParameters();

        String key = null;
        for (int i = 0; i < replacementParameters.length; i++) {
            key = replacementParameters[i];

            if (StringUtils.equals(key, CommitteeReplacementParameters.DOCUMENT_NUMBER)) {
                params.put(key, committee.getCommitteeDocument().getDocumentNumber());
            } else if (StringUtils.equals(key, CommitteeReplacementParameters.SEQUENCE_NUMBER)) {
                params.put(key, committee.getSequenceNumber().toString());
            } else if (StringUtils.equals(key, CommitteeReplacementParameters.COMMITTEE_NAME)) {
                params.put(key, committee.getCommitteeName());
            }

        }
        return params;
    }

    public CommitteeBase getCommittee() {
        return committee;
    }

    public void setCommittee(CommitteeBase committee) {
        this.committee = committee;
    }

    public BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = KRADServiceLocator.getBusinessObjectService();
        }
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public KcPersonService getKcPersonService() {
        if (kcPersonService == null) {
            kcPersonService = KraServiceLocator.getService(KcPersonService.class);
        }
        return kcPersonService;
    }

    public void setKcPersonService(KcPersonService kcPersonService) {
        this.kcPersonService = kcPersonService;
    }

//    private ConfigurationService getKualiConfigurationService() {
//        return CoreApiServiceLocator.getKualiConfigurationService();
//    }
}
