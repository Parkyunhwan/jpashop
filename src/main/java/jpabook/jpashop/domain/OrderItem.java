package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; // 하나의 아이템에 여러 개의 주문아이템을 가짐. (나이키 신발 -> 주문한 게 다 다르므로)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; // 하나의 오더가 여러개의 오더 아이템(하나의 결제에 여러 개의 아이템)을 가짐

    private int OrderPrice; // 주문 당시 가격
    private int count; // 주문 수

    protected OrderItem() { //제약용
        // 생성자 스타일의 생성을 막는 것 (똑같은 스타일 유지를 위해 사용)
    }

    //==생성 메서드==//

    /**
     * orderItem 을 생성과 생성하면서 수행해야할 비즈니스 로직 처리
     * @param item
     * @param orderPrice
     * @param count
     * @return orderItem
     */
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count); // 상품을 주문했으므로 상품 재고에서 count만큼 감소 시킴
        return orderItem;
    }


    //==비즈니스로직==//

    /**
     * 주문 상품 취소
     * - 상품 재고 증가
     */
    public void cancle() {
        getItem().addStock(count); // lombok의 getter 사용 + Item의 비즈니스 로직 사용 (재고 증가)
    }

    public int getTotalPrice() {
        return (getOrderPrice() * getCount());
    }
}