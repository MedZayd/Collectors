import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Family {
  private Long id;
  private Double volume;
}
