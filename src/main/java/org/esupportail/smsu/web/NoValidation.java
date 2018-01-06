package org.esupportail.smsu.web;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;


/* use our own version until we switch to spring 4 */
public class NoValidation implements Validator {

    public boolean supports(Class clazz) {
       return false;
    }

    public void validate(Object target, Errors errors) {
    }
 }    
