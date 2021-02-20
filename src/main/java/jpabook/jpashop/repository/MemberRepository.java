package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository // 컴포넌트 스캔에 의해 스프링 빈으로 관리
@RequiredArgsConstructor // final 되어있는 것만 생성자를 생성.
public class MemberRepository {

    //@PersistenceContext
    //@Autowired -> 스프링부트가 지원하는 JPA(스프링 부트 데이터 JPA)로 가능함!!
    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        // 단권 조회
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        // 첫번째 인자는 JPQL, 두번째 인자는 반환 타입!
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
