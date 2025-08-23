//package com.myshopnet.service;
//
//import com.myshopnet.models.Category;
//import com.myshopnet.repository.OrderRepository;
//import com.myshopnet.models.Order;
//
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.util.HashMap;
//import java.util.Map;
//
//public class SalesStatsService {
//
//    /** הכנסות סופיות לפי סניף (כולל כל הימים) */
//    public Map<String, Double> revenueByBranch(OrderRepository repo) {
//        Map<String, Double> out = new HashMap<>();
//        for (Order s : repo.list()) {
//            out.merge(s.getBranchId(), s.getFinalTotal(), Double::sum);
//        }
//        return out;
//    }
//
//    /** יחידות שנמכרו לכל SKU (כולל כל הימים) */
//    public Map<String, Integer> unitsSoldBySku(OrderRepository repo) {
//        Map<String, Integer> out = new HashMap<>();
//        for (Order s : repo.list()) {
//            for (Order.Line ln : s.getLines()) {
//                out.merge(ln.getSku(), ln.getQty(), Integer::sum);
//            }
//        }
//        return out;
//    }
//
//    /** הכנסות לפי קטגוריה (כולל כל הימים) */
//    public Map<Category, Double> revenueByCategory(OrderRepository repo) {
//        Map<Category, Double> out = new HashMap<>();
////        for (Sale s : repo.list()) {
////            for (Sale.Line ln : s.getLines()) {
////                out.merge(ln.getCategory(), ln.lineTotal(), Double::sum);
////            }
////        }
//        return out;
//    }
//
//    /** דוח יומי: הכנסות סופיות לפי סניף בתאריך מסוים */
//    public Map<String, Double> dailyRevenueByBranch(OrderRepository repo, LocalDate day) {
//        Map<String, Double> out = new HashMap<>();
//        ZoneId zone = ZoneId.systemDefault();
//        for (Order s : repo.list()) {
//            LocalDate saleDay = s.getTimestamp().atZone(zone).toLocalDate();
//            if (saleDay.equals(day)) {
//                out.merge(s.getBranchId(), s.getFinalTotal(), Double::sum);
//            }
//        }
//        return out;
//    }
//}
