package com.fp.finpoint.domain.member.entity;

import com.fp.finpoint.domain.file.entity.FileEntity;
import com.fp.finpoint.domain.invest.entity.Invest;
import com.fp.finpoint.domain.like.entity.Like;
import com.fp.finpoint.domain.oauth.OauthClient;
import com.fp.finpoint.domain.openbank.Entity.Token;
import com.fp.finpoint.domain.piece.Entity.PieceMember;
import com.fp.finpoint.global.audit.Auditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Member extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(unique = true, nullable = false)
    @Email
    @NotBlank
    private String email;

    private String password;

    private String salt;

    private String code;

    @Enumerated(EnumType.STRING)
    private OauthClient oauthClient;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();
  
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "token_id")
    private Token token;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Invest> invests = new ArrayList<>();

    private String fintech_use_num;

    private Long finPoint;

    private Long target_spend;

    private Integer goal;

    public void setGoal(Integer goal) {
        this.goal = goal;
    }

    @OneToOne
    @JoinColumn(name = "fileEntity_id")
    private FileEntity fileEntity;

    public void setFileEntity(FileEntity fileEntity) {
        this.fileEntity = fileEntity;
    }

    public void assignCode(String code) {
        this.code = code;
    }

    public void setFintech_use_num(String fintech_use_num) {
        this.fintech_use_num = fintech_use_num;
    }

    public void setToken(Token token) {
        this.token = token;
    }


    @OneToMany(mappedBy = "member")
    private List<Like> likes = new ArrayList<>(); //좋아요

    @OneToMany(mappedBy = "member")
    private List<PieceMember> pieceMembers = new ArrayList<>();


}
