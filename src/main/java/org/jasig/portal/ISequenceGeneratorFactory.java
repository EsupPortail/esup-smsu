/* Copyright 2001 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.portal;

/**
 * A factory for ISequenceGenerators.
 * @author Dan Ellentuck
 * @version $Revision: 34810 $
 */
public interface ISequenceGeneratorFactory {
/**
 * @return org.jasig.portal.ISequenceGenerator
 */
ISequenceGenerator getSequenceGenerator();
}
