package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class) // junit이 spring과 함께 실행
@SpringBootTest // 스프링 컨테이너와 함께하는 테스트
@Transactional
public class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private EntityManager em;

    @Test
    public void 회원가입() throws Exception {
        //given - 주어진 것
        Member member = new Member();
        member.setName("yunhwan");

        //when - 이것을 실행하면
        Long savedId = memberService.join(member);

        //then - 이것이 나와야한다 (검증)
        // Assertions.assertThat
        //em.flush(); // 강제로 쿼리를 날림 (롤백과 상관없이)
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예약() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim1");

        Member member2 = new Member();
        member2.setName("kim1");


        //when
        memberService.join(member1);
        memberService.join(member2); //예외가 터져야 함. Exception이 튀어나오게됨!!


        //-- 아래 코드 이전에 예외가 발생해야 테스트를 통과하는 코드 --//
        //then
        fail("예외가 발생해야 합니다"); // 수행이 되면 안되는 지점 (오류)
    }
}