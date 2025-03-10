/*
 * Copyright 2015 The Ariah Group, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ariahgroup.research.proposaldevelopment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.proposaldevelopment.bo.DeadlineType;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;

/**
 *
 * @author Ariah Group, Inc.
 */
public class DeadlineTypeValuesFinder extends UifKeyValuesFinderBase {

    private BusinessObjectService businessObjectService;

    /**
     *
     */
    public DeadlineTypeValuesFinder() {
        businessObjectService = KraServiceLocator.getService(BusinessObjectService.class);
    }

    /**
     * Retrieve the key/value list of DeadlineTypes sorted by the sortOrder
     * field and filtered for records with an active field of Y.
     *
     * @return The key/value list of DeadlineTypes
     */
    @Override
    public List<KeyValue> getKeyValues() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("active", "Y");

        List<KeyValue> result = new ArrayList<KeyValue>();
        Collection<DeadlineType> types = getBusinessObjectService().findMatchingOrderBy(DeadlineType.class, values, "sortOrder", true);

        // this is NOT a required field so add a blank row
        ConcreteKeyValue emptypair = new ConcreteKeyValue();
        emptypair.setKey("");
        emptypair.setValue("select");
        result.add(emptypair);

        for (DeadlineType type : types) {
            ConcreteKeyValue pair = new ConcreteKeyValue();
            pair.setKey(type.getDeadlineTypeCode());
            pair.setValue(type.getDescription());
            result.add(pair);
        }
        return result;
    }

    /**
     *
     * @return
     */
    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }
}
