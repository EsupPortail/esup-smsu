package org.esupportail.smsu.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Mail;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Template;
import org.esupportail.smsu.web.beans.UITemplate;
import org.springframework.beans.factory.annotation.Autowired;


public class TemplateManager {

	@Autowired private DaoService daoService;

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(getClass());


	///////////////////////////////////////
	// Principal methods
	//////////////////////////////////////
	public Template getTemplateById(final Integer id) {
		return daoService.getTemplateById(id);
	}
	
	public void updateUITemplate(final UITemplate template) {
		daoService.updateTemplate(convertFromUI(template));
	}
	
	public void addUITemplate(final UITemplate template) {
		daoService.addTemplate(convertFromUI(template));
	}
	
	public void deleteTemplate(int id) {
		daoService.deleteTemplate(daoService.getTemplateById(id));
	}
	
	/**
	 * @param label
	 * @param id 
	 * @return true if no other template has the same key.
	 */
	public Boolean isLabelAvailable(final String label, final Integer id) {
		Template template = daoService.getTemplateByLabel(label);
		return template == null 
				|| id != null && id.equals(template.getId());
	}

	public List<UITemplate> getUITemplates() {
		List<UITemplate> listUiTemplates = new ArrayList<UITemplate>();
		for (Template template : daoService.getTemplates()) {
			listUiTemplates.add(convertToUI(template));
		}	
		return listUiTemplates;
	}
	
	/**
	 * @param template 
	 * @return true if no message is linked to 
	 */
	private Boolean testMessagesBeforeDeleteTemplate(final Template template) {
		List<Message> listMessages = daoService.getMessagesByTemplate(template);		
		return listMessages.isEmpty();
	}
	
	private Boolean testMailsBeforeDeleteTemplate(final Template template) {
		List<Mail> listMails = daoService.getMailsByTemplate(template);
		return listMails.isEmpty();
	}

	private Template convertFromUI(final UITemplate template) {
		Template tpl = new Template(template.id, template.label.trim()); 
		tpl.setHeading(template.heading);
		tpl.setBody(template.body);
		tpl.setSignature(template.signature);
		return tpl;
	}
	
	private UITemplate convertToUI(Template template) {
		UITemplate tpl = new UITemplate();
		tpl.id = template.getId();
		tpl.label = template.getLabel();
		tpl.isDeletable = isDeletable(template);
		tpl.heading = template.getHeading();
		tpl.body = template.getBody();
		tpl.signature = template.getSignature();
		return tpl;
	}

	private Boolean isDeletable(Template template) {
		Boolean isDeletable = false;
		Boolean testMail = testMailsBeforeDeleteTemplate(template);
		Boolean testMessage = testMessagesBeforeDeleteTemplate(template);
		
		if (testMail && testMessage) {
			isDeletable = true;
		}
		return isDeletable;
	}
}
