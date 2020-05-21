package com.api.demo.grid.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Getter
@Setter
public class GameKey {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @JsonIgnore
    private Game game;

    @OneToOne
    @JsonIgnore
    private Sell sell;

    @OneToOne
    @JsonIgnore
    private Auction auction;

    private String retailer;

    private String platform;

    public void setGame(Game game) {
        //prevent endless loop
        if (sameAsFormer(game)) return ;
        //set new user
        this.game = game;

        //set myself into new owner
        if (game!=null) game.addGameKey(this);
    }

    private boolean sameAsFormer(Game newGame) {
        return Objects.equals(game, newGame);
    }

    public long getGameId(){
        if (game == null) return -1L;
        return this.game.getId();
    }
}
