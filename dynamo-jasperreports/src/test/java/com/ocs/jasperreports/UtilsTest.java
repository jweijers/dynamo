package com.ocs.jasperreports;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

public class UtilsTest {

    @Test
    public void testCreateUrl() throws URISyntaxException {
	URI uri = Utils.createUrl("http://somehost/path?k0=ã ã", "k1", "ão 355(KM16,5)");
	assertNotNull(uri);
	assertTrue(uri.toString().indexOf("%20") == 25);

	uri = Utils.createUrl("http://somehost/path", "k1", "ão 355(KM16,5)");
	assertNotNull(uri);
	assertTrue(uri.toString().indexOf("%20") == 26);

	uri = Utils.createUrl("//somehost/path", "k1", "ão 355(KM16,5)");
	assertNotNull(uri);
	assertTrue(uri.toString().indexOf("%20") == 26);

	uri = Utils.createUrl("//somehost/path", "k1", "ão 355(KM16,5)", "k2", " & % $ ? ");
	assertNotNull(uri);
	assertTrue(uri.toString().indexOf("%20") == 26);

    }

}
