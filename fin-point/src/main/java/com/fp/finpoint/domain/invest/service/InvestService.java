package com.fp.finpoint.domain.invest.service;

import com.fp.finpoint.domain.file.entity.FileEntity;
import com.fp.finpoint.domain.file.service.FileService;
import com.fp.finpoint.domain.invest.dto.InvestDto;
import com.fp.finpoint.domain.invest.entity.Invest;
import com.fp.finpoint.domain.invest.repository.InvestRepository;
import com.fp.finpoint.domain.member.entity.Member;
import com.fp.finpoint.domain.member.repository.MemberRepository;
import com.fp.finpoint.domain.piece.Entity.Piece;
import com.fp.finpoint.domain.piece.Entity.PieceMember;
import com.fp.finpoint.domain.piece.repository.PieceMemberRepository;
import com.fp.finpoint.global.exception.BusinessLogicException;
import com.fp.finpoint.global.exception.ExceptionCode;
import com.fp.finpoint.global.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestService {

    private final InvestRepository investRepository;
    private final MemberRepository memberRepository;
    private final PieceMemberRepository pieceMemberRepository;
    private final FileService fileService;

    @Transactional
    public Invest readInvestDetail(Long id) {
        Invest savedInvest = investRepository.findById(id).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.INVEST_NOT_FOUND));
        savedInvest.read();
        return savedInvest;
    }

    @Transactional
    public void create(InvestDto investDto, String email, MultipartFile file) throws IOException {
        Member findMember = memberRepository.findByEmail(email).orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        Invest invest = investDto.toEntity();
        invest.setMember(findMember);
        Piece piece = new Piece(investDto.getPieceName(), investDto.getPiecePrice(), investDto.getPieceCount(), generateUuid());
        invest.setPiece(piece);
        FileEntity savedFile = fileService.saveFile(file);
        invest.setFileEntity(savedFile);
        investRepository.save(invest);
    }

    public void deleteInvest(Long id) {
        investRepository.deleteById(id);
    }

    @Transactional
    public void updateInvest(InvestDto investDto, Long id){
       Invest invest = investRepository.findById(id).orElseThrow(()-> new BusinessLogicException(ExceptionCode.INVEST_NOT_FOUND));
        invest.setSubject(investDto.getSubject());
        invest.setContent(investDto.getContent());
    }

    public Resource getInvestImageUrl(Long id) throws MalformedURLException {
        Invest invest = investRepository.findById(id).orElseThrow(() -> new BusinessLogicException(ExceptionCode.INVEST_NOT_FOUND));
        return fileService.getInvestImageUrl(invest);
    }

    public Page<Invest> investList(Pageable pageable){
        return investRepository.findAll(pageable);
    }

    public Page<Invest> investSearchList(String searchKeyword,Pageable pageable){
        return investRepository.findBySubjectContaining(searchKeyword,pageable);

    }

    @Transactional
    public void purchase(Long id, HttpServletRequest request, Long count) {
        String email = CookieUtil.getEmailToCookie(request);
        Member savedMember = memberRepository.findByEmail(email).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        Invest invest = investRepository.findById(id).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.INVEST_NOT_FOUND));
        Piece savedPiece = invest.getPiece();
        if (savedPiece.getCount() < count) {
            throw new BusinessLogicException(ExceptionCode.PIECE_NOT_ENOUGH);
        }
//        if (savedMember.getFinPoint() < savedPiece.getPrice() * count) {
//            throw new BusinessLogicException(ExceptionCode.NOT_ENOUGH_POINT);
//        }
        savedPiece.updateCount(count);
        Piece newPiece = getNewPiece(count, savedMember, savedPiece);
        mapPieceAndMember(newPiece, savedMember);
    }

    private Piece getNewPiece(Long count, Member savedMember, Piece savedPiece) {
        String pieceSerial = savedPiece.getUuid();
        Piece newPiece = new Piece(savedPiece.getName(), savedPiece.getPrice(), count, pieceSerial);
        List<PieceMember> savedPieceMember = pieceMemberRepository.findByMember_memberId(savedMember.getMemberId());
        for (PieceMember pieceMember : savedPieceMember) {
            Piece piece = pieceMember.getPiece();
            if (piece.getUuid() != null && piece.getUuid().equals(pieceSerial)) {
                newPiece = piece;
                newPiece.plusCount(count);
                break;
            }
        }
        return newPiece;
    }

    @Transactional
    public void mapPieceAndMember(Piece piece, Member member) {
        PieceMember pieceMember = new PieceMember(member, piece);
        pieceMember.setMember(member);
        pieceMember.setPiece(piece);
        boolean isPieceMember = pieceMemberRepository.existsByPieceIdAndMember_MemberId(piece.getId(), member.getMemberId());
        if (!isPieceMember) {
            pieceMemberRepository.save(pieceMember);
        }
    }

    private String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
