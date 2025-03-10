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
package org.ariahgroup.research.coi;

/**
 * 
 * This class holds the action types for notifications and possible drools use.  This
 * is intended as a placeholder until a more complete solution is implemented
 */
public class CoiActionType {

    //These numbers are arbitrary, just don't duplicate
    public static final String ASSIGN_REVIEWER = "205";
    public static final String CERTIFIED_EVENT = "214";
    public static final String APPROVED_EVENT = "204";
    public static final String DISAPPROVED_EVENT = "304";
    public static final String REVIEW_COMPLETE_EVENT = "805";
    
    public static final String FE_CREATED_EVENT = "845";
    public static final String FE_MODIFIED_EVENT = "846";
}
