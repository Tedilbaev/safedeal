package com.project.safedeal.service;

import com.project.safedeal.model.Ad;
import com.project.safedeal.model.Order;
import com.project.safedeal.model.Photo;
import com.project.safedeal.model.User;
import com.project.safedeal.repository.AdRepository;
import com.project.safedeal.repository.PhotoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final AdService adService;

    public List<Photo> getAdPhotos(Authentication authentication, Long adId) {
        Ad ad = adService.getAdById(adId);
        return photoRepository.findByAd(ad);
    }

    public List<Photo> createPhoto(Authentication authentication, Long adId, MultipartFile[] photoFiles) throws IOException {
        Ad ad = adService.getAdById(adId);
        List<Photo> photos = new ArrayList<>();
        for (MultipartFile photoFile : photoFiles) {
            Photo photo = new Photo();
            photo.setAd(ad);
            if (photoFile != null && !photoFile.isEmpty()) {
                String photoUrl = savePhoto(photoFile);
                photo.setPhoto(photoUrl);
                photos.add(photoRepository.save(photo));
            }
        }
        return photos;
    }

    private String savePhoto(MultipartFile photo) throws IOException {
        String uploadDir = new File("../userData").getAbsolutePath() + "/";
        File directory = new File(uploadDir);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create directory: " + uploadDir);
        }

        String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
        File destination = new File(uploadDir + fileName);
        photo.transferTo(destination);
        return "/userData/" + fileName;
    }

    public void deletePhoto(Long id, Authentication authentication) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено"));
        photoRepository.delete(photo);
    }
}
