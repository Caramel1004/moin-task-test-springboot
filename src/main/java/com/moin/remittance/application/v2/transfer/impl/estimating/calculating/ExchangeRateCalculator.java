package com.moin.remittance.application.v2.transfer.impl.estimating.calculating;

import com.moin.remittance.exception.NegativeNumberException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import static com.moin.remittance.domain.vo.HttpResponseCode.BAD_NEGATIVE_TARGET_AMOUNT;

@Component
public class ExchangeRateCalculator {

    public BigDecimal calculateExchangeRate(long sourceAmount, BigDecimal fee, int currencyUnit, BigDecimal basePrice, String currencyCode) {
        System.out.println(fee);
        BigDecimal krw = new BigDecimal(sourceAmount).subtract(fee);
        BigDecimal targetCurrencyToKrwExchangeRate = basePrice.divide(new BigDecimal(currencyUnit), 4, RoundingMode.HALF_UP);// 소수점 넷째자리에서 반올림
        int decimalPlaces = Currency.getInstance(currencyCode).getDefaultFractionDigits();

        System.out.println(decimalPlaces);
        BigDecimal targetAmount = krw.divide(targetCurrencyToKrwExchangeRate, decimalPlaces, RoundingMode.HALF_UP);
        if (targetAmount.compareTo(new BigDecimal(0)) <= 0) {
            throw new NegativeNumberException(BAD_NEGATIVE_TARGET_AMOUNT);
        }
        System.out.println(targetAmount);
        // 자릿수 getDefaultFractionDigits
        return targetAmount;
    }
}
