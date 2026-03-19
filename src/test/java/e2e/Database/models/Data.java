package e2e.Database.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Data {
    String generation;
    float price;
    //    double price;
    String capacity;
    Object screenSize;
    String description;
    String color;
    String strapColour;
    String caseSize;
    int year;
    @JsonProperty(value = "CPU model")
    String cPUModel;

    @JsonProperty(value = "Hard disk size")
    String hardDiskSize;
    int capacityGB;

}