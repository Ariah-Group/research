/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.irb;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.common.notification.bo.KcNotification;
import org.kuali.kra.common.permissions.Permissionable;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.infrastructure.RoleConstants;
import org.kuali.kra.irb.actions.ProtocolAction;
import org.kuali.kra.irb.actions.ProtocolStatus;
import org.kuali.kra.irb.actions.amendrenew.ProtocolModule;
import org.kuali.kra.irb.actions.risklevel.ProtocolRiskLevel;
import org.kuali.kra.irb.actions.submit.ProtocolSubmission;
import org.kuali.kra.irb.actions.submit.ProtocolSubmissionStatus;
import org.kuali.kra.irb.actions.submit.ProtocolSubmissionType;
import org.kuali.kra.irb.noteattachment.ProtocolAttachmentFilter;
import org.kuali.kra.irb.noteattachment.ProtocolAttachmentProtocol;
import org.kuali.kra.irb.personnel.ProtocolPersonnelService;
import org.kuali.kra.irb.protocol.participant.ProtocolParticipant;
import org.kuali.kra.irb.protocol.research.ProtocolResearchArea;
import org.kuali.kra.irb.questionnaire.ProtocolModuleQuestionnaireBean;
import org.kuali.kra.irb.summary.ParticipantSummary;
import org.kuali.kra.irb.summary.ProtocolSummary;
import org.kuali.kra.krms.KrmsRulesContext;
import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.protocol.actions.ProtocolStatusBase;
import org.kuali.kra.protocol.actions.submit.ProtocolSubmissionBase;
import org.kuali.kra.protocol.actions.submit.ProtocolSubmissionStatusBase;
import org.kuali.kra.protocol.actions.submit.ProtocolSubmissionTypeBase;
import org.kuali.kra.protocol.noteattachment.ProtocolAttachmentFilterBase;
import org.kuali.kra.protocol.noteattachment.ProtocolAttachmentProtocolBase;
import org.kuali.kra.protocol.protocol.research.ProtocolResearchAreaBase;
import org.kuali.kra.questionnaire.answer.AnswerHeader;
import org.kuali.kra.questionnaire.answer.ModuleQuestionnaireBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.ariahgroup.research.irb.keywords.IrbProtocolKeyword;
import org.kuali.kra.bo.ScienceKeyword;
import org.kuali.kra.document.KeywordsManager;

/**
 *
 * This class is Protocol Business Object.
 */
public class Protocol extends ProtocolBase implements KeywordsManager<IrbProtocolKeyword> {

    private static final long serialVersionUID = 4396393806439396971L;

    private static final String DEFAULT_PROTOCOL_TYPE_CODE = "1";

    private String vulnerableSubjectIndicator;
    private List<ProtocolRiskLevel> protocolRiskLevels;
    private List<ProtocolParticipant> protocolParticipants;
    private transient boolean lookupActionNotifyIRBProtocol;
    private List<IrbProtocolKeyword> keywords;

    /**
     *
     * Constructs an Protocol BO.
     */
    public Protocol() {
        super();
        protocolRiskLevels = new ArrayList<ProtocolRiskLevel>();
        protocolParticipants = new ArrayList<ProtocolParticipant>();
        keywords = new ArrayList<IrbProtocolKeyword>();
    }

    public String getVulnerableSubjectIndicator() {
        return vulnerableSubjectIndicator;
    }

    public void setVulnerableSubjectIndicator(String vulnerableSubjectIndicator) {
        this.vulnerableSubjectIndicator = vulnerableSubjectIndicator;
    }

    public List<ProtocolRiskLevel> getProtocolRiskLevels() {
        return protocolRiskLevels;
    }

    public void setProtocolRiskLevels(List<ProtocolRiskLevel> protocolRiskLevels) {
        this.protocolRiskLevels = protocolRiskLevels;
        for (ProtocolRiskLevel riskLevel : protocolRiskLevels) {
            riskLevel.init(this);
        }
    }

    public List<ProtocolParticipant> getProtocolParticipants() {
        return protocolParticipants;
    }

    public void setProtocolParticipants(List<ProtocolParticipant> protocolParticipants) {
        this.protocolParticipants = protocolParticipants;
        for (ProtocolParticipant participant : protocolParticipants) {
            participant.init(this);
        }
    }

    /**
     * Gets index i from the protocol participant list.
     *
     * @param index
     * @return protocol participant at index i
     */
    public ProtocolParticipant getProtocolParticipant(int index) {
        return getProtocolParticipants().get(index);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getProtocolRiskLevels());
        managedLists.add(getProtocolParticipants());
        return managedLists;
    }

    /**
     * This method is to get protocol personnel service
     *
     * @return protocolPersonnelService
     */
    @Override
    protected ProtocolPersonnelService getProtocolPersonnelService() {
        ProtocolPersonnelService protocolPersonnelService = KraServiceLocator.getService(ProtocolPersonnelService.class);
        return protocolPersonnelService;
    }

    @Override
    public ProtocolSubmission getProtocolSubmission() {
        return (ProtocolSubmission) super.getProtocolSubmission();
    }

    @Override
    public ProtocolAction getLastProtocolAction() {
        return (ProtocolAction) super.getLastProtocolAction();
    }

    /**
     *
     * This method merges the data of a specific module of the amended protocol
     * into this protocol.
     *
     * @param amendment
     * @param protocolModuleTypeCode
     */
    @Override
    public void merge(ProtocolBase amendment, String protocolModuleTypeCode) {
        if (StringUtils.equals(protocolModuleTypeCode, ProtocolModule.GENERAL_INFO)) {
            mergeGeneralInfo(amendment);
        } else if (StringUtils.equals(protocolModuleTypeCode, ProtocolModule.AREAS_OF_RESEARCH)) {
            mergeResearchAreas(amendment);
        } else if (StringUtils.equals(protocolModuleTypeCode, ProtocolModule.FUNDING_SOURCE)) {
            mergeFundingSources(amendment);
        } else if (StringUtils.equals(protocolModuleTypeCode, ProtocolModule.PROTOCOL_ORGANIZATIONS)) {
            mergeOrganizations(amendment);
        } else if (StringUtils.equals(protocolModuleTypeCode, ProtocolModule.PROTOCOL_PERSONNEL)) {
            mergePersonnel(amendment);
        } else if (StringUtils.equals(protocolModuleTypeCode, ProtocolModule.ADD_MODIFY_ATTACHMENTS)) {
            if (amendment.isAmendment() || amendment.isRenewal()
                    || (!amendment.getAttachmentProtocols().isEmpty() && this.getAttachmentProtocols().isEmpty())) {
                mergeAttachments(amendment);
            } else {
                restoreAttachments(this);
            }
        } else if (StringUtils.equals(protocolModuleTypeCode, ProtocolModule.PROTOCOL_REFERENCES)) {
            mergeReferences(amendment);
        } else if (StringUtils.equals(protocolModuleTypeCode, ProtocolModule.SPECIAL_REVIEW)) {
            mergeSpecialReview(amendment);
        } else if (StringUtils.equals(protocolModuleTypeCode, ProtocolModule.SUBJECTS)) {
            mergeSubjects((Protocol) amendment);
        } else if (StringUtils.equals(protocolModuleTypeCode, ProtocolModule.OTHERS)) {
            mergeOthers(amendment);
        } else if (StringUtils.equals(protocolModuleTypeCode, ProtocolModule.PROTOCOL_PERMISSIONS)) {
            mergeProtocolPermissions(amendment);
        } else if (StringUtils.equals(protocolModuleTypeCode, ProtocolModule.QUESTIONNAIRE)) {
            mergeProtocolQuestionnaire(amendment);
        }
    }

    /*
     * get submit for review questionnaire answerheaders
     */
    @Override
    public List<AnswerHeader> getAnswerHeaderForProtocol(ProtocolBase protocol) {
        ModuleQuestionnaireBean moduleQuestionnaireBean = new ProtocolModuleQuestionnaireBean((Protocol) protocol);
        moduleQuestionnaireBean.setModuleSubItemCode("0");
        List<AnswerHeader> answerHeaders = getQuestionnaireAnswerService().getQuestionnaireAnswer(moduleQuestionnaireBean);
        return answerHeaders;
    }

    /*
     * merge amendment/renewal protocol action to original protocol when A/R is approved
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void mergeProtocolAction(ProtocolBase amendment) {
        List<ProtocolAction> protocolActions = (List<ProtocolAction>) deepCopy(amendment.getProtocolActions());
        Collections.sort(protocolActions, new Comparator<ProtocolAction>() {
            @Override
            public int compare(ProtocolAction action1, ProtocolAction action2) {
                return action1.getActionId().compareTo(action2.getActionId());
            }
        });
        // the first 1 'protocol created is already added to original protocol
        // the last one is 'approve'
        protocolActions.remove(0);
        protocolActions.remove(protocolActions.size() - 1);
        for (ProtocolAction protocolAction : protocolActions) {
            protocolAction.setProtocolNumber(this.getProtocolNumber());
            protocolAction.setProtocolActionId(null);
            protocolAction.setSequenceNumber(getSequenceNumber());
            protocolAction.setProtocolId(this.getProtocolId());
            String index = amendment.getProtocolNumber().substring(11);
            protocolAction.setActionId(getNextValue(NEXT_ACTION_ID_KEY));
            String type = "Amendment";
            if (amendment.isRenewal()) {
                type = "Renewal";
            }
            if (StringUtils.isNotBlank(protocolAction.getComments())) {
                protocolAction.setComments(type + "-" + index + ": " + protocolAction.getComments());
            } else {
                protocolAction.setComments(type + "-" + index + ": ");
            }
            // reset persistence state for copied notifications so they break ties with old document
            for (KcNotification notification : protocolAction.getProtocolNotifications()) {
                notification.setDocumentNumber(getProtocolDocument().getDocumentNumber());
                notification.resetPersistenceState();
                notification.setOwningDocumentIdFk(null);
            }
            this.getProtocolActions().add(protocolAction);
        }
    }

    @SuppressWarnings("unchecked")
    private void mergeSubjects(Protocol amendment) {
        setProtocolParticipants((List<ProtocolParticipant>) deepCopy(amendment.getProtocolParticipants()));
    }

    @Override
    public ProtocolSummary getProtocolSummary() {
        ProtocolSummary protocolSummary = createProtocolSummary();
        addPersonnelSummaries(protocolSummary);
        addResearchAreaSummaries(protocolSummary);
        addAttachmentSummaries(protocolSummary);
        addFundingSourceSummaries(protocolSummary);
        addParticipantSummaries(protocolSummary);
        addOrganizationSummaries(protocolSummary);
        addSpecialReviewSummaries(protocolSummary);
        addAdditionalInfoSummary(protocolSummary);
        return protocolSummary;
    }

    private void addParticipantSummaries(ProtocolSummary protocolSummary) {
        for (ProtocolParticipant participant : this.getProtocolParticipants()) {
            ParticipantSummary participantSummary = new ParticipantSummary();
            participantSummary.setDescription(participant.getParticipantType().getDescription());
            participantSummary.setCount(participant.getParticipantCount());
            protocolSummary.add(participantSummary);
        }
    }

    @Override
    protected ProtocolSummary createProtocolSummary() {
        ProtocolSummary summary = new ProtocolSummary();
        summary.setLastProtocolAction(getLastProtocolAction());
        summary.setProtocolNumber(getProtocolNumber());
        summary.setPiName(getPrincipalInvestigator().getPersonName());
        summary.setPiProtocolPersonId(getPrincipalInvestigator().getProtocolPersonId());
        summary.setInitialSubmissionDate(getInitialSubmissionDate());
        summary.setApprovalDate(getApprovalDate());
        summary.setLastApprovalDate(getLastApprovalDate());
        summary.setExpirationDate(getExpirationDate());
        if (getProtocolType() == null) {
            refreshReferenceObject("protocolType");
        }
        summary.setType(getProtocolType().getDescription());
        if (getProtocolStatus() == null) {
            refreshReferenceObject("protocolStatus");
        }
        summary.setStatus(getProtocolStatus().getDescription());
        summary.setTitle(getTitle());
        return summary;
    }

    /**
     *
     * @see org.kuali.kra.common.permissions.Permissionable#getDocumentKey()
     */
    @Override
    public String getDocumentKey() {
        return Permissionable.PROTOCOL_KEY;
    }

    /**
     *
     * @see org.kuali.kra.common.permissions.Permissionable#getRoleNames()
     */
    @Override
    public List<String> getRoleNames() {
        List<String> roleNames = new ArrayList<String>();

        roleNames.add(RoleConstants.PROTOCOL_AGGREGATOR);
        roleNames.add(RoleConstants.PROTOCOL_VIEWER);

        return roleNames;
    }

    @Override
    public String getNamespace() {
        return Constants.MODULE_NAMESPACE_PROTOCOL;
    }

    /**
     *
     * @see org.kuali.kra.UnitAclLoadable#getDocumentRoleTypeCode()
     */
    @Override
    public String getDocumentRoleTypeCode() {
        return RoleConstants.PROTOCOL_DOC_ROLE_TYPE;
    }

    @Override
    public void initializeProtocolAttachmentFilter() {
        ProtocolAttachmentFilterBase protocolAttachmentFilter = new ProtocolAttachmentFilter();

        //Lets see if there is a default set for the attachment sort
        try {
            String defaultSortBy = getParameterService().getParameterValueAsString(ProtocolDocument.class, Constants.PARAMETER_PROTOCOL_ATTACHMENT_DEFAULT_SORT);
            if (StringUtils.isNotBlank(defaultSortBy)) {
                protocolAttachmentFilter.setSortBy(defaultSortBy);
            }
        } catch (Exception e) {
            //No default found, do nothing.
        }

        setProtocolAttachmentFilter(protocolAttachmentFilter);
    }

    @Override
    public KrmsRulesContext getKrmsRulesContext() {
        return (KrmsRulesContext) getProtocolDocument();
    }

    @Override
    protected ProtocolStatusBase getProtocolStatusNewInstanceHook() {
        return new ProtocolStatus();
    }

    @Override
    protected String getDefaultProtocolStatusCodeHook() {
        return Constants.DEFAULT_PROTOCOL_STATUS_CODE;
    }

    @Override
    protected String getDefaultProtocolTypeCodeHook() {
        return DEFAULT_PROTOCOL_TYPE_CODE;
    }

    @Override
    protected ProtocolResearchAreaBase getNewProtocolResearchAreaInstance() {
        return new ProtocolResearchArea();
    }

    @Override
    protected ProtocolSubmissionStatusBase getProtocolSubmissionStatusNewInstanceHook() {
        return new ProtocolSubmissionStatus();
    }

    @Override
    protected ProtocolSubmissionTypeBase getProtocolSubmissionTypeNewInstanceHook() {
        return new ProtocolSubmissionType();
    }

    @Override
    protected ProtocolSubmissionBase getProtocolSubmissionNewInstanceHook() {
        return new ProtocolSubmission();
    }

    @Override
    protected String getProtocolModuleAddModifyAttachmentCodeHook() {
        return ProtocolModule.ADD_MODIFY_ATTACHMENTS;
    }

    @Override
    protected Class<? extends ProtocolAttachmentProtocolBase> getProtocolAttachmentProtocolClassHook() {
        return ProtocolAttachmentProtocol.class;
    }

    public boolean isLookupActionNotifyIRBProtocol() {
        return lookupActionNotifyIRBProtocol;
    }

    public void setLookupActionNotifyIRBProtocol(boolean lookupActionNotifyIRBProtocol) {
        this.lookupActionNotifyIRBProtocol = lookupActionNotifyIRBProtocol;
    }

    /**
     * Gets the keywords attribute.
     *
     * @return Returns the keywords.
     */
    @Override
    public List<IrbProtocolKeyword> getKeywords() {
        return keywords;
    }

    /**
     * Sets the keywords attribute value.
     *
     * @param keywords The keywords to set.
     */
    public void setKeywords(List<IrbProtocolKeyword> keywords) {
        this.keywords = keywords;
    }

    /**
     * Add selected science keyword to award science keywords list.
     *
     * @see
     * org.kuali.kra.document.KeywordsManager#addKeyword(org.kuali.kra.bo.ScienceKeyword)
     */
    @Override
    public void addKeyword(ScienceKeyword scienceKeyword) {
        IrbProtocolKeyword protKeyword = new IrbProtocolKeyword(getProtocolId(), scienceKeyword);
        getKeywords().add(protKeyword);
    }

    /**
     * It returns the ScienceKeyword object from keywords list
     *
     * @see org.kuali.kra.document.KeywordsManager#getKeyword(int)
     */
    @Override
    public IrbProtocolKeyword getKeyword(int index) {
        return getKeywords().get(index);
    }
}
