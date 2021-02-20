package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

// item 과 다 대 다 관계
@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    // category와 다 대 다 관계 -> 조인 테이블이 필요하다!!!
    // 객체는 컬렉션을 가질 수 있지만 관계형 DB는 컬렉션 관계를 양쪽에 가질 수 없기 때문에
    // 1 대 다, 다 대 1 로 풀어내는 중간 테이블이 있어야 한다.
    @ManyToMany
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY) // 내 부모는 하나 형제는 여럿
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent") // 나 자신은 하나 내 자식은 여럿.
    private List<Category> child = new ArrayList<>();

    //==연관관계 편의 메서드==//
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }
}
