package com.thanos.SecurityDemo.security;

import com.thanos.SecurityDemo.filter.CustomAuthenticationFilter;
import com.thanos.SecurityDemo.filter.CustomAuthorizationFilter;
import com.thanos.SecurityDemo.security.enums.UserPermission;
import com.thanos.SecurityDemo.security.enums.UserRole;
import com.thanos.SecurityDemo.service.DaoUserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final DaoUserAppService daoUserAppService;

    @Autowired
    public WebSecurityConfig(PasswordEncoder passwordEncoder, DaoUserAppService daoUserAppService) {
        this.passwordEncoder = passwordEncoder;
        this.daoUserAppService = daoUserAppService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .addFilter(new CustomAuthenticationFilter(authenticationManager()))
                .addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)

                .authorizeRequests()
                .antMatchers("/","/login","/request/refreshToken").permitAll()
                .antMatchers("/admin").hasRole(UserRole.ADMIN.name())
                .antMatchers("/client").hasAnyRole("ADMIN","CLIENT")
//                .antMatchers("/admin").hasAuthority(UserPermission.ADMIN_READ.getPermission())
//                .antMatchers("/client").hasAnyAuthority("admin:read","client:read")
                .antMatchers(HttpMethod.GET,"/allCus","/customerID/*").hasAuthority("customer:read")
                .antMatchers(HttpMethod.POST,"/addCus").hasAuthority(UserPermission.CUSTOMER_WRITE.getPermission())
                .antMatchers(HttpMethod.PUT,"/updateCus/*").hasAuthority(UserPermission.CUSTOMER_WRITE.getPermission())
                .antMatchers(HttpMethod.DELETE,"/deleteCus/*").hasAuthority(UserPermission.CUSTOMER_WRITE.getPermission())

                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();

//        This function is to connect to H2 database on browswer
        http.headers().frameOptions().disable();
//                .formLogin()
//                .loginPage("/login").permitAll()
//                .defaultSuccessUrl("/cus",true)
//                .and()
//                .logout()
//                .permitAll()
//                .logoutSuccessUrl("/");

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(daoUserAppService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
