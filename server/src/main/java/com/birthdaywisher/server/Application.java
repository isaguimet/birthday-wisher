package com.birthdaywisher.server;

import com.birthdaywisher.server.model.Board;
import com.birthdaywisher.server.model.Message;
import com.birthdaywisher.server.model.User;
import com.birthdaywisher.server.repository.BoardRepository;
import com.birthdaywisher.server.repository.UserRepository;
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
    private UserRepository userRepository;
    @Autowired
    private BoardRepository boardRepository;

    private ObjectId aliceId = new ObjectId("641497f20a359a777e5fd800");
    private ObjectId bobId = new ObjectId("641497f20a359a777e5fd801");
    private ObjectId eveId = new ObjectId("641497f20a359a777e5fd802");
    private ObjectId malloryId = new ObjectId("641497f20a359a777e5fd803");

    private List<User> users = Arrays.asList(
            new User(
                    aliceId, "Alice", "Wunderland", "alice@mail.com", "alice",
                    LocalDate.parse("1995-09-29"), new ArrayList<>(Arrays.asList(bobId, eveId)),
                    new HashMap<>(){{put(malloryId, true);}}, null
            ),
            new User(
                    bobId, "Bob", "Bulder", "bob@mail.com", "bob",
                    LocalDate.parse("1995-10-19"), new ArrayList<>(Arrays.asList(aliceId, eveId)),
                    new HashMap<>(){{put(malloryId, true);}}, null
            ),
            new User(
                    eveId, "Eve", "Adams", "eve@mail.com", "eve",
                    LocalDate.parse("1997-05-15"), new ArrayList<>(Arrays.asList(aliceId, bobId)),
                    new HashMap<>(){{put(malloryId, true);}}, null
            ),
            new User(
                    malloryId, "Mallory", "Manson", "mallory@mail.com", "mallory",
                    LocalDate.parse("2000-04-23"), new ArrayList<>(), new HashMap<>(){{
                put(aliceId, false);
                put(bobId, false);
                put(eveId, false);
            }}, null
            )
    );

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    public void resetUserRepo() {
        userRepository.deleteAll();
    }

    public void populateUserRepo() {
        userRepository.saveAll(users);
    }

    public void resetBoardRepo() {
        boardRepository.deleteAll();
    }

    public void populateBoardRepo() {
        List<Board> boards = new ArrayList<>();
        String bdayMsg = "Happy birthday, %s! - %s %s";

        for (User toUser : users) {
            ObjectId toUserId = toUser.getId();
            Map<ObjectId, Message> messages2021 = new HashMap<>();
            Map<ObjectId, Message> messages2022 = new HashMap<>();
            Map<ObjectId, Message> messages2023 = new HashMap<>();

            for (User fromUser : users){
                ObjectId fromUserId = fromUser.getId();

                if (toUserId != fromUserId) {
                    ObjectId oId = new ObjectId();
                    Message m = new Message(
                            oId, fromUserId, toUserId, LocalDate.parse("2023-02-18"),
                            String.format(bdayMsg, toUser.getFirstName(), fromUser.getFirstName(), fromUser.getLastName()));
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
    }

    @Bean
    InitializingBean initDatabaseDemo() {
        return () -> {
            resetUserRepo();
            populateUserRepo();

            resetBoardRepo();
            populateBoardRepo();
        };
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.serializerByType(ObjectId.class, new ToStringSerializer());
    }
}
