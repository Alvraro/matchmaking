package com.riotgames.interview.hongkong.matchmaking.matchmaker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/** 
 * MatchmakerFactory that creates a matchmaker with the default values specified in properties file
 */
public class DefaultMatchmakerFactory implements AbstractMatchmakerFactory {
	/** Path to the Matchmaker's configuration file */
	private static final String CONFIG_FILE = "src/main/resources/DefaultMatchmakerFactory.properties";

	/** Configuration properties */
	private Properties properties;

	private AbstractMatchmakerFactory matchmakerFactory;
	
	public DefaultMatchmakerFactory() throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		// Read default values from properties file		
		properties = new Properties();
		properties.load(new FileInputStream(CONFIG_FILE));

		// Build desired matchmakerFactory from properties file 
		String matchmakerFactoryClassname = properties.getProperty("matchmaker.factory.class");
		matchmakerFactory = (AbstractMatchmakerFactory) Class.forName(matchmakerFactoryClassname).newInstance();
	}

	@Override
	public Matchmaker createMatchMaker() {
		// Delegate on desired matchmakerFactory to create the matchmaker
		return matchmakerFactory.createMatchMaker();
	}

}
