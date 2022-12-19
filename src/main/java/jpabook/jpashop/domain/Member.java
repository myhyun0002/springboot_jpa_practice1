package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    // order 클래스에 있는 member column에 값을 변경하면 여기에 그대로 적용이 된다.
    // 거울 역할로 그냥 order 클래스의 member column의 내용을 보여주는 역할만 할 뿐 값을 바꿀 수 없고 바꿔도
    // db에 적용이 되지 않는다.
    private List<Order> orders = new ArrayList<Order>();


}
