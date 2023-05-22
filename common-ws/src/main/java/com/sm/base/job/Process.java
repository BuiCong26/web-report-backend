//package com.sm.base.job;
//
//import com.sm.base.common.Constant;
//import com.sm.base.model.modelDB1.StockTotal;
//import com.sm.base.repo.repoDB1.StockTotalRepo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//
//@Component
//@Slf4j
//public class Process {
//    @Autowired
//    private StockTotalRepo stockTotalRepo;
//
//
//    @PersistenceContext(unitName = Constant.CONFIG_DB1.UNIT_NAME_ENTITIES_DB1)
//    private EntityManager em;
//
////    @Scheduled(fixedDelayString = "${job.scheduler.times-delay}")
//    @Transactional
//    public void process() {
//        log.info("-----START-----");
//        ExecutorService executorService = null;
//        try {
//            //fix tao 10 thread cung 1 luc. neu can xu lt nhieu hon 10 thread thi chi chạy cung luc 10 thread. xu ly xong 1 thread thi lai chay tiep 1 thread tiep theo
//            // co the dung cac cach tao thread khac
//            executorService = Executors.newFixedThreadPool(10);
//            for (int i = 0; i < 15; i++) {
//                int finalI = i;
//                //tao 1 thread
//                executorService.submit(() -> {
//                    // try catch trong nay de chet 1 thread thì khong anh huong den cac thread khac
//                    try {
//                        List<StockTotal> stockTotals = stockTotalRepo.findStockTotal(1, 5);
//                        log.info("count test: " + stockTotals.size());
//                    } catch (Exception e) {
//                        log.error("Loi o 1 trong cac thread i = "+ finalI +": ",e);
//                    } finally {
//
//                    }
//                });
//            }
//
//            //dung lai o doan nay cho den khi cac thread cung hoan thanh cong viec thi moi chay tiep
//            try {
//                executorService.shutdown();
//                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//            } catch (InterruptedException e) {
//                log.error("loi khong doi duoc cac thread cung dung: ", e);
//            }
//            // neu muon tao tiep thread thì phai new thread lai vi da shutdown o ben tren. code nhu ben duoi
////            if(executorService.isShutdown() || executorService.isTerminated()){
////                executorService = Executors.newFixedThreadPool(10);
////            }
//        } catch (Exception e) {
//            log.error("Loi truoc khi tao thread: ", e);
//        } finally {
//            // chay xong thi phai giai phong thread ko thi may chu se bi treo
//            if (executorService != null) {
//                executorService.shutdown();
//            }
//            log.info("-----END-----");
//        }
//    }
//
//}
