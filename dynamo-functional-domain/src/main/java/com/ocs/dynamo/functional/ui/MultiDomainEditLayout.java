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
package com.ocs.dynamo.functional.ui;

import java.util.List;

import com.ocs.dynamo.constants.DynamoConstants;
import com.ocs.dynamo.exception.OCSRuntimeException;
import com.ocs.dynamo.functional.domain.Domain;
import com.ocs.dynamo.service.BaseService;
import com.ocs.dynamo.ui.ServiceLocator;
import com.ocs.dynamo.ui.component.DefaultVerticalLayout;
import com.ocs.dynamo.ui.composite.form.FormOptions;
import com.ocs.dynamo.ui.composite.layout.BaseCustomComponent;
import com.ocs.dynamo.ui.composite.layout.ServiceBasedSplitLayout;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

/**
 * A layout that allows the user to easily manage multiple domains. The list of domain classes can
 * be passed to the constructor. Please note that for every domain class, you must define a
 * (default) service
 * 
 * @author bas.rutten
 *
 */
public class MultiDomainEditLayout extends BaseCustomComponent {

	private static final long serialVersionUID = 4410282343830892631L;

	/**
	 * The classes of the domains that are managed by this screen
	 */
	private final List<Class<? extends Domain>> domainClasses;

	/**
	 * The combo box for selecting the domain
	 */
	private ComboBox domainCombo;

	/**
	 * The form options (these are passed directly to the split layout)
	 */
	private FormOptions formOptions;

	/**
	 * The main layout
	 */
	private VerticalLayout mainLayout;

	/**
	 * The selected domain class
	 */
	private Class<? extends Domain> selectedDomain;

	/**
	 * The layout that contains the controls for editing the selected domain
	 */
	private VerticalLayout selectedDomainLayout;

	/**
	 * The split layout that displays the currently selected domain
	 */
	private ServiceBasedSplitLayout<?, ?> splitLayout;

	/**
	 * Constructor
	 * 
	 * @param formOptions
	 *            the form options
	 * @param domainClasses
	 *            the classes of the domains
	 */
	public MultiDomainEditLayout(FormOptions formOptions, List<Class<? extends Domain>> domainClasses) {
		this.formOptions = formOptions;
		this.domainClasses = domainClasses;
	}

	@Override
	public void attach() {
		super.attach();
		build();
	}

	@Override
	public void build() {
		if (mainLayout == null) {

			mainLayout = new DefaultVerticalLayout(true, true);

			// form that contains the combo box
			FormLayout form = new FormLayout();
			form.setMargin(true);
			form.setStyleName(DynamoConstants.CSS_CLASS_HALFSCREEN);
			mainLayout.addComponent(form);

			// combo box for selecting domain
			domainCombo = new ComboBox(message("ocs.select.domain"));
			for (Class<? extends Domain> clazz : getDomainClasses()) {
				domainCombo.addItem(clazz);
				domainCombo.setItemCaption(clazz, getEntityModelFactory().getModel(clazz).getDisplayName());
			}
			domainCombo.setNullSelectionAllowed(false);
			domainCombo.setSizeFull();

			// respond to a change by displaying the correct domain
			domainCombo.addValueChangeListener(new ValueChangeListener() {

				private static final long serialVersionUID = 8441066091930807231L;

				@Override
				@SuppressWarnings("unchecked")
				public void valueChange(ValueChangeEvent event) {
					selectDomain((Class<? extends Domain>) event.getProperty().getValue());
				}
			});

			form.addComponent(domainCombo);

			selectedDomainLayout = new DefaultVerticalLayout();
			mainLayout.addComponent(selectedDomainLayout);

			// select the first domain (if there is any)
			if (!getDomainClasses().isEmpty()) {
				domainCombo.setValue(getDomainClasses().get(0));
			}
			setCompositionRoot(mainLayout);
		}
	}

	/**
	 * Construct a split layout for a certain domain
	 * 
	 * @param domainClass
	 *            the class of the domain
	 * @param formOptions
	 *            the form options
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T extends Domain> ServiceBasedSplitLayout<Integer, T> constructSplitLayout(Class<T> domainClass,
	        FormOptions formOptions) {

		BaseService<Integer, T> baseService = (BaseService<Integer, T>) ServiceLocator.getServiceForEntity(domainClass);
		if (baseService != null) {

			return new ServiceBasedSplitLayout<Integer, T>(baseService, getEntityModelFactory().getModel(domainClass),
			        formOptions, new SortOrder(Domain.NAME, SortDirection.ASCENDING)) {

				private static final long serialVersionUID = -6504072714662771230L;

				@Override
				protected Filter constructQuickSearchFilter(String value) {
					return new SimpleStringFilter(Domain.NAME, value, true, false);
				}

				@Override
				protected boolean isEditAllowed() {
					return MultiDomainEditLayout.this.isEditAllowed();
				}

				@Override
				protected boolean mustEnableButton(Button button, T selectedItem) {
					if (getRemoveButton() == button) {
						return isDeleteAllowed(getSelectedDomain());
					}
					return true;
				}

				@Override
				protected void postProcessButtonBar(Layout buttonBar) {
					MultiDomainEditLayout.this.postProcessButtonBar(buttonBar);
				}

			};
		} else {
			throw new OCSRuntimeException(message("ocs.no.service.class.found", domainClass));
		}
	}

	public List<Class<? extends Domain>> getDomainClasses() {
		return domainClasses;
	}

	/**
	 * 
	 * @return the currently selected domain class
	 */
	public Class<? extends Domain> getSelectedDomain() {
		return selectedDomain;
	}

	public Domain getSelectedItem() {
		return (Domain) splitLayout.getSelectedItem();
	}

	/**
	 * 
	 * @return the currently selected split layout
	 */
	public ServiceBasedSplitLayout<?, ?> getSplitLayout() {
		return splitLayout;
	}

	/**
	 * Check if the deletion of domain values for a certain class is allowed
	 * 
	 * @param clazz
	 *            the class
	 * @return
	 */
	protected boolean isDeleteAllowed(Class<?> clazz) {
		return true;
	}

	/**
	 * Indicates whether editing is allowed
	 */
	protected boolean isEditAllowed() {
		return true;
	}

	protected void postProcessButtonBar(Layout buttonBar) {
		// overwrite in subclasses
	}

	/**
	 * Registers a button. The button will be disabled or enabled depending on whether an item is
	 * selected
	 * 
	 * @param button
	 *            the button to register
	 */
	public void registerButton(Button button) {
		if (splitLayout != null) {
			splitLayout.registerButton(button);
		}
	}

	/**
	 * Reloads the screen
	 */
	public void reload() {
		if (splitLayout != null) {
			splitLayout.reload();
		}
	}

	/**
	 * Constructs a layout for editing a certain domain
	 * 
	 * @param clazz
	 *            the domain class
	 */
	private void selectDomain(Class<? extends Domain> clazz) {
		selectedDomain = clazz;
		ServiceBasedSplitLayout<?, ?> layout = constructSplitLayout(clazz, formOptions);
		selectedDomainLayout.replaceComponent(splitLayout, layout);
		splitLayout = layout;
	}
}
