package com.hp.hospin.global.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Configuration
public class FirebaseConfig {
    @Value("${firebaseAdminSdkPath}")
    private String FIREBASE_ADMIN_SDK_PATH;
    @PostConstruct
    public void initialize() {
        try {
            InputStream serviceAccount = new ClassPathResource(FIREBASE_ADMIN_SDK_PATH).getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
//                    .setCredentials(GoogleCredentials.fromStream(Files.newInputStream(Paths.get(FIREBASE_ADMIN_SDK_PATH))))
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("FirebaseApp initialization complete");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
