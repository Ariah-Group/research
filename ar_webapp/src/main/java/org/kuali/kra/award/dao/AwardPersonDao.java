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
package org.kuali.kra.award.dao;

import org.kuali.kra.award.contacts.AwardPerson;

import java.util.List;
import java.util.Map;

/**
 * This interface defines contract for database interactions with AwardPersons
 */
public interface AwardPersonDao {

    List<AwardPerson> getAwardPersons(Map<String, String> fieldValues);
}
