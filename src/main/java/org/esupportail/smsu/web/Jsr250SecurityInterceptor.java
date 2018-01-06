package org.esupportail.smsu.web;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

class Jsr250SecurityInterceptor extends HandlerInterceptorAdapter { 

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public class ForbiddenException extends RuntimeException {}

    @Override 
    public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception { 
        if (handler instanceof HandlerMethod) { 
            Method method = ((HandlerMethod) handler).getMethod(); 
            if (!check(request, method)) check(request, method.getDeclaringClass());
        }
        return true;
    } 

	private boolean check(HttpServletRequest request, AnnotatedElement e) {
		return check(request, e.getAnnotations());
	}

	private boolean check(HttpServletRequest request, Annotation[] annotations) {
		for (Annotation a : annotations) {
			if (a instanceof DenyAll) {
				throw new ForbiddenException();
			}
			if (a instanceof PermitAll) {
				return true;
			}
			if (a instanceof RolesAllowed) {
				RolesAllowed ra = (RolesAllowed) a;

				for (String allowed : ra.value()) {
                    if (request.isUserInRole(allowed)) return true;
                }
                throw new ForbiddenException();
            }
        }
        return false;
	}
}