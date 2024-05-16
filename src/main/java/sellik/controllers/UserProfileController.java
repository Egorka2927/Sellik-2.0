package sellik.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sellik.models.ListingModel;
import sellik.services.ListingService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserProfileController {
    private ListingService listingService;

    @Autowired
    public void setListingService(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping("/user-profile/{id}")
    public List<ListingModel> showProfile(@CookieValue(name = "jwt", required = false) String jwtToken, @PathVariable Long id) {
        return listingService.findListingsByUserId(jwtToken, id);
    }

    @DeleteMapping("/user-profile/{user_id}/{listing_id}")
    public List<ListingModel> showAfterDeletion(@PathVariable(name = "user_id") Long userId,
                                                @PathVariable(name = "listing_id") Long listingId) {
        return listingService.deleteListing(userId, listingId);
    }
}
