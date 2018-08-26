package com.example.lalala.demo.controller;

import com.example.lalala.demo.model.Item;
import com.example.lalala.demo.model.User;
import com.example.lalala.demo.respository.ItemRepository;
import com.example.lalala.demo.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @GetMapping("/user")
    public List<User> getAllItems() {
        return userRepository.findAll();
    }

    @GetMapping("/user/{email:.+}")
    public ResponseEntity<User> getUserByEmail(@PathVariable(value = "email") String email) {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/user/getUserHistoryItems/{email:.+}/{since}/{perPage}")
    public List<Item> getUserHistoryItems(@PathVariable(value = "email") String email, @PathVariable(value = "since") Long since, @PathVariable(value = "perPage") Long perPage) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            long userId = user.getId();
            return itemRepository.findUserHistoryItems(userId, since, perPage);
        }
        return null;
    }

    @DeleteMapping("/deleteUserHistoryItems/{userId}")
    public ResponseEntity deleteUserHistoryItems(@PathVariable(value = "userId") Long userId, @RequestBody Long[] ids) {
        itemRepository.deleteHistoryItems(userId, new ArrayList<>(Arrays.asList(ids)));
        return ResponseEntity.ok().build();
    }


    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "id") Long noteId,
                                           @Valid @RequestBody User itemDetails) {
        User item = userRepository.findOne(noteId);
        if(item == null) return ResponseEntity.notFound().build();

        User updatedItem = userRepository.save(item);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<User> deleteNote(@PathVariable(value = "id") Long noteId) {
        User item = userRepository.findOne(noteId);
        if(item == null) {
            return ResponseEntity.notFound().build();
        }

        userRepository.delete(item);
        return ResponseEntity.ok().build();
    }
}
