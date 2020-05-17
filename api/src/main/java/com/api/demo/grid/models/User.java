package com.api.demo.grid.models;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;
import java.util.Set;


@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Date birth_date;

    private String photoUrl;

    //The games he reviewed
    @OneToMany(cascade = CascadeType.ALL)
    private Set<ReviewGame> reviewGames;

    //The users he reviewed
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "review_from_user_id")
    private Set<ReviewUser> reviewUsers;

    //The users that reviewd him
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "review_to_user_id")
    private Set<ReviewUser> reviewedUsers;

    //The reviews directed to the users
    @OneToMany(cascade = CascadeType.ALL)
    private Set<ReviewUser> reviews;

    //The reports this user has issued on game reviews
    @OneToMany(cascade = CascadeType.ALL)
    private Set<ReportReviewGame> reportsOnGameReview;

    //The reports this user has issued on user reviews
    @OneToMany(cascade = CascadeType.ALL)
    private Set<ReportReviewUser> reportsOnUserReview;

    //The reports this user has received
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "report_from_user_id")
    private Set<ReportUser> reportsThisUser;

    //The reports this user has issued on users
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "report_to_user_id")
    private Set<ReportUser> reportsOnUser;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Buy> buys;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Auction> auctions;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Sell> sells;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "game_id")
    private Set<Game> wishList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private UserRole role;


    public User() {

    }

    public User(String name, String email, String password, String username, Date birth_data, UserRole role) {

        this.name = name;
        this.email = email;
        this.password = password;
        this.username = username;
        this.birth_date = birth_data;
        this.role = role;
    }
}
