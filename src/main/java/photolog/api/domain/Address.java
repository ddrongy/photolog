package photolog.api.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String degree;
    private String city;
    private String fullAddress;

    public Address(String fullAddress) {
        String[] parts = fullAddress.split(" ");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid address format");
        }
        this.degree = parts[0];
        this.city = parts[1];
        this.fullAddress = fullAddress;
    }
}
