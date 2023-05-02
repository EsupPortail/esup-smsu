//package org.esupportail.smsu.configuration;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//@EnableWebMvc
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry //
//          .addResourceHandler("/webjars/**") //
//          .addResourceLocations("/webjars/");
//        
////        registry //
////        .addResourceHandler("/css/**") //
////        .addResourceLocations("/src/main/webapp/css/"); //
//    }
////    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
////            "classpath:/META-INF/resources/", "classpath:/resources/",
////            "classpath:/static/", "classpath:/public/" };
////
////    @Override
////    public void addResourceHandlers(ResourceHandlerRegistry registry) {
////        registry.addResourceHandler("/**")
////            .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
////    }
//}