package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    // 기본적으로 Transactional 옵션이 있기에 db에 적용을 안시키고 rollback을 시킨다.
    // 따라서 rollback이 되면 우리가 알 수 없기에
    // em.flush()라는 함수를 쓰면 볼 수 있다.
    // flush()는 지금까지 한 걸 commit한다는 뜻이다.
    public void 회원가입() throws Exception{
        //given (이런게 주어질 때) - 새로운 회원을 등록한다.
        Member member = new Member();
        member.setName("kim");

        //when (이런 상황에서는) - 멤버를 db에 등록한다.
        Long saveId = memberService.join(member);

        //then (이런 결과가 나온다.) - db에 등록한 멤버가 방금 받아온 member가 같은지 본다.
        em.flush();
        assertEquals(member,memberRepository.findOne(saveId));
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception{
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        memberService.join(member2);

        //then
        fail("예외가 발생해야 한다.");
    }
}