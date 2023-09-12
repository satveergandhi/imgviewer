package com.inn;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableWebMvc
@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {
	
	  @Bean
	  public Docket getDocket() {
	    return new Docket(DocumentationType.SWAGGER_2)
	    		.groupName("public-apis")
	    		.apiInfo(getApiInfo())
	    		.select()
	    		.apis(RequestHandlerSelectors
				/* .withClassAnnotation(RestController.class) */	//excludes @controller tagged
	    		.basePackage("com.inn"))
	    		.paths(PathSelectors.any())
	    		.build();
	  }
	  
	  public ApiInfo getApiInfo() {
		  return new ApiInfoBuilder().title("Inventory data manager")
				  .description("Spring boot based rest api's to manage inventory system")
				  .version("1")
				  	.build();
	  }
	  
	  @Override
	   public void addResourceHandlers(ResourceHandlerRegistry registry) {
	       registry.addResourceHandler("swagger-ui.html")
	       .addResourceLocations("classpath:/META-INF/resources/");

	       registry.addResourceHandler("/webjars/**")
	       .addResourceLocations("classpath:/META-INF/resources/webjars/");
	   }

}
