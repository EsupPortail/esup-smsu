package org.esupportail.smsu.web;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.esupportail.smsu.utils.CachedDigest;

public class ServerSideDirectives {

	private String scriptStart = "(<script [^>]*)";
	private String scriptEnd = "([^>]*> *</script>)";
	private String linkStart = "(<link [^>]*)";
	private String linkEnd = "([^>]*>)";
	private String stringValue = "\"([^\"]*)\"";
	private String srcStringValue = "([^>]*src)=" + stringValue;
	private String hrefStringValue = "([^>]*href)=" + stringValue;

	@Inject private CachedDigest cachedDigest;
	
    public String instantiate(String template, Map<String,String> env, ServletContextWrapper context) {
    	template = instantiate_vars(template, env);
    	// first remove what is unneeded
    	template = instantiate_serverSideIf(template, env);
    	template = instantiate_serverSideCacheBuster(template, env, context);
    	// must be done after CacheBuster
    	template = instantiate_serverSidePrefix(template, env);
    	return template;
    }
    
    public String instantiate_vars(String template, final Map<String,String> env) {
    	String regex = Pattern.quote("{{serverSide.") + "([^}]+)" + Pattern.quote("}}");
    	return ReplaceAllWithCallback.doIt(template, regex, new ReplaceAllWithCallback.Callback() {			
			public String replace(MatchResult m) {
				return env.get(m.group(1));
			}
		});
    }
    
    /* 
     * NB: this cacheBuster adds ?d=<digest> to urls, which is not best according to https://developers.google.com/speed/docs/best-practices/caching
     * but it should be quite good for our needs (and better than nothing!!)
     */
    private String instantiate_serverSideCacheBuster(String template, Map<String,String> env, ServletContextWrapper context) {
		String ssp_src_regex = "\\bserver-side-cache-buster\\s+" + srcStringValue;
    	String ssp_href_regex = "\\bserver-side-cache-buster\\s+" + hrefStringValue;
		template = instantiate_serverSideCacheBuster(template, scriptStart + ssp_src_regex + scriptEnd, env, context);
		template = instantiate_serverSideCacheBuster(template, linkStart + ssp_href_regex + linkEnd, env, context);
    	if (template.matches("server-side-cache-buster")) throw new RuntimeException("syntax error for server-side-cache-buster in " + template);
		return template;
    }

    private String instantiate_serverSidePrefix(String template, Map<String,String> env) {
		String ssp_src_regex = "\\bserver-side-prefix=" + stringValue + "\\s+" + srcStringValue;
    	String ssp_href_regex = "\\bserver-side-prefix=" + stringValue + "\\s+" + hrefStringValue;
		template = instantiate_serverSidePrefix(template, scriptStart + ssp_src_regex + scriptEnd, env);
		template = instantiate_serverSidePrefix(template, linkStart + ssp_href_regex + linkEnd, env);
    	if (template.matches("server-side-prefix")) throw new RuntimeException("syntax error for server-side-prefix in " + template);
		return template;
    }

    private String instantiate_serverSideIf(String template, Map<String,String> env) {
		String ssp_regex = "\\bserver-side-if=" + stringValue + "\\s*";
    	template = instantiate_serverSideIf(template, scriptStart + ssp_regex + scriptEnd, env);
		template = instantiate_serverSideIf(template, linkStart + ssp_regex + linkEnd, env);
    	if (template.matches("server-side-if")) throw new RuntimeException("syntax error for server-side-if in " + template);
		return template;
    }
    
    private String instantiate_serverSideCacheBuster(String template, String regex, final Map<String,String> env, final ServletContextWrapper context) {
    	return ReplaceAllWithCallback.doIt(template, regex, new ReplaceAllWithCallback.Callback() {			
			public String replace(MatchResult m) {
				String	openTag = m.group(1),
						srcAttr = m.group(2),
						srcVal = m.group(3),
						endTag = m.group(4);
				
				File src = src2file(context, srcVal);
				String digest = cachedDigest.compute(src);
				digest = digest.substring(0, 8); // not too long, otherwise it's really ugly and non necessary
				
				return openTag + srcAttr + "=\"" + srcVal + "?d=" + digest + "\"" + endTag;
			}
    	});
    }
    
    private String instantiate_serverSidePrefix(String template, String regex, final Map<String,String> env) {
    	return ReplaceAllWithCallback.doIt(template, regex, new ReplaceAllWithCallback.Callback() {			
			public String replace(MatchResult m) {
				String 	openTag = m.group(1),
						prefixVar = m.group(2),
						srcAttr = m.group(3),
						srcVal = m.group(4),
						endTag = m.group(5);

				String prefix = env.get(prefixVar);
				if (prefix == null) throw new RuntimeException("invalid server-side-prefix " + prefixVar);
    		
				return openTag + srcAttr + "=\"" + prefix + "/" + srcVal + "\"" + endTag;
			}
    	});
    }
    
    private String instantiate_serverSideIf(String template, String regex, final Map<String,String> env) {
    	String result = ReplaceAllWithCallback.doIt(template, regex, new ReplaceAllWithCallback.Callback() {			
			public String replace(MatchResult m) {
				String	openTag = m.group(1),
						varName = m.group(2),
						endTag = m.group(3);

				String val = env.get(varName);
				if (val == null) throw new RuntimeException("invalid server-side-if " + varName);
    		
				return StringUtils.isBlank(val) ? "" : openTag + endTag;
			}
    	});
    	return result;
    }
	
	private File src2file(final ServletContextWrapper context, String src) {
		File baseURL = new File(context.getRealPath("/"));
		return new File(baseURL, src);
	}

    static public class ReplaceAllWithCallback
    {
        static public interface Callback
        {
            public String replace(MatchResult matchResult);
        }

        static public String doIt(String subject, String regex, Callback callback)
        {
    		return doIt(subject, Pattern.compile(regex), callback);
        }
        
        static public String doIt(String subject, Pattern pattern, Callback callback)
        {
        	Matcher m = pattern.matcher(subject);  
        	StringBuffer result = new StringBuffer();  
        	while (m.find()) {
        		m.appendReplacement(result, ""); // add before match in result   
        	    result.append(callback.replace(m));
        	}  
        	m.appendTail(result); // add after match in result
        	return result.toString();
        }
    }

}
