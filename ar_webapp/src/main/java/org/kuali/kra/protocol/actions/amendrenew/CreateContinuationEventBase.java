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
package org.kuali.kra.protocol.actions.amendrenew;

import org.kuali.kra.protocol.ProtocolDocumentBase;
import org.kuali.kra.rule.BusinessRuleInterface;
import org.kuali.kra.rule.event.KraDocumentEventBaseExtension;

public abstract class CreateContinuationEventBase extends KraDocumentEventBaseExtension {

    private String continuationSummary;
    private String propertyName;

    public CreateContinuationEventBase(ProtocolDocumentBase document, String propertyName, String continuationSummary) {
        super("Create Continuation", "", document);
        this.propertyName = propertyName;
        this.continuationSummary = continuationSummary;
    }

    public ProtocolDocumentBase getProtocolDocument() {
        return (ProtocolDocumentBase) getDocument();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getContinuationSummary() {
        return continuationSummary;
    }

    @Override
    public abstract BusinessRuleInterface getRule();

}
