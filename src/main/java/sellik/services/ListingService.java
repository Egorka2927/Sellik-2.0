package sellik.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.server.ResponseStatusException;
import sellik.entities.ListingEntity;
import sellik.models.ListingModel;
import sellik.repositories.ListingRepository;
import sellik.repositories.UserRepository;

import java.util.*;

@Service
public class ListingService {
    private ListingRepository listingRepository;
    private UserRepository userRepository;

    @Autowired
    public void setListingRepository(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ListingModel findListingById(Long id) {
        return ListingModel.toModel(listingRepository.findListingEntityById(id));
    }

    public List<ListingModel> findListingsByUserId(String jwtToken, Long id) {
//        if (jwtToken == null) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
//        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long authenticatedId = userRepository.findUserEntityByUsername(username).getId();
        if (!id.equals(authenticatedId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return ListingModel.toListModels(listingRepository.findListingEntitiesByUserId(id));
    }

    public List<ListingModel> findAllListings() {
        List<ListingModel> listingModels = new ArrayList<>();
        Iterable<ListingEntity> iterable = listingRepository.findAll();
        for (ListingEntity listingEntity : iterable) {
            listingModels.add(ListingModel.toModel(listingEntity));
        }
        return listingModels;
    }

    public List<ListingModel> findAllListingsFiltered(List<String> types,
                                                      List<String> categories,
                                                      Integer priceFrom,
                                                      Integer priceTo,
                                                      Integer daysPassed) {

        if (types == null) {
            types = List.of("Buy", "Rent", "Donations");
        }
        if (categories == null) {
            categories = List.of("Electronics", "Furniture", "Books",
                                 "Clothing", "Vehicles", "Home Appliances",
                                 "Jewelry & Accessories", "Sports & Outdoor Equipment",
                                 "Toys & Children's Items", "Collectibles & Art", "Others");
        }
        if (daysPassed != 1 && daysPassed != 7 && daysPassed != 30) {
            daysPassed = 30;
        }
        Date now = new Date();
        Date daysAgo = new Date(now.getTime() - daysPassed * 24L * 60 * 60 * 1000);
        return ListingModel.toListModels(listingRepository.findListingEntitiesByTypeInAndCategoryInAndPriceBetweenAndDateAfter(types,
                categories, priceFrom, priceTo, daysAgo));
    }

    public List<ListingModel> deleteListing(Long userId, Long listingId) {
        if (listingRepository.findById(listingId).isPresent() &&
                !listingRepository.findById(listingId).get().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } else if (!listingRepository.findById(listingId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        listingRepository.deleteById(listingId);
        return ListingModel.toListModels(listingRepository.findListingEntitiesByUserId(userId));
    }

    public void createListing(ListingModel listingModel) {
        ListingEntity entity = new ListingEntity();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        entity.setTitle(listingModel.getTitle());
        entity.setDescription(listingModel.getDescription());
        entity.setCategory(listingModel.getCategory());
        entity.setType(listingModel.getType());
        entity.setPrice(listingModel.getPrice());
        entity.setDate(new Date());
        entity.setEmail(listingModel.getEmail());
        entity.setPhone(listingModel.getPhone());
        userRepository.findUserEntityByUsername(authentication.getName()).getListings().add(entity);
        listingRepository.save(entity);
    }
}
