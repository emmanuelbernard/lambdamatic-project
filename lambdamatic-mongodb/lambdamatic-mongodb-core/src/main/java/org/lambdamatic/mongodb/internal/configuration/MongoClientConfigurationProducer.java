/**
 * 
 */
package org.lambdamatic.mongodb.internal.configuration;

import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;

/**
 * Utility class to load and read the JSON-based application configuration.
 * 
 * @author xcoulon
 * 
 */
@ApplicationScoped
public class MongoClientConfigurationProducer {

	@Produces
	@Default
	public MongoClientConfiguration getMongoDBClientConfiguration() {
		final InputStream jsonConfigFile = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("config.json");
		final JsonReader reader = Json.createReader(jsonConfigFile);
		final JsonObject root = (JsonObject) reader.read();
		final String databaseName = ((JsonString) root.get("databaseName")).getString();
		final MongoClientConfiguration mongoDBClientConfiguration = new MongoClientConfiguration(databaseName);
		return mongoDBClientConfiguration;
	}

}
