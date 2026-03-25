package e2e.Database.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@JsonInclude(JsonInclude.Include.NON_DEFAULT) // excludes 0 values being set
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ResponseModelItem {
    String id;
    String name;
    Data data;

}