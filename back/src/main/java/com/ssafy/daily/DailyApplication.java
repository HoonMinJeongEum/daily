package com.ssafy.daily;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class DailyApplication {

	public static void main(String[] args) throws IOException {
		if (FirebaseApp.getApps().isEmpty()) {
			FileInputStream serviceAccount =
					new FileInputStream("src/main/resources/serviceAccountKey.json");

			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.build();

			FirebaseApp.initializeApp(options);
		}
		SpringApplication.run(DailyApplication.class, args);
	}

}
