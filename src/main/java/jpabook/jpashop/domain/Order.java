package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id  @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // Member 클래스에서 한 사람이 여러개의 order를 주문할 수 있으니
    // Member 클래스에서는 one to many
    // order 클래스에서는 여러개의 member를 가지므로 many to one
    // 한 사람 (member)가 여러개의 order을 가질 수 있다
    // 하지만 한 사람은 주문건 하나가 된다.
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")     // foreign키 아이디가 member_id가 된다.
    private Member member;

    // casecade는 orderitem이 추가가 되면 또 따로 order을 추가를 시켜줘야 하는데
    // casecading을 해놓으면 orderitem들이 추가가 되었을 때 order또한 추가가 된다.
    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 하나의 order에 delivery도 하나니까
    // 1대1 관계이다.
    // 1대 1 관계일 때 사용자가 어느 것을 더 많이 볼까 생각해보자
    // 주문정보를 통해서 delivery를 검색하는 일이 많다.
    // 그래서 order에다가 fk를 두었다.
    // fk가 order에 있는 delivery이기 때문에 연관관계 주인은 order에 있는 delivery로 잡아주면 된다.
    // fk가 있는 곳에 joincolumn을 주고 아닌 곳에는 mappedby해준다.
    // joincolumn은 order의 delivery에 mappedby는 delivery의 order에 해주면 된다.
    // order가 들어가면 delivery도 들어간다.
    // persist를 같이 해준다.
    @OneToOne(fetch = LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;


    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)        // EnumType은 무조건 String으로 한다.
    private OrderStatus status; //주문 상태 (order, cancel) 두가지 상태를 가진다.

    //==연관관계 메서드==//

    // member 클래스의 orders 와 order 클래스의 member가 다 : 1관계로 joincolumn이 되어 있다.
    // 따라서 order가 들어오면 orders에도 넣고 member도 넣어야 한다.
    // 이걸 한꺼번에 해주는게 setMember 메서드이다.(이건 양방향이기 때문이다.)
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    // order와 orderItems도 양방향 관계이다.
    // item 클래스의 order와 order 클래스의 orderItems도 양방향 관계이다.
    public void addorderItem(OrderItem orderItem){
        orderItems.add(orderItem);  // 다 대
        orderItem.setOrder(this);   // 1
    }

    //order 클래스와 delivery 클래스도 양방향 관계이다.
    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    // 다른 데서 각각의 delivery나 orderitem 등등의 값을 set 하는게 아니라
    // 애초에 객체가 생성이 되면 createOrder 함수가 실행이 되어서 그 객체에 값이 넣어진다.
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem: orderItems){
            order.addorderItem(orderItem);
        }

        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//

    //주문취소
    // 이미 주문한 상품은 cancel 못한다.
    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송완료된 상품은 취소 불가능");
        }

        this.setStatus(OrderStatus.CANCEL);
        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }


}
