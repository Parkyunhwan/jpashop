package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     * 주문 화면에서 넘겨주는 값은 회원 정보, 상품명, 주문 수량이다. (회원ID, 상품ID, 주문수량)
     * @param memberId
     * @param itemId
     * @param count
     * @return
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송 정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문 상품 생성
        // static 이므로 OrderItem으로 바로 생성자메서드 호출 가능
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        /*
         * 만약 생성메서드 스타일로 생성할 것이라면 다른 스타일로 하지 못하게 해야 유지보수성이 좋아진다.
         */

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장 -> cascade 옵션에의해 배송정보와, 주문 상품도 함께 DB에 저장된다.
        orderRepository.save(order);
        return order.getId();

    }

    // 취소

    /**
     *
     * @param orderId
     */
    @Transactional
    public void cancleOrder(Long orderId) {
        Order order = orderRepository.findOne(orderId);

        //주문 취소
        // JPA가 스스로 Update 쿼리를 날려줌..매우 편안.. (원래는 직접 업데이트 쿼리를 날려줘야함)
        order.cancle();
    }

    // 검색
    /*public List<Order> findOrders(OrderSearch) {
        return orderRepository.finaAll(OrderSearch);
    }*/
}
