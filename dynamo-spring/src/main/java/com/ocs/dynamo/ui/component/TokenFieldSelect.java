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
package com.ocs.dynamo.ui.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.explicatis.ext_token_field.ExtTokenField;
import com.explicatis.ext_token_field.Tokenizable;
import com.explicatis.ext_token_field.events.TokenRemovedEvent;
import com.explicatis.ext_token_field.events.TokenRemovedListener;
import com.ocs.dynamo.domain.AbstractEntity;
import com.ocs.dynamo.domain.model.AttributeModel;
import com.ocs.dynamo.domain.model.EntityModel;
import com.ocs.dynamo.service.BaseService;
import com.ocs.dynamo.ui.Refreshable;
import com.ocs.dynamo.utils.ClassUtils;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

/**
 * A multiple select component that displays tags/tokens to indicate which values are selected
 * 
 * @author bas.rutten
 *
 * @param <ID>
 *            the type of the primary key
 * @param <T>
 *            the type of the entity
 * 
 */
public class TokenFieldSelect<ID extends Serializable, T extends AbstractEntity<ID>> extends
        QuickAddEntityField<ID, T, Collection<T>> implements Refreshable {

	private final class BeanItemTokenizable implements Tokenizable {
		private final T item;
		private final String displayValue;
		private final Long id;

		private BeanItemTokenizable(T item, String captionPropertyId) {
			this.item = item;
			this.id = getTokenIdentifier(item);
			this.displayValue = getTokenDisplayName(item, captionPropertyId);
		}

		@Override
		public long getIdentifier() {
			return id;
		}

		public T getItem() {
			return item;
		}

		@Override
		public String getStringValue() {
			return displayValue;
		}

		private String getTokenDisplayName(T entity, String captionPropertyId) {
			return ClassUtils.getFieldValueAsString(entity, captionPropertyId);
		}

		private long getTokenIdentifier(T entity) {
			return Long.parseLong(ClassUtils.getFieldValueAsString(entity, "id"));
		}
	}

	private static final long serialVersionUID = -1490179285573442827L;

	private final ExtTokenField extTokenField;

	private final EntityComboBox<ID, T> comboBox;

	private final BeanItemContainer<T> container;

	private final Collection<ValueChangeListener> valueChangeListeners;

	private boolean addAllowed = false;

	/**
	 * Constructor
	 * 
	 * @param em
	 * @param attributeModel
	 * @param service
	 * @param filter
	 * @param search
	 * @param sortOrders
	 */
	public TokenFieldSelect(EntityModel<T> em, AttributeModel attributeModel, BaseService<ID, T> service,
	        Container.Filter filter, boolean search, SortOrder... sortOrders) {
		super(service, em, attributeModel);
		extTokenField = new ExtTokenField();
		comboBox = new EntityComboBox<>(em, attributeModel, service, filter, sortOrders);
		container = new BeanItemContainer<>(AbstractEntity.class);
		valueChangeListeners = new ArrayList<>();
		this.addAllowed = !search && (attributeModel != null && attributeModel.isQuickAddAllowed());
	}

	/**
	 * Adds a token for every selected item
	 */
	private void addTokens() {
		extTokenField.clear();
		if (container.size() > 0) {
			for (T item : container.getItemIds()) {
				Tokenizable token = new BeanItemTokenizable(item, (String) comboBox.getItemCaptionPropertyId());
				extTokenField.addTokenizable(token);
			}
		}
		for (ValueChangeListener valueChangeListener : valueChangeListeners) {
			valueChangeListener.valueChange(new ValueChangeEvent(TokenFieldSelect.this));
		}
	}

	@Override
	public void addValueChangeListener(final ValueChangeListener listener) {
		valueChangeListeners.add(listener);
	}

	@Override
	protected void afterNewEntityAdded(T entity) {
		comboBox.addEntity(entity);
		container.addBean(entity);
		copyValueFromContainer();
	}

	/**
	 * Set up a listener to respond to a combo box selection change
	 */
	@SuppressWarnings("unchecked")
	private void attachComboBoxValueChange() {
		comboBox.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -1734818761735064248L;

			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				Object selectedObject = event.getProperty().getValue();
				if (selectedObject != null) {
					T abstractEntity = (T) selectedObject;
					container.addBean(abstractEntity);

					// reset the combo box
					comboBox.setValue(null);
					copyValueFromContainer();
				}
			}
		});
	}

	/**
	 * Respond to a token removal by also removing the corresponding value from the container
	 */
	@SuppressWarnings("unchecked")
	private void attachTokenFieldValueChange() {
		extTokenField.addTokenRemovedListener(new TokenRemovedListener() {
			@Override
			public void tokenRemovedEvent(TokenRemovedEvent event) {
				final BeanItemTokenizable tokenizable = (BeanItemTokenizable) event.getTokenizable();
				container.removeItem(tokenizable.getItem());
				copyValueFromContainer();
			}
		});
	}

	/**
	 * Copies the values from the container to the component
	 */
	private void copyValueFromContainer() {
		Collection<T> values = container.getItemIds();
		setValue(new HashSet<>(values));
	}

	@Override
	protected List<T> getInternalValue() {
		if (container.size() == 0) {
			return null;
		}
		return container.getItemIds();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<Collection<T>> getType() {
		return (Class<Collection<T>>) (Object) Collection.class;
	}

	@Override
	public List<T> getValue() {
		return getInternalValue();
	}

	@Override
	protected Component initContent() {
		HorizontalLayout layout = new DefaultHorizontalLayout(false, true, false);

		comboBox.setInputPrompt(getMessageService().getMessage("ocs.type.to.add"));
		comboBox.setFilteringMode(FilteringMode.CONTAINS);
		comboBox.setWidth(25, Unit.PERCENTAGE);
		comboBox.setHeightUndefined();

		extTokenField.setInputField(comboBox);
		extTokenField.setEnableDefaultDeleteTokenAction(true);

		attachComboBoxValueChange();
		attachTokenFieldValueChange();
		setupContainerFieldSync();

		layout.addComponent(extTokenField);

		if (addAllowed) {
			Button addButton = constructAddButton();
			layout.addComponent(addButton);
			layout.setExpandRatio(extTokenField, 0.90f);
			layout.setExpandRatio(addButton, 0.10f);
		}

		// initial filling of the field
		addTokens();

		layout.setSizeFull();

		return layout;
	}

	@Override
	protected void setInternalValue(Collection<T> values) {
		super.setInternalValue(values);
		container.removeAllItems();
		if (values != null) {
			container.addAll((Collection<T>) values);
		}
	}

	/**
	 * Update token selections
	 */
	private void setupContainerFieldSync() {
		container.addItemSetChangeListener(new Container.ItemSetChangeListener() {

			private static final long serialVersionUID = -2171389796068112560L;

			@Override
			public void containerItemSetChange(Container.ItemSetChangeEvent event) {
				addTokens();
			}
		});
	}

	@Override
	public void setValue(Collection<T> values) {
		super.setValue(values);
		setInternalValue(values);
	}

	public EntityComboBox<ID, T> getComboBox() {
		return comboBox;
	}

	public ExtTokenField getTokenField() {
		return extTokenField;
	}

	@Override
	public void refresh() {
		if (comboBox != null) {
			comboBox.refresh();
		}
	}

	@Override
	public void focus() {
		super.focus();
		if (comboBox != null) {
			comboBox.focus();
		}
	}
}
