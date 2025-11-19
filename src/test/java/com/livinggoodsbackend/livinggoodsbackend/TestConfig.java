package com.livinggoodsbackend.livinggoodsbackend;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.mockito.Mockito.mock;

/**
 * Test configuration to provide mock beans for external services
 * that are not available during testing (Firebase, Google Cloud Storage, etc.)
 */
@TestConfiguration
@Profile("test")
public class TestConfig {

    /**
     * Mock Google Cloud Storage bean
     * Prevents Firebase/GCS initialization errors during tests
     */
    @Bean
    @Primary
    public Storage mockStorage() {
        return mock(Storage.class);
    }

    /**
     * Mock GoogleCredentials bean
     * Prevents authentication errors during tests
     */
    @Bean
    @Primary
    public GoogleCredentials mockGoogleCredentials() throws IOException {
        // Create minimal valid JSON for mock credentials
        String mockCredentials = """
            {
              "type": "service_account",
              "project_id": "test-project",
              "private_key_id": "test-key-id",
              "private_key": "-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC7W8jQYR0VqR5V\\n-----END PRIVATE KEY-----\\n",
              "client_email": "test@test-project.iam.gserviceaccount.com",
              "client_id": "123456789",
              "auth_uri": "https://accounts.google.com/o/oauth2/auth",
              "token_uri": "https://oauth2.googleapis.com/token"
            }
            """;
        
        return GoogleCredentials.fromStream(
            new ByteArrayInputStream(mockCredentials.getBytes())
        );
    }

    /**
     * Prevent FirebaseApp initialization in tests
     * Only initialize if no apps exist and we're not in test mode
     */
    @Bean
    @Primary
    public FirebaseApp mockFirebaseApp() {
        // Return null or mock - Spring will handle it gracefully
        return mock(FirebaseApp.class);
    }
}