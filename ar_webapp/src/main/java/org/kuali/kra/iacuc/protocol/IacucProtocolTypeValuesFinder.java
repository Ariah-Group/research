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
package org.kuali.kra.iacuc.protocol;

import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.service.KeyValuesService;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class IacucProtocolTypeValuesFinder extends UifKeyValuesFinderBase {

    private static final long serialVersionUID = -3626497316348500710L;

    /**
     * Constructs the list of Protocol Types.  Each entry
     * in the list is a &lt;key, value&gt; pair, where the "key" is the unique
     * status code and the "value" is the textual description that is viewed
     * by a user.  The list is obtained from the IACUC_PROTOCOL_TYPE database table
     * via the "KeyValueFinderService".
     * 
     * @return the list of &lt;key, value&gt; pairs of abstract types.  The first entry
     * is always &lt;"", "select:"&gt;.
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */

    @Override
    public List<KeyValue> getKeyValues() {
        KeyValuesService keyValuesService = (KeyValuesService) KraServiceLocator.getService("keyValuesService");
        Collection protocolTypes = keyValuesService.findAllOrderBy(IacucProtocolType.class,"description",true);
        List<KeyValue> keyValues = new ArrayList<KeyValue>();   
        for (Iterator iter = protocolTypes.iterator(); iter.hasNext();) {
            IacucProtocolType protocolType = (IacucProtocolType) iter.next();
            keyValues.add(new ConcreteKeyValue(protocolType.getProtocolTypeCode(), protocolType.getDescription()));
        }
        return keyValues;
    }  
}
