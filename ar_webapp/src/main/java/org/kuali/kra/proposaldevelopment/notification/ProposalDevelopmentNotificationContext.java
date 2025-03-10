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
package org.kuali.kra.proposaldevelopment.notification;

import org.kuali.kra.bo.CoeusModule;
import org.kuali.kra.common.notification.NotificationContextBase;
import org.kuali.kra.common.notification.NotificationRenderer;
import org.kuali.kra.common.notification.service.KcNotificationModuleRoleService;
import org.kuali.kra.common.notification.service.KcNotificationService;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.proposaldevelopment.bo.DevelopmentProposal;
import org.kuali.kra.util.EmailAttachment;

import java.util.List;

/**
 * This class extends the notification context base and provides some helpful
 * functions for any Proposal Development specific events.
 */
public class ProposalDevelopmentNotificationContext extends NotificationContextBase {

    private static final long serialVersionUID = 7899968257291957401L;

    private DevelopmentProposal proposal;
    private String documentNumber;
    private String actionTypeCode;
    private String contextName;
    private List<EmailAttachment> emailAttachments;

    /**
     * Constructs a Proposal Development notification context and sets the
     * necessary services.
     *
     * @param developmentProposal
     * @param actionTypeCode
     * @param contextName
     * @param renderer
     */
    public ProposalDevelopmentNotificationContext(DevelopmentProposal developmentProposal, String actionTypeCode, String contextName,
            NotificationRenderer renderer) {
        super(renderer);

        this.proposal = developmentProposal;
        this.documentNumber = developmentProposal.getProposalDocument().getDocumentNumber();
        this.actionTypeCode = actionTypeCode;
        this.contextName = contextName;

        setNotificationService(KraServiceLocator.getService(KcNotificationService.class));
        setNotificationModuleRoleService(KraServiceLocator.getService(KcNotificationModuleRoleService.class));
        ProposalDevelopmentNotificationRoleQualifierService roleQualifier = KraServiceLocator.getService(ProposalDevelopmentNotificationRoleQualifierService.class);
        roleQualifier.setDevelopmentProposal(developmentProposal);
        setNotificationRoleQualifierService(roleQualifier);
    }

    /**
     *
     * Constructs a ProposalDevelopmentNotificationContext.java using a default
     * Proposal Development Renderer.
     *
     * @param developmentProposal
     * @param actionTypeCode
     * @param contextName
     */
    public ProposalDevelopmentNotificationContext(DevelopmentProposal developmentProposal, String actionTypeCode, String contextName) {
        this(developmentProposal, actionTypeCode, contextName,
                KraServiceLocator.getService(ProposalDevelopmentNotificationRenderer.class));
        ((ProposalDevelopmentNotificationRenderer) this.getRenderer()).setDevelopmentProposal(developmentProposal);
    }

    /**
     * {@inheritDoc}
     *
     * @see
     * org.kuali.kra.common.notification.NotificationContextBase#getModuleCode()
     */
    @Override
    public String getModuleCode() {
        return CoeusModule.PROPOSAL_DEVELOPMENT_MODULE_CODE;
    }

    /**
     * {@inheritDoc}
     *
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

    public DevelopmentProposal getProposal() {
        return proposal;
    }

    public void setProposal(DevelopmentProposal proposal) {
        this.proposal = proposal;
    }

}
