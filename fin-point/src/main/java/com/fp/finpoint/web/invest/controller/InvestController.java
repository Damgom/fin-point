package com.fp.finpoint.web.invest.controller;

import com.fp.finpoint.domain.invest.entity.Invest;
import com.fp.finpoint.domain.invest.entity.InvestDto;
import com.fp.finpoint.domain.invest.service.InvestService;
import com.fp.finpoint.domain.member.service.MemberService;
import com.fp.finpoint.global.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@RequestMapping("/invest")
@Controller
@RequiredArgsConstructor // DI 주입. (InvestService)
@Slf4j
public class InvestController {

    private final InvestService investService;
    private final MemberService memberService;

    // 전체 리스트 페이지.
    @GetMapping("/list")
    public String list (Model model,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                        String searchKeyword) {
        Page<Invest> list = null;

        if(searchKeyword == null) {
            list = investService.investList(pageable);
        }else {
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

    // 디테일 페이지.
    @GetMapping(value = "/list/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id) {
        Invest investDetail = investService.investDetail(id);
        model.addAttribute("investDetail", investDetail); // model 을 통해 view(화면)에서 사용 가능하게 함.

        return "invest_detail";
    }

    @GetMapping("/form")
    public String getList(@ModelAttribute InvestDto investDto, Model model) throws UnsupportedEncodingException {
        model.addAttribute("invest", investDto);
        return "invest_create";
    }

    @PostMapping("/create")
    public String listCreate(@ModelAttribute InvestDto investDto, HttpServletRequest request) throws UnsupportedEncodingException {
        String email = CookieUtil.getEmailToCookie(request);
        investService.create(investDto, email);
        return "redirect:/invest/list";
    }

    // 글 삭제.
    @GetMapping ("/delete/{id}")
    public String boardDelete(@PathVariable("id") Long id){
        investService.deleteInvest(id);

        return "redirect:/invest/list";
    }

    // 글 수정.
    @GetMapping("/modify/{id}")
    public String modifyInvest(@PathVariable("id") Long id, Model model) {
        Invest modifyInvest = investService.investDetail(id);
        model.addAttribute("modify", modifyInvest);

        return "invest_modify";
    }

    @PostMapping("/update/{id}")
    public String investUpdate(@PathVariable("id") Long id, Model model, InvestDto investDto) {

        this.investService.updateInvest(investDto);

        model.addAttribute("message", "수정 완료");
        model.addAttribute("SearchUrl", "/invest/list");

        return  "Message";
    }




}
