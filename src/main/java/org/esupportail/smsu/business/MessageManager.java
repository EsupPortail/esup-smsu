package org.esupportail.smsu.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.esupportail.commons.services.ldap.LdapUser;

import org.apache.log4j.Logger;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.BasicGroup;
import org.esupportail.smsu.dao.beans.Mail;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Person;
import org.esupportail.smsu.dao.beans.Recipient;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.esupportail.smsu.services.GroupUtils;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.web.beans.UIMessage;
import org.esupportail.smsu.web.controllers.InvalidParameterException;

import javax.inject.Inject;

public class MessageManager {

	@Inject private DaoService daoService;
	@Inject private LdapUtils ldapUtils;
	@Inject private GroupUtils groupUtils;
	
	private final Logger logger = Logger.getLogger(getClass());

	//////////////////////////////////////////////////////////////
	// Principal methods
	//////////////////////////////////////////////////////////////
	/**
	 *	if senderLogin is null, all messages are returned. 
	 *	if senderLogin is not null, only the messages of the sender associated to senderLogin are returned. 
	 *	If there is no sender associated to senderLogin, an empty list is returned.
	 */
	public List<UIMessage> getMessages(final String senderLogin, int maxResults) {
		Person sender = null;
		
		if (senderLogin != null) {
			sender = daoService.getPersonByLogin(senderLogin);
			if (sender == null) {
				return Collections.emptyList();
			}
		}
		List<Message> messages = daoService.getMessages(sender, maxResults);
		
		return convertToUI(messages);
	}

	/**
	 * @param messageId
	 * @return a message
	 */
	public Message getMessage(final Integer messageId, String allowedSender) {		
		Message msg = daoService.getMessageById(messageId);
		if (allowedSender != null && !allowedSender.equals(msg.getSender().getLogin())) {
			throw new InvalidParameterException(allowedSender + " is not allowed to view message " + messageId);
		}
		return msg;
	}

	public UIMessage getUIMessage(final Integer messageId, String allowedSender) {
		Message msg = getMessage(messageId, allowedSender);
		if (msg == null) return null;
		return convertToUI(Collections.singletonList(msg)).get(0);
	}
	
	public List<UIMessage> convertToUI(List<Message> messages) {
		Map<String, String> id2displayName = getIdToDisplayName(senderLogins(messages));

		List<UIMessage> uimessages = new ArrayList<>(messages.size());
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
		r.recipients = convertRecipientsToUI(mess.getRecipients());
		r.supervisors= convertToUI(mess.getSupervisors());
		r.senderLogin = mess.getSender().getLogin();
		r.senderName = id2displayName.get(r.senderLogin);
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
		List<String> t = new LinkedList<>();
		for (Person p : supervisors)
			t.add(p.getLogin());
		return t;
	}

	private List<String> convertRecipientsToUI(Set<Recipient> recipients) {
		if (recipients == null) return null;
		
		List<String> result = new LinkedList<>();
		for (Recipient r : recipients) {
		    result.add(r.getLogin() != null ? r.getLogin() : r.getPhone());
		}
		return result;
	}

	private LinkedHashSet<String> senderLogins(List<Message> messages) {
		LinkedHashSet<String> l = new LinkedHashSet<>();	       		
		for (Message mess : messages)
			l.add(mess.getSender().getLogin());
		return l;
	}

	private Map<String, String> getIdToDisplayName(Iterable<String> uids) {
		Map<String, String> result = new TreeMap<>();
		for (LdapUser u : ldapUtils.getUsersByUids(uids))
		    result.put(u.getId(), ldapUtils.getUserDisplayName(u));
		return result;
	}

	private String retreiveNiceGroupName(BasicGroup recipientGroup) {
		return recipientGroup != null ?
			groupUtils.getGroupDisplayName(recipientGroup) : null;
	}

	private String convertToUI(MessageStatus messageStatus) {
		logger.debug("mess.getStateAsEnum : " + messageStatus);
		return messageStatus != null ? messageStatus.name() : null;
	}

	private String convertToUI(Mail mail) {
		return mail != null ? mail.getStateAsEnum().name() : null;
	}
	
}
