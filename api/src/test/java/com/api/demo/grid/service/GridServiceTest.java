package com.api.demo.grid.service;

import com.api.demo.grid.exception.UnavailableListingException;
import com.api.demo.grid.exception.UnsufficientFundsException;
import com.api.demo.grid.exception.GameNotFoundException;

import com.api.demo.grid.models.Buy;
import com.api.demo.grid.models.Developer;
import com.api.demo.grid.models.Game;
import com.api.demo.grid.models.GameGenre;
import com.api.demo.grid.models.GameKey;
import com.api.demo.grid.models.Publisher;
import com.api.demo.grid.models.Sell;
import com.api.demo.grid.models.User;

import com.api.demo.grid.pojos.BuyListingsPOJO;
import com.api.demo.grid.pojos.DeveloperPOJO;
import com.api.demo.grid.pojos.GameGenrePOJO;
import com.api.demo.grid.pojos.GameKeyPOJO;
import com.api.demo.grid.pojos.GamePOJO;
import com.api.demo.grid.pojos.PublisherPOJO;
import com.api.demo.grid.pojos.SearchGamePOJO;
import com.api.demo.grid.pojos.SellPOJO;
import com.api.demo.grid.repository.BuyRepository;
import com.api.demo.grid.repository.DeveloperRepository;
import com.api.demo.grid.repository.GameGenreRepository;
import com.api.demo.grid.repository.GameKeyRepository;
import com.api.demo.grid.repository.GameRepository;
import com.api.demo.grid.repository.PublisherRepository;
import com.api.demo.grid.repository.SellRepository;
import com.api.demo.grid.repository.UserRepository;
import com.api.demo.grid.utils.Pagination;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Arrays;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GridServiceTest {
    @Mock(lenient = true)
    private GameRepository mMockGameRepo;

    @Mock(lenient = true)
    private GameGenreRepository mMockGameGenreRepo;

    @Mock(lenient = true)
    private DeveloperRepository mMockDevRepo;

    @Mock(lenient = true)
    private PublisherRepository mMockPubRepo;

    @Mock(lenient = true)
    private UserRepository mMockUserRepo;

    @Mock(lenient = true)
    private GameKeyRepository mMockGameKeyRepo;

    @Mock(lenient = true)
    private SellRepository mMockSellRepo;

    @Mock(lenient = true)
    private BuyRepository mMockBuyRepository;

    @InjectMocks
    private GridServiceImpl mGridService;

    private Game mGame;
    private Game mGame2;
    private GameGenre mGameGenre;
    private Developer mDeveloper;
    private Publisher mPublisher;
    private User mUser;
    private User mBuyer;
    private GameKey mGameKey;
    private GameKey mGameKey2;
    private Sell mSell1;
    private Sell mSell2;
    private SearchGamePOJO mSearchGamePOJO;

    @BeforeEach
    public void setUp(){
        mGame = new Game();
        mGame.setId(1L);
        mGame.setName("Game");

        mGameGenre = new GameGenre();
        mGameGenre.setId(2L);
        mGameGenre.setName("Genre");

        mDeveloper = new Developer();
        mDeveloper.setId(3L);
        mDeveloper.setName("Dev");

        mPublisher = new Publisher();
        mPublisher.setId(4L);
        mPublisher.setName("Pub");

        mGame2 = new Game();
        mGame2.setId(5L);
        mGame2.setName("Game 2");

        mGame.setGameGenres(new HashSet<>(Arrays.asList(mGameGenre)));
        mGame.setDevelopers(new HashSet<>(Arrays.asList(mDeveloper)));
        mGame.setPublisher(mPublisher);

        mUser = new User();
        mUser.setId(6L);

        mBuyer = new User();
        mBuyer.setId(11l);

        mGameKey = new GameKey();
        mGameKey.setRealKey("key1");
        mGameKey.setId(7L);

        mSell1 = new Sell();
        mSell1.setGameKey(mGameKey);
        mSell1.setUser(mUser);
        mSell1.setId(8l);
        mSell1.setPrice(5.3);

        mSell2 = new Sell();
        mSell2.setId(9l);
        mSell2.setUser(mUser);

        mGameKey2 = new GameKey();
        mGameKey2.setRealKey("key2");
        mGameKey2.setId(10l);

        mSell2.setGameKey(mGameKey2);
        mSell2.setPrice(50.3);

        mSearchGamePOJO = new SearchGamePOJO();
    }

    @Test
    void whenSearchingAll_ReturnAllGame() {
        List<Game> gamesList = Arrays.asList(mGame, mGame2);
        Pagination<Game> pagination = new Pagination<>(gamesList);
        int page = 1;
        int entriesPerPage = 18;
        PageRequest pageRequest = PageRequest.of(page, entriesPerPage);

        Page<Game> games = pagination.pageImpl(page, entriesPerPage);
        Mockito.when(mMockGameRepo.findAll(pageRequest)).thenReturn(games);

        assertEquals(games, mGridService.getAllGames(page));
        Mockito.verify(mMockGameRepo, Mockito.times(1)).findAll(pageRequest);
    }

    @Test
    void whenSearchingId_ReturnRightGame() {
        Mockito.when(mMockGameRepo.findById(1L)).thenReturn(java.util.Optional.ofNullable(mGame));

        assertEquals(mGame, mGridService.getGameById(1L));
        Mockito.verify(mMockGameRepo, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    void whenSearchingInvalidId_ReturnNull(){
        Mockito.when(mMockGameRepo.findById(2L)).thenReturn(Optional.empty());

        assertNull(mGridService.getGameById(2L));
        Mockito.verify(mMockGameRepo, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    void whenSearchingName_ReturnRightGame(){
        List<Game> games = Arrays.asList(mGame);
        Mockito.when(mMockGameRepo.findAllByNameContaining("Game")).thenReturn(games);

        assertEquals(games, mGridService.getAllGamesByName("Game"));
        Mockito.verify(mMockGameRepo, Mockito.times(1)).findAllByNameContaining(Mockito.anyString());
    }

    @Test
    void whenSearchingInvalidName_ReturnNull(){
        Mockito.when(mMockGameRepo.findAllByNameContaining("Game2")).thenReturn(new ArrayList<Game>());

        assertEquals(new ArrayList<Game>(), mGridService.getAllGamesByName("Game2"));
        Mockito.verify(mMockGameRepo, Mockito.times(1)).findAllByNameContaining(Mockito.anyString());
    }

    @Test
    void whenSearchingGameGenre_ReturnValidList(){
        List<Game> games = Arrays.asList(mGame);
        Mockito.when(mMockGameGenreRepo.findByName("Genre")).thenReturn(Optional.ofNullable(mGameGenre));
        Mockito.when(mMockGameRepo.findAllByGameGenresContains(mGameGenre)).thenReturn(games);

        assertEquals(games, mGridService.getAllGamesWithGenre("Genre"));
        Mockito.verify(mMockGameGenreRepo, Mockito.times(1)).findByName(Mockito.anyString());
        Mockito.verify(mMockGameRepo, Mockito.times(1))
                .findAllByGameGenresContains(Mockito.any(GameGenre.class));
    }

    @Test
    void whenSearchingInvalidGenre_ReturnEmptyList(){
        Mockito.when(mMockGameGenreRepo.findByName("Genre2")).thenReturn(Optional.empty());

        assertNull(mGridService.getAllGamesWithGenre("Genre2"));
        Mockito.verify(mMockGameGenreRepo, Mockito.times(1)).findByName(Mockito.anyString());
        Mockito.verify(mMockGameRepo, Mockito.times(0))
                .findAllByGameGenresContains(Mockito.any(GameGenre.class));

    }

    @Test
    void whenSearchingValidDev_ReturnValidList(){
        List<Game> games = Arrays.asList(mGame);
        Mockito.when(mMockDevRepo.findByName("Dev")).thenReturn(Optional.ofNullable(mDeveloper));
        Mockito.when(mMockGameRepo.findAllByDevelopersContaining(mDeveloper)).thenReturn(games);

        assertEquals(games, mGridService.getAllGamesByDev("Dev"));
        Mockito.verify(mMockDevRepo, Mockito.times(1)).findByName(Mockito.anyString());
        Mockito.verify(mMockGameRepo, Mockito.times(1))
                .findAllByDevelopersContaining(Mockito.any(Developer.class));
    }

    @Test
    void whenSearchingInvalidDev_ReturnEmptyList() {
        Mockito.when(mMockGameGenreRepo.findByName("Dev2")).thenReturn(Optional.empty());

        assertNull(mGridService.getAllGamesByDev("Dev2"));
        Mockito.verify(mMockDevRepo, Mockito.times(1)).findByName(Mockito.anyString());
        Mockito.verify(mMockGameRepo, Mockito.times(0))
                .findAllByDevelopersContaining(Mockito.any(Developer.class));

    }

    @Test
    void whenSearchingValidPublisher_ReturnValidList() {
        List<Game> games = Arrays.asList(mGame);
        Mockito.when(mMockPubRepo.findByName("Pub")).thenReturn(Optional.ofNullable(mPublisher));
        Mockito.when(mMockGameRepo.findAllByPublisher(mPublisher)).thenReturn(games);

        assertEquals(games, mGridService.getAllGamesByPublisher("Pub"));
        Mockito.verify(mMockPubRepo, Mockito.times(1)).findByName(Mockito.anyString());
        Mockito.verify(mMockGameRepo, Mockito.times(1))
                .findAllByPublisher(Mockito.any(Publisher.class));
    }

    @Test
    void whenSearchingInvalidPublisher_ReturnEmptyList() {
        Mockito.when(mMockGameGenreRepo.findByName("Pub2")).thenReturn(Optional.empty());

        assertNull(mGridService.getAllGamesByPublisher("Pub2"));
        Mockito.verify(mMockPubRepo, Mockito.times(1)).findByName(Mockito.anyString());
        Mockito.verify(mMockGameRepo, Mockito.times(0))
                .findAllByPublisher(Mockito.any(Publisher.class));
    }

    @Test
    void whenSavingGameGenrePojo_ReturnValidGameGenre() {
        GameGenrePOJO gameGenrePOJO = new GameGenrePOJO("Genre", null);

        GameGenre savedGameGenre = mGridService.saveGameGenre(gameGenrePOJO);

        assertEquals(mGameGenre.getName(), mGameGenre.getName());
        assertEquals(mGameGenre.getDescription(), savedGameGenre.getDescription());
    }


    @Test
    void whenSavingDevPOJO_ReturnValidDeveloper() {
        DeveloperPOJO developerPOJO = new DeveloperPOJO("Dev");

        Developer savedDev = mGridService.saveDeveloper(developerPOJO);

        assertEquals(mDeveloper.getName(), savedDev.getName());
    }

    @Test
    void whenSavingPubPOJO_ReturnValidPublisher() {
        PublisherPOJO publisherPOJO = new PublisherPOJO("Pub", null);

        Publisher savedPub = mGridService.savePublisher(publisherPOJO);

        assertEquals(mPublisher.getName(), savedPub.getName());
        assertEquals(mPublisher.getDescription(), mPublisher.getDescription());
    }

    @Test
    void whenSavingGamePOJO_ReturnValidGame(){
        Mockito.when(mMockGameGenreRepo.findByName("Genre")).thenReturn(Optional.ofNullable(mGameGenre));
        Mockito.when(mMockDevRepo.findByName("Dev")).thenReturn(Optional.ofNullable(mDeveloper));
        Mockito.when(mMockPubRepo.findByName("Pub")).thenReturn(Optional.ofNullable(mPublisher));

        Set<String> gameGenrePOJOSet = new HashSet<>(Arrays.asList("Genre"));

        Set<String> developerPOJOSet = new HashSet<>(Arrays.asList("Dev"));

        GamePOJO gamePOJO = new GamePOJO("Game", null, gameGenrePOJOSet, "Pub", developerPOJOSet, null, null);

        Game savedGame = mGridService.saveGame(gamePOJO);
        Mockito.verify(mMockPubRepo, Mockito.times(1)).findByName("Pub");
        Mockito.verify(mMockDevRepo, Mockito.times(1)).findByName("Dev");
        Mockito.verify(mMockGameGenreRepo, Mockito.times(1)).findByName("Genre");
        assertEquals(mGame.getName(), savedGame.getName());
        assertEquals(mGame.getDescription(), savedGame.getDescription());
        assertEquals(mGame.getPublisher(), savedGame.getPublisher());
        assertEquals(mGame.getGameGenres(), savedGame.getGameGenres());
    }

    @Test
    void whenSavingGamePOJOWithInvalidGenre_ReturnNull() {
        Mockito.when(mMockGameGenreRepo.findByName("Genre")).thenReturn(Optional.empty());
        Mockito.when(mMockDevRepo.findByName("Dev")).thenReturn(Optional.ofNullable(mDeveloper));
        Mockito.when(mMockPubRepo.findByName("Pub")).thenReturn(Optional.ofNullable(mPublisher));

        Set<String> gameGenrePOJOSet = new HashSet<>(Arrays.asList("Genre"));

        Set<String> developerPOJOSet = new HashSet<>(Arrays.asList("Dev"));

        GamePOJO gamePOJO = new GamePOJO("Game", null, gameGenrePOJOSet, "Pub", developerPOJOSet, null, null);

        Game savedGame = mGridService.saveGame(gamePOJO);
        Mockito.verify(mMockPubRepo, Mockito.times(0)).findByName("Pub");
        Mockito.verify(mMockDevRepo, Mockito.times(0)).findByName("Dev");
        Mockito.verify(mMockGameGenreRepo, Mockito.times(1)).findByName("Genre");
        assertNull(savedGame);
    }

    @Test
    void whenSavingGamePOJOWithInvalidPub_ReturnNull() {
        Mockito.when(mMockGameGenreRepo.findByName("Genre")).thenReturn(Optional.ofNullable(mGameGenre));
        Mockito.when(mMockDevRepo.findByName("Dev")).thenReturn(Optional.ofNullable(mDeveloper));
        Mockito.when(mMockPubRepo.findByName("Pub")).thenReturn(Optional.empty());

        Set<String> gameGenrePOJOSet = new HashSet<>(Arrays.asList("Genre"));

        Set<String> developerPOJOSet = new HashSet<>(Arrays.asList("Dev"));

        GamePOJO gamePOJO = new GamePOJO("Game", null, gameGenrePOJOSet, "Pub", developerPOJOSet, null, null);

        Game savedGame = mGridService.saveGame(gamePOJO);
        Mockito.verify(mMockPubRepo, Mockito.times(1)).findByName("Pub");
        Mockito.verify(mMockDevRepo, Mockito.times(0)).findByName("Dev");
        Mockito.verify(mMockGameGenreRepo, Mockito.times(1)).findByName("Genre");
        assertNull(savedGame);
    }

    @Test
    void whenSavingGamePOJOWithInvalidDev_ReturnNull(){
        Mockito.when(mMockGameGenreRepo.findByName("Genre")).thenReturn(Optional.ofNullable(mGameGenre));
        Mockito.when(mMockDevRepo.findByName("Dev")).thenReturn(Optional.empty());
        Mockito.when(mMockPubRepo.findByName("Pub")).thenReturn(Optional.ofNullable(mPublisher));

        Set<String> gameGenrePOJOSet = new HashSet<>(Arrays.asList("Genre"));

        Set<String> developerPOJOSet = new HashSet<>(Arrays.asList("Dev"));

        GamePOJO gamePOJO = new GamePOJO("Game", null, gameGenrePOJOSet, "Pub", developerPOJOSet, null, null);

        Game savedGame = mGridService.saveGame(gamePOJO);
        Mockito.verify(mMockPubRepo, Mockito.times(1)).findByName("Pub");
        Mockito.verify(mMockDevRepo, Mockito.times(1)).findByName("Dev");
        Mockito.verify(mMockGameGenreRepo, Mockito.times(1)).findByName("Genre");
        assertNull(savedGame);
    }
    
    @Test
    void whenSavingValidGameKeyPOJO_ReturnValidGameKey() {
        Mockito.when(mMockGameRepo.findById(2L)).thenReturn(Optional.ofNullable(mGame2));

        GameKeyPOJO gameKeyPOJO = new GameKeyPOJO("key", 2L, "steam", "ps3");

        GameKey savedGameKey = mGridService.saveGameKey(gameKeyPOJO);

        Mockito.verify(mMockGameRepo, Mockito.times(1)).findById(2L);
        assertEquals("key", savedGameKey.getRealKey());
        assertEquals("ps3", savedGameKey.getPlatform());
        assertEquals("steam", savedGameKey.getRetailer());
        assertEquals(mGame2.getName(), savedGameKey.getGame().getName());
    }

    @Test
    void whenSavingInvalidGameKeyPOJO_ReturnNullGameKey(){
        Mockito.when(mMockGameRepo.findById(2L)).thenReturn(Optional.empty());

        GameKeyPOJO gameKeyPOJO = new GameKeyPOJO("key", 2L, "steam", "ps3");

        GameKey savedGameKey = mGridService.saveGameKey(gameKeyPOJO);

        Mockito.verify(mMockGameRepo, Mockito.times(1)).findById(2L);
        assertNull(savedGameKey);
    }

    @Test
    void whenSavingValidSellPOJO_ReturnValidSell() {
        Mockito.when(mMockUserRepo.findById(6L)).thenReturn(Optional.ofNullable(mUser));
        Mockito.when(mMockGameKeyRepo.findByRealKey("key")).thenReturn(Optional.ofNullable(mGameKey));

        SellPOJO sellPOJO = new SellPOJO("key", 6L, 2.3, null);

        Sell savedSell = mGridService.saveSell(sellPOJO);
        Mockito.verify(mMockUserRepo, Mockito.times(1)).findById(6L);
        Mockito.verify(mMockGameKeyRepo, Mockito.times(1)).findByRealKey("key");
        assertEquals(2.3, savedSell.getPrice());
    }

    @Test
    void whenSavingInvalidUser_ReturnNullSell() {
        Mockito.when(mMockUserRepo.findById(6L)).thenReturn(Optional.empty());
        Mockito.when(mMockGameKeyRepo.findByRealKey("key")).thenReturn(Optional.ofNullable(mGameKey));

        SellPOJO sellPOJO = new SellPOJO("key", 6L, 2.3, null);

        Sell savedSell = mGridService.saveSell(sellPOJO);
        Mockito.verify(mMockUserRepo, Mockito.times(1)).findById(6L);
        Mockito.verify(mMockGameKeyRepo, Mockito.times(0)).findByRealKey("key");
        assertNull(savedSell);
    }

    @Test
    void whenSavingInvalidGameKey_ReturnNullSell() {
        Mockito.when(mMockUserRepo.findById(6L)).thenReturn(Optional.ofNullable(mUser));
        Mockito.when(mMockGameKeyRepo.findByRealKey("key")).thenReturn(Optional.empty());

        SellPOJO sellPOJO = new SellPOJO("key", 6L, 2.3, null);

        Sell savedSell = mGridService.saveSell(sellPOJO);
        Mockito.verify(mMockUserRepo, Mockito.times(1)).findById(6L);
        Mockito.verify(mMockGameKeyRepo, Mockito.times(1)).findByRealKey("key");
        assertNull(savedSell);
    }

    @Test
    void whenBuyingValidListings_AndUsingCreditCard_ReturnBuyArray(){
        Mockito.when(mMockSellRepo.findById(8l)).thenReturn(Optional.ofNullable(mSell1));
        Mockito.when(mMockSellRepo.findById(9l)).thenReturn(Optional.ofNullable(mSell2));
        Mockito.when(mMockUserRepo.findById(11l)).thenReturn(Optional.ofNullable(mBuyer));

        double previousFunds = mBuyer.getFunds();
        long[] longs = {8l, 9l};
        BuyListingsPOJO buyListingsPOJO = new BuyListingsPOJO(11l, longs, false);

        try {
            mGridService.saveBuy(buyListingsPOJO);
        } catch (UnavailableListingException | UnsufficientFundsException e) {
            fail();
        }
        Mockito.verify(mMockUserRepo, Mockito.times(1)).findById(11l);
        Mockito.verify(mMockSellRepo, Mockito.times(2)).findById(Mockito.anyLong());
        assertEquals(previousFunds, mBuyer.getFunds());
        assertEquals(2, mBuyer.getBuys().size());
    }

    @Test
    void whenBuyingValidListings_AndUsingFunds_WithEnoughFunds_ReturnBuyArray(){
        Mockito.when(mMockSellRepo.findById(8l)).thenReturn(Optional.ofNullable(mSell1));
        Mockito.when(mMockSellRepo.findById(9l)).thenReturn(Optional.ofNullable(mSell2));
        Mockito.when(mMockUserRepo.findById(11l)).thenReturn(Optional.ofNullable(mBuyer));

        mBuyer.setFunds(100);

        long[] longs = {8l, 9l};
        BuyListingsPOJO buyListingsPOJO = new BuyListingsPOJO(11l, longs, true);

        try {
            mGridService.saveBuy(buyListingsPOJO);
        } catch (UnavailableListingException | UnsufficientFundsException e) {
            fail();
        }

        Mockito.verify(mMockUserRepo, Mockito.times(1)).findById(11l);
        Mockito.verify(mMockSellRepo, Mockito.times(2)).findById(Mockito.anyLong());
        assertEquals(100 - mSell1.getPrice() - mSell2.getPrice(), mBuyer.getFunds());
        assertEquals(2, mBuyer.getBuys().size());
    }

    @Test
    void whenBuyingValidListings_AndUsingFunds_WithoutEnoughFunds_ThrowException(){
        Mockito.when(mMockSellRepo.findById(8l)).thenReturn(Optional.ofNullable(mSell1));
        Mockito.when(mMockSellRepo.findById(9l)).thenReturn(Optional.ofNullable(mSell2));
        Mockito.when(mMockUserRepo.findById(11l)).thenReturn(Optional.ofNullable(mBuyer));
        mBuyer.setFunds(-1);

        long[] longs = {8l, 9l};
        BuyListingsPOJO buyListingsPOJO = new BuyListingsPOJO(11l, longs, true);

        assertThrows(UnsufficientFundsException.class, () ->{ mGridService.saveBuy(buyListingsPOJO);});
    }

    @Test
    void whenBuyingBoughtListings_AndUsingCreditCard_ThrowException(){
        Mockito.when(mMockSellRepo.findById(8l)).thenReturn(Optional.ofNullable(mSell1));
        Mockito.when(mMockSellRepo.findById(9l)).thenReturn(Optional.ofNullable(mSell2));
        Mockito.when(mMockUserRepo.findById(11l)).thenReturn(Optional.ofNullable(mBuyer));
        mSell1.setPurchased(new Buy());

        long[] longs = {8l, 9l};
        BuyListingsPOJO buyListingsPOJO = new BuyListingsPOJO(11l, longs, false);

        assertThrows(UnavailableListingException.class, () -> {
            mGridService.saveBuy(buyListingsPOJO);
        });

    }

    @Test
    void whenBuyingBoughtListings_AndUsingFunds_ThrowException(){
        Mockito.when(mMockSellRepo.findById(8l)).thenReturn(Optional.ofNullable(mSell1));
        Mockito.when(mMockSellRepo.findById(9l)).thenReturn(Optional.ofNullable(mSell2));
        Mockito.when(mMockUserRepo.findById(11l)).thenReturn(Optional.ofNullable(mBuyer));
        mSell1.setPurchased(new Buy());

        long[] longs = {8l, 9l};
        BuyListingsPOJO buyListingsPOJO = new BuyListingsPOJO(11l, longs, true);

        assertThrows(UnavailableListingException.class, () -> {
            mGridService.saveBuy(buyListingsPOJO);
        });
    }

    @Test
    void whenBuyingInvalidListings_AndUsingCreditCard_ThrowException(){
        Mockito.when(mMockSellRepo.findById(8l)).thenReturn(Optional.empty());
        Mockito.when(mMockSellRepo.findById(9l)).thenReturn(Optional.ofNullable(mSell2));
        Mockito.when(mMockUserRepo.findById(11l)).thenReturn(Optional.ofNullable(mBuyer));
        mSell1.setPurchased(new Buy());

        long[] longs = {8l, 9l};
        BuyListingsPOJO buyListingsPOJO = new BuyListingsPOJO(11l, longs, false);

        assertThrows(UnavailableListingException.class, () -> {
            mGridService.saveBuy(buyListingsPOJO);
        });

    }

    @Test
    void whenBuyingInvalidListings_AndUsingFunds_ThrowException() {
        Mockito.when(mMockSellRepo.findById(8l)).thenReturn(Optional.ofNullable(mSell1));
        Mockito.when(mMockSellRepo.findById(9l)).thenReturn(Optional.empty());
        Mockito.when(mMockUserRepo.findById(11l)).thenReturn(Optional.ofNullable(mBuyer));
        mSell1.setPurchased(new Buy());

        long[] longs = {8l, 9l};
        BuyListingsPOJO buyListingsPOJO = new BuyListingsPOJO(11l, longs, true);

        assertThrows(UnavailableListingException.class, () -> {
            mGridService.saveBuy(buyListingsPOJO);
        });
    }

    @Test
    void whenReceivingSearchGame_withValidName_returnValidList(){
        Mockito.when(mMockGameRepo.findAllByNameContaining(Mockito.anyString()))
                .thenReturn(new ArrayList<>(Arrays.asList(mGame, mGame2)));
        mSearchGamePOJO.setName("g");

        List<Game> games = mGridService.searchGames(mSearchGamePOJO);
        assertEquals(2, games.size());
        assertEquals(mGame, games.get(0));
        assertEquals(mGame2, games.get(1));
    }

    @Test
    void whenReceivingSearchGame_withValidGenre_returnValidList(){
        Mockito.when(mMockGameGenreRepo.findByName(Mockito.anyString())).thenReturn(Optional.of(mGameGenre));
        Mockito.when(mMockGameRepo.findAllByGameGenresContains(Mockito.any(GameGenre.class)))
                .thenReturn(new ArrayList<>(Arrays.asList(mGame)));
        Mockito.when(mMockGameRepo.findAll()).thenReturn(new ArrayList<>(Arrays.asList(mGame, mGame2)));
        String[] genres = {"adventure"};
        mSearchGamePOJO.setGenres(genres);

        List<Game> games = mGridService.searchGames(mSearchGamePOJO);
        assertEquals(1, games.size());
        assertEquals(mGame, games.get(0));
    }

    @Test
    void whenReceivingSearchGame_withValidPlatforms_returnValidList(){
        Mockito.when(mMockGameRepo.findAll()).thenReturn(new ArrayList<>(Arrays.asList(mGame, mGame2)));
        mGameKey.setPlatform("ps4");
        mGame2.addGameKey(mGameKey);
        String[] platforms = {"ps4"};
        mSearchGamePOJO.setPlataforms(platforms);

        List<Game> games = mGridService.searchGames(mSearchGamePOJO);
        assertEquals(1, games.size());
        assertEquals(mGame2, games.get(0));
    }

    @Test
    void whenReceivingSearchGame_withValidBeginPrice_andNullEndPrice_returnListWithStartingPrice(){
        Mockito.when(mMockGameRepo.findAll()).thenReturn(new ArrayList<>(Arrays.asList(mGame, mGame2)));
        mGame.addGameKey(mGameKey);
        mGame2.addGameKey(mGameKey2);
        mSearchGamePOJO.setStartPrice(5);

        List<Game> games = mGridService.searchGames(mSearchGamePOJO);
        assertEquals(Arrays.asList(mGame, mGame2), games);
    }

    @Test
    void whenReceivingSearchGame_withValidBeginPriceAndEndPrice_returnListWithStartingPrice(){
        Mockito.when(mMockGameRepo.findAll()).thenReturn(new ArrayList<>(Arrays.asList(mGame, mGame2)));
        mGame.addGameKey(mGameKey);
        mGame2.addGameKey(mGameKey2);
        mSearchGamePOJO.setStartPrice(5);
        mSearchGamePOJO.setEndPrice(10);

        List<Game> games = mGridService.searchGames(mSearchGamePOJO);
        assertEquals(Arrays.asList(mGame), games);
    }

    @Test
    void whenReceivingSeargGame_withMoreParameters_returnIntersectionOfList(){
        Mockito.when(mMockGameRepo.findAllByNameContaining(Mockito.anyString()))
                .thenReturn(new ArrayList<>(Arrays.asList(mGame, mGame2)));
        mSearchGamePOJO.setName("g");
        Mockito.when(mMockGameGenreRepo.findByName(Mockito.anyString())).thenReturn(Optional.of(mGameGenre));
        Mockito.when(mMockGameRepo.findAllByGameGenresContains(Mockito.any(GameGenre.class)))
                .thenReturn(new ArrayList<>(Arrays.asList(mGame)));
        String[] genres = {"adventure"};
        mSearchGamePOJO.setGenres(genres);
        mGameKey.setPlatform("ps4");
        mGame2.addGameKey(mGameKey2);
        String[] platforms = {"ps4"};
        mSearchGamePOJO.setPlataforms(platforms);
        mSearchGamePOJO.setStartPrice(5);
        mSearchGamePOJO.setEndPrice(10);
        assertTrue(mGridService.searchGames(mSearchGamePOJO).isEmpty());
    }

    @Test
    void whenSearchingGame_ReturnPaginatedSearchGame() {
        Mockito.when(mMockGameRepo.findAll()).thenReturn(new ArrayList<>(Arrays.asList(mGame, mGame2)));

        List<Game> gamesList = Arrays.asList(mGame, mGame2);

        assertEquals(gamesList, mGridService.pageSearchGames(mSearchGamePOJO).getContent());
    }

    @Test
    void whenPostingValidWishList_ReturnWishList() {
        long userID = 1L;
        long gameID = 1L;


        Set<Game> expected = new HashSet<>();
        expected.add(mGame);
        Set<User> users = new HashSet<>();
        users.add(mUser);
        mUser.setWishList(expected);
        mGame.setUserWish(users);

        Mockito.when(mMockUserRepo.findById(userID)).thenReturn(Optional.ofNullable(mUser));
        Mockito.when(mMockGameRepo.findById(gameID)).thenReturn(Optional.ofNullable(mGame));

        Set<Game> games = mGridService.addWishListByUserID(gameID, userID);

        Mockito.verify(mMockUserRepo, Mockito.times(1)).findById(userID);
        Mockito.verify(mMockGameRepo, Mockito.times(1)).findById(gameID);

        assertEquals(expected, games);
    }

    @Test
    @SneakyThrows
    void whenSearchingSell_ByGame_ReturnPage() {
        List<Sell> gamesList = Arrays.asList(mSell1, mSell2);
        Pagination<Sell> pagination = new Pagination<>(gamesList);
        int page = 1;
        int entriesPerPage = 6;
        PageRequest pageRequest = PageRequest.of(page, entriesPerPage);

        Page<Sell> sells = pagination.pageImpl(page, entriesPerPage);
        Mockito.when(mMockGameRepo.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(mGame));
        Mockito.when(mMockSellRepo.findAllByGames(1l, pageRequest)).thenReturn(sells);

        assertEquals(sells, mGridService.getAllSellListings(1l, page));
        Mockito.verify(mMockSellRepo, Mockito.times(1)).findAllByGames(1l, pageRequest);
    }

    @Test
    @SneakyThrows
    void whenSearchingSell_andGameIsInvalid_ThrowsException(){
        List<Sell> gamesList = Arrays.asList(mSell1, mSell2);
        Pagination<Sell> pagination = new Pagination<>(gamesList);
        int page = 1;
        int entriesPerPage = 6;
        PageRequest pageRequest = PageRequest.of(page, entriesPerPage);

        Page<Sell> sells = pagination.pageImpl(page, entriesPerPage);
        Mockito.when(mMockGameRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(mMockSellRepo.findAllByGames(2l, pageRequest)).thenReturn(sells);

        assertThrows(GameNotFoundException.class, () -> mGridService.getAllSellListings(2L, 1));
    }
}