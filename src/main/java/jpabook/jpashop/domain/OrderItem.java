package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // 하나의 order에 여러 order_item을 한 사람은 주문할 수 있으니
    // order 클래스는 ManyToOne
    // order_item 클래스는 OneToMany
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문 시에 가격
    private int count;      // 주문 수량

    // 다른 사람이 createOrderitem으로 orderItem을 객체 생성 할 수 있도록
    // 애초에 new Orderitem으로 객체 생성을 막는 코드다.
    // protected Orderitem(){
    // }
    // 으로 하던가
    // 아니면 클래스에서 @NoArgsConstructor(access = AccessLevel.PROTECTED) 해주면 된다.


    //==생성 메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    //==비즈니스 로직==//
    public void cancel(){
        getItem().addStock(count);
    }

    // 주문 상품 전체 가격 조회
    public int getTotalPrice(){
        return getOrderPrice() * getCount();
    }
}

