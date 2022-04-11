package model.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "products")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductRootSeedDto {

    @XmlElement(name = "product")
    private List<ProductSeedDto> products;

    public List<ProductSeedDto> getProducts() {
        return products;
    }
}