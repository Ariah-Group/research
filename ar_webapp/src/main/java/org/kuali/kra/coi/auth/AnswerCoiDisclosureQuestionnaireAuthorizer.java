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
package org.kuali.kra.coi.auth;

/**
 * This class checks authorization for answering COI disclosure questionnaires
 */
public class AnswerCoiDisclosureQuestionnaireAuthorizer extends ModifyCoiDisclosureAuthorizer {
    
    /**
     * @see org.kuali.kra.coi.auth.ModifyCoiDisclosureAuthorizer#isAuthorized(java.lang.String, org.kuali.kra.coi.auth.CoiDisclosureTask)
     */
    @Override
    public boolean isAuthorized(String userId, CoiDisclosureTask task) {
        return super.isAuthorized(userId, task);
    }

}
