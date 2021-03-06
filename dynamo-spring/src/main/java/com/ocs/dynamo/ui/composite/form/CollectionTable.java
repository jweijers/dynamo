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
package com.ocs.dynamo.ui.composite.form;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.ocs.dynamo.service.MessageService;
import com.ocs.dynamo.ui.ServiceLocator;
import com.ocs.dynamo.ui.component.DefaultHorizontalLayout;
import com.ocs.dynamo.ui.component.DefaultVerticalLayout;
import com.ocs.dynamo.ui.converter.ConverterFactory;
import com.ocs.dynamo.ui.utils.VaadinUtils;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * A component for editing a property that is annotated as an @ElementCollection
 * 
 * @ElementCollection.
 * 
 * @author bas.rutten
 * @param <T>
 *            the type of the elements in the table
 */
@SuppressWarnings("serial")
public class CollectionTable<T extends Serializable> extends CustomField<Collection<T>> implements SignalsParent {

	private static final long serialVersionUID = -1203245694503350276L;

	private static final String VALUE = "value";

	/**
	 * The class of the elements of the collection being managed
	 */
	private Class<T> elementClass;

	/**
	 * Button for adding new items to the table
	 */
	private Button addButton;

	/**
	 * Form options that determine which buttons and functionalities are available
	 */
	private FormOptions formOptions;

	/**
	 * The maximum length of the fields
	 */
	private Integer maxLength;

	/**
	 * The minimum length of the fields
	 */
	private Integer minLength;

	/**
	 * The message service
	 */
	private MessageService messageService;

	/**
	 * The number of rows to display
	 */
	private int pageLength = 3;

	/**
	 * The parent form in which this component is embedded
	 */
	private ModelBasedEditForm<?, ?> parentForm;

	private boolean propagateChanges = true;

	/**
	 * the currently selected item in the table
	 */
	private Object selectedItem;

	/**
	 * The table for displaying the actual items
	 */
	private Table table;

	/**
	 * Whether the table is in view mode. If this is the case, editing is not allowed
	 */
	private boolean viewMode;

	/**
	 * Constructor
	 * 
	 * @param viewMode
	 *            whether to display the component in view (read-only) mode
	 * @param formOptions
	 *            FormOptions parameter object that can be used to govern how the component behaves
	 */
	public CollectionTable(boolean viewMode, FormOptions formOptions, Class<T> elementClass) {
		this.messageService = ServiceLocator.getMessageService();
		this.viewMode = viewMode;
		this.formOptions = formOptions;
		this.elementClass = elementClass;

		// set up a very basic table with one column
		table = new Table("");
		table.addContainerProperty(VALUE, elementClass, null);
	}

	/**
	 * Constructs the button that is used for adding new items
	 * 
	 * @param buttonBar
	 */
	protected void constructAddButton(Layout buttonBar) {
		addButton = new Button(messageService.getMessage("ocs.add"));
		addButton.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				// add a new item then set the validity to false (since an empty
				// item is never allowed)
				table.addItem();
				if (parentForm != null) {
					parentForm.signalDetailsTableValid(CollectionTable.this, false);
				}
			}
		});
		buttonBar.addComponent(addButton);
	}

	/**
	 * Constructs the button bar
	 * 
	 * @param parent
	 *            the parent layout
	 */
	protected void constructButtonBar(Layout parent) {
		Layout buttonBar = new DefaultHorizontalLayout();
		parent.addComponent(buttonBar);

		// button for adding a row
		if (!viewMode && !formOptions.isHideAddButton()) {
			constructAddButton(buttonBar);
		}

		postProcessButtonBar(buttonBar);
	}

	/**
	 * Extracts the values from the table and returns them as a set of Strings
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Set<T> extractValues() {
		Set<T> set = new HashSet<>();
		for (Object o : table.getItemIds()) {
			T t = (T) table.getItem(o).getItemProperty(VALUE).getValue();
			if (t != null) {
				set.add(t);
			}
		}
		return set;
	}

	public Button getAddButton() {
		return addButton;
	}

	public FormOptions getFormOptions() {
		return formOptions;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public Integer getMinLength() {
		return minLength;
	}

	public int getPageLength() {
		return pageLength;
	}

	public Object getSelectedItem() {
		return selectedItem;
	}

	public Table getTable() {
		return table;
	}

	/**
	 * Returns the type of the field (inherited form CustomField)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Class<? extends Collection<T>> getType() {
		return (Class<Collection<T>>) (Class<?>) Collection.class;
	}

	/**
	 * Constructs the actual component
	 */
	@Override
	protected Component initContent() {

		table.setColumnHeader(VALUE, messageService.getMessage("ocs.value"));

		table.setEditable(!isViewMode());
		table.setMultiSelect(false);
		table.setPageLength(pageLength);
		table.setColumnCollapsingAllowed(false);
		table.setSizeFull();

		table.setTableFieldFactory(new DefaultFieldFactory() {

			@Override
			public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {

				Field<?> f = super.createField(container, itemId, propertyId, uiContext);
				if (f instanceof TextField) {
					TextField tf = (TextField) f;
					tf.setNullRepresentation("");
					tf.setSizeFull();

					if (Integer.class.equals(elementClass)) {
						tf.setConverter(ConverterFactory.createIntegerConverter(false));
					}
				}

				// add a validator that checks for the maximum length
				if (maxLength != null) {
					f.addValidator(new StringLengthValidator(messageService.getMessage("ocs.value.too.long"), 0,
					        maxLength, true));
				}

				// add a validator that checks for the minimum length
				if (minLength != null) {
					f.addValidator(new StringLengthValidator(messageService.getMessage("ocs.value.too.short"),
					        minLength, Integer.MAX_VALUE, true));
				}

				// value change listener that makes sure the validity of the
				// parent form is correctly set
				f.addValueChangeListener(new ValueChangeListener() {

					@Override
					public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
						if (propagateChanges) {
							propagateChanges = false;
							Set<T> set = extractValues();
							setValue(set);
							parentForm.signalDetailsTableValid(CollectionTable.this,
							        VaadinUtils.allFixedTableFieldsValid(table));
							propagateChanges = true;
						}
					}
				});

				return f;
			}
		});

		VerticalLayout layout = new DefaultVerticalLayout(false, true);
		layout.addComponent(table);

		// add a change listener (to make sure the buttons are correctly
		// enabled/disabled)
		table.addValueChangeListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				selectedItem = table.getValue();
				onSelect(table.getValue());
			}
		});

		// add a remove button directly in the table
		if (!isViewMode() && formOptions.isShowRemoveButton()) {
			final String removeMsg = messageService.getMessage("ocs.remove");
			table.addGeneratedColumn(removeMsg, new ColumnGenerator() {

				@Override
				public Object generateCell(Table source, final Object itemId, Object columnId) {
					Button remove = new Button(removeMsg);
					remove.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {

							table.removeItem(itemId);
							setValue(extractValues());
							setSelectedItem(null);
						}
					});
					return remove;
				}
			});
		}

		// add the buttons
		constructButtonBar(layout);

		// set the reference to the parent so the status of the save button can
		// be set correctly
		ModelBasedEditForm<?, ?> parent = VaadinUtils.getParentOfClass(this, ModelBasedEditForm.class);
		setParentForm(parent);

		return layout;
	}

	public boolean isViewMode() {
		return viewMode;
	}

	/**
	 * Respond to a selection of an item in the table
	 */
	protected void onSelect(Object selected) {
		// overwrite in subclass if needed
	}

	/**
	 * Add additional buttons to the button bar
	 * 
	 * @param buttonBar
	 */
	protected void postProcessButtonBar(Layout buttonBar) {
		// overwrite in subclass if needed
	}

	public void setFormOptions(FormOptions formOptions) {
		this.formOptions = formOptions;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void setInternalValue(Collection<T> newValue) {
		if (propagateChanges && table != null) {

			// simply cleaning the container does not work since Vaadin keeps a
			// reference to a selected item
			// that cannot be removed - so instead unfortunately we have to
			// recreate the container
			table.setContainerDataSource(new IndexedContainer());
			table.addContainerProperty(VALUE, elementClass, null);

			if (newValue != null) {
				for (T t : newValue) {
					Object o = table.addItem();
					table.getItem(o).getItemProperty(VALUE).setValue(t);
				}
			}

		}
		super.setInternalValue(newValue);
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	public void setPageLength(int pageLength) {
		this.pageLength = pageLength;
	}

	/**
	 * This method is called to store a reference to the parent form
	 * 
	 * @param parentForm
	 */
	private void setParentForm(ModelBasedEditForm<?, ?> parentForm) {
		this.parentForm = parentForm;
		if (parentForm != null) {
			parentForm.signalDetailsTableValid(this, VaadinUtils.allFixedTableFieldsValid(table));
		}
	}

	public void setSelectedItem(String selectedItem) {
		this.selectedItem = selectedItem;
	}

	public void setViewMode(boolean viewMode) {
		this.viewMode = viewMode;
	}

}
