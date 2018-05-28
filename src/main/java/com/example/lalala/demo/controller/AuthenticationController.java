package com.example.lalala.demo.controller;

import com.example.lalala.demo.model.User;
import com.example.lalala.demo.respository.UserRepository;
import com.example.lalala.demo.security.FbToken;
import com.example.lalala.demo.security.TokenHelper;
import com.example.lalala.demo.security.UserTokenState;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
@RequestMapping( value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE )
public class AuthenticationController {

    @Autowired
    TokenHelper tokenHelper;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody FbToken fbToken) throws AuthenticationException {

        FacebookClient facebookClient = new DefaultFacebookClient(fbToken.getToken(), "c3f9ab2aafccf7fd02f4b0aa627ee891", Version.VERSION_2_5);
        com.restfb.types.User fbUser = facebookClient.fetchObject("me",
                com.restfb.types.User.class,
                Parameter.with("fields", "email,first_name,last_name,locale")
        );

        // Inject into security context
        //SecurityContextHolder.getContext().setAuthentication(authentication);

        // token creation
        User user = userRepository.findByEmail(fbUser.getEmail());
        if (user == null) { // if null -> new user first has to register
            user = createNewUser(fbUser);
        }
        String jws = tokenHelper.generateToken(user.getEmail());
        return ResponseEntity.ok(new UserTokenState(jws, tokenHelper.getExpiredIn(), user.getId())); // Return the token
    }

    private User createNewUser(com.restfb.types.User user) {
        String fullName = user.getFirstName() + " " + user.getLastName();
        return userRepository.save(new User(user.getEmail(), fullName, "https://graph.facebook.com/" + user.getId() + "/picture?type=large"));
    }

//    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
//    public ResponseEntity<?> refreshAuthenticationToken(HttpServletRequest request, Principal principal) {
//        String authToken = tokenHelper.getToken(request);
//        if (authToken != null && principal != null) {
//            // TODO check user password last update
//            String refreshedToken = tokenHelper.refreshToken(authToken);
//            int expiresIn = tokenHelper.getExpiredIn();
//            return ResponseEntity.ok(new UserTokenState(refreshedToken, expiresIn));
//        } else {
//            UserTokenState userTokenState = new UserTokenState();
//            return ResponseEntity.accepted().body(userTokenState);
//        }
//    }
}