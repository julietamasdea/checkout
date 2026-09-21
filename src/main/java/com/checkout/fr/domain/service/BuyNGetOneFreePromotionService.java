package com.checkout.fr.domain.service;

import com.checkout.fr.domain.model.BuyNGetOneFreePromotion;
import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.model.Product;
import com.checkout.fr.domain.model.PromotionType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BuyNGetOneFreePromotionService implements PromotionService {

    @Override
    public PromotionType getType() {
        return PromotionType.BUY_N_GET_ONE_FREE;
    }

    @Override
    public List<PromotionResult> apply(Map<String, Integer> quantities, PricingRules rules) {
        List<PromotionResult> results = new ArrayList<>();

        for (BuyNGetOneFreePromotion promotion : rules.promotionsOfType(BuyNGetOneFreePromotion.class)) {
            int quantity = quantities.getOrDefault(promotion.getProductId(), 0);
            if (quantity <= 0) {
                continue;
            }

            int blockSize = promotion.getBuyQuantity() + 1;
            int freeItems = quantity / blockSize;
            if (freeItems == 0) {
                continue;
            }

            Product product = rules.requireProduct(promotion.getProductId());
            int affectedQty = freeItems * blockSize;
            BigDecimal discount = product.getPrice().multiply(BigDecimal.valueOf(freeItems));

            results.add(new PromotionResult(
                    List.of(new AffectedItem(promotion.getProductId(), affectedQty)),
                    discount));
        }

        return results;
    }
}
