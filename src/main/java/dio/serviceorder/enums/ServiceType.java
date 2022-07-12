package dio.serviceorder.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceType {

    INSTALLATION("Installation"),
    REPAIR("Repair"),
    REMOVAL("Removal");

    private final String description;
}
