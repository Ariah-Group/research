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
package org.kuali.kra.budget.nonpersonnel;

import org.kuali.kra.budget.BudgetDecimal;
import org.kuali.kra.budget.core.BudgetAssociate;
import org.kuali.kra.budget.core.BudgetCategory;
import org.kuali.kra.budget.core.BudgetService;
import org.kuali.kra.budget.core.CostElement;
import org.kuali.kra.budget.parameters.BudgetPeriod;
import org.kuali.kra.infrastructure.DeepCopyIgnore;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.krad.util.ObjectUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public abstract class BudgetLineItemBase extends BudgetAssociate {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 8356817148151906918L;

    @DeepCopyIgnore
    private Long budgetLineItemId;

    private Long budgetPeriodId;

    private Integer budgetPeriod;

    private Integer lineItemNumber;

    private Boolean applyInRateFlag;

    private String budgetJustification;

    private String costElement;

    private BudgetDecimal costSharingAmount = BudgetDecimal.ZERO;

    private Date endDate;

    private BudgetDecimal lineItemCost = BudgetDecimal.ZERO;

    private String lineItemDescription;

    private Boolean onOffCampusFlag;

    private Date startDate;

    private BudgetDecimal underrecoveryAmount = BudgetDecimal.ZERO;

    private String budgetCategoryCode;

    private Integer basedOnLineItem;

    private Integer quantity;

    private BudgetDecimal directCost;

    private BudgetDecimal indirectCost;

    private BudgetCategory budgetCategory;

    private Integer lineItemSequence;

    private Boolean submitCostSharingFlag = Boolean.TRUE;

    private CostElement costElementBO;

    private BudgetDecimal totalCostSharingAmount;

    private boolean validToApplyInRate;

    private String groupName;
    
    private Boolean formulatedCostElementFlag;
    
    private List<BudgetFormulatedCostDetail> budgetFormulatedCosts;

    //ignore the budget period bo during deep copy as any link up the budget object graph
    //will cause generateAllPeriods to consume large amounts of memory
    @DeepCopyIgnore
    private BudgetPeriod budgetPeriodBO;

    public String getGroupName() {
        return groupName;
    }

    public String getCostElementName() {
        if (costElementBO != null) {
            return costElementBO.getDescription();
        } else {
            return "";
        }
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public BudgetLineItemBase() {
        budgetFormulatedCosts = new ArrayList<BudgetFormulatedCostDetail>();
    }

    public abstract List getBudgetCalculatedAmounts();

    /**
     * Gets the directCost attribute. 
     * @return Returns the directCost.
     */
    public BudgetDecimal getDirectCost() {
        return BudgetDecimal.returnZeroIfNull(directCost);
    }

    /**
     * Sets the directCost attribute value.
     * @param directCost The directCost to set.
     */
    public void setDirectCost(BudgetDecimal directCost) {
        this.directCost = directCost;
    }

    /**
     * Gets the indirectCost attribute. 
     * @return Returns the indirectCost.
     */
    public BudgetDecimal getIndirectCost() {
        return BudgetDecimal.returnZeroIfNull(indirectCost);
    }

    /**
     * Sets the indirectCost attribute value.
     * @param indirectCost The indirectCost to set.
     */
    public void setIndirectCost(BudgetDecimal indirectCost) {
        this.indirectCost = indirectCost;
    }

    public Integer getBudgetPeriod() {
        return budgetPeriod;
    }

    public void setBudgetPeriod(Integer budgetPeriod) {
        this.budgetPeriod = budgetPeriod;
    }

    public Integer getLineItemNumber() {
        return lineItemNumber;
    }

    public void setLineItemNumber(Integer lineItemNumber) {
        this.lineItemNumber = lineItemNumber;
    }

    public Boolean getApplyInRateFlag() {
        return applyInRateFlag;
    }

    public void setApplyInRateFlag(Boolean applyInRateFlag) {
        this.applyInRateFlag = applyInRateFlag;
    }

    public Integer getBasedOnLineItem() {
        return basedOnLineItem;
    }

    public void setBasedOnLineItem(Integer basedOnLineItem) {
        this.basedOnLineItem = basedOnLineItem;
    }

    public String getBudgetCategoryCode() {
        return budgetCategoryCode;
    }

    public void setBudgetCategoryCode(String budgetCategoryCode) {
        this.budgetCategoryCode = budgetCategoryCode;
    }

    public String getBudgetJustification() {
        return budgetJustification;
    }

    public void setBudgetJustification(String budgetJustification) {
        this.budgetJustification = budgetJustification;
    }

    public String getCostElement() {
        return costElement;
    }

    public void setCostElement(String costElement) {
        this.costElement = costElement;
    }

    public BudgetDecimal getCostSharingAmount() {
        return BudgetDecimal.returnZeroIfNull(costSharingAmount);
    }

    public void setCostSharingAmount(BudgetDecimal costSharingAmount) {
        this.costSharingAmount = costSharingAmount;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BudgetDecimal getLineItemCost() {
        return BudgetDecimal.returnZeroIfNull(lineItemCost);
    }

    public void setLineItemCost(BudgetDecimal lineItemCost) {
        this.lineItemCost = lineItemCost;
    }

    public String getLineItemDescription() {
        return lineItemDescription;
    }

    public void setLineItemDescription(String lineItemDescription) {
        this.lineItemDescription = lineItemDescription;
    }

    public Integer getLineItemSequence() {
        return lineItemSequence;
    }

    public void setLineItemSequence(Integer lineItemSequence) {
        this.lineItemSequence = lineItemSequence;
    }

    public Boolean getOnOffCampusFlag() {
        return onOffCampusFlag;
    }

    public void setOnOffCampusFlag(Boolean onOffCampusFlag) {
        this.onOffCampusFlag = onOffCampusFlag;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public BudgetDecimal getUnderrecoveryAmount() {
        return BudgetDecimal.returnZeroIfNull(underrecoveryAmount);
    }

    public void setUnderrecoveryAmount(BudgetDecimal underrecoveryAmount) {
        this.underrecoveryAmount = underrecoveryAmount;
    }

    public BudgetCategory getBudgetCategory() {
        return budgetCategory;
    }

    public void setBudgetCategory(BudgetCategory budgetCategory) {
        this.budgetCategory = budgetCategory;
    }

    public CostElement getCostElementBO() {
        return costElementBO;
    }

    public void setCostElementBO(CostElement costElementBO) {
        this.costElementBO = costElementBO;
    }

    public BudgetDecimal getTotalCostSharingAmount() {
        return BudgetDecimal.returnZeroIfNull(totalCostSharingAmount);
    }

    public void setTotalCostSharingAmount(BudgetDecimal totalCostSharingAmount) {
        this.totalCostSharingAmount = totalCostSharingAmount;
    }

    public Long getBudgetPeriodId() {
        return budgetPeriodId;
    }

    public void setBudgetPeriodId(Long budgetPeriodId) {
        this.budgetPeriodId = budgetPeriodId;
    }

    public boolean isValidToApplyInRate() {
        return KraServiceLocator.getService(BudgetService.class).validInflationCeRate(this);
    }

    public void setValidToApplyInRate(boolean validToApplyInRate) {
        this.validToApplyInRate = validToApplyInRate;
    }

    /**
     * Gets the budgetLineItemId attribute. 
     * @return Returns the budgetLineItemId.
     */
    public Long getBudgetLineItemId() {
        return budgetLineItemId;
    }

    /**
     * Sets the budgetLineItemId attribute value.
     * @param budgetLineItemId The budgetLineItemId to set.
     */
    public void setBudgetLineItemId(Long budgetLineItemId) {
        this.budgetLineItemId = budgetLineItemId;
    }

    /**
     * Sets the submitCostSharingFlag attribute value.
     * @param submitCostSharingFlag The submitCostSharingFlag to set.
     */
    public void setSubmitCostSharingFlag(Boolean submitCostSharingFlag) {
        this.submitCostSharingFlag = submitCostSharingFlag;
    }

    /**
     * Gets the submitCostSharingFlag attribute. 
     * @return Returns the submitCostSharingFlag.
     */
    public Boolean getSubmitCostSharingFlag() {
        if (ObjectUtils.isNull(budgetPeriodBO)) { 
            this.refreshReferenceObject("budgetPeriodBO");
        }
        return (getBudgetPeriodBO() != null && getBudgetPeriodBO().getBudget().getSubmitCostSharingFlag()) ? submitCostSharingFlag : false;
    }

    /**
     * Gets the budgetPeriodBO attribute. 
     * @return Returns the budgetPeriodBO.
     */
    public BudgetPeriod getBudgetPeriodBO() {
        return budgetPeriodBO;
    }

    /**
     * Sets the budgetPeriodBO attribute value.
     * @param budgetPeriodBO The budgetPeriodBO to set.
     */
    public void setBudgetPeriodBO(BudgetPeriod budgetPeriodBO) {
        this.budgetPeriodBO = budgetPeriodBO;
    }

    public Boolean getFormulatedCostElementFlag() {
        return formulatedCostElementFlag==null?Boolean.FALSE:formulatedCostElementFlag;
    }

    public void setFormulatedCostElementFlag(Boolean formulatedCostElementFlag) {
        this.formulatedCostElementFlag = formulatedCostElementFlag;
    }

    public List<BudgetFormulatedCostDetail> getBudgetFormulatedCosts() {
        return budgetFormulatedCosts;
    }

    public void setBudgetFormulatedCosts(List<BudgetFormulatedCostDetail> budgetFormulatedCosts) {
        this.budgetFormulatedCosts = budgetFormulatedCosts;
    }
}
