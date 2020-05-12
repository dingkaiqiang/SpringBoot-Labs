package com.anji.open.client.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @ClassName ClientDTO
 * @Description: TODO
 * @Author dingkaiqiang
 * @Date 2020-05-12
 * @Version V1.0
 **/
@Data
@Builder
public class ClientDTO {

    private String name;

    private int age;

    private int clientId;

    private String clientBody;
}
