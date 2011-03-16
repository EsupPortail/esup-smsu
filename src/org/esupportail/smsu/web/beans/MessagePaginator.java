package org.esupportail.smsu.web.beans;

/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */

import java.util.Date;
import java.util.LinkedList;
import java.util.List;


import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.web.beans.ListPaginator;
import org.esupportail.smsu.domain.DomainService;


/** 
 * A paginator for messages.
 */ 
public class MessagePaginator extends ListPaginator<UIMessage> {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 5351945226908445094L;
	private final Logger logger = new LoggerImpl(this.getClass());
	/**
	 * The domain service.
	 */
	private DomainService domainService;

	private Date beginDate;

	private Date endDate;

	private Integer userGroupId;

	private Integer userAccountId;

	private Integer userServiceId;

	private Integer userTemplateId;

	private String userUserId;

	 //////////////////////////////////////////////////////////////
	 // Constructors
	 //////////////////////////////////////////////////////////////
	 /**
	  * Constructor.
	  * @param domainService 
	  */
	 @SuppressWarnings("deprecation")
	 public MessagePaginator(final DomainService domainService) {
		 super(null, 0);
		 this.domainService = domainService;
	 }

	 //////////////////////////////////////////////////////////////
	 // Principal method getData()
	 //////////////////////////////////////////////////////////////
	 /**
	  * @see org.esupportail.commons.web.beans.ListPaginator#getData()
	  */
	 @Override
	 protected List<UIMessage> getData() {
		 List<UIMessage> data = new LinkedList<UIMessage>();
		 if (logger.isDebugEnabled()) {
			 logger.debug("userUserId : " + userUserId);
		 }
		 if (!userUserId.equals("noId")) {
			 if (logger.isDebugEnabled()) {
				 logger.debug("user id exists");
			 }
			 final Integer userId = Integer.valueOf(userUserId);
			 if (logger.isDebugEnabled()) {
				 logger.debug("userId : " + userId.toString());
			 }
			 data = domainService.getMessages(userGroupId, userAccountId, userServiceId, userTemplateId, userId, beginDate, endDate);
		
		 } 
		
		 return data;
		 //return domainService.getMessages();
	 } 

	 //////////////////////////////////////////////////////////////
	 // Add all setters
	 //////////////////////////////////////////////////////////////
	 public MessagePaginator setBeginDate(final Date beginDate) {
		 this.beginDate = beginDate;
		 forceReload();
		 return this;
	 }

	 public MessagePaginator setEndDate(final Date endDate) {
		 this.endDate = endDate;
		 forceReload();
		 return this;
	 }

	 public MessagePaginator setUserGroupId(final Integer userGroupId) {
		 this.userGroupId = userGroupId;
		 forceReload();
		 return this;
	 }

	 public MessagePaginator setUserAccountId(final Integer userAccountId) {
		 this.userAccountId = userAccountId;
		 forceReload();
		 return this;
	 }

	 public MessagePaginator setUserServiceId(final Integer userServiceId) {
		 this.userServiceId = userServiceId;
		 forceReload();
		 return this;
	 }

	 public MessagePaginator setUserTemplateId(final Integer userTemplateId) {
		 this.userTemplateId = userTemplateId;
		 forceReload();
		 return this;
	 }

	 public MessagePaginator setUserUserId(final String userUserId2) {
		 this.userUserId = userUserId2;
		 forceReload();
		 return this;
	 }


}

