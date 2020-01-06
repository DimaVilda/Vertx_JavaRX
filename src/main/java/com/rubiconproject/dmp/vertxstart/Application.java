package com.rubiconproject.dmp.vertxstart;

import com.rubiconproject.dmp.vertxstart.conf.SpringConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Hello world!
 *
 */
public class Application
{
    public static void main(String[] args)
    {
        Logger logger = LoggerFactory.getLogger(Application.class);
        try
        {
            ConfigurableApplicationContext context =
                    new SpringApplicationBuilder()
                            .bannerMode(Banner.Mode.OFF)
                            .sources(SpringConfiguration.class)
                            .run(args);
        }
        catch (Throwable e)
        {
            logger.error("Error thrown during Application startup - " + e.getMessage(), e);
            System.exit(1);
        }
    }


}
