package com.hanghae.orderservice.service;


import com.hanghae.orderservice.domain.OrderRepository;
import com.hanghae.orderservice.domain.entity.Order;
import com.hanghae.orderservice.domain.entity.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class StatusService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public StatusService(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateStatus() {
        LocalDateTime now = LocalDateTime.now();
//        주문완료 -> 배송중
        List<Order> completedOrders = orderRepository.findByStatus(OrderStatus.COMPLETE,now.minusDays(1));
        for (Order order : completedOrders) {
            orderService.changeOrderStatus(order.getId(),OrderStatus.DELIVERY_NOW);
            orderRepository.save(order);
            log.info("상태변경{},날짜변경{}",order.getStatus(),order.getUpdatedAt());

        }
//        배송중 -> 배송완료
        List<Order> deliveringOrders = orderRepository.findByStatus(OrderStatus.DELIVERY_NOW,now.minusDays(1));
        for (Order order : deliveringOrders) {
            orderService.changeOrderStatus(order.getId(),OrderStatus.DELIVERY_COMPLETE);
            orderRepository.save(order);
            log.info("상태변경{},날짜변경{}",order.getStatus(),order.getUpdatedAt());

        }
//        배송완료->반품불가
        List<Order> deliveredOrders = orderRepository.findByStatus(OrderStatus.DELIVERY_COMPLETE,now.minusDays(2));
        for (Order order : deliveredOrders) {
            orderService.changeOrderStatus(order.getId(),OrderStatus.RETURN_NOT_ALLOWED);
            orderRepository.save(order);
            log.info("상태변경{},날짜변경{}",order.getStatus(),order.getUpdatedAt());
        }
//        반품신청->반품완료 + 재고반영
        List<Order> returnOrders = orderRepository.findByStatus(OrderStatus.RETURN_NOW,now.minusDays(1));
        for (Order order : returnOrders) {
            orderService.changeOrderStatus(order.getId(),OrderStatus.RETURN_COMPLETE);
            orderService.cancelOrder(order.getId());
        }
    }



}
