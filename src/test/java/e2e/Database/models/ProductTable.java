package e2e.Database.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.extern.slf4j.Slf4j;


@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@EqualsAndHashCode
@Slf4j
@Table(name = "products")
public class ProductTable {

    @Id
//    @Column(name = "id")
    String id;

    //    @Column(name = "name")
    String name;

    Float price;
    int year;

    //    @Column(name = "strap_colour")
    String strapColour;

    //    @Column(name = "case_size")
    String caseSize;

    @Column(name = "cpu_model")
    String cPUModel;

    //    @Column(name = "hard_disk_size")
    String hardDiskSize;

    @Column(name = "capacity_gb")
    int capacityGB;

    //    @Column(name = "screen_size")
    String screenSize;
}
