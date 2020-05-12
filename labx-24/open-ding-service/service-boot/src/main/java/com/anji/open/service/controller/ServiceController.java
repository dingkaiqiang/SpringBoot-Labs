package com.anji.open.service.controller;

import com.anji.open.service.dto.ServiceDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ServiceController
 * @Description: TODO
 * @Author dingkaiqiang
 * @Date 2020-05-12
 * @Version V1.0
 **/
@RestController
@RequestMapping("service")
public class ServiceController {

    @GetMapping("/getDemo")
    public ServiceDTO getDemo(){
        return this.getServiceDTO();
    }

    private ServiceDTO getServiceDTO() {
        return ServiceDTO.builder()
                .age(1)
                .name("serviceDemo")
                .serviceId(1)
                .serviceBody("serviceBody测试").build();
    }
}
