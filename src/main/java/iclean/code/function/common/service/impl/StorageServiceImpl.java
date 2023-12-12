package iclean.code.function.common.service.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import iclean.code.function.common.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
public class StorageServiceImpl implements StorageService {
    @Value("${firebase.storage.tmp.url}")
    private String tmpUrl;
    @Value("${firebase.credentials.path}")
    private String firebaseConfig;
    @Value("${firebase.storage.bucket}")
    private String storageBucket;
    private FirebaseApp firebaseApp;

    @PostConstruct
    private void initialize() {
        try {
            InputStream serviceAccount = getClass().getResourceAsStream(firebaseConfig);
            assert serviceAccount != null;
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket(storageBucket)
                    .setDatabaseUrl("https://iclean-59a5b-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                this.firebaseApp = FirebaseApp.initializeApp(options);
            } else {
                this.firebaseApp = FirebaseApp.getInstance();
            }
        } catch (IOException e) {
            log.error("Create FirebaseApp Error", e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        String url = "";
        try {
            String fileName = UUID.randomUUID().toString();
            StorageClient storageClient = StorageClient.getInstance(firebaseApp);

            storageClient.bucket(storageBucket).create(fileName, file.getBytes(), "image/jpg");
            url = String.format(tmpUrl, fileName);
        } catch (IOException e) {
            log.error("Upload File Error", e);
        }
        return url;
    }

    @Override
    public boolean deleteFile(String url) {
        try {
            String fileName = getFileNameFromUrl(url);
            StorageClient storageClient = StorageClient.getInstance();

            return storageClient.bucket(storageBucket).get(fileName).delete();
        } catch (Exception e) {
            log.error("Delete File Error", e);
        }
        return false;
    }

    @Override
    public String getFileNameFromUrl(String url) {
        try {
            int lastSlashIndex = url.lastIndexOf('/');
            if (lastSlashIndex != -1 && lastSlashIndex < url.length() - 1) {
                return url.substring(lastSlashIndex + 1).replace("?alt=media", "");
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Get File Error", e);
        }
        return "";
    }
}
