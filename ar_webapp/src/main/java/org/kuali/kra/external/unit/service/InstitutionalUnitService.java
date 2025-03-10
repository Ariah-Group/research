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
package org.kuali.kra.external.unit.service;

import org.kuali.kra.external.HashMapElement;
import org.kuali.kra.external.unit.UnitDTO;
import org.kuali.kra.infrastructure.Constants;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.List;

/**
 * This is the external service that is published to the service bus for
 * accessing Institutional Unit information stored in the system.
 * 
 * @author Kuali Coeus Development Team
 */
@WebService(name = "institutionalUnitService", targetNamespace = Constants.FINANCIAL_INTEGRATION_KC_SERVICE_NAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface InstitutionalUnitService {
    
    /**
     * Get the Unit record corresponding to the given unit number.
     * 
     * @param unitNumber
     * @return UnitDTO
     */
    UnitDTO getUnit( @WebParam(name="unitNumber") String unitNumber);
    
    /**
     * Lookup Units according to the given search criteria.
     * 
     * @param searchCriteria Key-value pair map of search criteria.
     * @return List<UnitDTO>
     */
    List<UnitDTO> lookupUnits( @WebParam(name="searchCriteria") List<HashMapElement> criteria);
    
    /**
     * Retrieve the parent units of the given unit number.  The list will be in 
     * the order of ancestry (parent at index 0, grandparent at index 1, etc).
     * 
     * @param unitNumber
     * @return List<String>
     */
    List<String> getParentUnits( @WebParam(name="unitNumber") String unitNumber);
}
