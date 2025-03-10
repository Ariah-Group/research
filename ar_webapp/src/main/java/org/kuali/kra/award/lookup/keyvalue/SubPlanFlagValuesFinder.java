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
package org.kuali.kra.award.lookup.keyvalue;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * This class is a values finder for subPlanFlag of Award business object.
 */
@SuppressWarnings("unchecked")
public class SubPlanFlagValuesFinder extends UifKeyValuesFinderBase {

    /**
     * This method adds 3 pre-determined values to a key values pair and returns
     * it.
     *
     */
    @Override
    public List<KeyValue> getKeyValues() {

        List<KeyValue> keyValues = new ArrayList<KeyValue>();

        keyValues.add(new ConcreteKeyValue("U", "Unknown"));
        keyValues.add(new ConcreteKeyValue("Y", "Required"));
        keyValues.add(new ConcreteKeyValue("N", "Not Required"));

        return keyValues;
    }

}
