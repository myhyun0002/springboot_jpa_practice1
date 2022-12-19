package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {
    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    // 다 대 다 관계에서는
    // 1대 다 그리고 다 대 1로 서로 나누어서 연결해줘야 한다.
    // 그래서 category_item이라는 두 개의 join을 하기 위해나 table을 생성해주고
    // category와 item을 두개를 joincolumn 해주는 구문을 넣어준다.
    @ManyToMany
    @JoinTable(name = "category_item",
    joinColumns = @JoinColumn(name = "category_id"),
    inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();

    // 내 자신을 부모로 가진다.
    // 부모 하나에다가 자식 여러명이니까
    // parent는 ManyToOne
    // child는 OneToMany
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    //==연관관계 메서드==//
    public void addChileCategory(Category child){
        this.child.add(child);      // 부모의 child 모음에 넣고
        child.setParent(this);      // child에도 부모라고 설정해준다.
    }
}
