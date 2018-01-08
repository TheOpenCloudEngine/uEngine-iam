package org.uengine.iam.oauthtoken;

/**
 * Created by uengine on 2017. 12. 21..
 */

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "code", path = "code")
public interface OauthCodeRepository extends PagingAndSortingRepository<OauthCode, Long> {

    OauthCode findByCode(@Param("code") String code);

    OauthCode findByCodeAndClientKey(@Param("code") String code, @Param("clientKey") String clientKey);
}