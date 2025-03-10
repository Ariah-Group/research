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
package org.kuali.kra.award.contacts;

import org.kuali.kra.award.AwardForm;
import org.kuali.kra.bo.KcPerson;
import org.kuali.kra.bo.UnitAdministrator;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.service.KcPersonService;
import org.kuali.kra.service.UnitService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides support for the Award Contacts Project Personnel panel
 */
public class AwardCentralAdminContactsBean implements Serializable {

    private static final long serialVersionUID = 7614119508539116964L;
    protected AwardForm awardForm;
    private List<AwardUnitContact> centralAdminContacts;
    private static final String DEFAULT_GROUP_CODE_FOR_CENTRAL_ADMIN_CONTACTS = "C";

    public AwardCentralAdminContactsBean(AwardForm awardForm) {
        this.awardForm = awardForm;
    }

    public void initCentralAdminContacts() {
        centralAdminContacts = new ArrayList<AwardUnitContact>();
        List<UnitAdministrator> unitAdministrators = getUnitService().retrieveUnitAdministratorsByUnitNumber(awardForm.getAwardDocument().getAward().getUnitNumber());
        for (UnitAdministrator unitAdministrator : unitAdministrators) {
            if (unitAdministrator.getUnitAdministratorType().getDefaultGroupFlag().equals(DEFAULT_GROUP_CODE_FOR_CENTRAL_ADMIN_CONTACTS)) {
                KcPerson person = getKcPersonService().getKcPersonByPersonId(unitAdministrator.getPersonId());
                AwardUnitContact newAwardUnitContact = new AwardUnitContact();
                newAwardUnitContact.setAward(awardForm.getAwardDocument().getAward());
                newAwardUnitContact.setPerson(person);
                newAwardUnitContact.setUnitAdministratorType(unitAdministrator.getUnitAdministratorType());
                newAwardUnitContact.setFullName(person.getFullName());
                centralAdminContacts.add(newAwardUnitContact);
            }
        }
    }

    public UnitService getUnitService() {
        return (UnitService) KraServiceLocator.getService(UnitService.class);
    }

    public KcPersonService getKcPersonService() {
        return (KcPersonService) KraServiceLocator.getService(KcPersonService.class);
    }

    /**
     * This method finds the count of AwardContacts in the "Central
     * Administrator" category
     *
     * @return The list; may be empty
     */
    public List<AwardUnitContact> getCentralAdminContacts() {
        return centralAdminContacts;
    }

    /**
     * This method finds the count of AwardContacts in the "Unit Contacts"
     * category
     *
     * @return The count; may be 0
     */
    public int getCentralAdminContactsCount() {
        if (getCentralAdminContacts() == null || getCentralAdminContacts().isEmpty()) {
            return 0;
        }
        return getCentralAdminContacts().size();
    }

}
