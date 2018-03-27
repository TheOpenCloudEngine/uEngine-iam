package org.uengine.iam.oauthuser;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.uengine.iam.oauthscope.OauthScopeService;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/rest/v1")
public class OauthUserRestController {

    @Autowired
    private OauthUserRepository oauthUserRepository;

    @Autowired
    private OauthScopeService scopeService;

    /**
     * 유저 리스트 반환
     *
     * @param request
     * @param response
     * @param pageable
     * @return
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<OauthUser>> listAllUsers(HttpServletRequest request,
                                                        HttpServletResponse response,
                                                        @PageableDefault Pageable pageable) {

        OauthUserPage oauthUserPage = oauthUserRepository.findLikeUserName(null, pageable);
        HttpHeaders headers = new HttpHeaders();


        int offset = pageable.getPageSize() * pageable.getPageNumber();
        Long total = oauthUserPage.getTotal();
        headers.add("x-uengine-pagination-currentoffset", offset + "");
        headers.add("x-uengine-pagination-totalnbrecords", total + "");
        return new ResponseEntity<>(oauthUserPage.getOauthUserList(), headers, HttpStatus.OK);
    }

    /**
     * 유저 생성
     *
     * @param request
     * @param response
     * @param oauthUser
     * @return
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<OauthUser> createUser(HttpServletRequest request, HttpServletResponse response, @RequestBody OauthUser oauthUser) {
        OauthUser existUser = oauthUserRepository.findByUserName(oauthUser.getUserName());
        if (existUser != null) {
            new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(oauthUserRepository.insert(oauthUser), HttpStatus.CREATED);
    }

    /**
     * 유저 업데이트
     *
     * @param request
     * @param response
     * @param oauthUser
     * @return
     */
    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public ResponseEntity<OauthUser> updateUser(HttpServletRequest request,
                                                HttpServletResponse response,
                                                @RequestBody OauthUser oauthUser) {
        OauthUser existUser = oauthUserRepository.findByUserName(oauthUser.getUserName());
        if (existUser == null) {
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(oauthUserRepository.update(oauthUser), HttpStatus.OK);
    }

    /**
     * 유저 삭제
     *
     * @param request
     * @param response
     * @param userName
     * @return
     */
    @RequestMapping(value = "/user", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(
            HttpServletRequest request, HttpServletResponse response, @RequestParam("userName") String userName) {
        OauthUser existUser = oauthUserRepository.findByUserName(userName);
        if (existUser == null) {
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        oauthUserRepository.deleteByUserName(userName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 유저네임으로 유저 찾기
     *
     * @param request
     * @param response
     * @param userName
     * @return
     */
    @RequestMapping(value = "/user/search/findByUserName", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<OauthUser> findByUserName(HttpServletRequest request, HttpServletResponse response,
                                                    @RequestParam("userName") String userName) {

        OauthUser oauthUser = oauthUserRepository.findByUserName(userName);
        if (oauthUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(oauthUser, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/search/findLikeUserName", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<OauthUser>> findLikeUserName(HttpServletRequest request,
                                                            HttpServletResponse response,
                                                            @RequestParam("userName") String userName,
                                                            @PageableDefault Pageable pageable) {
        OauthUserPage oauthUserPage = oauthUserRepository.findLikeUserName(userName, pageable);
        HttpHeaders headers = new HttpHeaders();

        int offset = pageable.getPageSize() * pageable.getPageNumber();
        Long total = oauthUserPage.getTotal();
        headers.add("x-uengine-pagination-currentoffset", offset + "");
        headers.add("x-uengine-pagination-totalnbrecords", total + "");
        return new ResponseEntity<>(oauthUserPage.getOauthUserList(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.POST)
    public ResponseEntity<OauthAvatar> createUserAvatar(HttpServletRequest request,
                                                        HttpServletResponse response,
                                                        @RequestParam(required = false) String userName,
                                                        @RequestParam("contentType") String contentType) throws Exception {
        if (!contentType.startsWith("image/")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ServletInputStream inputStream = request.getInputStream();
        byte[] data = IOUtils.toByteArray(inputStream);
        OauthAvatar oauthAvatar = new OauthAvatar();
        oauthAvatar.setData(data);
        oauthAvatar.setUserName(userName);
        oauthAvatar.setContentType(contentType);
        OauthAvatar inserted = oauthUserRepository.insertAvatar(oauthAvatar);

        inserted.setData(null);
        return new ResponseEntity<>(inserted, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/avatar/formdata", method = RequestMethod.POST)
    public ResponseEntity<OauthAvatar> createUserAvatarByFormData(HttpServletRequest request, HttpServletResponse response,
                                                                  @RequestParam(required = false) String userName,
                                                                  @RequestParam("contentType") String contentType,
                                                                  @RequestParam("file") MultipartFile file) throws Exception {
        if (!contentType.startsWith("image/")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        byte[] data = IOUtils.toByteArray(file.getInputStream());
        OauthAvatar oauthAvatar = new OauthAvatar();
        oauthAvatar.setData(data);
        oauthAvatar.setUserName(userName);
        oauthAvatar.setContentType(contentType);
        OauthAvatar inserted = oauthUserRepository.insertAvatar(oauthAvatar);

        inserted.setData(null);
        return new ResponseEntity<>(inserted, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAvatar(HttpServletRequest request, HttpServletResponse response,
                                             @RequestParam(required = false) String userName) {

        oauthUserRepository.deleteAvatar(userName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Void> getAvatar(HttpServletRequest request, HttpServletResponse response,
                                          @RequestParam String userName) throws Exception {

        OauthAvatar avatar = oauthUserRepository.getAvatar(userName);
        response.setContentType(avatar.getContentType());
        response.setContentLength(avatar.getData().length);

        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(avatar.getData());
        outputStream.flush();
        outputStream.close();

        return null;
    }
}
