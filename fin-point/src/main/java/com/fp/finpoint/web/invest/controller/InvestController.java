package com.fp.finpoint.web.invest.controller;

import com.fp.finpoint.domain.file.service.InvestFileService;
import com.fp.finpoint.domain.invest.dto.InvestDto;
import com.fp.finpoint.domain.invest.entity.Invest;
import com.fp.finpoint.domain.invest.service.InvestService;
import com.fp.finpoint.domain.like.service.LikeService;
import com.fp.finpoint.domain.piece.Entity.Piece;
import com.fp.finpoint.global.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;

@Slf4j
@RequestMapping("/finpoint")
@Controller
@RequiredArgsConstructor
public class InvestController {

    private final InvestService investService;
    private final InvestFileService investFileService;
    private final LikeService likeService;

    @GetMapping("/invest/list")
    public String list(Model model,
                       @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                       String searchKeyword) {

        Page<Invest> list;

        if (searchKeyword == null) {
            list = investService.investList(pageable);
        } else {
            list = investService.investSearchList(searchKeyword, pageable);
        }
        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "invest_list";
    }

    @GetMapping(value = "/invest/list/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id,HttpServletRequest request) {
        Invest readInvestDetail = investService.readInvestDetail(id);
        String email = CookieUtil.getEmailToCookie(request);
        boolean like = likeService.findLike(id, email);
        Piece savedPiece = readInvestDetail.getPiece();
        model.addAttribute("investDetail", readInvestDetail);
        model.addAttribute("like",like);
        model.addAttribute("piece", savedPiece);
        return "invest_detail";
    }

    @GetMapping("/invest/create")
    public String listCreate(InvestDto investDto) {
        return "invest_create";
    }

    @PostMapping("/invest/create")
    public String listCreate(@ModelAttribute InvestDto investDto,
                             @RequestParam MultipartFile file,
                             HttpServletRequest request) throws IOException {

        Long fileEntity = investFileService.saveFile(file);
        log.info("파일 저장 fullPath={}", file);
        String email = CookieUtil.getEmailToCookie(request);
        investService.create(investDto, email, fileEntity);
        return "redirect:/finpoint/invest/list";
    }

    // 글 삭제.
    @GetMapping("/invest/delete/{id}")
    public String boardDelete(@PathVariable("id") Long id) {
        investService.deleteInvest(id);

        return "redirect:/finpoint/invest/list";
    }

    @GetMapping("/invest/modify/{id}")
    public String modifyInvest(@PathVariable("id") Long id, Model model) {
        Invest modifyInvest = investService.readInvestDetail(id);
        model.addAttribute("modify", modifyInvest);

        return "invest_modify";
    }

    @PostMapping("/invest/update/{id}")
    public String investUpdate(@PathVariable("id") Long id, Model model, InvestDto investDto) {

        investService.updateInvest(investDto,id);

        model.addAttribute("message", "수정 되었습니다.");
        model.addAttribute("SearchUrl", "/finpoint/invest/list");

        return "Message";
    }

    @PostMapping("/invest/list/detail/{id}/buy")
    public String test(@PathVariable("id") Long id, HttpServletRequest request, @RequestParam("count") Long count) {
        investService.purchase(id, request, count);
        log.info("count={}",count);
        return "redirect:/finpoint/invest/list";
    }

    @ResponseBody
    @GetMapping("/invest/image/{id}")
    public Resource investImage(@PathVariable("id") Long id) throws MalformedURLException {
        return investFileService.getInvestImageUrl(id);
    }

}

