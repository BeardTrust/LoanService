package com.beardtrust.webapp.loanservice.repos;

import com.beardtrust.webapp.loanservice.entities.CardEntity;
import com.beardtrust.webapp.loanservice.entities.CurrencyValue;
import com.beardtrust.webapp.loanservice.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * This class gives access to the cards in the database.
 *
 * @author Matthew Crowell <Matthew.Crowell@Smoothstack.com>
 */
@Repository
public interface CardRepository extends JpaRepository<CardEntity, String> {
	Page<CardEntity> findAll(Pageable page);

	Page<CardEntity> findAllByIdOrUser_UserIdOrCardNumberOrCardType_TypeNameOrNicknameContainsIgnoreCase(String id,
																										 String userId,
																										 String CardNumber,
																										 String cardTypeName,
																										 String nickname,
																										 Pageable page);

	Page<CardEntity> findAllByUser_UserIdEqualsAndBalanceOrBillCycleLengthOrInterestRateEquals(String userid,
																							   CurrencyValue balance,
																							   int billCycleLength,
																							   Double interestRate,
																							   Pageable page
																						  );

	Page<CardEntity> findAllByBalanceOrInterestRateEquals(CurrencyValue balance,
														  Double interestRate,
														  Pageable pageable);

	Page<CardEntity> findAllByCreateDateOrExpireDateEquals(LocalDateTime createDate, LocalDateTime expireDate, Pageable pageable);

	/**
	 * This method accepts a user entity as an argument and returns the list of
	 * all cards associated with that user as found in the database.
	 *
	 * @param user UserEntity the user id to search for
	 * @return List<CardEntity> the list of cards associated with the user id
	 */
	Page<CardEntity> findAllByUser(UserEntity user, Pageable pageable);

	@Modifying
	@Query(value = "update cards set active_status=false where id=?1", nativeQuery = true)
	void deactivateById(String cardId);

	Page<CardEntity> findAllByActiveStatusTrue(Pageable page);

	Page<CardEntity> findAllByUser_UserIdEqualsAndCreateDateOrExpireDateEquals(String UserId, LocalDateTime createDate, LocalDateTime expireDate, Pageable pageable);

	Page<CardEntity> findAllByUser_UserIdEqualsAndIdOrNicknameOrCardType_TypeNameContainsIgnoreCase(String userId,
																									String id,
																									String nickname,
																									String cardTypeName,
																									Pageable page);
}
