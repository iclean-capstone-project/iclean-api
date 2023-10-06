package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class ImgType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "img_type_id")
    private Integer imgTypeId;

    @Column(name = "title_img_type")
    private String titleImgType;

    @OneToMany(mappedBy = "imgType")
    @JsonIgnoreProperties("imgType")
    @JsonIgnore
    private List<ImgBooking> imgBookings;
}