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
package org.kuali.kra.proposaldevelopment.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.budget.core.BudgetService;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.infrastructure.TimeFormatter;
import org.kuali.kra.proposaldevelopment.bo.*;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.proposaldevelopment.hierarchy.ProposalHierarchyException;
import org.kuali.kra.proposaldevelopment.rule.*;
import org.kuali.kra.proposaldevelopment.rule.event.*;
import org.kuali.kra.proposaldevelopment.service.ProposalDevelopmentService;
import org.kuali.kra.proposaldevelopment.web.bean.ProposalUserRoles;
import org.kuali.kra.rule.BusinessRuleInterface;
import org.kuali.kra.rule.event.KraDocumentEventBaseExtension;
import org.kuali.kra.rules.ResearchDocumentRuleBase;
import org.kuali.kra.service.SponsorService;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.event.ApproveDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

import java.util.HashMap;
import java.util.List;
import org.ariahgroup.research.datadictionary.AttributeDefinition;
import org.ariahgroup.research.datadictionary.validation.processor.WordCountConstraintProcessor;

import static org.kuali.kra.logging.BufferedLogger.info;
import org.kuali.kra.proposaldevelopment.web.struts.form.ProposalDevelopmentForm;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntry;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.kuali.rice.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.rice.krad.datadictionary.validation.DictionaryObjectAttributeValueReader;
import org.kuali.rice.krad.datadictionary.validation.ErrorLevel;
import org.kuali.rice.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.rice.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.rice.krad.datadictionary.validation.result.ProcessorResult;

/**
 * Main Business Rule class for
 * <code>{@link ProposalDevelopmentDocument}</code>. Responsible for delegating
 * rules to independent rule classes.
 *
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ProposalDevelopmentDocumentRule extends ResearchDocumentRuleBase implements AddCongressionalDistrictRule, AddKeyPersonRule, AddNarrativeRule, ReplaceNarrativeRule, SaveNarrativesRule, AddInstituteAttachmentRule, ReplaceInstituteAttachmentRule, AddPersonnelAttachmentRule, ReplacePersonnelAttachmentRule, AddProposalSiteRule, BusinessRuleInterface, SaveProposalSitesRule, AbstractsRule, CopyProposalRule, ChangeKeyPersonRule, DeleteCongressionalDistrictRule, PermissionsRule, NewNarrativeUserRightsRule, SaveKeyPersonRule, CalculateCreditSplitRule, ProposalDataOverrideRule, ResubmissionPromptRule, BudgetDataOverrideRule {

    @SuppressWarnings("unused")
    //private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProposalDevelopmentDocumentRule.class);
    private static final String PROPOSAL_QUESTIONS_KEY = "proposalYnq[%d].%s";
    private static final String PROPOSAL_QUESTIONS_KEY_PROPERTY_ANSWER = "answer";
    private static final String PROPOSAL_QUESTIONS_KEY_PROPERTY_REVIEW_DATE = "reviewDate";
    private static final String PROPOSAL_QUESTIONS_KEY_PROPERTY_EXPLANATION = "explanation";
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProposalDevelopmentDocumentRule.class);

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean retval = true;
        //ProposalDevelopmentDocument proposalDevelopmentDocument = (ProposalDevelopmentDocument) document;

        retval &= super.processCustomRouteDocumentBusinessRules(document);
        retval &= new KeyPersonnelAuditRule().processRunAuditBusinessRules(document);
        retval &= new KeyPersonnelCertificationRule().processRouteDocument(document);
        return retval;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        if (!(document instanceof ProposalDevelopmentDocument)) {
            return false;
        }

        boolean valid = true;

        ProposalDevelopmentDocument proposalDevelopmentDocument = (ProposalDevelopmentDocument) document;

        GlobalVariables.getMessageMap().addToErrorPath("document.developmentProposalList[0]");
        valid &= processProposalRequiredFieldsBusinessRule(proposalDevelopmentDocument);

        valid &= processProposalYNQBusinessRule(proposalDevelopmentDocument, false);
        valid &= processBudgetVersionsBusinessRule(proposalDevelopmentDocument, false);
        valid &= processProposalGrantsGovBusinessRule(proposalDevelopmentDocument);
        valid &= processSponsorProgramBusinessRule(proposalDevelopmentDocument);
        valid &= processKeywordBusinessRule(proposalDevelopmentDocument);
        valid &= proccessValidateSponsor(proposalDevelopmentDocument);
        GlobalVariables.getMessageMap().removeFromErrorPath("document.developmentProposalList[0]");

        return valid;
    }

    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean retval = super.processCustomApproveDocumentBusinessRules(approveEvent);

        retval &= new KeyPersonnelCertificationRule().processApproveDocument(approveEvent);

        return retval;
    }

    private boolean proccessValidateSponsor(ProposalDevelopmentDocument proposalDevelopmentDocument) {
        System.err.println("proccessValidateSponsor");
        boolean valid = true;
        DataDictionaryService dataDictionaryService = KraServiceLocator.getService(DataDictionaryService.class);
        if (!this.getSponsorService().validateSponsor(proposalDevelopmentDocument.getDevelopmentProposal().getSponsor())) {
            System.err.println("  Invalid sponsor");
            valid = false;
            //this.reportError("document.developmentProposalList[0].sponsorCode", KeyConstants.ERROR_INVALID_SPONSOR_CODE, "");
            //GlobalVariables.getMessageMap().putError("document.developmentProposalList[0].sponsorCode", KeyConstants.ERROR_INVALID_SPONSOR_CODE, "");
            GlobalVariables.getMessageMap().putError("sponsorCode", KeyConstants.ERROR_MISSING, dataDictionaryService.getAttributeErrorLabel(DevelopmentProposal.class, "sponsorCode"));
        }
        if (!StringUtils.isEmpty(proposalDevelopmentDocument.getDevelopmentProposal().getPrimeSponsorCode())
                && !this.getSponsorService().validateSponsor(proposalDevelopmentDocument.getDevelopmentProposal().getPrimeSponsor())) {
            System.err.println("  Invalid  prime sponsor");
            valid = false;
            //this.reportError("document.developmentProposalList[0].primeSponsorCode", KeyConstants.ERROR_INVALID_SPONSOR_CODE, "");
            GlobalVariables.getMessageMap().putError("primeSponsorCode", KeyConstants.ERROR_MISSING, dataDictionaryService.getAttributeErrorLabel(DevelopmentProposal.class, "primeSponsorCode"));

        }
        System.err.println("  returning: " + (valid));
        return valid;
    }

    public boolean processAddCongressionalDistrictRules(AddProposalCongressionalDistrictEvent addCongressionalDistrictEvent) {
        return new ProposalDevelopmentCongressionalDistrictRule().processAddCongressionalDistrictRules(addCongressionalDistrictEvent);
    }

    public boolean processDeleteCongressionalDistrictRules(DeleteProposalCongressionalDistrictEvent deleteCongressionalDistrictEvent) {
        return new ProposalDevelopmentCongressionalDistrictRule().processDeleteCongressionalDistrictRules(deleteCongressionalDistrictEvent);
    }

    /**
     *
     * Validate proposal questions rule. validate explanation required and date
     * required fields based on question configuration. Answers are mandatory
     * for routing
     *
     * @param proposalDevelopmentDocument
     * @return
     */
    public boolean processProposalYNQBusinessRule(ProposalDevelopmentDocument proposalDevelopmentDocument, boolean docRouting) {
        boolean valid = true;

        for (int j = 0; j < proposalDevelopmentDocument.getDevelopmentProposal().getProposalYnqs().size(); j++) {
            ProposalYnq proposalYnq = proposalDevelopmentDocument.getDevelopmentProposal().getProposalYnqs().get(j);

            String groupName = proposalYnq.getYnq().getGroupName();
            HashMap<String, Integer> questionSerial = getQuestionSerialNumberBasedOnGroup(proposalDevelopmentDocument);
            String[] errorParameter = {groupName};
            String ynqAnswer = proposalYnq.getAnswer();
            /* look for answers - required for routing */
            if (docRouting && StringUtils.isBlank(proposalYnq.getAnswer())) {
                info("no answer");
                valid = false;

                reportError(String.format(PROPOSAL_QUESTIONS_KEY, j, PROPOSAL_QUESTIONS_KEY_PROPERTY_ANSWER), KeyConstants.ERROR_REQUIRED_ANSWER, questionSerial.get(proposalYnq.getQuestionId()).toString(), groupName);
            }
            /* look for date required */
            String dateRequiredFor = proposalYnq.getYnq().getDateRequiredFor();
            if (dateRequiredFor != null) {
                if (StringUtils.isNotBlank(ynqAnswer)
                        && dateRequiredFor.contains(ynqAnswer)
                        && proposalYnq.getReviewDate() == null) {
                    info("No review date");
                    valid = false;
                    reportError(String.format(PROPOSAL_QUESTIONS_KEY, j, PROPOSAL_QUESTIONS_KEY_PROPERTY_REVIEW_DATE), KeyConstants.ERROR_REQUIRED_FOR_REVIEW_DATE, questionSerial.get(proposalYnq.getQuestionId()).toString(), groupName);

                }
            }

            /* look for explanation required */
            String explanationRequiredFor = proposalYnq.getYnq().getExplanationRequiredFor();
            if (explanationRequiredFor != null) {
                if (StringUtils.isNotBlank(ynqAnswer)
                        && explanationRequiredFor.contains(ynqAnswer)
                        && StringUtils.isBlank(proposalYnq.getExplanation())) {
                    info("No explanation date");
                    valid = false;
                    reportError(String.format(PROPOSAL_QUESTIONS_KEY, j, PROPOSAL_QUESTIONS_KEY_PROPERTY_EXPLANATION), KeyConstants.ERROR_REQUIRED_FOR_EXPLANATION, questionSerial.get(proposalYnq.getQuestionId()).toString(), groupName);
                }
            }
        }

        return valid;
    }

    public static HashMap<String, Integer> getQuestionSerialNumberBasedOnGroup(ProposalDevelopmentDocument proposalDevelopmentDocument) {
        HashMap<String, Integer> ynqGroupSerial = new HashMap<String, Integer>();
        for (YnqGroupName ynqGroupName : proposalDevelopmentDocument.getDevelopmentProposal().getYnqGroupNames()) {
            Integer serialNumber = Integer.valueOf(1);
            for (ProposalYnq proposalYnq : proposalDevelopmentDocument.getDevelopmentProposal().getProposalYnqs()) {
                if (ynqGroupName.getGroupName().equalsIgnoreCase(proposalYnq.getYnq().getGroupName())) {
                    ynqGroupSerial.put(proposalYnq.getQuestionId(), serialNumber);
                    serialNumber++;
                }
            }
        }
        return ynqGroupSerial;
    }

    /**
     * This method validates Required Fields related fields on the Proposal
     * Development Document.
     *
     * @param proposalDevelopmentDocument document to validate
     * @return boolean whether the validation passed or not
     */
    private boolean processProposalRequiredFieldsBusinessRule(ProposalDevelopmentDocument proposalDevelopmentDocument) {
        boolean valid = true;

        MessageMap errorMap = GlobalVariables.getMessageMap();
        DataDictionaryService dataDictionaryService = KraServiceLocator.getService(DataDictionaryService.class);

        /*
         proposalDevelopmentDocument.getDevelopmentProposal().refreshReferenceObject("sponsor");
         if (proposalDevelopmentDocument.getDevelopmentProposal().getSponsorCode() != null
         && proposalDevelopmentDocument.getDevelopmentProposal().getSponsor() == null) {
         valid = false;
         errorMap.putError("sponsorCode", KeyConstants.ERROR_MISSING, dataDictionaryService.getAttributeErrorLabel(
         DevelopmentProposal.class, "sponsorCode"));
         }
         */
        //
        // we MUST check the TYPE of KualiForm since it is acually possible for the form object to be of type ProtocolForm (IRB) and IacucProtocolForm 
        // since when a DevProposal is created as a Funding Source of a Protocol it creates a DevProp and saves it, which triggers
        // this rules class
        KualiForm form = KNSGlobalVariables.getKualiForm();

        if (form instanceof ProposalDevelopmentForm) {
            ProposalDevelopmentForm proposalForm = (ProposalDevelopmentForm) form;

            //if either is missing, it should be caught on the DD validation.
            if (proposalForm.isProjectDatesRequired() && proposalDevelopmentDocument.getDevelopmentProposal().getRequestedStartDateInitial() != null
                    && proposalDevelopmentDocument.getDevelopmentProposal().getRequestedEndDateInitial() != null) {

                if (proposalDevelopmentDocument.getDevelopmentProposal().getRequestedStartDateInitial().after(
                        proposalDevelopmentDocument.getDevelopmentProposal().getRequestedEndDateInitial())) {

                    valid = false;
                    errorMap.putError("requestedStartDateInitial", KeyConstants.ERROR_START_DATE_AFTER_END_DATE,
                            new String[]{dataDictionaryService.getAttributeErrorLabel(DevelopmentProposal.class, "requestedStartDateInitial"),
                                dataDictionaryService.getAttributeErrorLabel(DevelopmentProposal.class, "requestedEndDateInitial")});
                }
            }

            ProposalDevelopmentService proposalDevelopmentService = KraServiceLocator.getService(ProposalDevelopmentService.class);

            if (StringUtils.isNotBlank(proposalDevelopmentDocument.getDevelopmentProposal().getCurrentAwardNumber())) {
                if (proposalDevelopmentService.getProposalCurrentAwardVersion(proposalDevelopmentDocument) == null) {
                    valid = false;
                    errorMap.putError("currentAwardNumber", KeyConstants.ERROR_MISSING,
                            dataDictionaryService.getAttributeErrorLabel(DevelopmentProposal.class, "currentAwardNumber"));
                }
            }

            // check first to ensure ProposalNumber is NOT null, as it can be during a Proposal Copy Operation for some odd reason due to DeepCopy issue
            if (proposalDevelopmentDocument.getDevelopmentProposal().getProposalNumber()!=null &&
                    proposalForm.isDisplayProposalCoordinator() && proposalForm.isProposalCoordinatorRequired()) {
                if (proposalDevelopmentDocument.getDevelopmentProposal().getProposalCoordinatorPrincipalName() == null) {
                    valid = false;
                    errorMap.putError("proposalCoordinatorPrincipalName", KeyConstants.ERROR_MISSING,
                            dataDictionaryService.getAttributeErrorLabel(DevelopmentProposal.class, "proposalCoordinatorPrincipalName"));
                }
            }

            if (StringUtils.isNotBlank(proposalDevelopmentDocument.getDevelopmentProposal().getContinuedFrom())) {
                if (proposalDevelopmentService.getProposalContinuedFromVersion(proposalDevelopmentDocument) == null) {
                    valid = false;
                    errorMap.putError("continuedFrom", KeyConstants.ERROR_MISSING,
                            dataDictionaryService.getAttributeErrorLabel(DevelopmentProposal.class, "continuedFrom"));
                }
            }

            if (proposalForm.isDisplayExecutiveSummary()) {
                final String entryName = DevelopmentProposal.class.getCanonicalName();
                DataObjectEntry entry = dataDictionaryService.getDataDictionary().getDataObjectEntry(entryName);
                AttributeDefinition defn = (AttributeDefinition) entry.getAttributeDefinition("executiveSummary");

                if (defn.getWordCountConstraint() != null) {

                    try {
                        String execSum = proposalDevelopmentDocument.getDevelopmentProposal().getExecutiveSummary();
                        
                        // null-check here is very important. If the first arg to getDictionaryValidationService().validate
                        // is NULL, it causes NullPointer to be thrown several levels down the stack.
                        if(execSum == null) {
                            execSum = "";
                        }

                        DictionaryValidationResult result = getDictionaryValidationService().validate(execSum, entry.getName(), entry, false);
                        DataDictionaryEntry dictEntry = getDataDictionaryService().getDataDictionary().getDictionaryObjectEntry(entryName);
                        AttributeValueReader attrReaderExisting = new DictionaryObjectAttributeValueReader(proposalDevelopmentDocument.getDevelopmentProposal(),
                                entryName, dictEntry);
                        attrReaderExisting.setAttributeName(defn.getName());

                        WordCountConstraintProcessor wcp = new WordCountConstraintProcessor();
                        ProcessorResult procRes = wcp.process(result, execSum, defn.getWordCountConstraint(), attrReaderExisting);

                        if (procRes != null && procRes.getConstraintValidationResults() != null) {

                            ConstraintValidationResult cvres = procRes.getConstraintValidationResults().get(0);

                            if (cvres != null && cvres.getStatus() == ErrorLevel.ERROR) {
                                valid = false;
                                errorMap.putError("executiveSummary", Constants.MESSAGE_WORD_COUNT_EXCEEDED,
                                        new String[]{dataDictionaryService.getAttributeErrorLabel(DevelopmentProposal.class, "executiveSummary"),
                                            String.valueOf(cvres.getErrorParameters()[1])});
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return valid;
    }

    /**
     *
     *
     * Validate Grants.gov business rules.
     *
     * @param proposalDevelopmentDocument
     * @return
     */
    private boolean processProposalGrantsGovBusinessRule(ProposalDevelopmentDocument proposalDevelopmentDocument) {
        boolean valid = true;

        if (proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity() != null
                && proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity().getOpportunityId() != null
                && StringUtils.equalsIgnoreCase(proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity().getRevisionCode(),
                        this.getS2sRevisionTypeOther())
                && (proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity().getRevisionOtherDescription() == null || StringUtils
                .equals(proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity().getRevisionOtherDescription().trim(),
                        ""))) {
            reportError("s2sOpportunity.revisionOtherDescription", KeyConstants.ERROR_IF_REVISIONTYPE_IS_OTHER);
            valid &= false;
        }

        if (proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity() != null
                && proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity().getOpportunityId() != null
                && !StringUtils.equalsIgnoreCase(proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity().getRevisionCode(),
                        this.getS2sRevisionTypeOther())
                && (proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity().getRevisionOtherDescription() != null && !StringUtils
                .equals(proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity().getRevisionOtherDescription().trim(),
                        ""))) {
            reportError("s2sOpportunity.revisionOtherDescription",
                    KeyConstants.ERROR_IF_REVISIONTYPE_IS_NOT_OTHER_SPECIFY_NOT_BLANK);
            valid &= false;
        }
        return valid;
    }

    /**
     * Gets the S2s Revision Type Other parameter
     *
     * @return the parameter
     */
    private String getS2sRevisionTypeOther() {
        return this.getParameterService().getParameterValueAsString(ProposalDevelopmentDocument.class, KeyConstants.S2S_REVISIONTYPE_OTHER);
    }

    public boolean processAddKeyPersonBusinessRules(ProposalDevelopmentDocument document, ProposalPerson person) {
        return new ProposalDevelopmentKeyPersonsRule().processAddKeyPersonBusinessRules(document, person);
    }

    /**
     * Validate Sponsor/program Information rule. Regex validation for CFDA
     * number(7 digits with a period in the 3rd character and an optional alpha
     * character in the 7th field).
     *
     * @param proposalDevelopmentDocument
     * @return
     */
    private boolean processSponsorProgramBusinessRule(ProposalDevelopmentDocument proposalDevelopmentDocument) {

        boolean valid = true;
        String regExpr = "(\\d{2})(\\.)(\\d{3})[a-zA-z]?";
        MessageMap errorMap = GlobalVariables.getMessageMap();
        DataDictionaryService dataDictionaryService = KraServiceLocator.getService(DataDictionaryService.class);
        if (StringUtils.isNotBlank(proposalDevelopmentDocument.getDevelopmentProposal().getCfdaNumber())
                && !(proposalDevelopmentDocument.getDevelopmentProposal().getCfdaNumber().matches(regExpr))
                && GlobalVariables.getMessageMap().getMessages("document.developmentProposalList[0].cfdaNumber") == null) {
            errorMap.putError("developmentProposalList[0].cfdaNumber", RiceKeyConstants.ERROR_INVALID_FORMAT, new String[]{
                dataDictionaryService.getAttributeErrorLabel(DevelopmentProposal.class, "cfdaNumber"),
                proposalDevelopmentDocument.getDevelopmentProposal().getCfdaNumber()});
            valid = false;
        }

        SponsorService sponsorService = KraServiceLocator.getService(SponsorService.class);
        String sponsorCode = proposalDevelopmentDocument.getDevelopmentProposal().getPrimeSponsorCode();

        if (sponsorCode != null) {
            String sponsorName = sponsorService.getSponsorName(sponsorCode);
            if (sponsorName == null) {
                errorMap.putError("developmentProposalList[0].primeSponsorCode", RiceKeyConstants.ERROR_EXISTENCE, new String[]{
                    dataDictionaryService.getAttributeLabel(DevelopmentProposal.class, "primeSponsorCode")});
                valid = false;
            }
        }
        if (proposalDevelopmentDocument.getDevelopmentProposal().getDeadlineTime() != null) {
            TimeFormatter formatter = new TimeFormatter();

            String deadLineTime = (String) formatter.convertToObject(proposalDevelopmentDocument.getDevelopmentProposal().getDeadlineTime());
            if (!deadLineTime.equalsIgnoreCase(Constants.INVALID_TIME)) {
                proposalDevelopmentDocument.getDevelopmentProposal().setDeadlineTime(deadLineTime);
            } else {
                errorMap.putError("deadlineTime", KeyConstants.INVALID_DEADLINE_TIME,
                        dataDictionaryService.getAttributeErrorLabel(DevelopmentProposal.class, "deadlineTime"));
                valid = false;
            }
        }

        // we MUST check the TYPE of KualiForm since it is acually possible for the form object to be of type ProtocolForm (IRB) and IacucProtocolForm 
        // since when a DevProposal is created as a Funding Source of a Protocol it creates a DevProp and saves it, which triggers
        // this rules class
        KualiForm form = KNSGlobalVariables.getKualiForm();

        if (form instanceof ProposalDevelopmentForm) {
            ProposalDevelopmentForm proposalForm = (ProposalDevelopmentForm) form;

            //if (proposalDevelopmentDocument.getDevelopmentProposal().getProposalNumber()!=null &&  proposalForm.isDeadlineDateRequired() && proposalDevelopmentDocument.getDevelopmentProposal().getDeadlineDate() == null) {
            if (proposalForm.isDeadlineDateRequired() && proposalDevelopmentDocument.getDevelopmentProposal().getDeadlineDate() == null) {
                errorMap.putError("deadlineDate", KeyConstants.WARNING_EMPTY_DEADLINE_DATE,
                        dataDictionaryService.getAttributeErrorLabel(DevelopmentProposal.class, "deadlineDate"));
                valid = false;
            }
        }

        return valid;
    }

    private boolean processKeywordBusinessRule(ProposalDevelopmentDocument document) {
        List<PropScienceKeyword> keywords = document.getDevelopmentProposal().getPropScienceKeywords();

        for (PropScienceKeyword keyword : keywords) {
            for (PropScienceKeyword keyword2 : keywords) {
                if (keyword == keyword2) {
                    continue;
                } else if (StringUtils.equalsIgnoreCase(keyword.getScienceKeywordCode(), keyword2.getScienceKeywordCode())) {
                    GlobalVariables.getMessageMap().putError("propScienceKeyword", "error.proposalKeywords.duplicate");

                    return false;
                }
            }
        }
        return true;
    }

    public boolean processAddNarrativeBusinessRules(AddNarrativeEvent addNarrativeEvent) {
        return new ProposalDevelopmentNarrativeRule().processAddNarrativeBusinessRules(addNarrativeEvent);
    }

    /**
     * @see
     * org.kuali.rice.krad.rules.rule.DocumentAuditRule#processRunAuditBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean processRunAuditBusinessRules(Document document) {
        if (((ProposalDevelopmentDocument) document).getDevelopmentProposal().isChild()) {
            throw new RuntimeException(new ProposalHierarchyException("Cannot run validation on a Proposal Hierarchy Child."));
        }
        boolean retval = true;

        retval &= super.processRunAuditBusinessRules(document);

        retval &= new ProposalDevelopmentProposalRequiredFieldsAuditRule().processRunAuditBusinessRules(document);

        retval &= new ProposalDevelopmentSponsorProgramInformationAuditRule().processRunAuditBusinessRules(document);

        retval &= new KeyPersonnelAuditRule().processRunAuditBusinessRules(document);

        retval &= new KeyPersonnelCertificationRule().processRunAuditBusinessRules(document);
        //audit for Proposal Attachments to ensure status code is set to complete.
        retval &= new ProposalDevelopmentProposalAttachmentsAuditRule().processRunAuditBusinessRules(document);

        //Change for KRACOEUS-1403
        ProposalDevelopmentDocument proposalDevelopmentDocument = (ProposalDevelopmentDocument) document;
        proposalDevelopmentDocument.getDevelopmentProposal().getYnqService().populateProposalQuestions(
                proposalDevelopmentDocument.getDevelopmentProposal().getProposalYnqs(),
                proposalDevelopmentDocument.getDevelopmentProposal().getYnqGroupNames(), proposalDevelopmentDocument);

        retval &= new ProposalDevelopmentYnqAuditRule().processRunAuditBusinessRules(document);
        //Change for KRACOEUS-1403 ends here       
        retval &= new ProposalDevelopmentGrantsGovAuditRule().processRunAuditBusinessRules(document);

        retval &= new ProposalDevelopmentS2sQuestionnaireAuditRule().processRunAuditBusinessRules(proposalDevelopmentDocument);
        retval &= new ProposalDevelopmentQuestionnaireAuditRule().processRunAuditBusinessRules(proposalDevelopmentDocument);

        // audit check for budgetversion with final status
        try {
            retval &= KraServiceLocator.getService(BudgetService.class).validateBudgetAuditRule((ProposalDevelopmentDocument) document);
        } catch (Exception ex) {
            // TODO : should log it here
            throw new RuntimeException("Validate Budget Audit rules encountered exception", ex);
        }

        return retval;
    }

    /**
     * @see
     * org.kuali.kra.proposaldevelopment.rule.AbstractsRule#processAddAbstractBusinessRules(org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument,
     * org.kuali.kra.proposaldevelopment.bo.ProposalAbstract)
     */
    public boolean processAddAbstractBusinessRules(ProposalDevelopmentDocument document, ProposalAbstract proposalAbstract) {
        return new ProposalDevelopmentAbstractsRule().processAddAbstractBusinessRules(document, proposalAbstract);
    }

    public boolean processSaveNarrativesBusinessRules(SaveNarrativesEvent saveNarrativesEvent) {
        return new ProposalDevelopmentNarrativeRule().processSaveNarrativesBusinessRules(saveNarrativesEvent);
    }

    public boolean processReplaceNarrativeBusinessRules(ReplaceNarrativeEvent replaceNarrativeEvent) {
        return new ProposalDevelopmentNarrativeRule().processReplaceNarrativeBusinessRules(replaceNarrativeEvent);
    }

    /**
     * @see
     * org.kuali.kra.proposaldevelopment.rule.CopyProposalRule#processCopyProposalBusinessRules(org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument,
     * org.kuali.kra.proposaldevelopment.bo.ProposalCopyCriteria)
     */
    public boolean processCopyProposalBusinessRules(ProposalDevelopmentDocument document, ProposalCopyCriteria criteria) {
        return new ProposalDevelopmentCopyRule().processCopyProposalBusinessRules(document, criteria);
    }

    /**
     *
     * @see
     * org.kuali.kra.proposaldevelopment.rule.AddInstituteAttachmentRule#processAddInstituteAttachmentBusinessRules(org.kuali.kra.proposaldevelopment.rule.event.AddInstituteAttachmentEvent)
     */
    public boolean processAddInstituteAttachmentBusinessRules(AddInstituteAttachmentEvent addInstituteAttachmentEvent) {
        return new ProposalDevelopmentInstituteAttachmentRule().processAddInstituteAttachmentBusinessRules(addInstituteAttachmentEvent);
    }

    public boolean processReplaceInstituteAttachmentBusinessRules(ReplaceInstituteAttachmentEvent event) {
        return new ProposalDevelopmentInstituteAttachmentRule().processReplaceInstituteAttachmentBusinessRules(event);
    }

    public boolean processAddPersonnelAttachmentBusinessRules(AddPersonnelAttachmentEvent addPersonnelAttachmentEvent) {
        return new ProposalDevelopmentPersonnelAttachmentRule().processAddPersonnelAttachmentBusinessRules(addPersonnelAttachmentEvent);
    }

    public boolean processSavePersonnelAttachmentBusinessRules(SavePersonnelAttachmentEvent savePersonnelAttachmentEvent) {
        return new ProposalDevelopmentPersonnelAttachmentRule().processSavePersonnelAttachmentBusinessRules(savePersonnelAttachmentEvent);
    }

    public boolean processReplacePersonnelAttachmentBusinessRules(ReplacePersonnelAttachmentEvent event) {
        return new ProposalDevelopmentPersonnelAttachmentRule().processReplacePersonnelAttachmentBusinessRules(event);
    }

    /**
     * Delegating method for the <code>{@link ChangeKeyPersonRule}</code> which
     * is triggered by the <code>{@link ChangeKeyPersonEvent}</code>
     *
     */
    public boolean processChangeKeyPersonBusinessRules(ProposalPerson proposalPerson, BusinessObject source, int index) {
        return new ProposalDevelopmentKeyPersonsRule().processChangeKeyPersonBusinessRules(proposalPerson, source, index);
    }

    /**
     *
     * @see
     * org.kuali.kra.proposaldevelopment.rule.AddProposalSiteRule#processAddProposalSiteBusinessRules(org.kuali.kra.proposaldevelopment.rule.event.AddProposalSiteEvent)
     */
    public boolean processAddProposalSiteBusinessRules(AddProposalSiteEvent addProposalLocationEvent) {
        return new ProposalDevelopmentProposalLocationRule().processAddProposalSiteBusinessRules(addProposalLocationEvent);
    }

    public boolean processSaveProposalSiteBusinessRules(SaveProposalSitesEvent saveProposalSitesEvent) {
        return new ProposalDevelopmentProposalLocationRule().processSaveProposalSiteBusinessRules(saveProposalSitesEvent);
    }

    /**
     * @see
     * org.kuali.kra.proposaldevelopment.rule.PermissionsRule#processAddProposalUserBusinessRules(org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument,
     * java.util.List, org.kuali.kra.proposaldevelopment.bo.ProposalUser)
     */
    public boolean processAddProposalUserBusinessRules(ProposalDevelopmentDocument document, List<ProposalUserRoles> list, ProposalUser proposalUser) {
        return new ProposalDevelopmentPermissionsRule().processAddProposalUserBusinessRules(document, list, proposalUser);
    }

    /**
     * @see
     * org.kuali.kra.proposaldevelopment.rule.PermissionsRule#processDeleteProposalUserBusinessRules(org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument,
     * java.util.List, int)
     */
    public boolean processDeleteProposalUserBusinessRules(ProposalDevelopmentDocument document, List<ProposalUserRoles> list, int index) {
        return new ProposalDevelopmentPermissionsRule().processDeleteProposalUserBusinessRules(document, list, index);
    }

    /**
     * @see
     * org.kuali.kra.proposaldevelopment.rule.PermissionsRule#processEditProposalUserRolesBusinessRules(org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument,
     * java.util.List,
     * org.kuali.kra.proposaldevelopment.bo.ProposalUserEditRoles)
     */
    public boolean processEditProposalUserRolesBusinessRules(ProposalDevelopmentDocument document, List<ProposalUserRoles> list, ProposalUserEditRoles editRoles) {
        return new ProposalDevelopmentPermissionsRule().processEditProposalUserRolesBusinessRules(document, list, editRoles);
    }

    /**
     * Delegate to {@link org.kuali.kra.proposaldevelopment.rules.ProposalDevelopmentKeyPersonsRule#processSaveKeyPersonBusinessRules(ProposalDevelopmentDocument)
     *
     * @see org.kuali.kra.proposaldevelopment.rule.SaveKeyPersonRule#processSaveKeyPersonBusinessRules(org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument)
     */
    public boolean processSaveKeyPersonBusinessRules(ProposalDevelopmentDocument document) {
        info("In processSaveKeyPersonBusinessRules()");
        return new ProposalDevelopmentKeyPersonsRule().processCustomSaveDocumentBusinessRules(document);
    }

    /**
     * @see
     * org.kuali.kra.proposaldevelopment.rule.NewNarrativeUserRightsRule#processNewNarrativeUserRightsBusinessRules(org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument,
     * java.util.List, int)
     */
    public boolean processNewNarrativeUserRightsBusinessRules(ProposalDevelopmentDocument document,
            List<NarrativeUserRights> newNarrativeUserRights, int narrativeIndex) {
        return new ProposalDevelopmentNarrativeRule().processNewNarrativeUserRightsBusinessRules(document, newNarrativeUserRights, narrativeIndex);
    }

    public boolean processCalculateCreditSplitBusinessRules(ProposalDevelopmentDocument document) {
        return new ProposalDevelopmentKeyPersonsRule().processCalculateCreditSplitBusinessRules(document);
    }

    public boolean processProposalDataOverrideRules(ProposalDataOverrideEvent proposalDataOverrideEvent) {
        return new ProposalDevelopmentDataOverrideRule().processProposalDataOverrideRules(proposalDataOverrideEvent);
    }

    public boolean processBudgetDataOverrideRules(BudgetDataOverrideEvent budgetDataOverrideEvent) {
        return new ProposalBudgetDataOverrideRule().processBudgetDataOverrideRules(budgetDataOverrideEvent);
    }

    public boolean processResubmissionPromptBusinessRules(ResubmissionRuleEvent resubmissionRuleEvent) {
        return new ProposalDevelopmentResubmissionPromptRule().processResubmissionPromptBusinessRules(resubmissionRuleEvent);
    }

    public boolean processRules(KraDocumentEventBaseExtension event) {
        boolean retVal = false;
        retVal = event.getRule().processRules(event);
        return retVal;
    }

    private SponsorService getSponsorService() {
        return KraServiceLocator.getService(SponsorService.class);
    }

}
