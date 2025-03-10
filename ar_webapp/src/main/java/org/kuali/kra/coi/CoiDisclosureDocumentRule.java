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
package org.kuali.kra.coi;

import org.kuali.kra.coi.disclosure.DisclosureFinancialEntityAuditRule;
import org.kuali.kra.coi.disclosure.SaveDisclosureReporterUnitEvent;
import org.kuali.kra.coi.questionnaire.DisclosureQuestionnaireAuditRule;
import org.kuali.kra.rule.BusinessRuleInterface;
import org.kuali.kra.rule.event.KraDocumentEventBaseExtension;
import org.kuali.kra.rules.ResearchDocumentRuleBase;
import org.kuali.rice.krad.document.Document;

/**
 *
 * This class is the rule class for coidisclosuredocument
 */
public class CoiDisclosureDocumentRule extends ResearchDocumentRuleBase implements BusinessRuleInterface {

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean retval = true;
        retval &= super.processCustomRouteDocumentBusinessRules(document);

        return retval;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        if (!(document instanceof CoiDisclosureDocument)) {
            return false;
        }

        boolean valid = true;
        CoiDisclosureDocument coiDisclosureDocument = (CoiDisclosureDocument) document;
        valid &= processReporterUnitRules(coiDisclosureDocument);
        return valid;
    }

    public boolean processReporterUnitRules(CoiDisclosureDocument document) {
        return processRules(new SaveDisclosureReporterUnitEvent("document.coiDisclosureList[0].disclosurePersons[0]",
                document.getCoiDisclosure().getDisclosureReporter().getDisclosurePersonUnits()));
    }

    /**
     *
     * @see
     * org.kuali.kra.rule.BusinessRuleInterface#processRules(org.kuali.kra.rule.event.KraDocumentEventBaseExtension)
     */
    @Override
    public boolean processRules(KraDocumentEventBaseExtension event) {
        return event.getRule().processRules(event);
    }

    @Override
    public boolean processRunAuditBusinessRules(Document document) {
        boolean retval = true;

        retval &= super.processRunAuditBusinessRules(document);
        retval &= new DisclosureFinancialEntityAuditRule().processRunAuditBusinessRules((CoiDisclosureDocument) document);
        retval &= new DisclosureQuestionnaireAuditRule().processRunAuditBusinessRules((CoiDisclosureDocument) document);
        return retval;
    }

}
