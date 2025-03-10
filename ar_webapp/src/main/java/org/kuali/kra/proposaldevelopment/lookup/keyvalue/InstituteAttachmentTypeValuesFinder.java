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
package org.kuali.kra.proposaldevelopment.lookup.keyvalue;

import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.lookup.keyvalue.KeyValueFinderService;
import org.kuali.kra.proposaldevelopment.bo.NarrativeType;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * This class is to get the drop down list of institute attachment type.
 */
public class InstituteAttachmentTypeValuesFinder extends UifKeyValuesFinderBase {

    private transient ParameterService parameterService;

    /**
     * Looks up and returns the ParameterService.
     *
     * @return the parameter service.
     */
    protected ParameterService getParameterService() {
        if (this.parameterService == null) {
            this.parameterService = KraServiceLocator.getService(ParameterService.class);
        }
        return this.parameterService;
    }

    private KeyValueFinderService keyValueFinderService = (KeyValueFinderService) KraServiceLocator.getService("keyValueFinderService");

    @Override
    public List<KeyValue> getKeyValues() {
        String instituteNarrativeTypeGroup = this.getParameterService().getParameterValueAsString(
                Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT,
                Constants.PARAMETER_COMPONENT_DOCUMENT,
                Constants.INSTITUTE_NARRATIVE_TYPE_GROUP);
        Map<String, String> queryMap = new HashMap<String, String>();
        queryMap.put("narrativeTypeGroup", instituteNarrativeTypeGroup);
        queryMap.put("systemGenerated", "N");
        return keyValueFinderService.getKeyValues(NarrativeType.class, "narrativeTypeCode", "description", queryMap);
    }
}
