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
package org.kuali.kra.award.lookup.keyvalue;

import org.kuali.kra.award.home.InvInstructionsIndicatorConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Values Finder for Invoice Instructions Indicator Values.
 *
 */
public class InvInstructionsIndicatorValuesFinder extends UifKeyValuesFinderBase {

    private List<KeyValue> labels;

    @Override
    public List<KeyValue> getKeyValues() {
        if (labels != null) {
            return labels;
        }
        labels = new ArrayList<KeyValue>();

        for (InvInstructionsIndicatorConstants inv : InvInstructionsIndicatorConstants.values()) {
            labels.add(new ConcreteKeyValue(inv.getCode(), inv.toString()));
        }
        return labels;
    }

}
