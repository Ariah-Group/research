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
package org.kuali.kra.proposaldevelopment.bo;

import org.kuali.kra.bo.KraSortablePersistableBusinessObjectBase;
import org.kuali.kra.bo.Ynq;

import java.sql.Date;

public class ProposalYnq extends KraSortablePersistableBusinessObjectBase implements Comparable<ProposalYnq> {

    private String proposalNumber;

    private String questionId;

    private String answer;

    private String explanation;

    private Date reviewDate;

    private Ynq ynq;

    private String dummyAnswer;

    private boolean explanationRequired = true;

    private boolean reviewDateRequired = true;

    private String explanationRequiredDescription;

    private String reviewDateRequiredDescription;

    public Ynq getYnq() {
        return ynq;
    }

    public void setYnq(Ynq ynq) {
        this.ynq = ynq;
    }

    public ProposalYnq() {
        super();
    }

    public String getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getDummyAnswer() {
        return dummyAnswer;
    }

    public void setDummyAnswer(String dummyAnswer) {
        this.dummyAnswer = dummyAnswer;
    }

    public boolean getExplanationRequired() {
        return explanationRequired;
    }

    public void setExplanationRequired(boolean explanationRequired) {
        this.explanationRequired = explanationRequired;
    }

    public boolean getReviewDateRequired() {
        return reviewDateRequired;
    }

    public void setReviewDateRequired(boolean reviewDateRequired) {
        this.reviewDateRequired = reviewDateRequired;
    }

    public String getExplanationRequiredDescription() {
        return explanationRequiredDescription;
    }

    public void setExplanationRequiredDescription(String explanationRequiredDescription) {
        this.explanationRequiredDescription = explanationRequiredDescription;
    }

    public String getReviewDateRequiredDescription() {
        return reviewDateRequiredDescription;
    }

    public void setReviewDateRequiredDescription(String reviewDateRequiredDescription) {
        this.reviewDateRequiredDescription = reviewDateRequiredDescription;
    }

    public int compareTo(ProposalYnq proposalYnq) {
        int comparator;
        if (getSortId() != null && proposalYnq.getSortId() != null) {
            comparator = getSortId().compareTo(proposalYnq.getSortId());
        } else {
            comparator = getQuestionId().compareTo(proposalYnq.getQuestionId());
        }
        return comparator;
    }
}
