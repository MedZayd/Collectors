import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Country {
  private String id;
  private List<Family> families;
  private List<Customer> customers;
}
