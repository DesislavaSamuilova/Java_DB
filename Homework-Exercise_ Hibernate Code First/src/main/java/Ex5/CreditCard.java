package Ex5;

import javax.persistence.*;

@Entity
@Table(name = "credit_cards")
public class CreditCard extends BillingDetail {
    private CardType cardType;
    private Integer expirationMonth;
    private Integer expirationYear;

    public CreditCard() {
    }

    @Enumerated(EnumType.STRING)
    public CardType getCardType() {
        return cardType;
    }
    public void setCardType(CardType cardType){
        this.cardType=cardType;
    }
    @Column(name = "exp_month")
    public Integer getExpirationMonth(){
        return expirationMonth;
    }
    public void setExpirationMonth(Integer expirationMonth){
        this.expirationMonth = expirationMonth;
    }

    @Column(name = "exp_year")
    public Integer getExpirationYear(){
        return expirationYear;
    }
    public void setExpirationYear(Integer expirationYear){
        this.expirationYear = expirationYear;
    }
}
