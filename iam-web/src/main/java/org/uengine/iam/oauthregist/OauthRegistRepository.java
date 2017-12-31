package org.uengine.iam.oauthregist;

/**
 * Created by uengine on 2017. 12. 21..
 */

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.uengine.iam.notification.NotificationType;


@RepositoryRestResource(collectionResourceRel = "regist", path = "regist")
public interface OauthRegistRepository extends PagingAndSortingRepository<OauthRegist, Long> {


    @Query("select r from OauthRegist r where r.clientKey = :clientKey and r.token = :token and r.notification_type = :notification_type")
    OauthRegist findByClientKeyAndTokenAndType(
            @Param("clientKey") String clientKey,
            @Param("token") String token,
            @Param("notification_type") NotificationType notification_type);

    OauthRegist findByToken(@Param("token") String token);
}