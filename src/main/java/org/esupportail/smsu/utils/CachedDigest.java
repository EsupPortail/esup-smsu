package org.esupportail.smsu.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;

public class CachedDigest {

    private Map<File, Long> file2lastModified= new ConcurrentHashMap<>();
    private Map<File, String> file2digest = new ConcurrentHashMap<>();
	
	public String compute(File file) {
		long lastModified = file.lastModified();
		Long prevLastModified = file2lastModified.get(file);
		String digest = null;
		if (prevLastModified != null && lastModified == prevLastModified) {
			digest = file2digest.get(file);
		}
		if (digest == null) {
			digest = computeDigest(file);
			file2lastModified.put(file, lastModified);
			file2digest.put(file, digest);
		}
		return digest;
	}

	private String computeDigest(File file) {
		try {
			//System.out.println("computing digest of " + file);
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(IOUtils.toByteArray(new FileInputStream(file)));
			return java.util.Base64.getEncoder().encodeToString(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
