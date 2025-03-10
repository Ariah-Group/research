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
package org.kuali.kra.dao.ojb;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.dao.AwardLookupDao;
import org.kuali.kra.dao.VersionHistoryLookupDao;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.dao.impl.LookupDaoOjb;
import org.kuali.rice.krad.lookup.CollectionIncomplete;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class AwardLookupDaoOjb extends LookupDaoOjb implements AwardLookupDao {

    private VersionHistoryLookupDao versionHistoryLookupDao;

    @SuppressWarnings("rawtypes")
    @Override
    public List<? extends BusinessObject> getAwardSearchResults(Map fieldValues, boolean usePrimaryKeys) {
        List<Award> searchResults = (List<Award>) getVersionHistoryLookupDao().getSequenceOwnerSearchResults(Award.class, fieldValues, usePrimaryKeys);
        List<Long> awardIds = new ArrayList<Long>();

        for (Award awardSearchBo : searchResults) {
            awardIds.add(awardSearchBo.getAwardId());
        }

        if (awardIds.isEmpty()) {
            return new ArrayList<Award>();
        }
        Criteria awardCr = new Criteria();
        awardCr.addIn("awardId", awardIds);

        List<Award> awardList = (List<Award>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Award.class, awardCr));
        if (searchResults instanceof CollectionIncomplete) {
            awardList = new CollectionIncomplete<Award>(awardList, ((CollectionIncomplete<Award>) searchResults).getActualSizeIfTruncated());
        }
        return awardList;
    }

    public VersionHistoryLookupDao getVersionHistoryLookupDao() {
        return versionHistoryLookupDao;
    }

    public void setVersionHistoryLookupDao(VersionHistoryLookupDao versionHistoryLookupDao) {
        this.versionHistoryLookupDao = versionHistoryLookupDao;
    }
}
