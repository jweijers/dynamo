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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Default implementation of the simple message service.
 */
public class DefaultMessageService implements MessageService {

    /** Logger for {@link DefaultMessageService}. */
    private static final Logger LOG = LoggerFactory.getLogger(DefaultMessageService.class);

    /** Default message when the message key is not found by this message service. TODO: move to interface */
    private static final String MESSAGE_NOT_FOUND = "[Warning: message {0} not found]";

    /** Default dynamo resources. */
    private DynamoResources resources = new DynamoResources();

    @Override
    public String getAttributeMessage(String reference, AttributeModel attributeModel, String propertyName) {
        String messageKey = reference + "." + attributeModel.getName() + "." + propertyName;
        return getMessage(messageKey);
    }

    @Override
    public String getEntityMessage(String reference, String propertyName) {
        String messageKey = reference + "." + propertyName;
        return getMessage(messageKey);
    }

    @Override
    public <E extends Enum<?>> String getEnumMessage(Class<E> enumClass, E value) {
        return value == null ? null : getMessage(enumClass.getSimpleName() + "." + value.name());
    }

    @Override
    public String getMessage(String key) {
        return getMessage(key, getLocale());
    }

    @Override
    public String getMessage(String key, Object... args) {
        return getMessage(key, getLocale(), args);
    }

    @Override
    public String getMessage(String key, Locale locale, Object... args) {
        return getMessage(key, locale, true, args);
    }

    @Override
    public String getMessageNoDefault(String key) {
        return getMessageNoDefault(key, getLocale());
    }

    @Override
    public String getMessageNoDefault(String key, Object... args) {
        return getMessageNoDefault(key, getLocale(), args);
    }

    @Override
    public String getMessageNoDefault(String key, Locale locale, Object... args) {
        return getMessage(key, locale, false, args);
    }

    private String getMessage(String key, Locale locale, boolean returnDefault, Object... args) {
        try {
            ResourceBundle bundle = resources.getBundle(locale);
            if (bundle == null) {
                LOG.warn(MessageFormat.format("ResourceBundle(s) not found for Locale '{0}'", locale.getDisplayName()));
                return getMissingResourceValue(key, returnDefault);
            }
            return MessageFormat.format(bundle.getString(key), args);
        } catch (MissingResourceException ex) {
            return getMissingResourceValue(key, returnDefault);
        }
    }

    private Locale getLocale() {
        return Locale.getDefault();
    }

    private String getMissingResourceValue(String key, boolean returnDefault) {
        if (returnDefault) {
            return MessageFormat.format(MESSAGE_NOT_FOUND, key);
        } else {
            return null;
        }
    }
}
