import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Customer {
  private Long id;
  private List<Family> families;
}
