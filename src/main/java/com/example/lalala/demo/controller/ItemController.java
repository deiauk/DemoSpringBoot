package com.example.lalala.demo.controller;

import com.example.lalala.demo.model.Candidate;
import com.example.lalala.demo.model.Item;
import com.example.lalala.demo.model.User;
import com.example.lalala.demo.respository.CandidateRepository;
import com.example.lalala.demo.respository.ItemRepository;
import com.example.lalala.demo.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ItemController {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CandidateRepository candidateRepository;

    @GetMapping("/item")
    public List<Item> getAllItems() {

        return itemRepository.findAll();
    }

    @GetMapping("/nearestItems/{lat}/{lng}/{beginAt}")
    public List<Item> GetNearest(@PathVariable(value = "lat") Double lat,
                                 @PathVariable(value = "lng") Double lng,
                                 @PathVariable(value = "beginAt") Integer beginAt) {

        return itemRepository.findNearest(lat, lng, beginAt);
    }

    @PostMapping("/item")
    public Item createNote(@Valid @RequestBody Item item) {
        User user = userRepository.findByEmail(item.getUser().getEmail());
        if (user != null) {
            item.setUser(user);
        }
        return itemRepository.save(item);
    }

    @GetMapping("/item/{id}")
    public ResponseEntity<Item> getNoteById(@PathVariable(value = "id") Long noteId) {
        Item item = itemRepository.findOne(noteId);
        if(item == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(item);
    }

    @PutMapping("/item/{id}")
    public ResponseEntity<Item> updateNote(@PathVariable(value = "id") Long noteId,
                                           @Valid @RequestBody Item itemDetails) {
        Item item = itemRepository.findOne(noteId);
        if(item == null) {
            return ResponseEntity.notFound().build();
        }
        item.setTitle(itemDetails.getTitle());
        item.setDescription(itemDetails.getDescription());

        Item updatedItem = itemRepository.save(item);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/item/{id}")
    public ResponseEntity<Item> deleteNote(@PathVariable(value = "id") Long noteId) {
        Item item = itemRepository.findOne(noteId);
        if(item == null) {
            return ResponseEntity.notFound().build();
        }

        itemRepository.delete(item);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/markItem/{itemId}/{userId}/{userItemId}")
    public Item markItemAsWish(@PathVariable(value = "itemId") Long itemId, @PathVariable(value = "userId") Long userId, @PathVariable(value = "userId") Long userItemId) {
        Item item = itemRepository.findOne(itemId);
        User user = userRepository.findOne(userId);
        if (user != null) {
            Item candidateItem = itemRepository.findOne(userItemId);
            Candidate candidate = candidateRepository.findByItemAndUser(candidateItem, user);
            if (candidate == null) {
                candidate = new Candidate(candidateItem, user);
            } else {
                System.out.printf("sdfsdf");
            }
            candidateRepository.save(candidate);
            item.addCandidate(candidate);
        }
        return itemRepository.save(item);
    }

    @PostMapping("/unmarkItem/{itemId}/{userId}/{userItemId}")
    public Item unmarkItemAsWish(@PathVariable(value = "itemId") Long itemId, @PathVariable(value = "userId") Long userId, @PathVariable(value = "userId") Long userItemId) {
        Item item = itemRepository.findOne(itemId);
        User user = userRepository.findOne(userId);
        if (user != null) {
            Item candidateItem = itemRepository.findOne(userItemId);
            Candidate candidate = candidateRepository.findByItemAndUser(candidateItem, user);
            item.removeCandidate(candidate);
            //candidateRepository.delete(candidate);
        }
        return itemRepository.save(item);
    }
}
