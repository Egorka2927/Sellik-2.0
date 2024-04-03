package sellik.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sellik.models.ListingModel;
import sellik.services.ListingService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
public class ListingController {
    private ListingService listingService;

    @Autowired
    public void setListingService(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping("/all-listings-info")
    public List<ListingModel> showListingsFiltered(@RequestParam(required = false) List<String> types,
                                                   @RequestParam(required = false) List<String> categories,
                                                   @RequestParam(name = "price-from", required = false, defaultValue = "0") Integer priceFrom,
                                                   @RequestParam(name = "price-to", required = false, defaultValue = "2147483647") Integer priceTo,
                                                   @RequestParam(name = "days-passed", required = false, defaultValue = "30") Integer daysPassed) {
        return listingService.findAllListingsFiltered(types, categories, priceFrom, priceTo, daysPassed);
    }

    @GetMapping("/listing-info/{id}")
    public ListingModel showListingInfo(@PathVariable Long id) {
        return listingService.findListingById(id);
    }

    @PostMapping("/create-listing")
    public String createListing(@RequestBody ListingModel listingModel) {
        listingService.createListing(listingModel);
        return "Your offer has been successfully created";
    }
}
