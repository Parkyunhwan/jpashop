package jpabook.jpashop.domain;

import jpabook.jpashop.domain.em.DeliveryStatus;
import jpabook.jpashop.domain.em.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 다 대 1 관계 (주문은 하나의 멤버와 연결됨)
    @JoinColumn(name = "member_id") // 매핑을 어떤 컬럼으로 할 것인가 FK가 무엇이 될것인가.
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 하나의 주문은 하나의 배송정보만을 가진다.
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문시각 (HIBERNATE 생성)

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ORDER, CANCCEL]

    //==연관관계 편의 메서드
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    // Order의 주문 상품 리스트에 주문상품을 추가 + 주문상품에 주문을 추가 => 이 행동을 Atomic하게 설정.
    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    protected Order() { //제약용
        // 생성자 스타일의 생성을 막는 것 (똑같은 스타일 유지를 위해 사용)
    }
    // Order안에 Delivery도 들어가고 OrderItem도 들어가는 등 생성할 게 많아 매우 복잡하다. 생성메서드 사용
    //==생성 메서드==/

    /**
     * 주문 생성에 대한 비즈니스 로직을 모아둠 (응집)
     * @param member
     * @param delivery
     * @param orderItems
     * @return
     */
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //== 비즈니스 로직 ==/
    /**
     * 주문 취소
     */
    public void cancle() {
        if (delivery.getDeliveryStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) { // Order 객체 내 orderItems
            orderItem.cancle(); // 각각의 상품에 대해서 cancle 상태로 만들어 주는 것
        }
    }

    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            //totalPrice += orderItem.getOrderPrice(); xxx (주문 수량을 곱하지 않은 가격)
            //사실 getCount()해서 Order내에서 처리해도 되지만 그건 객체의 자율성을 침해한다고 볼 수 있다.
            //OrderItem의 비즈니스 로직에서 구현 하도록 하자.
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
