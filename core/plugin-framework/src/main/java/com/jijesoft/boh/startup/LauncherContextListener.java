package com.jijesoft.boh.startup;

import static com.jijesoft.boh.core.plugin.PackageScannerConfigurationBuilder.packageScannerConfiguration;
import static com.jijesoft.boh.core.plugin.PluginsConfigurationBuilder.pluginsConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

import com.jijesoft.boh.core.plugin.DefaultModuleDescriptorFactory;
import com.jijesoft.boh.core.plugin.JijePlugins;
import com.jijesoft.boh.core.plugin.ModuleDescriptorFactory;
import com.jijesoft.boh.core.plugin.PluginsConfiguration;
import com.jijesoft.boh.core.plugin.event.PluginEventManager;
import com.jijesoft.boh.core.plugin.event.impl.DefaultPluginEventManager;
import com.jijesoft.boh.core.plugin.hostcontainer.SimpleConstructorHostContainer;
import com.jijesoft.boh.core.plugin.servlet.DefaultServletModuleManager;
import com.jijesoft.boh.core.plugin.servlet.ServletModuleManager;

public class LauncherContextListener implements ServletContextListener {
	private JijePlugins plugins;
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("Cleaning up...");
		plugins.stop();
	}
	
	public void contextInitialized(final ServletContextEvent event) {
		initialiseLogger(event.getServletContext().getRealPath(
				"/WEB-INF/log4j-standalone.properties"));
		String plugindirs = event.getServletContext().getRealPath(
				"/WEB-INF/plugins");
		File pluginDir = new File(plugindirs);
		
		String configFilePath=event.getServletContext().getRealPath("/WEB-INF/config");
		Env.getEnvInstance().addConfigFilePath(configFilePath);
		pluginDir.mkdir();
		
		final PluginEventManager pluginEventMgr = new DefaultPluginEventManager();
		Map<Class<?>, Object> context = new HashMap<Class<?>, Object>() {
			{
				put(ServletModuleManager.class,
						new DefaultServletModuleManager( event.getServletContext(),pluginEventMgr
								));
			}
		};
		final SimpleConstructorHostContainer factory = new SimpleConstructorHostContainer(context);
		final ModuleDescriptorFactory moduleDescriptorFactory = new DefaultModuleDescriptorFactory(factory);
		PluginsConfiguration config = pluginsConfiguration()
				.hotDeployPollingFrequency(5, TimeUnit.SECONDS)
				.pluginDirectory(pluginDir)
				.packageScannerConfiguration(packageScannerConfiguration().servletContext(event.getServletContext())
	//				packageScannerConfiguration().jarsToInclude(new File(libdir).list())
	//						.packagesToInclude("javax.servlet.*","org.apache.*",
	//								"com.jijesoft.*", "org.dom4j*")
	//						.packagesVersions(
	//								Collections.singletonMap(
	//										"org.apache.log4j", "1.2.15"))
				.build())
				.moduleDescriptorFactory(moduleDescriptorFactory).build();

	
		
		plugins = new JijePlugins(config,pluginEventMgr);
		plugins.start();
	}
	
	private static void initialiseLogger(String log4jpath) {
		final Properties logProperties = new Properties();

		try {
			logProperties.load(new FileInputStream(new File(log4jpath)));
			PropertyConfigurator.configure(logProperties);
		} catch (final IOException e) {
			throw new RuntimeException("Unable to load logging");
		} finally {
		}
	}

}
