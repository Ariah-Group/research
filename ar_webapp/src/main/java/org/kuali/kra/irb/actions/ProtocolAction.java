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
package org.kuali.kra.irb.actions;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.bo.CoeusModule;
import org.kuali.kra.bo.CoeusSubModule;
import org.kuali.kra.committee.service.CommitteeService;
import org.kuali.kra.common.committee.service.CommitteeServiceBase;
import org.kuali.kra.irb.Protocol;
import org.kuali.kra.irb.actions.submit.ProtocolSubmission;
import org.kuali.kra.irb.questionnaire.IrbSubmissionQuestionnaireHelper;
import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.protocol.actions.ProtocolActionBase;
import org.kuali.kra.protocol.questionnaire.ProtocolModuleQuestionnaireBeanBase;
import org.kuali.kra.protocol.questionnaire.ProtocolSubmissionQuestionnaireHelper;
import org.kuali.kra.questionnaire.answer.AnswerHeader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * This class manages all the attributes needed to maintain a protocol action.
 */
public class ProtocolAction extends ProtocolActionBase {

    
    private static final String COMMENT_SUBMITTED_TO_IRB = "Submitted to IRB";

    private static final long serialVersionUID = -2148599171919464303L;

    public ProtocolAction() {
    }


    public ProtocolAction(Protocol protocol, ProtocolSubmission protocolSubmission, String protocolActionTypeCode) {
        super(protocol, protocolSubmission, protocolActionTypeCode);        
    }
    
    public ProtocolAction(Protocol protocol, String protocolActionTypeCode) {
        super(protocol, protocolActionTypeCode);
    }

    public void setQuestionnairePrintOptionFromHelper(ActionHelper actionHelper) {
        if (getSubmissionNumber() != null
                && !ProtocolActionType.SUBMIT_TO_IRB.equals(getProtocolActionTypeCode())) {
            if (getQuestionnaireHelper().getAnswerHeaders().isEmpty()) {
                setQuestionnairePrintOption(getQnPrintOptionForAction(getProtocolNumber(),
                                getSubmissionNumber().toString(), CoeusSubModule.PROTOCOL_SUBMISSION));
            }
        } else if (ProtocolActionType.SUBMIT_TO_IRB.equals(getProtocolActionTypeCode())
                && COMMENT_SUBMITTED_TO_IRB.equals(getComments())) {
            if (getProtocol().isAmendment() || getProtocol().isRenewal()) {
                setQuestionnairePrintOption(getQnPrintOptionForAction(getProtocolNumber(),
                                getSequenceNumber().toString(), CoeusSubModule.AMENDMENT_RENEWAL));

            } else {
                setQuestionnairePrintOption(getQnPrintOptionForAction(getProtocolNumber(),
                                getInitialSequence(this, ""), CoeusSubModule.ZERO_SUBMODULE));
            }
        } else if (ProtocolActionType.SUBMIT_TO_IRB.equals(getProtocolActionTypeCode()) && StringUtils.isNotBlank(getComments())
                                                            && (getComments().startsWith(COMMENT_PREFIX_AMENDMENT) || getComments().startsWith(COMMENT_PREFIX_RENEWAL))) {
            String amendmentRenewalNumber = getAmendmentRenewalNumber(getComments());
            setQuestionnairePrintOption(getQnPrintOptionForAction(getProtocolNumber() + amendmentRenewalNumber, 
                                        getInitialSequence(this, amendmentRenewalNumber), CoeusSubModule.AMENDMENT_RENEWAL));
        }
    }
    
    /*
     * get the sequence number of the protocol that the action initially created
     */
    private String getInitialSequence(ProtocolAction protocolAction, String amendmentRenewalNumber) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(PROTOCOL_NUMBER_FIELD_KEY, protocolAction.getProtocolNumber() + amendmentRenewalNumber);
        if (StringUtils.isBlank(amendmentRenewalNumber)) {
            fieldValues.put(ACTION_ID_FIELD_KEY, protocolAction.getActionId().toString());
        }
        else {
            fieldValues.put(SUBMISSION_NUMBER_FIELD_KEY, protocolAction.getSubmissionNumber().toString());
        }
        fieldValues.put(PROTOCOL_ACTION_TYPE_CODE_FIELD_KEY, ProtocolActionType.SUBMIT_TO_IRB);
        return ((List<ProtocolAction>) getBusinessObjectService().findMatchingOrderBy(ProtocolAction.class, fieldValues,
                PROTOCOL_ACTION_ID_FIELD_KEY, true)).get(0).getProtocol().getSequenceNumber().toString();
    }

    @Override
    protected Class<? extends CommitteeServiceBase> getCommitteeServiceClassHook() {
        return CommitteeService.class;
    }

    @Override
    protected String getCoeusModule() {
        return CoeusModule.IRB_MODULE_CODE;
    }


    @Override
    protected ProtocolSubmissionQuestionnaireHelper getProtocolSubmissionQuestionnaireHelperHook(ProtocolBase protocol, String actionTypeCode,
            String submissionNumber) {
        return new IrbSubmissionQuestionnaireHelper(protocol, actionTypeCode, submissionNumber, true);
    }
    
    @Override
    public ProtocolSubmissionQuestionnaireHelper getQuestionnaireHelper() {
        IrbSubmissionQuestionnaireHelper questionnaireHelper = (IrbSubmissionQuestionnaireHelper) super.getQuestionnaireHelper();
        
        if(StringUtils.equals(getProtocolActionTypeCode(),ProtocolActionType.SUBMIT_TO_IRB)) {
            //add ZERO_SUBMODULE answer headers. These headers do not get picked up by populateAnswers() above.
            ProtocolModuleQuestionnaireBeanBase protocolBaseQnBean = questionnaireHelper.getBaseProtocolModuleQuestionnaireBean(getSequenceNumber() == null ? null : getSequenceNumber().toString());
            List<AnswerHeader> protocolAnswerHeaders = questionnaireHelper.getQuestionnaireAnswerService().getQuestionnaireAnswer(protocolBaseQnBean);
            List<AnswerHeader> submissionAnswerHeaders = questionnaireHelper.getAnswerHeaders();
            submissionAnswerHeaders.addAll(protocolAnswerHeaders);
            questionnaireHelper.setAnswerHeaders(submissionAnswerHeaders);
            questionnaireHelper.resetHeaderLabels();
        }
        
        return questionnaireHelper;
    }

}
