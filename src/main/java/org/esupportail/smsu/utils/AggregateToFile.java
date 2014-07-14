package org.esupportail.smsu.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;

public class AggregateToFile {

	private static final String encoding = "UTF-8";

	@Inject private CachedDigest cachedDigest;
	
    private Map<String, String> file2sourceDigests = new ConcurrentHashMap<String,String>();

    static public interface Filter
    {
        public String filter(File filename, String content);
    }

    public void concat(Collection<File> sources, File dest) throws IOException {
    	concat(sources, dest, null, null, null);
    }

    public void concat(Collection<File> sources, File dest, String header, String footer, Filter filter) throws IOException {
    	String newDigest = computeDigest(sources);
    	String prevDigest = file2sourceDigests.get(dest.toString());
    	if (prevDigest == null || !prevDigest.equals(newDigest) || !dest.exists()) { // NB: checking existence in case the file has been removed since we generated last time
    		concatAlwaysAtomic(sources, dest, header, footer, filter);
    		file2sourceDigests.put(dest.toString(), newDigest);
    	}    	
    }
    
	private String computeDigest(Collection<File> sources) {
		StringBuffer digest = new StringBuffer();
    	for (File source : sources) digest.append(cachedDigest.compute(source));
    	return digest.toString();
	}

    private void concatAlwaysAtomic(Collection<File> sources, File dest, String header, String footer, Filter filter) throws IOException {
    	File tmp = tmpFile(dest);
    	try {
	    	concatAlways(sources, tmp, header, footer, filter);
	    	tmp.renameTo(dest);
    	} finally {
    		tmp.delete(); // ensure no temp file lies if something goes wrong
    	}
    }

	private void concatAlways(Collection<File> sources, File dest, String header, String footer, Filter filter) throws IOException {
		OutputStream out = new FileOutputStream(dest);
		try {
			if (header != null) IOUtils.write(header, out);
		
			for (File source : sources) {
				FileInputStream in = new FileInputStream(source);
				
				if (filter == null) {
					IOUtils.copy(in, out);
				} else {
					String s = filter.filter(source, IOUtils.toString(in, encoding));
					IOUtils.write(s, out, encoding);
				}
			}
			if (footer != null) IOUtils.write(footer, out);
		} finally {
			out.close();
		}
	}
    
    private File tmpFile(File alike) throws IOException {
    	return File.createTempFile("tmp", "-" + alike.getName(), alike.getParentFile());
    }

}
