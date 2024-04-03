package sellik.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sellik.repositories.ListingRepository;

@Service
public class StorageService {
    private ListingRepository listingRepository;
    private final String path = "src/main/resources/static/images";

    @Autowired
    public void setListingRepository(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public void uploadImage(MultipartFile file) {
        String filePath = path + file.getOriginalFilename();
        
    }
}
