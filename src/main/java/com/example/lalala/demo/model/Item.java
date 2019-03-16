package com.example.lalala.demo.model;

import com.example.lalala.demo.TradeType;
import com.fasterxml.jackson.annotation.*;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "item")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
public class Item implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    private List<String> images;

    @NotNull
    private Double lat;

    @NotNull
    private Double lng;

    //@JsonIgnoreProperties("items")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;

    private Double price = -1d;
    private int candidatesCount = 0;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "candidate",
            joinColumns = { @JoinColumn(name="itemId") },
            inverseJoinColumns = { @JoinColumn(name="candidateItemId") })
    private List<Item> candidateItems;

    private TradeType tradeType;

    public Item() {

    }

    public Long getId() {
        return id;
    }

    public Double getPrice() {
        return price;
    }

    public List<Item> getCandidateItems() {
        return candidateItems;
    }

    public int getCandidatesCount() {
        return candidatesCount;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
    }

    public void addCandidateItem(Item candidate) {
        if (candidateItems == null) candidateItems = new ArrayList<>();
        for (Item tmp : candidateItems) {
            if (tmp.equals(candidate)) {
                return;
            }
        }
        this.candidateItems.add(candidate);
        candidatesCount = this.candidateItems.size();
    }

    public List<Item> getUserItems(long userId) {
        if (userId == -1L) return null;

        if (candidateItems != null) {
            List<Item> userItems = new ArrayList<>();
            candidateItems.forEach((v) -> {
                if (v.getUser().getId() == userId) {
                    userItems.add(v);
                }
            });
            return userItems;
        }
        return null;
    }

    public void removeCandidateItem(Item candidate) {
        if (candidateItems == null) candidateItems = new ArrayList<>();
        this.candidateItems.remove(candidate);
        candidatesCount = this.candidateItems.size();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getImages() {
        return images;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public User getUser() {
        return user;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
