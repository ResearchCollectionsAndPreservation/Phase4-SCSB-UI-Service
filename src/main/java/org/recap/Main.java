package org.recap;

import brave.sampler.Sampler;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
//import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;

/**
 * The type Main.
 */
@PropertySource("classpath:application.properties")
@SpringBootApplication(scanBasePackages = {"org.recap.controller", "org.recap.*"})
public class Main {

    /**
     * The Tomcat max parameter count.
     */
    @Value("${tomcat.maxParameterCount}")
    Integer tomcatMaxParameterCount;

    /**
     * The Tomcat secure.
     */
    @Value("${server.secure}")
    boolean tomcatSecure;

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
    /**
     * Servlet container factory embedded servlet container factory.
     *
     * @return the embedded servlet container factory
     */
    @Bean
    public ServletWebServerFactory servletContainerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                connector.setMaxParameterCount(tomcatMaxParameterCount);
                connector.setSecure(tomcatSecure);
            }
        });
        return factory;
    }

    /**
     * Get filter registered bean filter registration bean.
     *
     * @return the filter registration bean
     */
  /*  @Bean
    public FilterRegistrationBean getFilterRegisteredBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        Set<String> urlPatterns = new HashSet<>();
        urlPatterns.add("/*");
        filterRegistrationBean.setUrlPatterns(urlPatterns);
        filterRegistrationBean.setFilter(new SessionFilter());
        return filterRegistrationBean;
    }*/

    /**
     * Gets xss filter registered bean.
     *
     * @return the xss filter registered bean
     */
  /*  @Bean
    public FilterRegistrationBean getXSSFilterRegisteredBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        Set<String> urlPatterns = new HashSet<>();
        urlPatterns.add("/request");
        urlPatterns.add("/roles#");
        urlPatterns.add("/userRoles#");
        filterRegistrationBean.setUrlPatterns(urlPatterns);
        filterRegistrationBean.setFilter(new XSSFilter());
        return filterRegistrationBean;
    }*/

    /**
     * Container customizer embedded servlet container customizer.
     *
     * @return the embedded servlet container customizer
     */

    /*@Bean
    public WebServerFactoryCustomizer containerCustomizer() {
        return new WebServerFactoryCustomizer() {
            @Override
            public void customize(WebServerFactory container) {
                if (container.getClass().isAssignableFrom(TomcatServletWebServerFactory.class)) {
                    TomcatServletWebServerFactory tomcatContainer = (TomcatServletWebServerFactory) container;
                    tomcatContainer.addContextCustomizers(new ContextSecurityCustomizer());
                }
            }
        };
    }*/
    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }

   /* private static class ContextSecurityCustomizer implements TomcatContextCustomizer {
        @Override
        public void customize(Context context) {
            SecurityConstraint constraint = new SecurityConstraint();
            SecurityCollection securityCollection = new SecurityCollection();
            securityCollection.setName("restricted_methods");
            securityCollection.addPattern("/*");
            securityCollection.addMethod(HttpMethod.OPTIONS.toString());
            securityCollection.addMethod(HttpMethod.HEAD.toString());
            securityCollection.addMethod(HttpMethod.PUT.toString());
            securityCollection.addMethod(HttpMethod.PATCH.toString());
            securityCollection.addMethod(HttpMethod.DELETE.toString());
            securityCollection.addMethod(HttpMethod.TRACE.toString());
            constraint.addCollection(securityCollection);
            constraint.setAuthConstraint(true);
            context.addConstraint(constraint);
        }
    }*/

}
