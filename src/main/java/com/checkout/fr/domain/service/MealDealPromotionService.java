package com.checkout.fr.domain.service;

import com.checkout.fr.domain.model.MealDealPromotion;
import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.model.Product;
import com.checkout.fr.domain.model.PromotionType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MealDealPromotionService implements PromotionService {

    @Override
    public PromotionType getType() {
        return PromotionType.MEAL_DEAL;
    }

    @Override
    public List<PromotionResult> apply(Map<String, Integer> quantities, PricingRules rules) {
        List<PromotionResult> results = new ArrayList<>();

        for (MealDealPromotion promotion : rules.promotionsOfType(MealDealPromotion.class)) {
            int deals = promotion.getRequiredProductIds().stream()
                    .mapToInt(sku -> quantities.getOrDefault(sku, 0))
                    .min()
                    .orElse(0);

            if (deals <= 0) {
                continue;
            }

            BigDecimal unitTotal = BigDecimal.ZERO;
            List<AffectedItem> affectedItems = new ArrayList<>();

            for (String sku : promotion.getRequiredProductIds()) {
                Product product = rules.requireProduct(sku);
                affectedItems.add(new AffectedItem(sku, deals));
                unitTotal = unitTotal.add(product.getPrice().multiply(BigDecimal.valueOf(deals)));
            }

            BigDecimal promoTotal = promotion.getDealPrice().multiply(BigDecimal.valueOf(deals));
            BigDecimal discount = unitTotal.subtract(promoTotal);

            if (discount.signum() <= 0) {
                continue;
            }

            results.add(new PromotionResult(affectedItems, discount));
        }

        return results;
    }
}
