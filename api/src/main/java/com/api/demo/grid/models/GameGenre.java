package com.api.demo.grid.models;


import javax.persistence.*;
import java.util.Set;


@Entity
public class GameGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique=true)
    private String name;

    private String description;

    @ManyToMany
    private Set<Game> games;

    public Set<Game> getGames() { return games; }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}