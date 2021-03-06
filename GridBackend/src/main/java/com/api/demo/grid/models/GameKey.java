package com.api.demo.grid.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@JsonSerialize
@SuppressFBWarnings
public class GameKey {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @EqualsAndHashCode.Exclude
    private long id;

    @Column(unique = true)
    @JsonIgnore
    private String realKey;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Game game;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Sell sell;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Auction auction;

    private String retailer;

    private String platform;


    @Transactional
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

    public void setSell(Sell sell){
        if (sameAsFormerSell(sell)) return ;
        this.sell = sell;
        if (sell!=null) sell.setGameKey(this);
    }

    private boolean sameAsFormerSell(Sell newSell) {
        return Objects.equals(sell, newSell);
    }

    @EqualsAndHashCode.Include
    public long getGameId(){
        if (game == null) return -1L;
        return this.game.getId();
    }

    public void setAuction(Auction auction) {
        if(Objects.equals(this.auction, auction)) return;

        this.auction = auction;

        if (auction != null) {
            auction.setGameKey(this);
        }
    }
    
    @EqualsAndHashCode.Include
    public String getGameName(){
        if (game == null) return "";
        return this.game.getName();
    }

    @EqualsAndHashCode.Include
    public String getGamePhoto(){
        if (game == null) return "";
        return this.game.getCoverUrl();
    }
}
