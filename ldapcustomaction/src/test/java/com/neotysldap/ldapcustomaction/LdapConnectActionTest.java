package com.neotysldap.ldapcustomaction;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LdapConnectActionTest {
	@Test
	public void shouldReturnType() {
		final LdapConnectAction action = new LdapConnectAction();
		assertEquals("LdapConnect", action.getType());
	}

}
