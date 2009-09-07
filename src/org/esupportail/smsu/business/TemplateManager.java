package org.esupportail.smsu.business;

import java.util.ArrayList;
import java.util.List;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.dao.beans.Mail;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Template;
import org.esupportail.smsu.web.beans.UITemplate;



/**
 * @author xphp8691
 *
 */
public class TemplateManager {

	/**
	 * {@link DaoService}.
	 */
	private DaoService daoService;

	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	///////////////////////////////////////
	//  constructor
	//////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public TemplateManager() {
		super();
	}

	/////////////////////////////////////////
	//  setters for spring object daoService
	/////////////////////////////////////////
	/**
	 * @param daoService
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}
	
	///////////////////////////////////////
	// Principal methods
	//////////////////////////////////////
	/**
	 * @param id
	 * @return a template
	 */
	public Template getTemplateById(final Integer id) {
		Template tpl = this.daoService.getTemplateById(id);
		return tpl;
	}
	
	/**
	 * @return all templates
	 */
	public List<Template> getTemplates() {
		return this.daoService.getTemplates();
	}
	
	/**
	 * @param template
	 */
	public void updateUITemplate(final UITemplate template) {
		Template tpl = new Template(template.getId(), template.getLabel().trim()); 
		tpl.setHeading(template.getHeading());
		tpl.setBody(template.getBody());
		tpl.setSignature(template.getSignature());
		daoService.updateTemplate(tpl);
	}
	
	/**
	 * @param template
	 */
	public void addUITemplate(final UITemplate template) {
		Template tpl = new Template(template.getId(), template.getLabel().trim()); 
		tpl.setHeading(template.getHeading());
		tpl.setBody(template.getBody());
		tpl.setSignature(template.getSignature());
		daoService.addTemplate(tpl);
	}
	
	/**
	 * @param template
	 */
	public void deleteUITemplate(final UITemplate template) {
		Template tpl = new Template(template.getId(), template.getLabel()); 
		tpl.setHeading(template.getHeading());
		tpl.setBody(template.getBody());
		tpl.setSignature(template.getSignature());
		daoService.deleteTemplate(tpl);
	}
	
	/**
	 * @param label
	 * @return a template
	 */
	public Template getTemplateByLabel(final String label) {
		return daoService.getTemplateByLabel(label);
	}
	
	/**
	 * @param label
	 * @param id 
	 * @return true if no other template has the same key.
	 */
	public Boolean isLabelAvailable(final String label, final Integer id) {
		Template template = getTemplateByLabel(label);
		Boolean bReturn = true;
		
		if (template != null) {
			if (id != null) {
				Integer tId = template.getId();
				if (!tId.equals(id)) {
					bReturn = false;
				}
			} else {
				bReturn = false;
			}
		}
		
		return bReturn;
	}
	
	/**
	 * @param template 
	 * @return true if no message is linked to 
	 */
	public Boolean testMessagesBeforeDeleteTemplate(final Template template) {
		List<Message> listMessages = new ArrayList<Message>();
		listMessages = daoService.getMessagesByTemplate(template);
		
		if (listMessages.isEmpty()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * @param template
	 * @return
	 */
	public Boolean testMailsBeforeDeleteTemplate(final Template template) {
		List<Mail> listMails = new ArrayList<Mail>();
		listMails = daoService.getMailsByTemplate(template);
		
		if (listMails.isEmpty()) {
			logger.debug("listMails in method testMailsBeforeDeleteTemplate is empty");
			return true;
		}
		
		return false;
	}
	
	/**
	 * @return the list of UI templates.
	 */
	public List<UITemplate> getUITemplates() {
		List<UITemplate> listUiTemplates = new ArrayList<UITemplate>();
		List<Template> listTemplates = daoService.getTemplates();
		
		for (Template template : listTemplates) {
			Boolean isDeletable = false;
			Boolean testMail = testMailsBeforeDeleteTemplate(template);
			Boolean testMessage = testMessagesBeforeDeleteTemplate(template);
			
			if (testMail && testMessage) {
				isDeletable = true;
			}
			UITemplate tpl = new UITemplate(template.getId(), template.getLabel(), isDeletable);
			tpl.setHeading(template.getHeading());
			tpl.setBody(template.getBody());
			tpl.setSignature(template.getSignature());
			listUiTemplates.add(tpl);
		}
		
		
		return listUiTemplates;
	}
}
