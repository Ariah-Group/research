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
 *
 */
package org.kuali.kra.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.bo.Unit;
import org.kuali.kra.bo.UnitAdministrator;
import org.kuali.kra.bo.UnitCorrespondent;
import org.kuali.kra.dao.UnitLookupDao;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.service.UnitService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.KRADConstants;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The Unit Service Implementation.
 *
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class UnitServiceImpl implements UnitService {

    private BusinessObjectService businessObjectService;
    private static final String COLUMN = ":";
    private static final String SEPARATOR = ";1;";
    private static final String DASH = "-";
    private static final String UNIT_NUMBER = "unitNumber";
    private static final String PARENT_UNIT_NUMBER = "parentUnitNumber";
    private int numberOfUnits;
    private UnitLookupDao unitLookupDao;

    private static final Log LOG = LogFactory.getLog(UnitServiceImpl.class);

    /**
     * @see
     * org.kuali.kra.service.UnitService#getUnitCaseInsensitive(java.lang.String)
     */
    @Override
    public Unit getUnitCaseInsensitive(String unitNumber) {
        Unit unit = null;
        if (StringUtils.isNotEmpty(unitNumber)) {
            unit = this.getUnitLookupDao().findUnitbyNumberCaseInsensitive(unitNumber);
        }
        return unit;
    }

    /**
     * @see org.kuali.kra.service.UnitService#getUnitName(java.lang.String)
     */
    @Override
    public String getUnitName(String unitNumber) {
        String unitName = null;
        Map<String, String> primaryKeys = new HashMap<String, String>();
        if (StringUtils.isNotEmpty(unitNumber)) {
            primaryKeys.put("unitNumber", unitNumber);
            Unit unit = (Unit) businessObjectService.findByPrimaryKey(Unit.class, primaryKeys);
            if (unit != null) {
                unitName = unit.getUnitName();
            }
        }

        return unitName;
    }

    /**
     * @see org.kuali.kra.service.UnitService#getUnits()
     */
    @Override
    public Collection<Unit> getUnits() {
        return businessObjectService.findAll(Unit.class);
    }

    /**
     * @see org.kuali.kra.service.UnitService#getUnit(java.lang.String)
     */
    @Override
    public Unit getUnit(String unitNumber) {
        Unit unit = null;

        Map<String, String> primaryKeys = new HashMap<String, String>();
        if (StringUtils.isNotEmpty(unitNumber)) {
            primaryKeys.put("unitNumber", unitNumber);
            unit = (Unit) businessObjectService.findByPrimaryKey(Unit.class, primaryKeys);
        }

        return unit;
    }

    /**
     * @see org.kuali.kra.service.UnitService#getSubUnits(java.lang.String)
     */
    @Override
    public List<Unit> getSubUnits(String unitNumber) {

        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("parentUnitNumber", unitNumber);

        List<Unit> units = (List<Unit>) businessObjectService.findMatching(Unit.class, fieldValues);

        return units;
    }

    /**
     * @see org.kuali.kra.service.UnitService#getAllSubUnits(java.lang.String)
     */
    @Override
    public List<Unit> getAllSubUnits(String unitNumber) {
        List<Unit> units = new ArrayList<Unit>();
        List<Unit> subUnits = getSubUnits(unitNumber);
        units.addAll(subUnits);
        for (Unit subUnit : subUnits) {
            units.addAll(getAllSubUnits(subUnit.getUnitNumber()));
        }

        return units;
    }

    /**
     *
     * @see
     * org.kuali.kra.service.UnitService#getUnitHierarchyForUnit(java.lang.String)
     */
    @Override
    public List<Unit> getUnitHierarchyForUnit(String unitNumber) {
        List<Unit> units = null;
        Unit thisUnit = this.getUnit(unitNumber);
        if (thisUnit != null) {
            units = (ArrayList<Unit>) (getUnitParentsAndSelf(thisUnit));
        } else {
            units = new ArrayList<Unit>();
        }
        return units;
    }

    /**
     *
     * This method returns a List of Units containing all the unit's parents up
     * to the root unit, and includes the unit itself.
     *
     * @param unit
     * @return
     */
    private List<Unit> getUnitParentsAndSelf(Unit unit) {
        List<Unit> units = new ArrayList<Unit>();
        if (!StringUtils.isEmpty(unit.getParentUnitNumber())) {
            units.addAll(getUnitHierarchyForUnit(unit.getParentUnitNumber()));
        }
        units.add(unit);
        return units;
    }

    /**
     * Sets the businessObjectService attribute value. Injected by Spring.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Accessor for <code>{@link BusinessObjectService}</code>
     *
     * @return BusinessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return this.businessObjectService;
    }

    /**
     *
     * @see
     * org.kuali.kra.service.UnitService#getSubUnitsForTreeView(java.lang.String)
     */
    @Override
    public String getSubUnitsForTreeView(String unitNumber) {
        // unitNumber will be like "<table width="600"><tr><td width="70%">BL-BL : BLOOMINGTON CAMPUS"
        String subUnits = null;
        // Following index check maybe changed if refactor jsp page to align buttons.
        int startIdx = unitNumber.indexOf("px\">", unitNumber.indexOf("<tr>"));
        for (Unit unit : getSubUnits(unitNumber.substring(startIdx + 4, unitNumber.indexOf(COLUMN, startIdx) - 1))) {
            if (StringUtils.isNotBlank(subUnits)) {
                subUnits = subUnits + "#SEPARATOR#" + unit.getUnitNumber() + KRADConstants.BLANK_SPACE + COLUMN + KRADConstants.BLANK_SPACE + unit.getUnitName();
            } else {
                subUnits = unit.getUnitNumber() + KRADConstants.BLANK_SPACE + COLUMN + KRADConstants.BLANK_SPACE + unit.getUnitName();
            }
        }
        return subUnits;

    }

    /**
     * @see org.kuali.kra.service.UnitService#getTopUnit()
     */
    @Override
    public Unit getTopUnit() {
        Unit topUnit = null;

        List<Unit> allUnits = (List<Unit>) getUnits();
        if (CollectionUtils.isNotEmpty(allUnits)) {
            for (Unit unit : allUnits) {
                if (StringUtils.isEmpty(unit.getParentUnitNumber())) {
                    topUnit = unit;
                    break;
                }
            }
        }
//        Map<String, String> queryMap = new HashMap<String, String>();
//        // Field below is intentionally left as NULL
//        queryMap.put(PARENT_UNIT_NUMBER, null);
//        List<Unit> possibleTopUnits = (List<Unit>) getBusinessObjectService().findMatching(Unit.class, queryMap);
//
//        if (possibleTopUnits != null) {
//            if (possibleTopUnits.isEmpty()) {
//                LOG.error("*** ERROR *** : Lookup of top-most unit returned MULTIPLE results.");
//                LOG.error("                This is bad as it means there are multiple top units and the unit hierarchy functionality may be broken.");
//            } else {
//                 // retrieve what should be the FIRST and ONLY top unit
//                topUnit = possibleTopUnits.get(0);
//            }
//        }

        return topUnit;
    }

    /**
     * TODO : still WIP. cleanup b4 move to prod
     *
     * @see org.kuali.kra.service.UnitService#getInitialUnitsForUnitHierarchy()
     * Basic data structure : Get the Top node to display. The node data is like
     * following : 'parentidx-unitNumber : unitName' and separated by ';1;'
     */
    @Override
    public String getInitialUnitsForUnitHierarchy() {
        Unit instituteUnit = getTopUnit();
        int parentIdx = 0;
        String subUnits = instituteUnit.getUnitNumber() + KRADConstants.BLANK_SPACE + COLUMN + KRADConstants.BLANK_SPACE + instituteUnit.getUnitName() + SEPARATOR;
        numberOfUnits = 0;
        for (Unit unit : getSubUnits(instituteUnit.getUnitNumber())) {
            subUnits = subUnits + parentIdx + DASH + unit.getUnitNumber() + KRADConstants.BLANK_SPACE + COLUMN + KRADConstants.BLANK_SPACE + unit.getUnitName() + SEPARATOR;
            // we can make it more flexible, to add a while loop and with a 'depth' argument.
            numberOfUnits++;
            for (Unit unit1 : getSubUnits(unit.getUnitNumber())) {
                subUnits = subUnits + numberOfUnits + DASH + unit1.getUnitNumber() + KRADConstants.BLANK_SPACE + COLUMN + KRADConstants.BLANK_SPACE + unit1.getUnitName() + SEPARATOR;
            }
        }
        subUnits = subUnits.substring(0, subUnits.length() - 3);

        return subUnits;

    }

    @Override
    public String getInitialUnitsForUnitHierarchy(int depth) {
        Unit instituteUnit = getTopUnit();
        int parentIdx = 0;
        String subUnits = instituteUnit.getUnitNumber() + KRADConstants.BLANK_SPACE + COLUMN + KRADConstants.BLANK_SPACE + instituteUnit.getUnitName() + SEPARATOR;
        numberOfUnits = 0;
        for (Unit unit : getSubUnits(instituteUnit.getUnitNumber())) {
            subUnits = subUnits + parentIdx + DASH + unit.getUnitNumber() + KRADConstants.BLANK_SPACE + COLUMN + KRADConstants.BLANK_SPACE + unit.getUnitName() + SEPARATOR;
            // we can make it more flexible, to add a while loop and with a 'depth' argument.
            numberOfUnits++;
            if (depth - 2 > 0) {
                subUnits = subUnits + getSubUnits(unit, depth - 2);
            }
        }
        subUnits = subUnits.substring(0, subUnits.length() - 3);

        return subUnits;

    }

    protected String getSubUnits(Unit unit, int level) {
        String subUnits = "";
        int parentNum = numberOfUnits;
        level--;
        for (Unit unit1 : getSubUnits(unit.getUnitNumber())) {
            subUnits = subUnits + parentNum + DASH + unit1.getUnitNumber() + KRADConstants.BLANK_SPACE + COLUMN + KRADConstants.BLANK_SPACE + unit1.getUnitName() + SEPARATOR;
            numberOfUnits++;
            if (level > 0) {
                subUnits = subUnits + getSubUnits(unit1, level);
            }
        }
        return subUnits;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UnitAdministrator> retrieveUnitAdministratorsByUnitNumber(String unitNumber) {
        this.businessObjectService = KraServiceLocator.getService(BusinessObjectService.class);
        Map<String, String> queryMap = new HashMap<String, String>();
        queryMap.put(UNIT_NUMBER, unitNumber);
        List<UnitAdministrator> unitAdministrators
                = (List<UnitAdministrator>) getBusinessObjectService().findMatching(UnitAdministrator.class, queryMap);
        return unitAdministrators;
    }

    /**
     *
     * @see org.kuali.kra.service.UnitService#getMaxUnitTreeDepth()
     */
    @Override
    public int getMaxUnitTreeDepth() {
        /**
         * This function returns a higher number than the actual depth of the
         * hierarchy tree. This does not cause any problem as of yet. A closer
         * to accurate query would be: select count(distinct parent_unit_number)
         * as counter from unit where PARENT_UNIT_NUMBER is not null although
         * this to will result in a higher number than the true depth.
         *
         * @TODO fix this as time allows.
         */
        return getBusinessObjectService().countMatching(Unit.class, new HashMap<String, Object>());
    }

    public UnitLookupDao getUnitLookupDao() {
        return unitLookupDao;
    }

    public void setUnitLookupDao(UnitLookupDao unitLookupDao) {
        this.unitLookupDao = unitLookupDao;
    }

    /**
     * @see
     * org.kuali.kra.service.UnitService#retrieveUnitCorrespondentByUnitNumber(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<UnitCorrespondent> retrieveUnitCorrespondentsByUnitNumber(String unitNumber) {
        this.businessObjectService = KraServiceLocator.getService(BusinessObjectService.class);
        Map<String, String> queryMap = new HashMap<String, String>();
        queryMap.put(UNIT_NUMBER, unitNumber);
        List<UnitCorrespondent> unitCorrespondents
                = (List<UnitCorrespondent>) getBusinessObjectService().findMatching(UnitCorrespondent.class, queryMap);
        return unitCorrespondents;
    }

}
