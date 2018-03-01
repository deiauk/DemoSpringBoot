package com.example.lalala.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String img;

    @NotBlank
    @Column(unique = true)
    private String email;



    @Fetch(FetchMode.JOIN)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Item> items;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(
            name = "candidate",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> itemsList;

    public User() {

    }

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public void addItem(Item item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        item.setUser(this);
        items.add(item);
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
}