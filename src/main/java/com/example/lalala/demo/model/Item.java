package com.example.lalala.demo.model;

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
    private String description;

    @ElementCollection
    private List<String> images;

    @NotNull
    private Double lat;

    @NotNull
    private Double lng;

    @JsonIgnoreProperties("items")
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

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name="candidate",
            joinColumns={@JoinColumn(name="itemId")},
            inverseJoinColumns={@JoinColumn(name="candidateItemId")})
    private List<Item> candidateItems;

    public Item() {

    }

    public Item(String title, String description, Double lat, Double lng) {
        this.title = title;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
    }

    public Long getId() {
        return id;
    }

    public List<Item> getCandidateItems() {
        return candidateItems;
    }

    public void addCandidateItem(Item candidate) {
        if (candidateItems == null) candidateItems = new ArrayList<>();
        for (Item tmp : candidateItems) {
            if (tmp.equals(candidate)) {
                return;
            }
        }
        this.candidateItems.add(candidate);
    }

    public void removeCandidateItem(Item candidate) {
        if (candidateItems == null) candidateItems = new ArrayList<>();
        this.candidateItems.remove(candidate);
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
