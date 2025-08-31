package io.github.joannazadlo.recipedash.service;

import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile({"!test", "!test-e2e"})
@Service
@RequiredArgsConstructor
public class FirebaseTokenService {

    private final FirebaseService firebaseService;

    public FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException {
        return firebaseService.getAuth().verifyIdToken(idToken);
    }
}
