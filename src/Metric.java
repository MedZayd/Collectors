import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Builder
@Data
public class Metric {
  private Long parentRegionId;
  private Long regionId;
  private String countryCode;
  private Long customerId;
  private Long familyId;
  private Double volume;
}
