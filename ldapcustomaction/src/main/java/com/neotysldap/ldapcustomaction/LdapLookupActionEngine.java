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

public final class LdapLookupActionEngine implements ActionEngine {

	@Override
	public SampleResult execute(Context context, List<ActionParameter> parameters) {
		final SampleResult sampleResult = new SampleResult();
		final StringBuilder requestBuilder = new StringBuilder();
		final StringBuilder responseBuilder = new StringBuilder();
		String ldapSearchBase = null,ldapAccountToLookup = null,connectionname=null;
		LdapContext ctx = null;
		
		
		sampleResult.sampleStart();

		appendLineToStringBuilder(requestBuilder, "LdapAccountLookup request.");
		appendLineToStringBuilder(responseBuilder, "LdapAccountLookup response.");
		// TODO perform execution.

		
		for (ActionParameter temp:parameters) {
			switch (temp.getName().toLowerCase()) {
			case "ldapsearchbase" :
				ldapSearchBase = temp.getValue();
				break;
			case "accounttolookup" :
				ldapAccountToLookup = temp.getValue();
				break;
			case "connectionname":
				connectionname = temp.getValue();
				break;
			
			
			default:
				break;
			}
		}
		
		context.getLogger().debug("host="+ldapSearchBase+"user="+ldapAccountToLookup);
		
		
		
        try {
        	 ctx= (LdapContext) context.getCurrentVirtualUser().get( connectionname);
        	//LDAPTest ldap = new LDAPTest();
            
            //1) lookup the ldap account
            SearchResult srLdapUser = findAccountByAccountName(ctx, ldapSearchBase, ldapAccountToLookup);
            
       	 appendLineToStringBuilder(responseBuilder, "Account lookup details " + srLdapUser.getAttributes().toString());
            //2) get the SID of the users primary group
            String primaryGroupSID = getPrimaryGroupSID(srLdapUser);
            
            //3) get the users Primary Group
            String primaryGroupName = findGroupBySID(ctx, ldapSearchBase, primaryGroupSID);
            
		
			 appendLineToStringBuilder(responseBuilder, "Account lookup details " + primaryGroupName.toString());
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			appendLineToStringBuilder(responseBuilder, e.getMessage());
			return getErrorResult(context, sampleResult, e.getMessage(), e);
		}
        
      
        
		sampleResult.sampleEnd();

		sampleResult.setRequestContent(requestBuilder.toString());
		sampleResult.setResponseContent(responseBuilder.toString());
		return sampleResult;
	}
	
	 public SearchResult findAccountByAccountName(DirContext ctx, String ldapSearchBase, String accountName) throws NamingException {

	        String searchFilter = "(&(objectClass=user)(sAMAccountName=" + accountName + "))";

	        SearchControls searchControls = new SearchControls();
	        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

	        NamingEnumeration<SearchResult> results = ctx.search(ldapSearchBase, searchFilter, searchControls);

	        SearchResult searchResult = null;
	        if(results.hasMoreElements()) {
	             searchResult = (SearchResult) results.nextElement();
	             System.out.println(searchResult.getAttributes().toString());

	            //make sure there is not another item available, there should be only 1 match
	            if(results.hasMoreElements()) {
	                System.err.println("Matched multiple users for the accountName: " + accountName);
	                return null;
	            }
	        }
	        
	        return searchResult;
	    }
	    
	    public String findGroupBySID(DirContext ctx, String ldapSearchBase, String sid) throws NamingException {
	        
	        String searchFilter = "(&(objectClass=group)(objectSid=" + sid + "))";

	        SearchControls searchControls = new SearchControls();
	        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	        
	        NamingEnumeration<SearchResult> results = ctx.search(ldapSearchBase, searchFilter, searchControls);

	        if(results.hasMoreElements()) {
	            SearchResult searchResult = (SearchResult) results.nextElement();

	            //make sure there is not another item available, there should be only 1 match
	            if(results.hasMoreElements()) {
	                System.err.println("Matched multiple groups for the group with SID: " + sid);
	                return null;
	            } else {
	                return (String)searchResult.getAttributes().get("sAMAccountName").get();
	            }
	        }
	        return null;
	    }
	    
	    public String getPrimaryGroupSID(SearchResult srLdapUser) throws NamingException {
	        byte[] objectSID = (byte[])srLdapUser.getAttributes().get("objectSid").get();
	        String strPrimaryGroupID = (String)srLdapUser.getAttributes().get("primaryGroupID").get();
	        
	        String strObjectSid = decodeSID(objectSID);
	        
	        return strObjectSid.substring(0, strObjectSid.lastIndexOf('-') + 1) + strPrimaryGroupID;
	    }
	    
	    /**
	     * The binary data is in the form:
	     * byte[0] - revision level
	     * byte[1] - count of sub-authorities
	     * byte[2-7] - 48 bit authority (big-endian)
	     * and then count x 32 bit sub authorities (little-endian)
	     * 
	     * The String value is: S-Revision-Authority-SubAuthority[n]...
	     * 
	     * Based on code from here - http://forums.oracle.com/forums/thread.jspa?threadID=1155740&tstart=0
	     */
	    public static String decodeSID(byte[] sid) {
	        
	        final StringBuilder strSid = new StringBuilder("S-");

	        // get version
	        final int revision = sid[0];
	        strSid.append(Integer.toString(revision));
	        
	        //next byte is the count of sub-authorities
	        final int countSubAuths = sid[1] & 0xFF;
	        
	        //get the authority
	        long authority = 0;
	        //String rid = "";
	        for(int i = 2; i <= 7; i++) {
	           authority |= ((long)sid[i]) << (8 * (5 - (i - 2)));
	        }
	        strSid.append("-");
	        strSid.append(Long.toHexString(authority));
	        
	        //iterate all the sub-auths
	        int offset = 8;
	        int size = 4; //4 bytes for each sub auth
	        for(int j = 0; j < countSubAuths; j++) {
	            long subAuthority = 0;
	            for(int k = 0; k < size; k++) {
	                subAuthority |= (long)(sid[offset + k] & 0xFF) << (8 * k);
	            }
	            
	            strSid.append("-");
	            strSid.append(subAuthority);
	            
	            offset += size;
	        }
	        
	        return strSid.toString();    
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
