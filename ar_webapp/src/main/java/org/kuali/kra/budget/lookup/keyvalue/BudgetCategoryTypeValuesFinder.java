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

import org.kuali.kra.budget.core.BudgetCategoryType;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.kuali.rice.krad.service.BusinessObjectService;

public class BudgetCategoryTypeValuesFinder extends UifKeyValuesFinderBase {
    
    /**
     * Constructs the list of Budget Periods.  Each entry
     * in the list is a &lt;key, value&gt; pair, where the "key" is the unique
     * status code and the "value" is the textual description that is viewed
     * by a user.  The list is obtained from the BudgetDocument if any are defined there. 
     * Otherwise, it is obtained from a lookup of the BUDGET_PERIOD database table
     * via the "KeyValueFinderService".
     * 
     * @return the list of &lt;key, value&gt; pairs of abstract types.  The first entry
     * is always &lt;"", "select:"&gt;.
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        BusinessObjectService busService = (BusinessObjectService) KraServiceLocator.getService("businessObjectService");
        
        Map<String,String> queryMap = new HashMap<String,String>();
        queryMap.put("active", "Y");
        
        Collection<BudgetCategoryType> budgetCategoryTypes = busService.findMatchingOrderBy(BudgetCategoryType.class,queryMap ,"sortId",true);
        
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        
        for (Iterator iter = budgetCategoryTypes.iterator(); iter.hasNext();) {
            BudgetCategoryType budgetCategoryType = (BudgetCategoryType) iter.next();
            keyValues.add(new ConcreteKeyValue(budgetCategoryType.getBudgetCategoryTypeCode(), budgetCategoryType.getDescription()));                            
        }
        return keyValues;
    }
}
