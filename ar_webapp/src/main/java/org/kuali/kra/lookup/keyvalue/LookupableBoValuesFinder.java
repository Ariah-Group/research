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
package org.kuali.kra.lookup.keyvalue;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;

import java.util.*;

public class LookupableBoValuesFinder extends UifKeyValuesFinderBase {

    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();

        DataDictionaryService dataDictionaryService = KraServiceLocator.getService(DataDictionaryService.class);
        // this only has entries that have been loaded - force load?

        try {

            Map<String, BusinessObjectEntry> businessObjectEntries = dataDictionaryService.getDataDictionary().getBusinessObjectEntries();

            Map<String, Integer> labelCounts = getLabelCounts(businessObjectEntries);

            for (String businessObject : businessObjectEntries.keySet()) {
                org.kuali.rice.kns.datadictionary.BusinessObjectEntry businessObjectEntry = (org.kuali.rice.kns.datadictionary.BusinessObjectEntry) businessObjectEntries.get(businessObject);
                if ((businessObjectEntry.hasLookupDefinition())
                        && (businessObject.startsWith("org.kuali.kra") || businessObject.startsWith("org.ariahgroup.research")
                        || businessObject.equals("org.kuali.rice.location.impl.campus.CampusBo")
                        || businessObject.equals("org.kuali.rice.location.impl.country.CountryBo")
                        || businessObject.equals("org.kuali.rice.location.impl.county.CountyBo")
                        || businessObject.equals("org.kuali.rice.location.impl.postalcode.PostalCodeBo")
                        || businessObject.equals("org.kuali.rice.location.impl.state.StateBo"))) {
                    String key = businessObject;

                    String label = StringUtils.removeEnd(businessObjectEntry.getLookupDefinition().getTitle().trim(), " Lookup");

                    if (labelCounts.get(label) > 1) {
                        label = label + " (" + key.substring(key.lastIndexOf('.') + 1) + ")";
                    }

                    KeyValue KeyValue = new ConcreteKeyValue(key, label);
                    if (!keyValues.contains(KeyValue)) {
                        keyValues.add(KeyValue);
                    }
                }
            }

            // added comparator below to alphabetize lists on label
            Collections.sort(keyValues, new KeyValueComparator());
            keyValues.add(0, new ConcreteKeyValue("", "select"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyValues;
    }

    public Map<String, Integer> getLabelCounts(Map<String, BusinessObjectEntry> businessObjectEntries) {
        Map<String, Integer> labels = new HashMap<String, Integer>();

        for (String businessObject : businessObjectEntries.keySet()) {
            org.kuali.rice.kns.datadictionary.BusinessObjectEntry businessObjectEntry = (org.kuali.rice.kns.datadictionary.BusinessObjectEntry) businessObjectEntries.get(businessObject);
            if ((businessObjectEntry.hasLookupDefinition())
                    && (businessObject.startsWith("org.kuali.kra") || businessObject.startsWith("org.ariahgroup.research")
                    || businessObject.equals("org.kuali.rice.location.impl.campus.CampusBo")
                    || businessObject.equals("org.kuali.rice.location.impl.country.CountryBo")
                    || businessObject.equals("org.kuali.rice.location.impl.county.CountyBo")
                    || businessObject.equals("org.kuali.rice.location.impl.postalcode.PostalCodeBo")
                    || businessObject.equals("org.kuali.rice.location.impl.state.StateBo"))) {
                String label = StringUtils.removeEnd(businessObjectEntry.getLookupDefinition().getTitle().trim(), " Lookup");
                if (labels.containsKey(label)) {
                    Integer count = labels.get(label) + 1;
                    labels.put(label, count);
                } else {
                    labels.put(label, 1);
                }
            }
        }

        return labels;
    }

}
