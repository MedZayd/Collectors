import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Region {
  private Long id;
  private List<Family> families;
  private List<Country> countries;
}
