package sellik.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@Entity
@Table(name = "listings")
public class ListingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String category;
    private String type;
    private Integer price;
    private Date date;
    private String email;
    private String phone;
    private String path;
    @Column(name = "user_id")
    private Long userId;
}
