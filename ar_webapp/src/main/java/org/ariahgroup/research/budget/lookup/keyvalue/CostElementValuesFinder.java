/**
 * Copyright 2015 The Ariah Group, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.ariahgroup.research.budget.lookup.keyvalue;

import org.kuali.kra.budget.core.CostElement;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;

import java.util.*;
import org.kuali.rice.krad.service.BusinessObjectService;

public class CostElementValuesFinder extends UifKeyValuesFinderBase {

    private String budgetCategoryTypeCode;

    /**
     * Constructs the list of Budget Periods. Each entry in the list is a
     * &lt;key, value&gt; pair, where the "key" is the unique status code and
     * the "value" is the textual description that is viewed by a user. The list
     * is obtained from the BudgetDocument if any are defined there. Otherwise,
     * it is obtained from a lookup of the BUDGET_PERIOD database table via the
     * "KeyValueFinderService".
     *
     * @return the list of &lt;key, value&gt; pairs of abstract types. The first
     * entry is always &lt;"", "select:"&gt;.
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
//        KeyValuesService keyValuesService = KraServiceLocator.getService("keyValuesService");
        List<KeyValue> keyValues = new ArrayList<KeyValue>();

        Map<String, String> paramFields = new HashMap<String, String>();
        paramFields.put("active", "Y");
        paramFields.put("budgetCategory.budgetCategoryTypeCode", getBudgetCategoryTypeCode());

        Collection<CostElement> costElements = (Collection<CostElement>) KraServiceLocator
                .getService(BusinessObjectService.class).findMatchingOrderBy(CostElement.class, paramFields, "description", true);

        for (CostElement costElement : costElements) {
            keyValues.add(new ConcreteKeyValue(costElement.getCostElement(), costElement.getDescription()));
        }
//        Collection<BudgetCategory> budgetCategoryCodes = keyValuesService.findAll(BudgetCategory.class);
//
//        for (CostElement costElement : costElements) {
//            for(BudgetCategory budgetCategory : budgetCategoryCodes){
//                if (costElement.getBudgetCategoryCode().equalsIgnoreCase(budgetCategory.getBudgetCategoryCode())){
//                    if (StringUtils.equalsIgnoreCase(budgetCategory.getBudgetCategoryTypeCode(),getBudgetCategoryTypeCode())){
//                        if (costElement.isActive()) {
//                            keyValues.add(new ConcreteKeyValue(costElement.getCostElement(), costElement.getDescription()));
//                        }
//                    }
//                }
//            } 
//        }
        // added comparator below to alphabetize lists on label
        //Collections.sort(keyValues, new KeyValueComparator());
        keyValues.add(0, new ConcreteKeyValue("", "select"));
        return keyValues;
    }

    public String getBudgetCategoryTypeCode() {
        return budgetCategoryTypeCode;
    }

    public void setBudgetCategoryTypeCode(String budgetCategoryTypeCode) {
        this.budgetCategoryTypeCode = budgetCategoryTypeCode;
    }

//    class KeyValueComparator implements Comparator<KeyValue> {
//
//        public int compare(KeyValue o1, KeyValue o2) {
//            return o1.getValue().compareToIgnoreCase(o2.getValue());
//        }
//    }
}
