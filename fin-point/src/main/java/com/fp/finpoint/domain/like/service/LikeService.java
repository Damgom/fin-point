package com.fp.finpoint.domain.like.service;

import com.fp.finpoint.domain.invest.entity.Invest;
import com.fp.finpoint.domain.invest.repository.InvestRepository;
import com.fp.finpoint.domain.like.entity.Like;
import com.fp.finpoint.domain.like.repository.LikeRepository;
import com.fp.finpoint.domain.member.entity.Member;
import com.fp.finpoint.domain.member.repository.MemberRepository;
import com.fp.finpoint.global.exception.BusinessLogicException;
import com.fp.finpoint.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final InvestRepository investRepository;

    public boolean findLike(Long investId, String email) {
        memberRepository.findByEmail(email);
        return likeRepository.existsByInvest_IdAndMember_Email(investId, email);

    }

    @Transactional
    public boolean saveLike(Long investId, String email){
        if (!findLike(investId,email)) {
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
            Invest invest = investRepository.findById(investId)
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.INVEST_NOT_FOUND));
            Like like = new Like(member, invest);
            likeRepository.save(like);
            investRepository.doLike(investId);
            return true;
        } else {
            likeRepository.deleteByInvest_IdAndMember_Email(investId,email);
            investRepository.disLike(investId);
            return false;
        }
    }
}


