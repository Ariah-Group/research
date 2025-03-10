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
package org.kuali.kra.budget.lookup.keyvalue;

import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;

import java.util.ArrayList;
import java.util.List;

public class BudgetRatesLocationValuesFinder extends UifKeyValuesFinderBase {

    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue("", "View All"));
        keyValues.add(new ConcreteKeyValue(Constants.ON_CAMPUS_FLAG, "On CampusContractContract"));
        keyValues.add(new ConcreteKeyValue(Constants.OFF_CAMPUS_FLAG, "Off CampusContractContract"));
        return keyValues;
        
    }
}
