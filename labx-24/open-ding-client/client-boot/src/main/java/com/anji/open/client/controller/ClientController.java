package com.anji.open.client.controller;

import com.anji.open.client.dto.ClientDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ClientController
 * @Description: TODO
 * @Author dingkaiqiang
 * @Date 2020-05-12
 * @Version V1.0
 **/
@RestController
@RequestMapping("client")
public class ClientController {

    @GetMapping("")
    public ClientDTO getDemo(){
       return this.getClientDTO();
    }

    private ClientDTO getClientDTO() {
        return ClientDTO.builder()
                .age(1)
                .name("getDemo")
                .clientId(1)
                .clientBody("clitnBody测试").build();
    }
}
