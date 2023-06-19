package com.fp.finpoint.domain.invest.entity;

import com.fp.finpoint.domain.file.entity.FileEntity;
import com.fp.finpoint.domain.like.entity.Like;
import com.fp.finpoint.domain.member.entity.Member;
import com.fp.finpoint.domain.piece.Entity.Piece;
import com.fp.finpoint.global.audit.Auditable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
public class Invest extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fileEntity_id")
    private FileEntity fileEntity;

    @Column(columnDefinition = "integer default 0")
    private Integer readCount;

    private String category;

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member;

    @OneToMany(mappedBy = "invest", cascade = CascadeType.REMOVE)
    private List<Like> likes = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "piece_id")
    private Piece piece;

    public Invest(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }

    @Column(columnDefinition = "integer default 0")
    private Integer likeCnt;

    public void setMember(Member member) {
        this.member = member;
        member.getInvests().add(this);
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void read() {
        this.readCount++;
    }
}
