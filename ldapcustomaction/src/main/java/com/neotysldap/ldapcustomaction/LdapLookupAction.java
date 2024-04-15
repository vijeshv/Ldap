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

public final class LdapLookupAction implements Action{
	private static final String BUNDLE_NAME = "com.neotysldap.ldapcustomaction.bundlelookup";
	private static final String DISPLAY_NAME = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayName");
	private static final String DISPLAY_PATH = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayPath");

	@Override
	public String getType() {
		return "LdapLookup";
	}
	

	@Override
	public List<ActionParameter> getDefaultActionParameters() {
		final List<ActionParameter> parameters = new ArrayList<ActionParameter>();
		// TODO Add default parameters.
		
		parameters.add(new ActionParameter("connectionName","myConnection"));
		
		parameters.add(new ActionParameter("ldapSearchBase","searchbase"));
		parameters.add(new ActionParameter("AccountToLookup","account to lookup"));
		return parameters;
		
	}

	@Override
	public Class<? extends ActionEngine> getEngineClass() {
		return LdapLookupActionEngine.class;
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
		description.append("LdapLookup description.\n");
		description.append("This action allows you to lookup account in LDAP server.\n Parameters:\n");
		
		description.append("ldapSearchBase (mandatory): ldapSerachBase.\n");
		description.append("AccountToLookup (mandatory): Account to lookup.\n");
	
		

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
