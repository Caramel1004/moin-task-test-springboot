package com.moin.remittance.application.service.v2.calculating;

import com.moin.remittance.application.service.v2.calculating.policy.RemittanceFeePolicy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class ExchangeRateCalculator {
    public static double calculateExchangeRate(long amount, double currencyUnit, double basePrice, String currencyCode) {
        BigDecimal sourceAmount = new BigDecimal(amount);
        BigDecimal fee = RemittanceFeePolicy.calculateFee(amount);

        BigDecimal krw = sourceAmount.subtract(fee);
        BigDecimal targetCurrencyToKrwExchangeRate = new BigDecimal(basePrice / currencyUnit).setScale(4, RoundingMode.HALF_UP);// 소수점 넷째자리에서 반올림
        System.out.println(targetCurrencyToKrwExchangeRate);

        int decimalPlaces = Currency.getInstance(currencyCode).getDefaultFractionDigits();

        BigDecimal targetAmount = krw.divide(targetCurrencyToKrwExchangeRate, decimalPlaces, BigDecimal.ROUND_HALF_UP);// 자릿수 getDefaultFractionDigits

        return Double.parseDouble(String.valueOf(targetAmount));
    }
}
