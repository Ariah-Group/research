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

import org.kuali.kra.protocol.actions.notify.ProtocolActionAttachment;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * This class for the base property of request/notify irb action
 */
public abstract class ProtocolSubmissionBeanBase extends ProtocolActionBean implements org.kuali.kra.protocol.actions.ProtocolSubmissionBeanBase {
    
    private String committeeId;
    private ProtocolActionAttachment newActionAttachment;
    private List<ProtocolActionAttachment> actionAttachments = new ArrayList<ProtocolActionAttachment>();

    public ProtocolSubmissionBeanBase(ActionHelper actionHelper) {
        super(actionHelper);
    }

    @Override
    public String getCommitteeId() {
        return committeeId;
    }

    @Override
    public void setCommitteeId(String committeeId) {
        this.committeeId = committeeId;
    }

    @Override
    public ProtocolActionAttachment getNewActionAttachment() {
        return newActionAttachment;
    }

    @Override
    public void setNewActionAttachment(ProtocolActionAttachment newActionAttachment) {
        this.newActionAttachment = newActionAttachment;
    }

    @Override
    public List<ProtocolActionAttachment> getActionAttachments() {
        return actionAttachments;
    }

    @Override
    public void setActionAttachments(List<org.kuali.kra.protocol.actions.notify.ProtocolActionAttachment> actionAttachments) {
        this.actionAttachments = actionAttachments;
    }

}
