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
package org.kuali.kra.coi.lookup;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.authorization.ApplicationTask;
import org.kuali.kra.bo.KcPerson;
import org.kuali.kra.coi.CoiDisclosureUndisclosedEvents;
import org.kuali.kra.coi.disclosure.CoiDisclosureService;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.infrastructure.TaskName;
import org.kuali.kra.lookup.KraLookupableHelperServiceImpl;
import org.kuali.kra.service.KcPersonService;
import org.kuali.kra.service.TaskAuthorizationService;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class CoiDisclosureUndisclosedEventsLookupableHelper extends KraLookupableHelperServiceImpl {

    private CoiDisclosureService coiDisclosureService;

    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<CoiDisclosureUndisclosedEvents> undisclosedEvents = new ArrayList<CoiDisclosureUndisclosedEvents>();
        validateSearchParameters(fieldValues);
        addDateRangeCriteria(fieldValues);
        if(canViewUndisclosedEvents()) {
            undisclosedEvents = getCoiDisclosureService().getUndisclosedEvents(fieldValues);
        }
        return undisclosedEvents;
    }

    
    protected boolean validateDate(String dateFieldName, String dateFieldValue) {
        try{
            CoreApiServiceLocator.getDateTimeService().convertToSqlTimestamp(dateFieldValue);
            return true;
        } catch (ParseException e) {
            GlobalVariables.getMessageMap().putError(dateFieldName, KeyConstants.ERROR_SEARCH_INVALID_DATE);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            GlobalVariables.getMessageMap().putError(dateFieldName, KeyConstants.ERROR_SEARCH_INVALID_DATE);
            return false;
        }
    }

    @Override
    public void validateSearchParameters(Map<String,String> fieldValues) {
        super.validateSearchParameters(fieldValues);
        for (String key : fieldValues.keySet()) {
            String value = fieldValues.get(key);
            if (key.toUpperCase().indexOf("DATE") > 0) {
                //we have a date, now we need to weed out the calculated params that have '..' or '>=' or '<='
                if (value.indexOf("..") == -1 && value.indexOf(">=") == -1 && value.indexOf("<=") == -1) {
                    if (!StringUtils.isEmpty(value)) {
                        validateDate(key, value);
                    }
                }
            }
        }
    }
    
    private void addDateRangeCriteria(Map<String,String> fieldValues) {
        String dateParameter = fieldValues.get(CoiDisclosureUndisclosedEvents.SEARCH_CRITERIA_CREATE_DATE);
        if (dateParameter.contains("..")) {
            String[] values = dateParameter.split("\\.\\.");
            String startDate = values[0];
            String endDate = getRevisedCreateToDate(values[1], 1);
            fieldValues.put(CoiDisclosureUndisclosedEvents.SEARCH_CRITERIA_CREATE_DATE_FROM, startDate);
            fieldValues.put(CoiDisclosureUndisclosedEvents.SEARCH_CRITERIA_CREATE_DATE_TO, endDate);
        }else if(dateParameter.contains(">=")) {
            String dateValue = dateParameter.replace(">=", "");
            fieldValues.put(CoiDisclosureUndisclosedEvents.SEARCH_CRITERIA_CREATE_DATE_FROM, dateValue);
        }else if((dateParameter.contains("<="))) {
            String dateValue = dateParameter.replace("<=", "");
            String endDate = getRevisedCreateToDate(dateValue, 1);
            fieldValues.put(CoiDisclosureUndisclosedEvents.SEARCH_CRITERIA_CREATE_DATE_TO, endDate);
        }
    }
    
    @Override
    public List<Row> getRows() {
        List<Row> rows =  super.getRows();
        for (Row row : rows) {
            for (Field field : row.getFields()) {
                if (field.getPropertyName().equals("person.userName")) {
                    field.setFieldConversions("principalName:person.userName,principalId:personId");
                }
            }
        }
        return rows;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        String userName = (String) lookupForm.getFieldsForLookup().get("person.userName");
            if (StringUtils.isNotEmpty(userName)) {
                KcPerson person = getKcPersonService().getKcPersonByUserName(userName);
            if (person != null) {
                lookupForm.getFieldsForLookup().put("personId", person.getPersonId());
            }
        }
        
        return super.performLookup(lookupForm, resultTable, bounded);
    }
    
    public KcPersonService getKcPersonService() {
        return (KcPersonService) KraServiceLocator.getService(KcPersonService.class);
    }
    
   
    public CoiDisclosureService getCoiDisclosureService() {
        return coiDisclosureService;
    }
    
    public void setCoiDisclosureService(CoiDisclosureService coiDisclosureService) {
        this.coiDisclosureService = coiDisclosureService;
    }
    /*
     * The only action that we might have here in the future 
     * would be a notification to the reporter.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        return htmlDataList;
    }
    
   /*
    * Leaving this empty because we are not trying to open any documents.
    */
    @Override
    protected String getDocumentTypeName() {
        return "";
    }

    /*
     *This lookup is also not associated with any particular document, so leaving the html action part to null 
     */
    @Override
    protected String getHtmlAction() {
        return null;
    }

    @Override
    protected String getKeyFieldName() {
        return "projectId";
    }
       
    private boolean canViewUndisclosedEvents() {
        ApplicationTask task = new ApplicationTask(TaskName.VIEW_COI_UNDISCLOSED_EVENTS);
        return getTaskAuthorizationService().isAuthorized(getUserIdentifier(), task);     
    }

    private String getUserIdentifier() {
        return GlobalVariables.getUserSession().getPrincipalId();
    }
    
    protected TaskAuthorizationService getTaskAuthorizationService() {
        return KraServiceLocator.getService(TaskAuthorizationService.class);
    }
    
    /**
     * This method is to get an adjusted date to compare it with timestamp
     * @param dateInput
     * @param addDays
     * @return
     */
    private String getRevisedCreateToDate(String dateInput, int addDays) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT_PATTERN);
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(dateInput));
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        cal.add(Calendar.DATE, addDays); 
        return sdf.format(cal.getTime());        
    }

}

