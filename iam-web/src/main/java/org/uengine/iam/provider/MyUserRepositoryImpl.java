package org.uengine.iam.provider;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.uengine.iam.oauthuser.OauthAvatar;
import org.uengine.iam.oauthuser.OauthUser;
import org.uengine.iam.oauthuser.OauthUserPage;
import org.uengine.iam.oauthuser.OauthUserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MyUserRepositoryImpl implements OauthUserRepository {

    @Autowired
    private JPAUserRepository userRepository;

    @Autowired
    private JPAAvatarRepository avatarRepository;

    private OauthUser toOauthUser(JPAUserEntity entity) {
        if (entity == null) {
            return null;
        }
        OauthUser oauthUser = new OauthUser();
        oauthUser.setUserName(entity.getUserName());
        oauthUser.setUserPassword(entity.getUserPassword());
        oauthUser.setMetaData(entity.getMetaData());
        oauthUser.setRegDate(entity.getRegDate());
        oauthUser.setUpdDate(entity.getUpdDate());
        return oauthUser;
    }

    private JPAUserEntity toMyModel(OauthUser oauthUser) {
        if (oauthUser == null) {
            return null;
        }
        JPAUserEntity entity = new JPAUserEntity();
        entity.setUserName(oauthUser.getUserName());
        entity.setUserPassword(oauthUser.getUserPassword());
        entity.setMetaData(oauthUser.getMetaData());
        return entity;
    }

    @Override
    public OauthUser insert(OauthUser oauthUser) {
        JPAUserEntity entity = this.toMyModel(oauthUser);
        return this.toOauthUser(userRepository.save(entity));
    }

    @Override
    public OauthUser update(OauthUser oauthUser) {
        JPAUserEntity toUpdate = userRepository.findByUserName(oauthUser.getUserName());
        toUpdate.setUserPassword(oauthUser.getUserPassword());
        toUpdate.setMetaData(oauthUser.getMetaData());
        return this.toOauthUser(userRepository.save(toUpdate));
    }

    @Override
    public OauthUser findByUserName(String userName) {
        return this.toOauthUser(userRepository.findByUserName(userName));
    }


    @Override
    public OauthUserPage findLikeUserName(String searchKey, Pageable pageable) {
        Page<JPAUserEntity> all = null;
        if (StringUtils.isEmpty(searchKey)) {
            all = userRepository.findAll(pageable);
        } else {
            all = userRepository.findLikeUserName(searchKey, pageable);
        }
        OauthUserPage page = new OauthUserPage();
        page.setTotal(all.getTotalElements());

        List<OauthUser> oauthUserList = new ArrayList<>();
        List<JPAUserEntity> content = all.getContent();
        for (int i = 0; i < content.size(); i++) {
            oauthUserList.add(this.toOauthUser(content.get(i)));
        }
        page.setOauthUserList(oauthUserList);
        return page;
    }

    @Override
    public OauthUser findByUserNameAndUserPassword(String userName, String userPassword) {
        return this.toOauthUser(userRepository.findByUserNameAndUserPassword(userName, userPassword));
    }

    @Override
    public void deleteByUserName(String userName) {
        JPAUserEntity entity = userRepository.findByUserName(userName);
        userRepository.delete(entity);
    }

    @Override
    public OauthAvatar getAvatar(String userName) {
        JPAAvatarEntity avatarEntity = avatarRepository.findByUserName(userName);
        OauthAvatar oauthAvatar = new OauthAvatar();
        oauthAvatar.setContentType(avatarEntity.getContentType());
        oauthAvatar.setData(avatarEntity.getData());
        oauthAvatar.setUserName(avatarEntity.getUserName());
        return oauthAvatar;
    }

    @Override
    public OauthAvatar insertAvatar(OauthAvatar oauthAvatar) {
        JPAAvatarEntity avatarEntity = avatarRepository.findByUserName(oauthAvatar.getUserName());
        if (avatarEntity == null) {
            avatarEntity = new JPAAvatarEntity();
        }
        avatarEntity.setUserName(oauthAvatar.getUserName());
        avatarEntity.setContentType(oauthAvatar.getContentType());
        avatarEntity.setData(oauthAvatar.getData());
        avatarRepository.save(avatarEntity);
        return oauthAvatar;
    }

    @Override
    public void deleteAvatar(String userName) {
        JPAAvatarEntity avatarEntity = avatarRepository.findByUserName(userName);
        avatarRepository.delete(avatarEntity);
    }
}
