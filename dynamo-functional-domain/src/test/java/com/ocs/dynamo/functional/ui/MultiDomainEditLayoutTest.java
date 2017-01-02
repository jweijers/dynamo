package com.ocs.dynamo.functional.ui;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.ocs.dynamo.functional.domain.Country;
import com.ocs.dynamo.functional.domain.Domain;
import com.ocs.dynamo.functional.domain.Region;
import com.ocs.dynamo.test.BaseIntegrationTest;
import com.ocs.dynamo.ui.composite.form.FormOptions;
import com.ocs.dynamo.ui.composite.layout.BaseSplitLayout;
import com.vaadin.event.ListenerMethod.MethodException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Layout;

/**
 * Test for the MultiDomainEditLayout
 * 
 * @author bas.rutten
 *
 */
public class MultiDomainEditLayoutTest extends BaseIntegrationTest {

	@PersistenceContext
	private EntityManager em;

	private Country country;

	private Region region;

	@org.junit.Before
	public void setup() {

		region = new Region("EUR", "Europe");
		em.persist(region);

		country = new Country("NLD", "Netherlands");
		country.setRegion(region);
		em.persist(country);
		Assert.assertNotNull(country.getId());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCreate() {
		FormOptions fo = new FormOptions().setShowQuickSearchField(true).setShowRemoveButton(true);
		MultiDomainEditLayout layout = new MultiDomainEditLayout(fo, Lists.newArrayList(Country.class, Region.class));
		layout.build();

		// check that first domain class is selected by default
		Assert.assertEquals(2, layout.getDomainClasses().size());
		Assert.assertEquals(Country.class, layout.getSelectedDomain());
		Assert.assertTrue(layout.isDeleteAllowed(Country.class));

		Assert.assertNull(layout.getSelectedItem());

		BaseSplitLayout<?, ?> splitLayout = layout.getSplitLayout();
		Assert.assertNotNull(splitLayout);
		splitLayout.build();

		// adding is possible
		Assert.assertNotNull(splitLayout.getAddButton());
		Assert.assertTrue(splitLayout.getAddButton().isVisible());

		Assert.assertTrue(splitLayout.getRemoveButton().isVisible());
		Assert.assertFalse(splitLayout.getRemoveButton().isEnabled());

		// test reload
		splitLayout.reload();

	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCreateInReadOnly() {
		FormOptions fo = new FormOptions();
		MultiDomainEditLayout layout = new MultiDomainEditLayout(fo, Lists.newArrayList(Country.class, Region.class)) {

			private static final long serialVersionUID = -2364409278522556475L;

			@Override
			protected boolean isEditAllowed() {
				return false;
			}

			@Override
			protected void postProcessButtonBar(Layout buttonBar) {
				Button button = new Button("testButton");
				buttonBar.addComponent(button);
				registerButton(button);
			}
		};
		layout.build();

		BaseSplitLayout<?, ?> splitLayout = layout.getSplitLayout();
		splitLayout.build();

		// adding is not possible
		Assert.assertNotNull(splitLayout.getAddButton());
		Assert.assertFalse(splitLayout.getAddButton().isVisible());

		// test the reload method
		layout.reload();
	}

	/**
	 * Test what happens if there is no service class defined
	 */
	@Test(expected = MethodException.class)
	public void testCreateServiceMissing() {
		List<Class<? extends Domain>> list = new ArrayList<>();
		list.add(TestDomain.class);

		FormOptions fo = new FormOptions();
		MultiDomainEditLayout layout = new MultiDomainEditLayout(fo, list);
		layout.build();

	}

	private class TestDomain extends Domain {

		private static final long serialVersionUID = -204959303189799878L;

	}
}
