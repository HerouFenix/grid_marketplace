package com.api.demo.grid.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@JsonSerialize
@SuppressFBWarnings
@Table(
        uniqueConstraints =
                {@UniqueConstraint(columnNames = {"review_from_user_id", "review_to_user_id"})}
)
public class ReviewUser {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private String comment;

    @Min(0)
    @Max(5)
    @Column(nullable = false)
    private int score;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_from_user_id", nullable = false)
    @JsonIgnore
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_to_user_id", nullable = false)
    @JsonIgnore
    private User target;


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getAuthorId(){
        if (this.author == null) return -1l;
        return this.author.getId();
    }

    public long getTargetId(){
        if (this.target == null) return -1l;
        return this.target.getId();
    }

    public String getAuthorUsername(){
        if (this.author == null) return "";
        return this.author.getUsername();
    }

    public String getTargetUsername(){
        if (this.target == null) return "";
        return this.target.getUsername();
    }

}
