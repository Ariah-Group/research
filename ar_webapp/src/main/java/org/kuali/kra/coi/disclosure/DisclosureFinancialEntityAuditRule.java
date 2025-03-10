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
package org.kuali.kra.coi.disclosure;

import org.kuali.kra.coi.*;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.rules.ResearchDocumentRuleBase;
import org.kuali.rice.kns.util.AuditCluster;
import org.kuali.rice.kns.util.AuditError;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.DocumentAuditRule;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class DisclosureFinancialEntityAuditRule extends ResearchDocumentRuleBase implements DocumentAuditRule {

    private static final String FINANCIAL_ENTITY_AUDIT_ERRORS = "financialEntityDiscAuditErrors";
    private static final String NEW_TAG = "disclosureHelper.newCoiDisclProject.finEntityStatusMissing";
    private List<AuditError> auditErrors;

    @Override
    public boolean processRunAuditBusinessRules(Document document) {
        boolean isValid = true;
        CoiDisclosureDocument coiDisclosureDocument = (CoiDisclosureDocument) document;
        auditErrors = new ArrayList<AuditError>();

        // TODO : Once the normalize is done, then the audit rule will be simpler, and we don't
        // need these event check, all events will be the same.
        if (coiDisclosureDocument.getCoiDisclosure().isManualEvent()) {
            isValid = isConflictValueSelectedForManual(coiDisclosureDocument.getCoiDisclosure());

        } else if (coiDisclosureDocument.getCoiDisclosure().isAnnualEvent() && !coiDisclosureDocument.getCoiDisclosure().isAnnualUpdate()) {
            isValid = isConflictValueSelectedForAnnual(coiDisclosureDocument.getCoiDisclosure());

        } else if (coiDisclosureDocument.getCoiDisclosure().isUpdateEvent() || (coiDisclosureDocument.getCoiDisclosure().isAnnualEvent() && coiDisclosureDocument.getCoiDisclosure().isAnnualUpdate())) {
            isValid = isConflictValueSelectedForUpdate(coiDisclosureDocument.getCoiDisclosure());

        } else {
            isValid = isConflictValueSelected(coiDisclosureDocument.getCoiDisclosure());
        }

        reportAndCreateAuditCluster();

        return isValid;
    }

    protected void addErrorToAuditErrors(int index, String errorKey, String anchor, String error) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constants.COI_DISCLOSURE_DISCLOSURE_PAGE);
        stringBuilder.append(".");
        stringBuilder.append(anchor);
        auditErrors.add(new AuditError(String.format(errorKey, index), error, stringBuilder.toString()));
    }

    protected void addErrorToAuditErrors(int index, int index1, String errorKey) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constants.COI_DISCLOSURE_DISCLOSURE_PAGE);
        stringBuilder.append(".");
        stringBuilder.append(Constants.DISCLOSURE_FINANCIAL_ENTITY_PANEL_ANCHOR);
        auditErrors.add(new AuditError(String.format(errorKey, index, index1),
                KeyConstants.ERROR_COI_FINANCIAL_ENTITY_STATUS_REQUIRED,
                stringBuilder.toString()));
    }

    protected void addErrorToAuditErrors(String property, int index, int index1, String errorKey) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constants.COI_DISCLOSURE_DISCLOSURE_PAGE);
        stringBuilder.append(".");
        stringBuilder.append(Constants.DISCLOSURE_FINANCIAL_ENTITY_PANEL_ANCHOR);
        auditErrors.add(new AuditError(String.format(errorKey, property, index, index1),
                KeyConstants.ERROR_COI_FINANCIAL_ENTITY_STATUS_REQUIRED,
                stringBuilder.toString()));
    }

    protected boolean isConflictValueSelected(CoiDisclosure coiDisclosure) {
        boolean isSelected = true;
        int i = 0;
        for (CoiDiscDetail coiDiscDetail : coiDisclosure.getCoiDisclProjects().get(0).getCoiDiscDetails()) {
            if (coiDiscDetail.getEntityDispositionCode() == null) {
                addErrorToAuditErrors(i,
                        Constants.DISCLOSURE_FINANCIAL_ENTITY_KEY2,
                        Constants.DISCLOSURE_FINANCIAL_ENTITY_PANEL_ANCHOR,
                        KeyConstants.ERROR_COI_FINANCIAL_ENTITY_STATUS_REQUIRED);
                isSelected = false;
            }
            i++;
        }
        return isSelected;
    }

    protected boolean isConflictValueSelectedForAnnual(CoiDisclosure coiDisclosure) {
        boolean isSelected = true;
        int i = 0;
        // Allow annual disclosures to be attached without projects. This is a likely scenario
        if (ObjectUtils.isNotNull(coiDisclosure.getCoiDisclProjects()) && !coiDisclosure.getCoiDisclProjects().isEmpty()) {
            for (CoiDisclProject disclProject : coiDisclosure.getCoiDisclProjects()) {
                if (!isEventExcludedFE(disclProject.getDisclosureEventType())) {
                    int j = 0;
                    for (CoiDiscDetail coiDiscDetail : disclProject.getCoiDiscDetails()) {
                        if (coiDiscDetail.getEntityDispositionCode() == null) {
                            addErrorToAuditErrors(i, j, Constants.DISCLOSURE_ANNUAL_FINANCIAL_ENTITY_KEY2);
                            isSelected = false;
                        }
                        j++;
                    }
                }
                i++;
            }
        }
        return isSelected;
    }

    protected boolean isConflictValueSelectedForManual(CoiDisclosure coiDisclosure) {
        boolean isSelected = true;
        int i = 0;
        // Missing project. There should be a project linked to all manual and event disclosures
        if (coiDisclosure.getCoiDisclProjects().isEmpty()) {
            addErrorToAuditErrors(i, Constants.DISCLOSURE_MANUAL_FINANCIAL_ENTITY_KEY,
                    Constants.DISCLOSURE_FINANCIAL_ENTITY_PANEL_ANCHOR, KeyConstants.ERROR_COI_PROJECT_REQUIRED);
            isSelected = false;
        } else {
            if (!coiDisclosure.getCoiDisclosureEventType().isExcludeFinancialEntities()) {
                for (CoiDiscDetail coiDiscDetail : coiDisclosure.getCoiDisclProjects().get(0).getCoiDiscDetails()) {
                    if (coiDiscDetail.getEntityDispositionCode() == null) {
                        addErrorToAuditErrors(i, Constants.DISCLOSURE_MANUAL_FINANCIAL_ENTITY_KEY, NEW_TAG, KeyConstants.ERROR_COI_FINANCIAL_ENTITY_STATUS_REQUIRED);
                        isSelected = false;
                    }
                    i++;
                }
            }
        }
        return isSelected;
    }

    protected void reportAndCreateAuditCluster() {
        if (auditErrors.size() > 0) {
            KNSGlobalVariables.getAuditErrorMap().put(FINANCIAL_ENTITY_AUDIT_ERRORS,
                    new AuditCluster(Constants.COI_DISCLOSURE_DISCLOSURE_PANEL_NAME, auditErrors, Constants.AUDIT_ERRORS));
        }
    }

    protected boolean isConflictValueSelectedForUpdate(CoiDisclosure coiDisclosure) {
        MasterDisclosureBean masterDisclosureBean = getCoiDisclosureService().getMasterDisclosureDetail(coiDisclosure);
        boolean isSelected = true;
        isSelected &= checkProject(masterDisclosureBean.getAwardProjects(), "awardProjects");
        isSelected &= checkProject(masterDisclosureBean.getProtocolProjects(), "protocolProjects");
        isSelected &= checkProject(masterDisclosureBean.getProposalProjects(), "proposalProjects");
        isSelected &= checkProject(masterDisclosureBean.getManualAwardProjects(), "manualAwardProjects");
        isSelected &= checkProject(masterDisclosureBean.getManualProtocolProjects(), "manualProtocolProjects");
        isSelected &= checkProject(masterDisclosureBean.getManualProposalProjects(), "manualProposalProjects");
        isSelected &= checkProject(masterDisclosureBean.getManualTravelProjects(), "manualTravelProjects");
        return isSelected;
    }

    private boolean checkProject(List<CoiDisclosureProjectBean> disclProjects, String property) {
        boolean isSelected = true;
        int i = 0;
        for (CoiDisclosureProjectBean disclProjectBean : disclProjects) {
            if (!disclProjectBean.isExcludeFE()) {
                int j = 0;
                for (CoiDiscDetail coiDiscDetail : disclProjectBean.getCoiDisclProject().getCoiDiscDetails()) {
                    if (coiDiscDetail.getEntityDispositionCode() == null) {
                        addErrorToAuditErrors(property, i, j, Constants.DISCLOSURE_UPDATE_FINANCIAL_ENTITY_KEY);
                        isSelected = false;
                    }
                    j++;
                }
            }
            i++;
        }
        return isSelected;

    }

    private CoiDisclosureService getCoiDisclosureService() {
        return KraServiceLocator.getService(CoiDisclosureService.class);
    }

    private boolean isEventExcludedFE(String eventTypeCode) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("eventTypeCode", eventTypeCode);
        CoiDisclosureEventType CoiDisclosureEventType = KraServiceLocator.getService(BusinessObjectService.class).findByPrimaryKey(CoiDisclosureEventType.class, fieldValues);
        return CoiDisclosureEventType.isExcludeFinancialEntities();
    }
}
