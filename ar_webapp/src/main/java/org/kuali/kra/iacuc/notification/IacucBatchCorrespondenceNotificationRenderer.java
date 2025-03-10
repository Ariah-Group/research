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
package org.kuali.kra.iacuc.notification;

import org.kuali.kra.iacuc.IacucProtocol;

import java.util.Map;

/**
 * Renders additional fields for the Batch Correspondence notification.
 */
public class IacucBatchCorrespondenceNotificationRenderer extends IacucProtocolNotificationRenderer {

    private static final long serialVersionUID = -3536458485352249776L;

    private Long detailId;
    private String protocolCorrespondenceType;
    private String userFullName;

    /**
     * Constructs a Batch Correspondence notification renderer.
     * 
     * @param protocol
     * @param detailId
     * @param protocolCorrespondenceType
     * @param userFullName
     */
    public IacucBatchCorrespondenceNotificationRenderer(IacucProtocol protocol, Long detailId, String protocolCorrespondenceType, String userFullName) {
        super(protocol);
        
        this.detailId = detailId;
        this.protocolCorrespondenceType = protocolCorrespondenceType;
        this.userFullName = userFullName;
    }

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public String getProtocolCorrespondenceType() {
        return protocolCorrespondenceType;
    }

    public void setProtocolCorrespondenceType(String protocolCorrespondenceType) {
        this.protocolCorrespondenceType = protocolCorrespondenceType;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    /**
     * {@inheritDoc}
     * @see org.kuali.kra.common.notification.NotificationContext#replaceContextVariables(java.lang.String)
     */
    @Override
    public Map<String, String> getDefaultReplacementParameters() {
        Map<String, String> params = super.getDefaultReplacementParameters();
        params.put("{DETAIL_ID}", detailId.toString());
        params.put("{PROTOCOL_CORRESPONDENCE_TYPE}", protocolCorrespondenceType);
        return params;
    }

}