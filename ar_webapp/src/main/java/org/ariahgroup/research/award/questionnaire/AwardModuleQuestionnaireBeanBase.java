/*
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
package org.ariahgroup.research.award.questionnaire;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.bo.CoeusModule;
import org.kuali.kra.bo.CoeusSubModule;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.krms.KrmsRulesContext;
import org.kuali.kra.questionnaire.answer.ModuleQuestionnaireBean;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Generic base bean for award questionnaires.
 *
 * @author The Ariah Group, Inc.
 */
public class AwardModuleQuestionnaireBeanBase extends ModuleQuestionnaireBean {

    private Award award;

    /**
     * Create a new instance of AwardModuleQuestionnaireBeanBase object using the specified Award parameter.
     * @param award Award to use to populate the bean.
     */
    public AwardModuleQuestionnaireBeanBase(Award award) {
        super(CoeusModule.AWARD_MODULE_CODE, award.getAwardNumber(), CoeusSubModule.ZERO_SUBMODULE, award.getSequenceNumber().toString(),
                award.getAwardDocument().getDocumentHeader().getWorkflowDocument().isApproved());
        this.award = award;
        setAwardSubItemCode(CoeusSubModule.ZERO_SUBMODULE);
    }

    /**
     * 
     * @param moduleItemCode
     * @param moduleItemKey
     * @param moduleSubItemCode
     * @param moduleSubItemKey
     * @param finalDoc 
     */
    public AwardModuleQuestionnaireBeanBase(String moduleItemCode, String moduleItemKey, String moduleSubItemCode, String moduleSubItemKey, boolean finalDoc) {
        super(moduleItemCode, moduleItemKey, moduleSubItemCode, moduleSubItemKey, finalDoc);
    }

    /**
     * 
     * @param subModuleCode 
     */
    protected void setAwardSubItemCode(String subModuleCode) {
        setModuleSubItemCode(subModuleCode);
    }

    /**
     * 
     * @return 
     */
    @Override
    public KrmsRulesContext getKrmsRulesContextFromBean() {
        if (getAward() != null) {
            return getAward().getKrmsRulesContext();
        } else {
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("awardNumber", getModuleItemKey());
            values.put("sequenceNumber", getModuleSubItemKey());
            List<Award> awards = (List<Award>) KraServiceLocator.getService(BusinessObjectService.class).findMatching(Award.class, values);
            if (awards != null && !awards.isEmpty()) {
                return awards.get(0).getAwardDocument();
            } else {
                return null;
            }
        }
    }

    /**
     * 
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        boolean retVal = false;
        if ((o != null) && (o instanceof AwardModuleQuestionnaireBeanBase)) {
            AwardModuleQuestionnaireBeanBase pmqb = (AwardModuleQuestionnaireBeanBase) o;
            retVal = ((this.getModuleItemCode().equals(pmqb.getModuleItemCode()))
                    && (this.getModuleItemKey().equals(pmqb.getModuleItemKey()))
                    && (this.getModuleSubItemCode().equals(pmqb.getModuleSubItemCode()))
                    && (this.getModuleSubItemKey().equals(pmqb.getModuleSubItemKey()))
                    && (this.isFinalDoc() == pmqb.isFinalDoc()));
        }
        return retVal;
    }

    /**
     * Get the award used by this bean.
     * @return 
     */
    public Award getAward() {
        return award;
    }

    /**
     * Set the award used by this bean.
     * @param award 
     */
    public void setAward(Award award) {
        this.award = award;
    }
}
