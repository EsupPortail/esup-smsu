package org.esupportail.smsu.web;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.esupportail.smsu.utils.AggregateToFile;
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
	@Inject private AggregateToFile aggregateToFile;
	
    public String instantiate(String template, Map<String,Object> env, ServletContextWrapper context) {
    	template = instantiate_vars(template, env);
    	// first remove what is unneeded
    	template = instantiate_serverSideIf(template, env);
    	// generate angular templates js
    	template = instantiate_serverSideAngularTemplates(template, env, context);    	
    	template = instantiate_serverSideConcat(template, context);
    	// must be done after Concat in case both are used
    	template = instantiate_serverSideCacheBuster(template, context);
    	// must be done after CacheBuster
    	template = instantiate_serverSidePrefix(template, env);
    	return template;
    }
    
    public String instantiate_vars(String template, final Map<String,Object> env) {
    	String regex = Pattern.quote("{{serverSide.") + "([^}]+)" + Pattern.quote("}}");
    	return ReplaceAllWithCallback.doIt(template, regex, new ReplaceAllWithCallback.Callback() {			
			public String replace(MatchResult m) {
				return (String) env.get(m.group(1));
			}
		});
    }
    
    /* 
     * NB: this cacheBuster adds ?d=<digest> to urls, which is not best according to https://developers.google.com/speed/docs/best-practices/caching
     * but it should be quite good for our needs (and better than nothing!!)
     */
    private String instantiate_serverSideCacheBuster(String template, ServletContextWrapper context) {
		String ssp_src_regex = "\\bserver-side-cache-buster\\s+" + srcStringValue;
    	String ssp_href_regex = "\\bserver-side-cache-buster\\s+" + hrefStringValue;
		template = instantiate_serverSideCacheBuster(template, scriptStart + ssp_src_regex + scriptEnd, context);
		template = instantiate_serverSideCacheBuster(template, linkStart + ssp_href_regex + linkEnd, context);
    	if (template.matches("server-side-cache-buster")) throw new RuntimeException("syntax error for server-side-cache-buster in " + template);
		return template;
    }

    private String instantiate_serverSidePrefix(String template, Map<String,Object> env) {
		String ssp_src_regex = "\\bserver-side-prefix=" + stringValue + "\\s+" + srcStringValue;
    	String ssp_href_regex = "\\bserver-side-prefix=" + stringValue + "\\s+" + hrefStringValue;
		template = instantiate_serverSidePrefix(template, scriptStart + ssp_src_regex + scriptEnd, env);
		template = instantiate_serverSidePrefix(template, linkStart + ssp_href_regex + linkEnd, env);
    	if (template.matches("server-side-prefix")) throw new RuntimeException("syntax error for server-side-prefix in " + template);
		return template;
    }
    
    private String instantiate_serverSideConcat(String template, final ServletContextWrapper context) {
		String ssp_src_regex = "\\bserver-side-concat=" + stringValue + "\\s+" + srcStringValue;
    	String ssp_href_regex = "\\bserver-side-concat=" + stringValue + "\\s+" + hrefStringValue;
		template = instantiate_serverSideConcat(template, scriptStart + ssp_src_regex + scriptEnd, context);
		template = instantiate_serverSideConcat(template, linkStart + ssp_href_regex + linkEnd, context);
    	if (template.matches("server-side-concat")) throw new RuntimeException("syntax error for server-side-concat in " + template);
		return template;
    }

    private String instantiate_serverSideAngularTemplates(String template, Map<String, Object> env, final ServletContextWrapper context) {
		String ssp_src_regex = "\\bserver-side-angular-templates=" + stringValue + "\\s+" + srcStringValue;
		template = instantiate_serverSideAngularTemplates(template, scriptStart + ssp_src_regex + scriptEnd, env, context);
    	if (template.matches("server-side-angular-templates")) throw new RuntimeException("syntax error for server-side-concat in " + template);
		return template;
    }

    private String instantiate_serverSideIf(String template, Map<String, Object> env) {
		String ssp_regex = "\\bserver-side-if=" + stringValue + "\\s*";
    	template = instantiate_serverSideIf(template, scriptStart + ssp_regex + scriptEnd, env);
		template = instantiate_serverSideIf(template, linkStart + ssp_regex + linkEnd, env);
    	if (template.matches("server-side-if")) throw new RuntimeException("syntax error for server-side-if in " + template);
		return template;
    }
    
    private String instantiate_serverSideCacheBuster(String template, String regex, final ServletContextWrapper context) {
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
    
    private String instantiate_serverSidePrefix(String template, String regex, final Map<String,Object> env) {
    	return ReplaceAllWithCallback.doIt(template, regex, new ReplaceAllWithCallback.Callback() {			
			public String replace(MatchResult m) {
				String 	openTag = m.group(1),
						prefixVar = m.group(2),
						srcAttr = m.group(3),
						srcVal = m.group(4),
						endTag = m.group(5);

				String prefix = (String) env.get(prefixVar);
				if (prefix == null) throw new RuntimeException("invalid server-side-prefix " + prefixVar);
    		
				return openTag + srcAttr + "=\"" + prefix + "/" + srcVal + "\"" + endTag;
			}
    	});
    }
    
    private String instantiate_serverSideConcat(String template, String regex, final ServletContextWrapper context) {
    	final Map<String,List<File>> destination2sources = new TreeMap<String, List<File>>();
    	
    	String result = ReplaceAllWithCallback.doIt(template, regex, new ReplaceAllWithCallback.Callback() {			
			public String replace(MatchResult m) {
				String 	openTag = m.group(1),
						dest = m.group(2),
						srcAttr = m.group(3),
						srcVal = m.group(4),
						endTag = m.group(5);
				
				List<File> sources = destination2sources.get(dest);
				boolean isFirst = sources == null;
				if (isFirst) {
					sources = new LinkedList<File>();
					destination2sources.put(dest, sources);
				}
				sources.add(src2file(context, srcVal));
				
				if (isFirst) {
					return openTag + srcAttr + "=\"" + dest + "\"" + endTag;
				} else {
					return ""; // keep only the first, remove the others
				}

			}
    	});
    	
    	try {
	    	for (Entry<String,List<File>> e : destination2sources.entrySet()) {
	    		aggregateToFile.concat(e.getValue(), src2file(context, e.getKey()));
	    	}
    	} catch (IOException e) {
    		// failed to write concat, return input template unmodified
    		return template;
    	}
    	return result;
    }
    
    private String instantiate_serverSideAngularTemplates(String template, String regex, final Map<String, Object> env, final ServletContextWrapper context) {
    	return ReplaceAllWithCallback.doIt(template, regex, new ReplaceAllWithCallback.Callback() {			
			public String replace(MatchResult m) {
				String	openTag = m.group(1),
						templates = m.group(2),
						srcAttr = m.group(3),
						dest = m.group(4),			
						closeTag = m.group(5);

				String urlPrefix = getServerSidePrefix(m.group(), env);

				final Map<File,String> htmlFileToURL = new TreeMap<File, String>();
				for (String relative : templates.split("\\s+")) {
					htmlFileToURL.put(src2file(context, relative), urlPrefix + "/" + relative);
				}
				try {
					aggregateToFile.concat(htmlFileToURL.keySet(), src2file(context, dest), "", "", new AggregateToFile.Filter() {
						public String filter(File file, String html) {
							try {
								String url = htmlFileToURL.get(file);
								return prepareForClientSideInclude(prepareAngularTemplate(html, url));
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
					});
					return openTag + srcAttr + "=\"" + dest + "\"" + closeTag;
				} catch (IOException e) {
					return "";
				}

			}

    	});
    }
    
    private String instantiate_serverSideIf(String template, String regex, final Map<String,Object> env) {
    	String result = ReplaceAllWithCallback.doIt(template, regex, new ReplaceAllWithCallback.Callback() {			
			public String replace(MatchResult m) {
				String	openTag = m.group(1),
						varName = m.group(2),
						endTag = m.group(3);

				Object val = env.get(varName);
				if (val == null) throw new RuntimeException("invalid server-side-if " + varName);
    		
				return isFalse(val) ? "" : openTag + endTag;
			}
    	});
    	return result;
    }
	
    // similar to perl/python falseness
	private boolean isFalse(Object val) {
		if (val == null) return true;
		if (val instanceof Boolean) return !(Boolean) val;
		if (val instanceof String) return StringUtils.isBlank((String) val);
		if (val instanceof Number) return ((Number) val).doubleValue() == 0;
		return false;
	}

	private File src2file(final ServletContextWrapper context, String src) {
		File baseURL = new File(context.getRealPath("/"));
		return new File(baseURL, src);
	}

	private String prepareAngularTemplate(String html, String url) {
		return "<script type=\"text/ng-template\" id=\"" + url + "\">\n" + html + "\n</script>\n\n";
	}

	private String prepareForClientSideInclude(String script) throws IOException {
		return "document.write(" + new ObjectMapper().writeValueAsString(script) + ");\n";
	}

    private String getServerSidePrefix(String scriptTag, final Map<String, Object> env) {
		String urlPrefixVar = getFirstMatch("\\bserver-side-prefix=" + stringValue, scriptTag);
		
		String urlPrefix = (String) env.get(urlPrefixVar);
		if (urlPrefix == null) throw new RuntimeException("invalid server-side-prefix " + urlPrefixVar);
		return urlPrefix;
	}

	private String getFirstMatch(String re, String s) {
		Matcher m = Pattern.compile(re).matcher(s);
		return m.find() ? m.group(1) : null;
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
