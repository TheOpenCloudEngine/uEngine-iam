package org.uengine.iam.oauthuser;

import org.springframework.stereotype.Service;

@Service
public class OauthUserServiceImpl {//implements OauthUserService {
//    @Autowired
//    @Qualifier("config")
//    private Properties config;
//
//    @Autowired
//    private OauthUserRepository oauthUserRepository;
//
//    @Autowired
//    ConfigurationHelper configurationHelper;
//
//    @Autowired
//    ManagementService managementService;
//
//    @Autowired
//    OauthClientService clientService;
//
//    @Override
//    public OauthUser selectById(String id) {
//        return oauthUserRepository.selectById(id);
//    }
//
//    @Override
//    public OauthUser selectByName(String userName) {
//        return oauthUserRepository.selectByName(userName);
//    }
//
//    @Override
//    public List<OauthUser> selectAllByManagementId(String managementId) {
//        return oauthUserRepository.selectAllByManagementId(managementId);
//    }
//
//    @Override
//    public List<OauthUser> selectByManagementId(String managementId, int limit, Long skip) {
//        return oauthUserRepository.selectByManagementId(managementId, limit, skip);
//    }
//
//    @Override
//    public List<OauthUser> selectByManagementLikeUserName(String managementId, String userName, int limit, Long skip) {
//        return oauthUserRepository.selectByManagementLikeUserName(managementId, userName, limit, skip);
//    }
//
//    @Override
//    public Long countAllByManagementId(String managementId) {
//        return oauthUserRepository.countAllByManagementId(managementId);
//    }
//
//    @Override
//    public Long countAllByManagementIdLikeUserName(String managementId, String userName) {
//        return oauthUserRepository.countAllByManagementIdLikeUserName(managementId, userName);
//    }
//
//    @Override
//    public OauthUser selectByManagementIdAndUserName(String managementId, String userName) {
//        return oauthUserRepository.selectByManagementIdAndUserName(managementId, userName);
//    }
//
//    @Override
//    public OauthUser selectByManagementIdAndCredential(String managementId, String userName, String userPassword) {
//        return oauthUserRepository.selectByManagementIdAndCredential(managementId, userName, userPassword);
//    }
//
//    @Override
//    public OauthUser selectByManagementIdAndId(String managementId, String id) {
//        return oauthUserRepository.selectByManagementIdAndId(managementId, id);
//    }
//
//    @Override
//    public OauthUser updateById(OauthUser oauthUser) {
//        return oauthUserRepository.updateById(oauthUser);
//    }
//
//    @Override
//    public void deleteById(String id) {
//        oauthUserRepository.deleteById(id);
//    }
//
//    @Override
//    public OauthUser createUser(String managementId, OauthUser oauthUser) {
//        oauthUser.setManagementId(managementId);
//        oauthUserRepository.insert(oauthUser);
//        return oauthUser;
//    }
//
//    @Override
//    public OauthSessionToken validateSessionToken(String sessionToken) throws Exception {
//
//        OauthSessionToken oauthSessionToken = new OauthSessionToken();
//
//        JWTClaimsSet jwtClaimsSet = JwtUtils.parseToken(sessionToken);
//
//        //세션토큰은 이슈발급자가 매니지먼트 키
//        String managementKey = jwtClaimsSet.getIssuer();
//
//        Management management = managementService.selectByKey(managementKey);
//        String sharedSecret = management.getManagementJwtSecret();
//
//        Map context = (Map) jwtClaimsSet.getClaim("context");
//        String userName = (String) context.get("userName");
//
//        //만료시간
//        Date issueTime = jwtClaimsSet.getIssueTime();
//        Date expirationTime = new Date(issueTime.getTime() + management.getSessionTokenLifetime() * 1000);
//
//        boolean validated = JwtUtils.validateToken(sessionToken, expirationTime);
//
//        oauthSessionToken.setToken(sessionToken);
//        oauthSessionToken.setValidated(validated);
//        oauthSessionToken.setUserName(userName);
//
//        return oauthSessionToken;
//    }
//
//    @Override
//    public OauthScopeToken validateScopeToken(String scopeToken) throws Exception {
//        OauthScopeToken oauthScopeToken = new OauthScopeToken();
//
//        JWTClaimsSet jwtClaimsSet = JwtUtils.parseToken(scopeToken);
//
//        //세션토큰은 이슈발급자가 매니지먼트 키
//        String managementKey = jwtClaimsSet.getIssuer();
//
//        Management management = managementService.selectByKey(managementKey);
//        String sharedSecret = management.getManagementJwtSecret();
//
//        Map context = (Map) jwtClaimsSet.getClaim("context");
//        String userName = (String) context.get("userName");
//        String clientKey = (String) context.get("clientKey");
//        String scopes = (String) context.get("scopes");
//
//        //만료시간
//        Date issueTime = jwtClaimsSet.getIssueTime();
//        Date expirationTime = new Date(issueTime.getTime() + management.getScopeCheckLifetime() * 1000);
//
//        boolean validated = JwtUtils.validateToken(scopeToken, expirationTime);
//
//        oauthScopeToken.setToken(scopeToken);
//        oauthScopeToken.setValidated(validated);
//        oauthScopeToken.setUserName(userName);
//        oauthScopeToken.setClientKey(clientKey);
//        oauthScopeToken.setScopes(scopes);
//
//        return oauthScopeToken;
//    }
//
//    @Override
//    public OauthSessionToken generateSessionToken(String managementKey, String userName, String userPassword) throws Exception {
//
//        Management management = managementService.selectByKey(managementKey);
//        if (management == null) {
//            return null;
//        }
//
//        OauthUser oauthUser = this.selectByManagementIdAndCredential(management.get_id(), userName, userPassword);
//        if (oauthUser == null) {
//            return null;
//        }
//
//        //발급 시간
//        Date issueTime = new Date();
//
//        //만료시간
//        Date expirationTime = new Date(new Date().getTime() + management.getSessionTokenLifetime() * 1000);
//
//        //발급자
//        String issuer = management.getManagementKey();
//
//        //시그네이쳐 설정
//        String sharedSecret = management.getManagementJwtSecret();
//        JWSSigner signer = new MACSigner(sharedSecret);
//
//        //콘텍스트 설정
//        Map context = new HashMap();
//        context.put("managementKey", managementKey);
//        context.put("userName", userName);
//
//        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
//        JWTClaimsSet claimsSet = builder
//                .issuer(issuer)
//                .issueTime(issueTime)
//                .expirationTime(expirationTime)
//                .claim("context", context).build();
//
//        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
//
//        signedJWT.sign(signer);
//
//        String sessionToken = signedJWT.serialize();
//
//        OauthSessionToken oauthSessionToken = new OauthSessionToken();
//
//        oauthSessionToken.setToken(sessionToken);
//        oauthSessionToken.setUserName(userName);
//        oauthSessionToken.setValidated(true);
//
//        return oauthSessionToken;
//    }
//
//    @Override
//    public OauthScopeToken generateScopeToken(String managementKey, String userName, String clientKey, String scopes) throws Exception {
//        Management management = managementService.selectByKey(managementKey);
//        if (management == null) {
//            return null;
//        }
//
//        OauthClient oauthClient = clientService.selectByClientKey(clientKey);
//        if (oauthClient == null) {
//            return null;
//        }
//
//        //발급 시간
//        Date issueTime = new Date();
//
//        //만료시간
//        Date expirationTime = new Date(new Date().getTime() + management.getScopeCheckLifetime() * 1000);
//
//        //발급자
//        String issuer = management.getManagementKey();
//
//        //시그네이쳐 설정
//        String sharedSecret = management.getManagementJwtSecret();
//        JWSSigner signer = new MACSigner(sharedSecret);
//
//        //콘텍스트 설정
//        Map context = new HashMap();
//        context.put("managementKey", managementKey);
//        context.put("userName", userName);
//        context.put("clientKey", clientKey);
//        context.put("scopes", scopes);
//
//        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
//        JWTClaimsSet claimsSet = builder
//                .issuer(issuer)
//                .issueTime(issueTime)
//                .expirationTime(expirationTime)
//                .claim("context", context).build();
//
//        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
//
//        signedJWT.sign(signer);
//
//        String scopeToken = signedJWT.serialize();
//
//        OauthScopeToken oauthScopeToken = new OauthScopeToken();
//
//        oauthScopeToken.setToken(scopeToken);
//        oauthScopeToken.setUserName(userName);
//        oauthScopeToken.setValidated(true);
//        oauthScopeToken.setClientKey(clientKey);
//        oauthScopeToken.setScopes(scopes);
//
//        return oauthScopeToken;
//    }
//
//    @Override
//    public OauthUser insertAvatar(InputStream in, String contentType, OauthUser oauthUser) {
//        return oauthUserRepository.insertAvatar(in, contentType, oauthUser);
//    }
//
//    @Override
//    public void deleteAvatar(OauthUser oauthUser) {
//        oauthUserRepository.deleteAvatar(oauthUser);
//    }
}
