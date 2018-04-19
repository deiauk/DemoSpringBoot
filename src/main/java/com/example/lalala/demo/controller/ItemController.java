package com.example.lalala.demo.controller;

import com.example.lalala.demo.model.Item;
import com.example.lalala.demo.model.User;
import com.example.lalala.demo.respository.ItemRepository;
import com.example.lalala.demo.respository.UserRepository;
import com.example.lalala.demo.services.StorageService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE )
public class ItemController {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StorageService storageService;

    @GetMapping("/item")
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @GetMapping("/nearestItems/{email:.+}/{lat}/{lng}/{beginAt}")
    public List<Item> GetNearest(@PathVariable(value = "email") String email,
                                 @PathVariable(value = "lat") Double lat,
                                 @PathVariable(value = "lng") Double lng,
                                 @PathVariable(value = "beginAt") Integer beginAt) {
        User user = userRepository.findByEmail(email);
        if (user == null) return null;
        return itemRepository.findNearest(user.getId(), lat, lng, beginAt);
    }

    @PostMapping("/createNewItem")
    public Item createNewItem(@Valid @RequestBody Item item) {
        User user = userRepository.findByEmail(item.getUser().getEmail());
        if (user != null) item.setUser(user);

        return itemRepository.save(item);
    }

    @PostMapping("/uploadImages")
    public void uploadImages(@RequestParam Long itemId, @RequestParam("files") MultipartFile[] files) {

        System.out.println(itemId + " " + files.length);
        Item item = itemRepository.findOne(itemId);
        if (item == null) return;

        List<String> newNames = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;
            String newName = storageService.store(file);
            newNames.add(newName);
        }

        item.setImages(newNames);
        itemRepository.save(item);
    }

    @GetMapping("/item/{id}")
    public ResponseEntity<Item> getNoteById(@PathVariable(value = "id") Long noteId) {
        Item item = itemRepository.findOne(noteId);
        if(item == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(item);
    }

    @GetMapping("/image/{name:.+}")
    public byte[] getImage(@PathVariable(value = "name") String name) {
        return storageService.loadFileAsByteArray(name);
    }

    @PutMapping("/item/{id}")
    public ResponseEntity<Item> updateNote(@PathVariable(value = "id") Long noteId,
                                           @Valid @RequestBody Item itemDetails) {
        Item item = itemRepository.findOne(noteId);
        if(item == null) return ResponseEntity.notFound().build();

        item.setTitle(itemDetails.getTitle());
        item.setDescription(itemDetails.getDescription());

        Item updatedItem = itemRepository.save(item);
        return ResponseEntity.ok(updatedItem);
    }

    // todo maybe create new table and mark card as already seen
    @PostMapping("/markItemAsAlreadySeen")
    public Item createNewItem(@PathVariable(value = "itemId") Long itemId) {
        Item item = itemRepository.findOne(itemId);

        return itemRepository.save(item);
    }

    @DeleteMapping("/item/{id}")
    public ResponseEntity<Item> deleteNote(@PathVariable(value = "id") Long noteId) {
        Item item = itemRepository.findOne(noteId);
        if(item == null) return ResponseEntity.notFound().build();

        itemRepository.delete(item);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/userItems/{email:.+}")
    public List<Item> getUserItems(@PathVariable(value = "email") String email) {
        User user = userRepository.findByEmail(email);
        if(user == null) return null;

        return user.getItems();
    }

    @PostMapping("/markItem/{itemId}")
    public ResponseEntity<Item> markItemAsWish(@PathVariable(value = "itemId") Long itemId, @RequestBody Long[] ids) {
        Item targetItem = itemRepository.findOne(itemId);
        if (targetItem == null) return ResponseEntity.notFound().build();

        for (Long id : ids) {
            Item tradeItem = itemRepository.findOne(id);
            if (tradeItem == null) continue;

            // do not let same user select their own item
            if (!targetItem.getUser().equals(tradeItem.getUser())) {
                targetItem.addCandidateItem(tradeItem);

                Item item = itemRepository.save(targetItem);
                boolean ifItemsMatch = checkIfItemsMatch(item, tradeItem.getCandidateItems());

                if (ifItemsMatch) {
                    System.out.println("It's a match!");
                }
            }
        }

        return ResponseEntity.ok().build();
    }

    private boolean checkIfItemsMatch(Item item, List<Item> candidates) {
        for (Item candidateItem : candidates) {
            if (item.equals(candidateItem)) {
                return true;
            }
        }
        return false;
    }

    @PostMapping("/unmarkItem/{itemId}/{userItemId}")
    public Item unmarkItemAsWish(@PathVariable(value = "itemId") Long itemId, @PathVariable(value = "userItemId") Long userItemId) {
        Item targetItem = itemRepository.findOne(itemId);
        Item tradeItem = itemRepository.findOne(userItemId);
        if (targetItem != null && tradeItem != null) {
            targetItem.removeCandidateItem(tradeItem);
        }
        return itemRepository.save(targetItem);
    }
}
