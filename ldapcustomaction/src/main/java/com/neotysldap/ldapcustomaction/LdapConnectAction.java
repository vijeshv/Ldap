package com.neotysldap.ldapcustomaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Icon;

import com.google.common.base.Optional;
import com.neotys.extensions.action.Action;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;

public final class LdapConnectAction implements Action{
	private static final String BUNDLE_NAME = "com.neotysldap.ldapcustomaction.bundle";
	private static final String DISPLAY_NAME = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayName");
	private static final String DISPLAY_PATH = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayPath");

	@Override
	public String getType() {
		return "LdapConnect";
	}
	

	@Override
	public List<ActionParameter> getDefaultActionParameters() {
		final List<ActionParameter> parameters = new ArrayList<ActionParameter>();
		// TODO Add default parameters.
		
		parameters.add(new ActionParameter("connectionName","myConnection"));
		parameters.add(new ActionParameter("ldapServer","ldap://ldap.test.neotys.com:389"));
		parameters.add(new ActionParameter("ldapUsername","test"));
		parameters.add(new ActionParameter("ldapPassword","1234"));
		return parameters;
		
	}

	@Override
	public Class<? extends ActionEngine> getEngineClass() {
		return LdapConnectActionEngine.class;
	}

	@Override
	public Icon getIcon() {
		// TODO Add an icon
		return null;
	}

	@Override
	public boolean getDefaultIsHit(){
		return false;
	}

	@Override
	public String getDescription() {
		final StringBuilder description = new StringBuilder();
		// TODO Add description
		description.append("LdapConnect description.\n");
		description.append("This action allows you to open a connection to a LDAP server.\n Parameters:\n");
		description.append("ldapAdServer (mandatory): LdapAdserver to connect to. \n");
		description.append("ldapUsername (mandatory): ldapUsername you want to connect to.\n");
		description.append("ldapPassword (mandatory): ldapPassword to be used for connection.\n");
		description.append("Password (mandatory): Password to be used for connection.\n");
		

		return description.toString();
	}

	@Override
	public String getDisplayName() {
		return DISPLAY_NAME;
	}

	@Override
	public String getDisplayPath() {
		return DISPLAY_PATH;
	}

	@Override
	public Optional<String> getMinimumNeoLoadVersion() {
		return Optional.absent();
	}

	@Override
	public Optional<String> getMaximumNeoLoadVersion() {
		return Optional.absent();
	}
}
