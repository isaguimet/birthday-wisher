package com.birthdaywisher.server.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

// https://blog.marcusjanke.de/mongodb-map-keys-with-dots-in-spring-data-d470e6361fa
@Configuration
public class MongoMapKeyDotReplacementConfiguration {

    @Autowired
    public void setMapKeyDotReplacement(MappingMongoConverter mappingMongoConverter) {
        mappingMongoConverter.setMapKeyDotReplacement("#");
    }
}
