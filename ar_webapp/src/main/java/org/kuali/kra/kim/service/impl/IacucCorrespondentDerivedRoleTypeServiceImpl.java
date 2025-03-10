/*
un * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kra.kim.service.impl;

import org.apache.commons.lang.StringUtils;
import org.ariahgroup.research.iacuc.service.IacucUnitService;
import org.kuali.kra.bo.IacucOrganizationCorrespondent;
import org.kuali.kra.bo.IacucUnitCorrespondent;
import org.kuali.kra.iacuc.IacucProtocol;
import org.kuali.kra.iacuc.summary.IacucProtocolSummary;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.kim.bo.KcKimAttributes;
import org.kuali.kra.protocol.protocol.location.ProtocolLocationBase;
import org.kuali.kra.service.OrganizationService;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.framework.role.RoleTypeService;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Checks whether the principal is an IrbCorrespondent for the given Organization ID.
 */
public class IacucCorrespondentDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase implements RoleTypeService {
    
    private OrganizationService organizationService;
    private IacucUnitService unitService;

    private final String ROLE_NAME_ORGANIZATION_CORRESPONDENT = "IACUC Organization Correspondent";
    private final String ROLE_NAME_UNIT_CORRESPONDENT = "IACUC Unit Correspondent";
    private final String PERFORMING_ORG_TYPE_CODE = "1";
    
    protected List<String> requiredAttributes = new ArrayList<String>();
    {
        requiredAttributes.add(KcKimAttributes.PROTOCOL);
    }
    
    @Override
    public boolean hasDerivedRole(String principalId, List<String> groupIds, String namespaceCode, 
                                      String roleName, Map<String,String> qualification) {
        if (ROLE_NAME_ORGANIZATION_CORRESPONDENT.equals(roleName)) {
            return hasApplicationRoleOrganization(principalId, groupIds, namespaceCode, roleName, qualification);
        } else if (ROLE_NAME_UNIT_CORRESPONDENT.equals(roleName)) {
            return hasApplicationRoleUnit(principalId, groupIds, namespaceCode, roleName, qualification);
        }
        return false;
    }
    
    public boolean hasApplicationRoleOrganization(String principalId, List<String> groupIds, String namespaceCode, 
                                                  String roleName, Map<String,String> qualification) {

        String protocolNumber = qualification.get(KcKimAttributes.PROTOCOL);
        IacucProtocol protocol = getProtocolByNumber(protocolNumber);
        if (protocol != null) {
            for (ProtocolLocationBase location : protocol.getProtocolLocations()) {
                if (StringUtils.equals(location.getProtocolOrganizationTypeCode(), PERFORMING_ORG_TYPE_CODE)) {
                    List<IacucOrganizationCorrespondent> organizationCorrespondents = 
                            getOrganizationService().retrieveIacucOrganizationCorrespondentsByOrganizationId(location.getOrganizationId());
                    for (IacucOrganizationCorrespondent organizationCorrespondent : organizationCorrespondents) {
                        if (organizationCorrespondent.getPersonId().equals(principalId)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
    
    public boolean hasApplicationRoleUnit(String principalId, List<String> groupIds, String namespaceCode, 
                                          String roleName, Map<String,String> qualification) {

        String protocolNumber = qualification.get(KcKimAttributes.PROTOCOL);
        IacucProtocol protocol = getProtocolByNumber(protocolNumber);
        String unitNumber = protocol.getLeadUnitNumber();
        if (StringUtils.isNotBlank(unitNumber)) {
            List<IacucUnitCorrespondent> unitCorrespondents = getUnitService().retrieveIacucUnitCorrespondentsByUnitNumber(unitNumber);
            for (IacucUnitCorrespondent unitCorrespondent : unitCorrespondents) {
                if (unitCorrespondent.getPersonId().equals(principalId)) {
                    return true;
                }
            }
        }
        return false;
    }
        
        
    @Override
    public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String,String> qualification) {
        validateRequiredAttributesAgainstReceived(qualification);
        String protocolNumber = qualification.get(KcKimAttributes.PROTOCOL);
        if (protocolNumber != null) {
            IacucProtocol protocol = getProtocolByNumber(protocolNumber);
            if (ROLE_NAME_ORGANIZATION_CORRESPONDENT.equals(roleName)) {
                return getRoleMembersFromApplicationRoleOrganization(protocol, namespaceCode, roleName, qualification);
            } else if (ROLE_NAME_UNIT_CORRESPONDENT.equals(roleName)) {
                return getRoleMembersFromApplicationRoleUnit(protocol, namespaceCode, roleName, qualification);
            }
        }
        return new ArrayList<RoleMembership>();
    }

    public List<RoleMembership> getRoleMembersFromApplicationRoleOrganization(IacucProtocol protocol, String namespaceCode, String roleName, Map<String,String> qualification) {
        IacucProtocolSummary protocolSummary = (IacucProtocolSummary) protocol.getProtocolSummary();
        List<RoleMembership> members = new ArrayList<RoleMembership>();
        
        for (org.kuali.kra.protocol.summary.OrganizationSummary orgSummary: protocolSummary.getOrganizations()) {
            String organizationId = orgSummary.getId();
            if (StringUtils.isNotBlank(organizationId)) {
                List<IacucOrganizationCorrespondent> organizationCorrespondents = getOrganizationService().retrieveIacucOrganizationCorrespondentsByOrganizationId(organizationId);
                for ( IacucOrganizationCorrespondent organizationCorrespondent : organizationCorrespondents ) {
                    if ( StringUtils.isNotBlank(organizationCorrespondent.getPersonId()) ) {
                        members.add( RoleMembership.Builder.create(null, null, organizationCorrespondent.getPersonId(), MemberType.PRINCIPAL, null).build() );
                    }
                }
            }
        }
            
        return members;
    }

    public List<RoleMembership> getRoleMembersFromApplicationRoleUnit(IacucProtocol protocol, String namespaceCode, String roleName, Map<String,String> qualification) {
        String unitNumber = protocol.getLeadUnitNumber();
        List<RoleMembership> members = new ArrayList<RoleMembership>();
        
        if (StringUtils.isNotBlank(unitNumber)) {
            List<IacucUnitCorrespondent> unitCorrespondents = getUnitService().retrieveIacucUnitCorrespondentsByUnitNumber(unitNumber);
            for ( IacucUnitCorrespondent unitCorrespondent : unitCorrespondents ) {
                if ( StringUtils.isNotBlank(unitCorrespondent.getPersonId()) ) {
                    members.add( RoleMembership.Builder.create(null, null, unitCorrespondent.getPersonId(), MemberType.PRINCIPAL, null).build() );
                }
            }
        }
            
        return members;
    }
    
    private IacucUnitService getUnitService() {
        if (unitService == null) {
            unitService = (IacucUnitService) KraServiceLocator.getService(IacucUnitService.class);
        }
        return unitService;
    }

    private OrganizationService getOrganizationService() {
        if (organizationService == null) {
            organizationService = (OrganizationService) KraServiceLocator.getService(OrganizationService.class);
        }
        return organizationService;
    }

    private IacucProtocol getProtocolByNumber(String protocolNumber) {
        Map<String,Object> keymap = new HashMap<String,Object>();
        keymap.put( "protocolNumber", protocolNumber);
        keymap.put("active", "Y");
        for(IacucProtocol protocol : getBusinessObjectService().findMatching(IacucProtocol.class, keymap)) {
            return protocol;
        }
        return null;    
    }

    /*
     * Should override if derivedRoles should not to be cached.  Currently defaulting to system-wide default.
     */
    @Override
    public boolean dynamicRoleMembership(String namespaceCode, String roleName) {
        super.dynamicRoleMembership(namespaceCode, roleName);
        return true;
    }
}
