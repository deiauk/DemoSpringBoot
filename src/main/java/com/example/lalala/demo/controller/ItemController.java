package com.example.lalala.demo.controller;

import com.example.lalala.demo.model.Item;
import com.example.lalala.demo.model.User;
import com.example.lalala.demo.pushNotifications.AndroidPushNotificationsService;
import com.example.lalala.demo.respository.ItemRepository;
import com.example.lalala.demo.respository.UserRepository;
import com.example.lalala.demo.services.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE )
public class ItemController {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StorageService storageService;

    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    @GetMapping("/item")
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    ObjectMapper oMapper = new ObjectMapper();

    @GetMapping(value = {"/nearestItems/{email:.+}/{lat}/{lng}/{downloadedIds}"})
    public List<Item> GetNearest(@PathVariable(value = "email") String email,
                                 @PathVariable(value = "lat") Double lat,
                                 @PathVariable(value = "lng") Double lng,
                                 @RequestParam(value = "downloadedIds", required = false) List<Long> downloadedIds) {
        User user = userRepository.findByEmail(email);
        if (user == null) return null;

        List<Long> ids = user.getAlreadySeenCards();
        if (ids.isEmpty()) ids.add(-1L);

        if (downloadedIds != null) {
            ids.addAll(downloadedIds);
        }

        return itemRepository.findNearest(user.getId(), lat, lng, ids);
    }

    public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    @PostMapping("/createNewItem")
    public Item createNewItem(@Valid @RequestBody Item item) {
        User user = userRepository.findByEmail(item.getUser().getEmail());
        if (user != null) item.setUser(user);

        return itemRepository.save(item);
    }

    private void createMatchPushNotification(User user) {
        String body = createBody(user);

        HttpEntity<String> request = new HttpEntity<>(body);

        CompletableFuture<String> pushNotification = androidPushNotificationsService.send(request);
        CompletableFuture.allOf(pushNotification).join();

        try {
            String firebaseResponse = pushNotification.get();
            System.out.println(firebaseResponse);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/uploadImages")
    public void uploadImages(@RequestParam Long itemId, @RequestParam("files") MultipartFile[] files) {
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

    @GetMapping(value = "/image/{name:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
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

    @PostMapping("/markItemAsAlreadySeen/{itemId}/{userId}")
    public ResponseEntity markItemAsAlreadySeen(@PathVariable(value = "itemId") Long itemId, @PathVariable(value = "userId") Long userId) {
        Item item = itemRepository.findOne(itemId);
        if (item == null) return null;

        User user = userRepository.findOne(userId);
        if (user == null) return null;

        user.addAlreadySeenCard(item.getId());
        userRepository.save(user);
        return null;
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

    @GetMapping("/detailItemInfo/{itemId}")
    public ResponseEntity<Item> detailItemInfo(@PathVariable(value = "itemId") Long itemId) {
        Item item = itemRepository.findOne(itemId);
        if(item == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(item);
    }

    @GetMapping("/getCandidateItems/{itemId}")
    public ResponseEntity<List<Item>> getCandidateItems(@PathVariable(value = "itemId") Long itemId) {
        Item item = itemRepository.findOne(itemId);
        if(item == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(item.getCandidateItems());
    }

    @PostMapping("/markItem/{itemId}/{userId}")
    public ResponseEntity markItemAsWish(@PathVariable(value = "itemId") Long itemId, @PathVariable(value = "userId") Long userId, @RequestBody Long[] ids) {
        Item targetItem = itemRepository.findOne(itemId);
        if (targetItem == null) return ResponseEntity.notFound().build();

        User targetUser = targetItem.getUser();
        for (Long id : ids) {
            Item tradeItem = itemRepository.findOne(id);
            if (tradeItem == null) continue;

            System.out.println("dfsdfsdfsdfsdfsfAAAAAAAA id===========" + id +" " + !targetUser.equals(tradeItem.getUser()));

            // do not let same user select their own item
            if (!targetUser.equals(tradeItem.getUser())) {
                targetItem.addCandidateItem(tradeItem);

                Item item = itemRepository.save(targetItem);
                System.out.println("dfsdfsdfsdfsdfsf itemRepository.save(targetItem) BAIGTA" );
                boolean ifItemsMatch = checkIfItemsMatch(item, tradeItem.getCandidateItems());

                if (ifItemsMatch) {
                    createMatchPushNotification(targetUser); // send notification to other user that has previously selected item
                    return ResponseEntity.ok(item); // send other user item data
                }
            }
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteItemSelectedIds/{itemId}/{userId}")
    public ResponseEntity deleteItemSelectedIds(@PathVariable(value = "itemId") Long itemId, @PathVariable(value = "userId") Long userId, @RequestBody Long[] ids) {
        Item targetItem = itemRepository.findOne(itemId);
        if (targetItem == null) return ResponseEntity.notFound().build();

        removePreviouslySelectedUserItems(ids, targetItem);

        return ResponseEntity.ok().build();
    }

    private void removePreviouslySelectedUserItems(Long[] ids, Item targetItem) {
        if (ids != null && ids.length > 0) {
            List<Long> idsList = new ArrayList<>(Arrays.asList(ids));
            itemRepository.deleteFromCandidatesByItemIds(idsList);
            itemRepository.save(targetItem);
        }
    }

    private boolean checkIfItemsMatch(Item item, List<Item> candidates) {
        return candidates.stream().anyMatch(obj -> obj.equals(item));
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

    private String createBody(User user) {
        try {
            JSONObject body = new JSONObject();
            body.put("to", "/topics/" + user.getId());
            body.put("priority", "high");

            JSONObject notification = new JSONObject();
            notification.put("title", "Swapper");
            notification.put("body", "You got new match with " + user.getName() + "!");

            JSONObject data = new JSONObject();
            data.put("title", "Swapper");
            data.put("body", "You got new match with " + user.getName() + "!");

            body.put("notification", notification);
            body.put("data", data);
            return body.toString();

        } catch (Exception e) {

        }
        return null;
    }
}
