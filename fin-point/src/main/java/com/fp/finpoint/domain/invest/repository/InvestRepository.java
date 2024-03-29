package com.fp.finpoint.domain.invest.repository;

import com.fp.finpoint.domain.invest.entity.Invest;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface InvestRepository extends JpaRepository<Invest, Long> {

    Page<Invest> findBySubjectContaining(String searchKeyword, Pageable pageable);

    @Modifying
    @Query(value = "update Invest invest set invest.likeCnt = invest.likeCnt + 1 where invest.id = :invest_id")
    void doLike(@Param("invest_id") Long invest_id);

    @Modifying
    @Query(value = "update Invest invest set invest.likeCnt = invest.likeCnt - 1 where invest.id = :invest_id")
    void disLike(@Param("invest_id") Long invest_id);

    @Modifying
    @Query("UPDATE Invest i SET i.fileEntity.id = :fileEntityId WHERE i.id = :investId")
    void updateFileId(@Param("investId") Long investId, @Param("fileEntityId") Long fileEntityId);
}
