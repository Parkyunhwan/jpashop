package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService { // 단순히 레포지토리에 위임하는 서비스 로직..

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        validateDuplicateMember(item.getName());
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, String name, int price, int Quantity) {
        Item findItem = itemRepository.findOne(itemId);
        findItem.setPrice(price);
        // 트랜잭션 안에서 다시 조회한 객체를 변경 하면 커밋 시점에 더티 체킹에 의해 DB에 UPDATE 쿼리가 날라간다.
    }

    public void validateDuplicateMember(String name) {
        // 중복 회원 EXCEPTION 처리
        List<Item> findItems = itemRepository.findByName(name);
        if (!findItems.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    public List<Item> findItem() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
