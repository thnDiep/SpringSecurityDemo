package com.bookditi.profile.mapper;

import com.bookditi.profile.dto.request.UserProfileRequest;
import com.bookditi.profile.dto.response.UserProfileResponse;
import com.bookditi.profile.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfileResponse toUserProfileResponse(UserProfile userProfile);
    UserProfile toUserProfile(UserProfileRequest request);
    void updateUserProfile(@MappingTarget UserProfile userProfile, UserProfileRequest request);
}
