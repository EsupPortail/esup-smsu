/* Copyright 2001 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.portal.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * Provides a means to resolve uPortal DTDs
 * @author Peter Kharchenko, pkharchenko@unicon.net
 * @author Ken Weiner, kweiner@unicon.net
 * @author Dave Wallace, dwallace@udel.edu modifications
 * @version $Revision: 36727 $
 */
public class DTDResolver implements EntityResolver
{

  private static final Log log = LogFactory.getLog(DTDResolver.class);

  private static final String dtdPath = "dtd";

  private static class PublicId {
      public String publicId;
      public String dtdFile;
      public PublicId(final String publicId, final String dtdFile) {
          this.publicId = publicId;
          this.dtdFile = dtdPath + "/"  + dtdFile;
      }
  }
  private static final PublicId[] publicIds = new PublicId[]{
      new PublicId ("-//Netscape Communications//DTD RSS 0.91//EN", "rss-0.91.dtd"),
      new PublicId ("-//uPortal//Tables/EN", "tables.dtd"),
      new PublicId ("-//uPortal//PersonDirs/EN", "PersonDirs.dtd"),
      new PublicId ("-//uPortal//Channel Publishing/EN", "channelPublishingDocument.dtd"),
      new PublicId ("-//uPortal//Data/EN", "data.dtd"),
      new PublicId ("-//uPortal//PAGSGroupStore/EN", "PAGSGroupStore.dtd"),
      new PublicId ("-//uPortal//LDAPGroupStore/EN", "LDAPGroupStore.dtd")};
  private String dtdName = null;

  /**
   * Constructor for DTDResolver
   */
  public DTDResolver () {
  }

  /**
   * Constructor for DTDResolver
   * @param dtdName the name of the dtd
   */
  public DTDResolver (String dtdName) {
    this.dtdName = dtdName;
  }

  /**
   * Sets up a new input source based on the dtd specified in the xml document
   * @param publicId the public ID
   * @param systemId the system ID
   * @return an input source based on the dtd specified in the xml document
   *               or null if we don't have a dtd that matches systemId or publicId
   */
  public InputSource resolveEntity (String publicId, String systemId) {
    InputStream inStream = null;

    // Check for a match on the systemId
    if (systemId != null) {
      if (dtdName != null && systemId.indexOf(dtdName) != -1) {
        inStream = getResourceAsStream(dtdPath + "/" + dtdName);
      } else if (systemId.trim().equalsIgnoreCase("http://my.netscape.com/publish/formats/rss-0.91.dtd")) {
        inStream = getResourceAsStream(dtdPath + "/rss-0.91.dtd");
      }

      if ( null != inStream ) {
          return new InputSource(inStream);
      }
    }

    // Check for a match on the public id
    if ( publicId != null ) {
        publicId = publicId.trim();
        for (int i = 0; i < publicIds.length; i++) {
            if (publicId.equalsIgnoreCase(publicIds[i].publicId)) {
                inStream = getResourceAsStream(publicIds[i].dtdFile);
                if ( null != inStream ) {
                    return new InputSource(inStream);
                }
                break;
            }
        }
    }

    // Return null to let the parser handle this entity
    return null;
  }

  public InputStream getResourceAsStream(String resource){

            try {
				final URL root = DTDResolver.class.getResource("/");
				return new FileInputStream(root.toURI().getPath() + "../../" + resource);
			} catch (FileNotFoundException e) {
				log.error("Unable to find path to dtd " + resource, e);
			} catch (URISyntaxException e) {
				log.error("Unable to find path to dtd " + resource, e);
			}
			return null;

  }
}
