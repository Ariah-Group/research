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
package org.kuali.kra.irb;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kra.infrastructure.TaskName;
import org.kuali.kra.irb.actions.ProtocolStatus;
import org.kuali.kra.irb.auth.ProtocolTask;
import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.protocol.ProtocolDao;
import org.kuali.kra.protocol.ProtocolLookupableHelperServiceImplBase;
import org.kuali.kra.protocol.auth.ProtocolTaskBase;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * This class handles searching for protocols.
 */
public class ProtocolLookupableHelperServiceImpl extends ProtocolLookupableHelperServiceImplBase<Protocol> {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -6170836146164439176L;

    private ProtocolDao<Protocol> protocolDao;

    private static final String[] AMEND_RENEW_PROTOCOL_TASK_CODES = {TaskName.CREATE_PROTOCOL_AMENDMENT,
        TaskName.CREATE_PROTOCOL_RENEWAL};
    private static final String NOTIFY_IRB_PROTOCOL_LOOKUP_ACTION = "lookupActionNotifyIRBProtocol";
    private static final String[] NOTIFY_IRB_PROTOCOL_TASK_CODES = {TaskName.NOTIFY_IRB};
    private static final String[] REQUEST_PROTOCOL_TASK_CODES = {TaskName.PROTOCOL_REQUEST_CLOSE,
        TaskName.PROTOCOL_REQUEST_CLOSE_ENROLLMENT,
        TaskName.PROTOCOL_REQUEST_DATA_ANALYSIS,
        TaskName.PROTOCOL_REQUEST_REOPEN_ENROLLMENT,
        TaskName.PROTOCOL_REQUEST_SUSPENSION,
        TaskName.PROTOCOL_REQUEST_TERMINATE};
    private static final String[] PENDING_PROTOCOL_STATUS_CODES = {ProtocolStatus.IN_PROGRESS,
        ProtocolStatus.SUBMITTED_TO_IRB,
        ProtocolStatus.SPECIFIC_MINOR_REVISIONS_REQUIRED,
        ProtocolStatus.SUBSTANTIVE_REVISIONS_REQUIRED,
        ProtocolStatus.AMENDMENT_IN_PROGRESS,
        ProtocolStatus.RENEWAL_IN_PROGRESS,
        ProtocolStatus.WITHDRAWN};
    private static final String[] PENDING_PI_ACTION_PROTOCOL_STATUS_CODES = {ProtocolStatus.RETURN_TO_PI,
        ProtocolStatus.SPECIFIC_MINOR_REVISIONS_REQUIRED,
        ProtocolStatus.SUBSTANTIVE_REVISIONS_REQUIRED,
        ProtocolStatus.EXPIRED};

    @Override
    protected ProtocolTaskBase createNewProtocolTaskInstanceHook(String taskName, ProtocolBase protocol) {
        return new ProtocolTask(taskName, (Protocol) protocol);
    }

    @Override
    protected List<? extends BusinessObject> getSearchResultsFilteredByTask(Map<String, String> fieldValues) {
        List<? extends BusinessObject> searchResults = null;

        if (BooleanUtils.toBoolean(fieldValues.get(AMEND_RENEW_PROTOCOL_LOOKUP_ACTION))) {
            searchResults = filterProtocolsByTask(fieldValues, AMEND_RENEW_PROTOCOL_TASK_CODES);
        } else if (BooleanUtils.toBoolean(fieldValues.get(NOTIFY_IRB_PROTOCOL_LOOKUP_ACTION))) {
            searchResults = filterProtocolsByTask(fieldValues, NOTIFY_IRB_PROTOCOL_TASK_CODES);
        } else if (BooleanUtils.toBoolean(fieldValues.get(REQUEST_PROTOCOL_ACTION))) {
            searchResults = filterProtocolsByTask(fieldValues, REQUEST_PROTOCOL_TASK_CODES);
        } else if (BooleanUtils.toBoolean(fieldValues.get(PENDING_PROTOCOL_LOOKUP))) {
            searchResults = filterProtocolsByStatus(fieldValues, PENDING_PROTOCOL_STATUS_CODES);
        } else if (BooleanUtils.toBoolean(fieldValues.get(PENDING_PI_ACTION_PROTOCOL_LOOKUP))) {
            searchResults = filterProtocolsByStatus(fieldValues, PENDING_PI_ACTION_PROTOCOL_STATUS_CODES);
        } else if (StringUtils.isNotBlank(fieldValues.get(PROTOCOL_PERSON_ID_LOOKUP))) {
            searchResults = filterProtocolsByPrincipal(fieldValues, PROTOCOL_PERSON_ID_LOOKUP);
        } else {
            // Add minimum viable security to ensure the user AT LEAST has View Protocol permission 
            // on one of their roles or else the record should NOT even be visible in the result list.
            searchResults = filterProtocolsByTask(fieldValues, TaskName.VIEW_PROTOCOL);
        }

        return searchResults;
    }

    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);

        boolean requireAtLeastOneField = getParameterService().getParameterValueAsBoolean(Constants.MODULE_CODE_IRB,
                Constants.PARAMETER_COMPONENT_DOCUMENT, Constants.ARIAH_IRB_PROTOCOL_SEARCH_REQUIRE_FIELD_ENABLED, false);

        // if the parameter flag is true, check the search field values entered and 
        // if none have a value send an error to user that they must enter at least 
        // one search field
        if (requireAtLeastOneField) {
            requireAtLeastOneSearchField(fieldValues);
        }

    }

    /**
     * Check the values of the search fields and verify at least one has been set. 
     * If not set add an error to the message map.
     * 
     * @param fieldValues Map of search fields and values.
     */
    protected void requireAtLeastOneSearchField(Map<String, String> fieldValues) {

        // create a new Map and copy values over to ensure we're not modifying original Map
        Map<String, String> fieldValuesToCheck = new HashMap<String, String>(fieldValues.size());
        Set<String> keys = fieldValues.keySet();

        for (String key : keys) {
            String value = fieldValues.get(key);
            fieldValuesToCheck.put(key, value);
        }

        fieldValuesToCheck.remove(AMEND_RENEW_PROTOCOL_LOOKUP_ACTION);
        fieldValuesToCheck.remove(NOTIFY_IRB_PROTOCOL_LOOKUP_ACTION);
        fieldValuesToCheck.remove(REQUEST_PROTOCOL_ACTION);
        fieldValuesToCheck.remove(PENDING_PROTOCOL_LOOKUP);
        fieldValuesToCheck.remove(PENDING_PI_ACTION_PROTOCOL_LOOKUP);
        fieldValuesToCheck.remove(PROTOCOL_PERSON_ID_LOOKUP);
        fieldValuesToCheck.remove(KRADConstants.BACK_LOCATION);
        fieldValuesToCheck.remove(KRADConstants.DOC_FORM_KEY);
        fieldValuesToCheck.remove("active");
        fieldValuesToCheck.remove("docNum");

        boolean atLeastOneSearchFieldIsSet = false;

        Set<String> newKeys = fieldValuesToCheck.keySet();
        for (String key : newKeys) {
            String value = fieldValuesToCheck.get(key);
            if (!value.isEmpty()) {
                atLeastOneSearchFieldIsSet = true;
                break;
            }
        }

        if (!atLeastOneSearchFieldIsSet) {
            GlobalVariables.getMessageMap().putError("singleFieldNotEntered", KeyConstants.ERROR_PROTOCOL_SEARCH_MISSING_ATLEAST_ONE);
        }
    }

    @Override
    protected Map<String, String> removeExtraFilterParameters(Map<String, String> fieldValues) {
        fieldValues.remove(AMEND_RENEW_PROTOCOL_LOOKUP_ACTION);
        fieldValues.remove(NOTIFY_IRB_PROTOCOL_LOOKUP_ACTION);
        fieldValues.remove(REQUEST_PROTOCOL_ACTION);
        fieldValues.remove(PENDING_PROTOCOL_LOOKUP);
        fieldValues.remove(PENDING_PI_ACTION_PROTOCOL_LOOKUP);
        fieldValues.remove(PROTOCOL_PERSON_ID_LOOKUP);
        return fieldValues;
    }

    @Override
    protected List<HtmlData> getCustomActions(BusinessObject businessObject, List pkNames) {
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        if (isParameterTrue(AMEND_RENEW_PROTOCOL_LOOKUP_ACTION)) {
            htmlDataList.add(getPerformActionLink(businessObject, AMEND_RENEW_PROTOCOL_LOOKUP_ACTION));
        } else if (isParameterTrue(NOTIFY_IRB_PROTOCOL_LOOKUP_ACTION)) {
            htmlDataList.add(getPerformActionLink(businessObject, NOTIFY_IRB_PROTOCOL_LOOKUP_ACTION));
        } else if (isParameterTrue(REQUEST_PROTOCOL_ACTION)) {
            htmlDataList.add(getPerformActionLink(businessObject, REQUEST_PROTOCOL_ACTION));
        } else {
            htmlDataList.addAll(getEditCopyViewLinks(businessObject, pkNames));
        }

        return htmlDataList;
    }

    @Override
    protected ProtocolDao<Protocol> getProtocolDaoHook() {
        return this.protocolDao;
    }

    /**
     *
     * This is spring bean will be used to get search results.
     *
     * @param protocolDao
     */
    public void setProtocolDao(ProtocolDao<Protocol> protocolDao) {
        this.protocolDao = protocolDao;
    }

    @Override
    protected String getDocumentTypeNameHook() {
        return "ProtocolDocument";
    }

    @Override
    protected String getHtmlActionHook() {
        return "protocolProtocol.do";
    }

    @Override
    protected Class<? extends ProtocolBase> getProtocolClassHook() {
        return Protocol.class;
    }
}
