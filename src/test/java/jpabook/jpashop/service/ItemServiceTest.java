package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ItemServiceTest {
    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    EntityManager em;
    @Test
    public void 주문_상품_저장() throws Exception {
        //given
        Item book = new Book();
        book.setName("객체지향의 사실과 오해");
        book.setPrice(20000);
        book.addStock(1);

        //when
        itemService.saveItem(book);

        //then
        assertThat(itemService.findOne(book.getId())).isEqualTo(book);
    }

    @org.junit.Test(expected = IllegalStateException.class)
    public void 중복_제목_상품_등록() throws Exception {
        //given
        Item book1 = new Book();
        book1.setName("객체지향의 사실과 오해");
        book1.setPrice(20000);
        book1.addStock(1);

        Item book2 = new Book();
        book2.setName("객체지향의 사실과 오해");
        book2.setPrice(20000);
        book2.addStock(1);


        //when
        itemService.saveItem(book1);
        itemService.saveItem(book2);

        //-- 아래 코드 이전에 예외가 발생해야 테스트를 통과하는 코드 --//
        //then
        fail("예외가 발생해야 합니다"); // 수행이 되면 안되는 지점 (오류)
    }
}