package org.swyg.greensumer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.swyg.greensumer.dto.ZeroWasteShopResponse;
import org.swyg.greensumer.dto.request.ZeroWasteShopRequest;
import org.swyg.greensumer.dto.response.Response;
import org.swyg.greensumer.service.RecommendationService;
import org.swyg.greensumer.service.ZeroWasteShopRecommendationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/maps")
public class MapController {

    private final ZeroWasteShopRecommendationService zeroWasteShopRecommendationService;
    private final RecommendationService recommendationService;

    @PostMapping("/recommend")
    public Response<List<ZeroWasteShopResponse>> recommend(@RequestBody ZeroWasteShopRequest request){
        List<ZeroWasteShopResponse> zeroWasteShopResponses = zeroWasteShopRecommendationService.recommendZeroWasteShopList(request);

        return Response.success(zeroWasteShopResponses);
    }

    @GetMapping("/dir/{encodedId}")
    public Response<String> searchRecommendation(@PathVariable("encodedId") String encodedId) {
        String result = recommendationService.findRecommendationUrlById(encodedId);
        log.info("[MapController searchRecommendation] direction url: {}", result);

        return Response.success(result);
    }
}
