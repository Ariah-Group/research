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
import org.kuali.kra.infrastructure.RoleConstants;
import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.protocol.personnel.*;
import org.kuali.kra.service.KraAuthorizationService;

public class ProtocolPersonnelServiceImpl extends ProtocolPersonnelServiceImplBase implements ProtocolPersonnelService {

    @Override
    protected ProtocolUnitBase createNewProtocolUnitInstanceHook() {
        return new ProtocolUnit();
    }

    @Override
    protected String getSequenceNumberNameHook() {
        return ProtocolPerson.SEQUENCE_NAME_IRB_PROTOCOL_PERSONID;
    }

    @Override
    public Class<? extends ProtocolPersonRoleMappingBase> getProtocolPersonRoleMappingClassHook() {
        return ProtocolPersonRoleMapping.class;
    }

    @Override
    public Class<? extends ProtocolPersonRoleBase> getProtocolPersonRoleClassHook() {
        return ProtocolPersonRole.class;
    }

    /**
     * {@inheritDoc}
     *
     * @see
     * org.kuali.kra.irb.personnel.ProtocolPersonnelService#setPrincipalInvestigator(org.kuali.kra.irb.personnel.ProtocolPerson,
     * org.kuali.kra.irb.Protocol)
     */
    @Override
    public void setPrincipalInvestigator(ProtocolPersonBase newPrincipalInvestigator, ProtocolBase protocol) {
        if (protocol != null) {
            ProtocolPerson currentPrincipalInvestigator = (ProtocolPerson) getPrincipalInvestigator(protocol.getProtocolPersons());

            if (newPrincipalInvestigator != null) {
                newPrincipalInvestigator.setProtocolPersonRoleId(getPrincipalInvestigatorRole());
                if (currentPrincipalInvestigator == null) {
                    protocol.getProtocolPersons().add(newPrincipalInvestigator);
                } else if (!isDuplicatePerson(protocol.getProtocolPersons(), newPrincipalInvestigator)) {
                    protocol.getProtocolPersons().remove(currentPrincipalInvestigator);
                    protocol.getProtocolPersons().add(newPrincipalInvestigator);
                }

                // Assign the PI the APPROVER role if PI has a personId (for doc cancel).
                if (newPrincipalInvestigator.getPersonId() != null) {
                    personEditableService.populateContactFieldsFromPersonId(newPrincipalInvestigator);
                    KraAuthorizationService kraAuthService = KraServiceLocator.getService(KraAuthorizationService.class);
                    kraAuthService.addRole(newPrincipalInvestigator.getPersonId(), RoleConstants.PROTOCOL_APPROVER, protocol);
                } else {
                    personEditableService.populateContactFieldsFromRolodexId(newPrincipalInvestigator);
                }
            }
        }
    }
}
