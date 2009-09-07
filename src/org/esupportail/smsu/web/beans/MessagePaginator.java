package org.esupportail.smsu.web.beans;

/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */

import java.util.Date;
import java.util.List;


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

	private Integer userUserId;

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
		 return domainService.getMessages(userGroupId, userAccountId, userServiceId, userTemplateId, userUserId, beginDate, endDate);
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

	 public MessagePaginator setUserUserId(final Integer userUserId) {
		 this.userUserId = userUserId;
		 forceReload();
		 return this;
	 }


}

