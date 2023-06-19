package com.fp.finpoint.domain.invest.dto;

import com.fp.finpoint.domain.invest.entity.Invest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvestDto {
    private String subject;
    private String content;
    private String pieceName;
    private Long piecePrice;
    private Long pieceCount;
    public Invest toEntity() {
        return new Invest(this.subject, this.content);
    }
}
