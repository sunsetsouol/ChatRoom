package org.example.IdStrategy.IdGen.Impl;//package shop.sunsetsouol.IdStrategy.IdGen.Impl;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import shop.sunsetsouol.IdStrategy.IdGen.IdGenerator;
//import shop.sunsetsouol.IdStrategy.IdType.IdType;
//import shop.sunsetsouol.mapper.TicketServersMapper;
//
///**
// * @author yinjunbiao
// * @version 1.0
// * @date 2024/2/20
// */
//@Component
//public class TickerServers extends IdGenerator {
//
//    @Autowired
//    private TicketServersMapper ticketServersMapper;
//
//    @Override
//    @Transactional
//    public long getLongId() {
//        ticketServersMapper.replace();
//        return ticketServersMapper.getId();
//    }
//
//    @Override
//    public String getType() {
//        return IdType.LONG.type;
//    }
//}
