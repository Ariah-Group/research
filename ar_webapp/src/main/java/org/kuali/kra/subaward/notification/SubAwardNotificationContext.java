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
package org.kuali.kra.subaward.notification;

import org.kuali.kra.bo.CoeusModule;
import org.kuali.kra.common.notification.NotificationContextBase;
import org.kuali.kra.common.notification.NotificationRenderer;
import org.kuali.kra.common.notification.service.KcNotificationModuleRoleService;
import org.kuali.kra.common.notification.service.KcNotificationService;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.subaward.bo.SubAward;
import org.kuali.kra.util.EmailAttachment;

import java.util.List;

/**
 * This class extends the notification context base and provides some helpful
 * functions for any Subaward specific events.
 */
public class SubAwardNotificationContext extends NotificationContextBase {

    private SubAward subAward;
    private String documentNumber;
    private String actionTypeCode;
    private String contextName;
    private List<EmailAttachment> emailAttachments;
    private String forwardName;

    /**
     * Constructs an Subaward notification context and sets the necessary
     * services.
     *
     * @param subAward
     * @param actionTypeCode
     * @param contextName
     * @param renderer
     * @param forwardName
     */
    public SubAwardNotificationContext(SubAward subAward, String actionTypeCode, String contextName, NotificationRenderer renderer, String forwardName) {
        super(renderer);

        this.subAward = subAward;
        this.documentNumber = subAward.getSubAwardDocument().getDocumentNumber();
        this.actionTypeCode = actionTypeCode;
        this.contextName = contextName;
        this.forwardName = forwardName;

        setNotificationService(KraServiceLocator.getService(KcNotificationService.class));
        setNotificationModuleRoleService(KraServiceLocator.getService(KcNotificationModuleRoleService.class));
        setNotificationRoleQualifierService(KraServiceLocator.getService(SubAwardNotificationRoleQualifierService.class));
        ((SubAwardNotificationRoleQualifierService) getNotificationRoleQualifierService()).setSubAward(subAward);
    }

    public SubAwardNotificationContext(SubAward subAward, String actionTypeCode, String contextName) {
        this(subAward, actionTypeCode, contextName, new SubAwardNotificationRenderer(subAward), Constants.MAPPING_AWARD_ACTIONS_PAGE);
        ((SubAwardNotificationRenderer) getRenderer()).setSubAward(subAward);
    }

    public SubAwardNotificationContext(SubAward subAward, String actionTypeCode, String contextName, String forwardName) {
        this(subAward, actionTypeCode, contextName, new SubAwardNotificationRenderer(subAward), forwardName);
        ((SubAwardNotificationRenderer) getRenderer()).setSubAward(subAward);
    }

    /**
     * {@inheritDoc}
     *
     * @return
     * @see
     * org.kuali.kra.common.notification.NotificationContextBase#getModuleCode()
     */
    @Override
    public String getModuleCode() {
        return CoeusModule.SUBAWARD_MODULE_CODE;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     * @see
     * org.kuali.kra.common.notification.NotificationContextBase#getDocumentNumber()
     */
    @Override
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     * @see
     * org.kuali.kra.common.notification.NotificationContext#getActionTypeCode()
     */
    @Override
    public String getActionTypeCode() {
        return actionTypeCode;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     * @see
     * org.kuali.kra.common.notification.NotificationContext#getContextName()
     */
    @Override
    public String getContextName() {
        return contextName;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     * @see
     * org.kuali.kra.common.notification.NotificationContext#getEmailAttachments()
     */
    @Override
    public List<EmailAttachment> getEmailAttachments() {
        return emailAttachments;
    }

    /**
     * {@inheritDoc}
     *
     * @param emailAttachments
     */
    public void setEmailAttachments(List<EmailAttachment> emailAttachments) {
        this.emailAttachments = emailAttachments;
    }

    public SubAward getSubAward() {
        return subAward;
    }

    public void setSubAward(SubAward subAward) {
        this.subAward = subAward;
    }

    public String getForwardName() {
        return forwardName;
    }

    public void setForwardName(String forwardName) {
        this.forwardName = forwardName;
    }

}
