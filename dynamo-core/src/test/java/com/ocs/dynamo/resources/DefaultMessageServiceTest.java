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

import com.ocs.dynamo.domain.model.AttributeModel;
import com.ocs.dynamo.service.MessageService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Locale;

import static org.mockito.Mockito.when;

/**
 * JUnit test for default message service implementation.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultMessageServiceTest {

    private MessageService subject = new DefaultMessageService();

    @Mock
    private AttributeModel model;

    @Test
    public void getAttributeMessageFromAttributeModel() {

        // Given: Attribute definition.
        final String reference = this.getClass().getSimpleName();
        when(model.getName()).thenReturn("attributeMessageFromAttributeModel");
        final String propertyName = "property";

        // Test: Get the attribute message.
        String msg = subject.getAttributeMessage(reference, model, propertyName);

        // Assert: Message for attribute.
        Assert.assertEquals("Property", msg);
    }

    @Test
    public void getMessage() {

        // Given: Some message key.
        final String key = "ocs.menu.1.displayName";

        // Test: Get message for key.
        String msg = subject.getMessage(key);

        // Assert: Message for key.
        Assert.assertEquals("Menu 1", msg);
    }

    @Test
    public void getLocalizedMessage() {

        // Given: Some message key.
        final String key = "jimi.says";

        // Test: Get the localized message.
        String msg = subject.getMessage(key, Locale.forLanguageTag("nl"));

        // Assert: Localized message.
        Assert.assertEquals("De heer weet dat ik een Vodou kind ben.", msg);
    }

    @Test
    public void getParameterizedMessage() {

        // Given: This message needs some details.
        final String key = "substitution";

        // Test: Get the localized message.
        String msg = subject.getMessage(key, "needle", "record");

        // Assert: Localized message.
        Assert.assertEquals("Put the needle on the record.", msg);
    }

    @Test
    public void getEntityMessage() {

        // Given: Some property.
        final String key = "name";

        // Test: Get the localized message.
        String msg = subject.getEntityMessage(getClass().getSimpleName(), key);

        // Assert: Localized message.
        Assert.assertEquals("Message service test", msg);
    }

    @Test
    public void getEnumMessage() {

        // Given: Some enum.

        // Test: Get the Enum message.
        String msg = subject.getEnumMessage(DynamoResource.class, DynamoResource.VALIDATION);

        // Assert: Message for enum value.
        Assert.assertEquals("validation", msg);
    }
}
