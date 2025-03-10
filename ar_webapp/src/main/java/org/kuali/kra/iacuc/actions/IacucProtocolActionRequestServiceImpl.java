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
package org.kuali.kra.iacuc.actions;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kra.bo.CoeusModule;
import org.kuali.kra.bo.CoeusSubModule;
import org.kuali.kra.common.notification.bo.NotificationTypeRecipient;
import org.kuali.kra.iacuc.IacucProtocol;
import org.kuali.kra.iacuc.IacucProtocolDocument;
import org.kuali.kra.iacuc.IacucProtocolForm;
import org.kuali.kra.iacuc.actions.abandon.IacucProtocolAbandonService;
import org.kuali.kra.iacuc.actions.amendrenew.CreateIacucAmendmentEvent;
import org.kuali.kra.iacuc.actions.amendrenew.CreateIacucContinuationEvent;
import org.kuali.kra.iacuc.actions.amendrenew.CreateIacucRenewalEvent;
import org.kuali.kra.iacuc.actions.amendrenew.IacucProtocolAmendRenewService;
import org.kuali.kra.iacuc.actions.amendrenew.IacucProtocolAmendmentBean;
import org.kuali.kra.iacuc.actions.approve.IacucProtocolApproveBean;
import org.kuali.kra.iacuc.actions.approve.IacucProtocolApproveEvent;
import org.kuali.kra.iacuc.actions.approve.IacucProtocolApproveService;
import org.kuali.kra.iacuc.actions.assignCmt.IacucProtocolAssignCmtBean;
import org.kuali.kra.iacuc.actions.assignCmt.IacucProtocolAssignCmtEvent;
import org.kuali.kra.iacuc.actions.assignCmt.IacucProtocolAssignCmtService;
import org.kuali.kra.iacuc.actions.assignagenda.IacucProtocolAssignToAgendaBean;
import org.kuali.kra.iacuc.actions.assignagenda.IacucProtocolAssignToAgendaEvent;
import org.kuali.kra.iacuc.actions.assignagenda.IacucProtocolAssignToAgendaService;
import org.kuali.kra.iacuc.actions.correction.IacucAdminCorrectionBean;
import org.kuali.kra.iacuc.actions.correction.IacucProtocolAdminCorrectionEvent;
import org.kuali.kra.iacuc.actions.decision.IacucCommitteeDecision;
import org.kuali.kra.iacuc.actions.decision.IacucCommitteeDecisionEvent;
import org.kuali.kra.iacuc.actions.decision.IacucCommitteeDecisionService;
import org.kuali.kra.iacuc.actions.genericactions.IacucProtocolGenericActionBean;
import org.kuali.kra.iacuc.actions.genericactions.IacucProtocolGenericActionEvent;
import org.kuali.kra.iacuc.actions.genericactions.IacucProtocolGenericActionService;
import org.kuali.kra.iacuc.actions.modifysubmission.IacucProtocolModifySubmissionBean;
import org.kuali.kra.iacuc.actions.modifysubmission.IacucProtocolModifySubmissionEvent;
import org.kuali.kra.iacuc.actions.modifysubmission.IacucProtocolModifySubmissionService;
import org.kuali.kra.iacuc.actions.noreview.IacucProtocolReviewNotRequiredBean;
import org.kuali.kra.iacuc.actions.noreview.IacucProtocolReviewNotRequiredEvent;
import org.kuali.kra.iacuc.actions.noreview.IacucProtocolReviewNotRequiredService;
import org.kuali.kra.iacuc.actions.notifyiacuc.IacucProtocolNotifyIacucService;
import org.kuali.kra.iacuc.actions.notifyiacuc.NotifyIacucNotificationRenderer;
import org.kuali.kra.iacuc.actions.request.IacucProtocolRequestBean;
import org.kuali.kra.iacuc.actions.request.IacucProtocolRequestEvent;
import org.kuali.kra.iacuc.actions.request.IacucProtocolRequestRule;
import org.kuali.kra.iacuc.actions.request.IacucProtocolRequestService;
import org.kuali.kra.iacuc.actions.reviewcomments.IacucReviewCommentsBean;
import org.kuali.kra.iacuc.actions.submit.IacucProtocolActionService;
import org.kuali.kra.iacuc.actions.submit.IacucProtocolReviewType;
import org.kuali.kra.iacuc.actions.submit.IacucProtocolReviewerBean;
import org.kuali.kra.iacuc.actions.submit.IacucProtocolSubmission;
import org.kuali.kra.iacuc.actions.submit.IacucProtocolSubmissionStatus;
import org.kuali.kra.iacuc.actions.submit.IacucProtocolSubmissionType;
import org.kuali.kra.iacuc.actions.submit.IacucProtocolSubmitAction;
import org.kuali.kra.iacuc.actions.submit.IacucProtocolSubmitActionService;
import org.kuali.kra.iacuc.actions.table.IacucProtocolTableBean;
import org.kuali.kra.iacuc.actions.table.IacucProtocolTableService;
import org.kuali.kra.iacuc.actions.withdraw.IacucProtocolWithdrawService;
import org.kuali.kra.iacuc.auth.IacucGenericProtocolAuthorizer;
import org.kuali.kra.iacuc.auth.IacucProtocolTask;
import org.kuali.kra.iacuc.correspondence.IacucProtocolActionsCorrespondence;
import org.kuali.kra.iacuc.correspondence.IacucProtocolCorrespondence;
import org.kuali.kra.iacuc.infrastructure.IacucConstants;
import org.kuali.kra.iacuc.notification.IacucProtocolAssignReviewerNotificationRenderer;
import org.kuali.kra.iacuc.notification.IacucProtocolNotification;
import org.kuali.kra.iacuc.notification.IacucProtocolNotificationContext;
import org.kuali.kra.iacuc.notification.IacucProtocolNotificationRenderer;
import org.kuali.kra.iacuc.notification.IacucProtocolNotificationRequestBean;
import org.kuali.kra.iacuc.notification.IacucProtocolRequestActionNotificationRenderer;
import org.kuali.kra.iacuc.notification.IacucProtocolReviewDeterminationNotificationRenderer;
import org.kuali.kra.iacuc.notification.IacucProtocolWithReasonNotificationRenderer;
import org.kuali.kra.iacuc.notification.IacucRequestActionNotificationBean;
import org.kuali.kra.iacuc.onlinereview.IacucProtocolOnlineReview;
import org.kuali.kra.iacuc.questionnaire.IacucProtocolModuleQuestionnaireBean;
import org.kuali.kra.iacuc.questionnaire.IacucProtocolQuestionnaireAuditRule;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.TaskName;
import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.protocol.ProtocolDocumentBase;
import org.kuali.kra.protocol.ProtocolFormBase;
import org.kuali.kra.protocol.actions.ProtocolActionBase;
import org.kuali.kra.protocol.actions.ProtocolActionBean;
import org.kuali.kra.protocol.actions.ProtocolActionRequestServiceImpl;
import org.kuali.kra.protocol.actions.ProtocolActionTypeBase;
import org.kuali.kra.protocol.actions.correction.AdminCorrectionBean;
import org.kuali.kra.protocol.actions.correspondence.ProtocolActionsCorrespondenceBase;
import org.kuali.kra.protocol.actions.submit.ProtocolReviewerBeanBase;
import org.kuali.kra.protocol.actions.submit.ProtocolSubmissionBase;
import org.kuali.kra.protocol.auth.ProtocolTaskBase;
import org.kuali.kra.protocol.correspondence.ProtocolCorrespondence;
import org.kuali.kra.protocol.notification.ProtocolNotification;
import org.kuali.kra.protocol.notification.ProtocolNotificationContextBase;
import org.kuali.kra.protocol.notification.ProtocolNotificationRequestBeanBase;
import org.kuali.kra.protocol.questionnaire.ProtocolQuestionnaireAuditRuleBase;
import org.kuali.kra.questionnaire.answer.AnswerHeader;
import org.kuali.kra.questionnaire.answer.ModuleQuestionnaireBean;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.util.CollectionUtils;

public class IacucProtocolActionRequestServiceImpl extends ProtocolActionRequestServiceImpl implements IacucProtocolActionRequestService {
    private static final Log LOG = LogFactory.getLog(IacucProtocolActionRequestServiceImpl.class);

    // map to decide the followup action page to open.  "value" part is the action tab "title"
    private static Map<String, String> motionTypeMap = new HashMap<String, String>() {
        {
            put("1", "Approve Action");
            put("2", "Disapprove");
            put("3", "Return for Specific Minor Revisions");
            put("4", "Return for Substantive Revisions Required");
        }
    };

    protected static List <String> requestSubmissionTypes = Arrays.asList(new String[] {IacucProtocolSubmissionType.REQUEST_SUSPEND,
                                                                                      IacucProtocolSubmissionType.REQUEST_TO_LIFT_HOLD,
                                                                                      IacucProtocolSubmissionType.REQUEST_TO_DEACTIVATE});

    private IacucProtocolApproveService protocolApproveService;
    private IacucProtocolSubmitActionService protocolSubmitActionService;
    private IacucProtocolAmendRenewService protocolAmendRenewService;
    private IacucProtocolAssignToAgendaService protocolAssignToAgendaService;
    private IacucProtocolReviewNotRequiredService protocolReviewNotRequiredService;
    private IacucProtocolRequestService protocolRequestService;
    private IacucProtocolGenericActionService protocolGenericActionService;
    private IacucProtocolAbandonService protocolAbandonService;
    private IacucProtocolModifySubmissionService modifySubmissionService;
    private IacucProtocolTableService protocolTableService;
    private IacucProtocolWithdrawService protocolWithdrawService;
    private IacucProtocolNotifyIacucService protocolNotifyService;
    private IacucCommitteeDecisionService committeeDecisionService;
    private IacucProtocolAssignCmtService assignToCmtService;
    private IacucProtocolActionService protocolActionService;
    
    private static final String ACTION_NAME_CONTINUATION_WITHOUT_AMENDMENT = "Create Continuation without Amendment";
    private static final String ACTION_NAME_CONTINUATION_WITH_AMENDMENT = "Create Continuation with Amendment";
    private static final String ACTION_NAME_REMOVE_FROM_AGENDA = "Removed Agenda";
    private static final String ACTION_NAME_ACKNOWLEDGEMENT = "IACUC Acknowledgement";
    private static final String ACTION_NAME_HOLD = "IACUC Hold";
    private static final String ACTION_NAME_LIFT_HOLD = "IACUC Lift Hold";
    private static final String ACTION_NAME_DEACTIVATED = "Deactivated";
    private static final String ACTION_NAME_MODIFY_SUBMISSION = "Modify Submission";
    private static final String ACTION_NAME_TABLE_PROTOCOL = "Table ProtocolBase";
    private static final String ACTION_NAME_ADMINISTRATIVELY_WITHDRAW = "Administratively Withdraw";
    private static final String ACTION_NAME_NOTIFY = "Notify IACUC";
    private static final String ACTION_NAME_REVIEW_TYPE_DETERMINATION = "Send Review Type Determination Notification";
    private static final String ACTION_NAME_ADMINISTRATIVELY_INCOMPLETE = "Administratively Mark Incomplete";
    private static final String ACTION_NAME_ASSIGN_TO_COMMITTEE = "Assign to Committee";
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isFullApprovalAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isFullApprovalAuthorized(IacucProtocolForm protocolForm) {
        boolean requestAuthorized = false;
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        IacucProtocolApproveBean actionBean = (IacucProtocolApproveBean) protocolForm.getActionHelper().getProtocolFullApprovalBean();
        if (hasPermission(TaskName.APPROVE_PROTOCOL, (IacucProtocol) document.getProtocol())) {
            requestAuthorized = applyRules(new IacucProtocolApproveEvent(document, actionBean));
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isCreateAmendmentAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isCreateAmendmentAuthorized(IacucProtocolForm protocolForm) {
        boolean requestAuthorized = false;
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        IacucProtocolAmendmentBean protocolAmendmentBean = (IacucProtocolAmendmentBean) protocolForm.getActionHelper().getProtocolAmendmentBean();
        if (hasPermission(TaskName.CREATE_IACUC_PROTOCOL_AMENDMENT, (IacucProtocol) document.getProtocol())) {
            requestAuthorized = applyRules(new CreateIacucAmendmentEvent(document, Constants.PROTOCOL_CREATE_AMENDMENT_KEY, protocolAmendmentBean));
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isCreateRenewalAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isCreateRenewalAuthorized(IacucProtocolForm protocolForm) {
        boolean requestAuthorized = false;
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        String renewalSummary = protocolForm.getActionHelper().getRenewalSummary();
        if (hasPermission(TaskName.CREATE_IACUC_PROTOCOL_RENEWAL, (IacucProtocol) document.getProtocol())) {
            requestAuthorized = applyRules(new CreateIacucRenewalEvent(document, Constants.PROTOCOL_CREATE_RENEWAL_SUMMARY_KEY, renewalSummary));
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isCreateRenewalWithAmendmentAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isCreateRenewalWithAmendmentAuthorized(IacucProtocolForm protocolForm) {
        boolean requestAuthorized = false;
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        IacucProtocolAmendmentBean renewAmendmentBean = (IacucProtocolAmendmentBean)protocolForm.getActionHelper().getProtocolRenewAmendmentBean();
        if (hasPermission(TaskName.CREATE_IACUC_PROTOCOL_RENEWAL, (IacucProtocol) document.getProtocol())) {
            requestAuthorized = applyRules(new CreateIacucAmendmentEvent(document, Constants.PROTOCOL_CREATE_RENEWAL_WITH_AMENDMENT_KEY, renewAmendmentBean));
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isCreateContinuationAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isCreateContinuationAuthorized(IacucProtocolForm protocolForm) {
        boolean requestAuthorized = false;
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        IacucActionHelper actionHelper = (IacucActionHelper)protocolForm.getActionHelper();
        String continuationSummary = actionHelper.getContinuationSummary();
        if (hasPermission(TaskName.CREATE_IACUC_PROTOCOL_CONTINUATION, (IacucProtocol) document.getProtocol())) {
            requestAuthorized = applyRules(new CreateIacucContinuationEvent(document, Constants.PROTOCOL_CREATE_CONTINUATION_SUMMARY_KEY, continuationSummary));
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isCreateContinuationWithAmendmentAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isCreateContinuationWithAmendmentAuthorized(IacucProtocolForm protocolForm) {
        boolean requestAuthorized = false;
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        IacucActionHelper actionHelper = (IacucActionHelper)protocolForm.getActionHelper();
        IacucProtocolAmendmentBean continuationAmendmentBean = (IacucProtocolAmendmentBean)actionHelper.getProtocolContinuationAmendmentBean();
        if (hasPermission(TaskName.CREATE_IACUC_PROTOCOL_CONTINUATION, (IacucProtocol) document.getProtocol())) {
            requestAuthorized = applyRules(new CreateIacucAmendmentEvent(document, Constants.PROTOCOL_CREATE_CONTINUATION_WITH_AMENDMENT_KEY, continuationAmendmentBean));
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isAssignToAgendaAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isAssignToAgendaAuthorized(IacucProtocolForm protocolForm) {
        // set the task name to prevent entered data from being overwritten (in case of user errors) due to bean refresh in the action helper's prepare view 
        protocolForm.getActionHelper().setCurrentTask(TaskName.ASSIGN_TO_AGENDA);
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        boolean requestAuthorized = false;
        IacucProtocolAssignToAgendaBean actionBean = (IacucProtocolAssignToAgendaBean) protocolForm.getActionHelper().getAssignToAgendaBean();
        if (!hasDocumentStateChanged(protocolForm)) {
            if (hasPermission(TaskName.ASSIGN_TO_AGENDA, (IacucProtocol) document.getProtocol())) {
                requestAuthorized = applyRules(new IacucProtocolAssignToAgendaEvent(document, actionBean));
                actionBean.prepareView();
            }
        } else {
            updateDocumentStatusChangedMessage();
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isRemoveFromAgendaAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isRemoveFromAgendaAuthorized(IacucProtocolForm protocolForm) {
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        boolean requestAuthorized = false;
        if (!hasDocumentStateChanged(protocolForm)) {
            requestAuthorized = hasPermission(TaskName.REMOVE_FROM_AGENDA, (IacucProtocol) document.getProtocol());
        } else {
            updateDocumentStatusChangedMessage();
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isProtocolReviewNotRequiredAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isProtocolReviewNotRequiredAuthorized(IacucProtocolForm protocolForm) {
        boolean requestAuthorized = false;
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        IacucActionHelper actionHelper = (IacucActionHelper)protocolForm.getActionHelper();
        IacucProtocolReviewNotRequiredBean actionBean = (IacucProtocolReviewNotRequiredBean) actionHelper.getProtocolReviewNotRequiredBean();
        if (hasPermission(TaskName.REVIEW_NOT_REQUIRED_IACUC_PROTOCOL, (IacucProtocol) document.getProtocol())) {
            requestAuthorized = applyRules(new IacucProtocolReviewNotRequiredEvent(document, actionBean));
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isGrantAdminApprovalAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isGrantAdminApprovalAuthorized(IacucProtocolForm protocolForm) {
        boolean requestAuthorized = false;
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        IacucProtocolApproveBean actionBean = (IacucProtocolApproveBean) protocolForm.getActionHelper().getProtocolAdminApprovalBean();
        if (hasPermission(TaskName.ADMIN_APPROVE_PROTOCOL, (IacucProtocol) document.getProtocol())) {
            requestAuthorized = applyRules(new IacucProtocolApproveEvent(document, actionBean));
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isRequestActionAuthorized(org.kuali.kra.iacuc.IacucProtocolForm, java.lang.String)
     */
    public boolean isRequestActionAuthorized(IacucProtocolForm protocolForm, String taskName) {
        boolean requestAuthorized = false;
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        if (StringUtils.isNotBlank(taskName)) {
            requestAuthorized = hasPermission(taskName, (IacucProtocol) document.getProtocol());
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isDisapproveProtocolAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isDisapproveProtocolAuthorized(IacucProtocolForm protocolForm) {
        boolean requestAuthorized = false;
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        IacucProtocolGenericActionBean actionBean = (IacucProtocolGenericActionBean) protocolForm.getActionHelper().getProtocolDisapproveBean();
        if (hasPermission(TaskName.DISAPPROVE_PROTOCOL, (IacucProtocol) document.getProtocol())) {
            requestAuthorized = applyRules(new IacucProtocolGenericActionEvent(document, actionBean));
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isTerminateProtocolAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isTerminateProtocolAuthorized(IacucProtocolForm protocolForm) {
        boolean requestAuthorized = false;
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        IacucProtocolGenericActionBean actionBean = (IacucProtocolGenericActionBean) protocolForm.getActionHelper().getProtocolTerminateBean();
        if (hasGenericPermission(IacucGenericProtocolAuthorizer.TERMINATE_PROTOCOL, (IacucProtocol) document.getProtocol())) {
            requestAuthorized = applyRules(new IacucProtocolGenericActionEvent(document, actionBean));
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isExpireProtocolAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isExpireProtocolAuthorized(IacucProtocolForm protocolForm) {
        boolean requestAuthorized = false;
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        IacucProtocolGenericActionBean actionBean = (IacucProtocolGenericActionBean) protocolForm.getActionHelper().getProtocolExpireBean();
        if (hasGenericPermission(IacucGenericProtocolAuthorizer.EXPIRE_PROTOCOL, (IacucProtocol) document.getProtocol())) {
            requestAuthorized = applyRules(new IacucProtocolGenericActionEvent(document, actionBean));
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isSuspendProtocolAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isSuspendProtocolAuthorized(IacucProtocolForm protocolForm) {
        boolean requestAuthorized = false;
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        IacucProtocolGenericActionBean actionBean = (IacucProtocolGenericActionBean) protocolForm.getActionHelper().getProtocolSuspendBean();
        if (hasGenericPermission(IacucGenericProtocolAuthorizer.SUSPEND_PROTOCOL, (IacucProtocol) document.getProtocol())) {
            requestAuthorized = applyRules(new IacucProtocolGenericActionEvent(document, actionBean));
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isAcknowledgementAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isAcknowledgementAuthorized(IacucProtocolForm protocolForm) {
        boolean requestAuthorized = false;
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        IacucProtocolGenericActionBean actionBean = ((IacucActionHelper) protocolForm.getActionHelper()).getIacucAcknowledgeBean();
        if (hasPermission(TaskName.IACUC_ACKNOWLEDGEMENT, (IacucProtocol) document.getProtocol())) {
            requestAuthorized = applyRules(new IacucProtocolGenericActionEvent(document, actionBean));
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isHoldAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isHoldAuthorized(IacucProtocolForm protocolForm) {
        boolean requestAuthorized = false;
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        IacucProtocolGenericActionBean actionBean = ((IacucActionHelper) protocolForm.getActionHelper()).getIacucProtocolHoldBean();
        if (hasPermission(TaskName.IACUC_PROTOCOL_HOLD, (IacucProtocol) document.getProtocol())) {
            requestAuthorized = applyRules(new IacucProtocolGenericActionEvent(document, actionBean));
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isLiftHoldAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isLiftHoldAuthorized(IacucProtocolForm protocolForm) {
        boolean requestAuthorized = false;
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        IacucProtocolGenericActionBean actionBean = ((IacucActionHelper) protocolForm.getActionHelper()).getIacucProtocolLiftHoldBean();
        if (hasPermission(TaskName.IACUC_PROTOCOL_LIFT_HOLD, (IacucProtocol) document.getProtocol())) {
            requestAuthorized = applyRules(new IacucProtocolGenericActionEvent(document, actionBean));
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isReturnForSMRAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isReturnForSMRAuthorized(IacucProtocolForm protocolForm) {
        boolean requestAuthorized = false;
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        IacucProtocolGenericActionBean actionBean = (IacucProtocolGenericActionBean) protocolForm.getActionHelper().getProtocolSMRBean();
        if (hasPermission(TaskName.RETURN_FOR_SMR, (IacucProtocol) document.getProtocol())) {
            requestAuthorized = applyRules(new IacucProtocolGenericActionEvent(document, actionBean));
        }
        return requestAuthorized;
    }

    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isReturnForSRRAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isReturnForSRRAuthorized(IacucProtocolForm protocolForm) {
        boolean requestAuthorized = false;
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        IacucProtocolGenericActionBean actionBean = (IacucProtocolGenericActionBean) protocolForm.getActionHelper().getProtocolSRRBean();
        if (hasPermission(TaskName.RETURN_FOR_SRR, (IacucProtocol) document.getProtocol())) {
            requestAuthorized = applyRules(new IacucProtocolGenericActionEvent(document, actionBean));
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isReturnToPIAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isReturnToPIAuthorized(IacucProtocolForm protocolForm) {
        boolean requestAuthorized = false;
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        IacucProtocolGenericActionBean actionBean = (IacucProtocolGenericActionBean) protocolForm.getActionHelper().getProtocolReturnToPIBean();
        if (hasPermission(TaskName.RETURN_TO_PI_PROTOCOL, (IacucProtocol) document.getProtocol())) {
            requestAuthorized = applyRules(new IacucProtocolGenericActionEvent(document, actionBean));
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isDeactivateAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isDeactivateAuthorized(IacucProtocolForm protocolForm) {
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        boolean requestAuthorized = false;
        if (!hasDocumentStateChanged(protocolForm)) {
            if (hasPermission(TaskName.IACUC_PROTOCOL_DEACTIVATE, (IacucProtocol) document.getProtocol())) {
                IacucActionHelper actionHelper = (IacucActionHelper)protocolForm.getActionHelper();
                IacucProtocolGenericActionBean actionBean = actionHelper.getIacucProtocolDeactivateBean();
                requestAuthorized = applyRules(new IacucProtocolGenericActionEvent(document, actionBean));
            }
        } else {
            updateDocumentStatusChangedMessage();
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isOpenProtocolForAdminCorrectionAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isOpenProtocolForAdminCorrectionAuthorized(IacucProtocolForm protocolForm) {
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        boolean requestAuthorized = false;
        AdminCorrectionBean actionBean = (AdminCorrectionBean)protocolForm.getActionHelper().getProtocolAdminCorrectionBean();
        if (!hasDocumentStateChanged(protocolForm)) {
            if (hasPermission(TaskName.IACUC_PROTOCOL_ADMIN_CORRECTION, (IacucProtocol) document.getProtocol())) {
                requestAuthorized = applyRules(new IacucProtocolAdminCorrectionEvent(document, Constants.PROTOCOL_ADMIN_CORRECTION_PROPERTY_KEY,actionBean));
            }
        } else {
            updateDocumentStatusChangedMessage();
        }
        return requestAuthorized;
    }

    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isAbandonAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isAbandonAuthorized(IacucProtocolForm protocolForm) {
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        boolean requestAuthorized = hasPermission(TaskName.IACUC_ABANDON_PROTOCOL, (IacucProtocol) document.getProtocol());
        return requestAuthorized;
    }

    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isModifySubmissionActionAuthorized(org.kuali.kra.iacuc.IacucProtocolForm, java.util.List)
     */
    public boolean isModifySubmissionActionAuthorized(IacucProtocolForm protocolForm, List<ProtocolReviewerBeanBase> reviewers) {
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        boolean requestAuthorized = false;
        IacucProtocolModifySubmissionBean actionBean = ((IacucActionHelper) protocolForm.getActionHelper()).getIacucProtocolModifySubmissionBean();
        if (!hasDocumentStateChanged(protocolForm)) {
            if (hasPermission(TaskName.IACUC_MODIFY_PROTOCOL_SUBMISSION, (IacucProtocol) document.getProtocol())) {
                actionBean.setReviewers(reviewers);
                requestAuthorized = applyRules(new IacucProtocolModifySubmissionEvent(document, actionBean));
                actionBean.prepareView();
            }
        } else {
            updateDocumentStatusChangedMessage();
        }
        return requestAuthorized;
    }

    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isTableProtocolAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isTableProtocolAuthorized(IacucProtocolForm protocolForm) {
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        boolean requestAuthorized = false;
        if (!hasDocumentStateChanged(protocolForm)) {
            requestAuthorized = hasPermission(TaskName.IACUC_PROTOCOL_TABLE, (IacucProtocol) document.getProtocol());
        } else {
            updateDocumentStatusChangedMessage();
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isAdministrativelyWithdrawProtocolAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isAdministrativelyWithdrawProtocolAuthorized(IacucProtocolForm protocolForm) {
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        boolean requestAuthorized = false;
        if (!hasDocumentStateChanged(protocolForm)) {
            requestAuthorized = hasPermission(TaskName.ADMIN_WITHDRAW_PROTOCOL, (IacucProtocol) document.getProtocol());
        } else {
            updateDocumentStatusChangedMessage();
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isWithdrawProtocolAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isWithdrawProtocolAuthorized(IacucProtocolForm protocolForm) {
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        boolean requestAuthorized = false;
        if (!hasDocumentStateChanged(protocolForm)) {
            requestAuthorized = hasPermission(TaskName.PROTOCOL_WITHDRAW, (IacucProtocol) document.getProtocol());
        } else {
            updateDocumentStatusChangedMessage();
        }
        return requestAuthorized;
    }

    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isAdministrativelyMarkIncompleteProtocolAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isAdministrativelyMarkIncompleteProtocolAuthorized(IacucProtocolForm protocolForm) {
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        boolean requestAuthorized = false;
        if (!hasDocumentStateChanged(protocolForm)) {
            requestAuthorized = hasPermission(TaskName.ADMIN_INCOMPLETE_PROTOCOL, (IacucProtocol) document.getProtocol());
        } else {
            updateDocumentStatusChangedMessage();
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isSubmitCommitteeDecisionAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isSubmitCommitteeDecisionAuthorized(IacucProtocolForm protocolForm) {
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        boolean requestAuthorized = false;
        if (!hasDocumentStateChanged(protocolForm)) {
            IacucCommitteeDecision actionBean = (IacucCommitteeDecision) protocolForm.getActionHelper().getCommitteeDecision();
            requestAuthorized = applyRules(new IacucCommitteeDecisionEvent(document, actionBean));
        } else {
            updateDocumentStatusChangedMessage();
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isAssignCommitteeAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isAssignCommitteeAuthorized(IacucProtocolForm protocolForm) {
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        boolean requestAuthorized = false;
        IacucActionHelper actionHelper = (IacucActionHelper)protocolForm.getActionHelper();
        IacucProtocolAssignCmtBean actionBean = actionHelper.getProtocolAssignCmtBean();
        if (!hasDocumentStateChanged(protocolForm)) {
            if (hasPermission(TaskName.IACUC_ASSIGN_TO_COMMITTEE, (IacucProtocol) document.getProtocol())) {
                requestAuthorized = applyRules(new IacucProtocolAssignCmtEvent(document, actionBean));
            }
        } else {
            updateDocumentStatusChangedMessage();
        }
        return requestAuthorized;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#isWithdrawRequestActionAuthorized(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public boolean isWithdrawRequestActionAuthorized(IacucProtocolForm protocolForm) {
        return hasPermission(TaskName.IACUC_WITHDRAW_SUBMISSION, protocolForm.getProtocolDocument().getProtocol());
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#grantFullApproval(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    @SuppressWarnings("deprecation")
    public void grantFullApproval(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        IacucProtocol protocol = document.getIacucProtocol();
        IacucProtocolApproveBean actionBean = (IacucProtocolApproveBean) protocolForm.getActionHelper().getProtocolFullApprovalBean();
        getProtocolApproveService().grantFullApproval(protocol, actionBean);
        saveReviewComments(protocolForm, (IacucReviewCommentsBean) actionBean.getReviewCommentsBean());
        IacucProtocolSubmission submission = (IacucProtocolSubmission)protocol.getProtocolSubmission();
        String actionType;
        String actionDescription;
        String actionDescription2;
        if (StringUtils.equals(submission.getProtocolReviewTypeCode(),IacucProtocolReviewType.DESIGNATED_MEMBER_REVIEW)) {
            actionType = IacucProtocolActionType.DESIGNATED_REVIEW_APPROVAL;
            actionDescription = "Designated Member Approval";
            actionDescription2 = "Designated Member Approved";
        }
        else {
            actionType = IacucProtocolActionType.IACUC_APPROVED;
            actionDescription = "Full Approval";
            actionDescription2 = "Approved";
        }
        generateActionCorrespondence(actionType, protocolForm.getProtocolDocument().getProtocol());
        recordProtocolActionSuccess(actionDescription);
        protocolForm.getProtocolHelper().prepareView();
        if (protocolForm.getActionHelper().getProtocolCorrespondence() != null) {
            GlobalVariables.getUserSession().addObject("approvalComplCorrespondence", GlobalVariables.getUserSession().retrieveObject(DocumentAuthorizerBase.USER_SESSION_METHOD_TO_CALL_COMPLETE_OBJECT_KEY));
            // temporarily remove this key which is generated by super.approve
            GlobalVariables.getUserSession().removeObject(DocumentAuthorizerBase.USER_SESSION_METHOD_TO_CALL_COMPLETE_OBJECT_KEY);
        } else {
            IacucProtocolNotificationRenderer renderer = new IacucProtocolNotificationRenderer((IacucProtocol) document.getProtocol());
            IacucProtocolNotificationContext context = new IacucProtocolNotificationContext((IacucProtocol) document.getProtocol(), actionType, actionDescription2, renderer);
            getNotificationService().sendNotificationAndPersist(context, new IacucProtocolNotification(), protocol);
        }
        
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#submitForReview(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public void submitForReview(IacucProtocolForm protocolForm, List<ProtocolReviewerBeanBase> reviewers) throws Exception {
        IacucProtocolDocument protocolDocument = protocolForm.getIacucProtocolDocument();
        IacucProtocol protocol = protocolDocument.getIacucProtocol();
        IacucProtocolSubmitAction submitAction = (IacucProtocolSubmitAction) protocolForm.getActionHelper().getProtocolSubmitAction();
        submitAction.setReviewers(reviewers);
        getProtocolSubmitActionService().submitToIacucForReview(protocol, submitAction);
        generateActionCorrespondence(IacucProtocolActionType.SUBMITTED_TO_IACUC, protocolForm.getProtocolDocument().getProtocol());
        // first, send out notification that protocol has been submitted
        IacucProtocolNotificationRenderer submitRenderer = new IacucProtocolNotificationRenderer(protocol);
        IacucProtocolNotificationContext submitContext = new IacucProtocolNotificationContext(protocol, null, 
                                                    IacucProtocolActionType.SUBMITTED_TO_IACUC, "Submit", submitRenderer);
        getNotificationService().sendNotificationAndPersist(submitContext, new IacucProtocolNotification(), protocol);
    }

    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#createAmendment(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String createAmendment(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocol protocol = protocolForm.getIacucProtocolDocument().getIacucProtocol();
        String newDocId = getProtocolAmendRenewService().createAmendment(protocolForm.getProtocolDocument(), protocolForm.getActionHelper().getProtocolAmendmentBean());
        generateActionCorrespondence(IacucProtocolActionType.AMENDMENT_CREATED, protocolForm.getProtocolDocument().getProtocol());
        refreshAfterProtocolAction(protocolForm, newDocId, ACTION_NAME_AMENDMENT, true);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(protocol, IacucProtocolActionType.AMENDMENT_CREATED_NOTIFICATION, "Amendment Created");
        protocolForm.getActionHelper().setProtocolCorrespondence(getProtocolCorrespondence(protocolForm, IacucConstants.PROTOCOL_TAB, notificationBean, false));
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#createRenewal(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String createRenewal(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocol protocol = protocolForm.getIacucProtocolDocument().getIacucProtocol();
        String newDocId = getProtocolAmendRenewService().createRenewal(protocolForm.getProtocolDocument(),protocolForm.getActionHelper().getRenewalSummary());
        generateActionCorrespondence(IacucProtocolActionType.RENEWAL_CREATED, protocolForm.getProtocolDocument().getProtocol());
        refreshAfterProtocolAction(protocolForm, newDocId, ACTION_NAME_RENEWAL_WITHOUT_AMENDMENT, true);
        // Form fields copy needed to support modifyAmendmentSections
        protocolForm.getActionHelper().getProtocolAmendmentBean().setSummary(protocolForm.getActionHelper().getRenewalSummary());
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(protocol, IacucProtocolActionType.RENEWAL_CREATED_NOTIFICATION, "Renewal Created");
        protocolForm.getActionHelper().setProtocolCorrespondence(getProtocolCorrespondence(protocolForm, IacucConstants.PROTOCOL_TAB, notificationBean, false));
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#createRenewalWithAmendment(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String createRenewalWithAmendment(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocol protocol = protocolForm.getIacucProtocolDocument().getIacucProtocol();
        IacucProtocolDocument protocolDocument = protocolForm.getIacucProtocolDocument();
        IacucProtocolAmendmentBean renewAmendmentBean = (IacucProtocolAmendmentBean)protocolForm.getActionHelper().getProtocolRenewAmendmentBean();
        String newDocId = getProtocolAmendRenewService().createRenewalWithAmendment(protocolDocument,
                renewAmendmentBean);
        generateActionCorrespondence(IacucProtocolActionType.RENEWAL_WITH_AMENDMENT_CREATED, protocolForm.getProtocolDocument().getProtocol());
        refreshAfterProtocolAction(protocolForm, newDocId, ACTION_NAME_RENEWAL_WITH_AMENDMENT, true);
        // Form fields copy needed to support modifyAmendmentSections
        protocolForm.getActionHelper().setProtocolAmendmentBean(renewAmendmentBean);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(protocol, IacucProtocolActionType.RENEWAL_WITH_AMENDMENT_CREATED, "Renewal With Amendment Created");
        protocolForm.getActionHelper().setProtocolCorrespondence(getProtocolCorrespondence(protocolForm, IacucConstants.PROTOCOL_TAB, notificationBean, false));
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#createContinuation(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String createContinuation(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocol protocol = protocolForm.getIacucProtocolDocument().getIacucProtocol();
        IacucProtocolDocument protocolDocument = protocolForm.getIacucProtocolDocument();
        IacucActionHelper actionHelper = (IacucActionHelper)protocolForm.getActionHelper();
        String newDocId = getProtocolAmendRenewService().createContinuation(protocolDocument, actionHelper.getContinuationSummary());
        generateActionCorrespondence(IacucProtocolActionType.CONTINUATION, protocolForm.getProtocolDocument().getProtocol());
        refreshAfterProtocolAction(protocolForm, newDocId, ACTION_NAME_CONTINUATION_WITHOUT_AMENDMENT, true);
        // Form fields copy needed to support modifyAmendmentSections
        protocolForm.getActionHelper().getProtocolAmendmentBean().setSummary(actionHelper.getContinuationSummary());
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(protocol, IacucProtocolActionType.CONTINUATION_CREATED_NOTIFICATION, "Continuation Created");
        protocolForm.getActionHelper().setProtocolCorrespondence(getProtocolCorrespondence(protocolForm, IacucConstants.PROTOCOL_TAB, notificationBean, false));
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#createContinuationWithAmendment(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String createContinuationWithAmendment(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocol protocol = protocolForm.getIacucProtocolDocument().getIacucProtocol();
        IacucProtocolDocument protocolDocument = protocolForm.getIacucProtocolDocument();
        IacucActionHelper actionHelper = (IacucActionHelper)protocolForm.getActionHelper();
        IacucProtocolAmendmentBean continuationAmendmentBean = (IacucProtocolAmendmentBean)actionHelper.getProtocolContinuationAmendmentBean();
        String newDocId = getProtocolAmendRenewService().createContinuationWithAmendment(protocolDocument,
                continuationAmendmentBean);
        generateActionCorrespondence(IacucProtocolActionType.CONTINUATION_AMENDMENT, protocolForm.getProtocolDocument().getProtocol());
        refreshAfterProtocolAction(protocolForm, newDocId, ACTION_NAME_CONTINUATION_WITH_AMENDMENT, true);
        // Form fields copy needed to support modifyAmendmentSections
        protocolForm.getActionHelper().setProtocolAmendmentBean(continuationAmendmentBean);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(protocol, IacucProtocolActionType.CONTINUATION_AMENDMENT, "Continuation With Amendment Created");
        protocolForm.getActionHelper().setProtocolCorrespondence(getProtocolCorrespondence(protocolForm, IacucConstants.PROTOCOL_TAB, notificationBean, false));
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#assignToAgenda(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String assignToAgenda(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocol protocol = (IacucProtocol) protocolForm.getProtocolDocument().getProtocol();
        IacucProtocolAssignToAgendaBean actionBean = (IacucProtocolAssignToAgendaBean) protocolForm.getActionHelper().getAssignToAgendaBean();
        getProtocolAssignToAgendaService().assignToAgenda(protocol, actionBean);
        generateActionCorrespondence(IacucProtocolActionType.ASSIGNED_TO_AGENDA, protocolForm.getProtocolDocument().getProtocol());
        saveReviewComments(protocolForm, (IacucReviewCommentsBean) actionBean.getReviewCommentsBean());
        recordProtocolActionSuccess(ACTION_NAME_ASSIGN_TO_AGENDA);
        ProtocolActionBase lastAction = protocolForm.getProtocolDocument().getProtocol().getLastProtocolAction();
        ProtocolActionTypeBase lastActionType = lastAction.getProtocolActionType();
        String description = lastActionType.getDescription();
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(protocol, IacucProtocolActionType.ASSIGNED_TO_AGENDA, description);
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_ACTIONS_TAB);
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#removeFromAgenda(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String removeFromAgenda(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocol protocol = (IacucProtocol) protocolForm.getProtocolDocument().getProtocol();
        IacucActionHelper actionHelper = (IacucActionHelper)protocolForm.getActionHelper();
        IacucProtocolGenericActionBean actionBean = actionHelper.getIacucProtocolRemoveFromAgendaBean();
        getProtocolAssignToAgendaService().removeFromAgenda(protocol, actionBean);
        generateActionCorrespondence(IacucProtocolActionType.REMOVE_FROM_AGENDA, protocolForm.getProtocolDocument().getProtocol());
        saveReviewComments(protocolForm, (IacucReviewCommentsBean) actionBean.getReviewCommentsBean());
        recordProtocolActionSuccess(ACTION_NAME_REMOVE_FROM_AGENDA);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(protocol, IacucProtocolActionType.REMOVE_FROM_AGENDA, actionBean.getComments());
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_ACTIONS_TAB);
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#protocolReviewNotRequired(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String protocolReviewNotRequired(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        IacucActionHelper actionHelper = (IacucActionHelper)protocolForm.getActionHelper();
        IacucProtocolReviewNotRequiredBean actionBean = (IacucProtocolReviewNotRequiredBean) actionHelper.getProtocolReviewNotRequiredBean();
        getProtocolReviewNotRequiredService().reviewNotRequired(document, actionBean);
        generateActionCorrespondence(IacucProtocolActionType.IACUC_REVIEW_NOT_REQUIRED, protocolForm.getProtocolDocument().getProtocol());
        recordProtocolActionSuccess(ACTION_NAME_REVIEW_NOT_REQUIRED);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(document.getIacucProtocol(), IacucProtocolActionType.IACUC_REVIEW_NOT_REQUIRED, "Review Not Required");
        protocolForm.getActionHelper().setProtocolCorrespondence(getProtocolCorrespondence(protocolForm, IacucConstants.PROTOCOL_TAB, notificationBean, false));
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#grantAdminApproval(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String grantAdminApproval(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        IacucProtocolApproveBean actionBean = (IacucProtocolApproveBean) protocolForm.getActionHelper().getProtocolAdminApprovalBean();
        getProtocolApproveService().grantAdminApproval(document.getProtocol(), actionBean);
        generateActionCorrespondence(IacucProtocolActionType.ADMINISTRATIVE_APPROVAL, protocolForm.getProtocolDocument().getProtocol());
        saveReviewComments(protocolForm, (IacucReviewCommentsBean) actionBean.getReviewCommentsBean());
        recordProtocolActionSuccess("Administrative Approval");
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean((IacucProtocol) document.getProtocol(), IacucProtocolActionType.ADMINISTRATIVE_APPROVAL, "Admin Approval");
        protocolForm.getActionHelper().setProtocolCorrespondence(getProtocolCorrespondence(protocolForm, IacucConstants.PROTOCOL_TAB, notificationBean, false));
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }

    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#performRequestAction(org.kuali.kra.iacuc.IacucProtocolForm, java.lang.String)
     */
    public String performRequestAction(IacucProtocolForm protocolForm, String taskName) throws Exception {
        IacucProtocolDocument document = (IacucProtocolDocument)protocolForm.getProtocolDocument();

        IacucProtocolRequestAction requestAction = IacucProtocolRequestAction.valueOfTaskName(taskName);
        IacucProtocolRequestBean requestBean = getProtocolRequestBean(protocolForm, taskName);
        if (requestBean != null) {
            boolean valid = applyRules(new IacucProtocolRequestEvent<IacucProtocolRequestRule>(document, requestAction.getErrorPath(), requestBean));
            requestBean.setAnswerHeaders(getAnswerHeaders(protocolForm, requestAction.getActionTypeCode()));
            valid &= isMandatoryQuestionnaireComplete(requestBean.getAnswerHeaders(), "actionHelper." + requestAction.getBeanName() + ".datavalidation");
            if (valid) {
                getProtocolRequestService().submitRequest(protocolForm.getIacucProtocolDocument().getIacucProtocol(), requestBean);            
                generateActionCorrespondence(requestBean.getProtocolActionTypeCode(), protocolForm.getProtocolDocument().getProtocol());
                recordProtocolActionSuccess(requestAction.getActionName());
                return sendRequestNotification(protocolForm, requestBean.getProtocolActionTypeCode(), requestBean.getReason(), IacucConstants.PROTOCOL_ACTIONS_TAB);
            }
        }
        return Constants.MAPPING_BASIC;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#disapproveProtocol(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String disapproveProtocol(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        IacucProtocol protocol = (IacucProtocol) document.getProtocol();
        IacucProtocolGenericActionBean actionBean = (IacucProtocolGenericActionBean) protocolForm.getActionHelper().getProtocolDisapproveBean();
        getProtocolGenericActionService().disapprove(protocol, actionBean);
        generateActionCorrespondence(IacucProtocolActionType.IACUC_DISAPPROVED, protocolForm.getProtocolDocument().getProtocol());
        saveReviewComments(protocolForm, (IacucReviewCommentsBean) actionBean.getReviewCommentsBean());
        recordProtocolActionSuccess(ACTION_NAME_DISAPPROVE);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean((IacucProtocol) protocolForm.getProtocolDocument().getProtocol(), IacucProtocolActionType.IACUC_DISAPPROVED, "Disapproved");
        protocolForm.getActionHelper().setProtocolCorrespondence(getProtocolCorrespondence(protocolForm, IacucConstants.PROTOCOL_TAB, notificationBean, false));
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#expireProtocol(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String expireProtocol(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        IacucProtocol protocol = (IacucProtocol) document.getProtocol();
        IacucProtocolGenericActionBean actionBean = (IacucProtocolGenericActionBean) protocolForm.getActionHelper().getProtocolExpireBean();
        getProtocolGenericActionService().expire(protocol, actionBean);
        generateActionCorrespondence(IacucProtocolActionType.EXPIRED, protocolForm.getProtocolDocument().getProtocol());
        saveReviewComments(protocolForm, (IacucReviewCommentsBean) actionBean.getReviewCommentsBean());
        recordProtocolActionSuccess(ACTION_NAME_EXPIRE);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean((IacucProtocol) protocolForm.getProtocolDocument().getProtocol(), IacucProtocolActionType.EXPIRED, "Expired");
        protocolForm.getActionHelper().setProtocolCorrespondence(getProtocolCorrespondence(protocolForm, IacucConstants.PROTOCOL_TAB, notificationBean, false));
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#terminateProtocol(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String terminateProtocol(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        IacucProtocol protocol = (IacucProtocol) document.getProtocol();
        IacucProtocolGenericActionBean actionBean = (IacucProtocolGenericActionBean) protocolForm.getActionHelper().getProtocolTerminateBean();
        getProtocolGenericActionService().terminate(protocol, actionBean);
        generateActionCorrespondence(IacucProtocolActionType.TERMINATED, protocolForm.getProtocolDocument().getProtocol());
        saveReviewComments(protocolForm, (IacucReviewCommentsBean) actionBean.getReviewCommentsBean());
        recordProtocolActionSuccess(ACTION_NAME_TERMINATE);
        IacucProtocolNotificationRequestBean notificationBean = 
                new IacucProtocolNotificationRequestBean((IacucProtocol) protocolForm.getProtocolDocument().getProtocol(), IacucProtocolActionType.TERMINATED, "Terminated");
        protocolForm.getActionHelper().setProtocolCorrespondence(getProtocolCorrespondence(protocolForm, IacucConstants.PROTOCOL_TAB, notificationBean, false));
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#suspendProtocol(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String suspendProtocol(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        IacucProtocol protocol = (IacucProtocol) document.getProtocol();
        IacucProtocolGenericActionBean actionBean = (IacucProtocolGenericActionBean) protocolForm.getActionHelper().getProtocolSuspendBean();
        getProtocolGenericActionService().suspend(protocol, actionBean);
        generateActionCorrespondence(IacucProtocolActionType.SUSPENDED, protocolForm.getProtocolDocument().getProtocol());
        saveReviewComments(protocolForm, (IacucReviewCommentsBean) actionBean.getReviewCommentsBean());
        recordProtocolActionSuccess(ACTION_NAME_SUSPEND);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean((IacucProtocol) protocolForm.getProtocolDocument().getProtocol(), IacucProtocolActionType.SUSPENDED, "Suspended");
        protocolForm.getActionHelper().setProtocolCorrespondence(getProtocolCorrespondence(protocolForm, IacucConstants.PROTOCOL_TAB, notificationBean, false));
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#acknowledgement(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String acknowledgement(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        IacucProtocol protocol = (IacucProtocol) document.getProtocol();
        IacucProtocolGenericActionBean actionBean = ((IacucActionHelper) protocolForm.getActionHelper()).getIacucAcknowledgeBean();
        getProtocolGenericActionService().iacucAcknowledgement(protocol, actionBean);
        generateActionCorrespondence(IacucProtocolActionType.IACUC_ACKNOWLEDGEMENT, protocolForm.getProtocolDocument().getProtocol());
        saveReviewComments(protocolForm, (IacucReviewCommentsBean) actionBean.getReviewCommentsBean());
        recordProtocolActionSuccess(ACTION_NAME_ACKNOWLEDGEMENT);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(protocol, IacucProtocolActionType.IACUC_ACKNOWLEDGEMENT, "IACUC Acknowledgement");
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_ACTIONS_TAB);
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#hold(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String hold(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        IacucProtocol protocol = (IacucProtocol) document.getProtocol();
        IacucProtocolGenericActionBean actionBean = ((IacucActionHelper) protocolForm.getActionHelper()).getIacucProtocolHoldBean();
        getProtocolGenericActionService().iacucHold(protocol, actionBean);
        generateActionCorrespondence(IacucProtocolActionType.HOLD, protocolForm.getProtocolDocument().getProtocol());
        saveReviewComments(protocolForm, (IacucReviewCommentsBean) actionBean.getReviewCommentsBean());
        recordProtocolActionSuccess(ACTION_NAME_HOLD);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean((IacucProtocol) protocolForm.getProtocolDocument().getProtocol(), IacucProtocolActionType.HOLD, "IACUC Hold");
        protocolForm.getActionHelper().setProtocolCorrespondence(getProtocolCorrespondence(protocolForm, IacucConstants.PROTOCOL_TAB, notificationBean, false));
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }

    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#liftHold(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String liftHold(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        IacucProtocol protocol = (IacucProtocol) document.getProtocol();
        IacucProtocolGenericActionBean actionBean = ((IacucActionHelper) protocolForm.getActionHelper()).getIacucProtocolLiftHoldBean();
        getProtocolGenericActionService().iacucLiftHold(protocol, actionBean);
        generateActionCorrespondence(IacucProtocolActionType.LIFT_HOLD, protocolForm.getProtocolDocument().getProtocol());
        saveReviewComments(protocolForm, (IacucReviewCommentsBean) actionBean.getReviewCommentsBean());
        recordProtocolActionSuccess(ACTION_NAME_LIFT_HOLD);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(protocol, IacucProtocolActionType.LIFT_HOLD, "IACUC Lift Hold");
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#returnForSMR(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String returnForSMR(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        IacucProtocol protocol = (IacucProtocol) document.getProtocol();
        IacucProtocolGenericActionBean actionBean = (IacucProtocolGenericActionBean) protocolForm.getActionHelper().getProtocolSMRBean();
        ProtocolDocumentBase newDocument = getProtocolGenericActionService().returnForSMR(protocol, actionBean);
        generateActionCorrespondence(IacucProtocolActionType.IACUC_MINOR_REVISIONS_REQUIRED, protocolForm.getProtocolDocument().getProtocol());
        saveReviewComments(protocolForm, (IacucReviewCommentsBean) actionBean.getReviewCommentsBean());
        refreshAfterProtocolAction(protocolForm, newDocument.getDocumentNumber(), ACTION_NAME_SMR, false);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(protocol, IacucProtocolActionType.IACUC_MINOR_REVISIONS_REQUIRED, "Minor Revisions Required");
        protocolForm.getActionHelper().setProtocolCorrespondence(getProtocolCorrespondence(protocolForm, IacucConstants.PROTOCOL_TAB, notificationBean, false));
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }

    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#returnForSRR(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String returnForSRR(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        IacucProtocol protocol = (IacucProtocol) document.getProtocol();
        IacucProtocolGenericActionBean actionBean = (IacucProtocolGenericActionBean) protocolForm.getActionHelper().getProtocolSRRBean();
        ProtocolDocumentBase newDocument = getProtocolGenericActionService().returnForSRR(protocol, actionBean);
        generateActionCorrespondence(IacucProtocolActionType.IACUC_MAJOR_REVISIONS_REQUIRED, protocolForm.getProtocolDocument().getProtocol());
        saveReviewComments(protocolForm, (IacucReviewCommentsBean) actionBean.getReviewCommentsBean());
        refreshAfterProtocolAction(protocolForm, newDocument.getDocumentNumber(), ACTION_NAME_SRR, false);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(protocol, IacucProtocolActionType.IACUC_MAJOR_REVISIONS_REQUIRED, "Major Revisions Required");
        protocolForm.getActionHelper().setProtocolCorrespondence(getProtocolCorrespondence(protocolForm, IacucConstants.PROTOCOL_TAB, notificationBean, false));
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#returnToPI(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String returnToPI(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        IacucProtocol protocol = (IacucProtocol) document.getProtocol();
        IacucProtocolGenericActionBean actionBean = (IacucProtocolGenericActionBean) protocolForm.getActionHelper().getProtocolReturnToPIBean();
        ProtocolDocumentBase newDocument = getProtocolGenericActionService().returnToPI(protocol, actionBean);
        generateActionCorrespondence(IacucProtocolActionType.RETURNED_TO_PI, protocolForm.getProtocolDocument().getProtocol());
        saveReviewComments(protocolForm, (IacucReviewCommentsBean) actionBean.getReviewCommentsBean());
        refreshAfterProtocolAction(protocolForm, newDocument.getDocumentNumber(), ACTION_NAME_RETURN_TO_PI, false);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(protocol, IacucProtocolActionType.RETURNED_TO_PI, "Return To PI");
        protocolForm.getActionHelper().setProtocolCorrespondence(getProtocolCorrespondence(protocolForm, IacucConstants.PROTOCOL_TAB, notificationBean, false));
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }

    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#deactivate(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String deactivate(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        IacucProtocol protocol = (IacucProtocol) document.getProtocol();
        IacucActionHelper actionHelper = (IacucActionHelper)protocolForm.getActionHelper();
        IacucProtocolGenericActionBean actionBean = actionHelper.getIacucProtocolDeactivateBean();
        getProtocolGenericActionService().iacucDeactivate(protocol, actionBean);
        generateActionCorrespondence(IacucProtocolActionType.DEACTIVATED, protocolForm.getProtocolDocument().getProtocol());
        saveReviewComments(protocolForm, (IacucReviewCommentsBean) actionBean.getReviewCommentsBean());
        recordProtocolActionSuccess(ACTION_NAME_DEACTIVATED);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(protocol, IacucProtocolActionType.DEACTIVATED, "Deactivated");
        protocolForm.getActionHelper().setProtocolCorrespondence(getProtocolCorrespondence(protocolForm, IacucConstants.PROTOCOL_TAB, notificationBean, false));
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#openProtocolForAdminCorrection(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String openProtocolForAdminCorrection(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        IacucProtocol protocol = (IacucProtocol) document.getProtocol();
        document.getProtocol().setCorrectionMode(true); 
        protocolForm.getProtocolHelper().prepareView();
        IacucAdminCorrectionBean adminCorrectionBean = (IacucAdminCorrectionBean)protocolForm.getActionHelper().getProtocolAdminCorrectionBean();
        document.updateProtocolStatus(IacucProtocolActionType.ADMINISTRATIVE_CORRECTION, adminCorrectionBean.getComments());
        generateActionCorrespondence(IacucProtocolActionType.ADMINISTRATIVE_CORRECTION, protocolForm.getProtocolDocument().getProtocol());
        recordProtocolActionSuccess(ACTION_NAME_MANAGE_ADMINISTRATIVE_CORRECTION);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(protocol, IacucProtocolActionType.ADMINISTRATIVE_CORRECTION, "Administrative Correction");
        protocolForm.getActionHelper().setProtocolCorrespondence(getProtocolCorrespondence(protocolForm, IacucConstants.PROTOCOL_TAB, notificationBean, false));
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#abandon(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String abandon(IacucProtocolForm protocolForm) throws Exception {
        getProtocolAbandonService().abandonProtocol(protocolForm.getProtocolDocument().getProtocol(),
                protocolForm.getActionHelper().getProtocolAbandonBean());
        generateActionCorrespondence(IacucProtocolActionType.IACUC_ABANDON, protocolForm.getProtocolDocument().getProtocol());
        protocolForm.getProtocolHelper().prepareView();
        recordProtocolActionSuccess(ACTION_NAME_RECORD_ABANDON);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(protocolForm.getIacucProtocolDocument().getIacucProtocol(),IacucProtocolActionType.IACUC_ABANDON, "Abandon");
        protocolForm.getActionHelper().setProtocolCorrespondence(getProtocolCorrespondence(protocolForm, IacucConstants.PROTOCOL_ACTIONS_TAB, notificationBean, false));
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }

    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#modifySubmissionAction(org.kuali.kra.iacuc.IacucProtocolForm, java.util.List)
     */
    public String modifySubmissionAction(IacucProtocolForm protocolForm, List<ProtocolReviewerBeanBase> reviewers) throws Exception {
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        IacucProtocol protocol = (IacucProtocol) document.getProtocol();
        IacucProtocolModifySubmissionBean actionBean = ((IacucActionHelper) protocolForm.getActionHelper()).getIacucProtocolModifySubmissionBean();
        actionBean.setReviewers(reviewers);
        getModifySubmissionService().modifySubmission(document, actionBean, reviewers);
        recordProtocolActionSuccess(ACTION_NAME_MODIFY_SUBMISSION);
        performNotificationRendering(protocolForm, reviewers);
        IacucProtocolNotificationRenderer assignRenderer = new IacucProtocolNotificationRenderer(protocol);
        IacucProtocolNotificationContext assignContext = new IacucProtocolNotificationContext(protocol, null, 
                IacucProtocolActionType.MODIFY_PROTOCOL_SUBMISSION, "Modified", assignRenderer);
        getNotificationService().sendNotificationAndPersist(assignContext, new IacucProtocolNotification(), protocol);
        protocolForm.setReinitializeModifySubmissionFields(true);
        generateActionCorrespondence(IacucProtocolActionType.MODIFY_PROTOCOL_SUBMISSION, protocolForm.getProtocolDocument().getProtocol());
        return Constants.MAPPING_BASIC;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#tableProtocol(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String tableProtocol(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        IacucProtocol protocol = (IacucProtocol) document.getProtocol();
        IacucProtocolTableBean actionBean = ((IacucActionHelper) protocolForm.getActionHelper()).getIacucProtocolTableBean();
        IacucProtocolDocument pd = getProtocolTableService().tableProtocol(protocol, actionBean);
        IacucProtocol newIacucProtocol = (IacucProtocol)pd.getProtocol();
        generateActionCorrespondence(IacucProtocolActionType.TABLED, newIacucProtocol);
        refreshAfterProtocolAction(protocolForm, pd.getDocumentNumber(), ACTION_NAME_TABLE_PROTOCOL, false);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(newIacucProtocol, IacucProtocolActionType.TABLED, "Tabled");
        ProtocolCorrespondence newProtocolCorrespondence = getProtocolCorrespondence(newIacucProtocol, IacucConstants.PROTOCOL_TAB, notificationBean, false);
        protocolForm.getActionHelper().setProtocolCorrespondence(newProtocolCorrespondence);
        synchronizeCorrespondenceAndNotification(protocol, newProtocolCorrespondence, notificationBean, protocolForm, newIacucProtocol);
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#administrativelyWithdrawProtocol(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String administrativelyWithdrawProtocol(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        IacucProtocol protocol = (IacucProtocol) document.getProtocol();
        boolean isVersion = isVersion(protocol);
        ProtocolDocumentBase pd = getProtocolWithdrawService().administrativelyWithdraw(protocol, protocolForm.getActionHelper().getProtocolAdminWithdrawBean());
        IacucProtocol newIacucProtocol = (IacucProtocol)pd.getProtocol();
        generateActionCorrespondence(IacucProtocolActionType.ADMINISTRATIVELY_WITHDRAWN, newIacucProtocol);
        refreshAfterProtocolAction(protocolForm, pd.getDocumentNumber(), ACTION_NAME_ADMINISTRATIVELY_WITHDRAW, false);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(newIacucProtocol, IacucProtocolActionType.ADMINISTRATIVELY_WITHDRAWN, "Administratively Withdrawn");
        ProtocolCorrespondence newProtocolCorrespondence = getProtocolCorrespondence(newIacucProtocol, IacucConstants.PROTOCOL_TAB, notificationBean, false);
        protocolForm.getActionHelper().setProtocolCorrespondence(newProtocolCorrespondence);
        synchronizeWithdrawProcess(isVersion, protocol, newProtocolCorrespondence, notificationBean, protocolForm, newIacucProtocol);
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#withdrawProtocol(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String withdrawProtocol(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocolDocument document = (IacucProtocolDocument) protocolForm.getProtocolDocument();
        IacucProtocol protocol = (IacucProtocol) document.getProtocol();
        boolean isVersion = isVersion(protocol);
        ProtocolDocumentBase pd = getProtocolWithdrawService().withdraw(protocol, protocolForm.getActionHelper().getProtocolWithdrawBean());
        IacucProtocol newIacucProtocol = (IacucProtocol)pd.getProtocol();
        generateActionCorrespondence(IacucProtocolActionType.IACUC_WITHDRAWN, newIacucProtocol);
        refreshAfterProtocolAction(protocolForm, pd.getDocumentNumber(), ACTION_NAME_WITHDRAW, false);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(newIacucProtocol, IacucProtocolActionType.IACUC_WITHDRAWN, "Withdrawn");
        ProtocolCorrespondence newProtocolCorrespondence = getProtocolCorrespondence(newIacucProtocol, IacucConstants.PROTOCOL_TAB, notificationBean, false);
        protocolForm.getActionHelper().setProtocolCorrespondence(newProtocolCorrespondence);
        synchronizeWithdrawProcess(isVersion, protocol, newProtocolCorrespondence, notificationBean, protocolForm, newIacucProtocol);
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }
    
    /**
     * This method is to check whether we have created a version during withdraw process
     * If there is a new version, make sure we have linked correspondence and notification to both previous and new protocol
     * during withdraw action
     * @param previousProtocol
     * @param newProtocolCorrespondence
     * @param notificationBean
     * @param protocolForm
     * @param currentProtocol
     */
    private void synchronizeWithdrawProcess(boolean isVersion, IacucProtocol previousProtocol, ProtocolCorrespondence newProtocolCorrespondence, 
            IacucProtocolNotificationRequestBean notificationBean, IacucProtocolForm protocolForm, IacucProtocol currentProtocol) {
        if(isVersion) {
            synchronizeCorrespondenceAndNotification(previousProtocol, newProtocolCorrespondence, notificationBean, protocolForm, currentProtocol);
        }
    }
    
    /**
     * This method is to make sure we have linked correspondence and notification to both previous and versioned protocol
     * @param isVersion
     * @param previousProtocol
     * @param newProtocolCorrespondence
     * @param notificationBean
     * @param protocolForm
     * @param currentProtocol
     */
    private void synchronizeCorrespondenceAndNotification(IacucProtocol previousProtocol, ProtocolCorrespondence newProtocolCorrespondence, 
            IacucProtocolNotificationRequestBean notificationBean, IacucProtocolForm protocolForm, IacucProtocol currentProtocol) {
        ProtocolNotificationContextBase context = getProtocolNotificationContextHook(notificationBean, protocolForm);
        ProtocolBase notificationProtocol = null;
        if(newProtocolCorrespondence != null) {
            getProtocolActionCorrespondenceGenerationService().attachProtocolCorrespondence(previousProtocol, newProtocolCorrespondence.getCorrespondence(), 
                    newProtocolCorrespondence.getProtoCorrespTypeCode());
            notificationProtocol = previousProtocol;
        }else {
            notificationProtocol = currentProtocol;
        }
        getNotificationService().sendNotificationAndPersist(context, getProtocolNotificationInstanceHook(), notificationProtocol);
    }
    
    private boolean isVersion(IacucProtocol protocol) {
        boolean isVersion = IacucProtocolStatus.IN_PROGRESS.equals(protocol.getProtocolStatusCode()) ||
        IacucProtocolStatus.SUBMITTED_TO_IACUC.equals(protocol.getProtocolStatusCode()) ||
        IacucProtocolStatus.TABLED.equals(protocol.getProtocolStatusCode());
        return isVersion;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#notifyProtocol(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String notifyProtocol(IacucProtocolForm protocolForm) throws Exception {
        String returnPath = Constants.MAPPING_BASIC;
        IacucActionHelper actionHelper = (IacucActionHelper) protocolForm.getActionHelper();
        actionHelper.getIacucProtocolNotifyIacucBean().setAnswerHeaders(getAnswerHeaders(protocolForm, IacucProtocolActionType.NOTIFY_IACUC));
        if (isMandatoryQuestionnaireComplete(actionHelper.getIacucProtocolNotifyIacucBean().getAnswerHeaders(), "actionHelper.protocolNotifyIacucBean.datavalidation")) {
            getProtocolNotifyService().submitIacucNotification((IacucProtocol)protocolForm.getProtocolDocument().getProtocol(),
                    actionHelper.getIacucProtocolNotifyIacucBean());
            protocolForm.getQuestionnaireHelper().setAnswerHeaders(new ArrayList<AnswerHeader>());
            protocolForm.setReinitializeModifySubmissionFields(true);
            LOG.info("notifyIacucProtocol " + protocolForm.getProtocolDocument().getDocumentNumber());
            generateActionCorrespondence(IacucProtocolActionType.NOTIFY_IACUC, protocolForm.getProtocolDocument().getProtocol());
            recordProtocolActionSuccess(ACTION_NAME_NOTIFY);
            IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean((IacucProtocol)protocolForm.getProtocolDocument().getProtocol(),IacucProtocolActionType.NOTIFY_IACUC, "Notify IACUC");
            returnPath = getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_ACTIONS_TAB);
        }
        return returnPath;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#sendReviewDeterminationNotificationAction(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String sendReviewDeterminationNotificationAction(IacucProtocolForm protocolForm) throws Exception {
        String returnPath = Constants.MAPPING_BASIC;
        IacucActionHelper actionHelper = (IacucActionHelper) protocolForm.getActionHelper();
        IacucProtocol protocol = (IacucProtocol) protocolForm.getProtocolDocument().getProtocol();
        IacucProtocolModifySubmissionBean bean = actionHelper.getIacucProtocolModifySubmissionBean();
        Date dueDate = bean.getDueDate();
        IacucProtocolReviewDeterminationNotificationRenderer renderer = new IacucProtocolReviewDeterminationNotificationRenderer(protocol, dueDate);
        IacucProtocolNotificationContext context = new IacucProtocolNotificationContext(protocol, IacucProtocolActionType.REVIEW_TYPE_DETERMINATION, "Review Type Determination", renderer);
        if (protocolForm.getNotificationHelper().getPromptUserForNotificationEditor(context)) {
            protocolForm.getNotificationHelper().initializeDefaultValues(context);
            returnPath = IacucConstants.NOTIFICATION_EDITOR;
        } else {
            getNotificationService().sendNotificationAndPersist(context, new IacucProtocolNotification(), protocol);
        }
        generateActionCorrespondence(IacucProtocolActionType.REVIEW_TYPE_DETERMINATION, protocolForm.getProtocolDocument().getProtocol());
        recordProtocolActionSuccess(ACTION_NAME_REVIEW_TYPE_DETERMINATION);
        return returnPath;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#administrativelyMarkIncompleteProtocol(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String administrativelyMarkIncompleteProtocol(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocol protocol = (IacucProtocol) protocolForm.getProtocolDocument().getProtocol();
        boolean isVersion = isVersion(protocol);
        ProtocolDocumentBase pd = getProtocolWithdrawService().administrativelyMarkIncomplete(protocol, protocolForm.getActionHelper().getProtocolAdminIncompleteBean());
        IacucProtocol newIacucProtocol = (IacucProtocol)pd.getProtocol();
        generateActionCorrespondence(IacucProtocolActionType.ADMINISTRATIVELY_INCOMPLETE, newIacucProtocol);
        refreshAfterProtocolAction(protocolForm, pd.getDocumentNumber(), ACTION_NAME_ADMINISTRATIVELY_INCOMPLETE, false);
        IacucProtocolNotificationRequestBean notificationBean = new IacucProtocolNotificationRequestBean(newIacucProtocol, IacucProtocolActionType.ADMINISTRATIVELY_INCOMPLETE, "Administratively Marked Incomplete");
        ProtocolCorrespondence newProtocolCorrespondence = getProtocolCorrespondence(newIacucProtocol, IacucConstants.PROTOCOL_TAB, notificationBean, false);
        protocolForm.getActionHelper().setProtocolCorrespondence(newProtocolCorrespondence);
        synchronizeWithdrawProcess(isVersion, protocol, newProtocolCorrespondence, notificationBean, protocolForm, newIacucProtocol);
        return getRedirectPathAfterProtocolAction(protocolForm, notificationBean, IacucConstants.PROTOCOL_TAB);
    }

    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#submitCommitteeDecision(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    @SuppressWarnings("deprecation")
    public String submitCommitteeDecision(IacucProtocolForm protocolForm) throws Exception {
        IacucCommitteeDecision actionBean = (IacucCommitteeDecision) protocolForm.getActionHelper().getCommitteeDecision();
        getCommitteeDecisionService().processCommitteeDecision(protocolForm.getProtocolDocument().getProtocol(), actionBean);
        saveReviewComments(protocolForm, actionBean.getReviewCommentsBean());
        protocolForm.getTabStates().put(":" + WebUtils.generateTabKey(motionTypeMap.get(actionBean.getMotionTypeCode())), "OPEN");
        recordProtocolActionSuccess(ACTION_NAME_RECORD_COMMITTEE_DECISION);
        generateActionCorrespondence(IacucProtocolActionType.RECORD_COMMITTEE_DECISION, protocolForm.getProtocolDocument().getProtocol());
        return Constants.MAPPING_BASIC;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#assignCommittee(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String assignCommittee(IacucProtocolForm protocolForm) throws Exception {
        IacucActionHelper actionHelper = (IacucActionHelper)protocolForm.getActionHelper();
        IacucProtocolAssignCmtBean actionBean = actionHelper.getProtocolAssignCmtBean();
        if( protocolForm.getProtocolDocument().getProtocol().getProtocolSubmission() != null) {
            getAssignToCmtService().assignToCommittee(protocolForm.getProtocolDocument().getProtocol(), actionBean);
            recordProtocolActionSuccess(ACTION_NAME_ASSIGN_TO_COMMITTEE);
            protocolForm.setReinitializeModifySubmissionFields(true);
            generateActionCorrespondence(IacucProtocolActionType.ASSIGNED_TO_COMMITTEE, protocolForm.getProtocolDocument().getProtocol());
        }
        return Constants.MAPPING_BASIC;
    }
    
    public String performNotificationRendering(IacucProtocolForm protocolForm, List<ProtocolReviewerBeanBase> beans) {
        String returnPath = Constants.MAPPING_BASIC;
        IacucProtocol protocol = (IacucProtocol) protocolForm.getProtocolDocument().getProtocol();
        IacucProtocolAssignReviewerNotificationRenderer renderer = new IacucProtocolAssignReviewerNotificationRenderer(protocol, "added");
        List<ProtocolNotificationRequestBeanBase> addReviewerNotificationBeans = getNotificationRequestBeans(beans,
                IacucProtocolReviewerBean.CREATE, true);
        List<ProtocolNotificationRequestBeanBase> removeReviewerNotificationBeans = getNotificationRequestBeans(beans,
                IacucProtocolReviewerBean.REMOVE, false);
        if (!CollectionUtils.isEmpty(addReviewerNotificationBeans)) {
            ProtocolNotificationRequestBeanBase notificationBean = addReviewerNotificationBeans.get(0);
            IacucProtocolNotificationContext context = new IacucProtocolNotificationContext((IacucProtocol)notificationBean.getProtocol(),
                (IacucProtocolOnlineReview)notificationBean.getProtocolOnlineReview(), notificationBean.getActionType(),
                notificationBean.getDescription(), renderer);
            if (protocolForm.getNotificationHelper().getPromptUserForNotificationEditor(context)) {
                boolean sendNotification = checkToSendNotification(protocolForm, renderer, addReviewerNotificationBeans, IacucConstants.PROTOCOL_ACTIONS_TAB); 
                returnPath = sendNotification ? IacucConstants.NOTIFICATION_EDITOR : IacucConstants.PROTOCOL_ACTIONS_TAB;
                if (!CollectionUtils.isEmpty(removeReviewerNotificationBeans)) {
                    GlobalVariables.getUserSession().addObject("removeReviewer", removeReviewerNotificationBeans);
                }
            }
        }else {
            if (!CollectionUtils.isEmpty(removeReviewerNotificationBeans)) {
                renderer = new IacucProtocolAssignReviewerNotificationRenderer(protocol, "removed");
                ProtocolNotificationRequestBeanBase notificationBean = removeReviewerNotificationBeans.get(0);
                IacucProtocolNotificationContext context = new IacucProtocolNotificationContext(protocol,
                    (IacucProtocolOnlineReview)notificationBean.getProtocolOnlineReview(), notificationBean.getActionType(),
                    notificationBean.getDescription(), renderer);
                if (protocolForm.getNotificationHelper().getPromptUserForNotificationEditor(context)) {
                    boolean sendNotification = checkToSendNotification(protocolForm, renderer, removeReviewerNotificationBeans, IacucConstants.PROTOCOL_ACTIONS_TAB); 
                    returnPath = sendNotification ? IacucConstants.NOTIFICATION_EDITOR : IacucConstants.PROTOCOL_ACTIONS_TAB;
                }
            }
        }
        return returnPath;
    }
    
    private List<ProtocolNotificationRequestBeanBase> getNotificationRequestBeans(List<ProtocolReviewerBeanBase> beans, String actionFlag, boolean notNullFlag) {
        List<ProtocolNotificationRequestBeanBase> notificationRequestBeans = new ArrayList<ProtocolNotificationRequestBeanBase>();
        for (ProtocolReviewerBeanBase bean : beans) {
            if (StringUtils.equals(actionFlag, bean.getActionFlag()) && (notNullFlag == !StringUtils.isEmpty(bean.getReviewerTypeCode()))) {
                notificationRequestBeans.add(bean.getNotificationRequestBean());
            }
        }
        return notificationRequestBeans;
    }

    protected boolean checkToSendNotification(ProtocolFormBase protocolForm, IacucProtocolNotificationRenderer renderer, List<ProtocolNotificationRequestBeanBase> notificationRequestBeans, String promptAfterNotification) {
        IacucProtocolNotificationContext context = new IacucProtocolNotificationContext((IacucProtocol) notificationRequestBeans.get(0).getProtocol(),
            (IacucProtocolOnlineReview)notificationRequestBeans.get(0).getProtocolOnlineReview(), notificationRequestBeans.get(0).getActionType(),
            notificationRequestBeans.get(0).getDescription(), renderer);
        context.setPopulateRole(true);
        if (protocolForm.getNotificationHelper().getPromptUserForNotificationEditor(context)) {
            protocolForm.getNotificationHelper().initializeDefaultValues(context);
            List<NotificationTypeRecipient> notificationRecipients = protocolForm.getNotificationHelper()
                    .getNotificationRecipients();
            List<NotificationTypeRecipient> allRecipients = new ArrayList<NotificationTypeRecipient>();
            for (NotificationTypeRecipient recipient : notificationRecipients) {
                try {
                    NotificationTypeRecipient copiedRecipient = (NotificationTypeRecipient) ObjectUtils.deepCopy(recipient);
                    // populate role qualifier with proper context
                    context.populateRoleQualifiers(copiedRecipient);
                    allRecipients.add(copiedRecipient);
                }
                catch (Exception ex) {
                    LOG.error(ex.getMessage());
                }
            }
            int i = 1;
            // add all new reviewer to recipients
            while (notificationRequestBeans.size() > i) {
                context = new IacucProtocolNotificationContext((IacucProtocol) notificationRequestBeans.get(i).getProtocol(), 
                                                     (IacucProtocolOnlineReview) notificationRequestBeans.get(i).getProtocolOnlineReview(), 
                                                     notificationRequestBeans.get(i).getActionType(), 
                                                     notificationRequestBeans.get(i).getDescription(), renderer);
                context.setPopulateRole(true);
                protocolForm.getNotificationHelper().initializeDefaultValues(context);
                List<NotificationTypeRecipient> recipients = protocolForm.getNotificationHelper().getNotificationRecipients();

                for (NotificationTypeRecipient recipient : recipients) {
                    try {
                        /* NOTE : need to deepcopy here. If not, then all reviewer role will have same
                         * notification recipient object returned from service call
                         */
                        NotificationTypeRecipient copiedRecipient = (NotificationTypeRecipient) ObjectUtils.deepCopy(recipient);
                        context.populateRoleQualifiers(copiedRecipient);
                        allRecipients.add(copiedRecipient);
                    }
                    catch (Exception ex) {
                        LOG.error(ex.getMessage());
                    }
                }
                i++;
            }
            protocolForm.getNotificationHelper().setNotificationRecipients(allRecipients);
            if (promptAfterNotification == null) {
                context.setForwardName(RETURN_TO_HOLDING_PAGE);
            } else {
                context.setForwardName(promptAfterNotification);
            }
            return true;
        }else {
            return false;
        }
    }
    
    private IacucProtocolRequestBean getProtocolRequestBean(ProtocolFormBase protocolForm, String taskName) {
        IacucProtocolRequestBean protocolRequestBean = null;
        ProtocolActionBean protocolActionBean = getActionBean(protocolForm, taskName);
        if (protocolActionBean != null && protocolActionBean instanceof IacucProtocolRequestBean) {
            protocolRequestBean = (IacucProtocolRequestBean) protocolActionBean;
        }
        return protocolRequestBean;
    }
    
    /**
     * @see org.kuali.kra.iacuc.actions.IacucProtocolActionRequestService#withdrawRequestAction(org.kuali.kra.iacuc.IacucProtocolForm)
     */
    public String withdrawRequestAction(IacucProtocolForm protocolForm) throws Exception {
        IacucProtocolDocument document = protocolForm.getIacucProtocolDocument();
        IacucProtocol protocol = protocolForm.getIacucProtocolDocument().getIacucProtocol();
        IacucProtocolRequestAction requestAction = IacucProtocolRequestAction.valueOfTaskName(TaskName.IACUC_WITHDRAW_SUBMISSION);
        IacucProtocolRequestBean requestBean = getProtocolRequestBean(protocolForm, TaskName.IACUC_WITHDRAW_SUBMISSION);
        if (requestBean != null) {
            boolean valid = applyRules(new IacucProtocolRequestEvent<IacucProtocolRequestRule>(document, requestAction.getErrorPath(), requestBean));
            if (valid) {
                // find recently submitted action request and complete it
                List<ProtocolSubmissionBase> submissions = protocol.getProtocolSubmissions();
                ProtocolSubmissionBase submission = null;
                for (ProtocolSubmissionBase sub: submissions) {
                    if (requestSubmissionTypes.contains(sub.getSubmissionTypeCode())) {
                        submission = sub;
                    }
                }
                if (submission != null) {
                    submission.setSubmissionStatusCode(IacucProtocolSubmissionStatus.WITHDRAWN);
                    IacucProtocolAction protocolAction = new IacucProtocolAction(protocol, (IacucProtocolSubmission) submission, IacucProtocolActionType.IACUC_WITHDRAW_SUBMISSION);
                    protocolAction.setComments(requestBean.getReason());
                    protocolAction.setActionDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                    protocolAction.setSubmissionIdFk(submission.getSubmissionId());
                    protocolAction.setSubmissionNumber(submission.getSubmissionNumber());
                    document.getProtocol().getProtocolActions().add(protocolAction);
                    getProtocolActionService().updateProtocolStatus(protocolAction, document.getProtocol());
                    getBusinessObjectService().save(submission);
                    recordProtocolActionSuccess(requestAction.getActionName());
                    return sendRequestNotification(protocolForm, requestBean.getProtocolActionTypeCode(), requestBean.getReason(), IacucConstants.PROTOCOL_ACTIONS_TAB);
                }
            }
        }
        return Constants.MAPPING_BASIC;
    }
    
    @Override
    protected ProtocolTaskBase getProtocolTaskInstanceHook(String taskName, ProtocolBase protocol) {
        IacucProtocolTask task = new IacucProtocolTask(taskName, (IacucProtocol)protocol);
        return task;
    }

    @Override
    protected ProtocolActionsCorrespondenceBase getNewProtocolActionsCorrespondence(String protocolActionTypeCode) {
        return new IacucProtocolActionsCorrespondence(protocolActionTypeCode);
    }

    @Override
    protected Class<? extends ProtocolActionTypeBase> getProtocolActionTypeBOClassHook() {
        return IacucProtocolActionType.class;
    }

    @Override
    protected String getProtocolCreatedActionTypeHook() {
        return IacucProtocolActionType.IACUC_PROTOCOL_CREATED;
    }

    public IacucProtocolApproveService getProtocolApproveService() {
        return protocolApproveService;
    }

    public void setProtocolApproveService(IacucProtocolApproveService protocolApproveService) {
        this.protocolApproveService = protocolApproveService;
    }

    @Override
    protected Class<? extends ProtocolBase> getProtocolBOClassHook() {
        return IacucProtocol.class;
    }

    @Override
    protected ProtocolTaskBase getProtocolGenericActionTaskInstanceHook(String genericActionName,
            ProtocolBase protocol) {
        IacucProtocolTask task = new IacucProtocolTask(TaskName.GENERIC_IACUC_PROTOCOL_ACTION, (IacucProtocol)protocol, genericActionName);
        return task;
    }

    @Override
    protected String getProtocolRejectedInRoutingActionTypeHook() {
        return IacucProtocolActionType.REJECTED_IN_ROUTING;
    }

    @Override
    protected String getProtocolRecalledInRoutingActionTypeHook() {
        //This action type is not supported in IACUC
        return null;
    }

    public IacucProtocolSubmitActionService getProtocolSubmitActionService() {
        return protocolSubmitActionService;
    }

    public void setProtocolSubmitActionService(IacucProtocolSubmitActionService protocolSubmitActionService) {
        this.protocolSubmitActionService = protocolSubmitActionService;
    }

    public IacucProtocolAmendRenewService getProtocolAmendRenewService() {
        return protocolAmendRenewService;
    }

    public void setProtocolAmendRenewService(IacucProtocolAmendRenewService protocolAmendRenewService) {
        this.protocolAmendRenewService = protocolAmendRenewService;
    }

    public IacucProtocolActionService getProtocolActionService() {
        return protocolActionService;
    }

    public void setProtocolActionService(IacucProtocolActionService protocolActionService) {
        this.protocolActionService = protocolActionService;
    }

    @Override
    protected String getNotificationEditorHook() {
        return IacucConstants.NOTIFICATION_EDITOR;
    }

    @Override
    protected ProtocolNotification getProtocolNotificationInstanceHook() {
        return new IacucProtocolNotification();
    }

    @Override
    protected ProtocolNotificationContextBase getProtocolNotificationContextHook(
            ProtocolNotificationRequestBeanBase notificationRequestBean, ProtocolFormBase protocolForm) {
        IacucProtocolNotificationRenderer renderer = null;
        IacucProtocol protocol = (IacucProtocol)notificationRequestBean.getProtocol();
        if (StringUtils.equals(IacucProtocolActionType.NOTIFY_IACUC, notificationRequestBean.getActionType())) {
            renderer = new NotifyIacucNotificationRenderer(protocol, ((IacucActionHelper)protocolForm.getActionHelper()).getIacucProtocolNotifyIacucBean().getComment());
        } else if (StringUtils.equals(IacucProtocolActionType.IACUC_DELETED, notificationRequestBean.getActionType()) ||
                   StringUtils.equals(IacucProtocolActionType.IACUC_WITHDRAWN, notificationRequestBean.getActionType())) {
            renderer = new IacucProtocolWithReasonNotificationRenderer(protocol, protocolForm.getActionHelper().getProtocolDeleteBean());
        } else if (StringUtils.equals(IacucProtocolActionType.REQUEST_DEACTIVATE, notificationRequestBean.getActionType()) ||
                   StringUtils.equals(IacucProtocolActionType.REQUEST_LIFT_HOLD, notificationRequestBean.getActionType())) {
            IacucRequestActionNotificationBean requestNotificationRequestBean = (IacucRequestActionNotificationBean)notificationRequestBean; 
            renderer = new IacucProtocolRequestActionNotificationRenderer(protocol, requestNotificationRequestBean.getReason());
        } else if (StringUtils.equals(IacucProtocolActionType.IACUC_REQUEST_SUSPEND, notificationRequestBean.getActionType())) {
            IacucProtocolRequestBean iacucProtocolSuspendRequestBean = ((IacucActionHelper)protocolForm.getActionHelper()).getIacucProtocolSuspendRequestBean();
            renderer = new NotifyIacucNotificationRenderer(protocol, iacucProtocolSuspendRequestBean.getReason());
        } else {
            renderer = new IacucProtocolNotificationRenderer(protocol);
        }
        IacucProtocolNotificationContext context = new IacucProtocolNotificationContext(protocol, notificationRequestBean.getActionType(), notificationRequestBean.getDescription(), renderer);
        return context;
    }

    @Override
    protected Class<? extends ProtocolCorrespondence> getProtocolCorrespondenceBOClassHook() {
        return IacucProtocolCorrespondence.class;
    }

    public IacucProtocolAssignToAgendaService getProtocolAssignToAgendaService() {
        return protocolAssignToAgendaService;
    }

    public void setProtocolAssignToAgendaService(IacucProtocolAssignToAgendaService protocolAssignToAgendaService) {
        this.protocolAssignToAgendaService = protocolAssignToAgendaService;
    }

    public IacucProtocolReviewNotRequiredService getProtocolReviewNotRequiredService() {
        return protocolReviewNotRequiredService;
    }

    public void setProtocolReviewNotRequiredService(IacucProtocolReviewNotRequiredService protocolReviewNotRequiredService) {
        this.protocolReviewNotRequiredService = protocolReviewNotRequiredService;
    }

    @Override
    protected ModuleQuestionnaireBean getProtocolModuleQuestionnaireBeanInstanceHook(ProtocolFormBase protocolForm,
            String actionTypeCode) {
        return new IacucProtocolModuleQuestionnaireBean(CoeusModule.IACUC_PROTOCOL_MODULE_CODE, protocolForm.getProtocolDocument().getProtocol().getProtocolNumber() + "T", CoeusSubModule.PROTOCOL_SUBMISSION, actionTypeCode, false);
    }

    public IacucProtocolRequestService getProtocolRequestService() {
        return protocolRequestService;
    }

    public void setProtocolRequestService(IacucProtocolRequestService protocolRequestService) {
        this.protocolRequestService = protocolRequestService;
    }

    @Override
    protected ProtocolNotificationRequestBeanBase getRequestActionNotificationBeanInstanceHook(ProtocolBase protocol,
            String protocolActionTypeCode, String description, String reason) {
        return new IacucRequestActionNotificationBean((IacucProtocol)protocol, protocolActionTypeCode, description, reason);
    }

    @Override
    protected ProtocolQuestionnaireAuditRuleBase getProtocolQuestionnaireAuditRuleInstanceHook() {
        return new IacucProtocolQuestionnaireAuditRule();
    }

    public IacucProtocolGenericActionService getProtocolGenericActionService() {
        return protocolGenericActionService;
    }

    public void setProtocolGenericActionService(IacucProtocolGenericActionService protocolGenericActionService) {
        this.protocolGenericActionService = protocolGenericActionService;
    }

    public IacucProtocolAbandonService getProtocolAbandonService() {
        return protocolAbandonService;
    }

    public void setProtocolAbandonService(IacucProtocolAbandonService protocolAbandonService) {
        this.protocolAbandonService = protocolAbandonService;
    }

    public IacucProtocolModifySubmissionService getModifySubmissionService() {
        return modifySubmissionService;
    }

    public void setModifySubmissionService(IacucProtocolModifySubmissionService modifySubmissionService) {
        this.modifySubmissionService = modifySubmissionService;
    }

    public IacucProtocolTableService getProtocolTableService() {
        return protocolTableService;
    }

    public void setProtocolTableService(IacucProtocolTableService protocolTableService) {
        this.protocolTableService = protocolTableService;
    }

    public IacucProtocolWithdrawService getProtocolWithdrawService() {
        return protocolWithdrawService;
    }

    public void setProtocolWithdrawService(IacucProtocolWithdrawService protocolWithdrawService) {
        this.protocolWithdrawService = protocolWithdrawService;
    }

    public IacucProtocolNotifyIacucService getProtocolNotifyService() {
        return protocolNotifyService;
    }

    public void setProtocolNotifyService(IacucProtocolNotifyIacucService protocolNotifyService) {
        this.protocolNotifyService = protocolNotifyService;
    }

    public IacucCommitteeDecisionService getCommitteeDecisionService() {
        return committeeDecisionService;
    }

    public void setCommitteeDecisionService(IacucCommitteeDecisionService committeeDecisionService) {
        this.committeeDecisionService = committeeDecisionService;
    }

    public IacucProtocolAssignCmtService getAssignToCmtService() {
        return assignToCmtService;
    }

    public void setAssignToCmtService(IacucProtocolAssignCmtService assignToCmtService) {
        this.assignToCmtService = assignToCmtService;
    }

}
