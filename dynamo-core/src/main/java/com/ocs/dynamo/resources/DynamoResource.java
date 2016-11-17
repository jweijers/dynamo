/*
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.ocs.dynamo.resources;

/**
 * Enumeration for pre-defined message bundles.
 */
public enum DynamoResource {

    /**
     * Framework message translation.
     */
    FRAMEWORK("framework"),

    /**
     * User-defined Entity Model messages.
     */
    MODEL("entitymodel"),

    /**
     * User-defined validation messages.
     */
    VALIDATION("validation"),

    /**
     * User-defined UI messages.
     */
    UI("ui"),

    /**
     * User-defined application messages.
     */
    MESSAGES("messages");

    /** Resource reference id. */
    private String id;

    /**
     *  Constructor.
     */
    private DynamoResource(String id) {
        this.id = id;
    }

    /**
     * @return reference id.
     */
    public String getId() {
        return id;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s[id=%s]", name(), id);
    }
}
