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
package org.kuali.kra.irb.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kra.infrastructure.PermissionConstants;

/**
 * The View Protocol Authorizer determines if a user has the right to view a
 * specific protocol.
 */
public class ViewProtocolAuthorizer extends ProtocolAuthorizer {

    private static final Log LOG = LogFactory.getLog(ViewProtocolAuthorizer.class);

    /**
     * {@inheritDoc}
     *
     * @param userId
     * @return
     * @see
     * org.kuali.kra.irb.auth.ProtocolAuthorizer#isAuthorized(java.lang.String,
     * org.kuali.kra.irb.auth.ProtocolTask)
     */
    @Override
    public boolean isAuthorized(String userId, ProtocolTask task) {

        boolean hasPermissionsView = hasPermission(userId, task.getProtocol(), PermissionConstants.VIEW_PROTOCOL);
        boolean hasWorkflowPermissions = kraWorkflowService.hasWorkflowPermission(userId, task.getProtocol().getProtocolDocument());
        boolean isUserActionApprove = false;

        try {
            isUserActionApprove = kraWorkflowService.isUserActionListApproveRequested(task.getProtocol().getProtocolDocument(), userId, null);
        } catch (Exception e) {
            LOG.error("Error calling kraWorkflowService.isUserActionListApproveRequested for Protocol " + task.getProtocol().getProtocolNumber(), e);
        }

        return hasPermissionsView || hasWorkflowPermissions || isUserActionApprove;
    }
}
