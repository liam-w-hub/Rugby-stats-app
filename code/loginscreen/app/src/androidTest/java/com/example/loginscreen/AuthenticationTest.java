package com.example.loginscreen;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.Intent;
import android.widget.Toast;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;

public class AuthenticationTest {

    private FirebaseAuth testAuth;

    @Before
    public void setUp() {
        // initialising authentication for testing via emulator
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().getTargetContext());
        testAuth = FirebaseAuth.getInstance();
    }

    @Test
    public void loginAuthTest() {
        String email = "liam@gmail.com";
        String password = "test1234";

        testAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        assertTrue("User successfully logged in", task.isSuccessful());
                    } else {
                        fail("login failed");
                    }
                });
    }

}
