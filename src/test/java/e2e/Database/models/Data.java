package e2e.Database.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Data {

    String generation;

    float price;

    String capacity;

    String description;

    String color;

    //    @Column(name = "strap_colour")
    String strapColour;

    //    @Column(name = "case_size")
    String caseSize;

    int year;

    @JsonProperty(value = "CPU model")
//    @Column(name = "cpu_model")
    String cPUModel;

    @JsonProperty(value = "Hard disk size")
//    @Column(name = "hard_disk_size")
    String hardDiskSize;

    //    @Column(name = "capacity_gb")
    int capacityGB;

    //    @Column(name = "screen_size")
    String screenSize;       // changed Object → String for DB compatibility
}