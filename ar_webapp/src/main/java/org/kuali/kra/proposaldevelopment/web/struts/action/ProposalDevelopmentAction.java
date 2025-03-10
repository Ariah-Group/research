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
package org.kuali.kra.proposaldevelopment.web.struts.action;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kra.bo.CustomAttributeDocument;
import org.kuali.kra.bo.DocumentNextvalue;
import org.kuali.kra.bo.KcPerson;
import org.kuali.kra.budget.core.Budget;
import org.kuali.kra.budget.document.BudgetDocument;
import org.kuali.kra.budget.parameters.BudgetPeriod;
import org.kuali.kra.budget.rates.BudgetRate;
import org.kuali.kra.budget.rates.RateClassType;
import org.kuali.kra.budget.versions.BudgetDocumentVersion;
import org.kuali.kra.budget.web.struts.action.BudgetParentActionBase;
import org.kuali.kra.budget.web.struts.action.BudgetTDCValidator;
import org.kuali.kra.common.notification.service.KcNotificationService;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.infrastructure.RoleConstants;
import org.kuali.kra.kew.KraDocumentRejectionService;
import org.kuali.kra.kim.bo.KcKimAttributes;
import org.kuali.kra.krms.service.KrmsRulesExecutionService;
import org.kuali.kra.printing.Printable;
import org.kuali.kra.printing.print.AbstractPrint;
import org.kuali.kra.printing.service.PrintingService;
import org.kuali.kra.proposaldevelopment.bo.*;
import org.kuali.kra.proposaldevelopment.budget.bo.ProposalDevelopmentBudgetExt;
import org.kuali.kra.proposaldevelopment.budget.service.BudgetPrintService;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.proposaldevelopment.hierarchy.ProposalHierarcyActionHelper;
import org.kuali.kra.proposaldevelopment.printing.service.ProposalDevelopmentPrintingService;
import org.kuali.kra.proposaldevelopment.questionnaire.ProposalPersonQuestionnaireHelper;
import org.kuali.kra.proposaldevelopment.service.*;
import org.kuali.kra.proposaldevelopment.web.struts.form.ProposalDevelopmentForm;
import org.kuali.kra.questionnaire.answer.AnswerHeader;
import org.kuali.kra.questionnaire.print.QuestionnairePrint;
import org.kuali.kra.questionnaire.print.QuestionnairePrintingService;
import org.kuali.kra.s2s.S2SException;
import org.kuali.kra.s2s.bo.S2sOppForms;
import org.kuali.kra.s2s.bo.S2sOpportunity;
import org.kuali.kra.s2s.formmapping.FormMappingInfo;
import org.kuali.kra.s2s.formmapping.FormMappingLoader;
import org.kuali.kra.s2s.service.S2SBudgetCalculatorService;
import org.kuali.kra.s2s.service.S2SService;
import org.kuali.kra.service.KraAuthorizationService;
import org.kuali.kra.service.PersonEditableService;
import org.kuali.kra.service.SponsorService;
import org.kuali.kra.web.struts.action.AuditActionHelper;
import org.kuali.kra.web.struts.action.NonCancellingRecallQuestion;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kns.util.AuditCluster;
import org.kuali.rice.kns.util.AuditError;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import org.ariahgroup.research.bo.AttachmentDataSource;
import org.kuali.kra.bo.Sponsor;

import static org.kuali.kra.infrastructure.Constants.MAPPING_BASIC;

public class ProposalDevelopmentAction extends BudgetParentActionBase {
    
    private static final String PROPOSAL_NARRATIVE_TYPE_GROUP = "proposalNarrativeTypeGroup";
    private static final String DELIVERY_INFO_DISPLAY_INDICATOR = "deliveryInfoDisplayIndicator";
    private static final String PROPOSAL_SUMMARY_INDICATOR = "enableProposalSummaryPanel";
    private static final String BUDGET_SUMMARY_INDICATOR = "enableBudgetSummaryPanel";
    private static final String KEY_PERSONNEL_INDICATOR = "enableKeyPersonnelPanel";
    private static final String SPECIAL_REVIEW_INDICATOR = "enableSpecialReviewPanel";
    private static final String SUMMARY_PRINT_FORMS_INDICATOR = "enableSummaryPrintPanel";
    private static final String SUMMARY_CHANGE_DATA_INDICATOR = "enableSummaryChangeDataPanel";
    private static final String CUSTOM_DATA_INFO_INDICATOR = "enableCustomDataInfoPanel";
    private static final String SUMMARY_QUESTIONS_INDICATOR = "enableSummaryQuestionsPanel";
    private static final String SUMMARY_ATTACHMENTS_INDICATOR = "enableSummaryAttachmentsPanel";
    private static final String SUMMARY_KEYWORDS_INDICATOR = "enableSummaryKeywordsPanel";
    private static final String PROPOSAL_SUMMARY_DISCLAIMER_INDICATOR = "propSummaryDisclaimerText";
    private static final String SUMMARY_DATA_VALIDATION_INDICATOR = "enableSummaryDataValidationPanel";
    private static final String ERROR_NO_GRANTS_GOV_FORM_SELECTED = "error.proposalDevelopment.no.grants.gov.form.selected";
    private static final String ALL_SPONSOR_HIERARCHY_NIH_MULTI_PI = "ALL_SPONSOR_HIERARCHY_NIH_MULTI_PI";
    private static final String PERSON_INDEX = "personIndex";
    private static final String COMMENTS = "comments";
    private static final String SUMMARY_SPECIAL_REVIEW_LIST = "proposal.summary.validSpecialReviewCodes";
    private static final String PROPOSAL_ATTACHMENT_TYPE_GROUP_CODE = "P";
    private static final Log LOG = LogFactory.getLog(ProposalDevelopmentAction.class);
    private ProposalHierarcyActionHelper hierarchyHelper;
    private KcNotificationService notificationService;

    /**
     * @see
     * org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm,
     * javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = null;
        
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        String command = proposalDevelopmentForm.getCommand();
        String createProposalFromGrantsGov = request.getParameter("createProposalFromGrantsGov");
        
        String opportunityURL = request.getParameter("opportunityURL");
        String sponsorGrantsGovId = request.getParameter("sponsorId");
        
        S2sOpportunity s2sOpportunity = new S2sOpportunity();
        if (createProposalFromGrantsGov != null && createProposalFromGrantsGov.equals("true")) {
            s2sOpportunity = proposalDevelopmentForm.getNewS2sOpportunity();
            proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal().setS2sOpportunity(s2sOpportunity);
            proposalDevelopmentForm.setNewS2sOpportunity(new S2sOpportunity());
        }
        //KRACOEUS-5064
        if (proposalDevelopmentForm.getProposalDevelopmentDocument().getDocumentHeader().getDocumentNumber() == null && request.getParameter(KRADConstants.PARAMETER_DOC_ID) != null) {
            loadDocumentInForm(request, proposalDevelopmentForm);
        }
        
        ProposalDevelopmentService proposalDevelopmentService = KraServiceLocator.getService(ProposalDevelopmentService.class);
        
        if (KewApiConstants.ACTIONLIST_INLINE_COMMAND.equals(command)) {
            //forward = mapping.findForward(Constants.MAPPING_COPY_PROPOSAL_PAGE);
            //KRACOEUS-5064
            ProposalDevelopmentApproverViewDO approverViewDO = proposalDevelopmentService.populateApproverViewDO(proposalDevelopmentForm);
            proposalDevelopmentForm.setApproverViewDO(approverViewDO);
            forward = mapping.findForward(Constants.MAPPING_PROPOSAL_SUMMARY_PAGE);
            forward = new ActionForward(forward.getPath() + "?" + KRADConstants.PARAMETER_DOC_ID + "=" + request.getParameter(KRADConstants.PARAMETER_DOC_ID));
        } //else if (Constants.MAPPING_PROPOSAL_ACTIONS.equals(command)) {
        //            loadDocument(proposalDevelopmentForm);
        //            forward = actions(mapping, proposalDevelopmentForm, request, response);
        //        } else {
        //            forward = super.docHandler(mapping, form, request, response);
        //        }
        else {
            if (proposalDevelopmentForm.getDocTypeName() == null || proposalDevelopmentForm.getDocTypeName().equals("")) {
                proposalDevelopmentForm.setDocTypeName("ProposalDevelopmentDocument");
            }
            boolean rejectedDocument = false;
            KraDocumentRejectionService documentRejectionService = KraServiceLocator.getService(KraDocumentRejectionService.class);
            if (proposalDevelopmentForm.getDocument().getDocumentNumber() != null) {
                rejectedDocument = documentRejectionService.isDocumentOnInitialNode(proposalDevelopmentForm.getDocument().getDocumentNumber());
            }
            
            if (proposalDevelopmentService.canPerformWorkflowAction(proposalDevelopmentForm.getProposalDevelopmentDocument()) && !rejectedDocument) {
                
                ProposalDevelopmentApproverViewDO approverViewDO = proposalDevelopmentService.populateApproverViewDO(proposalDevelopmentForm);
                proposalDevelopmentForm.setApproverViewDO(approverViewDO);
                
                loadDocument(proposalDevelopmentForm);
                return approverView(mapping, form, request, response);
            } else if (Constants.MAPPING_PROPOSAL_ACTIONS.equals(command)) {
                loadDocument(proposalDevelopmentForm);
                forward = actions(mapping, proposalDevelopmentForm, request, response);
            } else {
                forward = super.docHandler(mapping, form, request, response);
            }
        }
        
        if (proposalDevelopmentForm.getProposalDevelopmentDocument().isProposalDeleted()) {
            return mapping.findForward("deleted");
        }
        
        if (KewApiConstants.INITIATE_COMMAND.equals(proposalDevelopmentForm.getCommand())) {
            proposalDevelopmentForm.getProposalDevelopmentDocument().initialize();
        } else {
            proposalDevelopmentForm.initialize();
        }
        
        if (Constants.MAPPING_PROPOSAL_ACTIONS.equals(command)) {
            forward = actions(mapping, proposalDevelopmentForm, request, response);
        }
        
        if (opportunityURL != null && !opportunityURL.isEmpty()) {
            s2sOpportunity.setOpportunityUrl(opportunityURL);
        }
        
        if (sponsorGrantsGovId != null && !sponsorGrantsGovId.isEmpty()) {
            s2sOpportunity.setSponsorId(sponsorGrantsGovId);
        }
        
        if (createProposalFromGrantsGov != null && createProposalFromGrantsGov.equals("true") && s2sOpportunity != null) {
            createS2sOpportunityDetails(proposalDevelopmentForm, s2sOpportunity);
        }
        
        String principalId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
        Role roleInfo = getRoleService().getRoleByNamespaceCodeAndName(RoleConstants.OSP_ROLE_TYPE, RoleConstants.OSP_ADMINISTRATOR);
        List<String> roleIds = new ArrayList<String>();
        roleIds.add(roleInfo.getId());
        Map<String, String> qualifiedRoleAttributes = new HashMap<String, String>();
        qualifiedRoleAttributes.put(KcKimAttributes.UNIT_NUMBER, "*");
        Map<String, String> qualifications = new HashMap<String, String>(qualifiedRoleAttributes);
        if (getRoleService().principalHasRole(principalId, roleIds, qualifications)) {
            proposalDevelopmentForm.setSaveXmlPermission(true);
        }
        KraAuthorizationService proposalAuthService = KraServiceLocator.getService(KraAuthorizationService.class);
        List<KcPerson> persons = proposalAuthService.getPersonsInRole(proposalDevelopmentForm.getProposalDevelopmentDocument(), RoleConstants.AGGREGATOR);
        for (KcPerson person : persons) {
            if (GlobalVariables.getUserSession().getPrincipalName().equals(person.getUserName())) {
                proposalDevelopmentForm.setSaveXmlPermission(true);
            }
        }
        if (proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal().getS2sOpportunity() != null) {
            proposalDevelopmentForm.setGrantsGovSelectFlag(true);
        }
        return forward;
    }
    
    private void createS2sOpportunityDetails(ProposalDevelopmentForm proposalDevelopmentForm, S2sOpportunity s2sOpportunity) throws S2SException {
        
        Boolean mandatoryFormNotAvailable = false;
        
        if (s2sOpportunity.getCfdaNumber() != null) {
            proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal().setCfdaNumber(s2sOpportunity.getCfdaNumber());
        }
        
        if (s2sOpportunity.getOpportunityId() != null) {
            proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal().setProgramAnnouncementNumber(s2sOpportunity.getOpportunityId());
        }
        
        if (s2sOpportunity.getOpportunityTitle() != null) {
            proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal().setProgramAnnouncementTitle(s2sOpportunity.getOpportunityTitle());
        }
        
        if (s2sOpportunity.getOpportunityUrl() != null) {
            proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal().setLinkToOpportunity(s2sOpportunity.getOpportunityUrl());
        }
        
        if (s2sOpportunity.getClosingDate() != null) {
            proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal().setDeadlineDate(new java.sql.Date(s2sOpportunity.getClosingDate().getTime()));
        }
        
        LOG.error("createS2sOpportunityDetails running....");
        LOG.error("createS2sOpportunityDetails : s2sOpportunity.getSponsorId() = " + s2sOpportunity.getSponsorId());
        if (s2sOpportunity.getSponsorId() != null && !s2sOpportunity.getSponsorId().isEmpty()) {
            Map<String, Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put("grantsGovId", s2sOpportunity.getSponsorId());
            List<Sponsor> sponsors = (List<Sponsor>) getBusinessObjectService().findMatching(Sponsor.class, fieldValues);
            
            if(sponsors != null) {
                LOG.error("createS2sOpportunityDetails : sponsors.size() = " + sponsors.size());
            } else {
                LOG.error("createS2sOpportunityDetails : sponsors is null");
            }
            
            
            if (sponsors != null && sponsors.size() > 0) {
                Sponsor spons = sponsors.get(0);
                if (spons != null) {
                    LOG.error("createS2sOpportunityDetails : spons = " + spons.getSponsorCode());
                    proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal().setSponsor(spons);
                    proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal().setSponsorCode(spons.getSponsorCode());
                }
            }
        }
        
        List<S2sOppForms> s2sOppForms = new ArrayList<S2sOppForms>();
        if (s2sOpportunity.getSchemaUrl() != null) {
            try {
                s2sOppForms = KraServiceLocator.getService(S2SService.class).parseOpportunityForms(s2sOpportunity);
            } catch (S2SException ex) {
                if (ex.getErrorKey().equals(KeyConstants.ERROR_GRANTSGOV_NO_FORM_ELEMENT)) {
                    ex.setMessage(s2sOpportunity.getOpportunityId());
                }
                if (ex.getTabErrorKey() != null) {
                    GlobalVariables.getMessageMap().putError(ex.getTabErrorKey(), ex.getErrorKey(), ex.getMessageWithParams());
                } else {
                    GlobalVariables.getMessageMap().putError(Constants.NO_FIELD, ex.getErrorKey(), ex.getMessageWithParams());
                }
            }
            List<String> mandatoryForms = new ArrayList<String>();
            if (s2sOppForms != null) {
                for (S2sOppForms s2sOppForm : s2sOppForms) {
                    if (s2sOppForm.getMandatory() && !s2sOppForm.getAvailable()) {
                        mandatoryFormNotAvailable = true;
                        mandatoryForms.add(s2sOppForm.getFormName());
//                        break;
                    }
                }
            }
            if (!mandatoryFormNotAvailable) {
                s2sOpportunity.setS2sOppForms(s2sOppForms);
                s2sOpportunity.setVersionNumber(proposalDevelopmentForm.getVersionNumberForS2sOpportunity());
                proposalDevelopmentForm.setVersionNumberForS2sOpportunity(null);
                proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal().setS2sOpportunity(s2sOpportunity);
            } else {
                GlobalVariables.getMessageMap().putError(Constants.NO_FIELD, KeyConstants.ERROR_IF_OPPORTUNITY_ID_IS_INVALID, s2sOpportunity.getOpportunityId(), mandatoryForms.toString());
                //proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal().setS2sOpportunity(new S2sOpportunity());
                proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal().setS2sOpportunity(s2sOpportunity);
            }
        }
    }
    
    protected ProposalHierarcyActionHelper getHierarchyHelper() {
        if (hierarchyHelper == null) {
            hierarchyHelper = new ProposalHierarcyActionHelper();
        }
        return hierarchyHelper;
    }
    
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ActionForward actionForward = super.execute(mapping, form, request, response);
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument document = proposalDevelopmentForm.getProposalDevelopmentDocument();
        String keywordPanelDisplay = this.getParameterService().getParameterValueAsString(
                ProposalDevelopmentDocument.class, Constants.KEYWORD_PANEL_DISPLAY);
        request.getSession().setAttribute(Constants.KEYWORD_PANEL_DISPLAY, keywordPanelDisplay);
        // TODO: not sure it's should be here - for audit error display.
        // ES: Still do not know how exactly *how* this should be done, 
        // but I added a check to only call the auditConditionally when the audit error 
        // map is empty - otherwise the display during a submit 
        //check to see if the audit errors are filled out.  This happens on a submit
        //that fails.

        if (proposalDevelopmentForm.isAuditActivated()) {
            proposalDevelopmentForm.setUnitRulesMessages(getUnitRulesMessages(proposalDevelopmentForm.getProposalDevelopmentDocument()));
        }
        if (KNSGlobalVariables.getAuditErrorMap().isEmpty()) {
            new AuditActionHelper().auditConditionally(proposalDevelopmentForm);
        }
        proposalDevelopmentForm.setProposalDataOverrideMethodToCalls(this.constructColumnsToAlterLookupMTCs(proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal().getProposalNumber()));
//            if (proposalDevelopmentForm.isAuditActivated()) {
//                if (document != null && 
//                    document.getDevelopmentProposal().getS2sOpportunity() != null ) {
//                    getService(S2SService.class).validateApplication(document);            
//                }
//            }

        //if(isPrincipalInvestigator){
        //}
        /*if(proposalDevelopmentForm.getProposalDevelopmentDocument().getSponsorCode()!=null){
         proposalDevelopmentForm.setAdditionalDocInfo1(new ConcreteKeyValue("datadictionary.Sponsor.attributes.sponsorCode.label",proposalDevelopmentForm.getProposalDevelopmentDocument().getSponsorCode()));
         }
         if(proposalDevelopmentForm.getProposalDevelopmentDocument().getPrincipalInvestigator()!=null){
         proposalDevelopmentForm.setAdditionalDocInfo2(new ConcreteKeyValue("${Document.DataDictionary.ProposalDevelopmentDocument.attributes.sponsorCode.label}",proposalDevelopmentForm.getProposalDevelopmentDocument().getPrincipalInvestigator().getFullName()));
         }*/
        // setup any Proposal Development System Parameters that will be needed
        ((ProposalDevelopmentForm) form).getProposalDevelopmentParameters().put(DELIVERY_INFO_DISPLAY_INDICATOR, this.getParameterService().getParameter(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, ParameterConstants.DOCUMENT_COMPONENT, DELIVERY_INFO_DISPLAY_INDICATOR));
        ((ProposalDevelopmentForm) form).getProposalDevelopmentParameters().put(PROPOSAL_NARRATIVE_TYPE_GROUP, this.getParameterService().getParameter(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, ParameterConstants.DOCUMENT_COMPONENT, PROPOSAL_NARRATIVE_TYPE_GROUP));
        
        if (document.getDevelopmentProposal().getS2sOpportunity() != null && document.getDevelopmentProposal().getS2sOpportunity().getS2sOppForms() != null) {
            Collections.sort(document.getDevelopmentProposal().getS2sOpportunity().getS2sOppForms(), new S2sOppFormsComparator1());
            Collections.sort(document.getDevelopmentProposal().getS2sOpportunity().getS2sOppForms(), new S2sOppFormsComparator3());
        }
        return actionForward;
    }
    
    protected List<String> getUnitRulesMessages(ProposalDevelopmentDocument pdDoc) {
        KrmsRulesExecutionService rulesService = KraServiceLocator.getService(KrmsRulesExecutionService.class);
        return rulesService.processUnitValidations(pdDoc.getLeadUnitNumber(), pdDoc);
    }

    /**
     * Do nothing. Used when the Proposal is in view-only mode. Instead of
     * saving the proposal when the tab changes, we simply do nothing.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward nullOp(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * @see
     * org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        ProposalDevelopmentDocument document = ((ProposalDevelopmentForm) kualiDocumentFormBase).getProposalDevelopmentDocument();
        loadDocument(document);
    }

    /**
     *
     * Called to load all necessary elements in the proposal development
     * document
     *
     * @param document
     */
    protected void loadDocument(ProposalDevelopmentDocument document) {
        getKeyPersonnelService().populateDocument(document);
        updateNIHDescriptions(document);
        setBudgetStatuses(document);
    }
    
    protected SponsorService getSponsorService() {
        return KraServiceLocator.getService(SponsorService.class);
    }

    /**
     *
     * Updates portions of the proposal that are not persisted and are based on
     * whether the sponsor is NIH or not
     *
     * @param document
     */
    protected void updateNIHDescriptions(ProposalDevelopmentDocument document) {
        SponsorService sponsorService = getSponsorService();
        DevelopmentProposal proposal = document.getDevelopmentProposal();
        // Update the NIH related properties since this information is not persisted with the document
        // (isSponsorNih sets the nih property as a side effect)
        boolean isSponsorMultiPi = false;
        if (proposal.getSponsor() != null) {
            isSponsorMultiPi = proposal.getSponsor().isMultiplePi();
        }
        
        if (isSponsorMultiPi) {
            proposal.setNihDescription(getKeyPersonnelService().loadKeyPersonnelRoleDescriptions(true));
        }
        boolean multiPIFlag = getParameterService().getParameterValueAsBoolean(ProposalDevelopmentDocument.class,
                ALL_SPONSOR_HIERARCHY_NIH_MULTI_PI);
        if (multiPIFlag) {
            proposal.setSponsorNihMultiplePi(true);
            proposal.setSponsorNihOsc(true);
        } else {
            
            boolean isSponsorOscEligible = false;
            if (proposal.getSponsor() != null) {
                isSponsorOscEligible = proposal.getSponsor().isOtherSignContrib();
            }
            
            proposal.setSponsorNihMultiplePi(isSponsorMultiPi);
            proposal.setSponsorNihOsc(isSponsorOscEligible);
        }
    }
    
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // We will need to determine if the proposal is being saved for the first time.
        final ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        final ProposalDevelopmentDocument doc = proposalDevelopmentForm.getProposalDevelopmentDocument();
        //if the proposal hasn't been saved yet, the s2sopp proposal number will be null. We need to save it in the form until we
        //have a proposal number to set due to OJBs difficulty in dealing with 1-to-1 relationships.
        S2sOpportunity s2sOpportunity = proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal().getS2sOpportunity();
        if (s2sOpportunity != null && s2sOpportunity.getProposalNumber() == null) {
            proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal().setS2sOpportunity(null);
            proposalDevelopmentForm.setS2sOpportunity(s2sOpportunity);
        }
        updateProposalDocument(proposalDevelopmentForm);
        
        preSave(mapping, proposalDevelopmentForm, request, response);
        
        ActionForward forward = super.save(mapping, form, request, response);
        // If validation is turned on, take the user to the proposal actions page (which contains the validation panel, which auto-expands)
        if (proposalDevelopmentForm.isAuditActivated()) {
            forward = mapping.findForward(Constants.MAPPING_PROPOSAL_ACTIONS);
        }
        s2sOpportunity = proposalDevelopmentForm.getS2sOpportunity();
        if (s2sOpportunity != null) {
            doc.getDevelopmentProposal().setS2sOpportunity(s2sOpportunity);
            s2sOpportunity.setProposalNumber(doc.getDevelopmentProposal().getProposalNumber());
            getBusinessObjectService().save(s2sOpportunity);
            proposalDevelopmentForm.setS2sOpportunity(null);
        }
        
        doc.getDevelopmentProposal().updateProposalNumbers();
        
        proposalDevelopmentForm.setFinalBudgetVersion(getFinalBudgetVersion(doc.getBudgetDocumentVersions()));
        setBudgetStatuses(doc);

        //if not on budget page
        if ("ProposalDevelopmentBudgetVersionsAction".equals(proposalDevelopmentForm.getActionName())) {
            GlobalVariables.getMessageMap().addToErrorPath(KRADConstants.DOCUMENT_PROPERTY_NAME + ".proposal");
            
            final BudgetTDCValidator tdcValidator = new BudgetTDCValidator(request);
            tdcValidator.validateGeneratingErrorsAndWarnings(doc);
        }
        if (doc.getBudgetDocumentVersions() != null && !doc.getBudgetDocumentVersions().isEmpty()) {
            for (BudgetDocumentVersion bdv : doc.getBudgetDocumentVersions()) {
                ProposalDevelopmentBudgetExt budget = this.getBusinessObjectService().findBySinglePrimaryKey(ProposalDevelopmentBudgetExt.class, bdv.getBudgetVersionOverview().getBudgetId());
                if (!budget.getFinalVersionFlag()) {
                    budget.setStartDate(proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal().getRequestedStartDateInitial());
                    budget.setEndDate(proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal().getRequestedEndDateInitial());
                    this.getBusinessObjectService().save(budget);
                }
            }
        }
        
        return forward;
    }

    /**
     * @see
     * org.kuali.kra.web.struts.action.KraTransactionalDocumentActionBase#saveOnClose(org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm,
     * javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected ActionForward saveOnClose(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.saveOnClose(mapping, form, request, response);
        
        final ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        final ProposalDevelopmentDocument doc = proposalDevelopmentForm.getProposalDevelopmentDocument();
        
        updateProposalDocument(proposalDevelopmentForm);
        
        doc.getDevelopmentProposal().updateProposalNumbers();
        
        return forward;
    }
    
    @Override
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        
        if (proposalDevelopmentForm.getViewFundingSource()) {
            return mapping.findForward(Constants.MAPPING_CLOSE_PAGE);
        } else {
            return super.close(mapping, form, request, response);
        }
    }

    /**
     *
     * This method attempts to deal with the multiple pessimistic locks that can
     * be on the proposal development document Proposal, Narratives, and Budget
     * must all be treated separately and therefore the other portions of the
     * document, outside of the one currently being saved, must be updated from
     * the database to make sure the sessions do not stomp changes already
     * persisted by another session.
     *
     * @param pdForm
     * @throws Exception
     */
    protected void updateProposalDocument(ProposalDevelopmentForm pdForm) throws Exception {
        ProposalDevelopmentDocument pdDocument = pdForm.getProposalDevelopmentDocument();
        ProposalDevelopmentDocument updatedDocCopy = getProposalDoc(pdDocument.getDocumentNumber());
        
        if (updatedDocCopy != null) {

            //For Budget and Narrative Lock regions, this is the only way in which a Proposal Document might get updated
            if (StringUtils.isNotEmpty(pdForm.getActionName()) && updatedDocCopy != null) {
                if (!pdForm.getActionName().equalsIgnoreCase("ProposalDevelopmentBudgetVersionsAction")) {
                    pdDocument.setBudgetDocumentVersions(updatedDocCopy.getBudgetDocumentVersions());
                    pdDocument.getDevelopmentProposal().setBudgetStatus(updatedDocCopy.getDevelopmentProposal().getBudgetStatus());
                } else {
                    //in case other parts of the document have been saved since we have saved,
                    //we save off possibly changed parts and reload the rest of the document
                    List<BudgetDocumentVersion> newVersions = pdDocument.getBudgetDocumentVersions();
                    String budgetStatus = pdDocument.getDevelopmentProposal().getBudgetStatus();
                    
                    pdForm.setDocument(updatedDocCopy);
                    pdDocument = updatedDocCopy;
                    loadDocument(pdDocument);
                    
                    pdDocument.setBudgetDocumentVersions(newVersions);
                    pdDocument.getDevelopmentProposal().setBudgetStatus(budgetStatus);
                }
                if (!pdForm.getActionName().equalsIgnoreCase("ProposalDevelopmentAbstractsAttachmentsAction")) {
                    pdDocument.getDevelopmentProposal().setNarratives(updatedDocCopy.getDevelopmentProposal().getNarratives());
                    pdDocument.getDevelopmentProposal().setInstituteAttachments(updatedDocCopy.getDevelopmentProposal().getInstituteAttachments());
                    pdDocument.getDevelopmentProposal().setProposalAbstracts(updatedDocCopy.getDevelopmentProposal().getProposalAbstracts());
                    pdDocument.getDevelopmentProposal().setPropPersonBios(updatedDocCopy.getDevelopmentProposal().getPropPersonBios());
                    removePersonnelAttachmentForDeletedPerson(pdDocument);
                } else {
                    //in case other parts of the document have been saved since we have saved,
                    //we save off possibly changed parts and reload the rest of the document
                    List<Narrative> newNarratives = pdDocument.getDevelopmentProposal().getNarratives();
                    List<Narrative> instituteAttachments = pdDocument.getDevelopmentProposal().getInstituteAttachments();
                    List<ProposalAbstract> newAbstracts = pdDocument.getDevelopmentProposal().getProposalAbstracts();
                    List<ProposalPersonBiography> newBiographies = pdDocument.getDevelopmentProposal().getPropPersonBios();
                    
                    pdForm.setDocument(updatedDocCopy);
                    pdDocument = updatedDocCopy;
                    loadDocument(pdDocument);
                    
                    List<Narrative> newNarrativesCopy = new ArrayList<Narrative>();
                    if (newNarratives != null) {
                        for (Narrative refreshNarrativesList : newNarratives) {
                            if (refreshNarrativesList.getNarrativeType().getNarrativeTypeGroup()
                                    .equalsIgnoreCase(PROPOSAL_ATTACHMENT_TYPE_GROUP_CODE)) {
                                newNarrativesCopy.add(refreshNarrativesList);
                            }
                        }
                    }
                    //now re-add narratives that could include changes and can't be modified otherwise
                    pdDocument.getDevelopmentProposal().setNarratives(newNarrativesCopy);
                    pdDocument.getDevelopmentProposal().setInstituteAttachments(instituteAttachments);
                    pdDocument.getDevelopmentProposal().setProposalAbstracts(newAbstracts);
                    pdDocument.getDevelopmentProposal().setPropPersonBios(newBiographies);
                    
                }
            }

            //rice objects are still using optimistic locking so update rice BO versions
            //if no other session has saved this document we should be updating with same version number, but no easy way to know if it has been
            //I don't think anyway? So do it every time.
            pdDocument.getDocumentHeader().setVersionNumber(updatedDocCopy.getDocumentHeader().getVersionNumber());
            int noteIndex = 0;
            for (Object note : pdDocument.getNotes()) {
                Note updatedNote = updatedDocCopy.getNote(noteIndex);
                ((Note) note).setVersionNumber(updatedNote.getVersionNumber());
                noteIndex++;
            }
            for (DocumentNextvalue documentNextValue : pdDocument.getDocumentNextvalues()) {
                DocumentNextvalue updatedDocumentNextvalue = updatedDocCopy.getDocumentNextvalueBo(documentNextValue.getPropertyName());
                if (updatedDocumentNextvalue != null) {
                    documentNextValue.setVersionNumber(updatedDocumentNextvalue.getVersionNumber());
                }
            }
            //fix budget document version's document headers
            for (int i = 0; i < pdDocument.getBudgetDocumentVersions().size(); i++) {
                BudgetDocumentVersion curVersion = pdDocument.getBudgetDocumentVersion(i);
                BudgetDocumentVersion otherVersion = updatedDocCopy.getBudgetDocumentVersion(i);
                otherVersion.refreshReferenceObject("documentHeader");
                if (curVersion != null && otherVersion != null) {
                    curVersion.getDocumentHeader().setVersionNumber(otherVersion.getDocumentHeader().getVersionNumber());
                }
            }
            pdForm.setDocument(pdDocument);
        }
    }

    /*
     * The updatePD has some issue, such as if person is deleted, and the person attachment is also deleted.
     * However, the updateProposalDocument recover everything from DB.  so, add this method to delete the deleted, but not saved personnel attachment.
     */
    private void removePersonnelAttachmentForDeletedPerson(ProposalDevelopmentDocument proposaldevelopmentDocument) {
        
        List<ProposalPersonBiography> personAttachments = new ArrayList();
        for (ProposalPersonBiography proposalPersonBiography : proposaldevelopmentDocument.getDevelopmentProposal()
                .getPropPersonBios()) {
            boolean personFound = false;
            for (ProposalPerson person : proposaldevelopmentDocument.getDevelopmentProposal().getProposalPersons()) {
                if (proposalPersonBiography.getProposalPersonNumber().equals(person.getProposalPersonNumber())) {
                    personFound = true;
                    break;
                }
            }
            if (!personFound) {
                personAttachments.add(proposalPersonBiography);
            }
            
        }
        if (!personAttachments.isEmpty()) {
            proposaldevelopmentDocument.getDevelopmentProposal().getPropPersonBios().removeAll(personAttachments);
        }
    }

//    private boolean isPropertyGetterMethod(Method method, Method methods[]) {
//        if (method.getName().startsWith("get") && method.getParameterTypes().length == 0) {
//            String setterName = method.getName().replaceFirst("get", "set");
//            for (Method m : methods) {
//                if (m.getName().equals(setterName)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
    protected ProposalDevelopmentDocument getProposalDoc(String pdDocumentNumber) throws Exception {
        ProposalDevelopmentDocument newCopy;
        DocumentService docService = KraServiceLocator.getService(DocumentService.class);
        newCopy = (ProposalDevelopmentDocument) docService.getByDocumentHeaderId(pdDocumentNumber);
        return newCopy;
    }
    
    public ActionForward proposal(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return mapping.findForward(Constants.PROPOSAL_PAGE);
    }

    /**
     * Action called to forward to a new KeyPersonnel tab.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward instance for forwarding to the tab.
     */
    public ActionForward keyPersonnel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        getKeyPersonnelService().populateDocument(pdform.getProposalDevelopmentDocument());

        // Let this be taken care of in KeyPersonnelAction execute() method
        if (this instanceof ProposalDevelopmentKeyPersonnelAction) {
            LOG.info("forwarding to keyPersonnel action");
            return mapping.findForward(Constants.KEY_PERSONNEL_PAGE);
        }
        
        new ProposalDevelopmentKeyPersonnelAction().prepare(form, request);
        
        return mapping.findForward(Constants.KEY_PERSONNEL_PAGE);
    }
    
    public ActionForward specialReview(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal().refreshReferenceObject("propSpecialReviews");
        ((ProposalDevelopmentForm) form).getSpecialReviewHelper().prepareView();
        return mapping.findForward(Constants.SPECIAL_REVIEW_PAGE);
    }
    
    public ActionForward permissions(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return mapping.findForward(Constants.PERMISSIONS_PAGE);
    }

    /**
     * method for setting the proposal Summary person certification Details.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getProposalPersonCertification(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument document = pdform.getProposalDevelopmentDocument();
        String personIndex = request.getParameter(PERSON_INDEX);
        request.setAttribute(PERSON_INDEX, personIndex);
        return mapping.findForward(Constants.PERSON_CERTIFICATE);
    }

    /**
     * method for setting the proposal Summary key person comment Details.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getProposalComment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument document = proposalDevelopmentForm.getProposalDevelopmentDocument();
        String personIndex = request.getParameter(PERSON_INDEX);
        String comments = request.getParameter(COMMENTS);
        request.setAttribute(COMMENTS, comments);
        request.setAttribute(PERSON_INDEX, personIndex);
        return mapping.findForward(Constants.PERSON_COMMENT);
    }
    
    public ActionForward hierarchy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        ProposalDevelopmentForm pdForm = (ProposalDevelopmentForm) form;
        pdForm.setHierarchyProposalSummaries(getHierarchyHelper().getHierarchyProposalSummaries(pdForm.getProposalDevelopmentDocument().getDevelopmentProposal().getProposalNumber()));
        return mapping.findForward(Constants.HIERARCHY_PAGE);
    }
    
    public ActionForward grantsGov(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        if (!((ProposalDevelopmentForm) form).isGrantsGovEnabled()) {
            GlobalVariables.getMessageMap().putWarning(Constants.NO_FIELD, KeyConstants.ERROR_IF_GRANTS_GOV_IS_DISABLED);
        }
        return mapping.findForward(Constants.GRANTS_GOV_PAGE);
    }
    
    public ActionForward budgetVersions(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        final ProposalDevelopmentForm pdForm = (ProposalDevelopmentForm) form;
        final String headerTabCall = getHeaderTabDispatch(request);
        if (StringUtils.isEmpty(headerTabCall)) {
            pdForm.getDocument().refreshPessimisticLocks();
        }
        pdForm.setFinalBudgetVersion(getFinalBudgetVersion(pdForm.getProposalDevelopmentDocument().getBudgetDocumentVersions()));
        setBudgetStatuses(pdForm.getProposalDevelopmentDocument());
        
        final BudgetTDCValidator tdcValidator = new BudgetTDCValidator(request);
        tdcValidator.validateGeneratingWarnings(pdForm.getProposalDevelopmentDocument());
        
        return mapping.findForward(Constants.PD_BUDGET_VERSIONS_PAGE);
    }
    
    @SuppressWarnings("unchecked")
    public ActionForward abstractsAttachments(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        // TODO temporarily to set up proposal person- remove this once keyperson is completed and htmlunit testing fine
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument doc = proposalDevelopmentForm.getProposalDevelopmentDocument();
        doc.getDevelopmentProposal().populateNarrativeRightsForLoggedinUser();

        /*
         * Save the current set of narratives.  In some cases, a user can view the
         * narrative panel info, but is not allowed to change it.  We will make a
         * copy of the original narratives to use for comparison when a save occurs.
         * If a user attempted to change a narrative they were not authorized to,
         * then an error will be posted.
         */
        List<Narrative> narratives = (List<Narrative>) ObjectUtils.deepCopy((Serializable) doc.getDevelopmentProposal().getNarratives());
        proposalDevelopmentForm.setNarratives(narratives);
        KraServiceLocator.getService(ProposalPersonBiographyService.class).setPersonnelBioTimeStampUser(doc.getDevelopmentProposal().getPropPersonBios());
        List<Narrative> narrativeList = new ArrayList<Narrative>();
        narrativeList.addAll(doc.getDevelopmentProposal().getNarratives());
        narrativeList.addAll(doc.getDevelopmentProposal().getInstituteAttachments());
        KraServiceLocator.getService(NarrativeService.class).setNarrativeTimeStampUser(narrativeList);
        KraServiceLocator.getService(ProposalAbstractsService.class).loadAbstractsUploadUserFullName(doc.getDevelopmentProposal().getProposalAbstracts());
        
        return mapping.findForward(Constants.ATTACHMENTS_PAGE);
    }
    
    public ActionForward customData(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        SortedMap<String, List<CustomAttributeDocument>> customAttributeGroups = new TreeMap<String, List<CustomAttributeDocument>>();
        
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument doc = proposalDevelopmentForm.getProposalDevelopmentDocument();
        proposalDevelopmentForm.getCustomDataHelper().prepareCustomData();
        return mapping.findForward(Constants.CUSTOM_ATTRIBUTES_PAGE);
    }
    
    public ActionForward actions(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        if (proposalDevelopmentForm.getProposalDevelopmentDocument().getDocumentNumber() == null) {
            // If entering this action from copy link on doc search
            loadDocumentInForm(request, proposalDevelopmentForm);
        }
        ProposalDevelopmentDocument proposalDevelopmentDocument = proposalDevelopmentForm.getProposalDevelopmentDocument();
        ProposalDevelopmentPrintingService printService = KraServiceLocator.getService(ProposalDevelopmentPrintingService.class);
        printService.populateSponsorForms(proposalDevelopmentForm.getSponsorFormTemplates(), proposalDevelopmentDocument.getDevelopmentProposal().getSponsorCode());
        return mapping.findForward(Constants.PROPOSAL_ACTIONS_PAGE);
    }

    /**
     *
     * This method gets called upon navigation to Medusa tab.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward medusa(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        if (proposalDevelopmentForm.getProposalDevelopmentDocument().getDocumentNumber() == null) {
            // If entering this action from the medusa link on the search
            loadDocumentInForm(request, proposalDevelopmentForm);
        }
        ProposalDevelopmentDocument document = proposalDevelopmentForm.getProposalDevelopmentDocument();
        String proposalNumber = document.getDevelopmentProposal().getProposalNumber();
        proposalDevelopmentForm.getMedusaBean().setMedusaViewRadio("0");
        proposalDevelopmentForm.getMedusaBean().setModuleName("DP");
        proposalDevelopmentForm.getMedusaBean().setModuleIdentifier(Long.valueOf(proposalNumber));
        proposalDevelopmentForm.getMedusaBean().generateParentNodes();
        return mapping.findForward(Constants.MAPPING_PROPOSAL_MEDUSA_PAGE);
    }

    /**
     * This method processes an auditMode action request
     *
     * @param mapping ActionMapping
     * @param form ActionForm
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return ActionForward to forward to ("auditMode")
     */
    public ActionForward auditMode(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        new AuditActionHelper().auditConditionally((ProposalDevelopmentForm) form);
        return mapping.findForward("auditMode");
    }

    /**
     * Grabs the <code>{@link KeyPersonnelService} from Spring!
     *
     * @return KeyPersonnelService
     */
    protected KeyPersonnelService getKeyPersonnelService() {
        return KraServiceLocator.getService(KeyPersonnelService.class);
    }
    
    protected PersonEditableService getPersonEditableService() {
        return KraServiceLocator.getService(PersonEditableService.class);
    }

    /**
     * @see
     * org.kuali.kra.web.struts.action.KraTransactionalDocumentActionBase#initialDocumentSave(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void initialDocumentSave(KualiDocumentFormBase form) throws Exception {
        ProposalDevelopmentForm pdForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument doc = pdForm.getProposalDevelopmentDocument();
        initializeProposalUsers(doc);
        //on initialization of a new document the original lead unit will be blank
        //and so on first save we need to make sure to fix it
        if (pdForm.getCopyCriteria() != null) {
            pdForm.getCopyCriteria().setOriginalLeadUnitNumber(doc.getDevelopmentProposal().getOwnedByUnitNumber());
        }
    }

    /**
     * Create the original set of Proposal Users for a new Proposal Development
     * Document. The creator the proposal is assigned to the AGGREGATOR role.
     */
    protected void initializeProposalUsers(ProposalDevelopmentDocument doc) {

        // Assign the creator of the proposal to the AGGREGATOR role.
        String userId = GlobalVariables.getUserSession().getPrincipalId();
        KraAuthorizationService kraAuthService = KraServiceLocator.getService(KraAuthorizationService.class);
        if (!kraAuthService.hasRole(userId, doc, RoleConstants.AGGREGATOR)) {
            kraAuthService.addRole(userId, RoleConstants.AGGREGATOR, doc);
        }

        // Add the users defined in the role templates for the proposal's lead unit
        ProposalRoleTemplateService proposalRoleTemplateService = KraServiceLocator.getService(ProposalRoleTemplateService.class);
        proposalRoleTemplateService.addUsers(doc);
    }
    
    protected void loadDocumentInForm(HttpServletRequest request, ProposalDevelopmentForm proposalDevelopmentForm)
            throws WorkflowException {
        String docIdRequestParameter = request.getParameter(KRADConstants.PARAMETER_DOC_ID);
        ProposalDevelopmentDocument retrievedDocument = (ProposalDevelopmentDocument) KRADServiceLocatorWeb.getDocumentService().getByDocumentHeaderId(docIdRequestParameter);
        proposalDevelopmentForm.setDocument(retrievedDocument);
        proposalDevelopmentForm.setDocTypeName(retrievedDocument.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
        request.setAttribute(KRADConstants.PARAMETER_DOC_ID, docIdRequestParameter);

        // Set lead unit on form when copying a document. This is needed so the lead unit shows up on the "Copy to New Document" panel under Proposal Actions.
        ProposalCopyCriteria cCriteria = proposalDevelopmentForm.getCopyCriteria();
        if (cCriteria != null) {
            cCriteria.setOriginalLeadUnitNumber(retrievedDocument.getDevelopmentProposal().getOwnedByUnitNumber());
        }
    }

    /**
     * Overriding headerTab to customize how clearing tab state works on PDForm.
     */
    @Override
    public ActionForward headerTab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ((KualiForm) form).setTabStates(new HashMap<String, String>());
        return super.headerTab(mapping, form, request, response);
    }

    /**
     *
     * This method is called to print forms
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward printForms(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        super.save(mapping, form, request, response);
        ProposalDevelopmentDocument proposalDevelopmentDocument = proposalDevelopmentForm.getProposalDevelopmentDocument();
        boolean grantsGovErrorExists = false;
        
        if (proposalDevelopmentDocument.getDevelopmentProposal().getSelectedS2sOppForms().isEmpty()) {    // error, no form is selected
            GlobalVariables.getMessageMap().putError("noKey", ERROR_NO_GRANTS_GOV_FORM_SELECTED);
            return mapping.findForward(Constants.PROPOSAL_ACTIONS_PAGE);
        }
        AttachmentDataSource attachmentDataSource = KraServiceLocator.getService(S2SService.class).printForm(proposalDevelopmentDocument);
        if (attachmentDataSource == null || attachmentDataSource.getContent() == null || attachmentDataSource.getContent().length == 0) {
            //KRACOEUS-3300 - there should be GrantsGov audit errors in this case, grab them and display them as normal errors on
            //the GrantsGov forms tab so we don't need to turn on auditing
            Iterator<String> iter = KNSGlobalVariables.getAuditErrorMap().keySet().iterator();
            while (iter.hasNext()) {
                String errorKey = (String) iter.next();
                AuditCluster auditCluster = (AuditCluster) KNSGlobalVariables.getAuditErrorMap().get(errorKey);
                if (StringUtils.equalsIgnoreCase(auditCluster.getCategory(), Constants.GRANTSGOV_ERRORS)) {
                    grantsGovErrorExists = true;
                    for (Object error : auditCluster.getAuditErrorList()) {
                        AuditError auditError = (AuditError) error;
                        GlobalVariables.getMessageMap().putError("grantsGovFormValidationErrors", auditError.getMessageKey(), auditError.getParams());
                    }
                }
            }
        }
        if (grantsGovErrorExists) {
            GlobalVariables.getMessageMap().putError("grantsGovFormValidationErrors", KeyConstants.VALIDATTION_ERRORS_BEFORE_GRANTS_GOV_SUBMISSION);
            return mapping.findForward(Constants.GRANTS_GOV_PAGE);
        }
        if (proposalDevelopmentDocument.getDevelopmentProposal().getGrantsGovSelectFlag()) {
            String loggingDirectory = KraServiceLocator.getService(ConfigurationService.class).getPropertyValueAsString(Constants.PRINT_XML_DIRECTORY);
            String saveXmlFolderName = proposalDevelopmentDocument.getSaveXmlFolderName();
            if (StringUtils.isNotBlank(loggingDirectory)) {
                File directory = new File(loggingDirectory);
                if (!directory.exists()) {
                    directory.createNewFile();
                }
                if (!loggingDirectory.endsWith("/")) {
                    loggingDirectory += "/";
                }
                File grantsGovXmlDirectoryFile = new File(loggingDirectory + saveXmlFolderName + ".zip");
                byte[] bytes = new byte[(int) grantsGovXmlDirectoryFile.length()];
                
                FileInputStream fileInputStream = null;
                ByteArrayOutputStream baos = null;
                
                try {
                    fileInputStream = new FileInputStream(grantsGovXmlDirectoryFile);
                    fileInputStream.read(bytes);
                    
                    baos = new ByteArrayOutputStream(bytes.length);
                    baos.write(bytes);
                    WebUtils.saveMimeOutputStreamAsFile(response, "binary/octet-stream", baos, saveXmlFolderName + ".zip");
                    
                } finally {
                    try {
                        if (fileInputStream != null) {
                            fileInputStream.close();
                            fileInputStream = null;
                        }
                    } catch (IOException ioEx) {
                        LOG.warn(ioEx.getMessage(), ioEx);
                    }
                    try {
                        if (baos != null) {
                            baos.close();
                            baos = null;
                        }
                    } catch (IOException ioEx) {
                        LOG.warn(ioEx.getMessage(), ioEx);
                    }
                }
            }
            proposalDevelopmentDocument.getDevelopmentProposal().setGrantsGovSelectFlag(false);
            return mapping.findForward(Constants.MAPPING_BASIC);
        }
        if (attachmentDataSource == null || attachmentDataSource.getContent() == null) {
            return mapping.findForward(Constants.MAPPING_PROPOSAL_ACTIONS);
        }
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream(attachmentDataSource.getContent().length);
            baos.write(attachmentDataSource.getContent());
            WebUtils.saveMimeOutputStreamAsFile(response, attachmentDataSource.getContentType(), baos, attachmentDataSource.getFileName());
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                    baos = null;
                }
            } catch (IOException ioEx) {
                LOG.warn(ioEx.getMessage(), ioEx);
            }
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This method produces a list of strings containg the methodToCall to be
     * registered for each of the ProposalColumnsToAlter lookup buttons that can
     * be rendered on the Proposal Data Override tab. The execute method in this
     * class puts this list into the form. The Proposal Data Override tag file
     * then calls registerEditableProperty on each when rendering the tab.
     *
     * @param proposalNumber The proposal number for which we are generating the
     * list for.
     * @return Possible editable properties that can be called from the page.
     */
    public List<String> constructColumnsToAlterLookupMTCs(String proposalNumber) {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        ProposalDevelopmentService proposalDevelopmentService = KraServiceLocator.getService(ProposalDevelopmentService.class);
        Collection<ProposalColumnsToAlter> proposalColumnsToAlterCollection = (KraServiceLocator.getService(BusinessObjectService.class).findMatching(ProposalColumnsToAlter.class, filterMap));
        
        List<String> mtcReturn = new ArrayList<String>();
        
        for (ProposalColumnsToAlter pcta : proposalColumnsToAlterCollection) {
            if (pcta.getHasLookup()) {
                Map<String, Object> primaryKeys = new HashMap<String, Object>();
                primaryKeys.put("columnName", pcta.getColumnName());
                Object fieldValue = proposalDevelopmentService.getProposalFieldValueFromDBColumnName(proposalNumber, pcta.getColumnName());
                String displayAttributeName = pcta.getLookupReturn();
                String displayLookupReturnValue = proposalDevelopmentService.getDataOverrideLookupDisplayReturnValue(pcta.getLookupClass());
                mtcReturn.add("methodToCall.performLookup.(!!" + pcta.getLookupClass() + "!!).(((" + displayLookupReturnValue + ":newProposalChangedData.changedValue," + displayAttributeName + ":newProposalChangedData.displayValue))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).anchorProposalDataOverride");
            }
        }
        return mtcReturn;
    }

    /**
     *
     * Handy method to stream the byte array to response object
     *
     * @param attachmentDataSource
     * @param response
     * @throws Exception
     */
    @Override
    protected void streamToResponse(AttachmentDataSource attachmentDataSource, HttpServletResponse response) throws Exception {
        byte[] xbts = attachmentDataSource.getContent();
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream(xbts.length);
            baos.write(xbts);
            WebUtils.saveMimeOutputStreamAsFile(response, attachmentDataSource.getContentType(), baos, attachmentDataSource.getFileName());
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                    baos = null;
                }
            } catch (IOException ioEx) {
                LOG.warn(ioEx.getMessage(), ioEx);
            }
        }
    }

    /**
     * This method gets called upon navigation to Questionnaire tab.
     *
     * @param mapping the Action Mapping
     * @param form the Action Form
     * @param request the Http Request
     * @param response Http Response
     * @return the Action Forward
     */
    public ActionForward questions(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        
        proposalDevelopmentForm.getQuestionnaireHelper().prepareView();
        proposalDevelopmentForm.getS2sQuestionnaireHelper().prepareView();
        //((ProposalDevelopmentForm)form).getQuestionnaireHelper().setSubmissionActionTypeCode(getSubmitActionType(request));
//        if (CollectionUtils.isEmpty(proposalDevelopmentForm.getQuestionnaireHelper().getAnswerHeaders())) {
        proposalDevelopmentForm.getQuestionnaireHelper().populateAnswers();
//        } else {
//            //nothing to do in this case right now..
//        }

        proposalDevelopmentForm.getS2sQuestionnaireHelper().populateAnswers();
        
        return mapping.findForward(Constants.QUESTIONS_PAGE);
    }

    /**
     * Action called to forward to approverView tab.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward instance for forwarding to the tab.
     */
    public ActionForward approverView(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument document = pdform.getProposalDevelopmentDocument();
        getKeyPersonnelService().populateDocument(pdform.getProposalDevelopmentDocument());
        BudgetDocument budgetDocument = getS2SBudgetCalculatorService().getFinalBudgetVersion(document);
        if (budgetDocument != null) {
            Budget budget = budgetDocument.getBudget();
            if (budget.getFinalVersionFlag()) {
                final Map<String, Object> fieldValues = new HashMap<String, Object>();
                fieldValues.put("budgetId", budget.getBudgetId());
                BusinessObjectService businessObjectService = KraServiceLocator.getService(BusinessObjectService.class);
                List<BudgetPeriod> budgetPeriods = (List<BudgetPeriod>) businessObjectService.findMatching(BudgetPeriod.class, fieldValues);
                budget.setBudgetPeriods(budgetPeriods);
                Collection<BudgetRate> rates = businessObjectService.findMatching(BudgetRate.class, fieldValues);
                if (!CollectionUtils.isEmpty(rates)) {
                    List<RateClassType> rateClassTypes = (List<RateClassType>) businessObjectService.findAll(RateClassType.class);
                    budget.setRateClassTypes(rateClassTypes);
                    pdform.setBudgetToSummarize(budget);
                }
                pdform.setBudgetToSummarize(budget);
            }
            if (budget.getBudgetPrintForms().isEmpty()) {
                BudgetPrintService budgetPrintService = KraServiceLocator.getService(BudgetPrintService.class);
                budgetPrintService.populateBudgetPrintForms(budget);
            }
        }
        ProposalDevelopmentPrintingService printService = KraServiceLocator.getService(ProposalDevelopmentPrintingService.class);
        printService.populateSponsorForms(pdform.getSponsorFormTemplates(), document.getDevelopmentProposal().getSponsorCode());
        pdform.getQuestionnaireHelper().prepareView();
        pdform.getS2sQuestionnaireHelper().prepareView();
        if (CollectionUtils.isEmpty(pdform.getQuestionnaireHelper().getAnswerHeaders())) {
            pdform.getQuestionnaireHelper().populateAnswers();
        }
        List<ProposalPersonQuestionnaireHelper> proposalPersonQuestionnaireHelpers = new ArrayList<ProposalPersonQuestionnaireHelper>();
        for (ProposalPerson person : document.getDevelopmentProposal().getProposalPersons()) {
            ProposalPersonQuestionnaireHelper helper = new ProposalPersonQuestionnaireHelper(pdform, person);
            proposalPersonQuestionnaireHelpers.add(helper);
        }
        pdform.setProposalPersonQuestionnaireHelpers(proposalPersonQuestionnaireHelpers);
        
        pdform.getProposalDevelopmentParameters().put(PROPOSAL_SUMMARY_INDICATOR, this.getParameterService().getParameter(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, ParameterConstants.DOCUMENT_COMPONENT, PROPOSAL_SUMMARY_INDICATOR));
        pdform.getProposalDevelopmentParameters().put(BUDGET_SUMMARY_INDICATOR, this.getParameterService().getParameter(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, ParameterConstants.DOCUMENT_COMPONENT, BUDGET_SUMMARY_INDICATOR));
        pdform.getProposalDevelopmentParameters().put(KEY_PERSONNEL_INDICATOR, this.getParameterService().getParameter(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, ParameterConstants.DOCUMENT_COMPONENT, KEY_PERSONNEL_INDICATOR));
        pdform.getProposalDevelopmentParameters().put(SPECIAL_REVIEW_INDICATOR, this.getParameterService().getParameter(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, ParameterConstants.DOCUMENT_COMPONENT, SPECIAL_REVIEW_INDICATOR));
        pdform.getProposalDevelopmentParameters().put(SUMMARY_PRINT_FORMS_INDICATOR, this.getParameterService().getParameter(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, ParameterConstants.DOCUMENT_COMPONENT, SUMMARY_PRINT_FORMS_INDICATOR));
        pdform.getProposalDevelopmentParameters().put(SUMMARY_CHANGE_DATA_INDICATOR, this.getParameterService().getParameter(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, ParameterConstants.DOCUMENT_COMPONENT, Constants.ARIAH_PROPDEV_SUMMARY_CHANGEDATA_PANEL_ENABLED));
        pdform.getProposalDevelopmentParameters().put(CUSTOM_DATA_INFO_INDICATOR, this.getParameterService().getParameter(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, ParameterConstants.DOCUMENT_COMPONENT, CUSTOM_DATA_INFO_INDICATOR));
        pdform.getProposalDevelopmentParameters().put(SUMMARY_QUESTIONS_INDICATOR, this.getParameterService().getParameter(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, ParameterConstants.DOCUMENT_COMPONENT, SUMMARY_QUESTIONS_INDICATOR));
        pdform.getProposalDevelopmentParameters().put(SUMMARY_ATTACHMENTS_INDICATOR, this.getParameterService().getParameter(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, ParameterConstants.DOCUMENT_COMPONENT, SUMMARY_ATTACHMENTS_INDICATOR));
        pdform.getProposalDevelopmentParameters().put(SUMMARY_KEYWORDS_INDICATOR, this.getParameterService().getParameter(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, ParameterConstants.DOCUMENT_COMPONENT, SUMMARY_KEYWORDS_INDICATOR));
        pdform.getProposalDevelopmentParameters().put(SUMMARY_DATA_VALIDATION_INDICATOR, this.getParameterService().getParameter(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, ParameterConstants.DOCUMENT_COMPONENT, SUMMARY_DATA_VALIDATION_INDICATOR));
        pdform.getProposalDevelopmentParameters().put(PROPOSAL_SUMMARY_DISCLAIMER_INDICATOR, this.getParameterService().getParameter(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, ParameterConstants.DOCUMENT_COMPONENT, PROPOSAL_SUMMARY_DISCLAIMER_INDICATOR));
        pdform.getProposalDevelopmentParameters().put(SUMMARY_SPECIAL_REVIEW_LIST, this.getParameterService().getParameter(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, ParameterConstants.DOCUMENT_COMPONENT, SUMMARY_SPECIAL_REVIEW_LIST));
        pdform.getSpecialReviewHelper().populatePropSpecialReviewApproverView(pdform.getProposalDevelopmentParameters().get(SUMMARY_SPECIAL_REVIEW_LIST).getValue());
        return mapping.findForward(Constants.MAPPING_PROPOSAL_APPROVER_PAGE);
    }

    /**
     * Class that encapsulates the workflow for obtaining an reason from an
     * action prompt.
     */
    private class ReasonPrompt {
        
        final String questionId;
        final String questionTextKey;
        final String questionType;
        final String missingReasonKey;
        final String questionCallerMapping;
        final String abortButton;
        final String noteIntroKey;
        
        private class Response {
            
            final String question;
            final ActionForward forward;
            final String reason;
            final String button;
            
            Response(String question, ActionForward forward) {
                this(question, forward, null, null);
            }
            
            Response(String question, String reason, String button) {
                this(question, null, reason, button);
            }
            
            private Response(String question, ActionForward forward, String reason, String button) {
                this.question = question;
                this.forward = forward;
                this.reason = reason;
                this.button = button;
            }
        }

        /**
         * @param questionId the question id/instance,
         * @param questionTextKey application resources key for question text
         * @param questionType the {@link org.kuali.rice.kns.question.Question}
         * question type
         * @param questionCallerMapping mapping of original action
         * @param abortButton button value considered to abort the prompt and
         * return (optional, may be null)
         * @param noteIntroKey application resources key for quesiton text
         * prefix (optional, may be null)
         */
        private ReasonPrompt(String questionId, String questionTextKey, String questionType, String missingReasonKey, String questionCallerMapping, String abortButton, String noteIntroKey) {
            this.questionId = questionId;
            this.questionTextKey = questionTextKey;
            this.questionType = questionType;
            this.questionCallerMapping = questionCallerMapping;
            this.abortButton = abortButton;
            this.noteIntroKey = noteIntroKey;
            this.missingReasonKey = missingReasonKey;
        }

        /**
         * Obtain a validated reason and button value via a Question prompt.
         * Reason is validated against sensitive data patterns, and max Note
         * text length
         *
         * @param mapping Struts mapping
         * @param form Struts form
         * @param request http request
         * @param response http response
         * @return Response object representing *either*: 1) an ActionForward
         * due to error or abort 2) a reason and button clicked
         * @throws Exception
         */
        @SuppressWarnings("deprecation")
        public Response ask(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
            String question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
            String reason = request.getParameter(KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME);
            
            if (StringUtils.isBlank(reason)) {
                String context = request.getParameter(KRADConstants.QUESTION_CONTEXT);
                if (context != null && StringUtils.contains(context, KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME + "=")) {
                    reason = StringUtils.substringAfter(context, KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME + "=");
                }
            }
            
            String disapprovalNoteText = "";

            // start in logic for confirming the disapproval
            if (question == null) {
                // ask question if not already asked
                return new Response(question, performQuestionWithInput(mapping, form, request, response,
                        this.questionId,
                        getKualiConfigurationService().getPropertyValueAsString(this.questionTextKey),
                        this.questionType, this.questionCallerMapping, ""));
            }
            
            String buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
            if (this.questionId.equals(question) && abortButton != null && abortButton.equals(buttonClicked)) {
                // if no button clicked just reload the doc
                return new Response(question, mapping.findForward(RiceConstants.MAPPING_BASIC));
            }

            // have to check length on value entered
            String introNoteMessage = "";
            if (noteIntroKey != null) {
                introNoteMessage = getKualiConfigurationService().getPropertyValueAsString(this.noteIntroKey) + KRADConstants.BLANK_SPACE;
            }

            // build out full message
            disapprovalNoteText = introNoteMessage + reason;

            // check for sensitive data in note
            boolean warnForSensitiveData = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(
                    KRADConstants.KNS_NAMESPACE, ParameterConstants.ALL_COMPONENT,
                    KRADConstants.SystemGroupParameterNames.SENSITIVE_DATA_PATTERNS_WARNING_IND);
            if (warnForSensitiveData) {
                String context = KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME + "=" + reason;
                ActionForward forward = checkAndWarnAboutSensitiveData(mapping, form, request, response,
                        KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME, disapprovalNoteText, this.questionCallerMapping, context);
                if (forward != null) {
                    return new Response(question, forward);
                }
            } else if (KRADUtils.containsSensitiveDataPatternMatch(disapprovalNoteText)) {
                return new Response(question, performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response,
                        this.questionId, getKualiConfigurationService().getPropertyValueAsString(this.questionTextKey),
                        this.questionType, this.questionCallerMapping, "", reason,
                        RiceKeyConstants.ERROR_DOCUMENT_FIELD_CONTAINS_POSSIBLE_SENSITIVE_DATA,
                        KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME, "reason"));
            }
            
            int disapprovalNoteTextLength = disapprovalNoteText.length();

            // get note text max length from DD
            int noteTextMaxLength = getDataDictionaryService().getAttributeMaxLength(Note.class, KRADConstants.NOTE_TEXT_PROPERTY_NAME);
            
            if (StringUtils.isBlank(reason) || (disapprovalNoteTextLength > noteTextMaxLength)) {
                
                if (reason == null) {
                    // prevent a NPE by setting the reason to a blank string
                    reason = "";
                }
                return new Response(question, performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response,
                        this.questionId,
                        getKualiConfigurationService().getPropertyValueAsString(this.questionTextKey),
                        this.questionType, this.questionCallerMapping, "", reason,
                        this.missingReasonKey,
                        KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME, Integer.toString(noteTextMaxLength)));
            }
            
            return new Response(question, disapprovalNoteText, buttonClicked);
        }
    }

    /**
     *
     * @see
     * org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#recall(org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm,
     * javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    @SuppressWarnings("deprecation")
    @Override
    public ActionForward recall(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward;  // the return value

        ReasonPrompt prompt = new ReasonPrompt(KRADConstants.DOCUMENT_RECALL_QUESTION,
                Constants.NON_CANCELLING_RECALL_QUESTION_TEXT_KEY,
                Constants.NON_CANCELLING_RECALL_QUESTION,
                RiceKeyConstants.ERROR_DOCUMENT_RECALL_REASON_REQUIRED,
                KRADConstants.MAPPING_RECALL,
                NonCancellingRecallQuestion.NO,
                RiceKeyConstants.MESSAGE_RECALL_NOTE_TEXT_INTRO);
        ReasonPrompt.Response resp = prompt.ask(mapping, form, request, response);
        
        if (resp.forward != null) {
            // forward either to a fresh display of the question, or to one with "blank reason" error message due to the previous answer, 
            // or back to the document if 'return to document' (abort button) was clicked
            forward = resp.forward;
        } // recall to action only if the button was selected by the user
        else if (KRADConstants.DOCUMENT_RECALL_QUESTION.equals(resp.question) && NonCancellingRecallQuestion.YES.equals(resp.button)) {
            KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
            doProcessingAfterPost(kualiDocumentFormBase, request);
            getDocumentService().recallDocument(kualiDocumentFormBase.getDocument(), resp.reason, false);
            // we should return to the portal to avoid problems with workflow routing changes to the document. 
            //This should eventually return to the holding page, but currently waiting on KCINFR-760.
            forward = mapping.findForward(KRADConstants.MAPPING_PORTAL);
        } else {
            // they chose not to recall so return them back to document
            forward = mapping.findForward(RiceConstants.MAPPING_BASIC);
        }
        
        return forward;
    }

    /**
     * This method allows logic to be executed before a save, after
     * authorization is confirmed.
     *
     * @param mapping the Action Mapping
     * @param form the Action Form
     * @param request the Http Request
     * @param response Http Response
     * @throws Exception if bad happens
     */
    public void preSave(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdForm = (ProposalDevelopmentForm) form;
        if (pdForm.isHidePropDevDocDescriptionPanel()) {
            pdForm.getProposalDevelopmentDocument().defaultDocumentDescription();
        }
    }

    /**
     * Use the Kuali Rule Service to apply the rules for the given event.
     *
     * @param event the event to process
     * @return true if success; false if there was a validation error
     */
    protected final boolean applyRules(KualiDocumentEvent event) {
        return getKualiRuleService().applyRules(event);
    }
    
    protected String getFormProperty(HttpServletRequest request, String methodToCall) {
        String parameterName = (String) request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
        String formProperty = "";
        if (StringUtils.isNotBlank(parameterName)) {
            formProperty = StringUtils.substringBetween(parameterName, "." + methodToCall, ".line");
        }
        return formProperty;
    }
    
    protected KcNotificationService getNotificationService() {
        if (notificationService == null) {
            notificationService = KraServiceLocator.getService(KcNotificationService.class);
        }
        return notificationService;
    }
    
    public void setNotificationService(KcNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * This method print questionnaire answers.
     *
     * @param mapping the Action Mapping
     * @param form the Action Form
     * @param request the Http Request
     * @param response Http Response
     * @return the Action Forward
     * @throws Exception
     */
    public ActionForward printQuestionnaireAnswer(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        ActionForward forward = mapping.findForward(MAPPING_BASIC);
        Map<String, Object> reportParameters = new HashMap<String, Object>();
        
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument document = pdform.getProposalDevelopmentDocument();
        
        final int personIndex = this.getSelectedLine(request);
        ProposalPerson person = document.getDevelopmentProposal().getProposalPerson(personIndex);
        ProposalPersonQuestionnaireHelper helper = new ProposalPersonQuestionnaireHelper(pdform, person);
        AnswerHeader header = helper.getAnswerHeaders().get(0);
        
        reportParameters.put("questionnaireId", header.getQuestionnaire().getQuestionnaireIdAsInteger());
        reportParameters.put("template", header.getQuestionnaire().getTemplate());
        
        AttachmentDataSource dataStream = KraServiceLocator.getService(QuestionnairePrintingService.class).printQuestionnaireAnswer(person, reportParameters);
        if (dataStream.getContent() != null) {
            streamToResponse(dataStream, response);
            forward = null;
        }
        
        return forward;
    }

    /**
     * This method print all questionnaire answers.
     *
     * @param mapping the Action Mapping
     * @param form the Action Form
     * @param request the Http Request
     * @param response Http Response
     * @return the Action Forward
     * @throws Exception
     */
    public ActionForward printAllQuestionnaireAnswer(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        ActionForward forward = mapping.findForward(MAPPING_BASIC);
        Map<String, Object> reportParameters = new HashMap<String, Object>();
        List<Printable> printables = new ArrayList<Printable>();
        
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument document = pdform.getProposalDevelopmentDocument();
        
        for (ProposalPerson person : document.getDevelopmentProposal().getProposalPersons()) {
            
            ProposalPersonQuestionnaireHelper helper = new ProposalPersonQuestionnaireHelper(pdform, person);
            AnswerHeader header = helper.getAnswerHeaders().get(0);
            reportParameters.put("questionnaireId", header.getQuestionnaire().getQuestionnaireIdAsInteger());
            reportParameters.put("template", header.getQuestionnaire().getTemplate());
            AbstractPrint printable = KraServiceLocator.getService(QuestionnairePrint.class);
            if (printable != null) {
                printable.setPrintableBusinessObject(person);
                printable.setReportParameters(reportParameters);
            }
            printables.add(printable);
        }
        AttachmentDataSource dataStream = KraServiceLocator.getService(PrintingService.class).print(printables);
        if (dataStream.getContent() != null) {
            streamToResponse(dataStream, response);
            forward = null;
        }
        
        return forward;
    }

    /**
     *
     * @return
     */
    public S2SBudgetCalculatorService getS2SBudgetCalculatorService() {
        return KraServiceLocator.getService(S2SBudgetCalculatorService.class);
    }

    /**
     * Quick method to get the RoleService
     *
     * @return RoleService reference
     */
    private RoleService getRoleService() {
        return KraServiceLocator.getService(RoleService.class);
    }
}

class S2sOppFormsComparator1 implements Comparator<S2sOppForms> {
    
    public int compare(S2sOppForms s2sOppForms1, S2sOppForms s2sOppForms2) {
        if (s2sOppForms2.getAvailable() && s2sOppForms1.getAvailable()) {
            return 1;
        }
        return s2sOppForms2.getAvailable().compareTo(s2sOppForms1.getAvailable());
    }
}

class S2sOppFormsComparator2 implements Comparator<S2sOppForms> {
    
    public int compare(S2sOppForms s2sOppForms1, S2sOppForms s2sOppForms2) {
        return s2sOppForms2.getMandatory().compareTo(s2sOppForms1.getMandatory());
    }
    
}

class S2sOppFormsComparator3 implements Comparator<S2sOppForms> {
    
    public int compare(S2sOppForms s2sOppForms1, S2sOppForms s2sOppForms2) {
        FormMappingInfo info1 = new FormMappingLoader().getFormInfo(s2sOppForms1.getOppNameSpace());
        FormMappingInfo info2 = new FormMappingLoader().getFormInfo(s2sOppForms2.getOppNameSpace());
        if (info1 != null && info2 != null) {
            Integer sortIndex1 = info1.getSortIndex();
            Integer sortIndex2 = info2.getSortIndex();
            return sortIndex1.compareTo(sortIndex2);
        }
        return 1;
    }
}
