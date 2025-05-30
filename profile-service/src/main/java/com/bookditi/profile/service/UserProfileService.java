package com.bookditi.profile.service;

import com.bookditi.profile.dto.request.UserProfileRequest;
import com.bookditi.profile.dto.response.UserProfileResponse;
import com.bookditi.profile.entity.UserProfile;
import com.bookditi.profile.exception.AppException;
import com.bookditi.profile.exception.ErrorCode;
import com.bookditi.profile.mapper.UserProfileMapper;
import com.bookditi.profile.repository.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserProfileService {
    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;

    public UserProfileResponse createUserProfile(UserProfileRequest request) {
        UserProfile userProfile = userProfileMapper.toUserProfile(request);
        return userProfileMapper.toUserProfileResponse(userProfileRepository.save(userProfile));
    }

    public List<UserProfileResponse> getUserProfiles() {
        return userProfileRepository.findAll().stream().map(userProfileMapper::toUserProfileResponse).toList();
    }

    public UserProfileResponse getUserProfile(String id) {
        UserProfile userProfile = userProfileRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_PROFILE_NOT_EXIST));
        return userProfileMapper.toUserProfileResponse(userProfile);
    }

    public UserProfileResponse updateUserProfile(UserProfileRequest request, String id) {
        UserProfile userProfile = userProfileRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_PROFILE_NOT_EXIST));
        userProfileMapper.updateUserProfile(userProfile, request);
        return userProfileMapper.toUserProfileResponse(userProfile);
    }

    public void deleteUserProfile(String id) {
        userProfileRepository.deleteById(id);
    }
}
