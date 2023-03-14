package org.swyg.greensumer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.swyg.greensumer.dto.ZeroWasteShopResponse;
import org.swyg.greensumer.dto.request.ZeroWasteShopRequest;
import org.swyg.greensumer.dto.response.Response;
import org.swyg.greensumer.service.ShopRecommendationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/maps/recommend")
public class MapController {

    private final ShopRecommendationService shopRecommendationService;

    @GetMapping("/test")
    public Response<Void> test() {
        return Response.success();
    }

    @PostMapping
    public Response<List<ZeroWasteShopResponse>> recommend(@RequestBody ZeroWasteShopRequest request){
        List<ZeroWasteShopResponse> zeroWasteShopResponses = shopRecommendationService.recommendZeroWasteShopList(request);

        return Response.success(zeroWasteShopResponses);
    }
}
