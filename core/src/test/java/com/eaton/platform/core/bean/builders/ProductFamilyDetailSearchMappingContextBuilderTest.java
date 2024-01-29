package com.eaton.platform.core.bean.builders;

import com.eaton.platform.core.bean.builders.product.BeanFiller;
import com.eaton.platform.core.bean.builders.product.ProductFamilyDetailBuilder;
import com.eaton.platform.core.bean.builders.product.exception.MissingFillingBeanException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductFamilyDetailSearchMappingContextBuilderTest {

    @Mock
    BeanFiller mockFillerOne;

    @Mock
    BeanFiller mockFillerTwo;

    @Test
    @DisplayName("Ensure bean filler fill method should be call on build")
    void testBuilderWithBeanFillerFillShouldBeInvoke() throws MissingFillingBeanException {
        doNothing().when(mockFillerOne).fill(any());
        doNothing().when(mockFillerTwo).fill(any());
        ProductFamilyDetailBuilder builder = new ProductFamilyDetailBuilder();
        builder.prepare(mockFillerOne,mockFillerTwo);
        builder.build();
        verify(mockFillerOne,times(1)).fill(any());
        verify(mockFillerTwo,times(1)).fill(any());
    }

    @Test
    @DisplayName("Ensure builder without bean filler will throw exception")
    void testEnsureBuilderWillThrowExceptionWithoutFiller() throws MissingFillingBeanException {
        ProductFamilyDetailBuilder builder = new ProductFamilyDetailBuilder();
        builder.prepare(null);
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,()->builder.build());
        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception instanceof IllegalArgumentException);
        Assertions.assertEquals(exception.getMessage(),"Unable to build because missing Bean Filler Object");
    }
}
