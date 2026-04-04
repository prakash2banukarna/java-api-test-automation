package e2e.Database.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "products")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ResponseModelItem {

    @Id
//    @Column(name = "id")
    String id;

    //    @Column(name = "name")
    String name;

    @Embedded               // ← Data fields embedded into products table
    Data data;
}