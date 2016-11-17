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

import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * JUnit test for obtaining default dynamo resources.
 */
public class DynamoResourcesTest {

    private DynamoResources subject = new DynamoResources();

    @Test
    public void getDefaultBundle() {

        // Given: Test resource files in classpath.

        // Test: Get the default resources bundles.
        ResourceBundle bundle = subject.getBundle();

        // Assert: Resource bundle found.
        Assert.assertNotNull(bundle);
    }

    @Test
    public void getConfiguredBundle() {

        // Given: Test resource files in classpath.
        // Given: A configured locale.
        Locale locale = Locale.forLanguageTag("nl");

        // Test: Get a specific resources bundle.
        ResourceBundle bundle = subject.getBundle(locale);

        // Assert: Resource bundle found.
        Assert.assertNotNull(bundle);
    }

    @Test
    public void getAnotherConfiguredBundle() {

        // Given: Test resource files in classpath.
        // Given: A configured locale.
        Locale locale = Locale.forLanguageTag("en");

        // Test: Get another resource bundles
        ResourceBundle bundle = subject.getBundle(locale);

        // Assert: Resource bundle found.
        Assert.assertNotNull(bundle);
    }
}
