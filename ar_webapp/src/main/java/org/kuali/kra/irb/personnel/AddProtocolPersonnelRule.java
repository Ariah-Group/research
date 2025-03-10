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
package org.kuali.kra.irb.personnel;

import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.protocol.personnel.ProtocolPersonnelRuleBase;
import org.kuali.kra.rule.BusinessRuleInterface;

/**
 * Runs the rule processing for adding a <code>ProtocolPerson</code>.
 */
public class AddProtocolPersonnelRule extends ProtocolPersonnelRuleBase implements BusinessRuleInterface<AddProtocolPersonnelEvent> {

    /**
     * {@inheritDoc}
     *
     * @see
     * org.kuali.kra.rule.BusinessRuleInterface#processRules(org.kuali.kra.rule.event.KraDocumentEventBaseExtension)
     */
    @Override
    public boolean processRules(AddProtocolPersonnelEvent event) {
        return processAddProtocolPersonnelEvent(event);
    }

    @Override
    public org.kuali.kra.protocol.personnel.ProtocolPersonnelService getProtocolPersonnelServiceHook() {
        return (ProtocolPersonnelService) KraServiceLocator.getService(ProtocolPersonnelService.class);
    }

}
