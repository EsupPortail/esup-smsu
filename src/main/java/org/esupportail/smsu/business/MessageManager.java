package org.esupportail.smsu.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.BasicGroup;
import org.esupportail.smsu.dao.beans.Mail;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Person;
import org.esupportail.smsu.dao.beans.Recipient;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.web.beans.UIMessage;
import org.springframework.beans.factory.annotation.Autowired;


public class MessageManager {

	@Autowired private DaoService daoService;
	@Autowired private LdapUtils ldapUtils;
	
	private final Logger logger = new LoggerImpl(getClass());

	//////////////////////////////////////////////////////////////
	// Principal methods
	//////////////////////////////////////////////////////////////
	
	/**
	 * @param[userGroupId, userAccountId, userServiceId, userTemplateId, userUserId, beginDate, endDate]
	 * @return the UI messages.
	 */
	public List<UIMessage> getMessages(final Integer userGroupId, final Integer userAccountId, 
			final Integer userServiceId, final Integer userTemplateId, final Integer userUserId, 
			final Date beginDate, final Date endDate) {

		java.sql.Date beginDateSQL = 
			beginDate == null ? null : new java.sql.Date(beginDate.getTime()); // get rid of HH:MM:SS
		java.sql.Date endDateSQL =
			endDate == null ? null : new java.sql.Date(addOneDay(endDate).getTime());
	
		List<Message> messages = daoService.getMessages(userGroupId, userAccountId, userServiceId, 
								 userTemplateId, userUserId, beginDateSQL, endDateSQL);
		return convertToUI(messages);
	}

	public List<String> getUIRecipients(int messageId) {
		Message message = getMessage(messageId);
		Set<Recipient> recipients = message.getRecipients();	
		if (recipients == null) return null;
		
		List<String> result = new LinkedList<String>();
		for (Recipient r : recipients) {
		    result.add(r.getLogin() != null ? r.getLogin() : r.getPhone());
		}
		return result;
	}

	/**
	 * @param messageId
	 * @return a message
	 */
	public Message getMessage(final Integer messageId) {
		return daoService.getMessageById(messageId);
	}

	public UIMessage getUIMessage(final Integer messageId) {
		return convertToUI(Collections.singletonList(getMessage(messageId))).get(0);
	}
	
	public List<UIMessage> convertToUI(List<Message> messages) {
		Map<String, String> id2displayName = getIdToDisplayName(senderLogins(messages));

		List<UIMessage> uimessages = new ArrayList<UIMessage>();
		for (Message mess : messages) {
			uimessages.add(convertToUI(mess, id2displayName));
		}
		return uimessages;
	}

	private UIMessage convertToUI(Message mess,
			Map<String, String> id2displayName) {
		UIMessage r = new UIMessage();
		r.id = mess.getId();
		r.date = mess.getDate();
		r.content = mess.getContent();
		r.nbRecipients = mess.getRecipients().size();
		r.supervisors= convertToUI(mess.getSupervisors());
		r.senderName = retreiveNiceDisplayName(id2displayName, mess.getSender().getLogin());
		r.accountLabel = mess.getAccount().getLabel();
		r.groupSenderName = retreiveNiceGroupName(mess.getGroupSender());
		r.groupRecipientName = retreiveNiceGroupName(mess.getGroupRecipient());
		r.serviceName = mess.getService() != null ? mess.getService().getName() : null;
		r.stateMessage = convertToUI(mess.getStateAsEnum());
		r.stateMail = convertToUI(mess.getMail());
		return r;
	}
	
	private List<String> convertToUI(Set<Person> supervisors) {
		if (supervisors == null) return null;
		List<String> t = new LinkedList<String>();
		for (Person p : supervisors)
			t.add(p.getLogin());
		return t;
	}

	private Date addOneDay(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}

	private LinkedHashSet<String> senderLogins(List<Message> messages) {
		LinkedHashSet<String> l = new LinkedHashSet<String>();	       		
		for (Message mess : messages)
			l.add(mess.getSender().getLogin());
		return l;
	}

	private Map<String, String> getIdToDisplayName(Iterable<String> uids) {
		Map<String, String> result = new TreeMap<String, String>();
		for (LdapUser u : ldapUtils.getUsersByUids(uids))
		    result.put(u.getId(), ldapUtils.getUserDisplayName(u));
		return result;
	}

	private String retreiveNiceDisplayName(Map<String, String> id2displayName, String senderLogin) {
		logger.debug("mess.getSender.getLogin is: " + senderLogin);
		
		String displayName = id2displayName.get(senderLogin);
		if (displayName != null) {
			logger.debug("displayName is: " + displayName);
			return displayName + "  (" + senderLogin + ")"; 
		} else {
			return senderLogin;
		}
	}

	private String retreiveNiceGroupName(BasicGroup recipientGroup) {
		return recipientGroup != null ?
			ldapUtils.getGroupDisplayName(recipientGroup.getLabel()) : null;
	}

	private String convertToUI(MessageStatus messageStatus) {
		logger.debug("mess.getStateAsEnum : " + messageStatus);
		return messageStatus != null ? messageStatus.name() : null;
	}

	private String convertToUI(Mail mail) {
		return mail != null ? mail.getStateAsEnum().name() : null;
	}
	
}
