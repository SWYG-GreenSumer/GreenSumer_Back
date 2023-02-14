package org.swyg.greensumer.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import org.swyg.greensumer.api.dto.KakaoApiResponseDto;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestPropertySource(properties = {"kakao.rest.api.key=xxx"})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
class KakaoAddressSearchServiceTest {

    private RestTemplate restTemplate;
    private KakaoUriBuilderService kakaoUriBuilderService;
    private KakaoAddressSearchService kakaoAddressSearchService;

    @Value("${kakao.rest.api.key}") String kakaoRestApiKey;

    @BeforeEach
    void setup() {
        restTemplate = new RestTemplate();
        kakaoUriBuilderService = new KakaoUriBuilderService();
        kakaoAddressSearchService = new KakaoAddressSearchService(restTemplate, kakaoUriBuilderService);
        kakaoAddressSearchService.setKakaoRestApiKey(kakaoRestApiKey);
    }

    @DisplayName("address 파라미터가 null일때, requestAddressSearch 메소드는 null을 반환한다.")
    @Test
    void givenAddressIsNull_whenRequestingRequestAddressSearch_thenReturnNull() {
        // Given
        String address = null;

        // When
        KakaoApiResponseDto result = kakaoAddressSearchService.requestAddressSearch(address);

        // Then
        assertThat(result).isEqualTo(null);
    }

    @DisplayName("주소값이 유효하다면, requestAddressSearch 메소드는 정상적으로 document를 반환한다.")
    @Test
    void givenAddress_whenRequestingRequestAddressSearch_thenReturnDocument() {
        // Given
        String address = "서울특별시 중구 세종대로 110(태평로1가)";

        // When
        KakaoApiResponseDto result = kakaoAddressSearchService.requestAddressSearch(address);

        // Then
        System.out.println(result);
    }

    @DisplayName("정상적인 주소를 입력했을 경우, 정상적으로 위도 경도로 변환 된다.")
    @MethodSource
    @ParameterizedTest(name = "[{index}] \"{0}\" => {1}")
    void givenAddress_whenRequestingRequestAddressSearch_thenReturnLatitudeAndLongitude(String address, boolean expected) {
        // Given
        boolean actualResult = false;

        // When
        KakaoApiResponseDto actual = kakaoAddressSearchService.requestAddressSearch(address);

        // Then
        if(actual == null) assertThat(actual != null).isEqualTo(expected);
        else assertThat(actual.getDocumentList().size() > 0).isEqualTo(expected);
    }

    static Stream<Arguments> givenAddress_whenRequestingRequestAddressSearch_thenReturnLatitudeAndLongitude() {
        return Stream.of(
                Arguments.arguments("서울 특별시 성북구 종암동", true),
                Arguments.arguments("서울 성북구 종암동 91", true),
                Arguments.arguments("서울 대학로", true),
                Arguments.arguments("서울 성북구 종암동 잘못된 주소", false),
                Arguments.arguments("광진구 구의동 251-45", true),
                Arguments.arguments("광진구 구의동 251-455555", false),
                Arguments.arguments("", false),
                Arguments.arguments(null, false)
        );
    }
}