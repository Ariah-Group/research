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
package org.kuali.kra.service.impl;

import org.kuali.kra.service.SystemAuthorizationService;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleQueryResults;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.krad.util.KRADPropertyConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * The System Authorization Service Implementation.
 *
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class SystemAuthorizationServiceImpl implements SystemAuthorizationService {  
    private PermissionService permissionService;
    private RoleService roleManagementService;
    private KimTypeInfoService kimTypeInfoService;
    
    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void setRoleManagementService(RoleService roleManagementService) {
        this.roleManagementService = roleManagementService;
    }

    protected KimTypeInfoService getKimTypeInfoService() {
        return kimTypeInfoService;
    }

    public void setKimTypeInfoService(KimTypeInfoService kimTypeInfoService) {
        this.kimTypeInfoService = kimTypeInfoService;
    }
    

    @Override
    public List<Role> getRolesForPermission(String permissionName, String namespaceCode) {
        List<String> roleResults = permissionService.getRoleIdsForPermission(namespaceCode, permissionName);
        return roleManagementService.getRoles(roleResults);
    }

    @Override
    public List<String> getRoleNamesForPermission(String permissionName, String namespaceCode) {
        List<String> roleNames = new ArrayList<String>();
        List<Role> roles = getRolesForPermission(permissionName, namespaceCode);
        for(Role role: roles) {
            roleNames.add(role.getName());
        }
        return roleNames;
    }

    @Override
    public List<String> getRoleIdsForPermission(String permissionName, String namespaceCode) {
        List<String> roleNames = new ArrayList<String>();
        List<Role> roles = getRolesForPermission(permissionName, namespaceCode);
        for(Role role: roles) {
            roleNames.add(role.getId());
        }
        return roleNames;
    }
    
    /**
     * @see org.kuali.kra.service.SystemAuthorizationService#getRoles(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Role> getRoles(String namespaceCode) {
        QueryByCriteria.Builder queryBuilder = QueryByCriteria.Builder.create();
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(PredicateFactory.equal(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode));
        predicates.add(PredicateFactory.equal(KRADPropertyConstants.ACTIVE, "Y"));
        queryBuilder.setPredicates(PredicateFactory.and(predicates.toArray(new Predicate[] {})));
        RoleQueryResults roleResults = roleManagementService.findRoles(queryBuilder.build());
        return roleResults.getResults();
    }

    @Override
    public KimType getKimTypeInfoForRole(Role role) {
        return getKimTypeInfoService().getKimType(role.getKimTypeId());
    }
}
