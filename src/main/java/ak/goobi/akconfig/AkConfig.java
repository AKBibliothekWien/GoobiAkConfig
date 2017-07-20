package main.java.ak.goobi.akconfig;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class AkConfig {

	private String configFilePath;

	
	public Map<String, String> getAkConfig() {
		
		String serverFilePath = akConfigProperties().getProperty("server.akconfigfile", "/opt/digiverso/goobi/config/goobi_ak.xml");
		String localFilePath = akConfigProperties().getProperty("local.akconfigfile", null);
		
		File configFile = new File(serverFilePath);
		if (!configFile.exists()) {
			configFilePath = localFilePath;
			configFile = new File(configFilePath);
			if (!configFile.exists()) {
				System.err.println("Konfigurationsdatei " + configFilePath + " existiert nicht! Bitte pruefen und ggf. anlegen.");
				return null;
			}
		} else {
			configFilePath = serverFilePath;
		}

		Map<String, String> akConfigMap = new HashMap<String, String>();

		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<XMLConfiguration> builder = new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class).configure(params.xml().setFileName(configFilePath));

		try {
			XMLConfiguration akConfig = builder.getConfiguration();
			Iterator<String> keys = akConfig.getKeys();

			if (!keys.hasNext()) {
				System.err.println("Konfigurationsdatei " + configFilePath + " enthaelt keine Konfigurationsangaben. Bitte oeffnen Sie die Datei und fuegen Sie die entsprechenden Angaben hinzu.");
				return null;
			} else {
				while(keys.hasNext()){
					String key = keys.next();
					String value = akConfig.getString(key);
					akConfigMap.put(key, value);
				}
			}
		} catch(ConfigurationException e) {
			System.err.println("\n----------\nError information for developer:\n");
			e.printStackTrace();
		}

		akConfigMap = (!akConfigMap.isEmpty()) ? akConfigMap : null;

		return akConfigMap;
	}
	
	
	private Properties akConfigProperties() {
		Properties mabProperties = new Properties();
		BufferedInputStream propertiesInputStream = null;
		propertiesInputStream = new BufferedInputStream(AkConfig.class.getResourceAsStream("/main/resources/config.properties"));

		try {
			// Load contents of properties-file:
			mabProperties.load(propertiesInputStream);
			
			// Close properties input stream:
			propertiesInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return mabProperties;
	}



}
