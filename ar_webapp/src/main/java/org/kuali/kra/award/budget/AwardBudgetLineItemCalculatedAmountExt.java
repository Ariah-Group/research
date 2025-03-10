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
package org.kuali.kra.award.budget;

import org.kuali.kra.budget.BudgetDecimal;
import org.kuali.kra.budget.nonpersonnel.BudgetLineItemCalculatedAmount;

/**
 * This class...
 */
public class AwardBudgetLineItemCalculatedAmountExt extends BudgetLineItemCalculatedAmount {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 8143479362083133558L;
    private BudgetDecimal obligatedAmount;

    /**
     * Gets the obligatedAmount attribute.
     *
     * @return Returns the obligatedAmount.
     */
    public BudgetDecimal getObligatedAmount() {
        return obligatedAmount == null ? BudgetDecimal.ZERO : obligatedAmount;
    }

    /**
     * Sets the obligatedAmount attribute value.
     *
     * @param obligatedAmount The obligatedAmount to set.
     */
    public void setObligatedAmount(BudgetDecimal obligatedAmount) {
        this.obligatedAmount = obligatedAmount;
    }
}
