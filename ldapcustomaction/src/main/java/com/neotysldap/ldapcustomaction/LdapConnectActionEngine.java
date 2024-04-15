package com.neotysldap.ldapcustomaction;

import java.util.Hashtable;
import java.util.List;

import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
//import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;



import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;

public final class LdapConnectActionEngine implements ActionEngine {

	@Override
	public SampleResult execute(Context context, List<ActionParameter> parameters) {
		final SampleResult sampleResult = new SampleResult();
		final StringBuilder requestBuilder = new StringBuilder();
		final StringBuilder responseBuilder = new StringBuilder();
		String ldapserver = null,ldapusername = null,ldappassword = null,connectionname=null;
		LdapContext ctx = null;
		
		
		sampleResult.sampleStart();

		appendLineToStringBuilder(requestBuilder, "LdapConnect request.");
		appendLineToStringBuilder(responseBuilder, "LdapConnect response.");
		// TODO perform execution.

		
		for (ActionParameter temp:parameters) {
			switch (temp.getName().toLowerCase()) {
			case "connectionname" :
			 connectionname = temp.getValue();
				break;
			case "ldapserver" :
				ldapserver = temp.getValue();
				break;
			case "ldapusername":
				ldapusername = temp.getValue();
				break;
			case "ldappassword":
				ldappassword = temp.getValue();
				break;
			
			default:
				break;
			}
		}
		
		context.getLogger().debug("host="+ldapserver+"user="+ldapusername+ "pass="+ldappassword);
		
		
		Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put(javax.naming.Context.SECURITY_AUTHENTICATION, "simple");
        if(ldapusername != null) {
            env.put(javax.naming.Context.SECURITY_PRINCIPAL, ldapusername);
        }
        if(ldappassword != null) {
            env.put(javax.naming.Context.SECURITY_CREDENTIALS, ldappassword);
        }
        env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(javax.naming.Context.PROVIDER_URL, ldapserver);
        env.put("java.naming.ldap.attributes.binary", "objectSID");
        try {
			ctx = new InitialLdapContext(env, null);
			 appendLineToStringBuilder(responseBuilder, "Connected to Ldap server " + ldapserver);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			appendLineToStringBuilder(responseBuilder, e.getMessage());
			return getErrorResult(context, sampleResult, e.getMessage(), e);
		}
        
        context.getCurrentVirtualUser().put( connectionname, ctx);
        
		sampleResult.sampleEnd();

		sampleResult.setRequestContent(requestBuilder.toString());
		sampleResult.setResponseContent(responseBuilder.toString());
		return sampleResult;
	}

	private void appendLineToStringBuilder(final StringBuilder sb, final String line){
		sb.append(line).append("\n");
	}

	/**
	 * This method allows to easily create an error result and log exception.
	 */
	private static SampleResult getErrorResult(final Context context, final SampleResult result, final String errorMessage, final Exception exception) {
		result.setError(true);
		result.setStatusCode("NL-LdapConnect_ERROR");
		result.setResponseContent(errorMessage);
		if(exception != null){
			context.getLogger().error(errorMessage, exception);
		} else{
			context.getLogger().error(errorMessage);
		}
		return result;
	}

	@Override
	public void stopExecute() {
		// TODO add code executed when the test have to stop.
	}

}
