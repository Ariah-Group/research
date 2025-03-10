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
package org.kuali.kra.iacuc.actions.notifyiacuc;

import org.kuali.kra.iacuc.IacucProtocol;
import org.kuali.rice.kew.api.exception.WorkflowException;

/**
 * Protocol Notify IACUC Service.
 */
public interface IacucProtocolNotifyIacucService {

    /**
     * Submit a notification to the IACUC office. The corresponding submission
     * and protocol action entries will be added to the database.
     *
     * @param protocol the protocol
     * @param notifyIacucBean the iacuc notification data
     */
    public void submitIacucNotification(IacucProtocol protocol, IacucProtocolNotifyIacucBean notifyIacucBean) throws WorkflowException;
}
