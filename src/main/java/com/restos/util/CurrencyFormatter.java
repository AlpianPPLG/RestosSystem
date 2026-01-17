package com.restos.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utility class for formatting currency values
 * Uses Indonesian Rupiah (IDR) format
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class CurrencyFormatter {

    private static final Locale INDONESIA = new Locale("id", "ID");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(INDONESIA);
    private static final DecimalFormat DECIMAL_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(INDONESIA);
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DECIMAL_FORMAT = new DecimalFormat("#,##0", symbols);
    }

    /**
     * Format amount to Indonesian Rupiah currency format
     * Example: 50000 -> Rp50.000
     * 
     * @param amount Amount to format
     * @return Formatted currency string
     */
    public static String format(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }

    /**
     * Format BigDecimal amount to Indonesian Rupiah currency format
     * Example: 50000 -> Rp50.000
     * 
     * @param amount Amount to format
     * @return Formatted currency string
     */
    public static String format(java.math.BigDecimal amount) {
        if (amount == null)
            return CURRENCY_FORMAT.format(0);
        return CURRENCY_FORMAT.format(amount.doubleValue());
    }

    /**
     * Format amount with "Rp" prefix
     * Example: 50000 -> Rp 50.000
     * 
     * @param amount Amount to format
     * @return Formatted currency string
     */
    public static String formatWithPrefix(double amount) {
        return "Rp " + DECIMAL_FORMAT.format(amount);
    }

    /**
     * Format amount without currency symbol
     * Example: 50000 -> 50.000
     * 
     * @param amount Amount to format
     * @return Formatted number string
     */
    public static String formatNumber(double amount) {
        return DECIMAL_FORMAT.format(amount);
    }

    /**
     * Format amount with full currency text
     * Example: 50000 -> Rp 50.000,00
     * 
     * @param amount Amount to format
     * @return Formatted currency string with decimals
     */
    public static String formatFull(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(INDONESIA);
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("Rp #,##0.00", symbols);
        return df.format(amount);
    }

    /**
     * Format amount for display in compact form
     * Example: 1500000 -> 1.5jt
     * 
     * @param amount Amount to format
     * @return Compact formatted string
     */
    public static String formatCompact(double amount) {
        if (amount >= 1_000_000_000) {
            return String.format("%.1fM", amount / 1_000_000_000);
        } else if (amount >= 1_000_000) {
            return String.format("%.1fjt", amount / 1_000_000);
        } else if (amount >= 1_000) {
            return String.format("%.1frb", amount / 1_000);
        }
        return DECIMAL_FORMAT.format(amount);
    }

    /**
     * Parse currency string to double
     * Handles Indonesian format (Rp, dots, commas)
     * 
     * @param currencyString Currency string to parse
     * @return Parsed double value
     */
    public static double parse(String currencyString) {
        if (currencyString == null || currencyString.isEmpty()) {
            return 0;
        }

        // Remove currency symbol and spaces
        String cleaned = currencyString
                .replace("Rp", "")
                .replace("rp", "")
                .replace(" ", "")
                .replace(".", "")
                .replace(",", ".");

        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Format price for receipt
     * Right-aligned with fixed width
     * 
     * @param amount Amount to format
     * @param width  Total width for padding
     * @return Padded formatted string
     */
    public static String formatForReceipt(double amount, int width) {
        String formatted = formatWithPrefix(amount);
        return String.format("%" + width + "s", formatted);
    }
}
