package iclean.code.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadFile(MultipartFile file);
    boolean deleteFile(String url);
    String getFileNameFromUrl(String url);
}
