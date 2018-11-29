/*******************************************************************************
 * Copyright 2012 The Odysseus Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.uniol.inf.is.odysseus.test;

/**
 * @author Merlin Wasmann, Dennis Geesen, Christian Kuka
 *
 */
public enum StatusCode {

    OK("OK"),

    FAILED("Failed"),

    ERROR_QUERY_NOT_INSTALLABLE("Query not installable"),

    ERROR_EXCEPTION_DURING_TEST("Exception during test"),

    ERROR_DUPLICATES("Duplicates"),

    ERROR_OUT_OF_ORDER("Out of order"),

    ERROR_MISSING_DATA("Missing data"),

    ERROR_NOT_EQUIVALENT("Not equivalent"),

    ERROR_WRONG_PARAMETERS("Wrong parameters"),

    ERROR_DEADLOCK_POSSIBLE("Deadlock possible");

    private String message;

    /**
     * Class constructor.
     *
     */
    private StatusCode(String message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return message;
    }

}
