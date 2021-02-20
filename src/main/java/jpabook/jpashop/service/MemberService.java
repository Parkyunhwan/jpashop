package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
//@AllArgsConstructor
@RequiredArgsConstructor // final로 되어있는 것만 생성자를 만들어준다!!!!
public class MemberService {


    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     * @param member
     * @return member.id
     */
    @Transactional // default -> readonly = false
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복회원 검증 - 비즈니스 로직
        memberRepository.save(member);
        return member.getId(); // 키 값은 항상 존재한다.
    }

    public void validateDuplicateMember(Member member) {
        // 중복 회원 EXCEPTION 처리
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }


    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 단권 조회
    public Member findOne(Long id) {
        return memberRepository.findOne(id);
    }
}
