package sellik.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import sellik.entities.ListingEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Data
public class ListingModel {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String type;
    private Integer price;
    private Date date;
    private String email;
    private String phone;
    private MultipartFile file;

    public static ListingModel toModel(ListingEntity entity) {
        ListingModel model = new ListingModel();
        model.setId(entity.getId());
        model.setTitle(entity.getTitle());
        model.setDescription(entity.getDescription());
        model.setCategory(entity.getCategory());
        model.setType(entity.getType());
        model.setPrice(entity.getPrice());
        model.setDate(entity.getDate());
        model.setEmail(entity.getEmail());
        model.setPhone(entity.getPhone());
        return model;
    }

    public static List<ListingModel> toListModels(List<ListingEntity> listingEntities) {
        List<ListingModel> listingModels = new ArrayList<>();
        for (ListingEntity e : listingEntities) {
            listingModels.add(toModel(e));
        }
        return listingModels;
    }
}
