package com.example.lalala.demo.loaders;

import com.example.lalala.demo.model.Item;
import com.example.lalala.demo.model.User;
import com.example.lalala.demo.respository.ItemRepository;
import com.example.lalala.demo.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DatabaseLoader implements ApplicationRunner {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Autowired
    public DatabaseLoader(UserRepository users, ItemRepository items) {
        this.userRepository = users;
        this.itemRepository = items;
    }

    public double coordinates(double upperLimit, double lowerLimit) {
        return (Math.random() * upperLimit) + lowerLimit;
    }

    @Override
    public void run(ApplicationArguments applicationArguments) {
//        User user = new User("tadas@gmail.com", "Tadas keturakis");
//        userRepository.save(user);
//
//        User user2 = new User("deividas717@gmail.com", "Deividas Aukštakalnis");
//        userRepository.save(user2);
//
//        for (int i = 0; i < 100; i++) {
//            Item item = new Item("Lg G Flex " + i, "Mazai naudotas idealios bukles", "www.google.lt", coordinates(80.0 , 20.0), coordinates(80.0 , 20.0));
//            itemRepository.save(item);
//        }
//        saveUsers();
    }

    private void saveUsers() {
        List<Item> items = itemRepository.findAll();
        List<User> users = userRepository.findAll();

        for (Item item : items) {
            int randomNum = ThreadLocalRandom.current().nextInt(0, users.size());
            item.setUser(users.get(randomNum));
            itemRepository.save(item);
        }
    }
}
