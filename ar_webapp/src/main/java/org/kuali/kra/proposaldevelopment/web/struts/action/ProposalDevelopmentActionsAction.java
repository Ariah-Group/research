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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kra.bo.FundingSourceType;
import org.kuali.kra.bo.SpecialReviewType;
import org.kuali.kra.bo.SponsorFormTemplate;
import org.kuali.kra.bo.SponsorFormTemplateList;
import org.kuali.kra.budget.core.Budget;
import org.kuali.kra.budget.document.BudgetDocument;
import org.kuali.kra.budget.versions.BudgetDocumentVersion;
import org.kuali.kra.budget.versions.BudgetVersionOverview;
import org.kuali.kra.common.specialreview.rule.event.SaveSpecialReviewLinkEvent;
import org.kuali.kra.common.specialreview.service.SpecialReviewService;
import org.kuali.kra.common.web.struts.form.ReportHelperBean;
import org.kuali.kra.common.web.struts.form.ReportHelperBeanContainer;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.kra.institutionalproposal.InstitutionalProposalConstants;
import org.kuali.kra.institutionalproposal.document.authorization.InstitutionalProposalDocumentAuthorizer;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;
import org.kuali.kra.institutionalproposal.proposaladmindetails.ProposalAdminDetails;
import org.kuali.kra.institutionalproposal.service.InstitutionalProposalService;
import org.kuali.kra.institutionalproposal.specialreview.InstitutionalProposalSpecialReview;
import org.kuali.kra.printing.service.CurrentAndPendingReportService;
import org.kuali.kra.proposaldevelopment.bo.*;
import org.kuali.kra.proposaldevelopment.budget.bo.BudgetChangedData;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.proposaldevelopment.hierarchy.ProposalHierarchyKeyConstants;
import org.kuali.kra.proposaldevelopment.hierarchy.service.ProposalHierarchyService;
import org.kuali.kra.proposaldevelopment.notification.ProposalDevelopmentNotificationContext;
import org.kuali.kra.proposaldevelopment.notification.ProposalDevelopmentNotificationRenderer;
import org.kuali.kra.proposaldevelopment.printing.service.ProposalDevelopmentPrintingService;
import org.kuali.kra.proposaldevelopment.rule.event.BudgetDataOverrideEvent;
import org.kuali.kra.proposaldevelopment.rule.event.CopyProposalEvent;
import org.kuali.kra.proposaldevelopment.rule.event.ProposalDataOverrideEvent;
import org.kuali.kra.proposaldevelopment.rules.ProposalDevelopmentRejectionRule;
import org.kuali.kra.proposaldevelopment.rules.ProposalAttachmentSubmitToSponsorRule;
import org.kuali.kra.proposaldevelopment.service.ProposalCopyService;
import org.kuali.kra.proposaldevelopment.service.ProposalDevelopmentService;
import org.kuali.kra.proposaldevelopment.service.ProposalStateService;
import org.kuali.kra.proposaldevelopment.specialreview.ProposalSpecialReview;
import org.kuali.kra.proposaldevelopment.web.bean.ProposalDevelopmentRejectionBean;
import org.kuali.kra.proposaldevelopment.web.struts.form.ProposalDevelopmentForm;
import org.kuali.kra.s2s.S2SException;
import org.kuali.kra.s2s.bo.S2sAppSubmission;
import org.kuali.kra.s2s.bo.S2sSubmissionType;
import org.kuali.kra.s2s.service.PrintService;
import org.kuali.kra.s2s.service.S2SService;
import org.kuali.kra.service.KraPersistenceStructureService;
import org.kuali.kra.service.KraWorkflowService;
import org.kuali.kra.web.struts.action.AuditActionHelper;
import org.kuali.kra.web.struts.action.StrutsConfirmation;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.RoutingReportCriteria;
import org.kuali.rice.kew.api.action.WorkflowDocumentActionsService;
import org.kuali.rice.kew.api.document.DocumentDetail;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.util.AuditCluster;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.action.AuditModeAction;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.service.*;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import org.ariahgroup.research.bo.AttachmentDataSource;
import org.ariahgroup.research.service.UnitService;
import org.kuali.kra.common.notification.bo.NotificationType;

import static org.kuali.kra.infrastructure.KraServiceLocator.getService;
import static org.kuali.rice.krad.util.KRADConstants.CONFIRMATION_QUESTION;

/**
 * Handles all of the actions from the Proposal Development Actions web page.
 *
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class ProposalDevelopmentActionsAction extends ProposalDevelopmentAction implements AuditModeAction {

    private static final Log LOG = LogFactory.getLog(ProposalDevelopmentActionsAction.class);
    private static final String DOCUMENT_APPROVE_QUESTION = "DocApprove";
    private static final String DOCUMENT_ROUTE_QUESTION = "DocRoute";
    private static final String DOCUMENT_DELETE_QUESTION = "ProposalDocDelete";
    BusinessObjectService businessObjectService;

    private static final String CONFIRM_SUBMISSION_WITH_WARNINGS_KEY = "submitApplication";
    private static final String EMPTY_STRING = "";
    private static final String SUPER_USER_ACTION_REQUESTS = "superUserActionRequests";

    private static final int OK = 0;
    private static final int WARNING = 1;
    private static final int ERROR = 2;
    /**
     * Struts mapping for the Proposal web page.
     */
    private static final String MAPPING_PROPOSAL = "proposal";
    private static final String ROUTING_WARNING_MESSAGE = "Validation Warning Exists.Are you sure want to submit to workflow routing.";

    private UnitService unitSvc;

    private enum SuperUserAction {

        SUPER_USER_APPROVE, TAKE_SUPER_USER_ACTIONS
    }

    private transient KraWorkflowService kraWorkflowService;

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward originalExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward actionForward = super.execute(mapping, form, request, response);
        if (!StringUtils.equals((String) request.getAttribute("methodToCallAttribute"), "methodToCall.reload")) {
            this.populateSponsorForms(form);
        }
        return actionForward;
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String methodToCall = (String) request.getAttribute("methodToCallAttribute");

        if (methodToCall != null && (methodToCall.contains("methodToCall.route")
                || methodToCall.contains("methodToCall.blanketApprove")
                || methodToCall.contains("methodToCall.approve"))) {

            // present certification question if it exists as a system parameter
            String questionText = getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT,
                    Constants.PARAMETER_COMPONENT_DOCUMENT,
                    Constants.ARIAH_PROPDEV_TEXT_APPROVAL_CERT_QUESTION);

            boolean showQuestion = getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT,
                    Constants.PARAMETER_COMPONENT_DOCUMENT,
                    Constants.ARIAH_PROPDEV_DISPLAY_APPROVAL_CERT_QUESTION, false);

            ProposalDevelopmentRejectionBean rejectionbean = ((ProposalDevelopmentForm) form).getProposalDevelopmentRejectionBean();

            if (StringUtils.isEmpty(questionText) || !showQuestion || !StringUtils.isEmpty(rejectionbean.getRejectReason())) {
                return originalExecute(mapping, form, request, response);
            } else {
                StrutsConfirmation question = new StrutsConfirmation();
                question.setQuestionText(questionText);
                question.setMapping(mapping);
                question.setForm(form);
                question.setRequest(request);
                question.setResponse(response);
                question.setQuestionId(Constants.ARIAH_PROPDEV_TEXT_APPROVAL_CERT_QUESTION);
                question.setQuestionType(CONFIRMATION_QUESTION);

                return confirm(question, "originalExecute", "originalExecute");
            }
        } else {
            return originalExecute(mapping, form, request, response);
        }
    }

    /**
     * Calls the document service to approve the document
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        WorkflowDocument workflowDoc = proposalDevelopmentForm.getProposalDevelopmentDocument().getDocumentHeader().getWorkflowDocument();

        proposalDevelopmentForm.setAuditActivated(true);
        ActionForward forward = mapping.findForward(Constants.MAPPING_BASIC);
        boolean forwardToSubmitToSponsor = false;
        int status = isValidSubmission(proposalDevelopmentForm);

        if (status == ERROR) {
            GlobalVariables.getMessageMap().clearErrorMessages(); // clear error from isValidSubmission()    
            GlobalVariables.getMessageMap().putError("datavalidation", KeyConstants.ERROR_WORKFLOW_SUBMISSION, new String[]{});

            forward = mapping.findForward((Constants.MAPPING_BASIC));
        } else {

            if (canGenerateRequestsInFuture(workflowDoc, GlobalVariables.getUserSession().getPrincipalId())) {
                forward = promptUserForInput(workflowDoc, "approve", mapping, form, request, response);
            } else {
                forward = super.approve(mapping, form, request, response);
            }

            if (proposalDevelopmentForm.getEditingMode().containsKey("submitToSponsor")
                    && getParameterService().getParameterValueAsBoolean(ProposalDevelopmentDocument.class, "autoSubmitToSponsorOnFinalApproval")
                    && getKraWorkflowService().isFinalApproval(workflowDoc)) {
                forwardToSubmitToSponsor = true;
            }

            ProposalDevelopmentDocument proposalDevelopmentDocument = ((ProposalDevelopmentForm) form).getProposalDevelopmentDocument();
            if (autogenerateInstitutionalProposal() && proposalDevelopmentDocument.getInstitutionalProposalNumber() != null) {

                String proposalTypeCodeRevision
                        = getParameterService().getParameterValueAsString(ProposalDevelopmentDocument.class, KeyConstants.PROPOSALDEVELOPMENT_PROPOSALTYPE_REVISION);

                if (proposalTypeCodeRevision.equals(proposalDevelopmentDocument.getDevelopmentProposal().getProposalTypeCode())) {
                    KNSGlobalVariables.getMessageList().add(KeyConstants.MESSAGE_INSTITUTIONAL_PROPOSAL_VERSIONED,
                            proposalDevelopmentDocument.getInstitutionalProposalNumber());
                } else {
                    String proposalNumber = proposalDevelopmentDocument.getInstitutionalProposalNumber();
                    KNSGlobalVariables.getMessageList().add(KeyConstants.MESSAGE_INSTITUTIONAL_PROPOSAL_CREATED, proposalNumber);
                }
            }
        }

        List<ActionForward> acceptedForwards = new ArrayList<ActionForward>();
        String routeHeaderId = ((ProposalDevelopmentForm) form).getDocument().getDocumentNumber();
        String returnLocation = buildActionUrl(routeHeaderId, Constants.MAPPING_PROPOSAL_ACTIONS, "ProposalDevelopmentDocument");
        if (forwardToSubmitToSponsor) {
            returnLocation = returnLocation.replace("proposalDevelopmentProposal", "proposalDevelopmentActions");
            returnLocation = returnLocation.replace("docHandler", "autoSubmitToSponsor");
            String workflowBase = getKualiConfigurationService().getPropertyValueAsString(
                    KRADConstants.WORKFLOW_URL_KEY);
            String actionListUrl = workflowBase + "/ActionList.do";
            ActionForward actionListForward = new ActionForward(actionListUrl, true);

            acceptedForwards.add(actionListForward);
            String command = proposalDevelopmentForm.getCommand();
            if (KewApiConstants.ACTIONLIST_COMMAND.equals(command)) {
                forward = actionListForward;
            }
        }
        acceptedForwards.add(mapping.findForward(KRADConstants.MAPPING_PORTAL));
        ActionForward holdingPageForward = mapping.findForward(Constants.MAPPING_HOLDING_PAGE);
        return routeToHoldingPage(acceptedForwards, forward, holdingPageForward, returnLocation);
    }

    private ActionForward promptUserForInput(WorkflowDocument workflowDoc, String action, ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
        Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
        String methodToCall = ((KualiForm) form).getMethodToCall();

        if (!DOCUMENT_APPROVE_QUESTION.equals(question)) {
            // ask question if not already asked
            return this.performQuestionWithoutInput(mapping, form, request, response, DOCUMENT_APPROVE_QUESTION, CoreApiServiceLocator
                    .getKualiConfigurationService().getPropertyValueAsString(KeyConstants.QUESTION_APPROVE_FUTURE_REQUESTS),
                    KRADConstants.CONFIRMATION_QUESTION, methodToCall, "");
        } else if (DOCUMENT_APPROVE_QUESTION.equals(question) && ConfirmationQuestion.NO.equals(buttonClicked)) {
            workflowDoc.setDoNotReceiveFutureRequests();
        } else if (DOCUMENT_APPROVE_QUESTION.equals(question)) {
            workflowDoc.setReceiveFutureRequests();
        }

        if (StringUtils.equals(action, "approve")) {
            return super.approve(mapping, form, request, response);
        } else if (StringUtils.equals(action, "route")) {
            return super.route(mapping, form, request, response);
        } else {
            throw new UnsupportedOperationException(String.format("promptUserForInput does not know how to forward for action %s.", action));
        }
    }

    private boolean canGenerateRequestsInFuture(WorkflowDocument workflowDoc, String principalId) throws Exception {
        RoutingReportCriteria.Builder reportCriteriaBuilder = RoutingReportCriteria.Builder.createByDocumentId(workflowDoc.getDocumentId());
        reportCriteriaBuilder.setTargetPrincipalIds(Collections.singletonList(principalId));

        org.kuali.rice.krad.workflow.service.WorkflowDocumentService workflowDocumentService = KRADServiceLocatorWeb.getService("workflowDocumentService");
        String currentRouteNodeNames = workflowDocumentService.getCurrentRouteNodeNames(workflowDoc);

        return (hasAskedToNotReceiveFutureRequests(workflowDoc, principalId) && canGenerateMultipleApprovalRequests(reportCriteriaBuilder.build(), principalId, currentRouteNodeNames));
    }

    private boolean hasAskedToNotReceiveFutureRequests(WorkflowDocument workflowDoc, String principalId) {
        boolean receiveFutureRequests = false;
        boolean doNotReceiveFutureRequests = false;

        Map<String, String> variables = workflowDoc.getVariables();
        if (variables != null && CollectionUtils.isNotEmpty(variables.keySet())) {
            Iterator<String> variableIterator = variables.keySet().iterator();
            while (variableIterator.hasNext()) {
                String variableKey = variableIterator.next();
                String variableValue = variables.get(variableKey);
                if (variableKey.startsWith(KewApiConstants.RECEIVE_FUTURE_REQUESTS_BRANCH_STATE_KEY)
                        && variableValue.toUpperCase().equals(KewApiConstants.RECEIVE_FUTURE_REQUESTS_BRANCH_STATE_VALUE)
                        && variableKey.contains(principalId)) {
                    receiveFutureRequests = true;
                    break;
                } else if (variableKey.startsWith(KewApiConstants.RECEIVE_FUTURE_REQUESTS_BRANCH_STATE_KEY)
                        && variableValue.toUpperCase().equals(KewApiConstants.DONT_RECEIVE_FUTURE_REQUESTS_BRANCH_STATE_VALUE)
                        && variableKey.contains(principalId)) {
                    doNotReceiveFutureRequests = true;
                    break;
                }
            }
        }

        return (receiveFutureRequests == false && doNotReceiveFutureRequests == false);
    }

    private boolean canGenerateMultipleApprovalRequests(RoutingReportCriteria reportCriteria, String loggedInPrincipalId, String currentRouteNodeNames) throws Exception {
        int approvalRequestsCount = 0;
        WorkflowDocumentActionsService info = GlobalResourceLoader.getService("rice.kew.workflowDocumentActionsService");

        DocumentDetail results1 = info.executeSimulation(reportCriteria);
        for (ActionRequest actionRequest : results1.getActionRequests()) {
            //!actionRequest.isRoleRequest() &&  removed from condition for 
            if (actionRequest.isPending() && actionRequest.getActionRequested().getCode().equalsIgnoreCase(KewApiConstants.ACTION_REQUEST_APPROVE_REQ)
                    && recipientMatchesUser(actionRequest, loggedInPrincipalId) && !StringUtils.contains(currentRouteNodeNames, actionRequest.getNodeName())) {
                approvalRequestsCount += 1;
            }
        }

        return (approvalRequestsCount > 0);
    }

    private boolean recipientMatchesUser(ActionRequest actionRequest, String loggedInPrincipalId) {
        if (actionRequest != null && loggedInPrincipalId != null) {
            List<ActionRequest> actionRequests = Collections.singletonList(actionRequest);
            if (actionRequest.isRoleRequest()) {
                actionRequests = actionRequest.getChildRequests();
            }
            for (ActionRequest cActionRequest : actionRequests) {
                String recipientUser = cActionRequest.getPrincipalId();
                if ((recipientUser != null && recipientUser.equals(loggedInPrincipalId))
                        || (StringUtils.isNotBlank(cActionRequest.getGroupId())
                        && getGroupService().isMemberOfGroup(loggedInPrincipalId, cActionRequest.getGroupId()))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     *
     * @return
     */
    @Override
    protected GroupService getGroupService() {
        return KimApiServiceLocator.getGroupService();
    }

    /**
     * Updates an Editable Proposal Field and adds it to the Proposal Change
     * History
     *
     * @param mapping the Strut's Action Mapping.
     * @param form the Proposal Development Form.
     * @param request the HTTP request.
     * @param response the HTTP response
     * @return the next web page to display
     * @throws Exception
     */
    public ActionForward addProposalChangedData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        BusinessObjectService boService = KraServiceLocator.getService(BusinessObjectService.class);
        KraPersistenceStructureService kraPersistenceStructureService = KraServiceLocator.getService(KraPersistenceStructureService.class);

        ActionForward forward = mapping.findForward("basic");
        ProposalDevelopmentForm pdForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument pdDocument = pdForm.getProposalDevelopmentDocument();
        ProposalChangedData newProposalChangedData = pdForm.getNewProposalChangedData();

        newProposalChangedData.setProposalNumber(pdDocument.getDevelopmentProposal().getProposalNumber());
        newProposalChangedData.setChangeNumber(getNextChangeNumber(boService, newProposalChangedData.getProposalNumber(), newProposalChangedData.getColumnName()));
        if (StringUtils.isEmpty(newProposalChangedData.getDisplayValue()) && StringUtils.isNotEmpty(newProposalChangedData.getChangedValue())) {
            newProposalChangedData.setDisplayValue(newProposalChangedData.getChangedValue());
        }

        String tmpLookupReturnPk = "";
        if (newProposalChangedData.getEditableColumn() != null) {
            tmpLookupReturnPk = newProposalChangedData.getEditableColumn().getLookupPkReturn();
        }

        newProposalChangedData.refreshReferenceObject("editableColumn");
        newProposalChangedData.getEditableColumn().setLookupPkReturn(tmpLookupReturnPk);

        if (newProposalChangedData.getEditableColumn() != null) {
            if (!newProposalChangedData.getEditableColumn().getHasLookup()) {
                newProposalChangedData.setDisplayValue(newProposalChangedData.getChangedValue());
            }
        }

        if (getKualiRuleService().applyRules(new ProposalDataOverrideEvent(pdForm.getProposalDevelopmentDocument(), newProposalChangedData))) {
            boService.save(newProposalChangedData);

            ProposalOverview proposalWrapper = createProposalWrapper(pdDocument);
            Map<String, String> columnToAttributesMap = kraPersistenceStructureService.getDBColumnToObjectAttributeMap(ProposalOverview.class);
            String proposalAttributeToPersist = columnToAttributesMap.get(newProposalChangedData.getColumnName());
            ObjectUtils.setObjectProperty(proposalWrapper, proposalAttributeToPersist, newProposalChangedData.getChangedValue());
            ObjectUtils.setObjectProperty(pdDocument.getDevelopmentProposal(), proposalAttributeToPersist, newProposalChangedData.getChangedValue());

            boService.save(proposalWrapper);
            pdForm.getProposalDevelopmentDocument().setVersionNumber(proposalWrapper.getVersionNumber());

            pdForm.setNewProposalChangedData(new ProposalChangedData());
            growProposalChangedHistory(pdDocument, newProposalChangedData);

            ProposalDevelopmentNotificationContext context
                    = new ProposalDevelopmentNotificationContext(pdDocument.getDevelopmentProposal(), "103", "Proposal Data Override");
            ((ProposalDevelopmentNotificationRenderer) context.getRenderer()).setProposalChangedData(newProposalChangedData);
            if (pdForm.getNotificationHelper().getPromptUserForNotificationEditor(context)) {
                pdForm.getNotificationHelper().initializeDefaultValues(context);
                forward = mapping.findForward("notificationEditor");
            } else {
                getNotificationService().sendNotification(context);
            }

        }

        return forward;
    }

    private void growProposalChangedHistory(ProposalDevelopmentDocument pdDocument, ProposalChangedData newProposalChangedData) {
        Map<String, List<ProposalChangedData>> changeHistory = pdDocument.getDevelopmentProposal().getProposalChangeHistory();
        if (changeHistory.get(newProposalChangedData.getEditableColumn().getColumnLabel()) == null) {
            changeHistory.put(newProposalChangedData.getEditableColumn().getColumnLabel(), new ArrayList<ProposalChangedData>());
        }

        changeHistory.get(newProposalChangedData.getEditableColumn().getColumnLabel()).add(0, newProposalChangedData);
    }

    private ProposalOverview createProposalWrapper(ProposalDevelopmentDocument pdDocument) throws Exception {
        ProposalOverview proposalWrapper = new ProposalOverview();
        PersistenceStructureService persistentStructureService = KraServiceLocator.getService(PersistenceStructureService.class);
        List<String> fieldsToUpdate = (List<String>) persistentStructureService.listFieldNames(ProposalOverview.class);
        for (String field : fieldsToUpdate) {
            boolean noSuchFieldPD = false;
            boolean noSuchFieldBO = false;
            Object tempVal = null;

            try {
                tempVal = ObjectUtils.getPropertyValue(pdDocument, field);
            } catch (Exception e) {
                noSuchFieldPD = true;
            }

            try {
                tempVal = ObjectUtils.getPropertyValue(pdDocument.getDevelopmentProposal(), field);
            } catch (Exception e) {
                noSuchFieldBO = true;
            }

            if (tempVal == null && noSuchFieldPD && noSuchFieldBO) {
                LOG.warn("Could not find property " + field + " in ProposalDevelopmentDocument or DevelopmnentProposal bo.");
            }

            ObjectUtils.setObjectProperty(proposalWrapper, field, (tempVal != null) ? tempVal : null);
        }
        return proposalWrapper;
    }

    private int getNextChangeNumber(BusinessObjectService boService, String proposalNumber, String columnName) {
        int changeNumber = 0;
        Map<String, Object> keyMap = new HashMap<String, Object>();
        keyMap.put("proposalNumber", proposalNumber);
        keyMap.put("columnName", columnName);
        List<ProposalChangedData> changedDataList = (List<ProposalChangedData>) boService.findMatchingOrderBy(ProposalChangedData.class, keyMap, "changeNumber", true);
        if (CollectionUtils.isNotEmpty(changedDataList)) {
            changeNumber = ((ProposalChangedData) changedDataList.get(changedDataList.size() - 1)).getChangeNumber();
        }

        return ++changeNumber;
    }

    /**
     * Copies a Proposal Development Document based upon user-specified
     * criteria.
     *
     * @param mapping the Strut's Action Mapping.
     * @param form the Proposal Development Form.
     * @param request the HTTP request.
     * @param response the HTTP response
     * @return the next web page to display
     * @throws Exception
     */
    public ActionForward copyProposal(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ActionForward nextWebPage = null;
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument doc = proposalDevelopmentForm.getProposalDevelopmentDocument();
        ProposalCopyCriteria criteria = proposalDevelopmentForm.getCopyCriteria();

        // check any business rules
        boolean rulePassed = getKualiRuleService().applyRules(new CopyProposalEvent(doc, criteria));

        if (!rulePassed) {
            nextWebPage = mapping.findForward(Constants.MAPPING_BASIC);
        } else {
            // Use the Copy Service to copy the proposal.

            ProposalCopyService proposalCopyService = (ProposalCopyService) KraServiceLocator.getService("proposalCopyService");

            if (proposalCopyService == null) {

                // Something bad happened. The errors are in the Global Error Map
                // which will be displayed to the user.
                nextWebPage = mapping.findForward(Constants.MAPPING_BASIC);
            } else {
                Map<String, Object> keyMap = new HashMap<String, Object>();
                if (proposalDevelopmentForm.getProposalDevelopmentDocument() != null) {
                    for (DevelopmentProposal proposalDevelopmentFormObject : proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposalList()) {
                        keyMap.put("proposalNumber", proposalDevelopmentFormObject.getProposalNumber());
                    }
                }
                businessObjectService = KraServiceLocator.getService(BusinessObjectService.class);
                List<S2sAppSubmission> s2sAppSubmissionProposalList = (List<S2sAppSubmission>) businessObjectService.findMatching(S2sAppSubmission.class, keyMap);

                String newDocId = proposalCopyService.copyProposal(doc, criteria);
                KraServiceLocator.getService(PessimisticLockService.class).releaseAllLocksForUser(doc.getPessimisticLocks(), GlobalVariables.getUserSession().getPerson());

                // Switch over to the new proposal development document and
                // go to the Proposal web page.
                proposalDevelopmentForm.setDocId(newDocId);
                this.loadDocument(proposalDevelopmentForm);

                ProposalDevelopmentDocument copiedDocument = proposalDevelopmentForm.getProposalDevelopmentDocument();
                initializeProposalUsers(copiedDocument);//add in any default permissions
                copiedDocument.getDevelopmentProposal().setS2sAppSubmission(new ArrayList<S2sAppSubmission>());
                for (S2sAppSubmission s2sAppSubmissionListValue : s2sAppSubmissionProposalList) {
                    copiedDocument.getDevelopmentProposal().setPrevGrantsGovTrackingID(s2sAppSubmissionListValue.getGgTrackingId());
                }

                DocumentService docService = KraServiceLocator.getService(DocumentService.class);
                docService.saveDocument(copiedDocument);

                nextWebPage = mapping.findForward(MAPPING_PROPOSAL);

                // Helper method to clear document form data.
                proposalDevelopmentForm.clearDocumentRelatedState();
                proposalDevelopmentForm.setViewOnly(false);

            }
        }

        return nextWebPage;
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public ActionForward activate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        proposalDevelopmentForm.setUnitRulesMessages(getUnitRulesMessages(proposalDevelopmentForm.getProposalDevelopmentDocument()));
        return new AuditActionHelper().setAuditMode(mapping, (ProposalDevelopmentForm) form, true);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public ActionForward deactivate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ((ProposalDevelopmentForm) form).clearUnitRulesMessages();
        return new AuditActionHelper().setAuditMode(mapping, (ProposalDevelopmentForm) form, false);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward reject(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        //System.err.println("******************* Got to the reject action **************************");
        // KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;

        //Object question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
        //Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
        //String reason = request.getParameter(KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        // String methodToCall = ((KualiForm) form).getMethodToCall();
        //if (question == null) {
        //  System.err.println("               question null");
        ((ProposalDevelopmentForm) form).setShowRejectionConfirmation(true);
        return mapping.findForward(Constants.MAPPING_BASIC);
        //return this.performQuestionWithInput(mapping, form, request, response, DOCUMENT_REJECT_QUESTION,
        //      "Are you sure you want to reject this document?", KRADConstants.CONFIRMATION_QUESTION, methodToCall, "");
        //}
        //else if ((DOCUMENT_REJECT_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
        //  System.err.println("               question answered NO");
        //return mapping.findForward(Constants.MAPPING_BASIC);
        //}
        //else {
        //  System.err.println("               question answered YES");
        // reject the document using the service.
        //ProposalDevelopmentDocument pDoc = (ProposalDevelopmentDocument) kualiDocumentFormBase.getDocument();

        //ProposalDevelopmentRejectionBean bean = ((ProposalDevelopmentForm)form).getProposalDevelopmentRejectionBean();
        //ProposalHierarchyService phService = KraServiceLocator.getService(ProposalHierarchyService.class);
        //phService.rejectProposalDevelopmentDocument(pDoc.getDevelopmentProposal().getProposalNumber(), bean.getRejectReason(), GlobalVariables
        //      .getUserSession().getPrincipalId());
        //return super.returnToSender(request, mapping, kualiDocumentFormBase);
        //}
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward rejectNo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ((ProposalDevelopmentForm) form).setShowRejectionConfirmation(false);
        ((ProposalDevelopmentForm) form).setProposalDevelopmentRejectionBean(new ProposalDevelopmentRejectionBean());
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward rejectYes(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        ProposalDevelopmentDocument pDoc = (ProposalDevelopmentDocument) kualiDocumentFormBase.getDocument();
        ProposalDevelopmentRejectionBean bean = ((ProposalDevelopmentForm) form).getProposalDevelopmentRejectionBean();

        if (new ProposalDevelopmentRejectionRule().proccessProposalDevelopmentRejection(bean)) {
            // reject the document using the service.
            ProposalHierarchyService phService = KraServiceLocator.getService(ProposalHierarchyService.class);
            phService.rejectProposalDevelopmentDocument(pDoc.getDevelopmentProposal().getProposalNumber(), bean.getRejectReason(), GlobalVariables
                    .getUserSession().getPrincipalId(), bean.getRejectFile());
            ((ProposalDevelopmentForm) form).setShowRejectionConfirmation(false);
            return super.returnToSender(request, mapping, kualiDocumentFormBase);
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * route the document using the document service
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //boolean success;

        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument pdDoc = proposalDevelopmentForm.getProposalDevelopmentDocument();

        proposalDevelopmentForm.setAuditActivated(true);
        //success=KraServiceLocator.getService(KualiRuleService.class).applyRules(new DocumentAuditEvent(proposalDevelopmentForm.getProposalDevelopmentDocument()));
        //HashMap map=KNSGlobalVariables.getAuditErrorMap();
        Object question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
        Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
        String methodToCall = ((KualiForm) form).getMethodToCall();

        ActionForward forward = mapping.findForward(Constants.MAPPING_BASIC);
        int status = isValidSubmission(proposalDevelopmentForm);
        boolean userSaysOk = false;

        //if((map.size()==1) &&  map.containsKey("sponsorProgramInformationAuditWarnings"))
        if (status == WARNING) {

            if (status == WARNING && question == null) {
                forward = this.performQuestionWithoutInput(mapping, form, request, response, DOCUMENT_ROUTE_QUESTION, "Validation Warning Exists. Are you sure want to submit to workflow routing?", KRADConstants.CONFIRMATION_QUESTION, methodToCall, "");
            } else if (DOCUMENT_ROUTE_QUESTION.equals(question) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                //status is OK now since the user said it was :)
                userSaysOk = true;
            } else if (DOCUMENT_APPROVE_QUESTION.equals(question)) {
                //user said ok in the past since we got to this question.
                userSaysOk = true;
            }

        }

        if (status == OK || userSaysOk) {
            WorkflowDocument workflowDoc = proposalDevelopmentForm.getProposalDevelopmentDocument().getDocumentHeader().getWorkflowDocument();
            if (canGenerateRequestsInFuture(workflowDoc, GlobalVariables.getUserSession().getPrincipalId())) {
                forward = promptUserForInput(workflowDoc, "route", mapping, form, request, response);
            } else {
                forward = super.route(mapping, proposalDevelopmentForm, request, response);
            }
        } else if (status == WARNING) {

        } else {
            GlobalVariables.getMessageMap().clearErrorMessages(); // clear error from isValidSubmission()    
            GlobalVariables.getMessageMap().putError("datavalidation", KeyConstants.ERROR_WORKFLOW_SUBMISSION, new String[]{});

            if ((pdDoc.getDevelopmentProposal().getContinuedFrom() != null) && isNewProposalType(pdDoc) && isSubmissionApplication(pdDoc)) {
                GlobalVariables.getMessageMap().putError("someKey", KeyConstants.ERROR_RESUBMISSION_INVALID_PROPOSALTYPE_SUBMISSIONTYPE);
            }

            forward = mapping.findForward((Constants.MAPPING_BASIC));
        }

        String routeHeaderId = proposalDevelopmentForm.getProposalDevelopmentDocument().getDocumentNumber();
        String returnLocation = buildActionUrl(routeHeaderId, Constants.MAPPING_PROPOSAL_ACTIONS, "ProposalDevelopmentDocument");

        ActionForward basicForward = mapping.findForward(KRADConstants.MAPPING_PORTAL);
        ActionForward holdingPageForward = mapping.findForward(Constants.MAPPING_HOLDING_PAGE);
        return routeToHoldingPage(basicForward, forward, holdingPageForward, returnLocation);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward autoSubmitToSponsor(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;

        //used when auto submitting to sponsor on final approval
        if (proposalDevelopmentForm.getProposalDevelopmentDocument().getDocumentNumber() == null) {
            // If entering this action from copy link on doc search
            loadDocumentInForm(request, proposalDevelopmentForm);
            loadDocument(proposalDevelopmentForm.getProposalDevelopmentDocument());
        }

        return submitToSponsor(mapping, form, request, response);
    }

    /**
     * Submit a proposal to a sponsor.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward submitToSponsor(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument proposalDevelopmentDocument = (ProposalDevelopmentDocument) proposalDevelopmentForm.getProposalDevelopmentDocument();

        if (!userCanCreateInstitutionalProposal()) {
            return mapping.findForward(Constants.MAPPING_BASIC);
        }

        proposalDevelopmentForm.setAuditActivated(true);
        ActionForward forward = mapping.findForward(Constants.MAPPING_BASIC);
        //cannot be run along side audit rules in ProposalDevelopmentDocumentRule::processRunAuditBusinessRules.
        boolean valid = new ProposalAttachmentSubmitToSponsorRule().processRunAuditBusinessRules(proposalDevelopmentDocument);

        int status = isValidSubmission(proposalDevelopmentForm);
        if (!valid || status == ERROR) {
            putErrorInGlobalMessageMap();
            return mapping.findForward(Constants.MAPPING_BASIC);
        }

        Object question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
        Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);

        if ((question == null || buttonClicked == null) && status == WARNING && proposalDevelopmentForm.getResubmissionOption() == null) {
            StrutsConfirmation qst = buildSubmitToGrantsGovWithWarningsQuestion(mapping, form, request, response);
            forward = confirm(qst, CONFIRM_SUBMISSION_WITH_WARNINGS_KEY, EMPTY_STRING);
            proposalDevelopmentForm.setResubmissionOption(null);
            proposalDevelopmentForm.setInstitutionalProposalToVersion(null);
        } else if (ConfirmationQuestion.NO.equals(buttonClicked)) {
            //do nothing
        } else {
            if ((ConfirmationQuestion.YES.equals(buttonClicked) || status == OK) && requiresResubmissionPrompt(proposalDevelopmentForm)) {
                return mapping.findForward(Constants.MAPPING_RESUBMISSION_PROMPT);
            }
            forward = submitApplication(mapping, form, request, response);
        }
        return forward;
    }

    /*
     * Used by isValidSubmission and reused in submitToSponsor, this method expects existence of at least a WARNING state/status.
     * i.e. at least one AuditError exists in Constants.AUDIT_WARNINGS audit cluster category. It also probes the cluster for
     * clusters belonging to AUDIT_ERRORS. If found, the status/state immediately escalates to, and returns ERROR.
     */
    private int putErrorInGlobalMessageMap() {
        int state = WARNING;
        for (Iterator iter = KNSGlobalVariables.getAuditErrorMap().keySet().iterator(); iter.hasNext();) {
            AuditCluster auditCluster = (AuditCluster) KNSGlobalVariables.getAuditErrorMap().get(iter.next());
            if (!StringUtils.equalsIgnoreCase(auditCluster.getCategory(), Constants.AUDIT_WARNINGS)) {
                GlobalVariables.getMessageMap().putError("noKey", KeyConstants.VALIDATTION_ERRORS_BEFORE_GRANTS_GOV_SUBMISSION);
                return ERROR;
            } else {
                state = WARNING;
            }
        }
        return state;
    }

    /**
     * Submit a proposal to GrantsGov.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward submitToGrantsGov(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument proposalDevelopmentDocument = (ProposalDevelopmentDocument) proposalDevelopmentForm.getProposalDevelopmentDocument();
        ActionForward forward = mapping.findForward(Constants.MAPPING_BASIC);
        proposalDevelopmentForm.setGrantsGovSubmitFlag(true);

        if (proposalDevelopmentDocument.getDevelopmentProposal().getSubmitFlag() != true) {
            forward = submitToSponsor(mapping, form, request, response);
        }
        ProposalDevelopmentService proposalDevelopmentService = KraServiceLocator.getService(ProposalDevelopmentService.class);
        InstitutionalProposal institutionalProposal = proposalDevelopmentService.getInstitutionalProposal(proposalDevelopmentDocument.getDevelopmentProposal().getProposalNumber());

        if (institutionalProposal != null) {
            try {
                submitS2sApplication(proposalDevelopmentDocument);
                proposalDevelopmentForm.setShowSubmissionDetails(true);
                if (proposalDevelopmentDocument.getDevelopmentProposal().getSubmitFlag() == true) {
                    forward = mapping.findForward(Constants.GRANTS_GOV_PAGE);
                }

            } catch (S2SException ex) {
                LOG.error(ex.getMessage(), ex);
                KNSGlobalVariables.getMessageList().add(new ErrorMessage(KeyConstants.ERROR_ON_GRANTS_GOV_SUBMISSION));
                return mapping.findForward(Constants.MAPPING_BASIC);
            }
        }
        return forward;
    }

    private boolean requiresResubmissionPrompt(ProposalDevelopmentForm proposalDevelopmentForm) {
        DevelopmentProposal developmentProposal = proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal();

        String proposalTypeCodeResubmission
                = getParameterService().getParameterValueAsString(ProposalDevelopmentDocument.class, KeyConstants.PROPOSALDEVELOPMENT_PROPOSALTYPE_RESUBMISSION);
        String proposalTypeCodeRevision
                = getParameterService().getParameterValueAsString(ProposalDevelopmentDocument.class, KeyConstants.PROPOSALDEVELOPMENT_PROPOSALTYPE_REVISION);
        String proposalTypeCodeContinuation
                = getParameterService().getParameterValueAsString(ProposalDevelopmentDocument.class, KeyConstants.PROPOSALDEVELOPMENT_PROPOSALTYPE_CONTINUATION);

        return (proposalTypeCodeResubmission.equals(developmentProposal.getProposalTypeCode())
                || proposalTypeCodeContinuation.equals(developmentProposal.getProposalTypeCode())
                || proposalTypeCodeRevision.equals(developmentProposal.getProposalTypeCode())
                || isSubmissionChangeCorrected(developmentProposal))
                && proposalDevelopmentForm.getResubmissionOption() == null;
    }

    private boolean isSubmissionChangeCorrected(DevelopmentProposal developmentProposal) {
        if (developmentProposal.getS2sOpportunity() != null) {
            return S2sSubmissionType.CHANGE_CORRECTED_CODE.equals(developmentProposal.getS2sOpportunity().getS2sSubmissionTypeCode());
        }
        return false;
    }

    /**
     * Is this a valid submission?
     *
     * @param proposalDevelopmentDocument
     * @return OK, WARNING or ERROR
     */
    private int isValidSubmission(ProposalDevelopmentForm proposalDevelopmentForm) {
        ProposalDevelopmentDocument proposalDevelopmentDocument = proposalDevelopmentForm.getProposalDevelopmentDocument();
        int state = OK;

        /*
         * If this proposal is a continuation from another proposal, it is illegal for
         * it to have a New Proposal Type and Application Submission Type.
         */
        if (!proposalDevelopmentForm.isUnitRulesErrorsExist() && proposalDevelopmentForm.isAuditActivated()) {
            proposalDevelopmentForm.setUnitRulesMessages(getUnitRulesMessages(proposalDevelopmentForm.getProposalDevelopmentDocument()));
        }
        if ((proposalDevelopmentDocument.getDevelopmentProposal().getContinuedFrom() != null) && isNewProposalType(proposalDevelopmentDocument) && isSubmissionApplication(proposalDevelopmentDocument)) {
            state = ERROR;
            //GlobalVariables.getMessageMap().putError("someKey", KeyConstants.ERROR_RESUBMISSION_INVALID_PROPOSALTYPE_SUBMISSIONTYPE);
        } else if (proposalDevelopmentForm.isUnitRulesErrorsExist()) {
            state = ERROR;
        } else {
            /* 
             * Don't know what to do about the Audit Rules.  The parent class invokes the Audit rules
             * from its execute() method.  It will stay this way for the time being because I this is
             * what the code did before it was refactored.
             */
            boolean auditPassed = new AuditActionHelper().auditUnconditionally(proposalDevelopmentDocument);

            /*
             * Check for S2S validation if we have a Grants.gov Opportunity.  If there is no Grants.gov
             * Opportunity, then the user wants to submit the proposal manually (printing and sending via
             * snail mail).
             */
            if (!KNSGlobalVariables.getAuditErrorMap().isEmpty()) {
                auditPassed = false;
            }
            boolean s2sPassed = true;
            if (proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity() != null) {
                S2SService s2sService = (S2SService) KraServiceLocator.getService(S2SService.class);
//                s2sPassed = s2sService.validateApplication(proposalDevelopmentDocument.getProposalNumber());
                try {
                    s2sPassed = s2sService.validateApplication(proposalDevelopmentDocument);
                } catch (S2SException e) {
                    log.error(e.getMessage(), e);
                    s2sPassed = false;
                }
            }

            /*
             * If either reported an error, then we have to check to see if we only have warnings or
             * if we also have some errors.
             */
            if (!auditPassed || !s2sPassed) {
                state = putErrorInGlobalMessageMap();
            }
        }

        return state;
    }

    private PermissionService getPermissionService() {
        return KimApiServiceLocator.getPermissionService();
    }

    /*
     * To submit to sponsor, user must also have permission to create and submit institutional proposal documents.
     */
    private boolean userCanCreateInstitutionalProposal() {
        boolean hasPermission = true;
        Map<String, String> permissionDetails = new HashMap<String, String>();
        permissionDetails.put(PermissionConstants.DOCUMENT_TYPE_ATTRIBUTE_QUALIFIER, InstitutionalProposalConstants.INSTITUTIONAL_PROPOSAL_DOCUMENT_NAME);
        if (!getPermissionService().hasPermission(GlobalVariables.getUserSession().getPrincipalId(),
                InstitutionalProposalConstants.INSTITUTIONAL_PROPOSAL_NAMESPACE,
                PermissionConstants.CREATE_INSTITUTIONAL_PROPOSAL)) {
            hasPermission = false;
            GlobalVariables.getMessageMap().putError("emptyProp",
                    KeyConstants.ERROR_SUBMIT_TO_SPONSOR_PERMISSONS,
                    GlobalVariables.getUserSession().getPrincipalName(),
                    PermissionConstants.CREATE_INSTITUTIONAL_PROPOSAL);
        }
        if (!getPermissionService().hasPermission(GlobalVariables.getUserSession().getPrincipalId(),
                InstitutionalProposalConstants.INSTITUTIONAL_PROPOSAL_NAMESPACE,
                PermissionConstants.SUBMIT_INSTITUTIONAL_PROPOSAL)) {
            hasPermission = false;
            GlobalVariables.getMessageMap().putError("emptyProp",
                    KeyConstants.ERROR_SUBMIT_TO_SPONSOR_PERMISSONS,
                    GlobalVariables.getUserSession().getPrincipalName(),
                    PermissionConstants.SUBMIT_INSTITUTIONAL_PROPOSAL);
        }
        return hasPermission;
    }

    /**
     * Is the proposal classified as NEW according to its proposal type?
     *
     * @param doc the proposal development document
     * @return true if new; otherwise false
     */
    private boolean isNewProposalType(ProposalDevelopmentDocument doc) {
        String newProposalTypeValue = getProposalParameterValue(KeyConstants.PROPOSALDEVELOPMENT_PROPOSALTYPE_NEW);
        return StringUtils.equalsIgnoreCase(doc.getDevelopmentProposal().getProposalTypeCode(), newProposalTypeValue);
    }

    /**
     * Does the proposal have a Grants.gov Opportunity with a S2S submission
     * type of APPLICATION?
     *
     * @param doc the proposal development document
     * @return true if proposal has grants.gov with submission type of
     * APPLICATION; otherwise false
     */
    private boolean isSubmissionApplication(ProposalDevelopmentDocument doc) {
        if (doc.getDevelopmentProposal().getS2sOpportunity() != null) {
            String applicationSubmissionValue = getProposalParameterValue(KeyConstants.S2S_SUBMISSIONTYPE_APPLICATION);
            return StringUtils.equalsIgnoreCase(doc.getDevelopmentProposal().getS2sOpportunity().getS2sSubmissionTypeCode(), applicationSubmissionValue);
        }
        return false;
    }

    /**
     * Get a Proposal parameter value from the System Parameters.
     *
     * @param parameterName the name of the parameter
     * @return the parameter's value
     */
    private String getProposalParameterValue(String parameterName) {
        return this.getParameterService().getParameterValueAsString(ProposalDevelopmentDocument.class, parameterName);
    }

    /**
     * Submit the proposal to the sponsor without any further error checking.
     * Can be invoked if the user wishes to ignore an warnings that occurred
     * when first trying to submit the proposal.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward submitApplication(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument proposalDevelopmentDocument = (ProposalDevelopmentDocument) proposalDevelopmentForm.getProposalDevelopmentDocument();
        ActionForward forward = mapping.findForward(Constants.MAPPING_BASIC);

        boolean isIPProtocolLinkingEnabled = getParameterService().getParameterValueAsBoolean(
                Constants.MODULE_NAMESPACE_PROTOCOL, Constants.PARAMETER_COMPONENT_DOCUMENT, "irb.protocol.institute.proposal.linking.enabled");
        List<ProposalSpecialReview> specialReviews = proposalDevelopmentDocument.getDevelopmentProposal().getPropSpecialReviews();
        if (!isIPProtocolLinkingEnabled || applyRules(new SaveSpecialReviewLinkEvent<ProposalSpecialReview>(proposalDevelopmentDocument, specialReviews))) {
            if (!(autogenerateInstitutionalProposal() && "X".equals(proposalDevelopmentForm.getResubmissionOption()))) {
                proposalDevelopmentDocument.getDevelopmentProposal().setSubmitFlag(true);

                ProposalStateService proposalStateService = KraServiceLocator.getService(ProposalStateService.class);
                if (ProposalState.APPROVED.equals(proposalDevelopmentDocument.getDevelopmentProposal().getProposalStateTypeCode())) {
                    proposalDevelopmentDocument.getDevelopmentProposal().setProposalStateTypeCode(ProposalState.APPROVED_AND_SUBMITTED);
                } else {
                    proposalDevelopmentDocument.getDevelopmentProposal().setProposalStateTypeCode(
                            proposalStateService.getProposalStateTypeCode(proposalDevelopmentDocument, false, false));
                }
            } else if (proposalDevelopmentDocument.getDocumentHeader().getWorkflowDocument().isFinal()) {
                proposalDevelopmentDocument.getDevelopmentProposal().setProposalStateTypeCode(ProposalState.APPROVED);
            } else {
                proposalDevelopmentDocument.getDevelopmentProposal().setProposalStateTypeCode(ProposalState.APPROVAL_PENDING);
            }
            String pCode = proposalDevelopmentDocument.getDevelopmentProposal().getProposalStateTypeCode();
            DocumentService documentService = KRADServiceLocatorWeb.getDocumentService();
            documentService.saveDocument(proposalDevelopmentDocument);
            if (!StringUtils.equals(pCode, proposalDevelopmentDocument.getDevelopmentProposal().getProposalStateTypeCode())) {
                proposalDevelopmentDocument.getDevelopmentProposal().setProposalStateTypeCode(pCode);
                proposalDevelopmentDocument.getDevelopmentProposal().refresh();
                KraServiceLocator.getService(BusinessObjectService.class).save(proposalDevelopmentDocument.getDevelopmentProposal());
            }

            if (autogenerateInstitutionalProposal()) {
                generateInstitutionalProposal(proposalDevelopmentForm, isIPProtocolLinkingEnabled);
                ProposalDevelopmentNotificationContext context = new ProposalDevelopmentNotificationContext(proposalDevelopmentDocument.getDevelopmentProposal(), "101", "Proposal Submitted");
                if (proposalDevelopmentForm.getNotificationHelper().getPromptUserForNotificationEditor(context)) {
                    proposalDevelopmentForm.getNotificationHelper().initializeDefaultValues(context);
                    forward = mapping.findForward("notificationEditor");
                } else {
                    getNotificationService().sendNotification(context);
                }
            }
        }

        return forward;
    }

    private void generateInstitutionalProposal(ProposalDevelopmentForm proposalDevelopmentForm, boolean isIPProtocolLinkingEnabled) {
        ProposalDevelopmentDocument proposalDevelopmentDocument = proposalDevelopmentForm.getProposalDevelopmentDocument();
        if ("X".equals(proposalDevelopmentForm.getResubmissionOption())) {
            if (proposalDevelopmentDocument.getDocumentHeader().getWorkflowDocument().isFinal()) {
                KNSGlobalVariables.getMessageList().add(KeyConstants.MESSAGE_INSTITUTIONAL_PROPOSAL_NOT_CREATED);
            } else {
                KNSGlobalVariables.getMessageList().add(KeyConstants.MESSAGE_INSTITUTIONAL_PROPOSAL_NOT_CREATED_INROUTE);
            }
            return;
        }
        if ("O".equals(proposalDevelopmentForm.getResubmissionOption())) {
            proposalDevelopmentForm.setInstitutionalProposalToVersion(proposalDevelopmentDocument.getDevelopmentProposal().getContinuedFrom());
        }
        if ("O".equals(proposalDevelopmentForm.getResubmissionOption())
                || "A".equals(proposalDevelopmentForm.getResubmissionOption())) {
            String versionNumber = createInstitutionalProposalVersion(
                    proposalDevelopmentForm.getInstitutionalProposalToVersion(),
                    proposalDevelopmentDocument.getDevelopmentProposal(),
                    proposalDevelopmentDocument.getFinalBudgetForThisProposal());
            KNSGlobalVariables.getMessageList().add(KeyConstants.MESSAGE_INSTITUTIONAL_PROPOSAL_VERSIONED,
                    versionNumber,
                    proposalDevelopmentForm.getInstitutionalProposalToVersion());

            Long institutionalProposalId = getActiveProposalId(proposalDevelopmentForm.getInstitutionalProposalToVersion());
            persistProposalAdminDetails(proposalDevelopmentDocument.getDevelopmentProposal().getProposalNumber(), institutionalProposalId);
            persistSpecialReviewProtocolFundingSourceLink(institutionalProposalId, isIPProtocolLinkingEnabled);
        } else {
            String proposalNumber = createInstitutionalProposal(
                    proposalDevelopmentDocument.getDevelopmentProposal(), proposalDevelopmentDocument.getFinalBudgetForThisProposal());
            KNSGlobalVariables.getMessageList().add(KeyConstants.MESSAGE_INSTITUTIONAL_PROPOSAL_CREATED, proposalNumber);

            Long institutionalProposalId = getActiveProposalId(proposalNumber);
            persistProposalAdminDetails(proposalDevelopmentDocument.getDevelopmentProposal().getProposalNumber(), institutionalProposalId);
            persistSpecialReviewProtocolFundingSourceLink(institutionalProposalId, isIPProtocolLinkingEnabled);
        }
    }

    private Long getActiveProposalId(String proposalNumber) {
        BusinessObjectService service = KraServiceLocator.getService(BusinessObjectService.class);
        Collection<InstitutionalProposal> ips = service.findMatching(InstitutionalProposal.class, getFieldValues(proposalNumber, "proposalNumber"));
        Long proposalId = ((InstitutionalProposal) ips.toArray()[0]).getProposalId();
        return proposalId;
    }

    /**
     * This method...
     *
     * @param proposalDevelopmentDocument
     * @param instProposalNumber
     * @return
     */
    private Long findInstProposalNumber(String devProposalNumber) {
        Long instProposalId = null;
        BusinessObjectService businessObjectService = KraServiceLocator.getService(BusinessObjectService.class);
        Collection<ProposalAdminDetails> proposalAdminDetails = businessObjectService.findMatching(ProposalAdminDetails.class, getFieldValues(devProposalNumber, "devProposalNumber"));

        for (Iterator iter = proposalAdminDetails.iterator(); iter.hasNext();) {
            ProposalAdminDetails pad = (ProposalAdminDetails) iter.next();
            instProposalId = pad.getInstProposalId();
            break;
        }
        return instProposalId;
    }

    private Map<String, String> getFieldValues(String value, String fieldName) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(fieldName, value);
        return map;
    }

    /**
     * This method...
     *
     * @param instProposalNumber
     * @param instProposalSequenceNumber TODO
     * @param proposalDevelopmentDocument
     */
    private void persistProposalAdminDetails(String devProposalNumber, Long instProposalId) {
        ProposalAdminDetails proposalAdminDetails = new ProposalAdminDetails();
        proposalAdminDetails.setDevProposalNumber(devProposalNumber);
        proposalAdminDetails.setInstProposalId(instProposalId);
        String loggedInUser = GlobalVariables.getUserSession().getPrincipalName();
        proposalAdminDetails.setSignedBy(loggedInUser);
        BusinessObjectService businessObjectService = KraServiceLocator.getService(BusinessObjectService.class);
        businessObjectService.save(proposalAdminDetails);
    }

    private void persistSpecialReviewProtocolFundingSourceLink(Long institutionalProposalId, boolean isIPProtocolLinkingEnabled) {
        if (isIPProtocolLinkingEnabled) {
            SpecialReviewService specialReviewService = KraServiceLocator.getService(SpecialReviewService.class);

            InstitutionalProposal institutionalProposal
                    = getBusinessObjectService().findBySinglePrimaryKey(InstitutionalProposal.class, institutionalProposalId);
            for (InstitutionalProposalSpecialReview specialReview : institutionalProposal.getSpecialReviews()) {
                if (SpecialReviewType.HUMAN_SUBJECTS.equals(specialReview.getSpecialReviewTypeCode())) {
                    String protocolNumber = specialReview.getProtocolNumber();
                    String fundingSourceNumber = institutionalProposal.getProposalNumber();
                    String fundingSourceTypeCode = FundingSourceType.INSTITUTIONAL_PROPOSAL;

                    if (!specialReviewService.isLinkedToProtocolFundingSource(protocolNumber, fundingSourceNumber, fundingSourceTypeCode)) {
                        String fundingSourceName = institutionalProposal.getSponsorName();
                        String fundingSourceTitle = institutionalProposal.getTitle();
                        specialReviewService.addProtocolFundingSourceForSpecialReview(
                                protocolNumber, fundingSourceNumber, fundingSourceTypeCode, fundingSourceName, fundingSourceTitle);
                    }
                }
            }
        }
    }

    /**
     * Submit a proposal to Grants.gov.
     *
     * @param proposalDevelopmentDocument
     * @throws Exception
     */
    private void submitS2sApplication(ProposalDevelopmentDocument proposalDevelopmentDocument) throws Exception {
        S2SService s2sService = ((S2SService) KraServiceLocator.getService(S2SService.class));
        s2sService.submitApplication(proposalDevelopmentDocument);
    }

    /**
     *
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    private StrutsConfirmation buildSubmitToGrantsGovWithWarningsQuestion(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return buildParameterizedConfirmationQuestion(mapping, form, request, response, CONFIRM_SUBMISSION_WITH_WARNINGS_KEY, KeyConstants.QUESTION_SUMBMIT_OPPORTUNITY_WITH_WARNINGS_CONFIRMATION);
    }

    private String createInstitutionalProposal(DevelopmentProposal developmentProposal, Budget budget) {
        InstitutionalProposalService institutionalProposalService = KraServiceLocator.getService(InstitutionalProposalService.class);
        String proposalNumber = institutionalProposalService.createInstitutionalProposal(developmentProposal, budget);
        return proposalNumber;
    }

    private String createInstitutionalProposalVersion(String proposalNumber, DevelopmentProposal developmentProposal, Budget budget) {
        InstitutionalProposalService institutionalProposalService = KraServiceLocator.getService(InstitutionalProposalService.class);
        String versionNumber = institutionalProposalService.createInstitutionalProposalVersion(proposalNumber, developmentProposal, budget);
        return versionNumber;
    }

    @Override
    public ActionForward reload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.reload(mapping, form, request, response);
        this.populateSponsorForms(form);
        return forward;
    }

    /**
     *
     * This method is called to populate sponsor forms under Print
     *
     * @param form
     * @throws Exception
     */
    public void populateSponsorForms(ActionForm form) throws Exception {
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument proposalDevelopmentDocument = proposalDevelopmentForm.getProposalDevelopmentDocument();
        ProposalDevelopmentPrintingService printService = KraServiceLocator.getService(ProposalDevelopmentPrintingService.class);
        printService.populateSponsorForms(proposalDevelopmentForm.getSponsorFormTemplates(), proposalDevelopmentDocument.getDevelopmentProposal().getSponsorCode());
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
    public ActionForward printSponsorForms(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument proposalDevelopmentDocument = (ProposalDevelopmentDocument) proposalDevelopmentForm.getProposalDevelopmentDocument();
        // ActionForward actionForward = mapping.findForward(MAPPING_BASIC);
        //  String proposalNumber = proposalDevelopmentDocument.getDevelopmentProposal().getProposalNumber();

        List<SponsorFormTemplateList> sponsorFormTemplateLists = proposalDevelopmentForm.getSponsorFormTemplates();
        ProposalDevelopmentPrintingService printService = KraServiceLocator.getService(ProposalDevelopmentPrintingService.class);
        List<SponsorFormTemplate> printFormTemplates = new ArrayList<SponsorFormTemplate>();
        printFormTemplates = printService.getSponsorFormTemplates(sponsorFormTemplateLists);
        Map<String, Object> reportParameters = new HashMap<String, Object>();
        reportParameters.put(ProposalDevelopmentPrintingService.SELECTED_TEMPLATES, printFormTemplates);

        // TODO fix - printing code does not catch null fields in proposal and does not fail gracefully when certain fields are blank
        try {
            AttachmentDataSource dataStream = printService.printProposalDevelopmentReport(proposalDevelopmentDocument.getDevelopmentProposal(),
                    ProposalDevelopmentPrintingService.PRINT_PROPOSAL_SPONSOR_FORMS, reportParameters);
            streamToResponse(dataStream, response);
            return null;//mapping.findForward(Constants.MAPPING_AWARD_BASIC);

        } catch (NullPointerException npe) {
            LOG.error("Error generating print stream for proposal forms", npe);
            GlobalVariables.getMessageMap().putError("print.nofield", KeyConstants.ERROR_PRINTING_UNKNOWN);
            return mapping.findForward(Constants.MAPPING_AWARD_BASIC);
        }
    }

    /**
     *
     * @param printFormTemplates
     * @param proposalNumber
     * @param contentType
     * @param ReportName
     * @param response
     * @throws Exception
     */
    public void streamToResponse(List<SponsorFormTemplate> printFormTemplates, String proposalNumber, String contentType, String ReportName, HttpServletResponse response) throws Exception {

        byte[] sftByteStream = KraServiceLocator.getService(PrintService.class).printProposalSponsorForms(proposalNumber, printFormTemplates);

        if (sftByteStream == null) {
            return;
        }
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream(sftByteStream.length);
            baos.write(sftByteStream);

            WebUtils.saveMimeOutputStreamAsFile(response, contentType, baos, ReportName);

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
    @Override
    public ActionForward printForms(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return super.printForms(mapping, form, request, response);
    }

    /**
     *
     * @return
     */
    @Override
    protected KualiRuleService getKualiRuleService() {
        return getService(KualiRuleService.class);
    }

    /**
     *
     * This method is for audit rule to forward to the page that the audit error
     * fix is clicked. This is an example for budget audit error forward from
     * proposal to budget page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward personnel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        String forward = getForwardToBudgetUrl(form);
        // TODO : what if forward is null
        forward = StringUtils.replace(forward, "budgetParameters.do?", "budgetPersonnel.do?auditActivated=true&");

        return new ActionForward(forward, true);
    }

    /**
     *
     * This method is for audit rule to forward to the Budget Parameters page
     * when the associated audit error fix is clicked.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward budgetDistributionAndIncome(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        String forward = getForwardToBudgetUrl(form);
        forward = StringUtils.replace(forward, "budgetParameters.do?", "budgetDistributionAndIncome.do?auditActivated=true&");

        return new ActionForward(forward, true);
    }

    /**
     *
     * This method is for audit rule to forward to the Budget Parameters page
     * when the associated audit error fix is clicked.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward parameters(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        String forward = getForwardToBudgetUrl(form);
        forward = StringUtils.replace(forward, "budgetParameters.do?", "budgetParameters.do?auditActivated=true&");

        return new ActionForward(forward, true);
    }

    /**
     *
     * This method gets called upon clicking of resubmit(replace sponsor) button
     * on Proposal Actions page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward resubmit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return mapping.findForward("resubmit");
    }

    /**
     *
     * This method is for budget expense rule when 'fix' is clicked.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward budgetExpenses(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        String forward = getForwardToBudgetUrl(form);
        String methodToCallAttribute = (String) request.getAttribute("methodToCallAttribute");
        String activePanelName = (String) request.getAttribute("activePanelName");

        String viewBudgetPeriodParam = null;
        if (StringUtils.isNotBlank(methodToCallAttribute)) {
            int idx = methodToCallAttribute.indexOf("&viewBudgetPeriod=");
            if (idx > 0) {
                viewBudgetPeriodParam = "viewBudgetPeriod=" + methodToCallAttribute.substring(methodToCallAttribute.indexOf('=', idx) + 1, methodToCallAttribute.indexOf('.', idx)) + "&";
            }
            idx = methodToCallAttribute.indexOf("&activePanelName=");
            if (idx > 0) {
                activePanelName = methodToCallAttribute.substring(methodToCallAttribute.indexOf('=', idx) + 1, methodToCallAttribute.indexOf('.', idx));
            }
        }

        forward = StringUtils.replace(forward, "budgetParameters.do?", "budgetExpenses.do?auditActivated=true&");
        if (viewBudgetPeriodParam != null) {
            forward = StringUtils.replace(forward, "budgetExpenses.do?", "budgetExpenses.do?" + viewBudgetPeriodParam);
        }

        if (StringUtils.isNotBlank(activePanelName) && "Personnel".equals(activePanelName)) {
            forward = StringUtils.replace(forward, "budgetExpenses.do?", "budgetPersonnel.do?activePanelName=" + activePanelName + "&");
        }

        return new ActionForward(forward, true);
    }

    /**
     *
     * This method is to forward to budget summary page for audit errors.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward summary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        String forward = getForwardToBudgetUrl(form);
        forward = StringUtils.replace(forward, "budgetParameters.do?", "budgetParameters.do?auditActivated=true&");

        return new ActionForward(forward, true);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward budgetRate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        String forward = getForwardToBudgetUrl(form);
        forward = StringUtils.replace(forward, "budgetParameters.do?", "budgetRates.do?auditActivated=true&anchor=" + ((KualiForm) form).getAnchor() + "&");
        return new ActionForward(forward, true);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveProposalActions(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return save(mapping, form, request, response);
    }

    /**
     *
     * This is a helper method to set up the forward to budget url for budget
     * audit error.
     *
     * @param form
     * @return
     */
    private String getForwardToBudgetUrl(ActionForm form) {
        ProposalDevelopmentForm pdForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument pdDoc = pdForm.getProposalDevelopmentDocument();
        BudgetDocument budgetDocument = null;
        String forward = null;
        try {
            for (BudgetDocumentVersion budgetDocumentVersion : pdDoc.getBudgetDocumentVersions()) {
                BudgetVersionOverview budgetVersion = budgetDocumentVersion.getBudgetVersionOverview();
                if (budgetVersion.isFinalVersionFlag()) {
                    DocumentService documentService = KraServiceLocator.getService(DocumentService.class);
                    budgetDocument = (BudgetDocument) documentService.getByDocumentHeaderId(budgetVersion.getDocumentNumber());
                }
            }
            String routeHeaderId = budgetDocument.getDocumentHeader().getWorkflowDocument().getDocumentId();
            forward = buildForwardUrl(routeHeaderId);
        } catch (Exception e) {
            LOG.error("forward to budgetsummary " + e.getStackTrace(), e);
            //TODO what is the forward here
        }
        return forward;

    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward linkChildToHierarchy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdForm = (ProposalDevelopmentForm) form;
        DevelopmentProposal hierarchyProposal = pdForm.getProposalDevelopmentDocument().getDevelopmentProposal();
        DevelopmentProposal newChildProposal = getHierarchyHelper().getDevelopmentProposal(pdForm.getNewHierarchyChildProposalNumber());
        String hierarchyBudgetTypeCode = pdForm.getNewHierarchyBudgetTypeCode();
        if (newChildProposal == null) {
            GlobalVariables.getMessageMap().putError(ProposalHierarchyKeyConstants.FIELD_CHILD_NUMBER, KeyConstants.ERROR_REQUIRED, "Link Child Proposal");
        } else {
            getHierarchyHelper().linkToHierarchy(hierarchyProposal, newChildProposal, hierarchyBudgetTypeCode);
            pdForm.setNewHierarchyChildProposalNumber("");
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward syncAllHierarchy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdForm = (ProposalDevelopmentForm) form;
        //DevelopmentProposal hierarchyProposal = pdForm.getProposalDevelopmentDocument().getDevelopmentProposal();
        getHierarchyHelper().syncAllHierarchy(pdForm.getProposalDevelopmentDocument());
        if (GlobalVariables.getMessageMap().containsMessageKey(ProposalHierarchyKeyConstants.QUESTION_EXTEND_PROJECT_DATE_CONFIRM)) {
            return doEndDateConfirmation(mapping, form, request, response, "syncAllHierarchy", "syncAllHierarchyConfirm");
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
        //return reload(mapping, form, request, response);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward syncAllHierarchyConfirm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdForm = (ProposalDevelopmentForm) form;
        //DevelopmentProposal hierarchyProposal = pdForm.getProposalDevelopmentDocument().getDevelopmentProposal();
        getHierarchyHelper().syncAllHierarchy(pdForm.getProposalDevelopmentDocument(), true);
        return mapping.findForward(Constants.MAPPING_BASIC);
        //return reload(mapping, form, request, response);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward removeFromHierarchy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        StrutsConfirmation question = buildParameterizedConfirmationQuestion(mapping, form, request, response, "removeFromHierarchy", ProposalHierarchyKeyConstants.QUESTION_REMOVE_CONFIRM);
        return confirm(question, "removeFromHierarchyConfirmed", "hierarchyActionCanceled");
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward removeFromHierarchyConfirmed(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdForm = (ProposalDevelopmentForm) form;
        DevelopmentProposal childProposal = pdForm.getProposalDevelopmentDocument().getDevelopmentProposal();
        getHierarchyHelper().removeFromHierarchy(childProposal);
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward hierarchyActionCanceled(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KNSGlobalVariables.getMessageList().add(ProposalHierarchyKeyConstants.MESSAGE_ACTION_CANCEL);
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward syncToHierarchyParent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdForm = (ProposalDevelopmentForm) form;
        DevelopmentProposal childProposal = pdForm.getProposalDevelopmentDocument().getDevelopmentProposal();
        getHierarchyHelper().syncToHierarchyParent(childProposal);
        if (GlobalVariables.getMessageMap().containsMessageKey(ProposalHierarchyKeyConstants.QUESTION_EXTEND_PROJECT_DATE_CONFIRM)) {
            return doEndDateConfirmation(mapping, form, request, response, "syncToHierarchyParent", "syncToHierarchyParentConfirm");
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward syncToHierarchyParentConfirm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdForm = (ProposalDevelopmentForm) form;
        DevelopmentProposal childProposal = pdForm.getProposalDevelopmentDocument().getDevelopmentProposal();
        getHierarchyHelper().syncToHierarchyParent(childProposal, true);
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward createHierarchy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdForm = (ProposalDevelopmentForm) form;
        DevelopmentProposal initialChildProposal = pdForm.getProposalDevelopmentDocument().getDevelopmentProposal();
        getHierarchyHelper().createHierarchy(initialChildProposal);
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward linkToHierarchy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdForm = (ProposalDevelopmentForm) form;
        DevelopmentProposal hierarchyProposal = getHierarchyHelper().getDevelopmentProposal(pdForm.getNewHierarchyProposalNumber());
        DevelopmentProposal newChildProposal = pdForm.getProposalDevelopmentDocument().getDevelopmentProposal();
        String hierarchyBudgetTypeCode = pdForm.getNewHierarchyBudgetTypeCode();
        if (hierarchyProposal == null) {
            GlobalVariables.getMessageMap().putError(ProposalHierarchyKeyConstants.FIELD_PARENT_NUMBER, KeyConstants.ERROR_REQUIRED, "Link to Hierarchy");
        } else {
            getHierarchyHelper().linkToHierarchy(hierarchyProposal, newChildProposal, hierarchyBudgetTypeCode);
            if (GlobalVariables.getMessageMap().containsMessageKey(ProposalHierarchyKeyConstants.QUESTION_EXTEND_PROJECT_DATE_CONFIRM)) {
                return doEndDateConfirmation(mapping, form, request, response, "linkToHierarchy", "linkToHierarchyConfirm");
            }
            pdForm.setNewHierarchyProposalNumber("");
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward linkToHierarchyConfirm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdForm = (ProposalDevelopmentForm) form;
        DevelopmentProposal hierarchyProposal = getHierarchyHelper().getDevelopmentProposal(pdForm.getNewHierarchyProposalNumber());
        DevelopmentProposal newChildProposal = pdForm.getProposalDevelopmentDocument().getDevelopmentProposal();
        String hierarchyBudgetTypeCode = pdForm.getNewHierarchyBudgetTypeCode();
        if (hierarchyProposal == null) {
            GlobalVariables.getMessageMap().putError(ProposalHierarchyKeyConstants.FIELD_PARENT_NUMBER, KeyConstants.ERROR_REQUIRED, "Link to Hierarchy");
        } else {
            getHierarchyHelper().linkToHierarchy(hierarchyProposal, newChildProposal, hierarchyBudgetTypeCode, true);
            pdForm.setNewHierarchyProposalNumber("");
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     *
     * @return
     */
    @Override
    public ParameterService getParameterService() {
        return KraServiceLocator.getService(ParameterService.class);
    }

    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ProposalDevelopmentForm pdForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument pdDoc = pdForm.getProposalDevelopmentDocument();
        pdDoc.prepareForSave();
        return super.cancel(mapping, form, request, response);
    }

    @Override
    public ActionForward disapprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ProposalDevelopmentForm pdForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument pdDoc = pdForm.getProposalDevelopmentDocument();
        ActionForward forward = super.disapprove(mapping, form, request, response);
        if (StringUtils.isNotBlank(request.getParameter(KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME))) {
            Long instPropId = findInstProposalNumber(pdDoc.getDevelopmentProposal().getProposalNumber());
            if (instPropId != null) {
                //we must have already submitted this proposal to sponsor so we need to create a new version of the inst prop
                //that has been withdrawn.
                InstitutionalProposal instProp = getBusinessObjectService().findBySinglePrimaryKey(InstitutionalProposal.class, instPropId);
                GlobalVariables.getUserSession().addObject(InstitutionalProposalDocumentAuthorizer.ALLOW_INIT_FOR_DISAPPROVED_PD_SESSION_KEY, Boolean.TRUE);
                String versionNumber = createInstitutionalProposalVersion(
                        instProp.getProposalNumber(),
                        pdDoc.getDevelopmentProposal(),
                        pdDoc.getFinalBudgetForThisProposal());
                GlobalVariables.getUserSession().removeObject(InstitutionalProposalDocumentAuthorizer.ALLOW_INIT_FOR_DISAPPROVED_PD_SESSION_KEY);
                KNSGlobalVariables.getMessageList().add(KeyConstants.MESSAGE_INSTITUTIONAL_PROPOSAL_VERSIONED,
                        versionNumber,
                        pdForm.getInstitutionalProposalToVersion());

                boolean isIPProtocolLinkingEnabled = getParameterService().getParameterValueAsBoolean(
                        Constants.MODULE_NAMESPACE_PROTOCOL, Constants.PARAMETER_COMPONENT_DOCUMENT, "irb.protocol.institute.proposal.linking.enabled");
                Long institutionalProposalId = getActiveProposalId(instProp.getProposalNumber());
                persistProposalAdminDetails(pdDoc.getDevelopmentProposal().getProposalNumber(), institutionalProposalId);
                persistSpecialReviewProtocolFundingSourceLink(institutionalProposalId, isIPProtocolLinkingEnabled);
            }
        }
        return forward;
    }

    @Override
    public ActionForward fyi(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ((ProposalDevelopmentForm) form).getDocument().prepareForSave();
        return super.fyi(mapping, form, request, response);
    }

    @Override
    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ActionForward returnForward = super.blanketApprove(mapping, form, request, response);

        String routeHeaderId = ((ProposalDevelopmentForm) form).getDocument().getDocumentNumber();
        String returnLocation = buildActionUrl(routeHeaderId, Constants.MAPPING_PROPOSAL_ACTIONS, "ProposalDevelopmentDocument");

        ActionForward forward = mapping.findForward(KRADConstants.MAPPING_PORTAL);
        ActionForward holdingPageForward = mapping.findForward(Constants.MAPPING_HOLDING_PAGE);
        return routeToHoldingPage(forward, returnForward, holdingPageForward, returnLocation);
    }

    @Override
    public ActionForward acknowledge(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ((ProposalDevelopmentForm) form).getDocument().prepareForSave();
        return super.acknowledge(mapping, form, request, response);
    }

    /**
     * Prepare current report (i.e. Awards that selected person is on)
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward printCurrentReportPdf(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CurrentAndPendingReportService currentAndPendingReportService = KraServiceLocator
                .getService(CurrentAndPendingReportService.class);
        ReportHelperBean helper = ((ReportHelperBeanContainer) form).getReportHelperBean();
        Map<String, Object> reportParameters = new HashMap<String, Object>();
        reportParameters.put(CurrentAndPendingReportService.PERSON_ID_KEY, helper.getPersonId());
        reportParameters.put(CurrentAndPendingReportService.REPORT_PERSON_NAME_KEY, helper.getTargetPersonName());
        AttachmentDataSource dataStream = currentAndPendingReportService.printCurrentAndPendingSupportReport(
                CurrentAndPendingReportService.CURRENT_REPORT_TYPE, reportParameters);
        streamToResponse(dataStream.getContent(), dataStream.getFileName(), null, response);
        return null;
    }

    /**
     * Prepare pending report (i.e. InstitutionalProposals that selected person
     * is on) {@inheritDoc}.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward printPendingReportPdf(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CurrentAndPendingReportService currentAndPendingReportService = KraServiceLocator
                .getService(CurrentAndPendingReportService.class);
        ReportHelperBean helper = ((ReportHelperBeanContainer) form).getReportHelperBean();
        Map<String, Object> reportParameters = new HashMap<String, Object>();
        reportParameters.put(CurrentAndPendingReportService.PERSON_ID_KEY, helper.getPersonId());
        reportParameters.put(CurrentAndPendingReportService.REPORT_PERSON_NAME_KEY, helper.getTargetPersonName());
        AttachmentDataSource dataStream = currentAndPendingReportService.printCurrentAndPendingSupportReport(
                CurrentAndPendingReportService.PENDING_REPORT_TYPE, reportParameters);
        streamToResponse(dataStream.getContent(), dataStream.getFileName(), null, response);
        return null;
    }

    /**
     * Prepare current report (i.e. Awards that selected person is on)
     * {@inheritDoc}
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws java.lang.Exception
     */
    public ActionForward prepareCurrentReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ReportHelperBean helper = ((ReportHelperBeanContainer) form).getReportHelperBean();
        request.setAttribute(CurrentAndPendingReportService.CURRENT_REPORT_ROWS_KEY, helper.prepareCurrentReport());
        request.setAttribute(CurrentAndPendingReportService.REPORT_PERSON_NAME_KEY, helper.getTargetPersonName());
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Prepare pending report (i.e. InstitutionalProposals that selected person
     * is on) {@inheritDoc}
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     *
     * @return
     * @throws java.lang.Exception
     */
    public ActionForward preparePendingReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ReportHelperBean helper = ((ReportHelperBeanContainer) form).getReportHelperBean();
        request.setAttribute(CurrentAndPendingReportService.PENDING_REPORT_ROWS_KEY, helper.preparePendingReport());
        request.setAttribute(CurrentAndPendingReportService.REPORT_PERSON_NAME_KEY, helper.getTargetPersonName());
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    private ActionForward doEndDateConfirmation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionId, String yesMethodName) throws Exception {
        List<ErrorMessage> errors = GlobalVariables.getMessageMap().getErrorMessagesForProperty(ProposalHierarchyKeyConstants.FIELD_CHILD_NUMBER);
        List<String> proposalNumbers = new ArrayList<String>();
        for (ErrorMessage error : errors) {
            if (error.getErrorKey().equals(ProposalHierarchyKeyConstants.QUESTION_EXTEND_PROJECT_DATE_CONFIRM)) {
                proposalNumbers.add(error.getMessageParameters()[0]);
            }
        }
        String proposalNumberList = StringUtils.join(proposalNumbers, ',');
        StrutsConfirmation question = buildParameterizedConfirmationQuestion(mapping, form, request, response, questionId, ProposalHierarchyKeyConstants.QUESTION_EXTEND_PROJECT_DATE_CONFIRM, proposalNumberList);
        GlobalVariables.getMessageMap().getErrorMessages().clear();
        GlobalVariables.getMessageMap().getWarningMessages().clear();
        GlobalVariables.getMessageMap().getInfoMessages().clear();
        return confirm(question, yesMethodName, "hierarchyActionCanceled");
    }

    private boolean autogenerateInstitutionalProposal() {
        return getParameterService().getParameterValueAsBoolean(
                Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT,
                ParameterConstants.DOCUMENT_COMPONENT,
                KeyConstants.AUTOGENERATE_INSTITUTIONAL_PROPOSAL_PARAM);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteProposal(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (((ProposalDevelopmentForm) form).getProposalDevelopmentDocument().getDevelopmentProposal().isInHierarchy()) {
            KNSGlobalVariables.getMessageList().add(new ErrorMessage(KeyConstants.ERROR_DELETE_PROPOSAL_IN_HIERARCHY));
            return mapping.findForward(Constants.MAPPING_BASIC);
        }
        StrutsConfirmation question
                = buildParameterizedConfirmationQuestion(mapping, form, request, response, DOCUMENT_DELETE_QUESTION, KeyConstants.QUESTION_DELETE_PROPOSAL);
        return confirm(question, "confirmDeleteProposal", "");
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward confirmDeleteProposal(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm propDevForm = (ProposalDevelopmentForm) form;
        KraServiceLocator.getService(ProposalDevelopmentService.class).deleteProposal(propDevForm.getProposalDevelopmentDocument());
        return mapping.findForward("portal");
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward sendNotification(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        DevelopmentProposal developmentProposal = proposalDevelopmentForm.getProposalDevelopmentDocument().getDevelopmentProposal();

        ProposalDevelopmentNotificationRenderer renderer = new ProposalDevelopmentNotificationRenderer(developmentProposal);

        ProposalDevelopmentNotificationContext context = new ProposalDevelopmentNotificationContext(developmentProposal,
                NotificationType.AD_HOC_NOTIFICATION_TYPE, NotificationType.AD_HOC_CONTEXT, renderer);

        proposalDevelopmentForm.getNotificationHelper().initializeDefaultValues(context);

        return mapping.findForward("notificationEditor");
    }

    /**
     *
     * @return
     */
    protected KraWorkflowService getKraWorkflowService() {
        if (kraWorkflowService == null) {
            kraWorkflowService = KraServiceLocator.getService(KraWorkflowService.class);
        }
        return kraWorkflowService;
    }

    /**
     *
     * @param kraWorkflowService
     */
    public void setKraWorkflowService(KraWorkflowService kraWorkflowService) {
        this.kraWorkflowService = kraWorkflowService;
    }

    /**
     * Updates an Editable Budget Field and adds it to the Budget Change History
     *
     * @param mapping the Strut's Action Mapping.
     * @param form the Proposal Development Form.
     * @param request the HTTP request.
     * @param response the HTTP response
     * @return the next web page to display
     * @throws Exception
     */
    public ActionForward addProposalBudgetChangedData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        BudgetDocument budgetDocument = null;
        BusinessObjectService boService = KraServiceLocator.getService(BusinessObjectService.class);
        KraPersistenceStructureService kraPersistenceStructureService = KraServiceLocator
                .getService(KraPersistenceStructureService.class);

        ActionForward forward = mapping.findForward("basic");
        ProposalDevelopmentForm pdForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument pdDocument = pdForm.getProposalDevelopmentDocument();
        Map budgetMap = new HashMap();
        budgetMap.put("parentDocumentKey", pdDocument.getDocumentNumber());
        BudgetChangedData newBudgetChangedData = pdForm.getNewBudgetChangedData();
        newBudgetChangedData.setProposalNumber(pdDocument.getDevelopmentProposal().getProposalNumber());

        Collection<BudgetDocument> budgetDocuments = boService.findMatching(BudgetDocument.class, budgetMap);
        for (BudgetDocument document : budgetDocuments) {
            if (document.getBudget().getFinalVersionFlag()) {
                budgetDocument = document;
                break;
            }
        }

        newBudgetChangedData.setChangeNumber(getBudgetNextChangeNumber(boService, newBudgetChangedData.getProposalNumber(),
                newBudgetChangedData.getColumnName()));

        if (StringUtils.isEmpty(newBudgetChangedData.getDisplayValue())
                && StringUtils.isNotEmpty(newBudgetChangedData.getChangedValue())) {
            newBudgetChangedData.setDisplayValue(newBudgetChangedData.getChangedValue());
        }

        String tmpLookupReturnPk = "";
        if (newBudgetChangedData.getEditableColumn() != null) {
            tmpLookupReturnPk = newBudgetChangedData.getEditableColumn().getLookupPkReturn();
        }

        newBudgetChangedData.refreshReferenceObject("editableColumn");
        newBudgetChangedData.getEditableColumn().setLookupPkReturn(tmpLookupReturnPk);

        if (newBudgetChangedData.getEditableColumn() != null) {
            if (!newBudgetChangedData.getEditableColumn().getHasLookup()) {
                newBudgetChangedData.setDisplayValue(newBudgetChangedData.getChangedValue());
            }
        }
        if (getKualiRuleService().applyRules(
                new BudgetDataOverrideEvent(pdForm.getProposalDevelopmentDocument(), newBudgetChangedData))) {
            boService.save(newBudgetChangedData);
            BudgetVersionOverview budgetVersionWrapper = createProposalBudgetWrapper(budgetDocument);
            Map<String, String> columnToAttributesMap = kraPersistenceStructureService
                    .getDBColumnToObjectAttributeMap(BudgetVersionOverview.class);
            String proposalAttributeToPersist = columnToAttributesMap.get(newBudgetChangedData.getColumnName());
            ObjectUtils.setObjectProperty(budgetVersionWrapper, proposalAttributeToPersist, newBudgetChangedData.getChangedValue());
            ObjectUtils.setObjectProperty(budgetDocument.getBudget(), proposalAttributeToPersist,
                    newBudgetChangedData.getChangedValue());
            boService.save(budgetVersionWrapper);
            budgetDocument.setVersionNumber(budgetVersionWrapper.getVersionNumber());
            pdForm.setNewBudgetChangedData(new BudgetChangedData());
            growProposalBudgetChangedHistory(pdDocument, newBudgetChangedData);

        }
        return forward;

    }

    private void growProposalBudgetChangedHistory(ProposalDevelopmentDocument proposalDevelopmentDocument,
            BudgetChangedData newBudgetChangedData) {
        Map<String, List<BudgetChangedData>> changeHistory = proposalDevelopmentDocument.getDevelopmentProposal()
                .getBudgetChangeHistory();

        if (changeHistory.get(newBudgetChangedData.getEditableColumn().getColumnLabel()) == null) {
            changeHistory.put(newBudgetChangedData.getEditableColumn().getColumnLabel(), new ArrayList<BudgetChangedData>());
        }

        changeHistory.get(newBudgetChangedData.getEditableColumn().getColumnLabel()).add(0, newBudgetChangedData);
    }

    private BudgetVersionOverview createProposalBudgetWrapper(BudgetDocument budgetDocument) throws Exception {
        BudgetVersionOverview budgetVersionWrapper = new BudgetVersionOverview();
        PersistenceStructureService persistentStructureService = KraServiceLocator.getService(PersistenceStructureService.class);
        List<String> fieldsToUpdate = (List<String>) persistentStructureService.listFieldNames(BudgetVersionOverview.class);
        for (String field : fieldsToUpdate) {
            boolean noSuchFieldPD = false;
            boolean noSuchFieldBO = false;
            Object tempVal = null;

            try {
                tempVal = ObjectUtils.getPropertyValue(budgetDocument, field);
            } catch (Exception e) {
                noSuchFieldPD = true;
            }

            try {
                tempVal = ObjectUtils.getPropertyValue(budgetDocument.getBudget(), field);
            } catch (Exception e) {
                noSuchFieldBO = true;
            }

            if (tempVal == null && noSuchFieldPD && noSuchFieldBO) {
                LOG.warn("Could not find property " + field + " in BudgettDocument or Budget bo.");
            }

            ObjectUtils.setObjectProperty(budgetVersionWrapper, field, (tempVal != null) ? tempVal : null);
        }
        return budgetVersionWrapper;
    }

    private int getBudgetNextChangeNumber(BusinessObjectService boService, String proposalNumber, String columnName) {
        int changeNumber = 0;
        Map<String, Object> keyMap = new HashMap<String, Object>();
        keyMap.put("proposalNumber", proposalNumber);
        keyMap.put("columnName", columnName);
        List<BudgetChangedData> changedDataList = (List<BudgetChangedData>) boService.findMatchingOrderBy(BudgetChangedData.class,
                keyMap, "changeNumber", true);
        if (CollectionUtils.isNotEmpty(changedDataList)) {
            changeNumber = ((BudgetChangedData) changedDataList.get(changedDataList.size() - 1)).getChangeNumber();
        }

        return ++changeNumber;
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    @Override
    public ActionForward takeSuperUserActions(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return superUserActionHelper(SuperUserAction.TAKE_SUPER_USER_ACTIONS, mapping, form, request, response);
    }

    /**
     *
     * This method enable the ability to save the generated system to system XML
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveXml(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument proposalDevelopmentDocument = (ProposalDevelopmentDocument) proposalDevelopmentForm.getDocument();
        proposalDevelopmentDocument.getDevelopmentProposal().setGrantsGovSelectFlag(true);
        proposalDevelopmentForm.setDocument(proposalDevelopmentDocument);
        return super.printForms(mapping, proposalDevelopmentForm, request, response);

    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    @Override
    public ActionForward superUserApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return superUserActionHelper(SuperUserAction.SUPER_USER_APPROVE, mapping, form, request, response);
    }

    /**
     *
     * @param actionName
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    protected ActionForward superUserActionHelper(SuperUserAction actionName, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument pdDoc = proposalDevelopmentForm.getProposalDevelopmentDocument();

        Object question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
        Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
        String methodToCall = ((KualiForm) form).getMethodToCall();

        ActionForward forward = mapping.findForward(Constants.MAPPING_BASIC);

        if (!pdDoc.getDocumentHeader().getWorkflowDocument().isEnroute()) {
            proposalDevelopmentForm.setAuditActivated(true);
            int status = isValidSubmission(proposalDevelopmentForm);
            boolean userSaysOk = false;

            if (status == WARNING) {
                if (question == null) {
                    List<String> selectedActionRequests = proposalDevelopmentForm.getSelectedActionRequests();
                    // Need to add the super user requests to user session because they are wiped out by 
                    //the KualiRequestProcessor reset on 
                    //clicking yes to the question. Retrieve again during actual routing and add to form.
                    GlobalVariables.getUserSession().addObject(SUPER_USER_ACTION_REQUESTS, selectedActionRequests);
                    try {
                        forward = this.performQuestionWithoutInput(mapping, form, request, response,
                                DOCUMENT_ROUTE_QUESTION, ROUTING_WARNING_MESSAGE,
                                KRADConstants.CONFIRMATION_QUESTION, methodToCall, "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (DOCUMENT_ROUTE_QUESTION.equals(question) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                    proposalDevelopmentForm.setSelectedActionRequests((List<String>) GlobalVariables.getUserSession().retrieveObject(SUPER_USER_ACTION_REQUESTS));
                    GlobalVariables.getUserSession().removeObject(SUPER_USER_ACTION_REQUESTS);
                    //status is OK now since the user said it was :)
                    userSaysOk = true;
                } else if (DOCUMENT_APPROVE_QUESTION.equals(question)) {
                    //user said ok in the past since we got to this question.
                    userSaysOk = true;
                }
            }

            if (status == OK || userSaysOk) {
                switch (actionName) {
                    case SUPER_USER_APPROVE:
                        forward = super.superUserApprove(mapping, proposalDevelopmentForm, request, response);
                        break;
                    case TAKE_SUPER_USER_ACTIONS:
                        forward = super.takeSuperUserActions(mapping, proposalDevelopmentForm, request, response);
                        break;
                }

            } else if (status == WARNING) {

            } else {
                GlobalVariables.getMessageMap().clearErrorMessages(); // clear error from isValidSubmission()    
                GlobalVariables.getMessageMap().putError("datavalidation", KeyConstants.ERROR_WORKFLOW_SUBMISSION, new String[]{});

                if ((pdDoc.getDevelopmentProposal().getContinuedFrom() != null) && isNewProposalType(pdDoc) && isSubmissionApplication(pdDoc)) {
                    GlobalVariables.getMessageMap().putError("someKey", KeyConstants.ERROR_RESUBMISSION_INVALID_PROPOSALTYPE_SUBMISSIONTYPE);
                }

                forward = mapping.findForward((Constants.MAPPING_BASIC));
            }
        } else {
            switch (actionName) {
                case SUPER_USER_APPROVE:
                    forward = super.superUserApprove(mapping, proposalDevelopmentForm, request, response);
                    break;
                case TAKE_SUPER_USER_ACTIONS:
                    forward = super.takeSuperUserActions(mapping, proposalDevelopmentForm, request, response);
                    break;
            }
        }
        return forward;
    }

    /**
     * handle a lock action
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward lock(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        lock(form);

        return saveAndShowLockMessage(Constants.MESSAGE_PROPOSAL_LOCKED, mapping, form, request, response);
    }

    /**
     * extracted from main method to enable easier testing
     *
     * @param form
     */
    protected void lock(ActionForm form) {
        updateProposalLock(form, true);
    }

    /**
     * handle an unlock action
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unlock(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        unlock(form);

        return saveAndShowLockMessage(Constants.MESSAGE_PROPOSAL_UNLOCKED, mapping, form, request, response);
    }

    /**
     * extracted from main method to enable easier testing
     *
     * @param form
     */
    protected void unlock(ActionForm form) {
        updateProposalLock(form, false);
    }

    /**
     * save the document and show a message if the lock was successful
     *
     * @param messageKey
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAndShowLockMessage(String messageKey, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward fwd = super.save(mapping, form, request, response);
        if (!GlobalVariables.getMessageMap().hasErrors()) {
            GlobalVariables.getMessageMap().putInfo(KRADConstants.GLOBAL_MESSAGES, messageKey);
        }

        return fwd;
    }

    /**
     * a convenience method that sets the proposal status to locked/unlocked and
     * sets an info message to be viewed on the UI
     *
     * @param form - the ProposalDevelopmentForm
     * @param lockStatus - the lock status to set - true for lock, false for
     * unlock
     */
    protected void updateProposalLock(ActionForm form, boolean lockStatus) {
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument pdDoc = proposalDevelopmentForm.getProposalDevelopmentDocument();
        DevelopmentProposal developmentProposal = pdDoc.getDevelopmentProposal();

        // TODO - create a method in unit service to fetch these admin types from parameter service
        List<String> qualifiedAdminTypes = Arrays.asList(proposalDevelopmentForm.getAdminTypesAuthorizedToLock());

        if (getUnitService().isQualifiedUnitAdminForProposal(developmentProposal,
                GlobalVariables.getUserSession().getPrincipalId(),
                qualifiedAdminTypes)) {
            developmentProposal.setLocked(lockStatus);
            developmentProposal.setLockedTimeStamp(new Timestamp(System.currentTimeMillis()));
            developmentProposal.setLockedByPrincipalName(GlobalVariables.getUserSession().getPrincipalName());
        }
    }

    /**
     * @return the unitSvc from {@link KraServiceLocator} or a mock
     */
    public UnitService getUnitService() {
        if (unitSvc == null) {
            unitSvc = KraServiceLocator.getService(UnitService.class);
        }
        return unitSvc;
    }

    /**
     * @param unitSvc the unitSvc to set
     */
    public void setUnitService(UnitService unitSvc) {
        this.unitSvc = unitSvc;
    }

}
