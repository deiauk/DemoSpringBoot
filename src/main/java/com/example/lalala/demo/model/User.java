package com.example.lalala.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
public class User implements Serializable, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String img;

    @NotBlank
    @Column(unique = true)
    private String email;

    @JsonIgnoreProperties("user")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Item> items;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)
    private List<UserItemsHistory> userItemsHistories;

    private int thumbsUpCount;
    private int thumbsDownCount;

    public User() {

    }

    public User(String email, String name, String img) {
        this.email = email;
        this.name = name;
        this.img = img;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public String getEmail() {
        return email;
    }

    public List<Item> getItems() {
        return items;
    }

    public int getThumbsUpCount() {
        return thumbsUpCount;
    }

    public int getThumbsDownCount() {
        return thumbsDownCount;
    }

    public void incThumbsUp() {
        this.thumbsUpCount++;
    }

    public void incThumbsDown() {
        this.thumbsDownCount++;
    }

    public List<Long> getAlreadySeenCards() {
        List<Long> ids = new ArrayList<>();
        if (userItemsHistories == null) return null;
        userItemsHistories.forEach(v -> ids.add(v.getItemId()));
        return ids;
    }

    public void addAlreadySeenCard(Long itemId) {
        if (userItemsHistories == null) userItemsHistories = new ArrayList<>();
        userItemsHistories.add(new UserItemsHistory(id, itemId));
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return null;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return email;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}