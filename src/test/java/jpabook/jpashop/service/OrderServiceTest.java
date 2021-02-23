package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.em.OrderStatus;
import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Fail.fail;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private EntityManager em;

    @Test
    public void 상품주문() throws Exception {
        //given
        //Member
        Member member = createMember();
        //delivery
//        Delivery delivery = new Delivery();
//        delivery.setAddress(member.getAddress());

        //Item
        Item item = createItem("IU", 10, 10000);

        //orderItem
        //OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), 2);
        // order
        //Order order = Order.createOrder(member, delivery, orderItem);

        //when (멤버하고 아이템만 있으면 된다!)
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야한다.", 1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량 이다.", 10000 * orderCount, getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야한다.", 8, item.getStockQuantity());
    }

    /**
     * removeStock에서 item에 재고가 없을 경우 예외 발생 테스트
     * @throws Exception
     */
    @Test(expected = NotEnoughStockException.class) // 커스텀 예외
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember();
        Item item = createItem("IU", 10, 10000);

        int orderCount = 11;

        //when
        orderService.order(member.getId(), item.getId(), orderCount);
        //then
        fail("재고 수량 예외가 발생해야 합니다.");
    }


    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Item item = createItem("IU", 10, 10000);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        //when
        orderService.cancleOrder(orderId);


        //then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals("주문 취소 시 상태는 CANCLE 이다.", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문이 취소 된 상품은 재고가 원복되어야 한다.", 10, item.getStockQuantity());
    }



    private Item createItem(String name, int stockQuantity, int price) {
        Item item = new Album();
        item.setName(name);
        item.setStockQuantity(stockQuantity);
        item.setPrice(price);
        em.persist(item);
        return item;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("park");
        Address address = new Address("seoul", "Doillo", "01188");
        member.setAddress(address);
        em.persist(member);
        return member;
    }
}