package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class Delivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    // 하나의 주문에는 하나의 배송이니가
    // Order과 Delivery는 1대1 관계
    @OneToOne(mappedBy = "delivery",fetch = LAZY)
    private Order order;

    // 내장 타입인 Address라는 내가 만든 타입을 쓰겠다는 의미
    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)    //
    private DeliveryStatus status;  // Ready, Comp 두가지 값이 있다.


}

