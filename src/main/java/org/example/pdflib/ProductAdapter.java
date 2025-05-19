/*
 * Classname: ProductAdapter
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.pdflib;

import sys.Product;

/**
 * Klasa adaptera konwertująca obiekt Product z głównego projektu
 * na obiekt Product używany przez bibliotekę do generowania PDF.
 */
public class ProductAdapter {

    public static sys.Product toPdfProduct(org.example.sys.Product source) {
        if (source == null) return null;
        // używamy PDF-owego konstruktora (name, category, price)
        return new sys.Product(
                source.getName(),
                source.getCategory(),
                source.getPrice()
        );
    }


    // Lista, jeśli potrzebujesz:
    public static java.util.List<sys.Product> toPdfProducts(
            java.util.List<org.example.sys.Product> src,
            java.util.List<Integer> quantities) {

        if (src == null || quantities == null || src.size() != quantities.size()) return null;
        java.util.List<sys.Product> out = new java.util.ArrayList<>(src.size());
        for (int i = 0; i < src.size(); i++) {
            out.add(toPdfProduct(src.get(i)));
        }
        return out;
    }
    /**
     * Konwertuje obiekt Product z biblioteki PDF z powrotem na obiekt Product
     * z głównego projektu.
     *
     * @param pdfProduct obiekt Product z biblioteki PDF
     * @return obiekt Product z głównego projektu
     */
    public static Product fromPdfProduct(sys.Product pdfProduct) {
        if (pdfProduct == null) {
            return null;
        }

        return new Product(
                pdfProduct.getName(),
                pdfProduct.getCategory(),
                pdfProduct.getPrice(),
                pdfProduct.getQuantity()
        );
    }

    /**
     * Konwertuje listę obiektów Product z biblioteki PDF na listę obiektów
     * Product z głównego projektu.
     *
     * @param pdfProducts lista obiektów Product z biblioteki PDF
     * @return lista obiektów Product z głównego projektu
     */
    public static java.util.List<Product> fromPdfProducts(java.util.List<sys.Product> pdfProducts) {
        if (pdfProducts == null) {
            return null;
        }

        java.util.List<Product> sourceProducts = new java.util.ArrayList<>(pdfProducts.size());
        for (sys.Product pdfProduct : pdfProducts) {
            sourceProducts.add(fromPdfProduct(pdfProduct));
        }

        return sourceProducts;
    }
}