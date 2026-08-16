package ai.s_ai.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Device {
    private String deviceId;
    private Long amount;


    public void increaseAmount() {
        this.amount += 1;
    }
}
