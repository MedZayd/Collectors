import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class ParentRegion {
  private Long id;
  private List<Family> families;
  List<Region> regions;
}
