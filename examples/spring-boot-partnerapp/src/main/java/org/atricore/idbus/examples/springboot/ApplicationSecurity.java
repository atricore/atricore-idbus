package org.atricore.idbus.examples.springboot;

import org.josso.agent.Lookup;
import org.josso.agent.http.HttpSSOAgent;
import org.josso.servlet.agent.GenericServletSSOAgentFilter;
import org.josso.spring.security.JOSSOAuthenticationFilter;
import org.josso.spring.security.JOSSOAuthenticationProvider;
import org.josso.spring.security.JOSSOProcessingFilterEntryPoint;
import org.josso.spring.security.JOSSOUserDetailsService;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(jossoAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .addFilterAfter(jossoFilter(), SecurityContextPersistenceFilter.class)
            .authorizeRequests()
            .antMatchers("/css/**").permitAll()
            .antMatchers("/img/**").permitAll()
            .antMatchers("/favicon.ico").permitAll()
            .antMatchers("/").permitAll()
            .antMatchers("/josso_security_check").permitAll()
            .antMatchers("/josso_login/").permitAll()
            .antMatchers("/josso_user_login/").permitAll()
            .antMatchers("/josso_logout/").permitAll()
            .antMatchers("/logout").permitAll()
            .anyRequest()
            .hasRole("USER")    // ROLE_USER
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint())
            .and()
            .logout().logoutSuccessUrl("/josso_logout/")
            .and()
            .csrf().disable()
            .headers().cacheControl().disable();
    }

    @Bean
    public FilterRegistrationBean jossoAgentFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new GenericServletSSOAgentFilter());
        registration.addUrlPatterns("/*");
        registration.setName("JOSSOGenericServletFilter");
        registration.addInitParameter("init", "lazy");
        return registration;
    }

    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new JOSSOProcessingFilterEntryPoint();
    }

    public AuthenticationProvider jossoAuthenticationProvider() {
        return new JOSSOAuthenticationProvider();
    }

    public Filter jossoFilter() throws Exception {
        JOSSOAuthenticationFilter jossoFilter = new JOSSOAuthenticationFilter(new LogoutHandler[] { new SecurityContextLogoutHandler() });
        jossoFilter.setUserDetailsService(jossoUserDetailsService());
        return jossoFilter;
    }

    public UserDetailsService jossoUserDetailsService() throws Exception {
        JOSSOUserDetailsService userDetailsService = new JOSSOUserDetailsService();

        Lookup lookup = Lookup.getInstance();
        lookup.init("josso-agent-config.xml");
        HttpSSOAgent agent = (HttpSSOAgent) lookup.lookupSSOAgent();

        userDetailsService.setRequester(agent.getConfiguration().getSsoPartnerApps().get(0).getId());
        userDetailsService.setGatewayServiceLocator(agent.getGatewayServiceLocator());

        return userDetailsService;
    }
}
