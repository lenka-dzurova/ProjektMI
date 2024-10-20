package back_end.audio_video.config;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    //fixlo problem s errorom 403 aj ked mala byt vyhodena 500 napr
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AuthorizationFilter authorizationFilter) {
            authorizationFilter.setFilterErrorDispatch(false);
        }
        return bean;
    }

    @Bean
    public WebMvcConfigurer customConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
                configurer.defaultContentType(MediaType.APPLICATION_JSON);
            }
        };
    }

}