package org.esupportail.smsu.services.wsgroups;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;

public class HttpRequestWsgroupsTest {

    static final String userGroup = "{\"key\":\"foo\",\"name\":\"bar\"}";
    static final String userGroups = "[{\"key\":\"foo\",\"name\":\"bar\"}]";
   
    @Test
	public void testUserGroups_from_json() throws IOException {
    	UserGroupWsgroups g = (new ObjectMapper()).readValue(userGroup, UserGroupWsgroups.class);
		assertEquals(g.id, "foo");
		assertEquals(g.name, "bar");

		List<UserGroupWsgroups> l = (new ObjectMapper()).readValue(userGroups, new TypeReference<List<UserGroupWsgroups>>() {});
		assertEquals(l.size(), 1);
		assertEquals(l.get(0).id, "foo");
		assertEquals(l.get(0).name, "bar");
	}

}

