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
package org.kuali.kra.protocol.protocol.location;

import org.kuali.kra.bo.Organization;
import org.kuali.kra.bo.Rolodex;
import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.service.OrganizationService;

public abstract class ProtocolLocationServiceImplBase implements ProtocolLocationService {

    private OrganizationService organizationService;
    private static final String REFERENCE_PROTOCOL_ORGANIZATION_TYPE = "protocolOrganizationType";
    private static final String REFERENCE_ORGANIZATION = "organization";
    private static final String REFERENCE_ROLODEX = "rolodex";
    private static final String PROTOCOL_NUMBER = "0";
    private static final Integer SEQUENCE_NUMBER = 0;

    /**
     * @param protocolLocation
     * @see
     * org.kuali.kra.protocol.protocol.location.ProtocolLocationService#addProtocolLocation(org.kuali.kra.protocol.ProtocolBase,
     * org.kuali.kra.protocol.protocol.location.ProtocolLocationBase)
     */
    @Override
    public void addProtocolLocation(ProtocolBase protocol, ProtocolLocationBase protocolLocation) {

        //TODO Framework problem of 2 saves
        protocolLocation.setProtocolNumber(PROTOCOL_NUMBER);
        protocolLocation.setSequenceNumber(SEQUENCE_NUMBER);
        protocolLocation.refreshReferenceObject(REFERENCE_PROTOCOL_ORGANIZATION_TYPE);
        protocolLocation.refreshReferenceObject(REFERENCE_ORGANIZATION);
        protocolLocation.setRolodexId(protocolLocation.getOrganization().getContactAddressId());
        protocolLocation.refreshReferenceObject(REFERENCE_ROLODEX);
        protocol.getProtocolLocations().add(protocolLocation);
    }

    /**
     * @see
     * org.kuali.kra.protocol.protocol.location.ProtocolLocationService#addDefaultProtocolLocation(org.kuali.kra.protocol.ProtocolBase)
     */
    @Override
    public void addDefaultProtocolLocation(ProtocolBase protocol) {
        if (protocol.getProtocolLocations().isEmpty()) {
            ProtocolLocationBase protocolLocation = getNewProtocolLocationInstanceHook();
            protocolLocation.setProtocolNumber(PROTOCOL_NUMBER);
            protocolLocation.setSequenceNumber(SEQUENCE_NUMBER);
            Organization organization = getOrganization(getDefaultProtocolOrganizationIdHook());
            protocolLocation.setOrganization(organization);
            protocolLocation.setOrganizationId(organization.getOrganizationId());
            protocolLocation.setRolodexId(organization.getContactAddressId());
            protocolLocation.setProtocolOrganizationTypeCode(getDefaultProtocolOrganizationTypeCodeHook());
            protocolLocation.refreshReferenceObject(REFERENCE_PROTOCOL_ORGANIZATION_TYPE);
            protocolLocation.refreshReferenceObject(REFERENCE_ROLODEX);
            protocol.getProtocolLocations().add(protocolLocation);
        }
    }

    protected abstract String getDefaultProtocolOrganizationIdHook();

    protected abstract String getDefaultProtocolOrganizationTypeCodeHook();

    protected abstract ProtocolLocationBase getNewProtocolLocationInstanceHook();

    /**
     * @see
     * org.kuali.kra.protocol.protocol.location.ProtocolLocationService#clearProtocolLocation(org.kuali.kra.protocol.ProtocolBase,
     * int)
     */
    @Override
    public void clearProtocolLocationAddress(ProtocolBase protocol, int lineNumber) {

        protocol.getProtocolLocations().get(lineNumber).setRolodexId(0);
        protocol.getProtocolLocations().get(lineNumber).setRolodex(new Rolodex());

    }

    /**
     * This method is to get default organization. Method is linked to
     * organization service.
     *
     * @param organizationId
     * @return Organ
     */
    protected Organization getOrganization(String organizationId) {
        return organizationService.getOrganization(organizationId);
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param organizationService
     */
    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

}
