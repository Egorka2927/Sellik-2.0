package sellik.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sellik.entities.ListingEntity;

import java.util.Date;
import java.util.List;

@Repository
public interface ListingRepository extends CrudRepository<ListingEntity, Long> {
    List<ListingEntity> findListingEntitiesByTypeInAndCategoryInAndPriceBetweenAndDateAfter(List<String> types,
                                                                                List<String> categories,
                                                                                Integer priceFrom,
                                                                                Integer priceTo,
                                                                                Date date);
    ListingEntity findListingEntityById(Long id);
    List<ListingEntity> findListingEntitiesByUserId(Long id);
}
