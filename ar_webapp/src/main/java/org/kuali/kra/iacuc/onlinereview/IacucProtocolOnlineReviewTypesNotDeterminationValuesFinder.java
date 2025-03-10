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
 *
 * --------------------------------
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
package org.kuali.kra.iacuc.onlinereview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kra.iacuc.actions.submit.IacucProtocolReviewType;
import org.kuali.kra.lookup.keyvalue.ExtendedPersistableBusinessObjectValuesFinder;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KeyValuesService;

public class IacucProtocolOnlineReviewTypesNotDeterminationValuesFinder extends ExtendedPersistableBusinessObjectValuesFinder {
        
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -4118821657613491616L;
    
    
    //value should match org.kuali.kra.maintenanceIacucProtocolOnlineReviewDeterminationTypeRecommendationMaintainableImpl.DEFAULT_SELECTION
    private static final String DEFAULT_SELECTION = "select"; 
    
    KeyValuesService keyValuesService = null;

    
    
    @Override
    public List<KeyValue> getKeyValues() {        
        Collection<IacucProtocolOnlineReviewDeterminationTypeRecommendation> reviewTypesUsedInDetermination = getKeyValuesService().findAll(IacucProtocolOnlineReviewDeterminationTypeRecommendation.class);
        Collection<IacucProtocolReviewType> allReviewTypes = getKeyValuesService().findAll(IacucProtocolReviewType.class);
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue("", DEFAULT_SELECTION));
        boolean found = false;
        
        for (IacucProtocolReviewType reviewType : allReviewTypes) {            
            for (IacucProtocolOnlineReviewDeterminationTypeRecommendation reviewTypesUsed : reviewTypesUsedInDetermination) {
                if (reviewType.getReviewTypeCode() != null && reviewTypesUsed.getIacucProtocolReviewTypeCode() != null && 
                    reviewType.getReviewTypeCode().equalsIgnoreCase(reviewTypesUsed.getIacucProtocolReviewTypeCode())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                String description = reviewType.getReviewTypeCode() + " - " + reviewType.getDescription();
                keyValues.add(new ConcreteKeyValue(reviewType.getReviewTypeCode(), description));
            }
            found = false;
        }       
        
        return keyValues;
    }
    

    protected KeyValuesService getKeyValuesService() {
        if (keyValuesService == null) {
            keyValuesService =  KRADServiceLocator.getKeyValuesService();
        }
        return keyValuesService;
    }
    
}
