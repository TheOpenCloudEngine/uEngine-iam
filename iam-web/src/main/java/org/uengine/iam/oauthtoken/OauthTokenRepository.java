package org.uengine.iam.oauthtoken;

/**
 * Created by uengine on 2017. 12. 21..
 */

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@RepositoryRestResource(collectionResourceRel = "token", path = "token")
public interface OauthTokenRepository extends PagingAndSortingRepository<OauthAccessToken, Long> {

    OauthAccessToken findByToken(@Param("token") String token);

    OauthAccessToken findByOldRefreshToken(@Param("oldRefreshToken") String oldRefreshToken);

    OauthAccessToken findByRefreshToken(@Param("refreshToken") String refreshToken);

    @Query("select t from OauthAccessToken t where t.clientKey = :clientKey and t.type = :tokenType and t.regDate < :expirationTime")
    Page<OauthAccessToken> findExpiredToken(
            @Param("clientKey") String clientKey,
            @Param("expirationTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    Date expirationTime,
            @Param("tokenType") String tokenType,
            Pageable pageable);
    //void deleteExpiredToken(String clientId, Long expirationTime, String tokenType);
}