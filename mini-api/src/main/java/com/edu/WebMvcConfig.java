package com.edu;

import com.edu.controller.interceptot.Mini_Interceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("file:D:/idea_java_project/UserData/");
    }
//    @Bean(initMethod = "Init")
//    public zkCuratorClient zkCuratorClient(){
//        //添加zookeeper
//        return new zkCuratorClient();
//    }

    @Bean
    public Mini_Interceptor mini_interceptor(){
        return new Mini_Interceptor();
    }
    //添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册拦截器
        registry.addInterceptor(mini_interceptor()).addPathPatterns("/user/**")
                                                   .addPathPatterns("/video/upload","/video/userLike",
                                                           "/video/userUnLike","/video/uploadCoverPath")
                                                    .addPathPatterns("/bgm/**")
                                                    .excludePathPatterns("/user/queryPublisherInfo");
        super.addInterceptors(registry);
    }
}
