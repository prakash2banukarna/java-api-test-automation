package e2e.Database.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ResponseModelItem{
	String id;
	String name;
	Data data;

}