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
package org.ariahgroup.research.negotiations.auth;

import org.kuali.kra.infrastructure.PermissionConstants;

public class ModifyNegotiationAuthorizer extends NegotiationAuthorizer {

    @Override
    public boolean isAuthorized(String userId, NegotiationTask task) {
        boolean retVal = hasPermission(userId, task.getNegotiationDocument().getNegotiation(), PermissionConstants.NEGOTIATION_MODIFIY_NEGOTIATION)
                && !task.getNegotiationDocument().isViewOnly();
        return retVal;
    }

}
