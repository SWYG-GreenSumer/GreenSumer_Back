package org.swyg.greensumer.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
class KakaoUriBuilderServiceTest {

    private KakaoUriBuilderService kakaoUriBuilderService;

    @BeforeEach
    void setup() {
        kakaoUriBuilderService = new KakaoUriBuilderService();
    }

    @DisplayName("한글 파라미터의 경우 정상적으로 인코딩된다.")
    @Test
    void givenAddress_whenRequestingEncoding_thenReturnURI() {
        // Given
        String address = "서울 성북구";
        Charset charset = StandardCharsets.UTF_8;

        // When
        URI uri = kakaoUriBuilderService.buildUriByAddressSearch(address);
        String decodedResult = URLDecoder.decode(uri.toString(), charset);

        // Then
        assertThat(decodedResult)
                .isEqualTo("https://dapi.kakao.com/v2/local/search/address.json?query=서울 성북구");
    }
}
