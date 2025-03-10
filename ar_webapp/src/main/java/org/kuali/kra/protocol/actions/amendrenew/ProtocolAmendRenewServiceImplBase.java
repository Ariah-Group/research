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
package org.kuali.kra.protocol.actions.amendrenew;

import org.kuali.kra.dao.KraLookupDao;
import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.protocol.ProtocolDocumentBase;
import org.kuali.kra.protocol.ProtocolFinderDao;
import org.kuali.kra.protocol.actions.ProtocolActionBase;
import org.kuali.kra.protocol.actions.copy.ProtocolCopyService;
import org.kuali.kra.protocol.noteattachment.ProtocolAttachmentProtocolBase;
import org.kuali.kra.questionnaire.answer.AnswerHeader;
import org.kuali.kra.questionnaire.answer.ModuleQuestionnaireBean;
import org.kuali.kra.questionnaire.answer.QuestionnaireAnswerService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The ProtocolBase Amendment/Renewal Service Implementation.
 */
/**
 * This class...
 */
public abstract class ProtocolAmendRenewServiceImplBase implements ProtocolAmendRenewService {

    protected static final String AMEND_ID = "A";
    protected static final String RENEW_ID = "R";
    protected static final int DIGIT_COUNT = 3;
    protected static final String AMEND_NEXT_VALUE = "nextAmendValue";
    protected static final String RENEW_NEXT_VALUE = "nextRenewValue";
    protected static final String AMENDMENT = "Amendment";
    protected static final String RENEWAL = "Renewal";
    protected static final String CREATED = "Created";
    protected static final String SUMMARY = "Summary";
    protected static final String PROTOCOL_NUMBER = "protocolNumber";
    protected static final String PROTOCOL_STATUS = "protocolStatus";

    protected DocumentService documentService;
    protected ProtocolCopyService protocolCopyService;
    protected KraLookupDao kraLookupDao;
    protected ProtocolFinderDao protocolFinderDao;

    protected QuestionnaireAnswerService questionnaireAnswerService;
    protected BusinessObjectService businessObjectService;

    /**
     * Set the Document Service.
     *
     * @param documentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Set the ProtocolBase Copy Service.
     *
     * @param protocolCopyService
     */
    public void setProtocolCopyService(ProtocolCopyService protocolCopyService) {
        this.protocolCopyService = protocolCopyService;
    }

    /**
     * Set the KRA Lookup DAO.
     *
     * @param kraLookupDao
     */
    public void setKraLookupDao(KraLookupDao kraLookupDao) {
        this.kraLookupDao = kraLookupDao;
    }

    /**
     * Set the KRA Lookup DAO.
     *
     * @param protocolFinderDao
     */
    public void setProtocolFinderDao(ProtocolFinderDao protocolFinderDao) {
        this.protocolFinderDao = protocolFinderDao;
    }

    /**
     * @throws java.lang.Exception
     * @see
     * org.kuali.kra.protocol.actions.amendrenew.ProtocolAmendRenewService#createAmendment(org.kuali.kra.protocol.ProtocolDocumentBase,
     * org.kuali.kra.protocol.actions.amendrenew.ProtocolAmendmentBean)
     */
    @Override
    public String createAmendment(ProtocolDocumentBase protocolDocument, ProtocolAmendmentBean amendmentBean) throws Exception {
        ProtocolDocumentBase amendProtocolDocument = null;
        try {
            //since the user probably doesn't have permission to create the document, we are going to add session variable so the document
            //authorizer knows to approve the user for initiating the document
            GlobalVariables.getUserSession().addObject(AMEND_RENEW_ALLOW_NEW_PROTOCOL_DOCUMENT, Boolean.TRUE);
            amendProtocolDocument = protocolCopyService.copyProtocol(protocolDocument, generateProtocolAmendmentNumber(protocolDocument), true);
        } finally {
            GlobalVariables.getUserSession().removeObject(AMEND_RENEW_ALLOW_NEW_PROTOCOL_DOCUMENT);
        }
        amendProtocolDocument.getProtocol().setInitialSubmissionDate(protocolDocument.getProtocol().getInitialSubmissionDate());
        amendProtocolDocument.getProtocol().setApprovalDate(protocolDocument.getProtocol().getApprovalDate());
        amendProtocolDocument.getProtocol().setExpirationDate(protocolDocument.getProtocol().getExpirationDate());
        amendProtocolDocument.getProtocol().setLastApprovalDate(protocolDocument.getProtocol().getLastApprovalDate());
        amendProtocolDocument.getProtocol().setProtocolStatusCode(getAmendmentInProgressStatusHook());
        amendProtocolDocument.getProtocol().refreshReferenceObject(PROTOCOL_STATUS);

        markProtocolAttachmentsAsFinalized(amendProtocolDocument.getProtocol().getAttachmentProtocols());

        ProtocolActionBase protocolAction = createCreateAmendmentProtocolAction(protocolDocument.getProtocol(),
                amendProtocolDocument.getProtocol().getProtocolNumber(), amendmentBean);

        protocolDocument.getProtocol().getProtocolActions().add(protocolAction);

        return createAmendment(protocolDocument, amendProtocolDocument, amendmentBean);
    }

    /**
     * This method marks all protocol attachment as finalized.
     *
     * @param attachmentProtocols
     */
    protected void markProtocolAttachmentsAsFinalized(List<ProtocolAttachmentProtocolBase> attachmentProtocols) {
        for (ProtocolAttachmentProtocolBase protocolAttachment : attachmentProtocols) {
            if ("1".equals(protocolAttachment.getDocumentStatusCode())) {
                protocolAttachment.setDocumentStatusCode("2");
            }
        }

    }

    /**
     * @throws java.lang.Exception
     * @see
     * org.kuali.kra.protocol.actions.amendrenew.ProtocolAmendRenewService#createRenewal(org.kuali.kra.protocol.ProtocolDocumentBase,
     * java.lang.String)
     */
    @Override
    public String createRenewal(ProtocolDocumentBase protocolDocument, String summary) throws Exception {
        ProtocolDocumentBase renewProtocolDocument = null;
        try {
            //since the user probably doesn't have permission to create the document, we are going to add session variable so the document
            //authorizer knows to approve the user for initiating the document
            GlobalVariables.getUserSession().addObject(AMEND_RENEW_ALLOW_NEW_PROTOCOL_DOCUMENT, Boolean.TRUE);
            renewProtocolDocument = protocolCopyService.copyProtocol(protocolDocument, generateProtocolRenewalNumber(protocolDocument), true);
        } finally {
            GlobalVariables.getUserSession().removeObject(AMEND_RENEW_ALLOW_NEW_PROTOCOL_DOCUMENT);
        }
        renewProtocolDocument.getProtocol().setInitialSubmissionDate(protocolDocument.getProtocol().getInitialSubmissionDate());
        renewProtocolDocument.getProtocol().setApprovalDate(protocolDocument.getProtocol().getApprovalDate());
        renewProtocolDocument.getProtocol().setExpirationDate(protocolDocument.getProtocol().getExpirationDate());
        renewProtocolDocument.getProtocol().setLastApprovalDate(protocolDocument.getProtocol().getLastApprovalDate());
        renewProtocolDocument.getProtocol().setProtocolStatusCode(getRenewalInProgressStatusHook());
        renewProtocolDocument.getProtocol().refreshReferenceObject(PROTOCOL_STATUS);

        markProtocolAttachmentsAsFinalized(renewProtocolDocument.getProtocol().getAttachmentProtocols());

        ProtocolActionBase protocolAction = createCreateRenewalProtocolAction(protocolDocument.getProtocol(),
                renewProtocolDocument.getProtocol().getProtocolNumber());
        protocolDocument.getProtocol().getProtocolActions().add(protocolAction);

        ProtocolAmendRenewalBase protocolAmendRenewal = createAmendmentRenewal(protocolDocument, renewProtocolDocument, summary, null);
        renewProtocolDocument.getProtocol().setProtocolAmendRenewal(protocolAmendRenewal);
        documentService.saveDocument(protocolDocument);
        documentService.saveDocument(renewProtocolDocument);

        return renewProtocolDocument.getDocumentNumber();
    }

    /**
     * @see
     * org.kuali.kra.irb.actions.amendrenew.ProtocolAmendRenewService#createRenewalWithAmendment(org.kuali.kra.irb.ProtocolDocumentBase,
     * org.kuali.kra.irb.actions.amendrenew.ProtocolAmendmentBean)
     */
    @Override
    public String createRenewalWithAmendment(ProtocolDocumentBase protocolDocument, ProtocolAmendmentBean amendmentBean) throws Exception {
        ProtocolDocumentBase renewProtocolDocument = null;
        try {
            //since the user probably doesn't have permission to create the document, we are going to add session variable so the document
            //authorizer knows to approve the user for initiating the document
            GlobalVariables.getUserSession().addObject(AMEND_RENEW_ALLOW_NEW_PROTOCOL_DOCUMENT, Boolean.TRUE);
            renewProtocolDocument = protocolCopyService.copyProtocol(protocolDocument, generateProtocolRenewalNumber(protocolDocument), true);
        } finally {
            GlobalVariables.getUserSession().removeObject(AMEND_RENEW_ALLOW_NEW_PROTOCOL_DOCUMENT);
        }
        renewProtocolDocument.getProtocol().setInitialSubmissionDate(protocolDocument.getProtocol().getInitialSubmissionDate());
        renewProtocolDocument.getProtocol().setApprovalDate(protocolDocument.getProtocol().getApprovalDate());
        renewProtocolDocument.getProtocol().setExpirationDate(protocolDocument.getProtocol().getExpirationDate());
        renewProtocolDocument.getProtocol().setLastApprovalDate(protocolDocument.getProtocol().getLastApprovalDate());
        renewProtocolDocument.getProtocol().setProtocolStatusCode(getRenewalInProgressStatusHook());
        renewProtocolDocument.getProtocol().refreshReferenceObject(PROTOCOL_STATUS);

        markProtocolAttachmentsAsFinalized(renewProtocolDocument.getProtocol().getAttachmentProtocols());

        ProtocolActionBase protocolAction = createCreateRenewalWithAmendmentProtocolAction(protocolDocument.getProtocol(),
                renewProtocolDocument.getProtocol().getProtocolNumber(), amendmentBean);
        protocolDocument.getProtocol().getProtocolActions().add(protocolAction);

        return createAmendment(protocolDocument, renewProtocolDocument, amendmentBean);
    }

    /**
     * @throws WorkflowException
     * @see
     * org.kuali.kra.irb.actions.amendrenew.ProtocolAmendRenewService#updateAmendmentRenewal(org.kuali.kra.irb.ProtocolDocumentBase,
     * org.kuali.kra.irb.actions.amendrenew.ProtocolAmendmentBean)
     */
    @Override
    public void updateAmendmentRenewal(ProtocolDocumentBase protocolDocument, ProtocolAmendmentBean amendmentBean) throws WorkflowException {
        protocolDocument.getProtocol().getProtocolAmendRenewal().setSummary(amendmentBean.getSummary());
        protocolDocument.getProtocol().getProtocolAmendRenewal().setModules(new ArrayList<ProtocolAmendRenewModuleBase>());
        addModules(protocolDocument.getProtocol(), amendmentBean);
    }

    /**
     * Create an Amendment. Adds an amendment entry into the database as well as
     * the modules that can be modified with this amendment.
     *
     * @param protocolDocument the original protocol document to be amended
     * @param amendProtocolDocument the amended protocol document
     * @param amendmentBean the amendment bean info
     * @return
     * @throws WorkflowException
     */
    protected String createAmendment(ProtocolDocumentBase protocolDocument, ProtocolDocumentBase amendProtocolDocument,
            ProtocolAmendmentBean amendmentBean) throws WorkflowException {

        ProtocolAmendRenewalBase protocolAmendRenewal = createAmendmentRenewal(protocolDocument, amendProtocolDocument, null, amendmentBean);
        amendProtocolDocument.getProtocol().setProtocolAmendRenewal(protocolAmendRenewal);
        addModules(amendProtocolDocument.getProtocol(), amendmentBean);
        documentService.saveDocument(protocolDocument);
        documentService.saveDocument(amendProtocolDocument);

        return amendProtocolDocument.getDocumentNumber();
    }

    /**
     * Generate the protocol number for an amendment. The protocol number for an
     * amendment is the original protocol's number appended with "Axxx" where
     * "xxx" is the next sequence number. A protocol can have more than one
     * amendment.
     *
     * @param protocolDocument
     * @return
     */
    protected String generateProtocolAmendmentNumber(ProtocolDocumentBase protocolDocument) {
        return generateProtocolNumber(protocolDocument, AMEND_ID, AMEND_NEXT_VALUE);
    }

    /**
     * Generate the protocol number for an renewal. The protocol number for an
     * renewal is the original protocol's number appended with "Rxxx" where
     * "xxx" is the next sequence number.
     *
     * @param protocolDocument
     * @return
     */
    protected String generateProtocolRenewalNumber(ProtocolDocumentBase protocolDocument) {
        return generateProtocolNumber(protocolDocument, RENEW_ID, RENEW_NEXT_VALUE);
    }

    /**
     * Generate the protocol number for an amendment or renewal.
     *
     * @param protocolDocument
     * @param letter
     * @param nextValueKey
     * @return
     */
    protected String generateProtocolNumber(ProtocolDocumentBase protocolDocument, String letter, String nextValueKey) {
        String protocolNumber = protocolDocument.getProtocol().getProtocolNumber();
        Integer nextValue = protocolDocument.getDocumentNextValue(nextValueKey);
        String s = nextValue.toString();
        int length = s.length();
        for (int i = 0; i < DIGIT_COUNT - length; i++) {
            s = "0" + s;
        }
        return protocolNumber + letter + s;
    }

    /**
     * Create an Amendment Entry.
     *
     * @param protocolDocument the original protocol document
     * @param amendProtocolDocument the amended protocol document
     * @param renewalSummary
     * @param amendmentBean
     * @return
     */
    protected ProtocolAmendRenewalBase createAmendmentRenewal(ProtocolDocumentBase protocolDocument, ProtocolDocumentBase amendProtocolDocument,
            String renewalSummary, ProtocolAmendmentBean amendmentBean) {

        ProtocolAmendRenewalBase protocolAmendRenewal = getNewProtocolAmendRenewalInstanceHook();
        protocolAmendRenewal.setProtoAmendRenNumber(amendProtocolDocument.getProtocol().getProtocolNumber());
        protocolAmendRenewal.setDateCreated(new Date(System.currentTimeMillis()));
        protocolAmendRenewal.setSummary(renewalSummary != null ? renewalSummary : amendmentBean.getSummary());
        protocolAmendRenewal.setProtocolNumber(protocolDocument.getProtocol().getProtocolNumber());
        protocolAmendRenewal.setProtocolId(amendProtocolDocument.getProtocol().getProtocolId());
        protocolAmendRenewal.setProtocol(amendProtocolDocument.getProtocol());
        protocolAmendRenewal.setSequenceNumber(0);
        return protocolAmendRenewal;
    }

    protected void removeEditedQuestionaire(ProtocolBase protocol) {
        ModuleQuestionnaireBean moduleQuestionnaireBean = getNewProtocolModuleQuestionnaireBeanInstanceHook(protocol);
        moduleQuestionnaireBean.setModuleSubItemCode("0");
        List<AnswerHeader> answerHeaders = questionnaireAnswerService.getQuestionnaireAnswer(moduleQuestionnaireBean);
        if (!answerHeaders.isEmpty() && answerHeaders.get(0).getAnswerHeaderId() != null) {
            businessObjectService.delete(answerHeaders);
        }
    }

    /**
     * Create a module entry.
     *
     * @param amendmentEntry
     * @param moduleTypeCode
     * @return
     */
    protected ProtocolAmendRenewModuleBase createModule(ProtocolAmendRenewalBase amendmentEntry, String moduleTypeCode) {
        ProtocolAmendRenewModuleBase module = getNewProtocolAmendRenewModuleInstanceHook();
        module.setProtocolAmendRenewalNumber(amendmentEntry.getProtoAmendRenNumber());
        module.setProtocolAmendRenewal(amendmentEntry);
        module.setProtocolAmendRenewalId(amendmentEntry.getId());
        module.setProtocolNumber(amendmentEntry.getProtocolNumber());
        module.setProtocolModuleTypeCode(moduleTypeCode);
        return module;
    }

    /**
     * Create a ProtocolBase Action indicating that an amendment has been
     * created.
     *
     * @param protocol
     * @param protocolNumber protocol number of the amendment
     * @param amendmentBean
     * @return a protocol action
     */
    protected ProtocolActionBase createCreateAmendmentProtocolAction(ProtocolBase protocol,
            String protocolNumber, ProtocolAmendmentBean amendmentBean) {

        ProtocolActionBase protocolAction = getNewAmendmentProtocolActionInstanceHook(protocol);
        String commentsBuffer = AMENDMENT + "-" + protocolNumber.substring(11) + " " + CREATED + "<br/>"
                + SUMMARY + ": " + amendmentBean.getSummary() + amendmentBean.getActiveModuleString();

        protocolAction.setComments(commentsBuffer);
        return protocolAction;
    }

     /**
     * Create a ProtocolBase Action indicating that a renewal with amendment has been created
     * @param protocol
     * @param protocolNumber protocol number of the renewal
     * @param amendmentBean
     * @return a protocol action
     */    
     protected ProtocolActionBase createCreateRenewalWithAmendmentProtocolAction(ProtocolBase protocol, String protocolNumber, ProtocolAmendmentBean amendmentBean) {
         ProtocolActionBase protocolAction = getNewRenewalProtocolActionInstanceHook(protocol);
         String commentsBuffer = RENEWAL + "-" + protocolNumber.substring(11) + " " + CREATED + "<br/>" + SUMMARY + ": " + amendmentBean.getSummary() + 
                                 amendmentBean.getActiveModuleString();
         protocolAction.setComments(commentsBuffer);
         return protocolAction;
     }    
    
    /**
     * Create a ProtocolBase Action indicating that a renewal has been created.
     *
     * @param protocol
     * @param protocolNumber protocol number of the renewal
     * @return a protocol action
     */
    protected ProtocolActionBase createCreateRenewalProtocolAction(ProtocolBase protocol, String protocolNumber) {
        ProtocolActionBase protocolAction = getNewRenewalProtocolActionInstanceHook(protocol);
        protocolAction.setComments(RENEWAL + "-" + protocolNumber.substring(11) + ": " + CREATED);
        return protocolAction;
    }

    /**
     * Create a ProtocolBase Action indicating that a renewal with amendment has
     * been created.
     *
     * @param protocol
     * @param protocolNumber protocol number of the renewal
     * @return a protocol action
     */
    protected ProtocolActionBase createCreateRenewalWithAmendmentProtocolAction(ProtocolBase protocol, String protocolNumber) {
        ProtocolActionBase protocolAction = getNewRenewalWithAmendmentProtocolActionInstanceHook(protocol);
        protocolAction.setComments(RENEWAL + "-" + protocolNumber.substring(11) + ": " + CREATED);
        return protocolAction;
    }

    /**
     * @see
     * org.kuali.kra.protocol.actions.amendrenew.ProtocolAmendRenewService#getAmendmentAndRenewals(java.lang.String)
     */
    @Override
    public List<ProtocolBase> getAmendmentAndRenewals(String protocolNumber) throws Exception {
        List<ProtocolBase> protocols = new ArrayList<ProtocolBase>();
        protocols.addAll(getAmendments(protocolNumber));
        protocols.addAll(getRenewals(protocolNumber));
        return protocols;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<ProtocolBase> getAmendments(String protocolNumber) throws Exception {
        List<ProtocolBase> amendments = new ArrayList<ProtocolBase>();
        Collection<ProtocolBase> protocols = (Collection<ProtocolBase>) kraLookupDao.findCollectionUsingWildCard(getProtocolBOClassHook(), PROTOCOL_NUMBER, protocolNumber + AMEND_ID + "%", true);
        for (ProtocolBase protocol : protocols) {
            ProtocolDocumentBase protocolDocument = (ProtocolDocumentBase) documentService.getByDocumentHeaderId(protocol.getProtocolDocument().getDocumentNumber());
            amendments.add(protocolDocument.getProtocol());
        }
        return amendments;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<ProtocolBase> getRenewals(String protocolNumber) throws Exception {
        List<ProtocolBase> renewals = new ArrayList<ProtocolBase>();
        Collection<ProtocolBase> protocols = (Collection<ProtocolBase>) kraLookupDao.findCollectionUsingWildCard(getProtocolBOClassHook(), PROTOCOL_NUMBER, protocolNumber + RENEW_ID + "%", true);
        for (ProtocolBase protocol : protocols) {
            ProtocolDocumentBase protocolDocument = (ProtocolDocumentBase) documentService.getByDocumentHeaderId(protocol.getProtocolDocument().getDocumentNumber());
            renewals.add(protocolDocument.getProtocol());
        }
        return renewals;
    }

    /**
     * @throws Exception
     * @see
     * org.kuali.kra.irb.actions.amendrenew.ProtocolAmendRenewService#getAvailableModules(java.lang.String)
     */
    @Override
    public List<String> getAvailableModules(String protocolNumber) throws Exception {
        List<String> moduleTypeCodes = getAllModuleTypeCodes();

        /*
         * Filter out the modules that are currently being modified by
         * outstanding amendments.
         */
        List<ProtocolBase> protocols = getAmendmentAndRenewals(protocolNumber);
        for (ProtocolBase protocol : protocols) {
            if (!isAmendmentCompleted(protocol)) {
                ProtocolAmendRenewalBase amendRenewal = protocol.getProtocolAmendRenewal();
                if (amendRenewal != null) {
                    List<ProtocolAmendRenewModuleBase> modules = amendRenewal.getModules();
                    for (ProtocolAmendRenewModuleBase module : modules) {
                        moduleTypeCodes.remove(module.getProtocolModuleTypeCode());
                    }
                }
            }
        }

        return moduleTypeCodes;
    }

    /**
     * Has the amendment completed, e.g. been approved, disapproved, etc?
     *
     * @param protocol
     * @return
     */
    protected boolean isAmendmentCompleted(ProtocolBase protocol) {
        WorkflowDocument workflowDocument = getWorkflowDocument(protocol.getProtocolDocument());
        if (workflowDocument != null) {
            return workflowDocument.isApproved()
                    || workflowDocument.isFinal()
                    || workflowDocument.isDisapproved()
                    || workflowDocument.isCanceled()
                    || workflowDocument.isException();
        }
        return false;
    }

    /**
     * Get the corresponding workflow document.
     *
     * @param doc the document
     * @return the workflow document or null if there is none
     */
    protected WorkflowDocument getWorkflowDocument(Document doc) {
        WorkflowDocument workflowDocument = null;
        if (doc != null) {
            DocumentHeader header = doc.getDocumentHeader();
            if (header != null) {
                try {
                    workflowDocument = header.getWorkflowDocument();
                } catch (RuntimeException ex) {
                    // do nothing; there is no workflow document
                }
            }
        }
        return workflowDocument;
    }

    public void setQuestionnaireAnswerService(QuestionnaireAnswerService questionnaireAnswerService) {
        this.questionnaireAnswerService = questionnaireAnswerService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    protected abstract ProtocolActionBase getNewAmendmentProtocolActionInstanceHook(ProtocolBase protocol);

    protected abstract ProtocolActionBase getNewRenewalProtocolActionInstanceHook(ProtocolBase protocol);

    protected abstract ProtocolActionBase getNewRenewalWithAmendmentProtocolActionInstanceHook(ProtocolBase protocol);

    protected abstract ModuleQuestionnaireBean getNewProtocolModuleQuestionnaireBeanInstanceHook(ProtocolBase protocol);

    protected abstract String getAmendmentInProgressStatusHook();

    protected abstract String getRenewalInProgressStatusHook();

    protected abstract List<String> getAllModuleTypeCodes();

    protected abstract void addModules(ProtocolBase protocol, ProtocolAmendmentBean amendmentBean);

    protected abstract Class<? extends ProtocolBase> getProtocolBOClassHook();

    protected abstract ProtocolAmendRenewalBase getNewProtocolAmendRenewalInstanceHook();

    protected abstract ProtocolAmendRenewModuleBase getNewProtocolAmendRenewModuleInstanceHook();

    public ProtocolCopyService<ProtocolDocumentBase> getProtocolCopyService() {
        return protocolCopyService;
    }
}
