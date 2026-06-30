package com.iis.projekat.repository.Beneficiary;

import com.iis.projekat.dto.Beneficiary.SurveyCommentDto;
import com.iis.projekat.model.Beneficiary.BeneficiarySurvey;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeneficiarySurveyRepository extends JpaRepository<BeneficiarySurvey,Long> {
    @Query("""
    SELECT COUNT(s)
    FROM BeneficiarySurvey s
""")
    long countSurveys();

    @Query("""
    SELECT AVG(s.rating)
    FROM BeneficiarySurvey s
""")
    Double averageRating();

    @Query("""
    SELECT s.distribution.id, AVG(s.rating)
    FROM BeneficiarySurvey s
    GROUP BY s.distribution.id
""")
    List<Object[]> averageRatingPerDistribution();

    @Query("""
    SELECT FLOOR(s.rating), COUNT(s)
    FROM BeneficiarySurvey s
    GROUP BY FLOOR(s.rating)
""")
    List<Object[]> ratingDistribution();

    @Query("""
    SELECT new com.iis.projekat.dto.Beneficiary.SurveyCommentDto(
        s.distribution.id,
        s.comment,
        s.rating
    )
    FROM BeneficiarySurvey s
    WHERE s.comment IS NOT NULL
    ORDER BY s.id DESC
""")
    List<SurveyCommentDto> latestComments(Pageable pageable);

    @Query("""
    SELECT b.type, AVG(s.rating)
    FROM BeneficiarySurvey s
    JOIN s.beneficiary b
    GROUP BY b.type
""")
    List<Object[]> averageRatingPerAidType();
}
