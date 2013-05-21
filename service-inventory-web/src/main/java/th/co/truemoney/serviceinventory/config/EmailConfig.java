package th.co.truemoney.serviceinventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

@Configuration
@Import({DevEmailConfig.class, LocalEmailConfig.class})
public class EmailConfig {
		
	@Bean
	public FreeMarkerConfigurationFactory freeMarkerConfigurationFactory() {
		FreeMarkerConfigurationFactory freeMarkerConfigurationFactory = new FreeMarkerConfigurationFactory();
		freeMarkerConfigurationFactory.setTemplateLoaderPath("email-template");
		return freeMarkerConfigurationFactory;
	}	
	
}
