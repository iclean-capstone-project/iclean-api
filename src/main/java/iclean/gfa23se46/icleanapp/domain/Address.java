package iclean.gfa23se46.icleanapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private int addressId;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private double latitude;

    private String description;

    private String street;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "is_default", nullable = false)
    private int isDefault;

    @Column(name = "create_at", nullable = false)
    private Date createAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
