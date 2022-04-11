package model.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "users")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserViewRootDto {

    @XmlElement(name = "user")
    private List<UserWithSoldProductsDto> products;

    public List<UserWithSoldProductsDto> getProducts() {
        return products;
    }

    public void setProducts(List<UserWithSoldProductsDto> products) {
        this.products = products;
    }
}