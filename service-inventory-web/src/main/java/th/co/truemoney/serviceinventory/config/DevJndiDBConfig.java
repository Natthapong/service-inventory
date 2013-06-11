package th.co.truemoney.serviceinventory.config;

import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
@Profile("dev")
@ComponentScan("com.tmn.core")
public class DevJndiDBConfig {

    private static final Logger logger = LoggerFactory.getLogger(DevJndiDBConfig.class);

    @Value("${jndi.name}")
    private String jndiName;

    @Bean
    public DataSource dataSource() throws NamingException, SQLException {
        Context context = new InitialContext();
        Context envCtx	= (Context) context.lookup("java:comp/env");
        DataSource ds = (DataSource) envCtx.lookup(jndiName);
        isValidDataSource(ds);
        logger.debug("dataSource : "+ds);
        return ds;
    }

    private void isValidDataSource(DataSource ds) throws SQLException{
            boolean result = ds.getConnection().isValid(100);
            logger.debug("dataSource is valid : "+result);
    }

    @Bean
    public static PropertyPlaceholderConfigurer dbProperties(){
      PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
      Resource[] resources = new ClassPathResource[ ]
        { new ClassPathResource( "prod_db.properties" ) };
      ppc.setLocations( resources );
      ppc.setIgnoreUnresolvablePlaceholders( true );
      return ppc;
    }

}
