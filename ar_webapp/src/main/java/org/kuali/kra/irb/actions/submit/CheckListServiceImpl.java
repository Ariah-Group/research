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
package org.kuali.kra.irb.actions.submit;

import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The implementation of the Check List Service.
 */
public class CheckListServiceImpl implements CheckListService {

    private BusinessObjectService businessObjectService;

    /**
     * Inject the Business Object Service.
     *
     * @param businessObjectService the Business Object Service
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * @see
     * org.kuali.kra.irb.actions.submit.CheckListService#getExpeditedReviewCheckList()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ExpeditedReviewCheckListItem> getExpeditedReviewCheckList() {
        Collection<ExpeditedReviewCheckListItem> items = businessObjectService.findAllOrderBy(ExpeditedReviewCheckListItem.class, "description", true);
        List<ExpeditedReviewCheckListItem> checkList = new ArrayList<ExpeditedReviewCheckListItem>();
        checkList.addAll(items);
        return checkList;
    }

    /**
     * @see
     * org.kuali.kra.irb.actions.submit.CheckListService#getExemptStudiesCheckList()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ExemptStudiesCheckListItem> getExemptStudiesCheckList() {
        Collection<ExemptStudiesCheckListItem> items = businessObjectService.findAllOrderBy(ExemptStudiesCheckListItem.class, "description", true);
        List<ExemptStudiesCheckListItem> checkList = new ArrayList<ExemptStudiesCheckListItem>();
        checkList.addAll(items);
        return checkList;
    }
}
