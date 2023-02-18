package com.birthdaywisher.server;

import com.birthdaywisher.server.model.Board;
import com.birthdaywisher.server.repository.BoardRepository;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Application {

    @Autowired
    private BoardRepository boardRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    InitializingBean initDatabaseDemo() {
        return () -> {
            // This is just a placeholder for now. This is a list of IDs taken from documents that currently exist in
            // users collection of our database.
            List<ObjectId> userIds = Arrays.asList(new ObjectId("63f12b1424e25937d0545ac1"), new ObjectId("63f12b1f24e25937d0545ac2"),
                    new ObjectId("63f12b3224e25937d0545ac3"), new ObjectId("63f12b4724e25937d0545ac4"));
            // TODO: once user stuff is merged, do a similar process as below to clear & populate the user collection
            // then use those user IDs as the userId field when creating the following boards.

            boardRepository.deleteAll();

            List<Board> boards = new ArrayList<>();

            for (ObjectId userId : userIds) {
                boards.add(new Board(userId));
            }

            boardRepository.saveAll(boards);
        };
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.serializerByType(ObjectId.class, new ToStringSerializer());
    }
}
