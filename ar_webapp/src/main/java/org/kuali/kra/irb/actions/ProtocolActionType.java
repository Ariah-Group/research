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
package org.kuali.kra.irb.actions;

import org.kuali.kra.protocol.actions.ProtocolActionTypeBase;

/**
 * A Protocol Action Type refers to the type of actions that an
 * that can be performed against a Protocol document.
 */
@SuppressWarnings("serial")
public class ProtocolActionType extends ProtocolActionTypeBase {

    public static final String PROTOCOL_CREATED = "100";

    public static final String SUBMIT_TO_IRB = "101";

    public static final String RENEWAL_CREATED = "102";

    public static final String AMENDMENT_CREATED = "103";

    public static final String REQUEST_FOR_TERMINATION = "104";

    public static final String REQUEST_TO_CLOSE = "105";

    public static final String REQUEST_FOR_SUSPENSION = "106";

    public static final String REQUEST_TO_CLOSE_ENROLLMENT = "108";

    public static final String NOTIFIED_COMMITTEE = "109";

    public static final String RENEWAL_REMINDER_GENERATED = "111";

    public static final String ADMINISTRATIVE_CORRECTION = "113";

    public static final String REQUEST_FOR_DATA_ANALYSIS_ONLY = "114";

    public static final String REQUEST_TO_REOPEN_ENROLLMENT = "115";

    public static final String NOTIFY_IRB = "116";

    public static final String ABANDON_PROTOCOL = "119";

    public static final String MODIFY_AMENDMENT_SECTION = "125";

    public static final String WITHDRAW_SUBMISSION = "131";
    
    public static final String ASSIGN_TO_AGENDA = "200";

    public static final String DEFERRED = "201";

    public static final String SUBSTANTIVE_REVISIONS_REQUIRED = "202";

    public static final String SPECIFIC_MINOR_REVISIONS_REQUIRED = "203";

    public static final String APPROVED = "204";

    public static final String EXPEDITE_APPROVAL = "205";

    public static final String GRANT_EXEMPTION = "206";

    public static final String CLOSED_FOR_ENROLLMENT = "207";

    public static final String RESPONSE_APPROVAL = "208";

    public static final String IRB_ACKNOWLEDGEMENT = "209";

    public static final String IRB_REVIEW_NOT_REQUIRED = "210";

    public static final String DATA_ANALYSIS_ONLY = "211";

    public static final String REOPEN_ENROLLMENT = "212";
    
    public static final String RETURNED_TO_PI = "213";

    public static final String CLOSED_ADMINISTRATIVELY_CLOSED = "300";

    public static final String TERMINATED = "301";

    public static final String SUSPENDED = "302";

    public static final String WITHDRAWN = "303";

    public static final String DISAPPROVED = "304";

    public static final String EXPIRED = "305";

    public static final String SUSPENDED_BY_DSMB = "306";

    public static final String MANAGE_REVIEW_COMMENTS = "310";

    // used for notifications since original codes were already used for something else
    public static final String PROTOCOL_CREATED_NOTIFICATION = "320";

    public static final String SUBMIT_TO_IRB_NOTIFICATION = "321";
    
    public static final String RENEWAL_CREATED_NOTIFICATION = "322";
    
    public static final String AMENDMENT_CREATED_NOTIFICATION = "323";

    //No corresponding actionTypeCode for this in Coeus  
    //Although we can use DISAPPROVED/SPECIFIC_MINOR_REVISIONS_REQUIRED/SUBSTANTIVE_REVISIONS_REQUIRED instead 
    //We still need a unique Action code to map if the option to be chosen is 'Approve' since we need to enable 'Approve' action subsequently 
    public static final String RECORD_COMMITTEE_DECISION = "308";

    //No corresponding actionTypeCode for this in Coeus  
    public static final String MODIFY_PROTOCOL_SUBMISSION = "309";
    
    // action type to be used only by workflow disapproval post-processor
    public static final String REJECTED_IN_ROUTING = "404";
    public static final String RECALLED_IN_ROUTING = "405";

    public static final String ASSIGN_REVIEWER = "901";
    public static final String REVIEW_COMPLETE = "902";
    public static final String REVIEW_REJECTED = "903";
    public static final String FUNDING_SOURCE = "904";
    public static final String REVIEW_DELETED = "905";
    public static final String RENEWAL_WITH_AMENDMENT_CREATED = "909";
    
    private String protocolActionTypeCode;

    private String description;

    private boolean triggerSubmission;

    private boolean triggerCorrespondence;

    private boolean finalActionForBatchCorrespondence;

    /**
     * Constructs a ProtocolActionType.
     */
    public ProtocolActionType() {
    }

    @Override
    public void setProtocolActionTypeCode(String protocolActionTypeCode) {
        this.protocolActionTypeCode = protocolActionTypeCode;
    }

    @Override
    public String getProtocolActionTypeCode() {
        return protocolActionTypeCode;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setTriggerSubmission(boolean triggerSubmission) {
        this.triggerSubmission = triggerSubmission;
    }

    @Override
    public boolean getTriggerSubmission() {
        return triggerSubmission;
    }

    @Override
    public void setTriggerCorrespondence(boolean triggerCorrespondence) {
        this.triggerCorrespondence = triggerCorrespondence;
    }

    @Override
    public boolean getTriggerCorrespondence() {
        return triggerCorrespondence;
    }

    @Override
    public boolean isFinalActionForBatchCorrespondence() {
        return finalActionForBatchCorrespondence;
    }

    @Override
    public void setFinalActionForBatchCorrespondence(boolean finalActionForBatchCorrespondence) {
        this.finalActionForBatchCorrespondence = finalActionForBatchCorrespondence;
    }
}
