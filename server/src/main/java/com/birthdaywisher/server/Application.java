package com.birthdaywisher.server;

import com.birthdaywisher.server.model.Board;
import com.birthdaywisher.server.model.Message;
import com.birthdaywisher.server.repository.BoardRepository;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.*;

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
            List<ObjectId> userIds = Arrays.asList(new ObjectId("63e947477d6de33dfab29aac"), new ObjectId("63e8878d9598d713e77c3b79"), new ObjectId("63f3ec13353bb43e7c82eb21"), new ObjectId("63f2f66168b165647bbfb41a"));
            // TODO: once user stuff is merged, do a similar process as below to clear & populate the user collection
            // then use those user IDs as the userId field when creating the following boards.

            boardRepository.deleteAll();

            List<Board> boards = new ArrayList<>();

            for (ObjectId toUserId : userIds) {
                Map<ObjectId, Message> messages2021 = new HashMap<>();
                Map<ObjectId, Message> messages2022 = new HashMap<>();
                Map<ObjectId, Message> messages2023 = new HashMap<>();

                for (ObjectId fromUserId : userIds){
                    if (toUserId != fromUserId) {
                        ObjectId oId = new ObjectId();
                        Message m = new Message(
                                oId, fromUserId, toUserId, LocalDate.parse("2023-02-18"), "Happy Birthday!");
                        messages2021.put(oId, m);
                        messages2022.put(oId, m);
                        messages2023.put(oId, m);
                    }
                }

                boards.add(new Board(new ObjectId(), true, false, "2021", toUserId, messages2021));
                boards.add(new Board(new ObjectId(), false, true, "2022", toUserId, messages2022));
                boards.add(new Board(new ObjectId(), true, true, "2023", toUserId, messages2023));
            }

            boardRepository.saveAll(boards);
        };
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.serializerByType(ObjectId.class, new ToStringSerializer());
    }
}
