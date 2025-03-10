/*
 * Copyright 2005-2014 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
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
package org.kuali.kra.s2s.util;

/**
 * 
 * This interface is used to store all constants for S2S classes
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public interface S2SConstants {

    public static final String HASH_ALGORITHM = "SHA-1";
    public static final String CONTENT_TYPE_OCTET_STREAM = "application/octet-stream";
    public static final String FORMVERSION_1_0 = "1.0";
    public static final String FORMVERSION_1_1 = "1.1";
    public static final String FORMVERSION_1_2 = "1.2";
    public static final String FORMVERSION_1_3 = "1.3";
    public static final String FORMVERSION_1_4 = "1.4";
    public static final String FORMVERSION_1_6 = "1.6";
    public static final String FORMVERSION_2_0 = "2.0";
    public static final String FORMVERSION_2_1 = "2.1";
    public static final String FORMVERSION_3_0 = "3.0";
    public static final String FORMVERSION_3_1 = "3.1";
    public static final String SPONSOR = "S2S";
    public static final String TASK_INFO = "taskInfo";
    public static final String VALUE_UNKNOWN = "Unknown";
//    public static final String FEDERAL_ID_NOT_FOUND = "-1";

    public static final String KEY_REVISION_CODE = "revisionCode";
    public static final String KEY_REVISION_OTHER_DESCRIPTION = "revisionOtherDescription";
    public static final String CORE_SCHEMA_VERSION_1_1 = "1.1";
    public static final String ERROR_MESSAGE = "Exception Occured";

    public static final int BUDGET_PERIOD_1 = 1;
    public static final int BUDGET_PERIOD_2 = 2;
    public static final int BUDGET_PERIOD_3 = 3;
    public static final int BUDGET_PERIOD_4 = 4;
    public static final int BUDGET_PERIOD_5 = 5;
    public static final String KEY_COST = "Cost";
    public static final String KEY_COSTSHARING = "CostSharing";

    public static final String YNQ_ANSWER = "answer";
    public static final String PROPOSAL_YNQ_ANSWER_Y = "Y";
    public static final String PROPOSAL_YNQ_ANSWER_NA = "X";
    public static final String PROPOSAL_YNQ_ANSWER_N = "N";
    public static final String YNQ_REVIEW_DATE = "reviewDate";
    public static final String YNQ_STATE_REVIEW_DATA = "stateReviewData";
    public static final String YNQ_STATE_NOT_SELECTED = "Not Selected";
    public static final String YNQ_STATE_NOT_COVERED = "Not Covered";

//    public static final String S2S_PROPERTY = "/S2SAuthentication.properties";
    public static final int DEFAULT_SSL_PORT = 446;
    public static final String PROTOCOL_TYPE = "https";
//    public static final String HOST = "host";
//    public static final String PORT = "port";

    public static final String STATUS_GRANTS_GOV_SUBMISSION_ERROR = "S2S Submission Error";
    public static final String STATUS_AGENCY_TRACKING_NUMBER_ASSIGNED = "Agency Tracking Number Assigned";
    public static final String STATUS_NO_RESPONSE_FROM_GRANTS_GOV = "No response from S2S Server";
    public static final String GRANTS_GOV_STATUS_MESSAGE = "S2S Submission in Process";
    public static final String GRANTS_GOV_COMMENTS_MESSAGE = "Trying to submit to S2S";
    public static final String GRANTS_GOV_SUBMISION_ERROR_MESSAGE="Error occured while submitting to S2S, Root Cause: ";
    public static final String GRANTS_GOV_SUBMISSION_MESSAGE="Submitted to S2S";
    public static final String GRANTS_GOV_PROCESSING_MESSAGE="S2S is processing the request";
    public static final String GRANTS_GOV_STIPEND_ERROR_MESSAGE="A stipend budget line item is required equal to or greater than the trainee numbers and associated stipend levels indicated in the questionnaire.Currently the stipend value in the budget is in deficit by (amount) for Year period of the budget.";
    public static final String STATUS_RECEIVING = "Receiving";
    public static final String STATUS_RECEIVED = "Received";
    public static final String STATUS_PROCESSING = "Processing";
    public static final String STATUS_VALIDATED = "Validated";
    public static final String STATUS_RECEIVED_BY_AGENCY = "Received by Agency";
    public static final String STATUS_REJECTED_WITH_ERRORS = "REJECTED_WITH_ERRORS";
    public static final String SUB_AWARD_BUDGET_NOT_FOUND = "budget.subaward.notfound";
    public static final String TRAINING_SUB_AWARD_BUDGET_NOT_FOUND = "budget.training.subaward.notfound";
    public static final String CONGRESSIONAL_DISTRICT = "performancesite.district.notfound";
    public static final String STATUS_PUREGED = "PURGED";
    public static final String ERROR_ERA_COMMON_USER_NAME="eRA Commons User Name is missing for ";
    public static final String PARTICIPANT_COUNT_REQUIRED = "s2s.budget.participantcount.required";
    public static final String PARTICIPANT_COSTS_REQUIRED = "s2s.budget.participantcost.required";
    public static final String MODULAR_BUDGET_REQUIRED = "s2s.budget.modularbudget.required";

    public static class NarrativeType {
    	public static final int PERSONNEL_JUSTIFICATION_CODE = 35;
    	public static final int CONSORTIUM_JUSTIFICATION_CODE = 36;
    	public static final int NARRATIVE_JUSTIFICATION_CODE = 37;
    	public static final int PHS_RES_TRAINING_PLAN_METHODS_FOR_ENHANCING_REPRODUCIBILITY_CODE = 140;
    	public static final int PHS_RES_TRAINING_PLAN_DATA_SAFETY_MONITORING_CODE = 129;

}

    
}
