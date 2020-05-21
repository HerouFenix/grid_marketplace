package com.api.demo.grid.controller;

import com.api.demo.DemoApplication;
import com.api.demo.grid.models.*;
import com.api.demo.grid.pojos.*;
import com.api.demo.grid.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class GridRestControllerIT {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameGenreRepository gameGenreRepository;

    @Autowired
    private DeveloperRepository developerRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellRepository sellRepository;

    @Autowired
    private GameKeyRepository gameKeyRepository;

    @Autowired
    private MockMvc mockMvc;

    private GameGenrePOJO gameGenrePOJO;
    private GamePOJO gamePOJO;
    private PublisherPOJO publisherPOJO;
    private DeveloperPOJO developerPOJO;
    private SellPOJO sellPOJO;
    private GameKeyPOJO gameKeyPOJO;

    @BeforeEach
    public void setUp(){
        gameGenrePOJO = new GameGenrePOJO("genre", "");
        publisherPOJO = new PublisherPOJO("publisher", "");
        developerPOJO = new DeveloperPOJO("developer");
        gamePOJO = new GamePOJO("game", "", null, null, null, null, "");
        gamePOJO.setDevelopers(new HashSet<String>(Arrays.asList("developer")));
        gamePOJO.setGameGenres(new HashSet<String>(Arrays.asList("genre")));
        gamePOJO.setPublisher("publisher");
        gameKeyPOJO = new GameKeyPOJO("key", 2L, "steam", "ps3");
        sellPOJO = new SellPOJO("key", 6L, 2.3, null);
        gameRepository.deleteAll();
        gameGenreRepository.deleteAll();
        developerRepository.deleteAll();
        publisherRepository.deleteAll();
        userRepository.deleteAll();
        gameKeyRepository.deleteAll();
        sellRepository.deleteAll();

    }

    @Test
    void whenPostingValidGenre_ReturnValidResponse() throws Exception{

        mockMvc.perform(post("/grid/genre")
                .content(asJsonString(gameGenrePOJO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(gameGenrePOJO.getName()))).andReturn();

        assertFalse(gameGenreRepository.findByName(gameGenrePOJO.getName()).isEmpty());

    }

    @Test
    void whenPostingValidPub_ReturnValidResponse() throws Exception{
        mockMvc.perform(post("/grid/publisher")
                .content(asJsonString(publisherPOJO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(publisherPOJO.getName())));
        assertFalse(publisherRepository.findByName(publisherPOJO.getName()).isEmpty());

    }

    @Test
    void whenPostingValidDeveloper_ReturnValidResponse() throws Exception{
        mockMvc.perform(post("/grid/developer")
                .content(asJsonString(developerPOJO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(developerPOJO.getName())));
        assertFalse(developerRepository.findByName(developerPOJO.getName()).isEmpty());
    }

    @Test
    void whenPostingValidGame_ReturnValidResponse() throws Exception{
        Developer developer = new Developer();
        developer.setName("dev");
        developerRepository.save(developer);

        Publisher publisher = new Publisher();
        publisher.setName("pub");
        publisherRepository.save(publisher);

        GameGenre gameGenre = new GameGenre();
        gameGenre.setName("genre");
        gameGenreRepository.save(gameGenre);

        gamePOJO.setPublisher("pub");
        gamePOJO.setDevelopers(new HashSet<>(Arrays.asList("dev")));
        gamePOJO.setGameGenres(new HashSet<>(Arrays.asList("genre")));
        mockMvc.perform(post("/grid/game")
                .content(asJsonString(gamePOJO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(gamePOJO.getName())))
        ;
        assertFalse(gameRepository.findAllByNameContains(gamePOJO.getName()).isEmpty());
    }

    @Test
    void whenPostingInvalidGame_ReturnErrorResponse() throws Exception{
        gamePOJO.setPublisher(null);
        mockMvc.perform(post("/grid/game")
                .content(asJsonString(gamePOJO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().reason("Could not save Game"));
        assertTrue(gameRepository.findAllByNameContains(gamePOJO.getName()).isEmpty());
    }

    @Test
    void whenPostingValidGameKey_ReturnValidGameKeyObject() throws Exception{
        Game game = new Game();
        gameRepository.save(game);
        gameKeyPOJO.setGameId(game.getId());
        mockMvc.perform(post("/grid/gamekey")
                .content(asJsonString(gameKeyPOJO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key", is("key")))
                .andExpect(jsonPath("$.gameId", is(Math.toIntExact(game.getId()))))
        ;
    }

    @Test
    void whenPostingInvalidGameKey_Return404Exception() throws Exception{
        gameKeyPOJO.setGameId(-1);
        mockMvc.perform(post("/grid/gamekey")
                .content(asJsonString(gameKeyPOJO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().reason("Could not save Game Key"))
        ;
    }

    @Test
    void whenPostingValidSellListing_ReturnValidSellObject() throws Exception{
        User user = new User();
        userRepository.save(user);
        GameKey gameKey = new GameKey();
        gameKey.setKey("key");
        gameKeyRepository.save(gameKey);
        sellPOJO.setUserId(user.getId());
        sellPOJO.setGameKey("key");
        mockMvc.perform(post("/grid/sell-listing")
                .content(asJsonString(sellPOJO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameKey.key", is("key")))
                .andExpect(jsonPath("$.userId", is(Math.toIntExact(user.getId()))))
        ;
    }

    @Test
    void whenPostingInvalidSellListing_Return404Exception() throws Exception{
        mockMvc.perform(post("/grid/sell-listing")
                .content(asJsonString(sellPOJO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().reason("Could not save Sell Listing"))
        ;
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
