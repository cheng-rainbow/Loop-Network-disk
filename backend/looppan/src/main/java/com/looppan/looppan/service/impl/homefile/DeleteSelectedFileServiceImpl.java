package com.looppan.looppan.service.impl.homefile;

import com.looppan.looppan.config.globalException.MyException;
import com.looppan.looppan.config.security.UserDetailsImpl;
import com.looppan.looppan.mapper.FileInfoMapper;
import com.looppan.looppan.pojo.User;
import com.looppan.looppan.service.homefile.DeleteSelectedFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeleteSelectedFileServiceImpl implements DeleteSelectedFilesService {
    @Autowired
    FileInfoMapper fileInfoMapper;

    @Override
    @Transactional
    public ResponseEntity<Map> deleteFiles(List<String> filesId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        String userId = user.getUserId();
        try {
            for (String fileId : filesId) {
                System.out.println(fileId);
                fileInfoMapper.DeleteByFileIdAndUserId(fileId, Integer.valueOf(userId));
            }
        } catch (RuntimeException e) {
            throw new MyException("删除失败");
        }

        Map<String, String> mp = new HashMap<String, String>();
        mp.put("message", "删除成功");
        return ResponseEntity.ok().body(mp);
    }
}
