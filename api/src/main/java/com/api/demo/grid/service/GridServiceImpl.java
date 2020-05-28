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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Date;

@Service
public class GridServiceImpl implements GridService {

    @Autowired
    private DeveloperRepository mDeveloperRepository;

    @Autowired
    private GameGenreRepository mGameGenreRepository;

    @Autowired
    private PublisherRepository mPublisherRepository;

    @Autowired
    private GameRepository mGameRepository;

    @Autowired
    private UserRepository mUserRepository;

    @Autowired
    private BuyRepository mBuyRepository;

    private SearchGamePOJO mPreviousGamePojo;

    private List<Game> mPreviousSearch;

    @Override
    public Game getGameById(long id) {
        Optional<Game> gameResponse = mGameRepository.findById(id);

        if (gameResponse.isEmpty()) return null;

        return gameResponse.get();
    }

    @Override
    public Page<Game> getAllGames(int page) {
        Page<Game> games = mGameRepository.findAll(PageRequest.of(page, 18));
        return games;
    }

    @Override
    public List<Game> getAllGamesWithGenre(String genre) {
        Optional<GameGenre> gameGenre = mGameGenreRepository.findByName(genre);

        if (gameGenre.isEmpty()) return null;

        return mGameRepository.findAllByGameGenresContains(gameGenre.get());
    }

    @Override
    public List<Game> getAllGamesByName(String name) {
        return mGameRepository.findAllByNameContaining(name);
    }

    @Override
    public List<Game> getAllGamesByDev(String developer) {
        Optional<Developer> dev = mDeveloperRepository.findByName(developer);

        if (dev.isEmpty()) return null;

        return mGameRepository.findAllByDevelopersContaining(dev.get());
    }

    @Override
    public List<Game> getAllGamesByPublisher(String publisher) {
        Optional<Publisher> pub = mPublisherRepository.findByName(publisher);

        if (pub.isEmpty()) return null;

        return mGameRepository.findAllByPublisher(pub.get());
    }

    @Override
    public Game saveGame(GamePOJO gamePOJO) {
        Game game = new Game();
        game.setName(gamePOJO.getName());
        game.setCoverUrl(gamePOJO.getCoverUrl());
        game.setDescription(gamePOJO.getDescription());
        game.setReleaseDate(gamePOJO.getReleaseDate());


        //Get Game genres
        Optional<GameGenre> gameGenre;
        for (String gameGenrePOJO : gamePOJO.getGameGenres()) {
            gameGenre = mGameGenreRepository.findByName(gameGenrePOJO);
            if (gameGenre.isEmpty()) return null;
            game.addGenre(gameGenre.get());
        }

        // Get Publisher
        Optional<Publisher> publisher = mPublisherRepository.findByName(gamePOJO.getPublisher());
        if (publisher.isEmpty()) return null;
        game.setPublisher(publisher.get());

        //Get Game Developers
        Optional<Developer> developer;
        for (String developerPOJO : gamePOJO.getDevelopers()) {
            developer = mDeveloperRepository.findByName(developerPOJO);
            if (developer.isEmpty()) return null;
            game.addDeveloper(developer.get());
        }

        this.mGameRepository.save(game);
        return game;
    }

    @Override
    public Publisher savePublisher(PublisherPOJO publisherPOJO) {
        Publisher publisher = new Publisher();
        publisher.setName(publisherPOJO.getName());
        publisher.setDescription(publisherPOJO.getDescription());
        this.mPublisherRepository.save(publisher);
        return publisher;
    }

    @Override
    public Developer saveDeveloper(DeveloperPOJO developerPOJO) {
        Developer developer = new Developer();
        developer.setName(developerPOJO.getName());
        this.mDeveloperRepository.save(developer);
        return developer;
    }

    @Override
    public GameGenre saveGameGenre(GameGenrePOJO gameGenrePOJO) {
        GameGenre gameGenre = new GameGenre();
        gameGenre.setName(gameGenrePOJO.getName());
        this.mGameGenreRepository.save(gameGenre);
        return gameGenre;
    }

    @Override
    public List<Game> searchGames(SearchGamePOJO searchGamePOJO) {
        String queryName = searchGamePOJO.getName();
        List<Game> games;
        if (!queryName.isEmpty()) {
            games = this.mGameRepository.findAllByNameContaining(queryName);
        } else {
            games = this.mGameRepository.findAll();
        }

        String[] genres = searchGamePOJO.getGenres();
        Optional<GameGenre> genre;
        ArrayList<GameGenre> realGenres = new ArrayList<>();
        if (genres.length > 0){
            for (String gen: genres){
                genre = this.mGameGenreRepository.findByName(gen);
                if (genre.isEmpty()) continue;
                realGenres.add(genre.get());
            }
            games.removeIf(game -> !game.getGameGenres().containsAll(realGenres));
        }
        //Filter by platform
        String[] platforms = searchGamePOJO.getPlataforms();
        if (platforms.length > 0){
            games.removeIf(game -> !CollectionUtils.containsAny(game.getPlatforms(), Arrays.asList(platforms)));
        }

        double begin = searchGamePOJO.getStartPrice();
        double end = searchGamePOJO.getEndPrice();
        if (begin != 0 && end > begin){
            games.removeIf(game -> game.getBestSell().getPrice() <= begin || game.getBestSell().getPrice() >= end);
        } else if (begin != 0){
            games.removeIf(game -> game.getBestSell().getPrice() <= begin);
        }
        return games;
    }

    @Override
    public Page<Game> pageSearchGames(SearchGamePOJO searchGamePOJO){
        if (!searchGamePOJO.equals(mPreviousGamePojo)){
            mPreviousGamePojo = searchGamePOJO;
            mPreviousSearch = searchGames(searchGamePOJO);
        }
        int page = searchGamePOJO.getPage();
        Pageable pageable = PageRequest.of(page, 18);
        long start = pageable.getOffset();
        long end = (start + 18 > mPreviousSearch.size())? mPreviousSearch.size():start+18;
        return new PageImpl<>(mPreviousSearch.subList((int)start,(int) end),
                PageRequest.of(page, 18), mPreviousSearch.size());
    }

    public Set<Game> addWishListByUserID(long gameID, long userID) {
        Optional<User> user = this.mUserRepository.findById(userID);
        if (user.isEmpty()) return null;

        Optional<Game> game = this.mGameRepository.findById(gameID);
        if (game.isEmpty()) return null;

        User realUser = user.get();
        Game realGame = game.get();
        Set<Game> wishList = realUser.getWishList();
        wishList.add(realGame);
        Set<User> users = realGame.getUserWish();
        users.add(realUser);
        realUser.setWishList(wishList);
        realGame.setUserWish(users);
        this.mUserRepository.save(realUser);
        this.mGameRepository.save(realGame);
        return wishList;
    }

}
