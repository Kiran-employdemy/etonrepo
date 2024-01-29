package com.eaton.platform.core.models.resourcelist;

import com.eaton.platform.integration.auth.models.UserProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationSecureAssetValidatorTest {

    public static final List<String> LIST_OF_TAGS = Arrays.asList("tag1", "tag2", "tag3");

    @Mock
    SecureAssetModel secureAssetModel;
    @Mock
    UserProfile userProfile;

    @Test
    @DisplayName("validate: the validateApplicationTags method is called")
    void testCorrectValidateMethodIsCalled() {
        when(userProfile.getAppAccessTags()).thenReturn(LIST_OF_TAGS);
        when(secureAssetModel.validateApplicationAccessTags(LIST_OF_TAGS)).thenReturn(false);

        assertFalse(new ApplicationSecureAssetValidator(userProfile).validate(secureAssetModel), "must return value of method call on secureAssetModel");
    }


}