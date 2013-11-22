package org.esupportail.smsu.web.controllers;

import javax.faces.component.html.HtmlInputTextarea;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.domain.beans.User;
import org.esupportail.smsu.domain.beans.fonction.FonctionName;
import org.esupportail.smsu.web.beans.TemplatesPaginator;
import org.esupportail.smsu.web.beans.UITemplate;

/**
 * @author xphp8691
 *
 */
public class TemplateManagerController extends AbstractContextAwareController {

	/**
	 * Serial Id.
	 */
	private static final long serialVersionUID = 3184009274703988880L;

	/**
	 * 
	 */
	private static final int LENGHTBODY = 160;
	
	/**
	 * a paginator.
	 */
	private TemplatesPaginator paginator;
	
	/**
	 * the template body.
	 */	
	private HtmlInputTextarea templateBody;
	
	/**
	 * a template.
	 */
	private UITemplate uiTemplate;
	
	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	//////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////
	/**
	 * a constructor.
	 */
	public TemplateManagerController() {
		super();
	}

	//////////////////////////////////////////////////////////////
	// Access control method 
	//////////////////////////////////////////////////////////////
	/**
	 * @return true
	 */
	public boolean isPageAuthorized() {
		//an access control is required for this page.
		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return false;
		}
		return currentUser.hasFonction(FonctionName.FCTN_GESTION_MODELES);
	}

	//////////////////////////////////////////////////////////////
	// Enter method (for Initialazation)
	//////////////////////////////////////////////////////////////
	/**
	 * JSF callback.
	 * @return A String.
	 */
	public String enter() {
		if (!isPageAuthorized()) {
			addUnauthorizedActionMessage();
			return null;
		}
		init();
		
		return "navigationManageTemplates";
	}

	//////////////////////////////////////////////////////////////
	// Init methods 
	//////////////////////////////////////////////////////////////
	/**
	 * initialize the page.
	 */
	private void init() {
		//can be used to initialize the page.
		paginator = new TemplatesPaginator(getDomainService());
		templateBody = new HtmlInputTextarea();
	}
	
	//////////////////////////////////////////////////////////////
	// Principal methods 
	//////////////////////////////////////////////////////////////
	/**
	 * @return a navigation rule.
	 */
	public String delete() {
		getDomainService().deleteUITemplate(uiTemplate);
		reset();
		return null;
	}
	/**
	 * @see org.esupportail.smsu.web.controllers.AbstractContextAwareController#reset()
	 */
	@Override
	public void reset() {
		super.reset();
		paginator = new TemplatesPaginator(getDomainService());
		
	}
	 
	/**
	 * @return a navigation rule.
	 */
	public String save() {

		Boolean bTemplateOk = true;
		String label = uiTemplate.getLabel();
		Integer id = uiTemplate.getId();
		String body = templateBody.getValue().toString();
		if (body.length() > LENGHTBODY) {
			logger.error("Error lenght Body: " + body.length() + " longer than: " + LENGHTBODY);
			addErrorMessage("createModifyTemplateForm:templateBody", "TEMPLATE.BODY.ERROR");
			bTemplateOk = false;
		} else {
			uiTemplate.setBody(body);	
		}
		if (!getDomainService().isTemplateLabelAvailable(label, id)) {
			addErrorMessage("createModifyTemplateForm:templateLabel", "TEMPLATE.LABEL.ERROR");
			bTemplateOk = false;
		}
		if (bTemplateOk) {
			if (uiTemplate.getId() == null) {
				getDomainService().addUITemplate(uiTemplate);
			} else {
				getDomainService().updateUITemplate(uiTemplate);
			}
			return "navigationManageTemplates";
		}
		return null;
	}
	
	/**
	 * @return navigationCreateTemplate
	 */
	public String createTemplateButton() {

		init();
		//the template is initialized.
		uiTemplate = new UITemplate();
		
		//the creation page is displayed.
		return "navigationCreateTemplate";
	}
	
	/**
	 * @return navigationModifyTemplate
	 */
	public String modifyTemplateButton() {
		//templateBody = new HtmlInputTextarea();
		templateBody.setValue(uiTemplate.getBody());
		
		return "navigationModifyTemplate";
	}
	
    //////////////////////////////////////////////////////////////
	// Getter and Setter of paginator
	//////////////////////////////////////////////////////////////
	/**
	 * @param paginator
	 */
	public void setPaginator(final TemplatesPaginator paginator) {
		this.paginator = paginator;
	}

	/**
	 * @return paginator
	 */
	public TemplatesPaginator getPaginator() {
		return paginator;
	}

    //////////////////////////////////////////////////////////////
	// Getter and Setter of uiTemplate
	//////////////////////////////////////////////////////////////
	/**
	 * @param template
	 */
	public void setUiTemplate(final UITemplate uiTemplate) {
		this.uiTemplate = uiTemplate;
	}

	/**
	 * @return template
	 */
	public UITemplate getUiTemplate() {
		return uiTemplate;
	}

	//////////////////////////////////////////////////////////////
	// Getter and Setter of templateBody
	//////////////////////////////////////////////////////////////
	/**
	 * @param templateBody
	 */
	public void setTemplateBody(final HtmlInputTextarea templateBody) {
		this.templateBody = templateBody;
	}

	/**
	 * @return the template body
	 */
	public HtmlInputTextarea getTemplateBody() {
		return templateBody;
	}
	
}
