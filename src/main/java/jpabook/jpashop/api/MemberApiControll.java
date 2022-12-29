package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiControll {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    // 위가 아니라 밑에 껄로 작성해라.
    @GetMapping("api/v2/members")
    public Result memberV2(){
        List<Member> fineMembers = memberService.findMembers();
        List<MemberDto> collect = fineMembers.stream()
                .map(m -> new MemberDto(m.getId(),m.getName(),m.getAddress()))
                .collect(Collectors.toList());
        return new Result(collect.size(),collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private Long id;
        private String name;
        private Address address;

    }


    // 회원 input
    @PostMapping("api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    // 회원 input
    //위처럼 만들지 말고 밑에처럼 만들어라.
    // entity를 매개변수로 사용하지 마라. entity는 언제든지 바뀔 수 있기 때문이다.
    // 별도의 CreateMemberRequest 클래스를 만들고 이 클래스에 받아올 것들을 정의한 뒤 받아온 다음
    // Member 객체를 새로 생성해서 집어 넣은 다음 다시 em.persist 하면 된다.
    @PostMapping("/api/v2/members")
    public CreateMemberResponse sveMemberV2(@RequestBody @Valid CreateMemberRequest request){

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    // 회원 아이디로 조회해서 이름 update
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){
        memberService.update(id,request.getName());
        Member findMember = memberService.finOne(id);
        return new UpdateMemberResponse(findMember.getId(), request.getName());
    }



    @Data
    static class CreateMemberRequest{
        private String name;
    }

    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    static class UpdateMemberRequest{
        @NotEmpty
        private String name;
    }


}
