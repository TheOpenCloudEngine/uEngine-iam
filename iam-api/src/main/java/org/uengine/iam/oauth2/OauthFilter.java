package org.uengine.iam.oauth2;

/**
 * Created by uengine on 2018. 1. 8..
 */
public interface OauthFilter {
    boolean preTokenIssue(AccessTokenResponse tokenResponse);

    void postTokenIssue(AccessTokenResponse tokenResponse);

    boolean preAuthorize(AuthorizeResponse authorizeResponse);

    void postAuthorize(AuthorizeResponse authorizeResponse);
}
