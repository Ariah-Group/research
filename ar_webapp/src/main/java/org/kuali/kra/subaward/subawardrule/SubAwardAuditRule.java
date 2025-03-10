/*.
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License,
 *  Version 2.0 (the "License");
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
package org.kuali.kra.subaward.subawardrule;

import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.rules.ResearchDocumentRuleBase;
import org.kuali.kra.subaward.document.SubAwardDocument;
import org.kuali.rice.kns.util.AuditCluster;
import org.kuali.rice.kns.util.AuditError;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.DocumentAuditRule;

import java.util.ArrayList;
import java.util.List;

/**
 * This class processes audit rules
 * (warnings) for the Report Information related
 * data of the SubAwardDocument.
 */
public class SubAwardAuditRule extends
ResearchDocumentRuleBase implements DocumentAuditRule{

    private static final String CONTACTS_AUDIT_ERRORS = "contactsAuditErrors";
    private List<AuditError> auditErrors;

    /**
     *
     * Constructs a SubAwardAuditRule.java. Added so unit test would not
     * need to call processRunAuditBusinessRules
     *  and therefore declare a document.
     */
    public SubAwardAuditRule() {
        auditErrors = new ArrayList<AuditError>();
    }

    /**
     * @see org.kuali.core.rule.DocumentAuditRule#processRunAuditBusinessRules(
     * org.kuali.core.document.Document)
     */
    @Override
    public boolean processRunAuditBusinessRules(Document document) {
        boolean valid = true;
        auditErrors = new ArrayList<AuditError>();

        valid &= checkForAtLeastOneContact(document);

        reportAndCreateAuditCluster();

        return valid;

    }

    /**
     * This method creates and adds
     * the AuditCluster to the Global AuditErrorMap.
     */
    @SuppressWarnings("unchecked")
    protected void reportAndCreateAuditCluster() {
        if (auditErrors.size() > 0) {
            AuditCluster existingErrors = (AuditCluster)
            KNSGlobalVariables.getAuditErrorMap().get(CONTACTS_AUDIT_ERRORS);
            if (existingErrors == null) {
                KNSGlobalVariables.getAuditErrorMap().put(
            CONTACTS_AUDIT_ERRORS, new AuditCluster(
            Constants.SUBAWARD_CONTACTS_PANEL_NAME,
                        auditErrors, Constants.AUDIT_ERRORS));
            } else {
                existingErrors.getAuditErrorList().addAll(auditErrors);
            }
        }
    }
    /**.
     * This method checkForAtLeastOneContact
     * @param document
     * @return boolean.
     */
    protected boolean checkForAtLeastOneContact(Document document) {
        SubAwardDocument subAwardDocument = (SubAwardDocument)document;
        subAwardDocument.getSubAward().getSubAwardContactsList().size();
        if (subAwardDocument.getSubAward().
          getSubAwardContactsList().size() <= 0) {
            subAwardDocument.getSubAward().setDefaultOpen(false);
            auditErrors.add(new AuditError(Constants.
          SUBAWARD_AUDIT_RULE_ERROR_KEY, KeyConstants.ERROR_SUBAWARD_CONTACT,
          Constants.MAPPING_SUBAWARD_PAGE
          + "." + Constants.SUBAWARD_CONTACTS_PANEL_ANCHOR));
            return false;
        } else {
            subAwardDocument.getSubAward().setDefaultOpen(true);
            return true;
        }
    }
}
