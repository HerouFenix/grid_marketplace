package com.api.demo.grid.controller;


import com.api.demo.DemoApplication;
import com.api.demo.grid.dtos.UserDTO;
import com.api.demo.grid.models.Auction;
import com.api.demo.grid.models.Game;
import com.api.demo.grid.models.GameKey;
import com.api.demo.grid.models.User;
import com.api.demo.grid.pojos.AuctionPOJO;
import com.api.demo.grid.repository.AuctionRepository;
import com.api.demo.grid.repository.GameKeyRepository;
import com.api.demo.grid.repository.GameRepository;
import com.api.demo.grid.repository.UserRepository;
import com.api.demo.grid.service.AuctionService;
import com.api.demo.grid.service.UserService;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.text.SimpleDateFormat;

import static com.api.demo.grid.utils.AuctionJson.addAuctionJson;
import static com.api.demo.grid.utils.BiddingJson.addBiddingJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuctionControllerIT {

    @Autowired
    private MockMvc mMvc;

    @Autowired
    private UserService mUserService;

    @Autowired
    private AuctionRepository mAuctionRepository;

    @Autowired
    private GameKeyRepository mGameKeyRepository;

    @Autowired
    private GameRepository mGameRepository;

    @Autowired
    private UserRepository mUserRepository;

    @Autowired
    private AuctionService mAuctionService;


    private Auction mAuction;
    private GameKey mGameKey;
    private Game mGame;
    private User mAuctioneer,
            mBuyer;
    private UserDTO mAuctioneerDTO,
            mBuyerDTO;

    private AuctionPOJO mAuctionPOJO;

    private double mPrice = 10.20;
    private String mEndDate = "10/11/2020";

    // auctioneer info
    private String mAuctioneerUsername = "username1",
            mAuctioneerName = "name1",
            mAuctioneerEmail = "email1",
            mAuctioneerCountry = "country1",
            mAuctioneerPassword = "password1",
            mAuctioneerBirthDateStr = "17/10/2010",
            mAuctioneerStartDateStr = "25/05/2020";

    // buyer info
    private String mBuyerUsername = "buyer1",
            mBuyerName = "name1",
            mBuyerEmail = "buyer_email1",
            mBuyerCountry = "country1",
            mBuyerPassword = "password1",
            mBuyerBirthDateStr = "17/10/2010",
            mBuyerStartDateStr = "25/05/2020";

    // game info
    private String mGameName = "game1",
            mGameKeyRKey = "game_key1";

    // auction json
    private String mAuctionJson,
            mBiddingJson;


    @BeforeEach
    @SneakyThrows
    void setup() {
        // create auctioneer
        mAuctioneer = new User();
        mAuctioneer.setUsername(mAuctioneerUsername);
        mAuctioneer.setName(mAuctioneerName);
        mAuctioneer.setEmail(mAuctioneerEmail);
        mAuctioneer.setPassword(mAuctioneerPassword);
        mAuctioneer.setCountry(mAuctioneerCountry);
        mAuctioneer.setBirthDate(new SimpleDateFormat("dd/MM/yyyy").parse(mAuctioneerBirthDateStr));
        mAuctioneer.setStartDate(new SimpleDateFormat("dd/MM/yyyy").parse(mAuctioneerStartDateStr));

        // create buyer
        mBuyer = new User();
        mBuyer.setUsername(mBuyerUsername);
        mBuyer.setName(mBuyerName);
        mBuyer.setEmail(mBuyerEmail);
        mBuyer.setPassword(mBuyerPassword);
        mBuyer.setCountry(mBuyerCountry);
        mBuyer.setBirthDate(new SimpleDateFormat("dd/MM/yyyy").parse(mBuyerBirthDateStr));
        mBuyer.setStartDate(new SimpleDateFormat("dd/MM/yyyy").parse(mBuyerStartDateStr));

        // create auctioneer dto
        mAuctioneerDTO = new UserDTO(mAuctioneerUsername, mAuctioneerName, mAuctioneerEmail, mAuctioneerCountry,
                mAuctioneerPassword, new SimpleDateFormat("dd/MM/yyyy").parse(mAuctioneerBirthDateStr));

        // create buyer dto
        mBuyerDTO = new UserDTO(mBuyerUsername, mBuyerName, mBuyerEmail, mBuyerCountry,
                mBuyerPassword, new SimpleDateFormat("dd/MM/yyyy").parse(mBuyerBirthDateStr));

        // create game
        mGame = new Game();
        mGame.setName(mGameName);

        // create game key
        mGameKey = new GameKey();
        mGameKey.setRealKey(mGameKeyRKey);

        // set auction pojo
        mAuctionPOJO = new AuctionPOJO(mAuctioneerUsername, mGameKeyRKey, mPrice,
                new SimpleDateFormat("dd/MM/yyyy").parse(mEndDate));

        // auction json
        mAuctionJson = addAuctionJson(mAuctioneerUsername, mGameKeyRKey, mEndDate, mPrice);
    }


    /***
     *  Add Auction
     ***/
    @Test
    @SneakyThrows
    void whenCreateCompleteFormAuction_creationIsSuccessful() {

        // save auctioneer, game and game key
        mUserService.saveUser(mAuctioneerDTO);
        mGameRepository.save(mGame);

        mGameKey.setGame(mGame);
        mGameKeyRepository.save(mGameKey);

        RequestBuilder request = post("/grid/auction").contentType(MediaType.APPLICATION_JSON)
                .content(mAuctionJson).with(httpBasic(mAuctioneerUsername, mAuctioneerPassword));

        mMvc.perform(request).andExpect(status().isOk())
                .andExpect(jsonPath("$.auctioneer", Matchers.is(mAuctioneerUsername)))
                .andExpect(jsonPath("$.buyer", Matchers.is(Matchers.nullValue())))
                .andExpect(jsonPath("$.gameKey", Matchers.is(mGameKeyRKey)))
                .andExpect(jsonPath("$.endDate", Matchers.is(mEndDate)))
                .andExpect(jsonPath("$.price", Matchers.is(mPrice)));
        assertEquals(1, mAuctionRepository.findAll().size());

        // verify if there is an auction on the auctioneer side
        assertEquals(1, mUserService.getUser(mAuctioneerUsername).getAuctionsCreated().size());

        // verify if there is an auction on the game key side
        assertNotNull(mGameKeyRepository.findByRealKey(mGameKeyRKey).get().getAuction());
    }

    @Test
    @SneakyThrows
    void whenCreateAuctionWithoutPrice_creationIsUnsuccessful() {

        // save auctioneer, game and game key
        mUserService.saveUser(mAuctioneerDTO);
        mGameRepository.save(mGame);

        mGameKey.setGame(mGame);
        mGameKeyRepository.save(mGameKey);

        mAuctionJson = addAuctionJson(mAuctioneerUsername, mGameKeyRKey, mEndDate);

        RequestBuilder request = post("/grid/auction").contentType(MediaType.APPLICATION_JSON)
                .content(mAuctionJson).with(httpBasic(mAuctioneerUsername, mAuctioneerPassword));

        mMvc.perform(request).andExpect(status().isBadRequest());
        assertEquals(0, mAuctionRepository.findAll().size());
    }

    @Test
    @SneakyThrows
    void whenCreateAuctionWithoutGameKey_creationIsUnsuccessful() {

        // save auctioneer, game and game key
        mUserService.saveUser(mAuctioneerDTO);
        mGameRepository.save(mGame);

        mGameKey.setGame(mGame);
        mGameKeyRepository.save(mGameKey);

        mAuctionJson = addAuctionJson(mAuctioneerUsername, null, mEndDate, mPrice);

        RequestBuilder request = post("/grid/auction").contentType(MediaType.APPLICATION_JSON)
                .content(mAuctionJson).with(httpBasic(mAuctioneerUsername, mAuctioneerPassword));

        mMvc.perform(request).andExpect(status().isBadRequest());
        assertEquals(0, mAuctionRepository.findAll().size());
    }

    @Test
    @SneakyThrows
    void whenCreateAuctionWithoutEndDate_creationIsUnsuccessful() {

        // save auctioneer, game and game key
        mUserService.saveUser(mAuctioneerDTO);
        mGameRepository.save(mGame);

        mGameKey.setGame(mGame);
        mGameKeyRepository.save(mGameKey);

        mAuctionJson = addAuctionJson(mAuctioneerUsername, mGameKeyRKey, null, mPrice);

        RequestBuilder request = post("/grid/auction").contentType(MediaType.APPLICATION_JSON)
                .content(mAuctionJson).with(httpBasic(mAuctioneerUsername, mAuctioneerPassword));

        mMvc.perform(request).andExpect(status().isBadRequest());
        assertEquals(0, mAuctionRepository.findAll().size());
    }

    @Test
    @SneakyThrows
    void whenCreateAuctionWithoutAuctioneer_creationIsUnsuccessful() {

        // save auctioneer, game and game key
        mUserService.saveUser(mAuctioneerDTO);
        mGameRepository.save(mGame);

        mGameKey.setGame(mGame);
        mGameKeyRepository.save(mGameKey);

        mAuctionJson = addAuctionJson(null, mGameKeyRKey, mEndDate, mPrice);

        RequestBuilder request = post("/grid/auction").contentType(MediaType.APPLICATION_JSON)
                .content(mAuctionJson).with(httpBasic(mAuctioneerUsername, mAuctioneerPassword));

        mMvc.perform(request).andExpect(status().is4xxClientError());
        assertEquals(0, mAuctionRepository.findAll().size());
    }

    @Test
    @SneakyThrows
    void whenCreateAuctionWithAuctioneerDifferentFromAuthenticatedUser_creationIsUnsuccessful() {

        // create fake auctioneer
        String fakeAuctioneerUsername = "fake_username";
        UserDTO fakeAuctioneer = new UserDTO(fakeAuctioneerUsername, mAuctioneerName, "fake_email", mAuctioneerCountry,
                mAuctioneerPassword, new SimpleDateFormat("dd/MM/yyyy").parse(mAuctioneerBirthDateStr));

        // save auctioneer, fake auctioneer, game and game key
        mUserService.saveUser(mAuctioneerDTO);
        mUserService.saveUser(fakeAuctioneer);
        mGameRepository.save(mGame);

        mGameKey.setGame(mGame);
        mGameKeyRepository.save(mGameKey);


        RequestBuilder request = post("/grid/auction").contentType(MediaType.APPLICATION_JSON)
                .content(mAuctionJson).with(httpBasic(fakeAuctioneerUsername, mAuctioneerPassword));

        mMvc.perform(request).andExpect(status().isForbidden());
        assertEquals(0, mAuctionRepository.findAll().size());
    }


    /***
     *  Add Bid
     ***/
    @Test
    @SneakyThrows
    void whenCreateCompleteBiddingAuction_creationIsSuccessful() {

        // save save auctioneer, game and game key
        User auctioneer = mUserService.saveUser(mAuctioneerDTO);
        mGameRepository.save(mGame);

        mGameKey.setGame(mGame);
        mGameKeyRepository.save(mGameKey);

        // create auction
        mAuction = new Auction();
        mAuction.setAuctioneer(auctioneer);
        mAuction.setGameKey(mGameKey);
        mAuction.setPrice(mPrice);
        mAuction.setEndDate(new SimpleDateFormat("dd/MM/yyyy").parse(mEndDate));

        // save auction and buyer dto
        mAuctionRepository.save(mAuction);
        mUserService.saveUser(mBuyerDTO);

        double newPrice = mPrice + 2.5;

        // bidding json
        mBiddingJson = addBiddingJson(mBuyerUsername, mGameKeyRKey, newPrice);

        RequestBuilder request = post("/grid/bidding").contentType(MediaType.APPLICATION_JSON)
                .content(mBiddingJson).with(httpBasic(mBuyerUsername, mBuyerPassword));

        mMvc.perform(request).andExpect(status().isOk())
                .andExpect(jsonPath("$.auctioneer", Matchers.is(mAuctioneerUsername)))
                .andExpect(jsonPath("$.buyer", Matchers.is(mBuyerUsername)))
                .andExpect(jsonPath("$.gameKey", Matchers.is(Matchers.nullValue())))
                .andExpect(jsonPath("$.endDate", Matchers.is(mEndDate)))
                .andExpect(jsonPath("$.price", Matchers.is(newPrice)));

        // verify if there is an auction on the buyer side
        assertEquals(1, mUserService.getUser(mBuyerUsername).getAuctionsWon().size());

        // verify if the buyer is saved om the auction
        assertEquals(mBuyerUsername, mAuctionRepository.findByGameKey_RealKey(mGameKeyRKey).getBuyer().getUsername());
    }

    @Test
    @SneakyThrows
    void whenCreateBiddingWithBuyerDifferentFromAuthenticatedUser_creationIsUnsuccessful() {

        // create fake buyer
        String fakeBuyerUsername = "fake_username";
        UserDTO fakeBuyerDTO = new UserDTO(fakeBuyerUsername, mBuyerName, "fake_email", mBuyerCountry,
                mBuyerPassword, new SimpleDateFormat("dd/MM/yyyy").parse(mBuyerBirthDateStr));

        // save save auctioneer, fake buyer and game and game key
        User auctioneer = mUserService.saveUser(mAuctioneerDTO);
        mUserService.saveUser(fakeBuyerDTO);
        mGameRepository.save(mGame);

        mGameKey.setGame(mGame);
        mGameKeyRepository.save(mGameKey);

        // create auction
        mAuction = new Auction();
        mAuction.setAuctioneer(auctioneer);
        mAuction.setGameKey(mGameKey);
        mAuction.setPrice(mPrice);
        mAuction.setEndDate(new SimpleDateFormat("dd/MM/yyyy").parse(mEndDate));

        // save auction and buyer dto
        mAuctionRepository.save(mAuction);
        mUserService.saveUser(mBuyerDTO);

        double newPrice = mPrice + 2.5;

        // bidding json
        mBiddingJson = addBiddingJson(mBuyerUsername, mGameKeyRKey, newPrice);

        RequestBuilder request = post("/grid/bidding").contentType(MediaType.APPLICATION_JSON)
                .content(mBiddingJson).with(httpBasic(fakeBuyerUsername, mBuyerPassword));

        mMvc.perform(request).andExpect(status().isForbidden());
    }


    /***
     *  Get all current auctions
     ***/
    @Test
    @SneakyThrows
    void whenGetAllCurrentAuctions_getIsSuccessful() {

        // save auctioneer, game and game key
        mUserRepository.save(mAuctioneer);
        mGameRepository.save(mGame);
        mGameKey.setGame(mGame);
        mGameKeyRepository.save(mGameKey);

        // insertion auction 1
        mAuctionService.addAuction(mAuctionPOJO);

        // save game key 2
        String gameKeyStr = "other_game_key";
        GameKey gameKey = new GameKey();
        gameKey.setRealKey(gameKeyStr);
        gameKey.setGame(mGame);
        mGameKeyRepository.save(gameKey);

        // insert auction 2
        AuctionPOJO auctionPOJO = new AuctionPOJO(mAuctioneerUsername, gameKeyStr, mPrice,
                new SimpleDateFormat("dd/MM/yyyy").parse(mEndDate));
        mAuctionService.addAuction(auctionPOJO);

        // save game key 3
        String gameKeyPastStr = "past_game_key";
        GameKey gameKeyPast = new GameKey();
        gameKeyPast.setRealKey(gameKeyPastStr);
        gameKeyPast.setGame(mGame);
        mGameKeyRepository.save(gameKeyPast);

        // insert auction 3
        AuctionPOJO auctionPastPOJO = new AuctionPOJO(mAuctioneerUsername, gameKeyPastStr, mPrice,
                new SimpleDateFormat("dd/MM/yyyy").parse("10/10/1999"));
        mAuctionService.addAuction(auctionPastPOJO);


        RequestBuilder request = get("/grid/auction").contentType(MediaType.APPLICATION_JSON)
                .param("gameId", String.valueOf(mGame.getId()));

        mMvc.perform(request).andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.[0].auctioneer", Matchers.is(mAuctioneerUsername)))
                .andExpect(jsonPath("$.[1].auctioneer", Matchers.is(mAuctioneerUsername)))
                .andExpect(jsonPath("$.[*].gameKey", Matchers.containsInAnyOrder(mGameKeyRKey, gameKeyStr)))
                .andExpect(jsonPath("$.[0].endDate", Matchers.is(mEndDate)))
                .andExpect(jsonPath("$.[1].endDate", Matchers.is(mEndDate)))
                .andExpect(jsonPath("$.[0].price", Matchers.is(mPrice)))
                .andExpect(jsonPath("$.[1].price", Matchers.is(mPrice)));
    }
}
